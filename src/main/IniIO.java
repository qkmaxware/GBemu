/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 *
 * @author Colin Halseth
 */
public class IniIO {
    
    public static IniIO DEFAULT;
    
    private HashMap<String,String> options = new HashMap<String,String>();
    
    private IniIO(){}
    
    public static IniIO template(String[] KeyValuePairs){
        IniIO io = new IniIO();
        for(String line : KeyValuePairs){
            String[] opts = line.split(":");
            if(opts.length >= 2)
                io.options.put(opts[0], opts[1]);
        }
        return io;
    }
    
    public static IniIO read(String filename){
        File f = new File(filename);
        if(!f.exists()){
            //New INI file with defaults
            return DEFAULT;
        }
        
        //Read INI
        try{
            IniIO ini = new IniIO();
            for(String line : Files.readAllLines(Paths.get(f.getAbsolutePath()))){
                String[] opts = line.split(":");
                if(opts.length >= 2)
                    ini.options.put(opts[0], opts[1].trim());
            }
            return ini;
        }catch(Exception e){
            //Return with default options
            return DEFAULT;
        }
    }
    
    public static IniIO merge(IniIO a, IniIO b){
        IniIO c = new IniIO();
        for(String key : a.options.keySet()){
            c.options.put(key, a.options.get(key));
        }
        
        for(String key : b.options.keySet()){
            c.options.put(key, b.options.get(key));
        }
        
        return c;
    }
    
    public boolean isSet(String prop){
        if(this.options.containsKey(prop)){
            return Boolean.parseBoolean(this.options.get(prop));
        }
        return false;
    }
    
    public String getString(String prop){
        if(this.options.containsKey(prop)){
            return String.valueOf(this.options.get(prop));
        }
        return null;
    }
    
    public static void write(IniIO settings, String filename){
        try{
            FileWriter writer = new FileWriter(filename);
            for(String key : settings.options.keySet()){
                writer.write(key+": "+settings.options.get(key)+System.lineSeparator());
            }
            writer.close();
        }catch(Exception e){
            System.out.println("Failed to write INI file");
        }
    }
    
}

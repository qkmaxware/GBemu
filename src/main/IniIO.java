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
                io.options.put(opts[0].trim().toLowerCase(), opts[1].trim().toLowerCase());
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
                if(line.startsWith("#") || !line.matches(".+:.+")){ 
                    continue;
                }
                String[] opts = line.split(":");
                if(opts.length >= 2)
                    ini.options.put(opts[0].trim().toLowerCase(), opts[1].trim().toLowerCase());
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
   
    public static IniIO readAndUpdate(String fname, IniIO updateTemplate){
        IniIO n = IniIO.read(fname);
        
        try{
            FileWriter writer = new FileWriter(fname, true);
            
            //If file did not exist, make the default file
            if(n == DEFAULT){
               for(String key : n.options.keySet()){
                   writer.write(key+": "+n.options.get(key)+System.lineSeparator());
               }    
            }
            
            //Append any new ini options to the file
            for(String key : updateTemplate.options.keySet()){
                if(!n.options.containsKey(key)){
                    n.options.put(key, updateTemplate.options.get(key));
                    writer.write(key+": "+updateTemplate.options.get(key)+System.lineSeparator());  
                }
            }
            
            writer.close();
        }catch(Exception e){
            System.out.println("Failed to update ini");
        }
        
        return n;
    }
    
    
    public boolean isSet(String prop){
        prop = prop.toLowerCase();
        if(this.options.containsKey(prop)){
            return Boolean.parseBoolean(this.options.get(prop));
        }
        return false;
    }
    
    public String getString(String prop){
        prop = prop.toLowerCase();
        if(this.options.containsKey(prop)){
            return String.valueOf(this.options.get(prop));
        }
        return null;
    }
    
    public Integer getInt(String prop){
        prop = prop.toLowerCase();
        if(this.options.containsKey(prop)){
            return Integer.parseInt(this.options.get(prop));
        }
        return -1;
    }
    
    public boolean exists(String prop){
        prop = prop.toLowerCase();
        if(this.options.containsKey(prop)){
            return true;
        }
        return false;
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

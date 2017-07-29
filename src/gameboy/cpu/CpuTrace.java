/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameboy.cpu;

import java.io.FileWriter;

/**
 *
 * @author Colin Halseth
 */
public class CpuTrace {
    
    private String fileName = "cpu.log";
    
    private FileWriter fw = null;
    
    public CpuTrace(){}
    
    public boolean isEnabled(){
        return this.fw != null;
    }
    
    public void write(String str){
        if(isEnabled()){
            try{
                fw.write(str+System.lineSeparator());
            }catch(Exception e){}
        }
    }
    
    public void enable(boolean bl){
        try{
            if(this.fw != null){
                this.fw.flush();
                this.fw.close();
            }
            this.fw = new FileWriter(fileName);
        }catch (Exception e){}
    }
    
}

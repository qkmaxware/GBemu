/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameboy.serial;

import gameboy.IMemory;
import gameboy.MemoryMap;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 *
 * @author Colin Halseth
 */
public class SerialConnection implements IMemory{

    private InputStreamReader reader;
    private OutputStreamWriter writer;
    
    private boolean enableOut = false;
    private boolean enableIn = false;
    
    public SerialConnection(InputStreamReader reader, OutputStreamWriter writer){
        this.reader = reader;
        this.writer = writer;
    }
    
    public void EnableRead(boolean b){
        enableIn = b;
    }
    
    public void EnableWrite(boolean b){
        enableOut = b;
    }
    
    
    @Override
    public void Reset() {}

    @Override
    public int rb(int addr) {
        try{
            if(enableIn){
                return reader.read() & 0xFF;
            }
            else{
                return 0;
            }
        }catch(Exception e){
            return 0;
        }
    }

    @Override
    public void wb(int addr, int value) {
        try{
            if(enableOut){
                writer.append(new String(new byte[]{(byte)value},"US-ASCII"));
                writer.flush();
            }
        }catch(Exception e){
            
        }
    }

    @Override
    public void SetMMU(MemoryMap mmu) {}
    
}

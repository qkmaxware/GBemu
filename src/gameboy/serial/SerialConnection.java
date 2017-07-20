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
    
    public SerialConnection(InputStreamReader reader, OutputStreamWriter writer){
        this.reader = reader;
        this.writer = writer;
    }
    
    @Override
    public void Reset() {}

    @Override
    public int rb(int addr) {
        try{
            return reader.read() & 0xFF;
        }catch(Exception e){
            return 0;
        }
    }

    @Override
    public void wb(int addr, int value) {
        try{
            byte converteValue = (byte)value;
            writer.append(new String(new byte[]{converteValue},"US-ASCII"));
            writer.flush();
        }catch(Exception e){
            
        }
    }

    @Override
    public void SetMMU(MemoryMap mmu) {}
    
}

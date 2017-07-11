/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameboy.disk;

import gameboy.IMemory;
import gameboy.MemoryMap;
import java.util.Arrays;

/**
 *
 * @author Colin Halseth
 */
public class HDD implements IMemory{

    public static final int WRAM_SIZE = 8192;
    public static final int ZRAM_SIZE = 128;  
    
    private int[] wram = new int[WRAM_SIZE]; //Working RAM
    private int[] zram = new int[ZRAM_SIZE]; //Zero page RAM

    
    public void Reset(){
        Arrays.fill(wram, 0);
        Arrays.fill(zram, 0);
    }
    
    private static boolean in(int x, int lower, int upper) {
        return lower <= x && x <= upper;
    }
    
    @Override
    public int rb(int addr) {
        if(in(addr, 0xC000, 0xCFFF)){
            //Internal RAM (fixed)
            return wram[addr & 0x1FFF]; //wram[addr&0x1FFF];
        }
        else if(in(addr, 0xD000, 0xDFFF)){
            //Internal RAM (switchable)
            return wram[addr & 0x1FFF];
        }
        else if(in(addr, 0xE000, 0xFDFF)){
            //Shadow Interal RAM (seems wrong to me)
            return wram[addr & 0x1FFF];
        }
        else if(in(addr, 0xFF80, 0xFFFE)){
            //Zero page
            return zram[addr & 0x7F];   //zram[addr&0x7F];
        }
        return 0;
    }

    @Override
    public void wb(int addr, int value) {
        if(in(addr, 0xC000, 0xCFFF)){
            //Internal RAM (fixed)
            wram[addr & 0x1FFF] = value; //wram[addr&0x1FFF];
        }
        else if(in(addr, 0xD000, 0xDFFF)){
            //Internal RAM (switchable)
            wram[addr & 0x1FFF] = value;
        }
        else if(in(addr, 0xE000, 0xFDFF)){
            //Shadow Interal RAM (seems wrong to me)
            wram[addr & 0x1FFF] = value;
        }
        else if(in(addr, 0xFF80, 0xFFFE)){
            //Zero page
            zram[addr & 0x7F] = value;   //zram[addr&0x7F];
        }
    }
    
    public void SetMMU(MemoryMap mmu){}
    
}

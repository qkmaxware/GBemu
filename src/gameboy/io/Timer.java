/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameboy.io;

import gameboy.IMemory;
import gameboy.MemoryMap;

/**
 *
 * @author Colin Halseth
 */
public class Timer implements IMemory{
    
    @Override
    public void Reset(){
        
    }

    @Override
    public int rb(int addr) {
        return 0;
    }

    @Override
    public void wb(int addr, int value) {}

    @Override
    public void SetMMU(MemoryMap mmu) {}
    
    
}

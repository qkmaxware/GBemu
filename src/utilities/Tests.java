/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import gameboy.MemoryMap;

/**
 *
 * @author Colin Halseth
 */
public class Tests {
    
    public static void TestMemory(MemoryMap mmu){
        
        for(int i = 0; i <= mmu.MaxAddress(); i++){
            mmu.wb(i, i);
            int r = mmu.rb(i);
        }
        
    }
    
}

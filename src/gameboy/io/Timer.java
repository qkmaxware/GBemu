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
    
    public class Clock{
        public int main = 0;
        public int sub = 0;
        public int div = 0;
    }
    
    public class Registry{
        public int div = 0;
        public int tima = 0;
        public int tma = 0;
        public int tac = 0;
    }
    
    public final Clock clock = new Clock();
    public final Registry reg = new Registry();
    private MemoryMap mmu;
    
    @Override
    public void Reset(){
        clock.main = 0;
        clock.sub = 0;
        clock.div = 0;
        
        reg.div = 0;
        reg.tac = 0;
        reg.tma = 0;
        reg.tima = 0;
    }

     public void Increment(int deltaTime){
        //Increment by the last opcode's time
        clock.sub += deltaTime;
        
        // No opcode takes longer than 4 M-times,
        // so we need only check for overflow once
        if(clock.sub >= 4){
            clock.main ++;
            clock.sub -= 4;
            
            clock.div ++;
            if(clock.div == 16){
                clock.div = (reg.div + 1) & 255;
                clock.div = 0;
            }
        }
        
        //Check if we need to do one step in the timer
        Check();
    }
    
    private void Check(){
        int threshold = 0;
        
        if((reg.tac & 4) != 0){
            switch(reg.tac & 3){
                case 0:
                    threshold = 64;
                    break;
                case 1:
                    threshold = 1;
                    break;
                case 2:
                    threshold = 4;
                    break;
                case 3:
                    threshold = 16;
                    break;
            }
            
            if(clock.main >= threshold)
                Step();
        }
    }
    
    public void Step(){
        clock.main = 0;
        reg.tima++;
        
        if(reg.tima > 255){
            reg.tima = reg.tma;
            mmu.i_flags |= 4;
        }
    }
    
    public int rb(int addr){
        switch(addr){
            case 0xFF04: return reg.div;
	    case 0xFF05: return reg.tima;
	    case 0xFF06: return reg.tma;
	    case 0xFF07: return reg.tac;
        }
        return 0;
    }
    
    public void wb(int addr, int value){
        switch(addr){
	    case 0xFF04: reg.div = 0; break;
	    case 0xFF05: reg.tima = value; break;
	    case 0xFF06: reg.tma = value; break;
	    case 0xFF07: reg.tac = value & 7; break;
	}
    }

    @Override
    public void SetMMU(MemoryMap mmu) {
        this.mmu = mmu;
    }
    
    
}

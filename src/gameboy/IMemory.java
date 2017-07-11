/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameboy;

/**
 *
 * @author Colin Halseth
 */
public interface IMemory {
 
    public void Reset();
    
    public int rb(int addr);
    
    public void wb(int addr, int value);
    
    public void SetMMU(MemoryMap mmu);
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameboy;

/**
 *
 * @author Colin
 */
public class Gameboy {
    
    public final Cpu cpu;
    public final Gpu gpu;
    public final MemoryMap mmu;
    public final Input input;
    
    public Gameboy(){
        mmu = new MemoryMap();
        
        HDD disk = new HDD();
        mmu.INTERNAL_RAM = disk;
        mmu.ZRAM = disk;        
        mmu.INTERUPT_REGISTER = disk;
        
        //TODO
        gpu = new Gpu();
        mmu.OAM = gpu;
        mmu.VRAM = gpu;
        
        input = new Input();
        mmu.INPUT_OUTPUT = input;
        
        cpu = new Cpu();
        cpu.SetMmu(mmu);
        
    }
    
    public void LoadCartridge(Cartridge cart){
        mmu.ROM_BANK_0 = cart;
        mmu.ROM_BANK_1 = cart;
        mmu.EXTERNAL_RAM = cart;
    }
    
    private void Dispatch(){
        //Step the cpu
        cpu.Step();
        
        //Step the gpu
        gpu.Step();
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameboy;

import gameboy.disk.HDD;
import gameboy.io.Input;
import gameboy.game.Cartridge;
import gameboy.cpu.Cpu;
import gameboy.gpu.Gpu;
import gameboy.io.Timer;

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

        HDD onboard = new HDD();
        gpu = new Gpu();
        input = new Input();
        Timer timer = new Timer();
        
        mmu.Set(MemoryMap.INTERNAL_RAM, onboard);
        mmu.Set(MemoryMap.ZRAM, onboard);
        
        mmu.Set(MemoryMap.JOYSTICK, input);
        mmu.Set(MemoryMap.TIMER, timer);
        
        mmu.Set(MemoryMap.OAM, gpu);
        mmu.Set(MemoryMap.VRAM, gpu);
        mmu.Set(MemoryMap.GPU, gpu);
        
        
        cpu = new Cpu();
        cpu.SetMmu(mmu);
        
    }
    
    public void Reset(){
        mmu.Reset();
        cpu.Reset();
        gpu.Reset();
    }
    
    public void LoadCartridge(Cartridge cart){
        mmu.Set(MemoryMap.ROM_BANK_0, cart);
        mmu.Set(MemoryMap.ROM_BANK_1, cart);
        mmu.Set(MemoryMap.EXTERNAL_RAM, cart);
    }
    
    private void Dispatch(){
        //Step the cpu
        int deltaTime = cpu.Step();
        
        //Step the gpu
        gpu.Step(deltaTime);
    }
}

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
import gameboy.game.CartridgeAdapter;
import gameboy.gpu.Gpu;
import gameboy.io.Timer;
import gameboy.serial.SerialConnection;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 *
 * @author Colin
 */
public class Gameboy {
    
    public final Cpu cpu;
    public final Gpu gpu;
    public final MemoryMap mmu;
    public final Input input;
    public final Timer timer;
    public final CartridgeAdapter cartridge;

    public Gameboy(){
        
        mmu = new MemoryMap();

        HDD onboard = new HDD();
        gpu = new Gpu();
        input = new Input();
        timer = new Timer();
        cartridge = new CartridgeAdapter();
        SerialConnection sysio = new SerialConnection(new InputStreamReader(System.in), new OutputStreamWriter(System.out));
        sysio.EnableWrite(false);
        
        mmu.Set(MemoryMap.INTERNAL_RAM, onboard);
        mmu.Set(MemoryMap.ZRAM, onboard);
        
        mmu.Set(MemoryMap.JOYSTICK, input);
        mmu.Set(MemoryMap.TIMER, timer);
        
        mmu.Set(MemoryMap.OAM, gpu);
        mmu.Set(MemoryMap.VRAM, gpu);
        mmu.Set(MemoryMap.GPU, gpu);
        
        mmu.Set(MemoryMap.ROM_BANK_0, cartridge);
        mmu.Set(MemoryMap.ROM_BANK_1, cartridge);
        mmu.Set(MemoryMap.EXTERNAL_RAM, cartridge);
        
        mmu.Set(MemoryMap.SERIALIO, sysio);
        
        cpu = new Cpu(mmu);
        
    }
    
    public void Reset(){
        mmu.Reset();
        gpu.Reset();
        cpu.Reset();
        input.Reset();
        timer.Reset();
        cartridge.Reset();
        System.out.println("GB reset");
    }
    
    public void LoadCartridge(Cartridge cart){
        Reset();
        cartridge.LoadCart(cart);
    }
    
    public void OnBufferReady(Listener listener){
        this.gpu.OnVBlank = listener;
    }
    
    public void Play(){
        while(true){
            try{
                Dispatch();
            }catch(Exception e){
                break;
            }
        }
    }
    
    public void Dispatch(){
        //Step the cpu
        int deltaTime = cpu.Step();
        
        //Step the gpu
        gpu.Step(deltaTime);
        
        //Step the timer
        timer.Increment(deltaTime);
    }
    
    public String toString(){
        return cpu.recentOps.getLast() + " WITH "+ cpu.reg.toString();
    }
}

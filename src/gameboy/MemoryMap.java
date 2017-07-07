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
public class MemoryMap implements IMemory{
    
    //GB memory map is as follows
    //http://gbdev.gg8.se/wiki/articles/Main_Page
    /*
        FFFF            Interrupt Enable Flag
        FF80 - FFFE     Zero page (127bytes)
        FF00 - FF7F     Hardware IO Registers
        FEA0 - FEFF     Unusable memory 
        FE00 - FE9F     OAM (object attribute memory)
        E000 - FDFF     Shadow Internal RAM
        D000 - DFFF     Internal RAM (switchable)
        C000 - CFFF     Internal RAM (fixed)
        A000 - BFFF     External Cartridge RAM
        9C00 - 9FFF     Video RAM (background map data 2)
        9800 - 9BFF     Video RAM (background map data 1)
        8000 - 97FF     Video RAM (character ram)
        4000 - 7FFF     Cartridge ROM (switchable)
        0150 - 3FFF     Cartridge ROM (fixed)
        0100 - 014F     Cartridge Header
        0000 - 00FF     Restart and Interrupt Vectors
    */
    
    public IMemory ROM_BANK_0;
    public IMemory ROM_BANK_1;
    public IMemory VRAM;
    public IMemory EXTERNAL_RAM;
    public IMemory INTERNAL_RAM;
    public IMemory OAM;
    public IMemory INPUT_OUTPUT;
    public IMemory ZRAM;
    public IMemory INTERUPT_REGISTER;
    
    public void Reset(){
        ROM_BANK_0.Reset();
        ROM_BANK_1.Reset();
        VRAM.Reset();
        EXTERNAL_RAM.Reset();
        INTERNAL_RAM.Reset();
        OAM.Reset();
        INPUT_OUTPUT.Reset();
        ZRAM.Reset();
        INTERUPT_REGISTER.Reset();
    }
    
    private static boolean in(int x, int lower, int upper) {
        return lower <= x && x <= upper;
    }
    
    public int MaxAddress(){
        return 0xFFFF;
    }
    
    public int rb(int addr){
        if(in(addr, 0, 0x00FF)){
            //Restart and Interrupt Vectors
            if(ROM_BANK_0 == null)
                return 0;
            return ROM_BANK_0.rb(addr);
        }
        else if(in(addr, 0x0100, 0x014F)){
            //Cartridge Header
            if(ROM_BANK_0 == null)
                return 0;
            return ROM_BANK_0.rb(addr);
        }
        else if(in(addr, 0x0150, 0x3FFF)){
            //Cartridge ROM (fixed)
            if(ROM_BANK_0 == null)
                return 0;
            return ROM_BANK_0.rb(addr);
        }
        else if(in(addr, 0x4000, 0x7FFF)){
            //Cartridge ROM (switchable)
            if(ROM_BANK_1 == null)
                return 0;
            return ROM_BANK_1.rb(addr);
        }
        else if(in(addr, 0x8000, 0x9FFF)){
            //Video RAM
            if(VRAM == null)
                return 0;
            return VRAM.rb(addr);
        }
        else if(in(addr, 0xA000, 0xBFFF)){
            //External Cartridge RAM
            if(EXTERNAL_RAM == null)
                return 0;
            return EXTERNAL_RAM.rb(addr);
        }
        else if(in(addr, 0xC000, 0xCFFF)){
            //Internal RAM (fixed)
            if(INTERNAL_RAM == null)
                return 0;
            return INTERNAL_RAM.rb(addr);
        }
        else if(in(addr, 0xD000, 0xDFFF)){
            //Internal RAM (switchable)
            if(INTERNAL_RAM == null)
                return 0;
            return INTERNAL_RAM.rb(addr);
        }
        else if(in(addr, 0xE000, 0xFDFF)){
            //Shadow Interal RAM
            if(INTERNAL_RAM == null)
                return 0;
            return INTERNAL_RAM.rb(addr);
        }
        else if(in(addr, 0xFE00, 0xFE9F)){
            //OAM (object attribute memory)
            if(OAM == null)
                return 0;
            return OAM.rb(addr);
        }
        else if(in(addr, 0xFEA0, 0xFEFF)){
            //Unusable Memory
        }
        else if(in(addr, 0xFF00, 0xFF7F)){
            //Hardware IO Registers
            if(INPUT_OUTPUT == null)
                return 0;
            return INPUT_OUTPUT.rb(addr);
        }
        else if(in(addr, 0xFF80, 0xFFFE)){
            //Zero page
            if(ZRAM == null)
                return 0;
            return ZRAM.rb(addr);
        }
        else if(addr == 0xFFFF){
            //Interrupt Enable Flag
            if(INTERUPT_REGISTER == null)
                return 0;
            return INTERUPT_REGISTER.rb(addr);
        }
        return 0;
    }
    
    public int rw(int addr){
        return rb(addr) + (rb(addr + 1) << 8);
    }
    
    public void wb(int addr, int value){
        if(in(addr, 0, 0x00FF)){
            //Restart and Interrupt Vectors
            ROM_BANK_0.wb(addr, value);
        }
        else if(in(addr, 0x0100, 0x014F)){
            //Cartridge Header
            ROM_BANK_0.wb(addr, value);
        }
        else if(in(addr, 0x0150, 0x3FFF)){
            //Cartridge ROM (fixed)
            ROM_BANK_0.wb(addr, value);
        }
        else if(in(addr, 0x4000, 0x7FFF)){
            //Cartridge ROM (switchable)
            ROM_BANK_1.wb(addr, value);
        }
        else if(in(addr, 0x8000, 0x9FFF)){
            //Video RAM
            VRAM.wb(addr, value);
        }
        else if(in(addr, 0xA000, 0xBFFF)){
            //External Cartridge RAM
            EXTERNAL_RAM.wb(addr, value);
        }
        else if(in(addr, 0xC000, 0xCFFF)){
            //Internal RAM (fixed)
            INTERNAL_RAM.wb(addr, value);
        }
        else if(in(addr, 0xD000, 0xDFFF)){
            //Internal RAM (switchable)
            INTERNAL_RAM.wb(addr, value);
        }
        else if(in(addr, 0xE000, 0xFDFF)){
            //Shadow Interal RAM
            INTERNAL_RAM.wb(addr, value);
        }
        else if(in(addr, 0xFE00, 0xFE9F)){
            //OAM (object attribute memory)
            OAM.wb(addr, value);
        }
        else if(in(addr, 0xFEA0, 0xFEFF)){
            //Unusable Memory
        }
        else if(in(addr, 0xFF00, 0xFF7F)){
            //Hardware IO Registers
            INPUT_OUTPUT.wb(addr, value);
        }
        else if(in(addr, 0xFF80, 0xFFFE)){
            //Zero page
            ZRAM.wb(addr, value);
        }
        else if(addr == 0xFFFF){
            //Interrupt Enable Flag
            INTERUPT_REGISTER.wb(addr, value);
        }
    }
    
    public void ww(int addr, int value){
        wb(addr,value&255); 
        wb(addr+1,value>>8);
    }
    
}

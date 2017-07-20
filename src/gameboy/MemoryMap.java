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
public class MemoryMap{
    
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
    
    public static final int ROM_BANK_0 = 0;
    public static final int ROM_BANK_1 = 1;
    public static final int VRAM = 2;
    public static final int EXTERNAL_RAM = 3;
    public static final int INTERNAL_RAM = 4;
    public static final int OAM = 5;
    public static final int GPU = 6;
    public static final int ZRAM = 7;
    public static final int JOYSTICK = 9;
    public static final int TIMER = 10;
    public static final int SERIALIO = 8;
    
    private IMemory[] ctrl = new IMemory[11];
    public int i_enable = 0;    //Which interupts are enabled
    public int i_flags = 0;     //Which interrupts need to be fired
    
    public void Reset(){
        for(IMemory mem : ctrl){
            if(mem != null)
                mem.Reset();
        }
    }
    
    private static boolean in(int x, int lower, int upper) {
        return lower <= x && x <= upper;
    }
    
    public int MaxAddress(){
        return 0xFFFF;
    }
    
    public int rb(int addr){
       if(in(addr, 0x0000, 0x3FFF)){
           //Rom Bank 0
           return ctrl[ROM_BANK_0].rb(addr);
       }
       else if(in(addr, 0x4000, 0x7FFF)){
           //Rom Bank 1
           return ctrl[ROM_BANK_1].rb(addr);
       }
       else if(in(addr, 0x8000, 0x9FFF)){
           //Video Ram
           return ctrl[VRAM].rb(addr);
       }
       else if(in(addr, 0xA000, 0xBFFF)){
           //Cartridge Ram
           return ctrl[EXTERNAL_RAM].rb(addr);
       }
       else if(in(addr, 0xC000, 0xFDFF)){
           //Work Ram and Shadow
           return ctrl[INTERNAL_RAM].rb(addr);
       }
       else if(in(addr, 0xFE00, 0xFE9F)){
           //Object Attribute Memory
           return ctrl[OAM].rb(addr);
       }
       else if(in(addr, 0xFF80, 0xFFFE)){
           //Zero Page Ram
           return ctrl[ZRAM].rb(addr);
       }
       else if(addr == 0xFF00){
           //Joystick input
           return ctrl[JOYSTICK].rb(addr);
       }
       else if(in(addr, 0xFF01, 0xFF03)){
           //Serial IO Data, Control, UNKNOWN
           return 0;
       }
       else if(in(addr, 0xFF04, 0xFF0E)){
           //Timer
           return ctrl[TIMER].rb(addr);
       }
       else if(addr == 0xFF0F){
           //Interrupt Flags
           return i_flags;
       }
       else if(in(addr, 0xFF10, 0xFF39)){
           //Sound control, envelope ect
           return 0;
       }
       else if(in(addr, 0xFF40, 0xFF7F)){
           return ctrl[GPU].rb(addr);
       }
       else if(addr == 0xFFFF){
           return i_enable;
       }
       return 0;
    }
    
    public int rw(int addr){
        return (rb(addr + 1) << 8) | rb(addr);
    }
    
    public void wb(int addr, int value){
        if(in(addr, 0x0000, 0x3FFF)){
           //Rom Bank 0 -- Readonly
           ctrl[ROM_BANK_0].wb(addr, value);    //Read Only Skip
       }
       else if(in(addr, 0x4000, 0x7FFF)){
           //Rom Bank 1 -- Readonly
           ctrl[ROM_BANK_1].wb(addr, value);    //Read Only Skip
       }
       else if(in(addr, 0x8000, 0x9FFF)){
           //Video Ram
           ctrl[VRAM].wb(addr, value);
       }
       else if(in(addr, 0xA000, 0xBFFF)){
           //Cartridge Ram
           ctrl[EXTERNAL_RAM].wb(addr, value); //WORKING I WROTE TO THIS
       }
       else if(in(addr, 0xC000, 0xFDFF)){
           //Work Ram and Shadow
           ctrl[INTERNAL_RAM].wb(addr, value);
       }
       else if(in(addr, 0xFE00, 0xFE9F)){
           //Object Attribute Memory
           ctrl[OAM].wb(addr, value);
       }
       else if(in(addr, 0xFF80, 0xFFFE)){
           //Zero Page Ram
           ctrl[ZRAM].wb(addr, value);
       }
       else if(addr == 0xFF00){
           //Joystick input
           ctrl[JOYSTICK].wb(addr, value);
       }
       else if(in(addr, 0xFF01, 0xFF03)){
           if(addr == 0xFF01 && ctrl[SERIALIO] != null){ //Serial IO Data
               ctrl[SERIALIO].wb(addr, value);
           }
           //Control, UNKNOWN
       }
       else if(in(addr, 0xFF04, 0xFF0E)){
           //Timer
           ctrl[TIMER].wb(addr, value);
       }
       else if(addr == 0xFF0F){
           //Interrupt Flags
           i_flags = value;
       }
       else if(in(addr, 0xFF10, 0xFF39)){
           //Sound control, envelope ect
       }
       else if(in(addr, 0xFF40, 0xFF7F)){
           ctrl[GPU].wb(addr, value);
       }
       else if(addr == 0xFFFF){
           i_enable = value;
       }
    }
    
    public void ww(int addr, int value){
        wb(addr,value&255); 
        wb(addr+1,value>>8);
    }
    
    public IMemory Get(int i){
        return this.ctrl[i];
    }
    
    public void Set(int i, IMemory map){
        this.ctrl[i] = map;
        map.SetMMU(this);
    }
    
}

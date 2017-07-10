/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameboy;

import java.util.Arrays;

/**
 *
 * @author Colin Halseth
 */
public class Cartridge implements IMemory{

    public static final int ERAM_SIZE = 32768;
    
    public final RomInfo info;
    private int[] rom;
    private int[] eram = new int[ERAM_SIZE]; //External Cartridge RAM
    
    private int romoff = 0x4000;
    private int ramoff = 0;
    
    private static boolean in(int x, int lower, int upper) {
        return lower <= x && x <= upper;
    }
    
    protected Cartridge(int[] rom){
        this.info = new RomInfo(rom);
        this.rom = rom;
    }
    
    public String toString(){
        return info.title;
    }
    
    @Override
    public void Reset() {
        Arrays.fill(eram, 0);
        
        romoff = 0x4000;
        ramoff = 0;
    }

    @Override
    public int rb(int addr) {
        if(in(addr, 0, 0x3FFF)){
            //Cartridge ROM (fixed) (rom bank 0)
            return rom[addr];
        }
        else if(in(addr, 0x4000, 0x7FFF)){
            //Cartridge ROM (switchable) (rom bank 1)
            return rom[romoff + (addr&0x3FFF)];
        }
        else if(in(addr, 0xA000, 0xBFFF)){
            //External cartridge RAM
            return eram[ramoff + (addr&0x1FFF)]; //eram[ramoffs+(addr&0x1FFF)];
        }
        return 0;
    }

    @Override
    public void wb(int addr, int value) {
        if(in(addr, 0xA000, 0xBFFF)){
            //External cartridge RAM
            eram[ramoff + (addr&0x1FFF)] = value; //eram[ramoffs+(addr&0x1FFF)];
        }
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameboy.game.controller;

import gameboy.game.Cartridge;
import java.util.Arrays;

/**
 *
 * @author Colin Halseth
 */
public class MBC2 implements MBC{
    
    public static final int ERAM_SIZE = 512;    //512x4 bits eram
    private int[] eram = new int[ERAM_SIZE];    //External Cartridge RAM
    
    private boolean ramEnabled = false;
    
    private int rambank = 0;
    private int rombank = 1;
 
    private Cartridge cart;
    
    public MBC2(Cartridge cart){
        this.cart = cart;
    }
    
     public void Reset(){
        Arrays.fill(eram, 0);
        
        ramEnabled = false;
        
        rambank = 0;
        rombank = 1;
    }
    
     private static boolean in(int x, int lower, int upper) {
        return lower <= x && x <= upper;
    }
    
    public int rb(int addr){
        //Create the appropriate offsets if required
        int romoff = GetRomOffset(); //Rom bank 1
        int ramoff = GetRamOffset();
        
        if(in(addr, 0, 0x3FFF)){
            //Cartridge ROM (fixed) (rom bank 0)
            if(cart == null)
                return 0;
            return cart.read(addr);
        }
        else if(in(addr, 0x4000, 0x7FFF)){
            //Cartridge ROM (switchable) (rom bank 1)
            if(cart == null)
                return 0;
            return cart.read((romoff + (addr - 0x4000)));
        }
        else if(in(addr, 0xA000, 0xBFFF)){
            //External cartridge RAM
            return eram[ramoff + (addr - 0xA000)]; //eram[ramoffs+(addr&0x1FFF)];
        }
        return 0;
    }
    
    public void wb(int addr, int value){
        //Create the appropriate offsets if required
        int ramoff = GetRamOffset();
        
        this.hasOccurredWrite(addr, value);
        
        if(in(addr, 0xA000, 0xBFFF)){
            //External cartridge RAM MBC2 only used 4 bits
            eram[ramoff + (addr - 0xA000)] = value & 0xF; 
        }
    }
     
    /**
     * If a write has occurred to the cartridge, check if an action is performed in accordance with MBC rules
     * @param addr
     * @param value 
     */
    public void hasOccurredWrite(int addr, int value){
        if(addr >= 0x0000 && addr <= 0x1FFF){
            //Enable RAM. Any Value with 0x0AH in the lower 4 bits enables ram, other values disable ram
            ramEnabled = (value & 0x0F) == 0x0A;
        }else if(addr >= 0x2000 && addr <= 0x3FFF){
            //Last 4 bits of the value become the rom bank number
            rombank = value & 0x0F;
            if(rombank == 0)
                rombank++;
            rombank &= (cart.info.romBanks - 1);
        }
    }
    
    public boolean IsRamEnabled(){
        return this.ramEnabled;
    }
    
    /**
     * Get the offset value to use for ram access
     * @return 
     */
    @Override
    public int GetRamOffset(){
        return rambank * 0x2000;
    }
    
    /**
     * Get the offset value to use for rom access
     * @return 
     */
    @Override
    public int GetRomOffset(){
        return rombank * 0x4000;
    }
    
}

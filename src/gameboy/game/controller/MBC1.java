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
public class MBC1 implements MBC{
    
    public static final int ERAM_SIZE = 32768;  //32KB eram
    private int[] eram = new int[ERAM_SIZE];    //External Cartridge RAM
    
    private boolean ramEnabled = false;
    private boolean ramSelected = true;
    
    private int rambank = 0;
    private int rombank = 1;
 
    private Cartridge cart;
    
    public MBC1(Cartridge cart){
        this.cart = cart;
    }
    
    public void Reset(){
        Arrays.fill(eram, 0);
        
        ramEnabled = false;
        ramSelected = true;
        
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
            return cart.read((romoff + (addr&0x3FFF)));
        }
        else if(in(addr, 0xA000, 0xBFFF)){
            //External cartridge RAM
            return eram[ramoff + (addr&0x1FFF)]; //eram[ramoffs+(addr&0x1FFF)];
        }
        return 0;
    }
    
    public void wb(int addr, int value){
        //Create the appropriate offsets if required
        int romoff = GetRomOffset(); //Rom bank 1
        int ramoff = GetRamOffset();
        
        this.hasOccurredWrite(addr, value);
        
        if(in(addr, 0xA000, 0xBFFF)){
            //External cartridge RAM
            eram[ramoff + (addr&0x1FFF)] = value; //eram[ramoffs+(addr&0x1FFF)];
        }
    }
    
    /**
     * If a write has occurred to the cartridge, check if an action is performed in accordance with MBC rules
     * @param addr
     * @param value 
     */
    @Override
    public void hasOccurredWrite(int addr, int value){
        if(addr >= 0x0000 && addr <= 0x1FFF){
            //Enable RAM. Any Value with 0x0AH in the lower 4 bits enables ram, other values disable ram
            ramEnabled = (value & 0x0A) == 0x0A;
        }else if(addr >= 0x2000 && addr <= 0x3FFF){
            //Writing to this adddress selects the lower 5 bits of the rom back number 01-1Fh, 
            //if 00 is written, bank 1 is still selected
            if((value & 0xFF) == 0)
                value = 1;
            
            rombank &= 0x60;                        //Clear lower bits
            rombank |= Math.max(value & 0x1F, 1);   //Set lower bits
        }else if(addr >= 0x4000 && addr <= 0x5FFF){
            //This 2 bit register can be used to select a ram bank in the range 00-03 or specify the upper 2 bits of the bank number
            //This behavior depends on the ROM/RAM mode select
            if(!ramSelected){
                rombank &= 0x1F;               //Clear upper 2 bits
                rombank |= (value & 3) << 5;   //Set upper 2 bits
            }else{
                rambank = value & 0b11;         //Set rambank number
            }
        }
        else if(addr >= 6000 && addr <= 0x7FFF){
            //This one bit register selects whether the two bits above should be used as the upper two bits of the rom bank
            //or as the ram bank number
            ramSelected = (value & 0x1) == 0x1; //Ram banking mode, else Rom banking mode
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

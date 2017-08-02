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
    
    public int[] getRam(){
        return this.eram;
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
            if(!ramSelected){
                return eram[addr - 0xA000];
            }else{
                return eram[(addr - 0xA000) + ramoff];
            }
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
            if(!ramSelected){
                eram[addr - 0xA000] = value;
            }else{
                eram[(addr - 0xA000) + ramoff] = value;
            }
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
            if(!ramSelected){
                //If rammode not selected, this represents the lower 5 bits, preserve the upper 5 bits
                rombank = (value & 0x1F) | ((rombank >> 5) << 5);
            }else{
                rombank = value & 0x1F;
            }
            
            //Never select 0th rombank
            if(rombank == 0x00 || rombank == 0x20 || rombank == 0x40 || rombank == 0x60){
                rombank++;
            }
            
            rombank &= (cart.header.romClass.banks - 1);
            
        }else if(addr >= 0x4000 && addr <= 0x5FFF){
            //This 2 bit register can be used to select a ram bank in the range 00-03 or specify the upper 2 bits of the bank number
            //This behavior depends on the ROM/RAM mode select
            if(!ramSelected){
                rombank = (rombank & 0x1F) | ((value & 3) << 5);   //Set upper 2 bits
                rombank &= (cart.header.romClass.banks - 1);
            
            }else{
                rambank = value & 3;          //Set rambank number
                rambank &= (cart.header.eramClass.banks - 1);
            }
        }
        else if(addr >= 6000 && addr <= 0x7FFF){
            //This one bit register selects whether the two bits above should be used as the upper two bits of the rom bank
            //or as the ram bank number
            ramSelected = (value & 0x1) != 0; //Ram banking mode, else Rom banking mode
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

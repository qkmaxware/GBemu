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
public class RomOnly implements MBC{

    public static final int ERAM_SIZE = 32768;  //32KB eram
    private int[] eram = new int[ERAM_SIZE];    //External Cartridge RAM
    
    private Cartridge cart;
    
    public RomOnly(Cartridge cart){
        this.cart = cart;
    }
    
    @Override
    public int GetRamOffset() {
        return 0;
    }

    @Override
    public int GetRomOffset() {
        return 0x4000;
    }

    @Override
    public void Reset() {
        Arrays.fill(eram, 0);
    }

    private static boolean in(int x, int lower, int upper) {
        return lower <= x && x <= upper;
    }
    
    public int[] getRam(){
        return this.eram;
    }
    
    @Override
    public int rb(int addr) {
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

    @Override
    public void wb(int addr, int value) {
        //Create the appropriate offsets if required
        int romoff = GetRomOffset(); //Rom bank 1
        int ramoff = GetRamOffset();
        
        if(in(addr, 0xA000, 0xBFFF)){
            //External cartridge RAM
            eram[ramoff + (addr&0x1FFF)] = value; //eram[ramoffs+(addr&0x1FFF)];
        }
    }
    
}

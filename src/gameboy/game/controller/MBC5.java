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
public class MBC5 implements MBC{

    public static final int ERAM_SIZE = 131072;
    private int[] eram = new int[ERAM_SIZE];
    
    private boolean ramEnabled = false;
    private int rambank = 0;
    private int rombank = 1;
    
    private Cartridge cart;
    
    public MBC5(Cartridge cart){
        this.cart = cart;
    }
    
    @Override
    public void Reset() {
        Arrays.fill(eram, 0);
        ramEnabled = false;
        rambank = 0;
        rombank = 1;
    }

    @Override
    public int GetRamOffset() {
        return rambank * 0x2000;
    }

    @Override
    public int GetRomOffset() {
        return rombank * 0x4000;
    }

    @Override
    public int[] getRam() {
        return this.eram;
    }

    @Override
    public int rb(int addr) {
        int romoff = GetRomOffset();
        int ramoff = GetRamOffset();
        
        //ROM Bank 0, read only
        if(addr >= 0 && addr <= 0x3FFF){
            return cart.read(addr);
        }
        //ROM Bank 1, switchable including rom bank 0
        else if(addr >= 0x4000 && addr <= 0x7FFF){
            return cart.read((romoff + (addr - 0x4000)));
        }
        //External Ram
        else if(addr >= 0xA000 && addr <= 0xBFFF){
            if(ramEnabled)
                return eram[(addr - 0xA000) + ramoff];
        }
        
        return 0;
    }

    @Override
    public void wb(int addr, int value) {
        int romoff = GetRomOffset();
        int ramoff = GetRamOffset();
        
        //RAM enable
        if(addr >= 0x0000 && addr <= 0x1FFF){
            this.ramEnabled = (value & 0xF) == 0x0A;
        }
        //Lower 8 bits of the ROM bank number
        else if(addr >= 0x2000 && addr <= 0x2FFF){
            this.rombank = value & 0xFF | (this.rombank & 0x100);
            this.rombank &= (cart.header.romClass.banks - 1);
        }
        //Upper 1 bit of the rom bank number
        else if(addr >= 0x3000 && addr <= 0x3FFF){
            this.rombank = (this.rombank & 0xFF) | ((value & 0b1) << 8);
            this.rombank &= (cart.header.romClass.banks - 1);
        }
        //RAM bank number
        else if(addr >= 0x4000 && addr <= 0x5FFF){
            this.rambank = value & 0x0F;
            this.rambank &= (cart.header.eramClass.banks - 1);
        }
        //RAM write
        else if(addr >= 0xA000 && addr <= 0xBFFF){
            if(this.ramEnabled)
                this.eram[ramoff + (addr - 0xA000)] = value;
        }
    }
    
}

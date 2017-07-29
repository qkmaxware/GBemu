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
    
    private Cartridge cart;
    
    private int rombank = 1;
    private boolean ramEnabled = false;
    
    public static final int ERAM_SIZE = 512;
    private int[] eram = new int[ERAM_SIZE];
    
    public MBC2(Cartridge cart){
        this.cart = cart;
    }

    @Override
    public void Reset() {
        rombank = 1;
        ramEnabled = false;
        
        Arrays.fill(eram, 0);
    }
    
    @Override
    public int GetRamOffset() {
        return 0x2000;
    }

    @Override
    public int GetRomOffset() {
        return rombank * 0x4000;
    }

    @Override
    public int rb(int addr) {
        //ROM BANK 0 (Read Only)
        if(addr >= 0x0000 && addr <= 0x3FFF){
            return cart.read(addr);
        }
        //ROM BANK 1-F (Read Only)
        else if(addr >= 0x4000 && addr <= 0x7FFF){
            return cart.read((addr - 0x4000) + GetRomOffset());
        }
        //512x4bit RAM (RW)
        else if(addr >= 0xA000 && addr <= 0xA1FF){
            if(this.ramEnabled)
                return this.eram[addr - 0xA000];
            else
                return 0xFF;
        } 
        
        return 0;
    }

    @Override
    public void wb(int addr, int value) {
        //RAM ENABLE (Write Only)
        if(addr >= 0x0000 && addr <= 0x1FFF){
            //Least significant bit of the upper address byte must be 0 to enable/disable ram cart
            if((addr & 0x100) == 0){
                ramEnabled = ((value & 0x0F) == 0x0A);
            }else{
                System.out.println("Not able to enable/disable ram unless theres a 0 in the least significant bit of the upper address.");
            }
        }
        //ROM BANK NUMBER (Write Only)
        else if(addr >= 0x2000 && addr <= 0x3FFF){
            //Least significant bit of the upper address byte must be 1 to select a rom bank
            if((addr & 0x100) != 0){
                this.rombank = value & 0x0F;
                this.rombank &= (this.cart.header.romClass.banks - 1);
                if(this.rombank == 0)
                    this.rombank = 1;
            }else{
                System.out.println("Not able to set the rombank without a 1 in the least significant bit of the upper address.");
            }
        }
        //512x4bit RAM (RW)
        else if(addr >= 0xA000 && addr <= 0xA1FF){
            if(ramEnabled){
                this.eram[addr - 0xA000] = value & 0x0F;
            }
            else{
                System.out.println("Writing to disabled ram");
            }
        }
        
    }
    
}

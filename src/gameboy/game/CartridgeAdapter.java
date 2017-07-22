/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameboy.game;

import gameboy.IMemory;
import gameboy.MemoryMap;
import java.util.Arrays;

/**
 *
 * @author Colin Halseth
 */
public class CartridgeAdapter implements IMemory{
    
    public static final int ERAM_SIZE = 32768;
    
    private int[] eram = new int[ERAM_SIZE]; //External Cartridge RAM
    
    private int romoff = 0x4000;
    private int ramoff = 0;
    
    private Cartridge cart;
    private MBC controller;
    
    public void LoadCart(Cartridge cart){
        this.cart = cart;
        
        switch(cart.info.cartType){
            case MBC1:
                controller = (MBC) new MBC1();
                break;
            default:
                controller = null;
        }
    }
    
    private static boolean in(int x, int lower, int upper) {
        return lower <= x && x <= upper;
    }
    
    public boolean supportsCGB(){
        if(cart == null)
            return false;
        return cart.supportsCGB();
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
            if(cart == null)
                return 0;
            return cart.read(addr);
        }
        else if(in(addr, 0x4000, 0x7FFF)){
            //Cartridge ROM (switchable) (rom bank 1)
            if(cart == null)
                return 0;
            return cart.read(romoff + (addr&0x3FFF));
        }
        else if(in(addr, 0xA000, 0xBFFF)){
            //External cartridge RAM
            return eram[ramoff + (addr&0x1FFF)]; //eram[ramoffs+(addr&0x1FFF)];
        }
        return 0;
    }

    @Override
    public void wb(int addr, int value) {
        //TODO write byte to cause MBC changes
        if(controller != null)
            controller.hasOccurredWrite(this, addr, value);
        
        if(in(addr, 0xA000, 0xBFFF)){
            //External cartridge RAM
            eram[ramoff + (addr&0x1FFF)] = value; //eram[ramoffs+(addr&0x1FFF)];
        }
    }
    
    public void SetMMU(MemoryMap mmu){}
    
}

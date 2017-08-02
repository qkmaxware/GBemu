/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameboy.game;

import gameboy.game.controller.*;
import gameboy.IMemory;
import gameboy.MemoryMap;

/**
 * Acts as a stable interface between a game cartridge and a memory model
 * @author Colin Halseth
 */
public class CartridgeAdapter implements IMemory{
    
    private Cartridge cart;
    private MBC controller;
    
    public void LoadCart(Cartridge cart){
        this.cart = cart;
        
        switch(cart.header.cartType.mbc){
            case MBC1:
                controller = (MBC) new MBC1(cart);
                break;
            case MBC2:
                controller = (MBC) new MBC2(cart);
                break;
            case MBC3:
                controller = (MBC) new MBC3(cart);
                break;
            default:
                controller = (MBC) new RomOnly(cart);
                break;
        }
    }
    
    public boolean supportsCGB(){
        if(cart == null)
            return false;
        return cart.supportsCGB();
    }
    
    /*
    public void LoadRam() throws IOException{
        BufferedReader reader = new BufferedReader(new FileReader(this.cart.source.getAbsolutePath()+".battery"));
        String s; int idx = 0;
        while((s = reader.readLine()) != null && idx < eram.length){
            eram[idx] = Integer.parseInt(s, 16);
        }
    }
    
    public void SaveRam() throws IOException{
        BufferedWriter ow = new BufferedWriter(new FileWriter(this.cart.source.getAbsolutePath()+".battery"));
        for(int i = 0; i < this.eram.length; i++){
            ow.write(Integer.toHexString(eram[i]));
            ow.newLine();
        }
        ow.flush();
        ow.close();
    }*/
    
    @Override
    public void Reset() {
        if(this.controller != null)
            this.controller.Reset();
    }

    @Override
    public int rb(int addr) {
        if(this.controller != null)
            return this.controller.rb(addr);
        return 0;
    }

    @Override
    public void wb(int addr, int value) {
        if(this.controller != null)
            this.controller.wb(addr, value);
    }
    
    public void SetMMU(MemoryMap mmu){}
    
}

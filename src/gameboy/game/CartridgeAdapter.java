/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameboy.game;

import gameboy.game.controller.*;
import gameboy.IMemory;
import gameboy.MemoryMap;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

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
        
        LoadRam();
    }
    
    public boolean supportsCGB(){
        if(cart == null)
            return false;
        return cart.supportsCGB();
    }
    
    /**
     * Load ram for a rom from file. Ram file name is rom file name + ".battery"
     */
    public void LoadRam(){
        if(this.controller == null || !cart.header.cartType.hasBattery)
            return;
        
        try{
            if(!this.cart.battery.exists())
                return;
            BufferedReader reader = new BufferedReader(new FileReader(this.cart.battery));
            String s; int idx = 0;
            while((s = reader.readLine()) != null && idx < controller.getRam().length){
                controller.getRam()[idx++] = Integer.parseInt(s, 16) & 0xFF;
            }
        }catch(IOException iex){
            System.out.println("Failed to load ram file");
            iex.printStackTrace();
        }
    }

    /**
     * Save the cartridge ram to file
     */
    public void SaveRam(){
        //No battery means no saveable ram
        if(controller == null || !cart.header.cartType.hasBattery)
            return;
        
        try{
            BufferedWriter ow = new BufferedWriter(new FileWriter(this.cart.battery));
            for(int i = 0; i < controller.getRam().length; i++){
                ow.write(Integer.toHexString(controller.getRam()[i]));
                ow.newLine();
            }
            ow.flush();
            ow.close();
        }catch(IOException iex){
            System.out.println("Failed to save ram file");
            iex.printStackTrace();
        }
    }
    
    @Override
    public void Reset() {
        if(this.controller != null)
            this.controller.Reset();
    }

    @Override
    public int rb(int addr) {
        if(this.controller != null){
            return this.controller.rb(addr);
        }
        return 0;
    }

    @Override
    public void wb(int addr, int value) {
        if(this.controller != null){
            this.controller.wb(addr, value);
        }
    }
    
    public void SetMMU(MemoryMap mmu){}
    
}

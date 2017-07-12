/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameboy.game;

/**
 *
 * @author Colin Halseth
 */
public class Cartridge{


    public final RomInfo info;
    private int[] rom;
    
    protected Cartridge(int[] rom){
        this.info = new RomInfo(rom);
        this.rom = rom;
    }
    
    public String toString(){
        return info.title;
    }
    
    public int read(int addr){
        return rom[addr];
    }
    
}

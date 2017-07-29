/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameboy.game;

import gameboy.game.header.CgbSupport;
import gameboy.game.header.RomInfo;
import gameboy.game.header.SgbSupport;
import java.io.File;

/**
 *
 * @author Colin Halseth
 */
public class Cartridge{

    public File source;
    public final RomInfo header;
    private int[] rom;
    
    protected Cartridge(int[] rom){
        this.header = new RomInfo(rom);
        this.rom = rom;
    }
    
    public boolean supportsSGB(){
        return header.sgb == SgbSupport.SGB;
    }
    
    public boolean supportsCGB(){
        return (header.cgb == CgbSupport.CbgAllowed || header.cgb == CgbSupport.CbgRequired);
    }
    
    public boolean HasRam(){
        return header.cartType.hasRam;
    }
    
    public String toString(){
        return (source == null ? header.title : source.getName());
    }
    
    public int read(int addr){
        return rom[addr];
    }
    
}

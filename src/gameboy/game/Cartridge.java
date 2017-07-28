/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameboy.game;

import java.io.File;

/**
 *
 * @author Colin Halseth
 */
public class Cartridge{

    public File source;
    public final RomInfo info;
    private int[] rom;
    
    protected Cartridge(int[] rom){
        this.info = new RomInfo(rom);
        this.rom = rom;
    }
    
    public boolean supportsSGB(){
        return info.sgb == RomInfo.SgbSupport.SGB;
    }
    
    public boolean supportsCGB(){
        return (info.cgb == RomInfo.CgbSupport.CbgAllowed || info.cgb == RomInfo.CgbSupport.CbgRequired);
    }
    
    public boolean HasRam(){
        return info.ramSizeClass != RomInfo.RamSizeClass.Unknown && 
                info.ramSizeClass != RomInfo.RamSizeClass.NONE &&
                info.ramBanks != 0;
    }
    
    public String toString(){
        return (source == null ? info.title : source.getName());
    }
    
    public int read(int addr){
        return rom[addr];
    }
    
}

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
public class MBC1 implements MBC{
    
    private boolean ramEnabled = false;
    private boolean romSelectMode = false;
    
    private int rambank = 0;
    private int rombank = 0;
    
    @Override
    public void hasOccurredWrite(CartridgeAdapter adapter, int addr, int value){
        if(addr >= 0x0000 && addr <= 0x1FFF){
            //Enable RAM. Any Value with 0x0AH in the lower 4 bits enables ram, other values disable ram
            ramEnabled = (value & 0x0A) == 0x0A;
        }else if(addr >= 0x2000 && addr <= 0x3FFF){
            //Writing to this adddress selects the lower 5 bits of the rom back number 01-1Fh, 
            //if 00 is written, bank 1 is still selected
            if((value & 0xFF) == 0)
                value = 1;
            
            rombank &= 0b11100000;      //Clear lower 5 bits
            rombank |= value & 0b11111; //Set lower 6 bits
        }else if(addr >= 0x4000 && addr <= 0x5FFF){
            //This 2 bit register can be used to select a ram bank in the range 00-03 or specify the upper 2 bits of the bank number
            //This behavior depends on the ROM/RAM mode select
            rombank &= 0b11111;             //Clear upper 2 bits
            rombank |= value & 0b1100000;   //Set upper 2 bits
            
            rambank = value & 0b11;         //Set rambank number
        }
        else if(addr >= 6000 && addr <= 0x7FFF){
            //This one bit register selects whether the two bits above should be used as the upper two bits of the rom bank
            //or as the ram bank number
            romSelectMode = (value & 0x1) == 0x1; //Ram bacnking mode, else Rom banking mode
        }
    }
    
    public boolean IsRamEnabled(){
        return this.ramEnabled;
    }
    
    public int GetRamOffset(){
        return 0;
    }
    
    public int GetRomOffset(){
        return 0;
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameboy.game.header;

/**
 *
 * @author Colin Halseth
 */
public class RomClass {
    
    public final int size;
    public final int banks;
    
    public RomClass(int kb, int banks){
        this.size = kb;
        this.banks = banks;
    }
    
    public static RomClass decode(int headerValue){
        switch(headerValue){
            case 0x00:
                return new RomClass(32, 2);
            case 0x01:
                return new RomClass(64, 4);
            case 0x02:
                return new RomClass(128, 8);
            case 0x03:
                return new RomClass(256, 16);
            case 0x04:
                return new RomClass(512, 32);
            case 0x05:
                return new RomClass(1000, 64);
            case 0x06:
                return new RomClass(2000, 128);
            case 0x07:
                return new RomClass(4000, 256);
            case 0x08:
                return new RomClass(8000, 512);
            case 0x52:
                return new RomClass(1100, 72);
            case 0x53:
                return new RomClass(1200, 80);
            case 0x54:
                return new RomClass(1500, 96);
            default:
                return new RomClass(0, 0);
        }
    }
    
}

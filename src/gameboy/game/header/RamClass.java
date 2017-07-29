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
public class RamClass {
    
    public final int size;
    public final int banks;
    
    public RamClass(int size, int banks){
        this.size = size;
        this.banks = banks;
    }
    
    public static RamClass decode(int headerValue){
        switch(headerValue){
            case 0x01:
                return new RamClass(2, 1);
            case 0x02:
                return new RamClass(8, 1);
            case 0x03:
                return new RamClass(32, 4);
            case 0x04:
                return new RamClass(128, 16);
            case 0x05:
                return new RamClass(64, 8);
            case 0x00:
            default:
                return new RamClass(0, 0);
        }
    }
    
}

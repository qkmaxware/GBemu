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
public enum Region{
    Unknown, Japanese, NotJapanese;
    
    public static Region decode(int headerValue){
        switch(headerValue){
            case 0x00:
                return Region.Japanese;
            case 0x01:
                return Region.NotJapanese;
            default:
                return Region.Unknown;
        }
    }
}
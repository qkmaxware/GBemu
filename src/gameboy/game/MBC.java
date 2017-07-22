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
public interface MBC {
    
    public void hasOccurredWrite(CartridgeAdapter adapter, int addr, int value);
    
}

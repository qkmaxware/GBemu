/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameboy.game.controller;

import gameboy.game.CartridgeAdapter;

/**
 *
 * @author Colin Halseth
 */
public interface MBC{
    
    /**
     * Get the offset value to use for ram access
     * @return 
     */
    public int GetRamOffset();
    
    /**
     * Get the offset value to use for rom access
     * @return 
     */
    public int GetRomOffset();
    
    public void Reset();
    
    public int rb(int addr);
    
    public void wb(int addr, int value);
    
}

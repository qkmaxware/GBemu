/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameboy.game.controller;

/**
 *
 * @author Colin Halseth
 */
public interface MBC{
   
    /**
     * Reset the state of the controller
     */
    public void Reset();
    
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
    
    /**
     * Get a reference to the ram array for this controller
     * @return 
     */
    public int[] getRam();
    
    /**
     * Read a byte from this controller
     * @param addr
     * @return 
     */
    public int rb(int addr);
    
    /**
     * Write a value to this controller (triggers side-effects)
     * @param addr
     * @param value 
     */
    public void wb(int addr, int value);
    
}

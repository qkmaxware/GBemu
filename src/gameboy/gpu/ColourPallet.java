/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameboy.gpu;

import java.awt.Color;

/**
 *
 * @author Colin Halseth
 */
public class ColourPallet {
    public class ColourMap{
        public Color DARK = Color.BLACK;
        public Color MEDIUM = Color.DARK_GRAY;
        public Color LIGHT = Color.GRAY;
        public Color WHITE = Color.WHITE;
    }
    
   
    public ColourMap bg = new ColourMap();
    public ColourMap obj0 = new ColourMap();
    public ColourMap obj1 = new ColourMap();
    
}

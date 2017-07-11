/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.awt.Dimension;

/**
 *
 * @author Colin Halseth
 */
public class Launcher {
    
    public static void main(String[] args){
        
        SwingGB window = new SwingGB();
        window.gb.Reset();
        window.setSize(new Dimension(300,360));
        window.setVisible(true);
        
    }
    
    
    
}

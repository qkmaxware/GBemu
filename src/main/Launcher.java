/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import gameboy.Cartridge;
import gameboy.CartridgeFactory;
import gameboy.Gameboy;
import java.io.File;
import java.io.FilenameFilter;
import javax.swing.JButton;
import utilities.Debugger;

/**
 *
 * @author Colin Halseth
 */
public class Launcher {
    
    public static void main(String[] args){
        Gameboy gb = new Gameboy();
        Debugger reader = new Debugger(gb);
        reader.setVisible(true);
        
        Cartridge[] games = GetLocalCartridges();
        gb.LoadCartridge(games[1]);
        
        System.out.println(games[1]);
        
    }
    
    public static Cartridge[] GetLocalCartridges(){
        File dir = new File(".");
        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".gb");
            }
        });

        Cartridge[] roms = new Cartridge[files.length];
        
        int i = 0;
        for (File gbfile : files) {
            Cartridge rom = CartridgeFactory.Load(gbfile.getAbsolutePath());
            roms[i] = rom;
            i++;
        }
        
        return roms;
    }
    
}

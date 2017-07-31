/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.awt.Dimension;
import javax.swing.SwingUtilities;
import main.swing.SwingLauncher;

/**
 *
 * @author Colin Halseth
 */
public class Launcher {
    
    public static void main(String[] args){
        //Load up the inital configs
        IniIO.DEFAULT = IniIO.template(new String[]{
            "AUTOPLAY: TRUE",
            "DEBUGGER: FALSE",
            "ROM FOLDER: ./roms/",
            "CPU TRACE: FALSE",
            "RENDER SIZE: 1x",
            "ENABLE FRAME LIMIT: TRUE",
            "FRAME RATE: 60"
        });
        
        //Load user configs and update config file with missing or new DEFAULT attributes
        IniIO userConfig = IniIO.readAndUpdate("config.ini", IniIO.DEFAULT);
        
        //Load in any command line parameters, overwrite user config if required
        
        
        //Apply the user config file
        SwingLauncher window = new SwingLauncher();
        
        window.autoPlay = userConfig.isSet("AUTOPLAY");
        window.enableDebugger = userConfig.isSet("DEBUGGER");
        window.romLocation = userConfig.getString("ROM FOLDER");
        window.enableTrace = userConfig.isSet("CPU TRACE");
        if(userConfig.exists("RENDER SIZE")){
            String value = userConfig.getString("RENDER SIZE");
            if(value.matches("\\d+x")){
                int multiple = Integer.parseInt(value.replace("x", ""));
                window.launchSize = new int[]{window.launchSize[0] * multiple, window.launchSize[1] * multiple};
            }else if(value.matches("\\d+\\,\\d+")){
                window.launchSize = new int[]{
                    Integer.parseInt(value.split(",")[0]), 
                    Integer.parseInt(value.split(",")[1])
                };
            }
        }
        window.frameRateLimit = userConfig.isSet("ENABLE FRAME LIMIT") ? userConfig.getInt("FRAME RATE") : -1;
        
        //Invoke the gui on the Swing thread not the main thread
        SwingUtilities.invokeLater(() -> {
            window.BuildFrame();
            window.setSize(new Dimension(300,360));
            window.setVisible(true);
        }); 
        
    }
    
    
    
}

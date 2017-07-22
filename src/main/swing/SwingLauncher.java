/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.swing;

import gameboy.game.Cartridge;
import gameboy.game.CartridgeFactory;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FilenameFilter;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import main.swing.utilities.Debugger;

/**
 *
 * @author Colin Halseth
 */
public class SwingLauncher extends JFrame{
    
    private String romLocation = "./roms/";
    
    public SwingLauncher(){
        //Buid swing components
        this.setTitle("Gameboy Emulator");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        
        JPanel header = new JPanel();
        header.setPreferredSize(new Dimension(0,48));
        header.setBackground(new Color(25,96,211));
        contentPane.add(header);
        
        Cartridge[] carts = GetLocalCartridges();
        
        JList body = new JList(carts);
        body.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        body.setLayoutOrientation(JList.VERTICAL);
        body.setVisibleRowCount(-1);
        body.setFixedCellHeight(32);
        ((DefaultListCellRenderer)(body.getCellRenderer())).setHorizontalAlignment(SwingConstants.CENTER);
        ((DefaultListCellRenderer)(body.getCellRenderer())).setForeground(Color.WHITE);
        
        body.setBackground(Color.BLACK);
        body.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent evt) {
                JList list = (JList)evt.getSource();
                switch(evt.getClickCount()){
                    case 2:
                        Cartridge cart = carts[list.locationToIndex(evt.getPoint())];
                                
                        SwingGB gb = new SwingGB(false);
                        gb.GetGameboy().LoadCartridge(cart);
                        gb.setTitle("Playing: "+cart.toString());
                        gb.setVisible(true);
                        
                        Debugger debugger = new Debugger(gb);
                        debugger.setVisible(true);
                        break;
                }
            }
        });
        
        JScrollPane scroller = new JScrollPane(body){
            public Dimension getPreferredSize(){
                return new Dimension(0, contentPane.getSize().height - 48);
            }
        };
        
        contentPane.add(scroller);
        
        this.add(contentPane);
    }
    
    public Cartridge[] GetLocalCartridges(){
        File dir = new File(this.romLocation);
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

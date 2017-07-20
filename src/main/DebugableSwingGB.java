/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import gameboy.game.Cartridge;
import gameboy.game.CartridgeFactory;
import gameboy.Gameboy;
import gameboy.gpu.Gpu;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import utilities.Debugger;
import utilities.SpriteViewer;
import utilities.TileViewer;

/**
 *
 * @author Colin Halseth
 */
public class DebugableSwingGB extends JFrame{
    
    public final Gameboy gb;
    private Debugger debugger;
    private SpriteViewer spriteViewer;
    
    public DebugableSwingGB(){
        //Assign code
        this.gb = new Gameboy();
        debugger = new Debugger(this.gb);
        debugger.setSize(640, 480);
        spriteViewer = new SpriteViewer(this.gb);
        TileViewer tileViewer = new TileViewer(this.gb);
        
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
        for(Cartridge cart : carts){
            
        }
        
        JList body = new JList(carts);
        body.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        body.setLayoutOrientation(JList.VERTICAL);
        body.setVisibleRowCount(-1);
        body.setFixedCellHeight(32);
        ((DefaultListCellRenderer)(body.getCellRenderer())).setHorizontalAlignment(SwingConstants.CENTER);
        ((DefaultListCellRenderer)(body.getCellRenderer())).setForeground(Color.WHITE);
        
        //Renderer
        JFrame renderer = new JFrame(){};
        renderer.setSize(Gpu.LCD_WIDTH, Gpu.LCD_HEIGHT);
        RenderPanel renderContainer = new RenderPanel(this.gb.gpu.canvas);
        renderer.add(renderContainer);
        this.gb.OnBufferReady(() -> {
            renderContainer.repaint();
        });
        JMenuBar menu = new JMenuBar();
        renderer.setJMenuBar(menu);

        JMenu deb = new JMenu("Debugging");
        menu.add(deb);
        JMenuItem mem = new JMenuItem("Memory");
        mem.addActionListener((evt) -> {
            debugger.setVisible(true);
        });
        JMenuItem spr = new JMenuItem("Sprites");
        spr.addActionListener((evt) -> {
            spriteViewer.setVisible(true);
        });
        JMenuItem tyl = new JMenuItem("Tiles");
        tyl.addActionListener((evt) -> {
            tileViewer.setVisible(true);
        });
        deb.add(mem);
        deb.add(spr);
        deb.add(tyl);
        
        body.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent evt) {
                JList list = (JList)evt.getSource();
                switch(evt.getClickCount()){
                    case 2:
                        gb.Reset();
                        Cartridge cart = carts[list.locationToIndex(evt.getPoint())];
                        gb.LoadCartridge(cart);
                        renderer.setTitle("Playing: "+cart.toString());
                        renderer.setVisible(true);
                        break;
                }
            }
        });
        
        body.setBackground(Color.BLACK);
        
        JScrollPane scroller = new JScrollPane(body){
            public Dimension getPreferredSize(){
                return new Dimension(0, contentPane.getSize().height - 48);
            }
        };
        
        contentPane.add(scroller);
        
        this.add(contentPane);
    }
    
    public Debugger getDebugger(){
        return debugger;
    }
    
    public SpriteViewer getSpriteViewer(){
        return spriteViewer;
    }
    
    public static Cartridge[] GetLocalCartridges(){
        File dir = new File("./roms/");
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

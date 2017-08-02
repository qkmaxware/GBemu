/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.swing.utilities;

import main.swing.DrawPanel;
import gameboy.Gameboy;
import gameboy.gpu.Bitmap;
import gameboy.gpu.Gpu;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 *
 * @author Colin Halseth
 */
public class BackgroundViewer extends JFrame{

    private Bitmap img = new Bitmap(32*8, 32*8);
    private Gpu gpu;

    private int charbase = 0x0000; //Or 0x0800
    private int mapbase = 0x1800;  //Or 0x1800
    
    public BackgroundViewer(Gameboy gb){
        super();
        
        this.setTitle("Map Viewer");
        this.setSize(440, 280);
        
        this.gpu = gb.gpu;
        
        JPanel center = new JPanel();
        center.setLayout(new BorderLayout());
        
        DrawPanel panel = new DrawPanel();
        panel.draw = (g2) -> {
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2.drawImage(img.GetImage(), 0, 0, panel.getWidth(), panel.getHeight(), null);
        };
        center.add(panel, BorderLayout.CENTER);
        
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new GridLayout(-1,1));
        center.add(leftPanel, BorderLayout.WEST);
        
        JPanel charset = new JPanel();
        charset.setBorder(BorderFactory.createTitledBorder("Charbase"));
        leftPanel.add(charset);
        
        ButtonGroup chargroup = new ButtonGroup();
        
        JRadioButton c_0x0000 = new JRadioButton("0x8000");
        c_0x0000.setSelected(true);
        c_0x0000.addActionListener((evt) -> {
            charbase = 0x0000;
            Refresh();
        });
        chargroup.add(c_0x0000);
        charset.add(c_0x0000);
        
        JRadioButton c_0x0800 = new JRadioButton("0x8800");
        c_0x0800.addActionListener((evt) -> {
            charbase = 0x0800;
            Refresh();
        });
        chargroup.add(c_0x0800);
        charset.add(c_0x0800);
        
        JPanel mapset = new JPanel();
        mapset.setBorder(BorderFactory.createTitledBorder("Mapbase"));
        leftPanel.add(mapset);
        
        ButtonGroup mapgroup = new ButtonGroup();
        
        JRadioButton m_0x1800 = new JRadioButton("0x9800");
        m_0x1800.setSelected(true);
        m_0x1800.addActionListener((evt) -> {
            mapbase = 0x1800;
            Refresh();
        });
        mapgroup.add(m_0x1800);
        mapset.add(m_0x1800);
        
        JRadioButton m_0x1C00 = new JRadioButton("0x9C00");
        m_0x1C00.addActionListener((evt) -> {
            mapbase = 0x1C00;
            Refresh();
        });
        mapgroup.add(m_0x1C00);
        mapset.add(m_0x1C00);
        
        
        JButton ref = new JButton("Refresh");
        ref.addActionListener((evt) -> {
            Refresh();
        });
        center.add(ref, BorderLayout.SOUTH);
        
        this.add(center);
    }
    
    public void Refresh(){
        for(int scanline = 0; scanline < this.img.GetHeight(); scanline++){
            //Scroll Y is the Y position of the background where to start drawing the viewing area from
            //Scroll X is the X position of the background where to start drawing the viewing area from
            int scrollY = 0;
            int scrollX = 0;
            int pixelY = scanline;

            //Get address space for tiles
            int tileAddressBase = charbase; //Or 0x0800
            boolean unsigned = tileAddressBase !=0 ? false : true;

            //What background memory
            int backgroundAddressBase = mapbase; //Or 0x1800

            //Which of the 32 vertical tiles am I drawing
            int tileY = scrollY + scanline;

            //Which of the 8 vertical pixels of this tile am I on
            int rowY = ((tileY & 255) / 8) * 32;

            //Time to start drawing the scanline
            for(int pixelX = 0; pixelX < img.GetWidth(); pixelX++){
                int xpos = pixelX + scrollX;

                //Which of the 32 horizontal tiles does this pixel fall
                int tileX = (xpos / 8) & 31;

                //Get the tile id number
                int tileNum = gpu.readVRAM(backgroundAddressBase + rowY + tileX);
                if(!unsigned){
                    //Signed space, tile address != 0
                    if(tileNum < 128)
                        tileNum = 256+tileNum;
                }

                //Get the pixel
                int colorNum = gpu.readTile(tileNum, tileY % 8, xpos % 8);
                Color c = getColor(colorNum);

                //Set the pixel in the buffer and in the scanline
                img.SetColor(pixelX, pixelY, c);
            }
        }
        this.repaint();
    }
    
    private Color getColor(int color){
        switch(color){
            case 0: 
                return Color.WHITE;
            case 1: 
                return Color.LIGHT_GRAY;
            case 2:
                return Color.DARK_GRAY;
            default:
                return Color.BLACK;
        }
    }
    
}

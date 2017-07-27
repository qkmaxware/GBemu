/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.swing.utilities;

import gameboy.Gameboy;
import gameboy.gpu.Bitmap;
import gameboy.gpu.Gpu;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/**
 *
 * @author Colin Halseth
 */
public class TileViewer extends JFrame{
    
    public interface Action{
        public void Invoke(Graphics2D g2);
    }
    
    private class DrawPanel extends JPanel{
        public Action draw;
        @Override
        public void paintComponent(Graphics g){ if(draw != null) draw.Invoke((Graphics2D)g); }
    }
    
    private Gameboy gb;
    private Gpu gpu;
    private int selected =0;
    private Bitmap bmp = new Bitmap(8,8);
    private Bitmap all = new Bitmap(15*8, 17*8); //255 Tiles per base 0x0000, 0x0800
    private int base = 0;
    
    private JPanel drawPanel;
    private JPanel allPanel;
    
    private JTextField tileid;
    
    public TileViewer(Gameboy gb){
        super();
        
        this.gb = gb;
        this.gpu = gb.gpu;
        
        this.setTitle("Tiles");
        
        this.setLayout(new BorderLayout());
        
        this.setSize(500,340);
        
        JPanel header = new JPanel();
        
        JButton left = new JButton("<");
        left.addActionListener((evt) -> {
            selected --;
            if(selected < 0)
                selected = gpu.tilemap.length - 1;
            
            Refresh();
        });
        
        tileid = new JTextField(String.valueOf(this.selected));
        tileid.setPreferredSize(new Dimension(50, 32));
        tileid.setEditable(false);
        
        JButton right = new JButton(">");
        right.addActionListener((evt) -> {
            selected ++;
            if(selected >= gpu.tilemap.length)
                selected = 0;
            
            Refresh();
        });
        
        JButton jumptp = new JButton("goto");
        jumptp.addActionListener((evt) -> {
            String res = JOptionPane.showInputDialog("Tile index");
            try{
                int i = Integer.parseInt(res);
                if(i < 0 || i >= gpu.tilemap.length)
                    throw new Exception("Index out of bounds");
                selected = i;
                Refresh();
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Invalid tile index");
            }
        });
        
        header.add(left);
        header.add(tileid);
        header.add(right);
        header.add(jumptp);
        
        DrawPanel center = new DrawPanel();
        this.drawPanel = center;
        center.draw = (g2) -> {
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            int min = Math.min(center.getWidth(), center.getHeight());
            g2.setColor(this.getBackground());
            g2.fillRect(0, 0, this.getWidth(), this.getHeight());
            g2.drawImage(bmp.GetImage(), 0, 0, min, min, null);
        };
        
        JButton refresh = new JButton("Refresh");
        refresh.addActionListener((evt) -> {
            Refresh();
        });
        
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new GridLayout(-1,1));
        leftPanel.add(header);
        leftPanel.add(center);
        this.add(refresh, BorderLayout.SOUTH);
        
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        DrawPanel allpanel = new DrawPanel();
        allPanel = allpanel;
        rightPanel.add(allpanel, BorderLayout.CENTER);
        allpanel.draw = (g2) -> {
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2.drawImage(all.GetImage(), 0, 0, allpanel.getWidth(), allpanel.getHeight(), null);
        };
        
        JRadioButton b8000 = new JRadioButton("0x8000");
        JRadioButton b8800 = new JRadioButton("0x8800");
        ButtonGroup group = new ButtonGroup();
        b8000.addActionListener((evt) -> {base=0; Refresh();});
        b8800.addActionListener((evt) -> {base=1; Refresh();});
        group.add(b8000); group.add(b8800);
        b8000.setSelected(true);
        JPanel ft = new JPanel();
        ft.setBorder(BorderFactory.createTitledBorder("Char Base"));
        ft.add(b8000);
        ft.add(b8800);
        leftPanel.add(ft);
        
        this.add(leftPanel, BorderLayout.WEST);
        this.add(rightPanel, BorderLayout.CENTER);
        
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    
    public void Refresh(){
        tileid.setText(String.valueOf(this.selected));
        
        //Refresh this specific tyle
        int[][] tile = gpu.tilemap[this.selected];
        for(int y = 0; y < 8; y++){
            for(int x = 0; x < 8; x++){
                int c = tile[y][x];
                Color cl;
                switch(c){
                    case 0: 
                        cl = Color.WHITE;
                        break;
                    case 1: 
                        cl = Color.LIGHT_GRAY;
                        break;
                    case 2:
                        cl = Color.DARK_GRAY;
                        break;
                    default:
                        cl = Color.BLACK;
                        break;
                }
                bmp.SetColor(x, y, cl);
            }
        }
        
        //Refresh all tiles map
        int lx = 0; int ly = 0;
        for(int i = 0; i < 255; i++){
            int[][] tiledata = gpu.tilemap[base*255 + i];
            for(int y = 0; y < 8; y++){
                for(int x = 0; x < 8; x++){
                    int drawX = lx+x;
                    int drawY = ly+y;
                    int color = tiledata[y][x];
                    Color cl;
                    switch(color){
                        case 0: 
                            cl = Color.WHITE;
                            break;
                        case 1: 
                            cl = Color.LIGHT_GRAY;
                            break;
                        case 2:
                            cl = Color.DARK_GRAY;
                            break;
                        default:
                            cl = Color.BLACK;
                            break;
                    }
                    all.SetColor(drawX, drawY, cl);
                }
            }
            lx+=8;
            if(lx >= 15*8) {
                lx = 0;
                ly+=8;
            }
        }
        
        drawPanel.repaint();
        allPanel.repaint();
    }
    
}

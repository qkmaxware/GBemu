/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import gameboy.Gameboy;
import gameboy.gpu.Bitmap;
import gameboy.gpu.Gpu;
import gameboy.gpu.Sprite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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
    
    private JPanel drawPanel;
    
    private JTextField tileid;
    
    public TileViewer(Gameboy gb){
        super();
        
        this.gb = gb;
        this.gpu = gb.gpu;
        
        this.setTitle("Tiles");
        
        this.setLayout(new BorderLayout());
        
        this.setSize(400,340);
        
        JPanel header = new JPanel();
        this.add(header, BorderLayout.NORTH);
        
        JButton left = new JButton("<");
        left.addActionListener((evt) -> {
            selected --;
            if(selected < 0)
                selected = gpu.tilemap.length - 1;
            
            Refresh();
        });
        
        tileid = new JTextField(String.valueOf(this.selected));
        tileid.setPreferredSize(new Dimension(100, 32));
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
        this.add(center, BorderLayout.CENTER);
        center.draw = (g2) -> {
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2.drawImage(bmp.GetImage(), 0, 0, center.getWidth(), center.getHeight(), null);
        };
        
        JButton refresh = new JButton("Refresh");
        refresh.addActionListener((evt) -> {
            Refresh();
        });
        this.add(refresh, BorderLayout.SOUTH);
    }
    
    public void Refresh(){
        tileid.setText(String.valueOf(this.selected));
        
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
        
        drawPanel.repaint();
    }
    
}

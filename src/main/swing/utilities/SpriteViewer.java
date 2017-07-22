/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.swing.utilities;

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
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author Colin Halseth
 */
public class SpriteViewer extends JFrame{
    
    public interface Action{
        public void Invoke(Graphics2D g2);
    }
    
    private class DrawPanel extends JPanel{
        public Action draw;
        @Override
        public void paintComponent(Graphics g){ if(draw != null) draw.Invoke((Graphics2D)g); }
    }
    
    private Gpu gpu;
    private int selected =0;
    private Bitmap bmp = new Bitmap(8,8);
    
    private JPanel drawPanel;
    
    private JTextField spriteid;
    private JTextField x = new JTextField();
    private JTextField y = new JTextField();
    private JTextField prior = new JTextField();
    private JTextField xori = new JTextField();
    private JTextField yori = new JTextField();
    private JTextField pallet = new JTextField();
    
    public SpriteViewer(Gameboy gb){
        super();
        
        this.gpu = gb.gpu;
        
        this.setTitle("Sprites");
        
        this.setLayout(new BorderLayout());
        
        this.setSize(400,340);
        
        JPanel header = new JPanel();
        this.add(header, BorderLayout.NORTH);
        
        JButton left = new JButton("<");
        left.addActionListener((evt) -> {
            selected --;
            if(selected < 0)
                selected = 39;
            
            Refresh();
        });
        
        spriteid = new JTextField(String.valueOf(this.selected));
        spriteid.setPreferredSize(new Dimension(100, 32));
        spriteid.setEditable(false);
        
        JButton right = new JButton(">");
        right.addActionListener((evt) -> {
            selected ++;
            if(selected >= 40)
                selected = 0;
            
            Refresh();
        });
        
        header.add(left);
        header.add(spriteid);
        header.add(right);
        
        DrawPanel center = new DrawPanel();
        this.drawPanel = center;
        this.add(center, BorderLayout.CENTER);
        center.draw = (g2) -> {
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2.drawImage(bmp.GetImage(), 0, 0, center.getWidth(), center.getHeight(), null);
        };
        
        JPanel details = new JPanel();
        details.setLayout(new GridLayout(-1, 1));
        
        details.setPreferredSize(new Dimension(120, 0));
        
        x = new JTextField(); x.setEditable(false);
        y = new JTextField(); y.setEditable(false);
        prior = new JTextField(); prior.setEditable(false);
        xori = new JTextField(); xori.setEditable(false);
        yori = new JTextField(); yori.setEditable(false);
        pallet = new JTextField(); pallet.setEditable(false);
        
        details.add(new JLabel("X"));
        details.add(x);
        details.add(new JLabel("Y"));
        details.add(y);
        details.add(new JLabel("Priority"));
        details.add(prior);
        details.add(new JLabel("X Orientation"));
        details.add(xori);
        details.add(new JLabel("Y Orientation"));
        details.add(yori);
        details.add(new JLabel("Colour Pallet"));
        details.add(pallet);
        
        this.add(details, BorderLayout.WEST);
        
        JButton refresh = new JButton("Refresh");
        refresh.addActionListener((evt) -> {
            Refresh();
        });
        this.add(refresh, BorderLayout.SOUTH);
        
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    
    public void Refresh(){
        spriteid.setText(String.valueOf(this.selected));
        
        Sprite spr = gpu.oam_data[this.selected];
        x.setText(String.valueOf(spr.x));
        y.setText(String.valueOf(spr.y));
        prior.setText(String.valueOf(spr.priority));
        xori.setText(String.valueOf(spr.xflip));
        yori.setText(String.valueOf(spr.yflip));
        pallet.setText(String.valueOf(spr.objPalette));
        
        int[][] tile = gpu.tilemap[spr.tile];
        
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

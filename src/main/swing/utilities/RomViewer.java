/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.swing.utilities;

import gameboy.game.Cartridge;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author Colin Halseth
 */
public class RomViewer extends JFrame{
    
    public RomViewer(Cartridge cart){
        super();
        
        this.setSize(400, 320);
        this.setTitle("Cart Info: "+cart.toString());
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel content = new JPanel();
        content.setLayout(new BorderLayout());
        
        JTextArea text = new JTextArea();
        text.setEditable(false);
        text.setText(cart.header.toString());
        content.add(new JScrollPane(text), BorderLayout.CENTER);
        
        JPanel pic = new JPanel(){
            @Override
            public void paintComponent(Graphics g){
            
            }
        };
        pic.setPreferredSize(new Dimension(160,0));
        content.add(pic, BorderLayout.EAST);
        
        this.add(content);
        
    }
    
}

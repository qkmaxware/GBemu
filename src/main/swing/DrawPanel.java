/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.swing;

import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

/**
 *
 * @author Colin Halseth
 */
public class DrawPanel extends JPanel{
    public static interface Action{
        public void Invoke(Graphics2D g2);
    }
    
    public Action draw;
   
    @Override
    public void paintComponent(Graphics g){ if(draw != null) draw.Invoke((Graphics2D)g); }
}
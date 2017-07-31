/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.swing.utilities;

import gameboy.Gameboy;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author Colin Halseth
 */
public class StackViewer extends JFrame{
    
    private Gameboy gb;
    
    private JList list;
    private JLabel size;
    
    public StackViewer(Gameboy gb){
        super();
        
        this.setTitle("Stack");
        this.setSize(240, 240);
        
        this.gb = gb;
        this.list = new JList();

        JPanel content = new JPanel();
        content.setLayout(new BorderLayout());
        
        JButton refresh = new JButton("Refresh");
        refresh.addActionListener((evt) -> {
            Refresh();
        });
        
        this.size = new JLabel("Size: 0");
        
        content.add(new JScrollPane(this.list), BorderLayout.CENTER);
        content.add(refresh, BorderLayout.SOUTH);
        content.add(this.size, BorderLayout.NORTH);
        
        this.add(content);
    }
    
    public void Refresh(){
        int sp = gb.cpu.reg.sp();
        int stacksize = Math.max(0xFFFE - sp, 0) >> 1; //Each is one word
        Object[] stackValues = new Object[stacksize];
        if(stacksize > 0)
            for(int i = 0xFFFE - 2, j = 0; j < stackValues.length; i-=2, j++){
                stackValues[stackValues.length - 1 - j] = String.format("0x%04X", i)+" - "+String.format("%X", gb.mmu.rw(i));
            }
        
        list.setListData(stackValues);
        list.repaint();
        
        this.size.setText("Size: "+stacksize);
    }
    
}

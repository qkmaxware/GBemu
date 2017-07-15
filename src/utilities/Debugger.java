/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import gameboy.cpu.Cpu;
import gameboy.Gameboy;
import gameboy.MemoryMap;
import gameboy.cpu.Op;
import gameboy.cpu.Registry;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author Colin Halseth
 */
public class Debugger extends JFrame{
    
    private class Table<T>{
        private ArrayList<ArrayList<T>> rows = new ArrayList<ArrayList<T>>();
        private int columns;
        public Table(int columns){
            this.columns = columns;
        }
        public int Columns(){
            return columns;
        }
        public int Rows(){
            return rows.size();
        }
        public ArrayList<T> AddRow(){
            ArrayList<T> row = new ArrayList<T>(columns);
            for(int i = 0; i < columns; i++){
                row.add(null);
            }
            rows.add(row);
            return row;
        }
        public ArrayList<T> GetRow(int r){
            return rows.get(r);
        }
    }
    
    private Table<String> memoryTable = new Table<String>(2);
    private Table<String> registryTable = new Table<String>(2);
    private MemoryMap mmu;
    private Registry reg;
    private Cpu cpu;
    private TableModel memoryModel;
    private TableModel registryModel;
    private int displayMode = 0;
    private JTextArea logger;
    private JScrollPane logScroll;
    private JTable table;
    
    public Debugger(Gameboy gb){
        this.setLayout(new BorderLayout());
        this.mmu = gb.mmu;
        this.reg = gb.cpu.reg;
        this.cpu = gb.cpu;
        
        String[] MemoryColumnNames = new String[]{"Address", "Value"};
        memoryModel = new AbstractTableModel(){
            @Override
            public int getRowCount() {
                return memoryTable.Rows();
            }

            @Override
            public int getColumnCount() {
                return memoryTable.Columns();
            }
            
            @Override
            public Object getValueAt(int i, int i1) {
                ArrayList<String> row = memoryTable.GetRow(i);
                if(row.size() > i1)
                    return row.get(i1);
                return null;
            }
            
            @Override
            public String getColumnName(int col){
                return MemoryColumnNames[col];
            }
        };
        
        String[] RegistryColumnNames = new String[]{"Register", "Value"};
        registryModel = new AbstractTableModel(){
            @Override
            public int getRowCount() {
                return registryTable.Rows();
            }

            @Override
            public int getColumnCount() {
                return registryTable.Columns();
            }

            @Override
            public Object getValueAt(int i, int i1) {
                ArrayList<String> row = registryTable.GetRow(i);
                if(row.size() > i1)
                    return row.get(i1);
                return null;
            }
            
            @Override
            public String getColumnName(int col){
                return RegistryColumnNames[col];
            }
        };
        
        table = new JTable(memoryModel);
        this.add(new JScrollPane(table), BorderLayout.CENTER);
        
        JTable regTable = new JTable(registryModel);
        
        logger = new JTextArea();
        logger.setEditable(false);
        logScroll = new JScrollPane(logger);
        logScroll.setPreferredSize(new Dimension(300, 120));
        
        JPanel right = new JPanel();
        right.setLayout(new GridLayout(0,1));
        right.setPreferredSize(new Dimension(300, 120));
        right.add(regTable);
        right.add(logScroll);
        
        this.add(new JScrollPane(right), BorderLayout.EAST);
        
        JPanel panelheader = new JPanel();
        JPanel footer = new JPanel();
        
        ButtonGroup group = new ButtonGroup();
        JRadioButton decimal = new JRadioButton("decimal");
        decimal.addActionListener((evt) -> {displayMode = 0; Refresh();});
        decimal.setSelected(true);
        JRadioButton hex = new JRadioButton("hex");
        hex.addActionListener((evt) -> {displayMode = 1; Refresh();});
        JRadioButton opc = new JRadioButton("opcode");
        opc.addActionListener((evt) -> {displayMode = 2; Refresh();});
        
        group.add(decimal);
        group.add(hex);
        group.add(opc);
        
        JPanel displayMode = new JPanel();
        displayMode.add(decimal);
        displayMode.add(hex);
        displayMode.add(opc);
        
        JButton button = new JButton("Refresh");
        button.addActionListener((evt) -> {
            Refresh();
        });
        
        JButton button2 = new JButton("Step");
        button2.addActionListener((evt) -> {
            gb.Dispatch();
            Refresh();
            table.setRowSelectionInterval(reg.pc(), reg.pc());
        });
        
        JButton button6 = new JButton("Breakpoint");
        button6.addActionListener((evt) -> {
            try{
                String r = JOptionPane.showInputDialog(null, "Enter a break address");
                int addr = Integer.parseInt(r,16);
                while(true){
                    gb.Dispatch();
                    if(reg.pc() == addr)
                        break;
                }
                Refresh();
                table.setRowSelectionInterval(addr, addr);
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Bad address format");
            }
        });
        
        JButton button3 = new JButton("Inject");
        button3.addActionListener((evt) -> {
            JTextField loc = new JTextField();
            loc.setPreferredSize(new Dimension(120, 32));
            JTextField value = new JTextField();
            value.setPreferredSize(new Dimension(120, 32));
            
            ButtonGroup mode = new ButtonGroup();
            JRadioButton dmode = new JRadioButton("decimal");
            dmode.setSelected(true);
            JRadioButton hmode = new JRadioButton("hex");
            JRadioButton bmode = new JRadioButton("binary");
            mode.add(dmode);
            mode.add(hmode);
            mode.add(bmode);
            
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
            JPanel inner = new JPanel();
            inner.add(new JLabel("Addr:"));
            inner.add(loc);
            inner.add(new JLabel("V:"));
            inner.add(value);
            panel.add(inner, BorderLayout.CENTER);
            JPanel header = new JPanel();
            header.add(dmode);
            header.add(hmode);
            header.add(bmode);
            panel.add(header, BorderLayout.NORTH);
            
            int result = JOptionPane.showConfirmDialog(null, panel, 
               "Value Injection Parameters", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
               try{
                    String t = loc.getText();
                    int a = Integer.parseInt(t, 16);
                    int b = 0;
                    if(dmode.isSelected()){
                        b = Integer.parseInt(value.getText());
                    }else if(hmode.isSelected()){
                        b = Integer.parseInt(value.getText(), 16);
                    }else if(bmode.isSelected()){
                        b = Integer.parseInt(value.getText(), 2);
                    }
                   
                    System.out.println("Trying to put "+b+" into "+a);
                    mmu.wb(a, b);
                    int c = mmu.rb(a);
                    if(c != b)
                        System.out.println("Failed, unwritable");
                    Refresh();
               } catch(Exception e){
                   JOptionPane.showMessageDialog(null, "Failed to set value into desired address");
               }
            }
        });
        
        JButton button4 = new JButton("Run Op");
        button4.addActionListener((evt) -> {
            String r = JOptionPane.showInputDialog(null, "Select Opcode To Run");
            try{
                int i = Integer.parseInt(r);
                gb.cpu.opcodes.Fetch(i).Invoke();
                Refresh();
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Failed to execute opcode");
            }
        });
        
        JButton button5 = new JButton("Goto");
        button5.addActionListener((evt) -> {
            String r = JOptionPane.showInputDialog(null, "Select address to scroll to");
            try{
                int i = Integer.parseInt(r, 16);
                System.out.println("jump to "+i);
                Rectangle rect = table.getCellRect(i, 0, true);
                Point pt = ((JViewport)table.getParent()).getViewPosition();
                rect.setLocation(rect.x - pt.x, rect.y - pt.y);
                table.scrollRectToVisible(rect);
                table.setRowSelectionInterval(i, i);
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Bad address format");
            }
        });
         
        panelheader.add(displayMode);
        footer.add(button);
        footer.add(button5);
        footer.add(button2);
        footer.add(button6);
        footer.add(button3);
        footer.add(button4);
        
        this.add(footer, BorderLayout.SOUTH);
        this.add(panelheader, BorderLayout.NORTH);
        
        this.setTitle("Debugger");
    }
    
    public void Refresh(){
        int startId = table.getSelectionModel().getMinSelectionIndex();
        int endId = table.getSelectionModel().getMaxSelectionIndex();
        
        for(int i = 0; i <= mmu.MaxAddress(); i++){
            ArrayList<String> row;
            if(i >= memoryTable.Rows()){
                row = memoryTable.AddRow();
            }else{
                row = memoryTable.GetRow(i);
            }
            row.set(0, String.format("%04X", i));
            switch(displayMode){
                case 0:
                    row.set(1, String.valueOf(mmu.rb(i)));
                    break;
                case 1:
                    row.set(1, String.format("0x%04X", mmu.rb(i)));
                    break;
                case 2:
                    Op op = this.cpu.opcodes.Fetch(mmu.rb(i));
                    row.set(1, op!=null ? op.toString() : ""+mmu.rb(i));
                    break;
            }            
        }
        
        while(registryTable.Rows() < 10){
            registryTable.AddRow();
        }
        
        String smallformat = (displayMode == 0 ? "%d" : "%02X");
        String largeformat = (displayMode == 0 ? "%d" : "%04X");
        
        registryTable.GetRow(0).set(0, "PC");
        registryTable.GetRow(0).set(1, String.format(largeformat, reg.pc()));
        
        registryTable.GetRow(1).set(0, "SP");
        registryTable.GetRow(1).set(1, String.format(largeformat, reg.sp()));
        
        registryTable.GetRow(2).set(0, "A");
        registryTable.GetRow(2).set(1, String.format(smallformat,reg.a()));
        
        registryTable.GetRow(3).set(0, "B");
        registryTable.GetRow(3).set(1, String.format(smallformat,reg.b()));
        
        registryTable.GetRow(4).set(0, "C");
        registryTable.GetRow(4).set(1, String.format(smallformat,reg.c()));
        
        registryTable.GetRow(5).set(0, "D");
        registryTable.GetRow(5).set(1, String.format(smallformat,reg.d()));
        
        registryTable.GetRow(6).set(0, "E");
        registryTable.GetRow(6).set(1, String.format(smallformat,reg.e()));
        
        registryTable.GetRow(7).set(0, "H");
        registryTable.GetRow(7).set(1, String.format(smallformat,reg.h()));
        
        registryTable.GetRow(8).set(0, "L");
        registryTable.GetRow(8).set(1, String.format(smallformat, reg.l()));
        
        registryTable.GetRow(9).set(0, "Flags");
        registryTable.GetRow(9).set(1, String.format(smallformat,reg.f()));
        
        ((AbstractTableModel)memoryModel).fireTableDataChanged();
        ((AbstractTableModel)registryModel).fireTableDataChanged();
        
        logger.setText(String.join("\n", cpu.recentOps));
        this.logScroll.getVerticalScrollBar().setValue(0); //Stay at the top
        
        this.repaint();
        
        //Preserve selection
        if(startId != -1 && endId != -1){
            table.setRowSelectionInterval(startId, endId);
        }
    }
    
}

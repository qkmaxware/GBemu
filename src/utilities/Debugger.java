/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import gameboy.Cpu;
import gameboy.Gameboy;
import gameboy.MemoryMap;
import gameboy.Registry;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
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
    private TableModel memoryModel;
    private TableModel registryModel;
    private int displayMode = 0;
    
    public Debugger(Gameboy gb){
        this.setLayout(new BorderLayout());
        this.mmu = gb.mmu;
        this.reg = gb.cpu.reg;
        
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
        
        JTable table = new JTable(memoryModel);
        this.add(new JScrollPane(table), BorderLayout.CENTER);
        
        JTable regTable = new JTable(registryModel);
        
        JTextArea logger = new JTextArea();
        JScrollPane logScroll = new JScrollPane(logger);
        logScroll.setPreferredSize(new Dimension(300, 120));
        
        JPanel right = new JPanel();
        right.setLayout(new GridLayout(0,1));
        right.setPreferredSize(new Dimension(300, 120));
        right.add(regTable);
        right.add(logScroll);
        
        this.add(new JScrollPane(right), BorderLayout.EAST);
        
        JPanel footer = new JPanel();
        
        ButtonGroup group = new ButtonGroup();
        JRadioButton decimal = new JRadioButton("decimal");
        decimal.addActionListener((evt) -> {displayMode = 0;});
        decimal.setSelected(true);
        JRadioButton hex = new JRadioButton("hex");
        hex.addActionListener((evt) -> {displayMode = 1;});
        
        group.add(decimal);
        group.add(hex);
        
        JPanel displayMode = new JPanel();
        displayMode.add(decimal);
        displayMode.add(hex);
        
        JButton button = new JButton("Refresh");
        button.addActionListener((evt) -> {
            Refresh();
        });
        
        JButton button2 = new JButton("Step");
        button2.addActionListener((evt) -> {
            
        });
        
        JButton button3 = new JButton("Inject");
        button3.addActionListener((evt) -> {
            JTextField loc = new JTextField();
            loc.setPreferredSize(new Dimension(120, 32));
            JTextField value = new JTextField();
            value.setPreferredSize(new Dimension(120, 32));
            
            JPanel panel = new JPanel();
            panel.add(new JLabel("Addr:"));
            panel.add(loc);
            panel.add(new JLabel("V:"));
            panel.add(value);
            
            int result = JOptionPane.showConfirmDialog(null, panel, 
               "Value Injection Parameters", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
               try{
                    String t = loc.getText();
                    int a;
                    if(t.matches("-?[0-9a-fA-F]+")){
                        a = Integer.parseInt(t, 16);
                    }else{
                        a= Integer.parseInt(t);
                    }
                    int b = Integer.parseInt(value.getText());
                    
                    mmu.wb(a, b);
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
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Failed to execute opcode");
            }
        });
        
        
        footer.add(displayMode);
        footer.add(button);
        footer.add(button2);
        footer.add(button3);
        footer.add(button4);
        
        this.add(footer, BorderLayout.SOUTH);
        
        this.setTitle("Debugger");
    }
    
    public void Refresh(){
        for(int i = 0; i < mmu.MaxAddress(); i++){
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
            }            
        }
        
        while(registryTable.Rows() < 10){
            registryTable.AddRow();
        }
        registryTable.GetRow(0).set(0, "PC");
        registryTable.GetRow(0).set(1, String.valueOf(reg.pc()));
        
        registryTable.GetRow(1).set(0, "SP");
        registryTable.GetRow(1).set(1, String.valueOf(reg.sp()));
        
        registryTable.GetRow(2).set(0, "A");
        registryTable.GetRow(2).set(1, String.valueOf(reg.a()));
        
        registryTable.GetRow(3).set(0, "B");
        registryTable.GetRow(3).set(1, String.valueOf(reg.b()));
        
        registryTable.GetRow(4).set(0, "C");
        registryTable.GetRow(4).set(1, String.valueOf(reg.a()));
        
        registryTable.GetRow(5).set(0, "D");
        registryTable.GetRow(5).set(1, String.valueOf(reg.d()));
        
        registryTable.GetRow(6).set(0, "E");
        registryTable.GetRow(6).set(1, String.valueOf(reg.e()));
        
        registryTable.GetRow(7).set(0, "H");
        registryTable.GetRow(7).set(1, String.valueOf(reg.h()));
        
        registryTable.GetRow(8).set(0, "L");
        registryTable.GetRow(8).set(1, String.valueOf(reg.a()));
        
        registryTable.GetRow(9).set(0, "Flags");
        registryTable.GetRow(9).set(1, String.valueOf(reg.f()));
        
        ((AbstractTableModel)memoryModel).fireTableDataChanged();
        ((AbstractTableModel)registryModel).fireTableDataChanged();
        this.repaint();
    }
    
}

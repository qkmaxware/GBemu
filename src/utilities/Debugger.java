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
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
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
    private Table<String> registryTable = new Table<String>(4);
    private MemoryMap mmu;
    private Registry reg;
    private Cpu cpu;
    private TableModel memoryModel;
    private TableModel registryModel;
    private int displayMode = 0;
    private JTextArea logger;
    private JScrollPane logScroll;
    private JTable table;
    
    private ArrayList<Integer> breakpoints = new ArrayList<Integer>();
    
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
        
        String[] RegistryColumnNames = new String[]{"Register", "Value", "Register", "Value"};
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
        
        DefaultTableCellRenderer  bold = new DefaultTableCellRenderer (){
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                if (isSelected) {
                    setBackground(table.getSelectionBackground());
                    setForeground(table.getSelectionForeground());
                }
                else {
                    setBackground(table.getBackground());
                    setForeground(table.getForeground());
                }
                this.setValue(table.getValueAt(row, column));
                this.setFont(this.getFont().deriveFont(Font.BOLD));
                return this;
            }
        };
        
        final Color breakpointColor = Color.RED;
        final Color breakpointSelectedColor = new Color(255, 0, 255);
        
        DefaultTableCellRenderer  boldAndBreakpoint = new DefaultTableCellRenderer (){
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                
                if (isSelected) {
                    if(breakpoints.contains(row)){
                        setBackground(breakpointSelectedColor);
                    }else{
                        setBackground(table.getSelectionBackground());
                    }
                    setForeground(table.getSelectionForeground());
                }
                else {
                    if(breakpoints.contains(row)){
                        setBackground(breakpointColor);
                    }else{
                        setBackground(table.getBackground());
                    }
                    setForeground(table.getForeground());
                }
                this.setValue(table.getValueAt(row, column));
                this.setFont(this.getFont().deriveFont(Font.BOLD));
                return this;
            }
        };
        
        table = new JTable(memoryModel);
        table.getColumnModel().getColumn(0).setCellRenderer(boldAndBreakpoint);
        
        table.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseReleased(MouseEvent e) {
                int rowindex = table.rowAtPoint(e.getPoint());
                if (rowindex < 0)
                    return;
                if (e.isPopupTrigger() && e.getComponent() instanceof JTable ) {
                    JPopupMenu popup = new JPopupMenu();
                    JMenuItem breakpoint;
                    if(breakpoints.contains(rowindex)){
                        breakpoint = new JMenuItem("Remove Breakpoint");
                        breakpoint.addActionListener((evt) -> {
                            breakpoints.remove((Integer)rowindex);
                            Refresh();
                        });
                    }else{
                        breakpoint = new JMenuItem("Add Breakpoint");
                        breakpoint.addActionListener((evt) -> {
                            breakpoints.add(rowindex);
                            Refresh();
                        });
                    }
                    popup.add(breakpoint);
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        
        
        this.add(new JScrollPane(table), BorderLayout.CENTER);
        
        JTable regTable = new JTable(registryModel);

        regTable.getColumnModel().getColumn(0).setCellRenderer(bold);
        regTable.getColumnModel().getColumn(2).setCellRenderer(bold);
        JScrollPane regScroll = new JScrollPane(regTable);
        regScroll.setPreferredSize(new Dimension(300, 120));
        
        logger = new JTextArea();
        logger.setEditable(false);
        logScroll = new JScrollPane(logger);
        logScroll.setPreferredSize(new Dimension(300, 120));
        
        JPanel right = new JPanel();
        right.setLayout(new GridLayout(0,1));
        right.setPreferredSize(new Dimension(300, 120));
        right.add(regScroll);
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
            jumpToRow(reg.pc());
            table.setRowSelectionInterval(reg.pc(), reg.pc());
        });
        
        JButton button6 = new JButton("Breakpoint Step");
        button6.addActionListener((evt) -> {
            //Run action in a whole new thread
            Thread t = new Thread(){
                @Override
                public void run(){
                    try{
                        while(true){
                            gb.Dispatch();
                            if(breakpoints.contains(reg.pc()))
                                break;
                            //Thread.sleep(1);
                        }
                        Refresh();
                        jumpToRow(reg.pc());
                        table.setRowSelectionInterval(reg.pc(), reg.pc());
                    }catch(Exception e){
                        JOptionPane.showMessageDialog(null, e);
                        e.printStackTrace();
                    }
                }
            };
            t.start();
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
                jumpToRow(i);
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Bad address format");
            }
        });
        
        JButton button7 = new JButton("Goto PC");
        button7.addActionListener((evt) -> {
            jumpToRow(reg.pc());
        });
         
        panelheader.add(displayMode);
        footer.add(button);
        footer.add(button7);
        footer.add(button5);
        footer.add(button2);
        footer.add(button6);
        footer.add(button3);
        footer.add(button4);
        
        this.add(footer, BorderLayout.SOUTH);
        this.add(panelheader, BorderLayout.NORTH);
        
        this.setTitle("Debugger");
    }
    
    public void jumpToRow(int i){
        Rectangle rect = table.getCellRect(i, 0, true);
        Point pt = ((JViewport)table.getParent()).getViewPosition();
        rect.setLocation(rect.x - pt.x, rect.y - pt.y);
        ((JViewport)table.getParent()).scrollRectToVisible(rect);
        table.setRowSelectionInterval(i, i);
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
                    //For multi-byte opcodes
                    int n = 0; int nn = 0;
                    if(i <= mmu.MaxAddress() - 1)
                        n = mmu.rb(i+1);
                    if(i <= mmu.MaxAddress() - 2)
                        nn = (mmu.rb(i+2) << 8) | mmu.rb(i+1);
                    
                    String str;
                    if(op != null){
                        str = op.toString();
                        str = str.replace("nn", String.format("%X", nn));
                        str = str.replace("n", String.format("%X", n));
                    }else{
                        str = ""+mmu.rb(i);
                    }
                    
                    row.set(1, str);
                    break;
            }            
        }
        
        while(registryTable.Rows() < 14){
            registryTable.AddRow();
        }
        
        String smallformat = (displayMode == 0 ? "%d" : "%02X");
        String largeformat = (displayMode == 0 ? "%d" : "%04X");
        
        registryTable.GetRow(0).set(0, "PC");
        registryTable.GetRow(0).set(1, String.format(largeformat, reg.pc()));
        
        registryTable.GetRow(0).set(2, "SP");
        registryTable.GetRow(0).set(3, String.format(largeformat, reg.sp()));
        
        registryTable.GetRow(1).set(0, "A");
        registryTable.GetRow(1).set(1, String.format(smallformat,reg.a()));
        
        registryTable.GetRow(1).set(2, "F (flags)");
        registryTable.GetRow(1).set(3, String.format(smallformat,reg.f()));
        
        registryTable.GetRow(2).set(0, "B");
        registryTable.GetRow(2).set(1, String.format(smallformat,reg.b()));
        
        registryTable.GetRow(2).set(2, "C");
        registryTable.GetRow(2).set(3, String.format(smallformat,reg.c()));
        
        registryTable.GetRow(3).set(0, "D");
        registryTable.GetRow(3).set(1, String.format(smallformat,reg.d()));
        
        registryTable.GetRow(3).set(2, "E");
        registryTable.GetRow(3).set(3, String.format(smallformat,reg.e()));
        
        registryTable.GetRow(4).set(0, "H");
        registryTable.GetRow(4).set(1, String.format(smallformat,reg.h()));
        
        registryTable.GetRow(4).set(2, "L");
        registryTable.GetRow(4).set(3, String.format(smallformat, reg.l()));
        
        //Spacer
        registryTable.GetRow(5).set(0, "");
        registryTable.GetRow(5).set(1, "");
        
        registryTable.GetRow(6).set(0, "IME");
        registryTable.GetRow(6).set(1, String.format(smallformat, reg.ime()));
        
        registryTable.GetRow(6).set(2, "LCDC");
        registryTable.GetRow(6).set(3, String.format(smallformat, mmu.rb(0xFF40)));
        
        registryTable.GetRow(7).set(0, "IE");
        registryTable.GetRow(7).set(1, String.format(smallformat, mmu.rb(0xFFFF)));
        
        registryTable.GetRow(7).set(2, "LCD STAT");
        registryTable.GetRow(7).set(3, String.format(smallformat, mmu.rb(0xFF41)));
        
        registryTable.GetRow(8).set(0, "IF");
        registryTable.GetRow(8).set(1, String.format(smallformat, mmu.rb(0xFF0F)));
        
        registryTable.GetRow(8).set(2, "LY");
        registryTable.GetRow(8).set(3, String.format(smallformat, mmu.rb(0xFF44)));
        
        
        
        ((AbstractTableModel)memoryModel).fireTableDataChanged();
        ((AbstractTableModel)registryModel).fireTableDataChanged();
        
        logger.setText(String.join("\n", cpu.recentOps));
        this.logScroll.getVerticalScrollBar().setValue(1); //Stay at the top
        
        this.repaint();
        
        //Preserve selection
        if(startId != -1 && endId != -1){
            table.setRowSelectionInterval(startId, endId);
        }
    }
    
}

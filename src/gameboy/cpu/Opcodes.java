/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameboy.cpu;

import gameboy.MemoryMap;
import gameboy.Metrics;
import java.util.ArrayList;

/**
 *
 * @author Colin
 */
public class Opcodes {
    
    private Registry reg;
    private Registry cpp;
    private Clock clock;
    private MemoryMap mmu;
    
    private Op[] map = new Op[256];
    private Op[] cbmap = new Op[256];
    
    public Opcodes(Cpu cpu, MemoryMap mmu){
        this.reg = cpu.reg;
        this.cpp = cpu.cpp;
        this.clock = cpu.clock;
        this.mmu = mmu;
        
        ArrayList<Integer> count = new ArrayList<Integer>(); 
        ArrayList<Integer> ccount = new ArrayList<Integer>(); 
        for(int i = 0 ; i < map.length; i++){
            if(map[i] == null){
                count.add(i);
                Op XX = new Op(i, "XX", null, () -> {
                    System.out.println( "Unimplemented opcode called at "+(reg.pc() - 1)+" with opcode "+mmu.rb((reg.pc() - 1)));
                    clock.m(1);
                    clock.t(4);
                });
                map[i] = XX;
            }
            if(cbmap[i] == null){
                ccount.add(i);
                Op XX = new Op(i, "XX", null, () -> {
                    System.out.println("Unimplemented cb-opcode called at "+(reg.pc() - 1)+" with opcode "+mmu.rb((reg.pc() - 1)));
                    clock.m(1);
                    clock.t(4);
                });
                cbmap[i] = XX;
            }
        }
        
        System.out.println(count.size()+" base opcodes undefined and "+ccount.size()+" CB opcodes undefined");
    }
    
    public boolean isValidOpcode(int opcode){
        if(this.map == null){
            return false;
        }
        if(opcode < 0 || opcode >= this.map.length){
            return false;
        }
        return true;
    }
    
    public Op Fetch(int opcode){
        if(!isValidOpcode(opcode)){
            throw new RuntimeException("Opcode is not valid at: "+reg.pc());
        }
        Op op = this.map[opcode];
        return op; 
    }
    
    public Op Decode(int opcode){
        if(!isValidOpcode(opcode)){
            return null;
        }
        Op op = this.map[opcode];
        return op; 
    }
    
    //--------------------------------------------------------------------------
    // Helper functions
    //--------------------------------------------------------------------------
    
    private boolean isHalfCarry(int a, int b){
        //Borrow
        if(b < 0){
            return (((a & 0xF) - (Math.abs(b) & 0xF)) < 0);
        }
        //Carry
        return (((a & 0xF) + (b & 0xF))) > 0xF;
    }
    
    private boolean isHalfCarry16(int a, int b){
        //Borrow
        if(b < 0){
            return (((a & 0xFFF) - (Math.abs(b) & 0xFFF)) < 0);
        }
        //Carry
        return(((a & 0xFFF) + (b & 0xFFF))) > 0x0FFF;
    }
    
    private boolean isCarry16(int i){
        return i > 0xFFFF || i < 0;
    }
    
    private boolean isCarry(int i){
        return i > 0xFF || i < 0;
    }
    
    private boolean isZero(int i, int max){
        return (i & max) == 0;
    }
    
    private boolean isZero(int i){
        return isZero(i, Metrics.BIT8);
    }
    
    //Compare two values and set the flags accordingly
    public void compare(int a, int b){
        int c = a - b;
        
        reg.zero(isZero(c));                //If result is zero, set the zero flag
        reg.subtract(true);                 //Subtract is always set to true
        reg.halfcarry(isHalfCarry(a, -b));  //Set the half carry flag
        reg.carry(c < 0);                   //Set the carry flag if c < 0
    }
    
    //Check if a particular bit is set
    public void testBit(int v, int bit){
        int mask = 1 << bit;
        //Set is bit 'b' of register r is 0;
        reg.zero((v & mask) == 0);
        reg.subtract(false);
        reg.halfcarry(true);
    }
    
    //Set a bit to 0
    public int resetBit(int i, int bit){
        int mask = ~(1 << bit);
        return i & mask;
    }
    
    //Set a bit to 1
    public int setBit(int i, int bit){
        int mask = 1 << bit;
        return i | mask;
    }
    
    //Rotate right 9Bit with carry
    public int rotateRight(int value){
        //9Bit rotation to the right, the carry is copied into bit 7
        boolean carry = reg.carry();
        boolean leaving = (value & 0x1) != 0;
        int shift = ((value >> 1) | (carry ? 0x80 : 0)) & 0xFF;
        
        reg.carry(leaving);
        reg.subtract(false);
        reg.halfcarry(false);
        reg.zero(isZero(shift));
        
        return shift;
    }
    
    //Rotate left 9Bit with carry
    public int rotateLeft(int value){
        //9Bit rotation to the right, the carry is copied into bit 7
        boolean carry = reg.carry();
        boolean leaving = (value & 0x80) != 0;
        int shift = ((value << 1) | (carry ? 0x1 : 0)) & 0xFF;
        
        reg.carry(leaving);
        reg.subtract(false);
        reg.halfcarry(false);
        reg.zero(isZero(shift));
        
        return shift;
    }
    
    //Rotate right 8Bit without carry
    public int rotateRightCarry(int value){
        boolean leaving = (value & 0x1) != 0;
        int shift = ((value >> 1) | (leaving ? 0x80 : 0)) & 0xFF;
        
        reg.zero(isZero(shift));
        reg.subtract(false);
        reg.halfcarry(false);
        reg.carry(leaving);
        
        return shift;
    }
    
    //Rotate left 8Bit without carry
    public int rotateLeftCarry(int value){
        boolean leaving = (value & 0x80) != 0;
        int shift = ((value << 1) | (leaving ? 1 : 0)) & 0xFF;
        
        reg.zero(isZero(shift));
        reg.subtract(false);
        reg.halfcarry(false);
        reg.carry(leaving);
        
        return shift;
    }
    
    //Shift to the Left filling 0's in the lower bit
    public int shiftLeft(int value){
        int shift = (value << 1) & 0xFF;
        boolean bit7 = (value & 0x80) != 0;
        
        reg.zero(isZero(shift));
        reg.subtract(false);
        reg.halfcarry(false);
        reg.carry(bit7);
        
        return shift;
    }
    
    //Shift to the right, 0's in the upper bit
    public int shiftRight0(int value){
        int shift = (value >> 1) & 0b01111111;
        boolean bit0 = (value & 0x1) != 0;
        
        reg.zero(isZero(shift));
        reg.subtract(false);
        reg.halfcarry(false);
        reg.carry(bit0);
    
        return shift;
    }
    
    //Shift to the right, bit 7 preserved
    public int shiftRightExtend(int value){
        int shift = (value >> 1) & 0xFF;
        boolean bit0 = (value & 0x1) != 0;
        int bit7 = value & 0x80;    //Preserve bit 7 value
        shift = shift | bit7;
        
        reg.zero(isZero(shift));
        reg.subtract(false);
        reg.halfcarry(false);
        reg.carry(bit0);
    
        return shift;
    }
    
    public int signedByteToUnsigned(int s8){
        if(s8 < 0)
            s8 = 256 + s8;
        return s8;
    }
    
    public int unsignedByteToSigned(int u8){
        if(u8 > 127)
            u8 = -((~u8+1)&255);
        return u8;
    }
    
    private int addCarry8(int a, int b){
        int r = (a & 0xF) + (b & 0xF) + (reg.carry() ? 1 : 0);
        int r2 = a + b + (reg.carry() ? 1 : 0);
        
        reg.zero(isZero(r2));
        reg.subtract(false);
        reg.halfcarry(r > 0xF);
        reg.carry(r2 > 0xFF);
        
        return r2;
    }
    
    private int subCarry8(int a, int b){
        int r2 = (a) - (b) - (reg.carry() ? 1 : 0);
        
        reg.zero(isZero(r2));
        reg.subtract(true);
        reg.halfcarry(((r2 ^ b ^ a) & 0x10) != 0);
        reg.carry(r2 > 0xFF || r2 < 0);
        
        return r2;
    }
    
    private void push(int a){
        reg.sppp(-1);
        mmu.wb(reg.sp(), a >> 8);
        reg.sppp(-1);
        mmu.wb(reg.sp(), a & 0xFF);
    }
    
    private int pop(){
        int l = mmu.rb(reg.sp());
        reg.sppp(1);
        int h = mmu.rb(reg.sp());
        reg.sppp(1);
        return h << 8 | l;
    }
    
    //--------------------------------------------------------------------------
    // List of all OPCODES
    //--------------------------------------------------------------------------
    
    //No operation
    Op NOP = new Op(0x00, "NOP", map, () -> {
        clock.m(1); //Actual machine time
        clock.t(4); //Number of cycles taken
    });

    ///
    // 8 Bit Loads
    ///
    
    //Load an 8bit immediate value into registry B
    Op LD_B_n = new Op(0x06, "LD B,n", map, () -> {
        int v = mmu.rb(reg.pc());
        reg.b(v);
        reg.pcpp(1);
        clock.m(2);
        clock.t(8);
    });
    
    //Load an 8bit immediate value into registry C
    Op LD_C_n = new Op(0x0E, "LD C,n", map, () -> {
        int v = mmu.rb(reg.pc());
        reg.c(v);
        reg.pcpp(1);
        clock.m(2);
        clock.t(8);
    });
    
    //Load an 8bit immediate value into registry D
    Op LD_D_n = new Op(0x16, "LD D,n", map, () -> {
        int v = mmu.rb(reg.pc());
        reg.d(v);
        reg.pcpp(1);
        clock.m(2);
        clock.t(8);
    });
    
    //Load an 8bit immediate value into registry E
    Op LD_E_n = new Op(0x1E, "LD E,n", map, () -> {
        int v = mmu.rb(reg.pc());
        reg.e(v);
        reg.pcpp(1);
        clock.m(2);
        clock.t(8);
    });
    
    //Load an 8bit immediate value into registry H
    Op LD_H_n = new Op(0x26, "LD H,n", map, () -> {
        int v = mmu.rb(reg.pc());
        reg.h(v);
        reg.pcpp(1);
        clock.m(2);
        clock.t(8);
    });
    
    //Load an 8bit immediate value into registry H
    Op LD_L_n = new Op(0x2E, "LD L,n", map, () -> {
        int v = mmu.rb(reg.pc());
        reg.l(v);
        reg.pcpp(1);
        clock.m(2);
        clock.t(8);
    });
    
    //Load into register A from register A
    Op LD_rr_aa = new Op(0x7F, "LD A,A", map, () -> {
        int v = reg.a();
        reg.a(v);
        clock.m(1);
        clock.t(4);
    });
    
    //Load into register A from register B
    Op LD_rr_ab = new Op(0x78, "LD A,B", map, () -> {
        int v = reg.b();
        reg.a(v);
        clock.m(1);
        clock.t(4);
    });
    
    //Load into register A from register C
    Op LD_rr_ac = new Op(0x79, "LD A,C", map, () -> {
        int v = reg.c();
        reg.a(v);
        clock.m(1);
        clock.t(4);
    });
    
    //Load into register A from register D
    Op LD_rr_ad = new Op(0x7A, "LD A,D", map, () -> {
        int v = reg.d();
        reg.a(v);
        clock.m(1);
        clock.t(4);
    });
    
    //Load into register A from register E
    Op LD_rr_ae = new Op(0x7B, "LD A,E", map, () -> {
        int v = reg.e();
        reg.a(v);
        clock.m(1);
        clock.t(4);
    });
    
    //Load into register A from register H
    Op LD_rr_ah = new Op(0x7C, "LD A,H", map, () -> {
        int v = reg.h();
        reg.a(v);
        clock.m(1);
        clock.t(4);
    });
    
    //Load into register A from register L
    Op LD_rr_al = new Op(0x7D, "LD A,L", map, () -> {
        int v = reg.l();
        reg.a(v);
        clock.m(1);
        clock.t(4);
    });
    
    //Load into register A from memory HL
    Op LD_rr_ahl = new Op(0x7E, "LD A,(HL)", map, () -> {
        int v = mmu.rb(reg.hl());
        reg.a(v);
        clock.m(2);
        clock.t(8);
    });
    
    //Load into register B from register B
    Op LD_rr_bb = new Op(0x40, "LD B,B", map, () -> {
        int v = reg.b();
        reg.b(v);
        clock.m(1);
        clock.t(4);
    });
    
    //Load into register B from register C
    Op LD_rr_bc = new Op(0x41, "LD B,C", map, () -> {
        int v = reg.c();
        reg.b(v);
        clock.m(1);
        clock.t(4);
    });
    
    //Load into register B from register D
    Op LD_rr_bd = new Op(0x42, "LD B,D", map, () -> {
        int v = reg.d();
        reg.b(v);
        clock.m(1);
        clock.t(4);
    });
    
    //Load into register B from register E
    Op LD_rr_be = new Op(0x43, "LD B,E", map, () -> {
        int v = reg.e();
        reg.b(v);
        clock.m(1);
        clock.t(4);
    });
    
    //Load into register B from register H
    Op LD_rr_bh = new Op(0x44, "LD B,H", map, () -> {
        int v = reg.h();
        reg.b(v);
        clock.m(1);
        clock.t(4);
    });
    
    //Load into register B from register L
    Op LD_rr_bl = new Op(0x45, "LD B,L", map, () -> {
        int v = reg.l();
        reg.b(v);
        clock.m(1);
        clock.t(4);
    });
    
    //Load into register B from memory HL
    Op LD_rr_bhl = new Op(0x46, "LD B,(HL)", map, () -> {
        int v = mmu.rb(reg.hl());
        reg.b(v);
        clock.m(2);
        clock.t(8);
    });
    
    //Load into register C from register B
    Op LD_rr_cb = new Op(0x48, "LD C,B", map, () -> {
        int v = reg.b();
        reg.c(v);
        clock.m(1);
        clock.t(4);
    });
    
    //Load into register C from register C
    Op LD_rr_cc = new Op(0x49, "LD C,C", map, () -> {
        int v = reg.c();
        reg.c(v);
        clock.m(1);
        clock.t(4);
    });
    
    //Load into register C from register D
    Op LD_rr_cd = new Op(0x4A, "LD C,D", map, () -> {
        int v = reg.d();
        reg.c(v);
        clock.m(1);
        clock.t(4);
    });
    
    //Load into register C from register E
    Op LD_rr_ce = new Op(0x4B, "LD C,E", map, () -> {
        int v = reg.e();
        reg.c(v);
        clock.m(1);
        clock.t(4);
    });
    
    //Load into register C from register H
    Op LD_rr_ch = new Op(0x4C, "LD C,H", map, () -> {
        int v = reg.h();
        reg.c(v);
        clock.m(1);
        clock.t(4);
    });
    
    //Load into register C from register L
    Op LD_rr_cl = new Op(0x4D, "LD C,L", map, () -> {
        int v = reg.l();
        reg.c(v);
        clock.m(1);
        clock.t(4);
    });
    
    //Load into register C from memory HL
    Op LD_rr_chl = new Op(0x4E, "LD C,(HL)", map, () -> {
        int v = mmu.rb(reg.hl());
        reg.c(v);
        clock.m(2);
        clock.t(8);
    });
    
    //Load into register D from register B
    Op LD_rr_db = new Op(0x50, "LD D,B", map, () -> {
        int v = reg.b();
        reg.d(v);
        clock.m(1);
        clock.t(4);
    });
    
    //Load into register D from register C
    Op LD_rr_dc = new Op(0x51, "LD D,C", map, () -> {
        int v = reg.c();
        reg.d(v);
        clock.m(1);
        clock.t(4);
    });
    
    //Load into register D from register D
    Op LD_rr_dd = new Op(0x52, "LD D,D", map, () -> {
        int v = reg.d();
        reg.d(v);
        clock.m(1);
        clock.t(4);
    });
    
    //Load into register D from register E
    Op LD_rr_de = new Op(0x53, "LD D,E", map, () -> {
        int v = reg.e();
        reg.d(v);
        clock.m(1);
        clock.t(4);
    });
    
    //Load into register D from register H
    Op LD_rr_dh = new Op(0x54, "LD D,H", map, () -> {
        int v = reg.h();
        reg.d(v);
        clock.m(1);
        clock.t(4);
    });
    
    //Load into register D from register L
    Op LD_rr_dl = new Op(0x55, "LD D,L", map, () -> {
        int v = reg.l();
        reg.d(v);
        clock.m(1);
        clock.t(4);
    });
    
    //Load into register D from memory HL
    Op LD_rr_dhl = new Op(0x56, "LD D,(HL)", map, () -> {
        int v = mmu.rb(reg.hl());
        reg.d(v);
        clock.m(2);
        clock.t(8);
    });
    
    //Load into register E from register B
    Op LD_rr_eb = new Op(0x58, "LD E,B", map, () -> {
        int v = reg.b();
        reg.e(v);
        clock.m(1);
        clock.t(4);
    });
    
    //Load into register E from register C
    Op LD_rr_ec = new Op(0x59, "LD E,C", map, () -> {
        int v = reg.c();
        reg.e(v);
        clock.m(1);
        clock.t(4);
    });
    
    //Load into register E from register D
    Op LD_rr_ed = new Op(0x5A, "LD E,D", map, () -> {
        int v = reg.d();
        reg.e(v);
        clock.m(1);
        clock.t(4);
    });
    
    //Load into register E from register E
    Op LD_rr_ee = new Op(0x5B, "LD E,E", map, () -> {
        int v = reg.e();
        reg.e(v);
        clock.m(1);
        clock.t(4);
    });
    
    //Load into register E from register H
    Op LD_rr_eh = new Op(0x5C, "LD E,H", map, () -> {
        int v = reg.h();
        reg.e(v);
        clock.m(1);
        clock.t(4);
    });
    
    //Load into register E from register L
    Op LD_rr_el = new Op(0x5D, "LD E,L", map, () -> {
        int v = reg.l();
        reg.e(v);
        clock.m(1);
        clock.t(4);
    });
    
    //Load into register E from memory HL
    Op LD_rr_ehl = new Op(0x5E, "LD E,(HL)", map, () -> {
        int v = mmu.rb(reg.hl());
        reg.e(v);
        clock.m(2);
        clock.t(8);
    });
    
    //Load into register H from register B
    Op LD_rr_hb = new Op(0x60, "LD H,B", map, () -> {
        int v = reg.b();
        reg.h(v);
        clock.m(1);
        clock.t(4);
    });
    
    //Load into register H from register C
    Op LD_rr_hc = new Op(0x61, "LD H,C", map, () -> {
        int v = reg.c();
        reg.h(v);
        clock.m(1);
        clock.t(4);
    });
    
    //Load into register H from register D
    Op LD_rr_hd = new Op(0x62, "LD H,D", map, () -> {
        int v = reg.d();
        reg.h(v);
        clock.m(1);
        clock.t(4);
    });
    
    //Load into register H from register E
    Op LD_rr_he = new Op(0x63, "LD H,E", map, () -> {
        int v = reg.e();
        reg.h(v);
        clock.m(1);
        clock.t(4);
    });
    
    //Load into register H from register H
    Op LD_rr_hh = new Op(0x64, "LD H,H", map, () -> {
        int v = reg.h();
        reg.h(v);
        clock.m(1);
        clock.t(4);
    });
    
    //Load into register H from register L
    Op LD_rr_hl = new Op(0x65, "LD H,L", map, () -> {
        int v = reg.l();
        reg.h(v);
        clock.m(1);
        clock.t(4);
    });
    
    //Load into register H from register HL
    Op LD_rr_hhl = new Op(0x66, "LD H,(HL)", map, () -> {
        int v = mmu.rb(reg.hl());
        reg.h(v);
        clock.m(2);
        clock.t(8);
    });
    
    //Load into register L from register B
    Op LD_rr_lb = new Op(0x68, "LD L,B", map, () -> {
        int v = reg.b();
        reg.l(v);
        clock.m(1);
        clock.t(4);
    });
    
    //Load into register L from register C
    Op LD_rr_lc = new Op(0x69, "LD L,C", map, () -> {
        int v = reg.c();
        reg.l(v);
        clock.m(1);
        clock.t(4);
    });
    
    //Load into register L from register D
    Op LD_rr_ld = new Op(0x6A, "LD L,D", map, () -> {
        int v = reg.d();
        reg.l(v);
        clock.m(1);
        clock.t(4);
    });
    
    //Load into register L from register E
    Op LD_rr_le = new Op(0x6B, "LD L,E", map, () -> {
        int v = reg.e();
        reg.l(v);
        clock.m(1);
        clock.t(4);
    });
    
    //Load into register L from register H
    Op LD_rr_lh = new Op(0x6C, "LD L,H", map, () -> {
        int v = reg.h();
        reg.l(v);
        clock.m(1);
        clock.t(4);
    });
    
    //Load into register L from register L
    Op LD_rr_ll = new Op(0x6D, "LD L,L", map, () -> {
        int v = reg.l();
        reg.l(v);
        clock.m(1);
        clock.t(4);
    });
    
    //Load into register L from memory HL
    Op LD_rr_lhl = new Op(0x6E, "LD L,(HL)", map, () -> {
        int v = mmu.rb(reg.hl());
        reg.l(v);
        clock.m(2);
        clock.t(8);
    });
    
    //Load into memory HL from register B
    Op LD_rr_hlb = new Op(0x70, "LD (HL),B", map, () -> {
        mmu.wb(reg.hl(), reg.b());
        clock.m(2);
        clock.t(8);
    });
    
    //Load into memory HL from register C
    Op LD_rr_hlc = new Op(0x71, "LD (HL),C", map, () -> {
        mmu.wb(reg.hl(), reg.c());
        clock.m(2);
        clock.t(8);
    });
    
    //Load into memory HL from register D
    Op LD_rr_hld = new Op(0x72, "LD (HL),D", map, () -> {
        mmu.wb(reg.hl(), reg.d());
        clock.m(2);
        clock.t(8);
    });
    
    //Load into memory HL from register E
    Op LD_rr_hle = new Op(0x73, "LD (HL),E", map, () -> {
        mmu.wb(reg.hl(), reg.e());
        clock.m(2);
        clock.t(8);
    });
    
    //Load into memory HL from register H
    Op LD_rr_hlh = new Op(0x74, "LD (HL),H", map, () -> {
        mmu.wb(reg.hl(), reg.h());
        clock.m(2);
        clock.t(8);
    });
    
    //Load into memory HL from register L
    Op LD_rr_hll = new Op(0x75, "LD (HL),L", map, () -> {
        mmu.wb(reg.hl(), reg.l());
        clock.m(2);
        clock.t(8);
    });
    
    //Load into memory HL from immediate value n
    Op LD_rr_hln = new Op(0x36, "LD (HL),n", map, () -> {
        mmu.wb(reg.hl(), mmu.rb(reg.pc()));
        reg.pcpp(1);
        clock.m(3);
        clock.t(12);
    });
    
    //Load into register A from memory BC
    Op LD_abc = new Op(0x0A, "LD A,(BC)", map, () -> {
        int v = mmu.rb(reg.bc());
        reg.a(v);
        clock.m(2);
        clock.t(8);
    });
    
    //Load into register A from memory DE
    Op LD_ade = new Op(0x1A, "LD A,(DE)", map, () -> {
        int v = mmu.rb(reg.de());
        reg.a(v);
        clock.m(2);
        clock.t(8);
    });

    //Load into register A from memory nn
    Op LD_ann = new Op(0xFA, "LD A,(nn)", map, () -> {
        int v = mmu.rb( mmu.rw( reg.pc() ) );
        reg.a(v);
        reg.pcpp(2);
        clock.m(4);
        clock.t(16);
    });
    
    //Load into register A from immediate n
    Op LD_an = new Op(0x3E, "LD A,n", map, () -> {
        int v = mmu.rb( reg.pc() );
        reg.a(v);
        reg.pcpp(1);
        clock.m(2);
        clock.t(8);
    });
    
    //Load into register B the value in register A
    Op LD_ba = new Op(0x47, "LD B,A", map, () -> {
        int v = reg.a();
        reg.b(v);
        clock.m(1);
        clock.t(4);
    });
    
    //Load into register C the value in register A
    Op LD_ca = new Op(0x4F, "LD C,A", map, () -> {
        int v = reg.a();
        reg.c(v);
        clock.m(1);
        clock.t(4);
    });
    
    //Load into register D the value in register A
    Op LD_da = new Op(0x57, "LD D,A", map, () -> {
        int v = reg.a();
        reg.d(v);
        clock.m(1);
        clock.t(4);
    });
    
    //Load into register E the value in register A
    Op LD_ea = new Op(0x5F, "LD E,A", map, () -> {
        int v = reg.a();
        reg.e(v);
        clock.m(1);
        clock.t(4);
    });
    
    //Load into register H the value in register A
    Op LD_ha = new Op(0x67, "LD H,A", map, () -> {
        int v = reg.a();
        reg.h(v);
        clock.m(1);
        clock.t(4);
    });
    
    //Load into register L the value in register A
    Op LD_la = new Op(0x6F, "LD L,A", map, () -> {
        int v = reg.a();
        reg.l(v);
        clock.m(1);
        clock.t(4);
    });
    
    //Load into memory BC the value in register A
    Op LD_bca = new Op(0x02, "LD (BC),A", map, () -> {
        mmu.wb(reg.bc(), reg.a());
        clock.m(2);
        clock.t(8);
    });
    
    //Load into memory DE the value in register A
    Op LD_dea = new Op(0x12, "LD (DE),A", map, () -> {
        mmu.wb(reg.de(), reg.a());
        clock.m(2);
        clock.t(8);
    });
    
    //Load into memory HL the value in register A
    Op LD_hla = new Op(0x77, "LD (HL),A", map, () -> {
        mmu.wb(reg.hl(), reg.a());
        clock.m(2);
        clock.t(8);
    });
    
    //Load into memory nn the value in register A
    Op LD_nna = new Op(0xEA, "LD (nn),A", map, () -> {
        mmu.wb(mmu.rw(reg.pc()), reg.a());
        reg.pcpp(2);
        clock.m(4);
        clock.t(16);
    });
    
    //PAGE 70
    
    //Load into register A the value in 0xFF00 + register C
    Op LD_A_c = new Op(0xF2, "LD A,(FF00 + C)", map, () -> {
        int v = mmu.rb(0xFF00 + reg.c());
        reg.a(v);
        clock.m(2);
        clock.t(8);
    });
    
    //Load into memory 0xFF00 + register C the value in register A
    Op LD_c_A = new Op(0xE2, "LD (FF00 + C),A", map, () -> {
        mmu.wb(0xFF00 + reg.c(), reg.a());
        clock.m(2);
        clock.t(8);
    });
    
    //Put value at address HL into A, Decrement HL
    //Same as LD A,(HL) -> DEC HL
    Op LDD_A_HL = new Op(0x3A, "LDD A,(HL)", map, () -> {
        int v = mmu.rb(reg.hl());
        reg.a(v);
        reg.hl(reg.hl() - 1); //TODO
        clock.m(2);
        clock.t(8);
    });
    
    //Put register A into memory address at HL
    //Same as LD (HL),A -> DEC HL
    Op LDD_HL_A = new Op(0x32, "LDD (HL),A", map, () ->{
        mmu.wb(reg.hl(), reg.a());
        reg.hl(reg.hl() - 1); //TODO
        clock.m(2);
        clock.t(8);
    });
    
    //Put the value at address HL into A and increment HL
    Op LDI_A_HL = new Op(0x2A, "LDI A,(HL)", map, () -> {
        int v = mmu.rb(reg.hl());
        reg.a(v);
        reg.hl(reg.hl() + 1);
        clock.m(2);
        clock.t(8);
    });
    
    //Put into memory at HL the value in register A, increment HL
    Op LDI_HL_A = new Op(0x22, "LDI (HL),A", map, () -> {
        mmu.wb(reg.hl(), reg.a());
        reg.hl(reg.hl() + 1);
        clock.m(2);
        clock.t(8);
    });
    
    //Put A into memory address 0xFF00 + n
    Op LDH_n_A = new Op(0xE0, "LDH (FF00 + n),A", map, () -> {
        mmu.wb(0xFF00 + mmu.rb(reg.pc()), reg.a());
        reg.pcpp(1);
        clock.m(3);
        clock.t(12);
    });
    
    //Put memory address 0xFF00 + n into register A
    Op LDH_A_n = new Op(0xF0, "LDH A,(FF00 + n)", map, () -> {
        int v = mmu.rb(0xFF00 + mmu.rb(reg.pc()));
        reg.a(v);
        reg.pcpp(1);
        clock.m(3);
        clock.t(12);
    });
    
    //PAGE 76
    
    ///
    // 16 Bit Loads
    ///
    
    //Put 16bit immediate value into register BC
    Op LD_BC_nn = new Op(0x01, "LD BC,nn", map, () -> {
        int v = mmu.rw(reg.pc());
        reg.bc(v);
        reg.pcpp(2);
        clock.m(3);
        clock.t(12);
    });
    
    //Put 16bit immediate value into register DE
    Op LD_DE_nn = new Op(0x11, "LD DE,nn", map, () -> {
        int v = mmu.rw(reg.pc());
        reg.de(v);
        reg.pcpp(2);
        clock.m(3);
        clock.t(12);
    });
    
    //Put 16bit immediate value into register HL
    Op LD_HL_nn = new Op(0x21, "LD HL,nn", map, () -> {
        int v = mmu.rw(reg.pc());
        reg.hl(v);
        reg.pcpp(2);
        clock.m(3);
        clock.t(12);
    });
    
    //Put 16bit immediate value into register HL
    Op LD_SP_nn = new Op(0x31, "LD SP,nn", map, () -> {
        int v = mmu.rw(reg.pc());
        reg.sp(v);
        reg.pcpp(2);
        clock.m(3);
        clock.t(12);
    });
    
    //Put HL into stack pointer register SP TODO
    Op LD_SP_HL = new Op(0xF9, "LD SP,HL", map, () -> {
        int v = reg.hl();
        reg.sp(v);
        clock.m(2);
        clock.t(8);
    });
    
    //Put SP + n into HL, (n is a 8bit signed value)
    //Flags Z-Reset, N-Reset, H-Set or Reset, C-Set or Reset
    Op LDHL_SP_n = new Op(0xF8, "LDHL SP,n", map, () -> {
        int n =  unsignedByteToSigned(mmu.rb(reg.pc()));
        int sp = reg.sp();
        reg.hl(sp + n);
        reg.pcpp(1);
        clock.m(3);
        clock.t(12);
        
        reg.zero(false);
        reg.subtract(false);
        reg.halfcarry(isHalfCarry16(sp,n));
        reg.carry(isCarry16(sp + n));
    });
    
    //Put stack pointer into memory at nn //TODO
    Op LD_nn_SP = new Op(0x08, "LD (nn),SP", map, () -> {
        int v = reg.sp();
        mmu.ww(mmu.rw(reg.pc()), v);
        reg.pcpp(2);
        clock.m(5);
        clock.t(20);
    });
    
    //Push register pair AF onto the stack, Decrement Stack Pointer Twice
    Op PUSH_AF = new Op(0xF5, "PUSH AF", map, () -> {
        push(reg.af());
        clock.m(4);
        clock.t(16);
    });
    
    //Push register pair BC onto the stack, Decrement Stack Pointer Twice
    Op PUSH_BC = new Op(0xC5, "PUSH BC", map, () -> {
        push(reg.bc());
        clock.m(4);
        clock.t(16);
    });
    
    //Push register pair DE onto the stack, Decrement Stack Pointer Twice
    Op PUSH_DE = new Op(0xD5, "PUSH DE", map, () -> {
        push(reg.de());
        clock.m(4);
        clock.t(16);
    });
    
    //Push register pair HL onto the stack, Decrement Stack Pointer Twice
    Op PUSH_HL = new Op(0xE5, "PUSH HL", map, () -> {
        push(reg.hl());
        clock.m(4); //TODO 3 and 12 or 4 and 16? different sources say different things
        clock.t(16);
    });
    
    //Pop value off stack into register AF
    Op POP_AF = new Op(0xF1, "POP AF", map, () -> {
        reg.af(pop());
                
        clock.m(3);
        clock.t(12);
    });
    
    //Pop value off stack into register BC
    Op POP_BC = new Op(0xC1, "POP BC", map, () -> {
        reg.bc(pop());
        clock.m(3);
        clock.t(12);
    });
    
    //Pop value off stack into register DE
    Op POP_DE = new Op(0xD1, "POP DE", map, () -> {
        reg.de(pop());
        clock.m(3);
        clock.t(12);
    });
    
    //Pop value off stack into register HL
    Op POP_HL = new Op(0xE1, "POP HL", map, () -> {
        reg.hl(pop());
        clock.m(3);
        clock.t(12);
    });
    
    //PAGE 80
    
    ///
    // 8 Bit ALU
    ///
    
    //Add register A and register A into register A
    Op ADD_A_A = new Op(0x87, "ADD A,A", map, () -> {
        int a = reg.a();
        int b = reg.a();
        int v = a + b;
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(isHalfCarry(a, b));
        reg.carry(isCarry(v));
        
        clock.m(1);
        clock.t(4);
    });
    
    //Add register A and register B into register A
    Op ADD_A_B = new Op(0x80, "ADD A,B", map, () -> {
        int a = reg.a();
        int b = reg.b();
        int v = a + b;
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(isHalfCarry(a, b));
        reg.carry(isCarry(v));
        
        clock.m(1);
        clock.t(4);
    });
    
    //Add register A and register C into register A
    Op ADD_A_C = new Op(0x81, "ADD A,C", map, () -> {
        int a = reg.a();
        int b = reg.c();
        int v = a + b;
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(isHalfCarry(a, b));
        reg.carry(isCarry(v));
        
        clock.m(1);
        clock.t(4);
    });
    
    //Add register A and register D into register A
    Op ADD_A_D = new Op(0x82, "ADD A,D", map, () -> {
        int a = reg.a();
        int b = reg.d();
        int v = a + b;
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(isHalfCarry(a, b));
        reg.carry(isCarry(v));
        
        clock.m(1);
        clock.t(4);
    });
    
    //Add register A and register E into register A
    Op ADD_A_E = new Op(0x83, "ADD A,E", map, () -> {
        int a = reg.a();
        int b = reg.e();
        int v = a + b;
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(isHalfCarry(a, b));
        reg.carry(isCarry(v));
        
        clock.m(1);
        clock.t(4);
    });
    
    //Add register A and register H into register A
    Op ADD_A_H = new Op(0x84, "ADD A,H", map, () -> {
        int a = reg.a();
        int b = reg.h();
        int v = a + b;
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(isHalfCarry(a, b));
        reg.carry(isCarry(v));
        
        clock.m(1);
        clock.t(4);
    });
    
    //Add register A and register L into register A
    Op ADD_A_L = new Op(0x85, "ADD A,L", map, () -> {
        int a = reg.a();
        int b = reg.l();
        int v = a + b;
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(isHalfCarry(a, b));
        reg.carry(isCarry(v));
        
        clock.m(1);
        clock.t(4);
    });
    
    //Add register A and memory at HL into register A
    Op ADD_A_HL = new Op(0x86, "ADD A,(HL)", map, () -> {
        int a = reg.a();
        int b = mmu.rb(reg.hl());
        int v = a + b;
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(isHalfCarry(a, b));
        reg.carry(isCarry(v));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Add register A and immediate value 'n' into register A
    Op ADD_A_n = new Op(0xC6, "ADD A,n", map, () -> {
        int a = reg.a();
        int b = mmu.rb(reg.pc());
        int v = a + b;
        reg.a(v);
        reg.pcpp(1);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(isHalfCarry(a, b));
        reg.carry(isCarry(v));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Add A + Carry Flag into registry A
    //int v = addCarry(reg.a(), reg.a());
    Op ADC_A_A = new Op(0x8F, "ADC A,A", map, () -> {
        int v = addCarry8(reg.a(), reg.a());
        reg.a(v);
        
        clock.m(1);
        clock.t(4);
    });
    
    //Add B + Carry Flag into registry A
    Op ADC_A_B = new Op(0x88, "ADC A,B", map, () -> {
        int v = addCarry8(reg.a(), reg.b());
        reg.a(v);
        
        clock.m(1);
        clock.t(4);
    });
    
    //Add C + Carry Flag into registry A
    Op ADC_A_C = new Op(0x89, "ADC A,C", map, () -> {
        int v = addCarry8(reg.a(), reg.c());
        reg.a(v);
        
        clock.m(1);
        clock.t(4);
    });
    
    //Add D + Carry Flag into registry A
    Op ADC_A_D = new Op(0x8A, "ADC A,D", map, () -> {
        int v = addCarry8(reg.a(), reg.d());
        reg.a(v);
        
        clock.m(1);
        clock.t(4);
    });
    
    //Add E + Carry Flag into registry A
    Op ADC_A_E = new Op(0x8B, "ADC A,E", map, () -> {
        int v = addCarry8(reg.a(), reg.e());
        reg.a(v);
        
        clock.m(1);
        clock.t(4);
    });
    
    //Add H + Carry Flag into registry A
    Op ADC_A_H = new Op(0x8C, "ADC A,H", map, () -> {
        int v = addCarry8(reg.a(), reg.h());
        reg.a(v);
        
        clock.m(1);
        clock.t(4);
    });
    
    //Add L + Carry Flag into registry A
    Op ADC_A_L = new Op(0x8D, "ADC A,L", map, () -> {
        int v = addCarry8(reg.a(), reg.l());
        reg.a(v);
        
        clock.m(1);
        clock.t(4);
    });
    
    //Add memory at HL + Carry Flag into registry A
    Op ADC_A_HL = new Op(0x8E, "ADC A,(HL)", map, () -> {
        int v = addCarry8(reg.a(), mmu.rb(reg.hl()));
        reg.a(v);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Add immediate value n + Carry Flag into registry A
    Op ADC_A_n = new Op(0xCE, "ADC A,n", map, () -> {
        int v = addCarry8(reg.a(), mmu.rb(reg.pc()));
        reg.a(v);
        reg.pcpp(1);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Subtract register A from register A
    Op SUB_A_A = new Op(0x97, "SUB A,A", map, () -> {
        int n = reg.a(); //Change me
        int a = reg.a();
        int v = a - n;
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(true);
        reg.halfcarry(isHalfCarry(a, -n)); //WATCH THIS
        reg.carry(isCarry(v));
        
        clock.m(1);
        clock.t(4);
    });
    
    //Subtract register B from register A
    Op SUB_A_B = new Op(0x90, "SUB A,B", map, () -> {
        int n = reg.b(); //Change me
        int a = reg.a();
        int v = a - n;
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(true);
        reg.halfcarry(isHalfCarry(a, -n)); //WATCH THIS
        reg.carry(isCarry(v));
        
        clock.m(1);
        clock.t(4);
    });
    
    //Subtract register C from register A
    Op SUB_A_C = new Op(0x91, "SUB A,C", map, () -> {
        int n = reg.c(); //Change me
        int a = reg.a();
        int v = a - n;
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(true);
        reg.halfcarry(isHalfCarry(a, -n)); //WATCH THIS
        reg.carry(isCarry(v));
        
        clock.m(1);
        clock.t(4);
    });
    
    //Subtract register D from register A
    Op SUB_A_D = new Op(0x92, "SUB A,D", map, () -> {
        int n = reg.d(); //Change me
        int a = reg.a();
        int v = a - n;
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(true);
        reg.halfcarry(isHalfCarry(a, -n)); //WATCH THIS
        reg.carry(isCarry(v));
        
        clock.m(1);
        clock.t(4);
    });
    
    //Subtract register E from register A
    Op SUB_A_E = new Op(0x93, "SUB A,E", map, () -> {
        int n = reg.e(); //Change me
        int a = reg.a();
        int v = a - n;
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(true);
        reg.halfcarry(isHalfCarry(a, -n)); //WATCH THIS
        reg.carry(isCarry(v));
        
        clock.m(1);
        clock.t(4);
    });
    
    //Subtract register CHfrom register A
    Op SUB_A_H = new Op(0x94, "SUB A,H", map, () -> {
        int n = reg.h(); //Change me
        int a = reg.a();
        int v = a - n;
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(true);
        reg.halfcarry(isHalfCarry(a, -n)); //WATCH THIS
        reg.carry(isCarry(v));
        
        clock.m(1);
        clock.t(4);
    });
    
    //Subtract register L from register A
    Op SUB_A_L = new Op(0x95, "SUB A,L", map, () -> {
        int n = reg.l(); //Change me
        int a = reg.a();
        int v = a - n;
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(true);
        reg.halfcarry(isHalfCarry(a, -n)); //WATCH THIS
        reg.carry(isCarry(v));
        
        clock.m(1);
        clock.t(4);
    });
    
    //Subtract memory at HL from register A
    Op SUB_A_HL = new Op(0x96, "SUB A,(HL)", map, () -> {
        int n = mmu.rb(reg.hl()); //Change me
        int a = reg.a();
        int v = a - n;
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(true);
        reg.halfcarry(isHalfCarry(a, -n)); //WATCH THIS
        reg.carry(isCarry(v));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Subtract immediate value n from register A
    Op SUB_A_n = new Op(0xD6, "SUB A,n", map, () -> {
        int n = mmu.rb(reg.pc()); //Change me
        int a = reg.a();
        int v = a - n;
        reg.a(v);
        reg.pcpp(1);
        
        reg.zero(isZero(v));
        reg.subtract(true);
        reg.halfcarry(isHalfCarry(a, -n)); //WATCH THIS
        reg.carry(isCarry(v));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Subtract A + Carry flag from register A
    //int v = subCarry(reg.a(), reg.a());
    Op SBC_A_A = new Op(0x9F, "SBC A,A", map, () -> {
        int v = subCarry8(reg.a(), reg.a());
        reg.a(v);
        
        clock.m(1);
        clock.t(4);
    });
    
    //Subtract B + Carry flag from register A
    Op SBC_A_B = new Op(0x98, "SBC A,B", map, () -> {
        int v = subCarry8(reg.a(), reg.b());
        reg.a(v);
        
        clock.m(1);
        clock.t(4);
    });
    
    //Subtract C + Carry flag from register A
    Op SBC_A_C = new Op(0x99, "SBC A,C", map, () -> {
        int v = subCarry8(reg.a(), reg.c());
        reg.a(v);
        
        clock.m(1);
        clock.t(4);
    });
    
    //Subtract D + Carry flag from register A
    Op SBC_A_D = new Op(0x9A, "SBC A,D", map, () -> {
        int v = subCarry8(reg.a(), reg.d());
        reg.a(v);
        
        clock.m(1);
        clock.t(4);
    });
    
    //Subtract E + Carry flag from register A
    Op SBC_A_E = new Op(0x9B, "SBC A,E", map, () -> {
        int v = subCarry8(reg.a(), reg.e());
        reg.a(v);
        
        clock.m(1);
        clock.t(4);
    });
    
    
    //Subtract H + Carry flag from register A
    Op SBC_A_H = new Op(0x9C, "SBC A,H", map, () -> {
        int v = subCarry8(reg.a(), reg.h());
        reg.a(v);
        
        clock.m(1);
        clock.t(4);
    });
    
    //Subtract L + Carry flag from register A
    Op SBC_A_L = new Op(0x9D, "SBC A,L", map, () -> {
        int v = subCarry8(reg.a(), reg.l());
        reg.a(v);
        
        clock.m(1);
        clock.t(4);
    });
    
    //Subtract memory at HL + Carry flag from register A
    Op SBC_A_HL = new Op(0x9E, "SBC A,(HL)", map, () -> {
        int v = subCarry8(reg.a(), mmu.rb(reg.hl()));
        reg.a(v);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Subtract immediate value n + Carry flag from register A
    Op SBC_A_n = new Op(0xDE, "SBC A,n", map, () -> {
        int v = subCarry8(reg.a(), mmu.rb(reg.pc()));
        reg.a(v);
        reg.pcpp(1);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Logically and register A with register A, result in A
    Op AND_A_A = new Op(0xA7, "AND A,A", map, () -> {
        int n = reg.a();
        int a = reg.a();
        int v = a & n;
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(true);
        reg.carry(false);
        
        clock.m(1);
        clock.t(4);
    });
    
    //Logically and register A with register B, result in A
    Op AND_A_B = new Op(0xA0, "AND A,B", map, () -> {
        int n = reg.b();
        int a = reg.a();
        int v = a & n;
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(true);
        reg.carry(false);
        
        clock.m(1);
        clock.t(4);
    });
    
    //Logically and register A with register C, result in A
    Op AND_A_C = new Op(0xA1, "AND A,C", map, () -> {
        int n = reg.c();
        int a = reg.a();
        int v = a & n;
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(true);
        reg.carry(false);
        
        clock.m(1);
        clock.t(4);
    });
    
    //Logically and register A with register D, result in A
    Op AND_A_D = new Op(0xA2, "AND A,D", map, () -> {
        int n = reg.d();
        int a = reg.a();
        int v = a & n;
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(true);
        reg.carry(false);
        
        clock.m(1);
        clock.t(4);
    });
    
    //Logically and register A with register E, result in A
    Op AND_A_E = new Op(0xA3, "AND A,E", map, () -> {
        int n = reg.e();
        int a = reg.a();
        int v = a & n;
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(true);
        reg.carry(false);
        
        clock.m(1);
        clock.t(4);
    });
    
    //Logically and register A with register H, result in A
    Op AND_A_H = new Op(0xA4, "AND A,H", map, () -> {
        int n = reg.h();
        int a = reg.a();
        int v = a & n;
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(true);
        reg.carry(false);
        
        clock.m(1);
        clock.t(4);
    });
    
    //Logically and register A with register L, result in A
    Op AND_A_L = new Op(0xA5, "AND A,L", map, () -> {
        int n = reg.l();
        int a = reg.a();
        int v = a & n;
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(true);
        reg.carry(false);
        
        clock.m(1);
        clock.t(4);
    });
    
    //Logically and register A with memory at HL, result in A
    Op AND_A_HL = new Op(0xA6, "AND A,(HL)", map, () -> {
        int n = mmu.rb(reg.hl());
        int a = reg.a();
        int v = a & n;
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(true);
        reg.carry(false);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Logically and register A with immediate value n, result in A
    Op AND_A_n = new Op(0xE6, "AND A,n", map, () -> {
        int n = mmu.rb(reg.pc());
        int a = reg.a();
        int v = a & n;
        reg.a(v);
        reg.pcpp(1);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(true);
        reg.carry(false);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Logically or register A with register A, result in A
    Op OR_A_A = new Op(0xB7, "OR A,A", map, () -> {
        int n = reg.a();
        int a = reg.a();
        int v = a | n;
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(false);
        reg.carry(false);
        
        clock.m(1);
        clock.t(4);
    });
    
    //Logically or register A with register B, result in A
    Op OR_A_B = new Op(0xB0, "OR A,B", map, () -> {
        int n = reg.b();
        int a = reg.a();
        int v = a | n;
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(false);
        reg.carry(false);
        
        clock.m(1);
        clock.t(4);
    });
    
    //Logically or register A with register C, result in A
    Op OR_A_C = new Op(0xB1, "OR A,C", map, () -> {
        int n = reg.c();
        int a = reg.a();
        int v = a | n;
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(false);
        reg.carry(false);
        
        clock.m(1);
        clock.t(4);
    });
    
    //Logically or register A with register D, result in A
    Op OR_A_D = new Op(0xB2, "OR A,D", map, () -> {
        int n = reg.d();
        int a = reg.a();
        int v = a | n;
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(false);
        reg.carry(false);
        
        clock.m(1);
        clock.t(4);
    });
    
    //Logically or register A with register E, result in A
    Op OR_A_E = new Op(0xB3, "OR A,E", map, () -> {
        int n = reg.e();
        int a = reg.a();
        int v = a | n;
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(false);
        reg.carry(false);
        
        clock.m(1);
        clock.t(4);
    });
    
    //Logically or register A with register H, result in A
    Op OR_A_H = new Op(0xB4, "OR A,H", map, () -> {
        int n = reg.h();
        int a = reg.a();
        int v = a | n;
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(false);
        reg.carry(false);
        
        clock.m(1);
        clock.t(4);
    });
    
    //Logically or register A with register L, result in A
    Op OR_A_L = new Op(0xB5, "OR A,L", map, () -> {
        int n = reg.l();
        int a = reg.a();
        int v = a | n;
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(false);
        reg.carry(false);
        
        clock.m(1);
        clock.t(4);
    });
    
    //Logically or register A with memory at HL, result in A
    Op OR_A_HL = new Op(0xB6, "OR A,(HL)", map, () -> {
        int n = mmu.rb(reg.hl());
        int a = reg.a();
        int v = a | n;
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(false);
        reg.carry(false);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Logically or register A with immediate value n, result in A
    Op OR_A_n = new Op(0xF6, "OR A,n", map, () -> {
        int n = mmu.rb(reg.pc());
        int a = reg.a();
        int v = a | n;
        reg.a(v);
        reg.pcpp(1);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(false);
        reg.carry(false);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Logically exclusive or register A with register A, result in A
    Op XOR_A_A = new Op(0xAF, "XOR A,A", map, () -> {
        int n = reg.a();
        int a = reg.a();
        int v = a ^ n;
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(false);
        reg.carry(false);
        
        clock.m(1);
        clock.t(4);
    });
    
    //Logically exclusive or register A with register B, result in A
    Op XOR_A_B = new Op(0xA8, "XOR A,B", map, () -> {
        int n = reg.b();
        int a = reg.a();
        int v = a ^ n;
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(false);
        reg.carry(false);
        
        clock.m(1);
        clock.t(4);
    });
    
    //Logically exclusive or register A with register C, result in A
    Op XOR_A_C = new Op(0xA9, "XOR A,C", map, () -> {
        int n = reg.c();
        int a = reg.a();
        int v = a ^ n;
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(false);
        reg.carry(false);
        
        clock.m(1);
        clock.t(4);
    });
    
    //Logically exclusive or register A with register D, result in A
    Op XOR_A_D = new Op(0xAA, "XOR A,D", map, () -> {
        int n = reg.d();
        int a = reg.a();
        int v = a ^ n;
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(false);
        reg.carry(false);
        
        clock.m(1);
        clock.t(4);
    });
    
    //Logically exclusive or register A with register E, result in A
    Op XOR_A_E = new Op(0xAB, "XOR A,E", map, () -> {
        int n = reg.e();
        int a = reg.a();
        int v = a ^ n;
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(false);
        reg.carry(false);
        
        clock.m(1);
        clock.t(4);
    });
    
    //Logically exclusive or register A with register H, result in A
    Op XOR_A_H = new Op(0xAC, "XOR A,H", map, () -> {
        int n = reg.h();
        int a = reg.a();
        int v = a ^ n;
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(false);
        reg.carry(false);
        
        clock.m(1);
        clock.t(4);
    });
    
    //Logically exclusive or register A with register L, result in A
    Op XOR_A_L = new Op(0xAD, "XOR A,L", map, () -> {
        int n = reg.l();
        int a = reg.a();
        int v = a ^ n;
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(false);
        reg.carry(false);
        
        clock.m(1);
        clock.t(4);
    });
    
    //Logically exclusive or register A with memory at HL, result in A
    Op XOR_A_HL = new Op(0xAE, "XOR A,(HL)", map, () -> {
        int n = mmu.rb(reg.hl());
        int a = reg.a();
        int v = a ^ n;
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(false);
        reg.carry(false);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Logically exclusive or register A with immedate value n, result in A
    Op XOR_A_n = new Op(0xEE, "XOR A,n", map, () -> {
        int n = mmu.rb(reg.pc());
        int a = reg.a();
        int v = a ^ n;
        reg.a(v);
        reg.pcpp(1);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(false);
        reg.carry(false);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Compare register A with register A
    Op CP_A_A = new Op(0xBF, "CP A,A", map, () -> {
        int n = reg.a();
        int a = reg.a();
            
        compare(a,n);
        
        clock.m(1);
        clock.t(4);
    });
    
    //Compare register A with register B
    Op CP_A_B = new Op(0xB8, "CP A,B", map, () -> {
        int n = reg.b();
        int a = reg.a();
            
        compare(a,n);
        
        clock.m(1);
        clock.t(4);
    });
    
    //Compare register A with register C
    Op CP_A_C = new Op(0xB9, "CP A,C", map, () -> {
        int n = reg.c();
        int a = reg.a();
            
        compare(a,n);
        
        clock.m(1);
        clock.t(4);
    });
    
    //Compare register A with register D
    Op CP_A_D = new Op(0xBA, "CP A,D", map, () -> {
        int n = reg.d();
        int a = reg.a();
            
        compare(a,n);
        
        clock.m(1);
        clock.t(4);
    });
    
    //Compare register A with register E
    Op CP_A_E = new Op(0xBB, "CP A,E", map, () -> {
        int n = reg.e();
        int a = reg.a();
            
        compare(a,n);
        
        clock.m(1);
        clock.t(4);
    });
    
    //Compare register A with register H
    Op CP_A_H = new Op(0xBC, "CP A,H", map, () -> {
        int n = reg.h();
        int a = reg.a();
            
        compare(a,n);
        
        clock.m(1);
        clock.t(4);
    });
    
    //Compare register A with register L
    Op CP_A_L = new Op(0xBD, "CP A,L", map, () -> {
        int n = reg.l();
        int a = reg.a();
            
        compare(a,n);
        
        clock.m(1);
        clock.t(4);
    });
    
    //Compare register A with memory at HL
    Op CP_A_HL = new Op(0xBE, "CP A,(HL)", map, () -> {
        int n = mmu.rb(reg.hl());
        int a = reg.a();
            
        compare(a,n);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Compare register A with immediate value n
    Op CP_A_n = new Op(0xFE, "CP A,n", map, () -> {
        int n = mmu.rb(reg.pc());
        int a = reg.a();
        reg.pcpp(1);    
        
        compare(a,n);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Increment register A
    Op INC_A = new Op(0x3C, "INC A", map, () -> {
        int n = reg.a();
        int v = n + 1;
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(isHalfCarry(n, 1));
        
        clock.m(1);
        clock.t(4);
    });
    
    //Increment register B
    Op INC_B = new Op(0x04, "INC B", map, () -> {
        int n = reg.b();
        int v = n + 1;
        reg.b(v);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(isHalfCarry(n, 1));
        
        clock.m(1);
        clock.t(4);
    });
    
    //Increment register C
    Op INC_C = new Op(0x0C, "INC C", map, () -> {
        int n = reg.c();
        int v = n + 1;
        reg.c(v);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(isHalfCarry(n, 1));
        
        clock.m(1);
        clock.t(4);
    });
    
    //Increment register D
    Op INC_D = new Op(0x14, "INC D", map, () -> {
        int n = reg.d();
        int v = n + 1;
        reg.d(v);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(isHalfCarry(n, 1));
        
        clock.m(1);
        clock.t(4);
    });
    
    //Increment register E
    Op INC_E = new Op(0x1C, "INC E", map, () -> {
        int n = reg.e();
        int v = n + 1;
        reg.e(v);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(isHalfCarry(n, 1));
        
        clock.m(1);
        clock.t(4);
    });
    
    //Increment register H
    Op INC_H = new Op(0x24, "INC H", map, () -> {
        int n = reg.h();
        int v = n + 1;
        reg.h(v);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(isHalfCarry(n, 1));
        
        clock.m(1);
        clock.t(4);
    });
    
    //Increment register L
    Op INC_L = new Op(0x2C, "INC L", map, () -> {
        int n = reg.l();
        int v = n + 1;
        reg.l(v);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(isHalfCarry(n, 1));
        
        clock.m(1);
        clock.t(4);
    });
    
    //Increment memory at HL
    Op INC_HL = new Op(0x34, "INC (HL)", map, () -> {
        int n = mmu.rb(reg.hl());
        int v = n + 1;
        mmu.wb(reg.hl(), v);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(isHalfCarry(n, 1));
        
        clock.m(3);
        clock.t(12);
    });
    
    //Decrement register A
    Op DEC_A = new Op(0x3D, "DEC A", map, () -> {
        int n = reg.a();
        int v = n - 1;
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(true);
        reg.halfcarry(isHalfCarry(n, -1));
        
        clock.m(1);
        clock.t(4);
    });
    
    //Decrement register B
    Op DEC_B = new Op(0x05, "DEC B", map, () -> {
        int n = reg.b();
        int v = n - 1;
        reg.b(v);
        
        reg.zero(isZero(v));
        reg.subtract(true);
        reg.halfcarry(isHalfCarry(n, -1));
        
        clock.m(1);
        clock.t(4);
    });
    
    //Decrement register C
    Op DEC_C = new Op(0x0D, "DEC C", map, () -> {
        int n = reg.c();
        int v = n - 1;
        reg.c(v);
        
        reg.zero(isZero(v));
        reg.subtract(true);
        reg.halfcarry(isHalfCarry(n, -1));
        
        clock.m(1);
        clock.t(4);
    });
    
    //Decrement register D
    Op DEC_D = new Op(0x15, "DEC D", map, () -> {
        int n = reg.d();
        int v = n - 1;
        reg.d(v);
        
        reg.zero(isZero(v));
        reg.subtract(true);
        reg.halfcarry(isHalfCarry(n, -1));
        
        clock.m(1);
        clock.t(4);
    });
    
    //Decrement register E
    Op DEC_E = new Op(0x1D, "DEC E", map, () -> {
        int n = reg.e();
        int v = n - 1;
        reg.e(v);
        
        reg.zero(isZero(v));
        reg.subtract(true);
        reg.halfcarry(isHalfCarry(n, -1));
        
        clock.m(1);
        clock.t(4);
    });
    
    //Decrement register H
    Op DEC_H = new Op(0x25, "DEC H", map, () -> {
        int n = reg.h();
        int v = n - 1;
        reg.h(v);
        
        reg.zero(isZero(v));
        reg.subtract(true);
        reg.halfcarry(isHalfCarry(n, -1));
        
        clock.m(1);
        clock.t(4);
    });
    
    //Decrement register L
    Op DEC_L = new Op(0x2D, "DEC L", map, () -> {
        int n = reg.l();
        int v = n - 1;
        reg.l(v);
        
        reg.zero(isZero(v));
        reg.subtract(true);
        reg.halfcarry(isHalfCarry(n, -1));
        
        clock.m(1);
        clock.t(4);
    });
    
    //Decrement memory at HL
    Op DEC_HL = new Op(0x35, "DEC (HL)", map, () -> {
        int n = mmu.rb(reg.hl());
        int v = n - 1;
        mmu.wb(reg.hl(), v);
        
        reg.zero(isZero(v));
        reg.subtract(true);
        reg.halfcarry(isHalfCarry(n, -1));
        
        clock.m(3);
        clock.t(12);
    });
    
    //PAGE 90
    
    ///
    // 16 Bit ALU
    ///
    
    //Add to register HL the register BC
    Op ADD_HL_BC = new Op(0x09, "ADD HL,BC", map, () -> {
        int n = reg.bc();
        int hl = reg.hl();
        int v = hl + n;
        reg.hl(v);
        
        reg.subtract(false);
        reg.halfcarry(isHalfCarry16(hl, n));
        reg.carry(isCarry16(v));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Add to register HL the register DE
    Op ADD_HL_DE = new Op(0x19, "ADD HL,DE", map, () -> {
        int n = reg.de();
        int hl = reg.hl();
        int v = hl + n;
        reg.hl(v);
        
        reg.subtract(false);
        reg.halfcarry(isHalfCarry16(hl, n));
        reg.carry(isCarry16(v));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Add to register HL the register HL
    Op ADD_HL_HL = new Op(0x29, "ADD HL,HL", map, () -> {
        int n = reg.hl();
        int hl = reg.hl();
        int v = hl + n;
        reg.hl(v);
        
        reg.subtract(false);
        reg.halfcarry(isHalfCarry16(hl, n));
        reg.carry(isCarry16(v));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Add to register HL the register SP
    Op ADD_HL_SP = new Op(0x39, "ADD HL,SP", map, () -> {
        int n = reg.sp();
        int hl = reg.hl();
        int v = hl + n;
        reg.hl(v);
        
        reg.subtract(false);
        reg.halfcarry(isHalfCarry16(hl, n));
        reg.carry(isCarry16(v));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Add to register SP the signed immediate value n
    Op ADD_SP_n = new Op(0xE8, "ADD SP,n", map, () -> {
        //Read byte from pc and convert to a signed value
        int n = unsignedByteToSigned(mmu.rb(reg.pc()));
        reg.pcpp(1);
        
        //Add the signed value to the stack pointer value
        int sp = reg.sp();
        int v = sp + n;
        reg.sp(v);
        
        reg.zero(false);
        reg.subtract(false);
        reg.halfcarry(isHalfCarry16(sp, n));
        reg.carry(isCarry16(v));
        
        clock.m(4);
        clock.t(16);
    });
    
    //Increment the register BC
    Op INC_BC = new Op(0x03, "INC BC", map, () -> {
        int v = reg.bc() + 1;
        reg.bc(v);
        
        clock.m(1);
        clock.t(4);
    });
    
    //Increment the register DE
    Op INC_DE = new Op(0x13, "INC DE", map, () -> {
        int v = reg.de() + 1;
        reg.de(v);
        
        clock.m(1);
        clock.t(4);
    });
    
    //Increment the register HL
    Op INC_rHL = new Op(0x23, "INC HL", map, () -> {
        int v = reg.hl() + 1;
        reg.hl(v);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Increment the register SP
    Op INC_SP = new Op(0x33, "INC SP", map, () -> {
        int v = reg.sp() + 1;
        reg.sp(v);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Decrement the register BC
    Op DEC_BC = new Op(0x0B, "DEC BC", map, () -> {
        int v = reg.bc() - 1;
        reg.bc(v);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Decrement the register DE
    Op DEC_DE = new Op(0x1B, "DEC DE", map, () -> {
        int v = reg.de() - 1;
        reg.de(v);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Decrement the register BC
    Op DEC_rHL = new Op(0x2B, "DEC HL", map, () -> {
        int v = reg.hl() - 1;
        reg.hl(v);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Decrement the register SP
    Op DEC_SP = new Op(0x3B, "DEC SP", map, () -> {
        int v = reg.sp() - 1;
        reg.sp(v);
        
        clock.m(2);
        clock.t(8);
    });
    
    ///
    // Miscellaneous Operations
    ///
    
    //Swap the upper and lower nibbles of register A
    Op SWAP_A = new Op(0x37, "SWAP A", cbmap, () -> {
        int t = reg.a();
        int v = ((t&0x0F) << 4) | ((t&0xF0) >> 4);
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(false);
        reg.carry(false);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Swap the upper and lower nibbles of register B
    Op SWAP_B = new Op(0x30, "SWAP B", cbmap, () -> {
        int t = reg.b();
        int v = ((t&0x0F) << 4) | ((t&0xF0) >> 4);
        reg.b(v);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(false);
        reg.carry(false);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Swap the upper and lower nibbles of register C
    Op SWAP_C = new Op(0x31, "SWAP C", cbmap, () -> {
        int t = reg.c();
        int v = ((t&0x0F) << 4) | ((t&0xF0) >> 4);
        reg.c(v);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(false);
        reg.carry(false);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Swap the upper and lower nibbles of register D
    Op SWAP_D = new Op(0x32, "SWAP D", cbmap, () -> {
        int t = reg.d();
        int v = ((t&0x0F) << 4) | ((t&0xF0) >> 4);
        reg.d(v);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(false);
        reg.carry(false);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Swap the upper and lower nibbles of register E
    Op SWAP_E = new Op(0x33, "SWAP E", cbmap, () -> {
        int t = reg.e();
        int v = ((t&0x0F) << 4) | ((t&0xF0) >> 4);
        reg.e(v);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(false);
        reg.carry(false);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Swap the upper and lower nibbles of register H
    Op SWAP_H = new Op(0x34, "SWAP H", cbmap, () -> {
        int t = reg.h();
        int v = ((t&0x0F) << 4) | ((t&0xF0) >> 4);
        reg.h(v);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(false);
        reg.carry(false);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Swap the upper and lower nibbles of register L
    Op SWAP_L = new Op(0x35, "SWAP L", cbmap, () -> {
        int t = reg.l();
        int v = ((t&0x0F) << 4) | ((t&0xF0) >> 4);
        reg.l(v);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(false);
        reg.carry(false);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Swap the upper and lower nibbles of memory at HL
    Op SWAP_HL = new Op(0x36, "SWAP (HL)", cbmap, () -> {
        int t = mmu.rb(reg.hl());
        int v = ((t&0x0F) << 4) | ((t&0xF0) >> 4);
        mmu.wb(reg.hl(), v);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(false);
        reg.carry(false);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Adjust register A so that the correct represnetation of a binary coded decimal is obtained
    Op DAA = new Op(0x27, "DAA", map, () -> {
        int a = reg.a();
        
        if(!reg.subtract()){
            if(reg.halfcarry() || (a & 0x0F) > 9)
                a += 6;
            
            if(reg.carry() || a > 0x9F)
                a += 0x60;
        }else{
            if(reg.halfcarry()){
                a -= 6;
                if(!reg.carry())
                    a &= 0xFF;
            }
            
            if(reg.carry())
                a -= 0x60;
        }
        
        reg.a(a);
        
        reg.zero(isZero(a));
        reg.carry((a & 0x100) != 0);
        reg.halfcarry(false);
        
        clock.m(1);
        clock.t(4);
    });
    
    //Compement the A register
    Op CPL = new Op(0x2F, "CPL", map, () -> {
        int a = reg.a() ^ 255;
        reg.a(a);
        
        reg.subtract(true);
        reg.halfcarry(true);
        
        clock.m(1);
        clock.t(4);
    });
    
    //If carry flag is set, reset it. If flag is reset, set it
    Op CCF = new Op(0x3F, "CCF", map, () -> {
        reg.carry(!reg.carry());
        reg.halfcarry(false);
        reg.subtract(false);
        
        clock.m(1);
        clock.t(4);
    });
    
    //Set the carry flag
    Op SCF = new Op(0x37, "SCF", map, () -> {
        reg.carry(true);
        reg.halfcarry(false);
        reg.subtract(false);
        
        clock.m(1);
        clock.t(4);
    });
    
    //Power down the CPU
    Op HALT = new Op(0x76, "HALT", map, () -> {
        clock.m(1);
        clock.t(4);
    });
    
    //Disables interrupts
    Op DI = new Op(0xF3, "DI", map, () -> {
        reg.ime(0);
        
        clock.m(1);
        clock.t(4);
    });
    
    //Enabled interrupts
    Op EI = new Op(0xFB, "EI", map, () -> {
        reg.ime(1);
        
        clock.m(1);
        clock.t(4);
    });
    
    ///
    // Rotates and Shifts
    ///
    //MARK
    //Rotate A left, old bit 7 to Carry flag
    Op RLCA = new Op(0x07, "RCLA", map, () -> {
        int a = reg.a();
        boolean carry = ((a & 0x80) != 0);
        a = (a << 1) | (carry ? 1: 0);
        reg.a(a);
        
        reg.zero(false);
        reg.subtract(false);
        reg.halfcarry(false);
        reg.carry(carry);

        clock.m(1);
        clock.t(4);
    });
    
    //Rotate A left through carry flag 
    Op RLA = new Op(0x17, "RLA", map, () -> {
        int a = reg.a();
        boolean carry = ((a & 0x80) != 0);
        a = (a << 1) | (reg.carry() ? 1 : 0);
        reg.a(a);
        
        reg.zero(false);
        reg.subtract(false);
        reg.halfcarry(false);
        reg.carry(carry);
        
        clock.m(1);
        clock.t(4);
    });
    
    //Rotate A right, old bit 0 to carry flag
    Op RRCA = new Op(0x0F, "RRCA", map, () -> {
        int a = reg.a();
        boolean toCarry = ((a & 0b1) != 0);
        a = (a >> 1) | (toCarry ? 0x80 : 0);
        reg.a(a);
        
        reg.zero(false);
        reg.subtract(false);
        reg.halfcarry(false);
        reg.carry(toCarry);
        
        clock.m(1);
        clock.t(4);
    });
    
    //Rotate A right though the carry flag, old bit 0 to carry flag
    Op RRA = new Op(0x1F, "RRA", map, () -> {
        int a = reg.a();
        boolean toCarry = ((a & 0b1) != 0);
        a = (a >> 1) | (reg.carry() ? 0x80 : 0);
        reg.a(a);
        
        reg.zero(false);
        reg.subtract(false);
        reg.halfcarry(false);
        reg.carry(toCarry);
        
        clock.m(1);
        clock.t(4);
    }); 
    
    ///
    // Jumps
    ///
    
    //Jump to immediate address nn
    Op JP_nn = new Op(0xC3, "JP nn", map, () -> {
        int nn = mmu.rw(reg.pc());
        reg.pc(nn);
        
        clock.m(3);
        clock.t(12);
    });
    
    //Jump to address nn if Z flag is reset
    Op JP_NZ_nn = new Op(0xC2, "JP NZ,nn", map, () -> {
        int nn = mmu.rw(reg.pc());
        
        if(!reg.zero()){
            reg.pc(nn); //Maybe clock.m++;
            clock.m(1);
            clock.t(4);
        }else
            reg.pcpp(2);
        
        clock.m(3);
        clock.t(12);
    });
    
    //Jump to address nn if Z flag is set
    Op JP_Z_nn = new Op(0xCA, "JP Z,nn", map, () -> {
        int nn = mmu.rw(reg.pc());
        
        if(reg.zero()){
            reg.pc(nn); //Maybe clock.m++;
            clock.m(1);
            clock.t(4);
        }else
            reg.pcpp(2);
        
        clock.m(3);
        clock.t(12);
    });
    
    //Jump to address nn if Carry flag is reset
    Op JP_NC_nn = new Op(0xD2, "JP NC,nn", map, () -> {
        int nn = mmu.rw(reg.pc());
        
        if(!reg.carry()){
            reg.pc(nn); //Maybe clock.m++;
            clock.m(1);
            clock.t(4);
        }else
            reg.pcpp(2);
        
        clock.m(3);
        clock.t(12);
    });
    
    //Jump to address nn if Carry flag is set
    Op JP_C_nn = new Op(0xDA, "JP C,nn", map, () -> {
        int nn = mmu.rw(reg.pc());
        
        if(reg.carry()){
            reg.pc(nn); //Maybe clock.m++;
            clock.m(1);
            clock.t(4);
        }else
            reg.pcpp(2);
        
        clock.m(3);
        clock.t(12);
    });
    
    //Jump to address in HL
    Op JP_HL = new Op(0xE9, "JP (HL)", map, () -> {
        reg.pc(reg.hl());
        
        clock.m(1);
        clock.t(4);
    });
    
    //Add signed value n to the current address and jump to it
    Op JR_n = new Op(0x18, "JR n", map, () -> {
        int n = unsignedByteToSigned(mmu.rb(reg.pc()));
        reg.pcpp(1);
        int a = reg.pc() + n;
        reg.pc(a);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Jump to pc + signed n if Z flag is reset
    Op JR_NZ_n = new Op(0x20, "JR NZ,n", map, () -> {
        int i = unsignedByteToSigned(mmu.rb(reg.pc()));
        reg.pcpp(1);
        
        if(!reg.zero()){
            reg.pc(reg.pc() + i);
            clock.m(1);
            clock.t(4);
        }
        
        clock.m(2);
        clock.t(8);
    });
    
    //Jump to pc + signed n if Z flag is set
    Op JR_Z_n = new Op(0x28, "JR Z,n", map, () -> {
        int i = unsignedByteToSigned(mmu.rb(reg.pc()));
        reg.pcpp(1);
        
        if(reg.zero()){
            reg.pc(reg.pc() + i);
            clock.m(1);
            clock.t(4);
        }
        
        clock.m(2);
        clock.t(8);
    });
    
    //Jump to pc + signed n if C flag is reset
    Op JR_NC_n = new Op(0x30, "JR NC,n", map, () -> {
        int i = unsignedByteToSigned(mmu.rb(reg.pc()));
        reg.pcpp(1);
        
        if(!reg.carry()){
            reg.pc(reg.pc() + i);
            clock.m(1);
            clock.t(4);
        }
        
        clock.m(2);
        clock.t(8);
    });
    
    //Jump to pc + signed n if C flag is set
    Op JR_C_n = new Op(0x38, "JR C,n", map, () -> {
        int i = unsignedByteToSigned(mmu.rb(reg.pc()));
        reg.pcpp(1);
        
        if(reg.carry()){
            reg.pc(reg.pc() + i);
            clock.m(1);
            clock.t(4);
        }
        
        clock.m(2);
        clock.t(8);
    });
    
    //PAGE 114
    
    ///
    // Calls
    //
    
    //Push next instruction onto stack and jump to address nn
    Op CALL_nn = new Op(0xCD, "CALL nn", map, () -> {
        //Get inst params
        int addr = mmu.rw(reg.pc());
        reg.pcpp(2);
        
        //Push next to stack
        push(reg.pc());
        
        //Jump to immediate value
        reg.pc(addr);
        
        clock.m(5);
        clock.t(24);
    });
    
    //Call address n if the condtion is true
    Op CALL_NZ_nn = new Op(0xC4, "CALL NZ,nn", map, () -> {
        if(!reg.zero()){
            //Get inst params
            int addr = mmu.rw(reg.pc());
            reg.pcpp(2);

            //Push next to stack
            push(reg.pc());

            //Jump to immediate value
            reg.pc(addr);
            
            clock.m(2);
            clock.t(8);
        }else{
            reg.pcpp(2);
        }
        
        clock.m(3);
        clock.t(12);
    });
    
    //Call address n if the condtion is true
    Op CALL_Z_nn = new Op(0xCC, "CALL Z,nn", map, () -> {
        if(reg.zero()){
            //Get inst params
            int addr = mmu.rw(reg.pc());
            reg.pcpp(2);

            //Push next to stack
            push(reg.pc());

            //Jump to immediate value
            reg.pc(addr);
            
            clock.m(2);
            clock.t(8);
        }else{
            reg.pcpp(2);
        }
        
        clock.m(3);
        clock.t(12);
    });
    
    //Call address n if the condtion is true
    Op CALL_NC_nn = new Op(0xD4, "CALL NC,nn", map, () -> {
        if(!reg.carry()){
            //Get inst params
            int addr = mmu.rw(reg.pc());
            reg.pcpp(2);

            //Push next to stack
            push(reg.pc());

            //Jump to immediate value
            reg.pc(addr);
            
            clock.m(2);
            clock.t(8);
        }else{
            reg.pcpp(2);
        }
        
        clock.m(3);
        clock.t(12);
    });
    
    //Call address n if the condtion is true
    Op CALL_C_nn = new Op(0xDC, "CALL C,nn", map, () -> {
        if(reg.carry()){
            //Get inst params
            int addr = mmu.rw(reg.pc());
            reg.pcpp(2);

            //Push next to stack
            push(reg.pc());

            //Jump to immediate value
            reg.pc(addr);
            
            clock.m(2);
            clock.t(8);
        }else{
            reg.pcpp(2);
        }
        
        clock.m(3);
        clock.t(12);
    });
    
    //PAGE 116
    
    ///
    // Restarts
    ///
    
    private void rst(int loc){
        push(reg.pc());
        reg.pc(loc);
        
        clock.m(3); //Maybe 8 and 32 or 4 or something
        clock.t(12);
    }
    
    //Push present address onto stack and jump to 0x0000 + n
    Op RST_00h = new Op(0xC7, "RST 00H", map, () -> {
        rst(0x00);
    });
    
    //Push present address onto stack and jump to 0x0000 + n
    Op RST_08h = new Op(0xCF, "RST 08H", map, () -> {
        rst(0x08);
    });
    
    //Push present address onto stack and jump to 0x0000 + n
    Op RST_10h = new Op(0xD7, "RST 10H", map, () -> {
        rst(0x10);
    });
    
    //Push present address onto stack and jump to 0x0000 + n
    Op RST_18h = new Op(0xDF, "RST 18H", map, () -> {
        rst(0x18);
    });
    
    //Push present address onto stack and jump to 0x0000 + n
    Op RST_20h = new Op(0xE7, "RST 20H", map, () -> {
        rst(0x20);
    });
    
    //Push present address onto stack and jump to 0x0000 + n
    Op RST_28h = new Op(0xEF, "RST 28H", map, () -> {
        rst(0x28);
    });
    
    //Push present address onto stack and jump to 0x0000 + n
    Op RST_30h = new Op(0xF7, "RST 30H", map, () -> {
        rst(0x30);
    });
    
    //Push present address onto stack and jump to 0x0000 + n
    Op RST_38h = new Op(0xFF, "RST 38H", map, () -> {
        rst(0x38);
    });
    
    ///
    // System Specific Interrupt handlers
    ///
    
    public Op RST_40h = new Op(0xFF1, "RST 40H", null, () -> {
        rst(0x40);
    });
    
    public Op RST_48h = new Op(0xFF2, "RST 48H", null, () -> {
        rst(0x48);
    });
    
    public Op RST_50h = new Op(0xFF3, "RST 50H", null, () -> {
        rst(0x50);
    });
    
    public Op RST_58h = new Op(0xFF4, "RST 58H", null, () -> {
        rst(0x58);
    });
    
    public Op RST_60h = new Op(0xFF5, "RST 60H", null, () -> {
        rst(0x60);
    });
    
    //PAGE 117
    
    ///
    // Returns
    ///
    
    //Pop 2 bytes off stack and jump to that address
    Op RET = new Op(0xC9, "RET", map, () -> {
        reg.pc(pop());
        
        clock.m(3);
        clock.t(12);
    });
    
    //Return if Z flag is reset
    Op RET_NZ = new Op(0xC0, "RET NZ", map, () -> {
        if(!reg.zero()){
            reg.pc(pop());
            clock.m(3);
            clock.t(12);
        }else{
            clock.m(1);
            clock.t(4);
        }
    });
    
    //Return if Z flag is set
    Op RET_Z = new Op(0xC8, "RET Z", map, () -> {
        if(reg.zero()){
            reg.pc(pop());
            clock.m(3);
            clock.t(12);
        }else{
            clock.m(1);
            clock.t(4);
        }
    });
    
    //Return if C flag is reset
    Op RET_CZ = new Op(0xD0, "RET CZ", map, () -> {
        if(!reg.carry()){
            reg.pc(pop());
            clock.m(3);
            clock.t(12);
        }else{
            clock.m(1);
            clock.t(4);
        }
    });
    
    //Return if C flag is set
    Op RET_C = new Op(0xD8, "RET C", map, () -> {
        if(reg.carry()){
            reg.pc(pop());
            clock.m(3);
            clock.t(12);
        }else{
            clock.m(1);
            clock.t(4);
        }
    });
    
    //Pop 2 bytes off the stack, and jump there then enable interrupts
    Op RETI = new Op(0xD9, "RETI", map, () -> {
        //rrs(); //Restore registry //TODO
        EI.Invoke();
        reg.pc(pop());
        
        clock.m(2);
        clock.t(8);
    });
    
    //Use CB opcode
    Op CB = new Op(0xCB, "CBOP", map, () -> {
        int i = mmu.rb(reg.pc());
        reg.pcpp(1);
        if(i >= 0 && i < cbmap.length){
            cbmap[i].Invoke();
        }
        else{
            System.out.println("Trying to call CB-prefixed opcode out of range 0-255 ("+i+")");
        }
    });
    
    Op DJNZn = new Op(0x10, "DJNZn", map, () -> { //TODO this looks to be a STOP operation
        int i = unsignedByteToSigned(mmu.rb(reg.pc()));
        reg.pcpp(1);
        
        clock.m(2);
        clock.t(8);
        
        reg.b(reg.b() - 1);
        if(reg.b() != 0){
            reg.pcpp(i);
            clock.m(1);
            clock.t(4);
        }
    });
    
    ///
    // CB Opcodes
    ///
    
    //Rotate register A left
    Op RLC_A = new Op(0x07, "RLC A", cbmap, () -> {
        int a = rotateLeftCarry(reg.a());
        reg.a(a);

        clock.m(2);
        clock.t(8);
    });
    
    //Rotate register B left
    Op RLC_B = new Op(0x00, "RLC B", cbmap, () -> {
        int a = rotateLeftCarry(reg.b());
        reg.b(a);

        clock.m(2);
        clock.t(8);
    });
    
    //Rotate register C left
    Op RLC_C = new Op(0x01, "RLC C", cbmap, () -> {
        int a = rotateLeftCarry(reg.c());
        reg.c(a);

        clock.m(2);
        clock.t(8);
    });
    
    //Rotate register D left
    Op RLC_D = new Op(0x02, "RLC D", cbmap, () -> {
        int a = rotateLeftCarry(reg.d());
        reg.d(a);

        clock.m(2);
        clock.t(8);
    });
    
    //Rotate register E left
    Op RLC_E = new Op(0x03, "RLC E", cbmap, () -> {
        int a = rotateLeftCarry(reg.e());
        reg.e(a);

        clock.m(2);
        clock.t(8);
    });
    
    //Rotate register H left
    Op RLC_H = new Op(0x04, "RLC H", cbmap, () -> {
        int a = rotateLeftCarry(reg.h());
        reg.h(a);

        clock.m(2);
        clock.t(8);
    });
    
    //Rotate register L left
    Op RLC_L = new Op(0x05, "RLC L", cbmap, () -> {
        int a = rotateLeftCarry(reg.l());
        reg.l(a);

        clock.m(2);
        clock.t(8);
    });
    
    //Rotate memory location at HL left
    Op RLC_HL = new Op(0x06, "RLC (HL)", cbmap, () -> {
        int a = rotateLeftCarry(mmu.rb(reg.hl()));
        mmu.wb(reg.hl(), a);

        clock.m(4);
        clock.t(16);
    });
    
    //Rotate register A left through the carry flag
    Op RL_A = new Op(0x17, "RL A", cbmap, () -> {
        int a = rotateLeft(reg.a());
        reg.a(a);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Rotate register B left through the carry flag
    Op RL_B = new Op(0x10, "RL B", cbmap, () -> {
        int a = rotateLeft(reg.b());
        reg.b(a);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Rotate register C left through the carry flag
    Op RL_C = new Op(0x11, "RL C", cbmap, () -> {
        int a = rotateLeft(reg.c());
        reg.c(a);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Rotate register D left through the carry flag
    Op RL_D = new Op(0x12, "RL D", cbmap, () -> {
        int a = rotateLeft(reg.d());
        reg.d(a);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Rotate register E left through the carry flag
    Op RL_E = new Op(0x13, "RL E", cbmap, () -> {
        int a = rotateLeft(reg.e());
        reg.e(a);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Rotate register H left through the carry flag
    Op RL_H = new Op(0x14, "RL H", cbmap, () -> {
        int a = rotateLeft(reg.h());
        reg.h(a);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Rotate register L left through the carry flag
    Op RL_L = new Op(0x15, "RL L", cbmap, () -> {
        int a = rotateLeft(reg.l());
        reg.l(a);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Rotate memry at HL left through the carry flag
    Op RL_HL = new Op(0x16, "RL (HL)", cbmap, () -> {
        int a = rotateLeft(mmu.rb(reg.hl()));
        mmu.wb(reg.hl(), a);
        
        clock.m(4);
        clock.t(16);
    });
    
    //Rotate A right though the carry flag, old bit 0 to carry flag
    Op RR_A = new Op(0x1F, "RR A", cbmap, () -> {
        int a = rotateRight(reg.a());
        reg.a(a);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Rotate B right though the carry flag, old bit 0 to carry flag
    Op RR_B = new Op(0x18, "RR B", cbmap, () -> {
        int a = rotateRight(reg.b());
        reg.b(a);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Rotate C right though the carry flag, old bit 0 to carry flag
    Op RR_C = new Op(0x19, "RR C", cbmap, () -> {
        int a = rotateRight(reg.c());
        reg.c(a);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Rotate D right though the carry flag, old bit 0 to carry flag
    Op RR_D = new Op(0x1A, "RR D", cbmap, () -> {
        int a = rotateRight(reg.d());
        reg.d(a);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Rotate E right though the carry flag, old bit 0 to carry flag
    Op RR_E = new Op(0x1B, "RR E", cbmap, () -> {
        int a = rotateRight(reg.e());
        reg.e(a);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Rotate H right though the carry flag, old bit 0 to carry flag
    Op RR_H = new Op(0x1C, "RR H", cbmap, () -> {
        int a = rotateRight(reg.h());
        reg.h(a);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Rotate L right though the carry flag, old bit 0 to carry flag
    Op RR_L = new Op(0x1D, "RR L", cbmap, () -> {
        int a = rotateRight(reg.l());
        reg.l(a);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Rotate memory HL right though the carry flag, old bit 0 to carry flag
    Op RR_HL = new Op(0x1E, "RR (HL)", cbmap, () -> {
        int a = rotateRight(mmu.rb(reg.hl()));
        mmu.wb(reg.hl(), a);
        
        clock.m(4);
        clock.t(16);
    });
    
    //Rotate A right, old bit 0 to carry flag
    Op RRC_A = new Op(0x0F, "RRC A", cbmap, () -> {
        int a = rotateRightCarry(reg.a());
        reg.a(a);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Rotate B right, old bit 0 to carry flag
    Op RRC_B = new Op(0x08, "RRC B", cbmap, () -> {
        int a = rotateRightCarry(reg.b());
        reg.b(a);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Rotate C right, old bit 0 to carry flag
    Op RRC_C = new Op(0x09, "RRC C", cbmap, () -> {
        int a = rotateRightCarry(reg.c());
        reg.c(a);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Rotate D right, old bit 0 to carry flag
    Op RRC_D = new Op(0x0A, "RRC D", cbmap, () -> {
        int a = rotateRightCarry(reg.d());
        reg.d(a);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Rotate E right, old bit 0 to carry flag
    Op RRC_E = new Op(0x0B, "RRC E", cbmap, () -> {
        int a = rotateRightCarry(reg.e());
        reg.e(a);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Rotate H right, old bit 0 to carry flag
    Op RRC_H = new Op(0x0C, "RRC H", cbmap, () -> {
        int a = rotateRightCarry(reg.h());
        reg.h(a);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Rotate L right, old bit 0 to carry flag
    Op RRC_L = new Op(0x0D, "RRC L", cbmap, () -> {
        int a = rotateRightCarry(reg.l());
        reg.l(a);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Rotate memory at HL right, old bit 0 to carry flag
    Op RRC_HL = new Op(0x0E, "RRC (HL)", cbmap, () -> {
        int a = rotateRightCarry(mmu.rb(reg.hl()));
        mmu.wb(reg.hl(), a);
        
        clock.m(4);
        clock.t(16);
    });
    
    //PAGE 105 specifically 0x86 and 0xFE
    
    //Shift A into carry, LSB is set to 0;
    Op SLA_A = new Op(0x27, "SLA A", cbmap, () -> {
        int n = this.shiftLeft(reg.a());
        reg.a(n);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Shift B into carry, LSB is set to 0;
    Op SLA_B = new Op(0x20, "SLA B", cbmap, () -> {
        int n = this.shiftLeft(reg.b());
        reg.b(n);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Shift C into carry, LSB is set to 0;
    Op SLA_C = new Op(0x21, "SLA C", cbmap, () -> {
        int n = this.shiftLeft(reg.c());
        reg.c(n);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Shift D into carry, LSB is set to 0;
    Op SLA_D = new Op(0x22, "SLA D", cbmap, () -> {
        int n = this.shiftLeft(reg.d());
        reg.d(n);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Shift E into carry, LSB is set to 0;
    Op SLA_E = new Op(0x23, "SLA E", cbmap, () -> {
        int n = this.shiftLeft(reg.e());
        reg.e(n);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Shift H into carry, LSB is set to 0;
    Op SLA_H = new Op(0x24, "SLA H", cbmap, () -> {
        int n = this.shiftLeft(reg.h());
        reg.h(n);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Shift L into carry, LSB is set to 0;
    Op SLA_L = new Op(0x25, "SLA L", cbmap, () -> {
        int n = this.shiftLeft(reg.l());
        reg.l(n);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Shift memory at HL into carry, LSB is set to 0;
    Op SLA_HL = new Op(0x26, "SLA (HL)", cbmap, () -> {
        int n = this.shiftLeft(mmu.rb(reg.hl()));
        mmu.wb(reg.hl(), n);
        
        clock.m(4);
        clock.t(16);
    });
 
    //Shift A into carry, MSB doesnt change
    Op SRA_A = new Op(0x2F, "SRA A", cbmap, () -> {
        int n = this.shiftRightExtend(reg.a());
        reg.a(n);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Shift B into carry, MSB doesnt change
    Op SRA_B = new Op(0x28, "SRA B", cbmap, () -> {
        int n = this.shiftRightExtend(reg.b());
        reg.b(n);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Shift C into carry, MSB doesnt change
    Op SRA_C = new Op(0x29, "SRA C", cbmap, () -> {
        int n = this.shiftRightExtend(reg.c());
        reg.c(n);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Shift D into carry, MSB doesnt change
    Op SRA_D = new Op(0x2A, "SRA D", cbmap, () -> {
        int n = this.shiftRightExtend(reg.d());
        reg.d(n);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Shift E into carry, MSB doesnt change
    Op SRA_E = new Op(0x2B, "SRA E", cbmap, () -> {
        int n = this.shiftRightExtend(reg.e());
        reg.e(n);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Shift H into carry, MSB doesnt change
    Op SRA_H = new Op(0x2C, "SRA H", cbmap, () -> {
        int n = this.shiftRightExtend(reg.h());
        reg.h(n);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Shift L into carry, MSB doesnt change
    Op SRA_L = new Op(0x2D, "SRA L", cbmap, () -> {
        int n = this.shiftRightExtend(reg.l());
        reg.l(n);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Shift memory at HL into carry, MSB doesnt change
    Op SRA_HL = new Op(0x2E, "SRA (HL)", cbmap, () -> {
        int n = this.shiftRightExtend(mmu.rb(reg.hl()));
        mmu.wb(reg.hl(), n);
        
        clock.m(4);
        clock.t(16);
    });
    
    //Shift A into carry, MSB 0
    Op SRL_A = new Op(0x3F, "SRL A", cbmap, () -> {
        int n = this.shiftRight0(reg.a());
        reg.a(n);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Shift B into carry, MSB 0
    Op SRL_B = new Op(0x38, "SRL B", cbmap, () -> {
        int n = this.shiftRight0(reg.b());
        reg.b(n);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Shift C into carry, MSB 0
    Op SRL_C = new Op(0x39, "SRL C", cbmap, () -> {
        int n = this.shiftRight0(reg.c());
        reg.c(n);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Shift D into carry, MSB 0
    Op SRL_D = new Op(0x3A, "SRL D", cbmap, () -> {
        int n = this.shiftRight0(reg.d());
        reg.d(n);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Shift E into carry, MSB 0
    Op SRL_E = new Op(0x3B, "SRL E", cbmap, () -> {
        int n = this.shiftRight0(reg.e());
        reg.e(n);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Shift H into carry, MSB 0
    Op SRL_H = new Op(0x3C, "SRL H", cbmap, () -> {
        int n = this.shiftRight0(reg.h());
        reg.h(n);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Shift L into carry, MSB 0
    Op SRL_L = new Op(0x3D, "SRL L", cbmap, () -> {
        int n = this.shiftRight0(reg.l());
        reg.l(n);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Shift memory at HL into carry, MSB 0
    Op SRL_HL = new Op(0x3E, "SRL (HL)", cbmap, () -> {
        int n = this.shiftRight0(mmu.rb(reg.hl()));
        mmu.wb(reg.hl(), n);
        
        clock.m(4);
        clock.t(16);
    });
    
    ///
    // Bit manipulations
    ///
    
    //PAGE 108
    
    //Check if bit 0 of register A is set
    Op BIT0_A = new Op(0x47, "BIT 0,A", cbmap, () -> {
        testBit(reg.a(), 0);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Check if bit 0 of register B is set
    Op BIT0_B = new Op(0x40, "BIT 0,B", cbmap, () -> {
        testBit(reg.b(), 0);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Check if bit 0 of register C is set
    Op BIT0_C = new Op(0x41, "BIT 0,C", cbmap, () -> {
        testBit(reg.c(), 0);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Check if bit 0 of register D is set
    Op BIT0_D = new Op(0x42, "BIT 0,D", cbmap, () -> {
        testBit(reg.d(), 0);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Check if bit 0 of register E is set
    Op BIT0_E = new Op(0x43, "BIT 0,E", cbmap, () -> {
        testBit(reg.e(), 0);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Check if bit 0 of register H is set
    Op BIT0_H = new Op(0x44, "BIT 0,H", cbmap, () -> {
        testBit(reg.h(), 0);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Check if bit 0 of register L is set
    Op BIT0_L = new Op(0x45, "BIT 0,L", cbmap, () -> {
        testBit(reg.l(), 0);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Check if bit 0 of memory HL is set
    Op BIT0_HL = new Op(0x46, "BIT 0,(HL)", cbmap, () -> {
        testBit(mmu.rb(reg.hl()), 0);
        
        clock.m(4);
        clock.t(16);
    });
    
    //Check if bit 1 of register A is set
    Op BIT1_A = new Op(0x4F, "BIT 1,A", cbmap, () -> {
        testBit(reg.a(), 1);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Check if bit 1 of register B is set
    Op BIT1_B = new Op(0x48, "BIT 1,B", cbmap, () -> {
        testBit(reg.b(), 1);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Check if bit 1 of register C is set
    Op BIT1_C = new Op(0x49, "BIT 1,C", cbmap, () -> {
        testBit(reg.c(), 1);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Check if bit 1 of register D is set
    Op BIT1_D = new Op(0x4A, "BIT 1,D", cbmap, () -> {
        testBit(reg.d(), 1);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Check if bit 1 of register E is set
    Op BIT1_E = new Op(0x4B, "BIT 1,E", cbmap, () -> {
        testBit(reg.e(), 1);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Check if bit 1 of register H is set
    Op BIT1_H = new Op(0x4C, "BIT 1,H", cbmap, () -> {
        testBit(reg.h(), 1);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Check if bit 1 of register L is set
    Op BIT1_L = new Op(0x4D, "BIT 1,L", cbmap, () -> {
        testBit(reg.l(), 1);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Check if bit 1 of memory HL is set
    Op BIT1_HL = new Op(0x4E, "BIT 1,(HL)", cbmap, () -> {
        testBit(mmu.rb(reg.hl()), 1);
        
        clock.m(4);
        clock.t(16);
    });
    
    //Check if bit 2 of register A is set
    Op BIT2_A = new Op(0x57, "BIT 2,A", cbmap, () -> {
        testBit(reg.a(), 2);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Check if bit 2 of register B is set
    Op BIT2_B = new Op(0x50, "BIT 2,B", cbmap, () -> {
        testBit(reg.b(), 2);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Check if bit 2 of register C is set
    Op BIT2_C = new Op(0x51, "BIT 2,C", cbmap, () -> {
        testBit(reg.c(), 2);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Check if bit 2 of register D is set
    Op BIT2_D = new Op(0x52, "BIT 2,D", cbmap, () -> {
        testBit(reg.d(), 2);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Check if bit 2 of register E is set
    Op BIT2_E = new Op(0x53, "BIT 2,E", cbmap, () -> {
        testBit(reg.e(), 2);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Check if bit 2 of register H is set
    Op BIT2_H = new Op(0x54, "BIT 2,H", cbmap, () -> {
        testBit(reg.h(), 2);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Check if bit 2 of register L is set
    Op BIT2_L = new Op(0x55, "BIT 2,L", cbmap, () -> {
        testBit(reg.l(), 2);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Check if bit 2 of memory HL is set
    Op BIT2_HL = new Op(0x56, "BIT 2,(HL)", cbmap, () -> {
        testBit(mmu.rb(reg.hl()), 2);
        
        clock.m(4);
        clock.t(16);
    });
    
    //Check if bit 3 of register A is set
    Op BIT3_A = new Op(0x5F, "BIT 3,A", cbmap, () -> {
        testBit(reg.a(), 3);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Check if bit 3 of register B is set
    Op BIT3_B = new Op(0x58, "BIT 3,B", cbmap, () -> {
        testBit(reg.b(), 3);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Check if bit 3 of register C is set
    Op BIT3_C = new Op(0x59, "BIT 3,C", cbmap, () -> {
        testBit(reg.c(), 3);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Check if bit 3 of register D is set
    Op BIT3_D = new Op(0x5A, "BIT 3,D", cbmap, () -> {
        testBit(reg.d(), 3);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Check if bit 3 of register E is set
    Op BIT3_E = new Op(0x5B, "BIT 3,E", cbmap, () -> {
        testBit(reg.e(), 3);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Check if bit 3 of register H is set
    Op BIT3_H = new Op(0x5C, "BIT 3,H", cbmap, () -> {
        testBit(reg.h(), 3);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Check if bit 3 of register L is set
    Op BIT3_L = new Op(0x5D, "BIT 3,L", cbmap, () -> {
        testBit(reg.l(), 3);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Check if bit 3 of memory HL is set
    Op BIT3_HL = new Op(0x5E, "BIT 3,(HL)", cbmap, () -> {
        testBit(mmu.rb(reg.hl()), 3);
        
        clock.m(4);
        clock.t(16);
    });
    
    //Check if bit 4 of register A is set
    Op BIT4_A = new Op(0x67, "BIT 4,A", cbmap, () -> {
        testBit(reg.a(), 4);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Check if bit 4 of register B is set
    Op BIT4_B = new Op(0x60, "BIT 4,B", cbmap, () -> {
        testBit(reg.b(), 4);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Check if bit 4 of register C is set
    Op BIT4_C = new Op(0x61, "BIT 4,C", cbmap, () -> {
        testBit(reg.c(), 4);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Check if bit 4 of register D is set
    Op BIT4_D = new Op(0x62, "BIT 4,D", cbmap, () -> {
        testBit(reg.d(), 4);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Check if bit 4 of register E is set
    Op BIT4_E = new Op(0x63, "BIT 4,E", cbmap, () -> {
        testBit(reg.e(), 4);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Check if bit 4 of register H is set
    Op BIT4_H = new Op(0x64, "BIT 4,H", cbmap, () -> {
        testBit(reg.h(), 4);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Check if bit 4 of register L is set
    Op BIT4_L = new Op(0x65, "BIT 4,L", cbmap, () -> {
        testBit(reg.l(), 4);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Check if bit 4 of memory HL is set
    Op BIT4_HL = new Op(0x66, "BIT 4,(HL)", cbmap, () -> {
        testBit(mmu.rb(reg.hl()), 4);

        clock.m(4);
        clock.t(16);
    });
    
    //Check if bit 5 of register A is set
    Op BIT5_A = new Op(0x6F, "BIT 5,A", cbmap, () -> {
        testBit(reg.a(), 5);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Check if bit 5 of register B is set
    Op BIT5_B = new Op(0x68, "BIT 5,B", cbmap, () -> {
        testBit(reg.b(), 5);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Check if bit 5 of register C is set
    Op BIT5_C = new Op(0x69, "BIT 5,C", cbmap, () -> {
        testBit(reg.c(), 5);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Check if bit 5 of register D is set
    Op BIT5_D = new Op(0x6A, "BIT 5,D", cbmap, () -> {
        testBit(reg.d(), 5);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Check if bit 5 of register E is set
    Op BIT5_E = new Op(0x6B, "BIT 5,E", cbmap, () -> {
        testBit(reg.e(), 5);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Check if bit 5 of register H is set
    Op BIT5_H = new Op(0x6C, "BIT 5,H", cbmap, () -> {
        testBit(reg.h(), 5);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Check if bit 5 of register L is set
    Op BIT5_L = new Op(0x6D, "BIT 5,L", cbmap, () -> {
        testBit(reg.l(), 5);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Check if bit 5 of memory HL is set
    Op BIT5_HL = new Op(0x6E, "BIT 5,(HL)", cbmap, () -> {
        testBit(mmu.rb(reg.hl()), 5);
        
        clock.m(4);
        clock.t(16);
    });
    
    //Check if bit 6 of register A is set
    Op BIT6_A = new Op(0x77, "BIT 6,A", cbmap, () -> {
        testBit(reg.a(), 6);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Check if bit 6 of register B is set
    Op BIT6_B = new Op(0x70, "BIT 6,B", cbmap, () -> {
        testBit(reg.b(), 6);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Check if bit 6 of register C is set
    Op BIT6_C = new Op(0x71, "BIT 6,C", cbmap, () -> {
        testBit(reg.c(), 6);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Check if bit 6 of register D is set
    Op BIT6_D = new Op(0x72, "BIT 6,D", cbmap, () -> {
        testBit(reg.d(), 6);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Check if bit 6 of register E is set
    Op BIT6_E = new Op(0x73, "BIT 6,E", cbmap, () -> {
        testBit(reg.e(), 6);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Check if bit 6 of register H is set
    Op BIT6_H = new Op(0x74, "BIT 6,H", cbmap, () -> {
        testBit(reg.h(), 6);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Check if bit 6 of register L is set
    Op BIT6_L = new Op(0x75, "BIT 6,L", cbmap, () -> {
        testBit(reg.l(), 6);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Check if bit 6 of memory HL is set
    Op BIT6_HL = new Op(0x76, "BIT 6,(HL)", cbmap, () -> {
        testBit(mmu.rb(reg.hl()), 6);

        clock.m(4);
        clock.t(16);
    });
    
    //Check if bit 7 of register A is set
    Op BIT7_A = new Op(0x7F, "BIT 7,A", cbmap, () -> {
        testBit(reg.a(), 7);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Check if bit 7 of register B is set
    Op BIT7_B = new Op(0x78, "BIT 7,B", cbmap, () -> {
        testBit(reg.b(), 7);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Check if bit 7 of register C is set
    Op BIT7_C = new Op(0x79, "BIT 7,C", cbmap, () -> {
        testBit(reg.c(), 7);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Check if bit 7 of register D is set
    Op BIT7_D = new Op(0x7A, "BIT 7,D", cbmap, () -> {
        testBit(reg.d(), 7);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Check if bit 7 of register E is set
    Op BIT7_E = new Op(0x7B, "BIT 7,E", cbmap, () -> {
        testBit(reg.e(), 7);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Check if bit 7 of register H is set
    Op BIT7_H = new Op(0x7C, "BIT 7,H", cbmap, () -> {
        testBit(reg.h(), 7);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Check if bit 7 of register L is set
    Op BIT7_L = new Op(0x7D, "BIT 7,L", cbmap, () -> {
        testBit(reg.l(), 7);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Check if bit 7 of memory HL is set
    Op BIT7_HL = new Op(0x7E, "BIT 7,(HL)", cbmap, () -> {
        testBit(mmu.rb(reg.hl()), 7);
        
        clock.m(4);
        clock.t(16);
    });
    
    //Set bit b in register r
    Op SET0_A = new Op(0xC7, "SET 0,A", cbmap, () -> {
        reg.a(setBit(reg.a(), 0));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Set bit b in register r
    Op SET0_B = new Op(0xC0, "SET 0,B", cbmap, () -> {
        reg.b(setBit(reg.b(), 0));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Set bit b in register r
    Op SET0_C = new Op(0xC1, "SET 0,C", cbmap, () -> {
        reg.c(setBit(reg.c(), 0));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Set bit b in register r
    Op SET0_D = new Op(0xC2, "SET 0,D", cbmap, () -> {
        reg.d(setBit(reg.d(), 0));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Set bit b in register r
    Op SET0_E = new Op(0xC3, "SET 0,E", cbmap, () -> {
        reg.e(setBit(reg.e(), 0));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Set bit b in register r
    Op SET0_H = new Op(0xC4, "SET 0,H", cbmap, () -> {
        reg.h(setBit(reg.h(), 0));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Set bit b in register r
    Op SET0_L = new Op(0xC5, "SET 0,L", cbmap, () -> {
        reg.l(setBit(reg.l(), 0));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Set bit b in register r
    Op SET0_HL = new Op(0xC6, "SET 0,(HL)", cbmap, () -> {
        mmu.wb( reg.hl(), setBit(mmu.rb(reg.hl()), 0));
        
        clock.m(4);
        clock.t(16);
    });
    
    //Set bit b in register r
    Op SET1_A = new Op(0xCF, "SET 1,A", cbmap, () -> {
        reg.a(setBit(reg.a(), 1));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Set bit b in register r
    Op SET1_B = new Op(0xC8, "SET 1,B", cbmap, () -> {
        reg.b(setBit(reg.b(), 1));
        
        clock.m(2);
        clock.t(8);
    });
   
    //Set bit b in register r
    Op SET1_C = new Op(0xC9, "SET 1,C", cbmap, () -> {
        reg.c(setBit(reg.c(), 1));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Set bit b in register r
    Op SET1_D = new Op(0xCA, "SET 1,D", cbmap, () -> {
        reg.d(setBit(reg.d(), 1));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Set bit b in register r
    Op SET1_E = new Op(0xCB, "SET 1,E", cbmap, () -> {
        reg.e(setBit(reg.e(), 1));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Set bit b in register r
    Op SET1_H = new Op(0xCC, "SET 1,H", cbmap, () -> {
        reg.h(setBit(reg.h(), 1));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Set bit b in register r
    Op SET1_L = new Op(0xCD, "SET 1,L", cbmap, () -> {
        reg.l(setBit(reg.l(), 1));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Set bit b in register r
    Op SET1_HL = new Op(0xCE, "SET 1,(HL)", cbmap, () -> {
        mmu.wb( reg.hl(), setBit(mmu.rb(reg.hl()), 1));
        
        clock.m(4);
        clock.t(16);
    });
    
    //Set bit b in register r
    Op SET2_A = new Op(0xD7, "SET 2,A", cbmap, () -> {
        reg.a(setBit(reg.a(), 2));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Set bit b in register r
    Op SET2_B = new Op(0xD0, "SET 2,B", cbmap, () -> {
        reg.b(setBit(reg.b(), 2));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Set bit b in register r
    Op SET2_C = new Op(0xD1, "SET 2,C", cbmap, () -> {
        reg.c(setBit(reg.c(), 2));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Set bit b in register r
    Op SET2_D = new Op(0xD2, "SET 2,D", cbmap, () -> {
        reg.d(setBit(reg.d(), 2));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Set bit b in register r
    Op SET2_E = new Op(0xD3, "SET 2,E", cbmap, () -> {
        reg.e(setBit(reg.e(), 2));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Set bit b in register r
    Op SET2_H = new Op(0xD4, "SET 2,H", cbmap, () -> {
        reg.h(setBit(reg.h(), 2));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Set bit b in register r
    Op SET2_L = new Op(0xD5, "SET 2,L", cbmap, () -> {
        reg.l(setBit(reg.l(), 2));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Set bit b in register r
    Op SET2_HL = new Op(0xD6, "SET 2,(HL)", cbmap, () -> {
        mmu.wb( reg.hl(), setBit(mmu.rb(reg.hl()), 2));
        
        clock.m(4);
        clock.t(16);
    });
    
    //Set bit b in register r
    Op SET3_A = new Op(0xDF, "SET 3,A", cbmap, () -> {
        reg.a( setBit(reg.a(), 3));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Set bit b in register r
    Op SET3_B = new Op(0xD8, "SET 3,B", cbmap, () -> {
        reg.b(setBit(reg.b(), 3));
        
        clock.m(2);
        clock.t(8);
    });
   
    //Set bit b in register r
    Op SET3_C = new Op(0xD9, "SET 3,C", cbmap, () -> {
        reg.c(setBit(reg.c(), 3));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Set bit b in register r
    Op SET3_D = new Op(0xDA, "SET 3,D", cbmap, () -> {
        reg.d( setBit(reg.d(), 3));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Set bit b in register r
    Op SET3_E = new Op(0xDB, "SET 3,E", cbmap, () -> {
        reg.e(setBit(reg.e(), 3));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Set bit b in register r
    Op SET3_H = new Op(0xDC, "SET 3,H", cbmap, () -> {
        reg.h(setBit(reg.h(), 3));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Set bit b in register r
    Op SET3_L = new Op(0xDD, "SET 3,L", cbmap, () -> {
        reg.l(setBit(reg.l(), 3));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Set bit b in register r
    Op SET3_HL = new Op(0xDE, "SET 3,(HL)", cbmap, () -> {
        mmu.wb( reg.hl(), setBit(mmu.rb(reg.hl()), 3));
        
        clock.m(4);
        clock.t(16);
    });
    
    //Set bit b in register r
    Op SET4_A = new Op(0xE7, "SET 4,A", cbmap, () -> {
        reg.a(setBit(reg.a(), 4));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Set bit b in register r
    Op SET4_B = new Op(0xE0, "SET 4,B", cbmap, () -> {
        reg.b(setBit(reg.b(), 4));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Set bit b in register r
    Op SET4_C = new Op(0xE1, "SET 4,C", cbmap, () -> {
        reg.c(setBit(reg.c(), 4));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Set bit b in register r
    Op SET4_D = new Op(0xE2, "SET 4,D", cbmap, () -> {
        reg.d(setBit(reg.d(), 4));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Set bit b in register r
    Op SET4_E = new Op(0xE3, "SET 4,E", cbmap, () -> {
        reg.e(setBit(reg.e(), 4));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Set bit b in register r
    Op SET4_H = new Op(0xE4, "SET 4,H", cbmap, () -> {
        reg.h(setBit(reg.h(), 4));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Set bit b in register r
    Op SET4_L = new Op(0xE5, "SET 4,L", cbmap, () -> {
        reg.l(setBit(reg.l(), 4));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Set bit b in register r
    Op SET4_HL = new Op(0xE6, "SET 4,(HL)", cbmap, () -> {
        mmu.wb( reg.hl(), setBit(mmu.rb(reg.hl()), 4));
        
        clock.m(4);
        clock.t(16);
    });
    
    //Set bit b in register r
    Op SET5_A = new Op(0xEF, "SET 5,A", cbmap, () -> {
        reg.a(setBit(reg.a(), 5));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Set bit b in register r
    Op SET5_B = new Op(0xE8, "SET 5,B", cbmap, () -> {
        reg.b( setBit(reg.b(), 5));
        
        clock.m(2);
        clock.t(8);
    });
   
    //Set bit b in register r
    Op SET5_C = new Op(0xE9, "SET 5,C", cbmap, () -> {
        reg.c(setBit(reg.c(), 5));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Set bit b in register r
    Op SET5_D = new Op(0xEA, "SET 5,D", cbmap, () -> {
        reg.d(setBit(reg.d(), 5));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Set bit b in register r
    Op SET5_E = new Op(0xEB, "SET 5,E", cbmap, () -> {
        reg.e(setBit(reg.e(), 5));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Set bit b in register r
    Op SET5_H = new Op(0xEC, "SET 5,H", cbmap, () -> {
        reg.h( setBit(reg.h(), 5));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Set bit b in register r
    Op SET5_L = new Op(0xED, "SET 5,L", cbmap, () -> {
        reg.l(setBit(reg.l(), 5));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Set bit b in register r
    Op SET5_HL = new Op(0xEE, "SET 5,(HL)", cbmap, () -> {
        mmu.wb( reg.hl(), setBit(mmu.rb(reg.hl()), 5));
        
        clock.m(4);
        clock.t(16);
    });
    
    //Set bit b in register r
    Op SET6_A = new Op(0xF7, "SET 6,A", cbmap, () -> {
        reg.a(setBit(reg.a(), 6));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Set bit b in register r
    Op SET6_B = new Op(0xF0, "SET 6,B", cbmap, () -> {
        reg.b(setBit(reg.b(), 6));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Set bit b in register r
    Op SET6_C = new Op(0xF1, "SET 6,C", cbmap, () -> {
        reg.c(setBit(reg.c(), 6));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Set bit b in register r
    Op SET6_D = new Op(0xF2, "SET 6,D", cbmap, () -> {
        reg.d(setBit(reg.d(), 6));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Set bit b in register r
    Op SET6_E = new Op(0xF3, "SET 6,E", cbmap, () -> {
        reg.e(setBit(reg.e(), 6));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Set bit b in register r
    Op SET6_H = new Op(0xF4, "SET 6,H", cbmap, () -> {
        reg.h(setBit(reg.h(), 6));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Set bit b in register r
    Op SET6_L = new Op(0xF5, "SET 6,L", cbmap, () -> {
        reg.l(setBit(reg.l(), 6));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Set bit b in register r
    Op SET6_HL = new Op(0xF6, "SET 6,(HL)", cbmap, () -> {
        mmu.wb( reg.hl(), setBit(mmu.rb(reg.hl()), 6));
        
        clock.m(4);
        clock.t(16);
    });
    
    //Set bit b in register r
    Op SET7_A = new Op(0xFF, "SET 7,A", cbmap, () -> {
        reg.a(setBit(reg.a(), 7));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Set bit b in register r
    Op SET7_B = new Op(0xF8, "SET 7,B", cbmap, () -> {
        reg.b(setBit(reg.b(), 7));
        
        clock.m(2);
        clock.t(8);
    });
   
    //Set bit b in register r
    Op SET7_C = new Op(0xF9, "SET 7,C", cbmap, () -> {
        reg.c(setBit(reg.c(), 7));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Set bit b in register r
    Op SET7_D = new Op(0xFA, "SET 7,D", cbmap, () -> {
        reg.d(setBit(reg.d(), 7));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Set bit b in register r
    Op SET7_E = new Op(0xFB, "SET 7,E", cbmap, () -> {
        reg.e(setBit(reg.e(), 7));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Set bit b in register r
    Op SET7_H = new Op(0xFC, "SET 7,H", cbmap, () -> {
        reg.h(setBit(reg.h(), 7));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Set bit b in register r
    Op SET7_L = new Op(0xFD, "SET 7,L", cbmap, () -> {
        reg.l( setBit(reg.l(), 7));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Set bit b in register r
    Op SET7_HL = new Op(0xFE, "SET 7,(HL)", cbmap, () -> {
        mmu.wb( reg.hl(), setBit(mmu.rb(reg.hl()), 7));
        
        clock.m(4);
        clock.t(16);
    });
    
    
    //Reset the bit b in register r
    Op RES0_A = new Op(0x87, "RES 0,A", cbmap, () -> {
        //Reset value
        reg.a(resetBit(reg.a(), 0));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Reset the bit b in register r
    Op RES0_B = new Op(0x80, "RES 0,B", cbmap, () -> {
        //Reset value
        reg.b(resetBit(reg.b(), 0));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Reset the bit b in register r
    Op RES0_C = new Op(0x81, "RES 0,C", cbmap, () -> {
        //Reset value
        reg.c(resetBit(reg.c(), 0));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Reset the bit b in register r
    Op RES0_D = new Op(0x82, "RES 0,D", cbmap, () -> {
        //Reset value
        reg.d(resetBit(reg.d(), 0));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Reset the bit b in register r
    Op RES0_E = new Op(0x83, "RES 0,E", cbmap, () -> {
        //Reset value
        reg.e(resetBit(reg.e(), 0));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Reset the bit b in register r
    Op RES0_H = new Op(0x84, "RES 0,H", cbmap, () -> {
        //Reset value
        reg.h(resetBit(reg.h(), 0));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Reset the bit b in register r
    Op RES0_L = new Op(0x85, "RES 0,L", cbmap, () -> {
        //Reset value
        reg.l(resetBit(reg.l(), 0));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Reset the bit b in register r
    Op RES0_HL = new Op(0x86, "RES 0,(HL)", cbmap, () -> {
        //Reset value
        mmu.wb( reg.hl(), resetBit(mmu.rb(reg.hl()), 0) );
        
        clock.m(4);
        clock.t(16);
    });
    
    //Reset the bit b in register r
    Op RES1_A = new Op(0x8F, "RES 1,A", cbmap, () -> {
        //Reset value
        reg.a( resetBit(reg.a(), 1));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Reset the bit b in register r
    Op RES1_B = new Op(0x88, "RES 1,B", cbmap, () -> {
        //Reset value
        reg.b( resetBit(reg.b(), 1));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Reset the bit b in register r
    Op RES1_C = new Op(0x89, "RES 1,C", cbmap, () -> {
        //Reset value
        reg.c(resetBit(reg.c(), 1));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Reset the bit b in register r
    Op RES1_D = new Op(0x8A, "RES 1,D", cbmap, () -> {
        //Reset value
        reg.d(resetBit(reg.d(), 1));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Reset the bit b in register r
    Op RES1_E = new Op(0x8B, "RES 1,E", cbmap, () -> {
        //Reset value
        reg.e( resetBit(reg.e(), 1));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Reset the bit b in register r
    Op RES1_H = new Op(0x8C, "RES 1,H", cbmap, () -> {
        //Reset value
        reg.h(resetBit(reg.h(), 1));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Reset the bit b in register r
    Op RES1_L = new Op(0x8D, "RES 1,L", cbmap, () -> {
        //Reset value
        reg.l(resetBit(reg.l(), 1));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Reset the bit b in register r
    Op RES1_HL = new Op(0x8E, "RES 1,(HL)", cbmap, () -> {
        //Reset value
        mmu.wb( reg.hl(), resetBit(mmu.rb(reg.hl()), 1));
        
        clock.m(4);
        clock.t(16);
    });
    
    //Reset the bit b in register r
    Op RES2_A = new Op(0x97, "RES 2,A", cbmap, () -> {
        //Reset value
        reg.a(resetBit(reg.a(), 2));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Reset the bit b in register r
    Op RES2_B = new Op(0x90, "RES 2,B", cbmap, () -> {
        //Reset value
        reg.b(resetBit(reg.b(), 2));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Reset the bit b in register r
    Op RES2_C = new Op(0x91, "RES 2,C", cbmap, () -> {
        //Reset value
        reg.c( resetBit(reg.c(), 2));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Reset the bit b in register r
    Op RES2_D = new Op(0x92, "RES 2,D", cbmap, () -> {
        //Reset value
        reg.d(resetBit(reg.d(), 2));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Reset the bit b in register r
    Op RES2_E = new Op(0x93, "RES 2,E", cbmap, () -> {
        //Reset value
        reg.e(resetBit(reg.e(), 2));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Reset the bit b in register r
    Op RES2_H = new Op(0x94, "RES 2,H", cbmap, () -> {
        //Reset value
        reg.h(resetBit(reg.h(), 2));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Reset the bit b in register r
    Op RES2_L = new Op(0x95, "RES 2,L", cbmap, () -> {
        //Reset value
        reg.l(resetBit(reg.l(), 2));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Reset the bit b in register r
    Op RES2_HL = new Op(0x96, "RES 2,(HL)", cbmap, () -> {
        //Reset value
        mmu.wb( reg.hl(), resetBit(mmu.rb(reg.hl()), 2));
        
        clock.m(4);
        clock.t(16);
    });
    
    //Reset the bit b in register r
    Op RES3_A = new Op(0x9F, "RES 3,A", cbmap, () -> {
        //Reset value
        reg.a(resetBit(reg.a(), 3));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Reset the bit b in register r
    Op RES3_B = new Op(0x98, "RES 3,B", cbmap, () -> {
        //Reset value
        reg.b( resetBit(reg.b(), 3) );
        
        clock.m(2);
        clock.t(8);
    });
    
    //Reset the bit b in register r
    Op RES3_C = new Op(0x99, "RES 3,C", cbmap, () -> {
        //Reset value
        reg.c( resetBit(reg.c(), 3) );
        
        clock.m(2);
        clock.t(8);
    });
    
    //Reset the bit b in register r
    Op RES3_D = new Op(0x9A, "RES 3,D", cbmap, () -> {
        //Reset value
        reg.d( resetBit(reg.d(), 3) );
        
        clock.m(2);
        clock.t(8);
    });
    
    //Reset the bit b in register r
    Op RES3_E = new Op(0x9B, "RES 3,E", cbmap, () -> {
        //Reset value
        reg.e( resetBit(reg.e(), 3) );
        
        clock.m(2);
        clock.t(8);
    });
    
    //Reset the bit b in register r
    Op RES3_H = new Op(0x9C, "RES 3,H", cbmap, () -> {
        //Reset value
        reg.h( resetBit(reg.h(), 3) );
        
        clock.m(2);
        clock.t(8);
    });
    
    //Reset the bit b in register r
    Op RES3_L = new Op(0x9D, "RES 3,L", cbmap, () -> {
        //Reset value
        reg.l( resetBit(reg.l(), 3) );
        
        clock.m(2);
        clock.t(8);
    });
    
    //Reset the bit b in register r
    Op RES3_HL = new Op(0x9E, "RES 3,(HL)", cbmap, () -> {
        //Reset value
        mmu.wb( reg.hl(), resetBit(mmu.rb(reg.hl()), 3));
        
        clock.m(4);
        clock.t(16);
    });
    
    //Reset the bit b in register r
    Op RES4_A = new Op(0xA7, "RES 4,A", cbmap, () -> {
        //Reset value
        reg.a( resetBit(reg.a(), 4) );
        
        clock.m(2);
        clock.t(8);
    });
    
    //Reset the bit b in register r
    Op RES4_B = new Op(0xA0, "RES 4,B", cbmap, () -> {
        //Reset value
        reg.b( resetBit(reg.b(), 4) );
        
        clock.m(2);
        clock.t(8);
    });
    
    //Reset the bit b in register r
    Op RES4_C = new Op(0xA1, "RES 4,C", cbmap, () -> {
        //Reset value
        reg.c( resetBit(reg.c(), 4) );
        
        clock.m(2);
        clock.t(8);
    });
    
    //Reset the bit b in register r
    Op RES4_D = new Op(0xA2, "RES 4,D", cbmap, () -> {
        //Reset value
        reg.d( resetBit(reg.d(), 4) );
        
        clock.m(2);
        clock.t(8);
    });
    
    //Reset the bit b in register r
    Op RES4_E = new Op(0xA3, "RES 4,E", cbmap, () -> {
        //Reset value
        reg.e( resetBit(reg.e(), 4) );
        
        clock.m(2);
        clock.t(8);
    });
    
    //Reset the bit b in register r
    Op RES4_H = new Op(0xA4, "RES 4,H", cbmap, () -> {
        //Reset value
        reg.h( resetBit(reg.h(), 4) );
        
        clock.m(2);
        clock.t(8);
    });
    
    //Reset the bit b in register r
    Op RES4_L = new Op(0xA5, "RES 4,L", cbmap, () -> {
        //Reset value
        reg.l( resetBit(reg.l(), 4) );
        
        clock.m(2);
        clock.t(8);
    });
    
    //Reset the bit b in register r
    Op RES4_HL = new Op(0xA6, "RES 4,(HL)", cbmap, () -> {
        //Reset value
        mmu.wb( reg.hl(), resetBit(mmu.rb(reg.hl()), 4) );
        
        clock.m(4);
        clock.t(16);
    });
    
    //Reset the bit b in register r
    Op RES5_A = new Op(0xAF, "RES 5,A", cbmap, () -> {
        //Reset value
        reg.a( resetBit(reg.a(), 5) );
        
        clock.m(2);
        clock.t(8);
    });
    
    //Reset the bit b in register r
    Op RES5_B = new Op(0xA8, "RES 5,B", cbmap, () -> {
        //Reset value
        reg.b( resetBit(reg.b(), 5) );
        
        clock.m(2);
        clock.t(8);
    });
    
    //Reset the bit b in register r
    Op RES5_C = new Op(0xA9, "RES 5,C", cbmap, () -> {
        //Reset value
        reg.c( resetBit(reg.c(), 5) );
        
        clock.m(2);
        clock.t(8);
    });
    
    //Reset the bit b in register r
    Op RES5_D = new Op(0xAA, "RES 5,D", cbmap, () -> {
        //Reset value
        reg.d( resetBit(reg.d(), 5) );
        
        clock.m(2);
        clock.t(8);
    });
    
    //Reset the bit b in register r
    Op RES5_E = new Op(0xAB, "RES 5,E", cbmap, () -> {
        //Reset value
        reg.e( resetBit(reg.e(), 5) );
        
        clock.m(2);
        clock.t(8);
    });
    
    //Reset the bit b in register r
    Op RES5_H = new Op(0xAC, "RES 5,H", cbmap, () -> {
        //Reset value
        reg.h( resetBit(reg.h(), 5) );
        
        clock.m(2);
        clock.t(8);
    });
    
    //Reset the bit b in register r
    Op RES5_L = new Op(0xAD, "RES 5,L", cbmap, () -> {
        //Reset value
        reg.l( resetBit(reg.l(), 5) );
        
        clock.m(2);
        clock.t(8);
    });
    
    //Reset the bit b in register r
    Op RES5_HL = new Op(0xAE, "RES 5,(HL)", cbmap, () -> {
        //Reset value
        mmu.wb( reg.hl(), resetBit(mmu.rb(reg.hl()), 5) );
        
        clock.m(4);
        clock.t(16);
    });
    
    //Reset the bit b in register r
    Op RES6_A = new Op(0xB7, "RES 6,A", cbmap, () -> {
        //Reset value
        reg.a( resetBit(reg.a(), 6) );
        
        clock.m(2);
        clock.t(8);
    });
    
    //Reset the bit b in register r
    Op RES6_B = new Op(0xB0, "RES 6,B", cbmap, () -> {
        //Reset value
        reg.b( resetBit(reg.b(), 6) );
        
        clock.m(2);
        clock.t(8);
    });
    
    //Reset the bit b in register r
    Op RES6_C = new Op(0xB1, "RES 6,C", cbmap, () -> {
        //Reset value
        reg.c( resetBit(reg.c(), 6) );
        
        clock.m(2);
        clock.t(8);
    });
    
    //Reset the bit b in register r
    Op RES6_D = new Op(0xB2, "RES 6,D", cbmap, () -> {
        //Reset value
        reg.d( resetBit(reg.d(), 6) );
        
        clock.m(2);
        clock.t(8);
    });
    
    //Reset the bit b in register r
    Op RES6_E = new Op(0xB3, "RES 6,E", cbmap, () -> {
        //Reset value
        reg.e( resetBit(reg.e(), 6) );
        
        clock.m(2);
        clock.t(8);
    });
    
    //Reset the bit b in register r
    Op RES6_H = new Op(0xB4, "RES 6,H", cbmap, () -> {
        //Reset value
        reg.h( resetBit(reg.h(), 6) );
        
        clock.m(2);
        clock.t(8);
    });
    
    //Reset the bit b in register r
    Op RES6_L = new Op(0xB5, "RES 6,L", cbmap, () -> {
        //Reset value
        reg.l( resetBit(reg.l(), 6) );
        
        clock.m(2);
        clock.t(8);
    });
    
    //Reset the bit b in register r
    Op RES6_HL = new Op(0xB6, "RES 6,(HL)", cbmap, () -> {
        //Reset value
        mmu.wb( reg.hl(), resetBit(mmu.rb(reg.hl()), 6) );
        
        clock.m(4);
        clock.t(16);
    });
    
    //Reset the bit b in register r
    Op RES7_A = new Op(0xBF, "RES 7,A", cbmap, () -> {
        //Reset value
        reg.a( resetBit(reg.a(), 7) );
        
        clock.m(2);
        clock.t(8);
    });
    
    //Reset the bit b in register r
    Op RES7_B = new Op(0xB8, "RES 7,B", cbmap, () -> {
        //Reset value
        reg.b( resetBit(reg.b(), 7) );
        
        clock.m(2);
        clock.t(8);
    });
    
    //Reset the bit b in register r
    Op RES7_C = new Op(0xB9, "RES 7,C", cbmap, () -> {
        //Reset value
        reg.c( resetBit(reg.c(), 7) );
        
        clock.m(2);
        clock.t(8);
    });
    
    //Reset the bit b in register r
    Op RES7_D = new Op(0xBA, "RES 7,D", cbmap, () -> {
        //Reset value
        reg.d( resetBit(reg.d(), 7) );
        
        clock.m(2);
        clock.t(8);
    });
    
    //Reset the bit b in register r
    Op RES7_E = new Op(0xBB, "RES 7,E", cbmap, () -> {
        //Reset value
        reg.e( resetBit(reg.e(), 7) );
        
        clock.m(2);
        clock.t(8);
    });
    
    //Reset the bit b in register r
    Op RES7_H = new Op(0xBC, "RES 7,H", cbmap, () -> {
        //Reset value
        reg.h( resetBit(reg.h(), 7) );
        
        clock.m(2);
        clock.t(8);
    });
    
    //Reset the bit b in register r
    Op RES7_L = new Op(0xBD, "RES 7,L", cbmap, () -> {
        //Reset value
        reg.l( resetBit(reg.l(), 7) );
        
        clock.m(2);
        clock.t(8);
    });
    
    //Reset the bit b in register r
    Op RES7_HL = new Op(0xBE, "RES 7,(HL)", cbmap, () -> {
        //Reset value
        mmu.wb( reg.hl(), resetBit(mmu.rb(reg.hl()), 7) );
        
        clock.m(4);
        clock.t(16);
    });
}



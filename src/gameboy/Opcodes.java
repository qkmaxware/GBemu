/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameboy;

/**
 *
 * @author Colin
 */
public class Opcodes {
 
    //https://github.com/tomdalling/gemuboi/blob/master/source/cpu.hpp
    
    private Cpu cpu;
    private Registry reg;
    private Clock clock;
    private MemoryMap mmu;
    
    private Op[] map = new Op[255];
    private Op[] cmap = new Op[255];
    
    public Opcodes(Cpu cpu){
        this.cpu = cpu;
        this.reg = cpu.reg;
        this.clock = cpu.clock;
        this.mmu = cpu.GetMmu();
        
        int count = 0;
        for(int i = 0 ; i < map.length; i++){
            if(map[i] == null)
                count++;
        }
        
        System.out.println(count+" base opcodes undefined");
        /*
        map = new Op[]{
            NOP, LDBCnn, LDBCmA, INCBC,
            INCr_b, DECr_b, LDrn_b, RLCA,
            LDmmSP, ADDHLBC, LDABCm, DECBC,
            INCr_c, DECr_c, LDrn_c, RRCA,
            // 10
            DJNZn, LDDEnn, LDDEmA, INCDE,
            INCr_d, DECr_d, LDrn_d, RLA,
            JRn, ADDHLDE, LDADEm, DECDE,
            INCr_e, DECr_e, LDrn_e, RRA,
            // 20
            JRNZn, LDHLnn, LDHLIA, INCHL,
            INCr_h, DECr_h, LDrn_h, XX, //XX is DAA
            JRZn, ADDHLHL, LDAHLI, DECHL,
            INCr_l, DECr_l, LDrn_l, CPL,
            // 30
            JRNCn, LDSPnn, LDHLDA, INCSP,
            INCHLm, DECHLm, LDHLmn, SCF,
            JRCn, ADDHLSP, LDAHLD, DECSP,
            INCr_a, DECr_a, LDrn_a, CCF,
            // 40
            LDrr_bb, LDrr_bc, LDrr_bd, LDrr_be,
            LDrr_bh, LDrr_bl, LDrHLm_b, LDrr_ba,
            LDrr_cb, LDrr_cc, LDrr_cd, LDrr_ce,
            LDrr_ch, LDrr_cl, LDrHLm_c, LDrr_ca,
            // 50
            LDrr_db, LDrr_dc, LDrr_dd, LDrr_de,
            LDrr_dh, LDrr_dl, LDrHLm_d, LDrr_da,
            LDrr_eb, LDrr_ec, LDrr_ed, LDrr_ee,
            LDrr_eh, LDrr_el, LDrHLm_e, LDrr_ea,
            // 60
            LDrr_hb, LDrr_hc, LDrr_hd, LDrr_he,
            LDrr_hh, LDrr_hl, LDrHLm_h, LDrr_ha,
            LDrr_lb, LDrr_lc, LDrr_ld, LDrr_le,
            LDrr_lh, LDrr_ll, LDrHLm_l, LDrr_la,
            // 70
            LDHLmr_b, LDHLmr_c, LDHLmr_d, LDHLmr_e,
            LDHLmr_h, LDHLmr_l, HALT, LDHLmr_a,
            LDrr_ab, LDrr_ac, LDrr_ad, LDrr_ae,
            LDrr_ah, LDrr_al, LDrHLm_a, LDrr_aa,
            // 80
            ADDr_b, ADDr_c, ADDr_d, ADDr_e,
            ADDr_h, ADDr_l, ADDHL, ADDr_a,
            ADCr_b, ADCr_c, ADCr_d, ADCr_e,
            ADCr_h, ADCr_l, ADCHL, ADCr_a,
            // 90
            SUBr_b, SUBr_c, SUBr_d, SUBr_e,
            SUBr_h, SUBr_l, SUBHL, SUBr_a,
            SBCr_b, SBCr_c, SBCr_d, SBCr_e,
            SBCr_h, SBCr_l, SBCHL, SBCr_a,
            // A0
            ANDr_b, ANDr_c, ANDr_d, ANDr_e,
            ANDr_h, ANDr_l, ANDHL, ANDr_a,
            XORr_b, XORr_c, XORr_d, XORr_e,
            XORr_h, XORr_l, XORHL, XORr_a,
            // B0
            ORr_b, ORr_c, ORr_d, ORr_e,
            ORr_h, ORr_l, ORHL, ORr_a,
            CPr_b, CPr_c, CPr_d, CPr_e,
            CPr_h, CPr_l, CPHL, CPr_a,
            // C0
            RETNZ, POPBC, JPNZnn, JPnn,
            CALLNZnn, PUSHBC, ADDn, RST00,
            RETZ, RET, JPZnn, MAPcb,
            CALLZnn, CALLnn, ADCn, RST08,
            // D0
            RETNC, POPDE, JPNCnn, XX,
            CALLNCnn, PUSHDE, SUBn, RST10,
            RETC, RETI, JPCnn, XX,
            CALLCnn, XX, SBCn, RST18,
            // E0
            LDIOnA, POPHL, LDIOCA, XX,
            XX, PUSHHL, ANDn, RST20,
            ADDSPn, JPHL, LDmmA, XX,
            XX, XX, XORn, RST28,
            // F0
            LDAIOn, POPAF, LDAIOC, DI,
            XX, PUSHAF, ORn, RST30,
            LDHLSPn, XX, LDAmm, EI,
            XX, XX, CPn, RST38
        };
        
        cmap = new Op[]{
            // CB00
            RLCr_b, RLCr_c, RLCr_d, RLCr_e,
            RLCr_h, RLCr_l, RLCHL, RLCr_a,
            RRCr_b, RRCr_c, RRCr_d, RRCr_e,
            RRCr_h, RRCr_l, RRCHL, RRCr_a,
            // CB10
            RLr_b, RLr_c, RLr_d, RLr_e,
            RLr_h, RLr_l, RLHL, RLr_a,
            RRr_b, RRr_c, RRr_d, RRr_e,
            RRr_h, RRr_l, RRHL, RRr_a,
            // CB20
            SLAr_b, SLAr_c, SLAr_d, SLAr_e,
            SLAr_h, SLAr_l, XX, SLAr_a,
            SRAr_b, SRAr_c, SRAr_d, SRAr_e,
            SRAr_h, SRAr_l, XX, SRAr_a,
            // CB30
            SWAPr_b, SWAPr_c, SWAPr_d, SWAPr_e,
            SWAPr_h, SWAPr_l, XX, SWAPr_a,
            SRLr_b, SRLr_c, SRLr_d, SRLr_e,
            SRLr_h, SRLr_l, XX, SRLr_a,
            // CB40
            BIT0b, BIT0c, BIT0d, BIT0e,
            BIT0h, BIT0l, BIT0m, BIT0a,
            BIT1b, BIT1c, BIT1d, BIT1e,
            BIT1h, BIT1l, BIT1m, BIT1a,
            // CB50
            BIT2b, BIT2c, BIT2d, BIT2e,
            BIT2h, BIT2l, BIT2m, BIT2a,
            BIT3b, BIT3c, BIT3d, BIT3e,
            BIT3h, BIT3l, BIT3m, BIT3a,
            // CB60
            BIT4b, BIT4c, BIT4d, BIT4e,
            BIT4h, BIT4l, BIT4m, BIT4a,
            BIT5b, BIT5c, BIT5d, BIT5e,
            BIT5h, BIT5l, BIT5m, BIT5a,
            // CB70
            BIT6b, BIT6c, BIT6d, BIT6e,
            BIT6h, BIT6l, BIT6m, BIT6a,
            BIT7b, BIT7c, BIT7d, BIT7e,
            BIT7h, BIT7l, BIT7m, BIT7a,
        };*/
        
    }
    
    public boolean isValidOpcode(int opcode){
        if(this.map == null){
            return false;
        }
            //throw new RuntimeException("Opcodes have not been initialized");
        if(opcode < 0 || opcode >= this.map.length){
            //throw new RuntimeException("Opcode ("+opcode+")is not valid ");
            return false;
        }
        //if(this.map[opcode]] == XX){
            //System.out.println("Opcode "+opcode+" is not implemented");
        //}
        return true;
    }
    
    public Op Fetch(int opcode){
        if(!isValidOpcode(opcode))
            return null;
        Op op = this.map[opcode];
        return op; 
    }
    
    
    //--------------------------------------------------------------------------
    // Helper functions
    //--------------------------------------------------------------------------
    private boolean invokeCb(int i){
        if(i > 0 && i < cmap.length){
            cmap[i].Invoke();
            return true;
        }
        return false;
    }
    
    //Sets flags during a calculation
    private int rfz(int i, int j, int sign){
        int result = i + j;
        reg.f(0);   //Clear flag before proceeding
        reg.zero((result & Metrics.BIT8) == 0);                                 //Zero flag set when op is 0
        reg.cary(result > 255 || result < 0); //Or a 1 is bitshifted out        //Carry flag set on overflow
        reg.subtract(sign < 0);                                                 //Set on subtraction operaton
        reg.halfcarry((((i & 0xf) + (j & 0xf)) & 0x10) == 0x10);                //Set on half-carry
        return result;
    }
    
    private int arfz(int i, int j){
        return rfz(i, j, 1);
    }
    
    private int srfz(int i, int j){
        return rfz(i,j,-1);
    }
    
    private int rfz(int i, int j){
        return rfz(i,j, (j < 0 ? -1 : (j > 0 ? 1 : 0)));
    }
    
    //--------------------------------------------------------------------------
    // List of all OPCODES
    //--------------------------------------------------------------------------
    
    //No operation
    Op NOP = new Op(0x00, "NOP", map, () -> {
        clock.m(1);
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
    
    //TODO LD nn,n for BC, DE, HL, SP
    
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
    Op LD_rr_lr = new Op(0x6B, "LD L,E", map, () -> {
        int v = reg.e();
        reg.l(v);
        clock.m(1);
        clock.t(4);
    });
    
    //Load into register L from register H
    Op LD_rr_lH = new Op(0x6C, "LD L,H", map, () -> {
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
        clock.m(2);
        clock.t(8);
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
    
    //Load into register A from memory n
    Op LD_an = new Op(0x3E, "LD A,(n)", map, () -> {
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
        mmu.wb(reg.bc(), reg.a());
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
    Op LD_A_c = new Op(0xF2, "LD A,(C)", map, () -> {
        int v = mmu.rb(0xFF00 + reg.c());
        reg.a(v);
        clock.m(2);
        clock.t(8);
    });
    
    //Load into memory 0xFF00 + register C the value in register A
    Op LD_c_A = new Op(0xE2, "LD (C),A", map, () -> {
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
    Op LDH_n_A = new Op(0xE0, "LDH (n),A", map, () -> {
        mmu.wb(0xFF00 + mmu.rb(reg.pc()), reg.a());
        reg.pcpp(1);
        clock.m(3);
        clock.t(12);
    });
    
    //Put memory address 0xFF00 + n into register A
    Op LDH_A_n = new Op(0xF0, "LDH A,(n)", map, () -> {
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
    
    
}

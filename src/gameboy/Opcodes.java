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
    
    private Op[] map = new Op[256];
    private Op[] cbmap = new Op[256];
    
    public Opcodes(Cpu cpu){
        this.cpu = cpu;
        this.reg = cpu.reg;
        this.clock = cpu.clock;
        this.mmu = cpu.GetMmu();
        
        int count = 0; int ccount = 0;
        for(int i = 0 ; i < map.length; i++){
            if(map[i] == null){
                count++;
                Op XX = new Op(i, "XX"+i, null, () -> {
                    System.out.println("Unimplemented opcode called at "+(reg.pc() - 1)+" with opcode "+mmu.rb((reg.pc() - 1)));
                });
                map[i] = XX;
            }
            if(cbmap[i] == null){
                ccount++;
                Op XX = new Op(i, "XX"+i, null, () -> {
                    System.out.println("Unimplemented opcode called at "+(reg.pc() - 1)+" with opcode "+mmu.rb((reg.pc() - 1)));
                });
                cbmap[i] = XX;
            }
        }
        
        System.out.println(count+" base opcodes undefined");
        System.out.println(ccount+" CB opcodes undefined");
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
    
    private boolean invokeCb(int i){ //map to CB
        //int i=MMU.rb(Reg.pc); Reg.pc++;
	//Reg.pc &= 65535;
        if(i > 0 && i < cbmap.length){
            cbmap[i].Invoke();
            return true;
        }
        return false;
    }
    
    //Sets flags during a calculation
    private int rfz(int i, int j, int sign){
        int result = i + j;
        reg.f(0);   //Clear flag before proceeding
        reg.zero((result & Metrics.BIT8) == 0);                                 //Zero flag set when op is 0
        reg.carry(result > 255 || result < 0); //Or a 1 is bitshifted out        //Carry flag set on overflow
        reg.subtract(sign < 0);                                                 //Set on subtraction operaton
        reg.halfcarry(isHalfCarry(i,j));                //Set on half-carry
        return result;
    }
    
    private boolean isHalfCarry(int a, int b){
        return(((a & 0xf) + (b & 0xf)) & 0x10) == 0x10;
    }
    
    private boolean isHalfCarry16(int a, int b){
        return(((a & 0xff) + (b & 0xff)) & 0x100) == 0x100;
    }
    
    private boolean isCarry16(int i){
        return i > 65535 || i < 0;
    }
    
    private boolean isCarry(int i){
        return i > 255 || i < 0;
    }
    
    private boolean isZero(int i, int max){
        return (i & max) == 0;
    }
    
    private boolean isZero(int i){
        return isZero(i, Metrics.BIT8);
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
        int n =  mmu.rb(reg.pc());
        if(n > 127) //Signed stranformation
            n = -((~n+1)&255);
        int sp = reg.sp();
        reg.hl(sp + n);
        reg.pcpp(1);
        clock.m(3);
        clock.t(12);
        
        reg.zero(false);
        reg.subtract(false);
        reg.halfcarry(isHalfCarry(sp,n));
        reg.carry(isCarry(sp + n));
    });
    
    //Put stack pointer into memory at nn //TODO
    Op LD_nn_SP = new Op(0x08, "LD (nn),SP", map, () -> {
        int v = reg.sp();
        mmu.ww(v, mmu.rw(reg.pc()));
        reg.pcpp(2);
        clock.m(5);
        clock.t(20);
    });
    
    //Push register pair AF onto the stack, Decrement Stack Pointer Twice
    Op PUSH_AF = new Op(0xF5, "PUSH AF", map, () -> {
        reg.sppp(-2);
        mmu.ww(reg.sp(), reg.af());
        clock.m(4);
        clock.t(16);
    });
    
    //Push register pair BC onto the stack, Decrement Stack Pointer Twice
    Op PUSH_BC = new Op(0xC5, "PUSH BC", map, () -> {
        reg.sppp(-2);
        mmu.ww(reg.sp(), reg.bc());
        clock.m(4);
        clock.t(16);
    });
    
    //Push register pair DE onto the stack, Decrement Stack Pointer Twice
    Op PUSH_DE = new Op(0xD5, "PUSH DE", map, () -> {
        reg.sppp(-2);
        mmu.ww(reg.sp(), reg.de());
        clock.m(4);
        clock.t(16);
    });
    
    //Push register pair HL onto the stack, Decrement Stack Pointer Twice
    Op PUSH_HL = new Op(0xE5, "PUSH HL", map, () -> {
        reg.sppp(-2);
        mmu.ww(reg.sp(), reg.hl()); //TODO confirm if this is the same as sp-- wb(h) sp-- wb(l)
        clock.m(4); //TODO 3 and 12 or 4 and 16? different sources say different things
        clock.t(16);
    });
    
    //Pop value off stack into register AF
    Op POP_AF = new Op(0xF1, "POP AF", map, () -> {
        int v = mmu.rw(reg.sp()); //TODO confirm if this is the same as sp-- wb(h) sp-- wb(l)
        reg.af(v);
        reg.sppp(2);
        clock.m(3);
        clock.t(12);
    });
    
    //Pop value off stack into register BC
    Op POP_BC = new Op(0xC1, "POP BC", map, () -> {
        int v = mmu.rw(reg.sp()); //TODO confirm if this is the same as sp-- wb(h) sp-- wb(l)
        reg.bc(v);
        reg.sppp(2);
        clock.m(3);
        clock.t(12);
    });
    
    //Pop value off stack into register DE
    Op POP_DE = new Op(0xD1, "POP DE", map, () -> {
        int v = mmu.rw(reg.sp()); //TODO confirm if this is the same as sp-- wb(h) sp-- wb(l)
        reg.de(v);
        reg.sppp(2);
        clock.m(3);
        clock.t(12);
    });
    
    //Pop value off stack into register HL
    Op POP_HL = new Op(0xE1, "POP HL", map, () -> {
        int v = mmu.rw(reg.sp()); //TODO confirm if this is the same as sp-- wb(h) sp-- wb(l)
        reg.hl(v);
        reg.sppp(2);
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
    Op ADC_A_A = new Op(0x8F, "ADC A,A", map, () -> {
        int n = reg.a(); //Change me
        int c = (reg.carry() ? 1 : 0);
        int i = n + c;
        int a = reg.a();
        int v = i + a;  
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(isHalfCarry(i,a));
        reg.carry(isCarry(v));
        
        clock.m(1);
        clock.t(4);
    });
    
    //Add B + Carry Flag into registry A
    Op ADC_A_B = new Op(0x88, "ADC A,B", map, () -> {
        int n = reg.b(); //Change me
        int c = (reg.carry() ? 1 : 0);
        int i = n + c;
        int a = reg.a();
        int v = i + a;  
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(isHalfCarry(i,a));
        reg.carry(isCarry(v));
        
        clock.m(1);
        clock.t(4);
    });
    
    //Add C + Carry Flag into registry A
    Op ADC_A_C = new Op(0x89, "ADC A,C", map, () -> {
        int n = reg.c(); //Change me
        int c = (reg.carry() ? 1 : 0);
        int i = n + c;
        int a = reg.a();
        int v = i + a;  
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(isHalfCarry(i,a));
        reg.carry(isCarry(v));
        
        clock.m(1);
        clock.t(4);
    });
    
    //Add D + Carry Flag into registry A
    Op ADC_A_D = new Op(0x8A, "ADC A,D", map, () -> {
        int n = reg.d(); //Change me
        int c = (reg.carry() ? 1 : 0);
        int i = n + c;
        int a = reg.a();
        int v = i + a;  
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(isHalfCarry(i,a));
        reg.carry(isCarry(v));
        
        clock.m(1);
        clock.t(4);
    });
    
    //Add E + Carry Flag into registry A
    Op ADC_A_E = new Op(0x8B, "ADC A,E", map, () -> {
        int n = reg.e(); //Change me
        int c = (reg.carry() ? 1 : 0);
        int i = n + c;
        int a = reg.a();
        int v = i + a;  
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(isHalfCarry(i,a));
        reg.carry(isCarry(v));
        
        clock.m(1);
        clock.t(4);
    });
    
    //Add H + Carry Flag into registry A
    Op ADC_A_H = new Op(0x8C, "ADC A,H", map, () -> {
        int n = reg.h(); //Change me
        int c = (reg.carry() ? 1 : 0);
        int i = n + c;
        int a = reg.a();
        int v = i + a;  
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(isHalfCarry(i,a));
        reg.carry(isCarry(v));
        
        clock.m(1);
        clock.t(4);
    });
    
    //Add L + Carry Flag into registry A
    Op ADC_A_L = new Op(0x8D, "ADC A,L", map, () -> {
        int n = reg.l(); //Change me
        int c = (reg.carry() ? 1 : 0);
        int i = n + c;
        int a = reg.a();
        int v = i + a;  
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(isHalfCarry(i,a));
        reg.carry(isCarry(v));
        
        clock.m(1);
        clock.t(4);
    });
    
    //Add memory at HL + Carry Flag into registry A
    Op ADC_A_HL = new Op(0x8E, "ADC A,(HL)", map, () -> {
        int n = mmu.rb(reg.hl()); //Change me
        int c = (reg.carry() ? 1 : 0);
        int i = n + c;
        int a = reg.a();
        int v = i + a;  
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(isHalfCarry(i,a));
        reg.carry(isCarry(v));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Add immediate value n + Carry Flag into registry A
    Op ADC_A_n = new Op(0xCE, "ADC A,n", map, () -> {
        int n = mmu.rb(reg.pc()); //Change me
        int c = (reg.carry() ? 1 : 0);
        int i = n + c;
        int a = reg.a();
        int v = i + a;  
        reg.a(v);
        reg.pcpp(1);
        
        reg.zero(isZero(v));
        reg.subtract(false);
        reg.halfcarry(isHalfCarry(i,a));
        reg.carry(isCarry(v));
        
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
    Op SBC_A_A = new Op(0x9F, "SBC A,A", map, () -> {
        int n = reg.a(); //Change me
        int c = (reg.carry() ? 1 : 0);
        int i = c + n;
        int a = reg.a();
        int v = a - i;
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(true);
        reg.halfcarry(isHalfCarry(a, -i)); //WATCH THIS
        reg.carry(isCarry(v));
        
        clock.m(1);
        clock.t(4);
    });
    
    //Subtract B + Carry flag from register A
    Op SBC_A_B = new Op(0x98, "SBC A,B", map, () -> {
        int n = reg.b(); //Change me
        int c = (reg.carry() ? 1 : 0);
        int i = c + n;
        int a = reg.a();
        int v = a - i;
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(true);
        reg.halfcarry(isHalfCarry(a, -i)); //WATCH THIS
        reg.carry(isCarry(v));
        
        clock.m(1);
        clock.t(4);
    });
    
    //Subtract C + Carry flag from register A
    Op SBC_A_C = new Op(0x99, "SBC A,C", map, () -> {
        int n = reg.c(); //Change me
        int c = (reg.carry() ? 1 : 0);
        int i = c + n;
        int a = reg.a();
        int v = a - i;
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(true);
        reg.halfcarry(isHalfCarry(a, -i)); //WATCH THIS
        reg.carry(isCarry(v));
        
        clock.m(1);
        clock.t(4);
    });
    
    //Subtract D + Carry flag from register A
    Op SBC_A_D = new Op(0x9A, "SBC A,D", map, () -> {
        int n = reg.d(); //Change me
        int c = (reg.carry() ? 1 : 0);
        int i = c + n;
        int a = reg.a();
        int v = a - i;
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(true);
        reg.halfcarry(isHalfCarry(a, -i)); //WATCH THIS
        reg.carry(isCarry(v));
        
        clock.m(1);
        clock.t(4);
    });
    
    //Subtract E + Carry flag from register A
    Op SBC_A_E = new Op(0x9B, "SBC A,E", map, () -> {
        int n = reg.e(); //Change me
        int c = (reg.carry() ? 1 : 0);
        int i = c + n;
        int a = reg.a();
        int v = a - i;
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(true);
        reg.halfcarry(isHalfCarry(a, -i)); //WATCH THIS
        reg.carry(isCarry(v));
        
        clock.m(1);
        clock.t(4);
    });
    
    
    //Subtract H + Carry flag from register A
    Op SBC_A_H = new Op(0x9C, "SBC A,H", map, () -> {
        int n = reg.h(); //Change me
        int c = (reg.carry() ? 1 : 0);
        int i = c + n;
        int a = reg.a();
        int v = a - i;
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(true);
        reg.halfcarry(isHalfCarry(a, -i)); //WATCH THIS
        reg.carry(isCarry(v));
        
        clock.m(1);
        clock.t(4);
    });
    
    //Subtract L + Carry flag from register A
    Op SBC_A_L = new Op(0x9D, "SBC A,L", map, () -> {
        int n = reg.l(); //Change me
        int c = (reg.carry() ? 1 : 0);
        int i = c + n;
        int a = reg.a();
        int v = a - i;
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(true);
        reg.halfcarry(isHalfCarry(a, -i)); //WATCH THIS
        reg.carry(isCarry(v));
        
        clock.m(1);
        clock.t(4);
    });
    
    //Subtract memory at HL + Carry flag from register A
    Op SBC_A_HL = new Op(0x9E, "SBC A,(HL)", map, () -> {
        int n = mmu.rb(reg.hl()); //Change me
        int c = (reg.carry() ? 1 : 0);
        int i = c + n;
        int a = reg.a();
        int v = a - i;
        reg.a(v);
        
        reg.zero(isZero(v));
        reg.subtract(true);
        reg.halfcarry(isHalfCarry(a, -i)); //WATCH THIS
        reg.carry(isCarry(v));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Subtract immediate value n + Carry flag from register A
    Op SBC_A_n = new Op(0xDE, "SBC A,n", map, () -> {
        int n = mmu.rb(reg.pc()); //Change me
        int c = (reg.carry() ? 1 : 0);
        int i = c + n;
        int a = reg.a();
        int v = a - i;
        reg.a(v);
        reg.pcpp(1);
        
        reg.zero(isZero(v));
        reg.subtract(true);
        reg.halfcarry(isHalfCarry(a, -i)); //WATCH THIS
        reg.carry(isCarry(v));
        
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
            
        reg.zero((n & Metrics.BIT8) == (a & Metrics.BIT8));
        reg.subtract(true);
        reg.halfcarry(isHalfCarry(a, -n));
        reg.carry((a & Metrics.BIT8) < (n & Metrics.BIT8));
        
        clock.m(1);
        clock.t(4);
    });
    
    //Compare register A with register B
    Op CP_A_B = new Op(0xB8, "CP A,B", map, () -> {
        int n = reg.b();
        int a = reg.a();
            
        reg.zero((n & Metrics.BIT8) == (a & Metrics.BIT8));
        reg.subtract(true);
        reg.halfcarry(isHalfCarry(a, -n));
        reg.carry((a & Metrics.BIT8) < (n & Metrics.BIT8));
        
        clock.m(1);
        clock.t(4);
    });
    
    //Compare register A with register C
    Op CP_A_C = new Op(0xB9, "CP A,C", map, () -> {
        int n = reg.c();
        int a = reg.a();
            
        reg.zero((n & Metrics.BIT8) == (a & Metrics.BIT8));
        reg.subtract(true);
        reg.halfcarry(isHalfCarry(a, -n));
        reg.carry((a & Metrics.BIT8) < (n & Metrics.BIT8));
        
        clock.m(1);
        clock.t(4);
    });
    
    //Compare register A with register D
    Op CP_A_D = new Op(0xBA, "CP A,D", map, () -> {
        int n = reg.d();
        int a = reg.a();
            
        reg.zero((n & Metrics.BIT8) == (a & Metrics.BIT8));
        reg.subtract(true);
        reg.halfcarry(isHalfCarry(a, -n));
        reg.carry((a & Metrics.BIT8) < (n & Metrics.BIT8));
        
        clock.m(1);
        clock.t(4);
    });
    
    //Compare register A with register E
    Op CP_A_E = new Op(0xBB, "CP A,E", map, () -> {
        int n = reg.e();
        int a = reg.a();
            
        reg.zero((n & Metrics.BIT8) == (a & Metrics.BIT8));
        reg.subtract(true);
        reg.halfcarry(isHalfCarry(a, -n));
        reg.carry((a & Metrics.BIT8) < (n & Metrics.BIT8));
        
        clock.m(1);
        clock.t(4);
    });
    
    //Compare register A with register H
    Op CP_A_H = new Op(0xBC, "CP A,H", map, () -> {
        int n = reg.h();
        int a = reg.a();
            
        reg.zero((n & Metrics.BIT8) == (a & Metrics.BIT8));
        reg.subtract(true);
        reg.halfcarry(isHalfCarry(a, -n));
        reg.carry((a & Metrics.BIT8) < (n & Metrics.BIT8));
        
        clock.m(1);
        clock.t(4);
    });
    
    //Compare register A with register L
    Op CP_A_L = new Op(0xBD, "CP A,L", map, () -> {
        int n = reg.l();
        int a = reg.a();
            
        reg.zero((n & Metrics.BIT8) == (a & Metrics.BIT8));
        reg.subtract(true);
        reg.halfcarry(isHalfCarry(a, -n));
        reg.carry((a & Metrics.BIT8) < (n & Metrics.BIT8));
        
        clock.m(1);
        clock.t(4);
    });
    
    //Compare register A with memory at HL
    Op CP_A_HL = new Op(0xBE, "CP A,(HL)", map, () -> {
        int n = mmu.rb(reg.hl());
        int a = reg.a();
            
        reg.zero((n & Metrics.BIT8) == (a & Metrics.BIT8));
        reg.subtract(true);
        reg.halfcarry(isHalfCarry(a, -n));
        reg.carry((a & Metrics.BIT8) < (n & Metrics.BIT8));
        
        clock.m(2);
        clock.t(8);
    });
    
    //Compare register A with immediate value n
    Op CP_A_n = new Op(0xFE, "CP A,n", map, () -> {
        int n = mmu.rb(reg.pc());
        int a = reg.a();
        reg.pcpp(1);    
        
        reg.zero((n & Metrics.BIT8) == (a & Metrics.BIT8));
        reg.subtract(true);
        reg.halfcarry(isHalfCarry(a, -n));
        reg.carry((a & Metrics.BIT8) < (n & Metrics.BIT8));
        
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
        int n = mmu.rb(reg.pc());
        if(n > 127)
            n = -((~n+1)&255);
        int sp = reg.sp();
        int v = sp + n;
        reg.sp(v);
        reg.pcpp(1);
        
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
        
        clock.m(2);
        clock.t(8);
    });
    
    //Increment the register DE
    Op INC_DE = new Op(0x13, "INC DE", map, () -> {
        int v = reg.de() + 1;
        reg.de(v);
        
        clock.m(2);
        clock.t(8);
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
        //TODO wtf is this
        int a = reg.a();
        if(reg.halfcarry() || (reg.a() & 15) > 9)
            reg.a(reg.a() + 6);
        reg.carry(false);
        if(reg.halfcarry() || a > 0x99){
            reg.a(reg.a() + 0x60);
            reg.carry(true);
        }
        
        reg.zero(isZero(reg.a()));
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
        
        clock.m(1);
        clock.t(4);
    });
    
    //Set the carry flag
    Op SCF = new Op(0x37, "SCF", map, () -> {
        reg.carry(true);
        
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
        //TODO
        clock.m(1);
        clock.t(4);
    });
    
    //Enabled interrupts
    Op EI = new Op(0xFB, "EI", map, () -> {
        //TODO
        clock.m(1);
        clock.t(4);
    });
    
    ///
    // Rotates and Shifts
    ///
    
    //Rotate A left, old bit 7 to Carry flag
    Op RLCA = new Op(0x07, "RCLA", map, () -> {
        int a = reg.a();
        boolean carry = ((a & 0x80) != 0);
        a = (a << 1) + (carry ? 1: 0);
        reg.a(a);
        
        reg.zero(isZero(a));
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
        a = (a << 1) + (reg.carry() ? 1 : 0);
        reg.a(a);
        
        reg.zero(isZero(a));
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
        a = (a >> 1) + (toCarry ? 0x80 : 0);
        reg.a(a);
        
        reg.zero(isZero(a));
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
        a = (a >> 1) + (reg.carry() ? 0x80 : 0);
        reg.a(a);
        
        reg.zero(isZero(a));
        reg.subtract(false);
        reg.halfcarry(false);
        reg.carry(toCarry);
        
        clock.m(1);
        clock.t(4);
    });
    
    //TODO PAGE 101
    
    
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
        }else
            reg.pcpp(2);
        
        clock.m(3);
        clock.t(12);
    });
    
    //Jump to address in HL
    Op JP_NL = new Op(0xE9, "JP HL", map, () -> {
        reg.pc(reg.hl());
        
        clock.m(1);
        clock.t(4);
    });
    
    //Add signed value n to the current address and jump to it
    Op JP_n = new Op(0x18, "JR n", map, () -> {
        int n = mmu.rb(reg.pc());
        reg.pcpp(1);
        if(n > 127)
            n = -((~n+1)&255);
        int a = reg.pc() + n;
        reg.pc(a);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Jump to pc + signed n if Z flag is reset
    Op JP_NZ_n = new Op(0x20, "JR NZ,n", map, () -> {
        int i = mmu.rb(reg.pc());
        if(i > 127)
            i = -((~i+1)&255);
        reg.pcpp(1);
        
        if(!reg.zero()){
            reg.pc(reg.pc() + i);
        }
        
        clock.m(2);
        clock.t(8);
    });
    
    //Jump to pc + signed n if Z flag is set
    Op JP_Z_n = new Op(0x28, "JR Z,n", map, () -> {
        int i = mmu.rb(reg.pc());
        if(i > 127)
            i = -((~i+1)&255);
        reg.pcpp(1);
        
        if(reg.zero()){
            reg.pc(reg.pc() + i);
        }
        
        clock.m(2);
        clock.t(8);
    });
    
    //Jump to pc + signed n if C flag is reset
    Op JP_NC_n = new Op(0x30, "JR NC,n", map, () -> {
        int i = mmu.rb(reg.pc());
        if(i > 127)
            i = -((~i+1)&255);
        reg.pcpp(1);
        
        if(!reg.carry()){
            reg.pc(reg.pc() + i);
        }
        
        clock.m(2);
        clock.t(8);
    });
    
    //Jump to pc + signed n if C flag is reset
    Op JP_C_n = new Op(0x38, "JR C,n", map, () -> {
        int i = mmu.rb(reg.pc());
        if(i > 127)
            i = -((~i+1)&255);
        reg.pcpp(1);
        
        if(reg.carry()){
            reg.pc(reg.pc() + i);
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
        //Next inst to stack
        reg.sppp(-2);
        int next = reg.pc() + 2;
        mmu.wb(reg.sp(), next);
        
        //Jump to immediate value
        int jp = mmu.rw(reg.pc());
        reg.pc(jp);
        
        clock.m(3);
        clock.t(12);
    });
    
    //Call address n if the condtion is true
    Op CALL_NZ_nn = new Op(0xC4, "CALL NZ,nn", map, () -> {
        if(!reg.zero()){
             //Next inst to stack
            reg.sppp(-2);
            int next = reg.pc() + 2;
            mmu.wb(reg.sp(), next);
            
            //Jump to immediate value
            int jp = mmu.rw(reg.pc());
            reg.pc(jp);
        }else{
            reg.pcpp(2);
        }
        
        clock.m(3);
        clock.t(12);
    });
    
    //Call address n if the condtion is true
    Op CALL_Z_nn = new Op(0xCC, "CALL Z,nn", map, () -> {
        if(reg.zero()){
             //Next inst to stack
            reg.sppp(-2);
            int next = reg.pc() + 2;
            mmu.wb(reg.sp(), next);
            
            //Jump to immediate value
            int jp = mmu.rw(reg.pc());
            reg.pc(jp);
        }else{
            reg.pcpp(2);
        }
        
        clock.m(3);
        clock.t(12);
    });
    
    //Call address n if the condtion is true
    Op CALL_NC_nn = new Op(0xD4, "CALL NC,nn", map, () -> {
        if(!reg.carry()){
             //Next inst to stack
            reg.sppp(-2);
            int next = reg.pc() + 2;
            mmu.wb(reg.sp(), next);
            
            //Jump to immediate value
            int jp = mmu.rw(reg.pc());
            reg.pc(jp);
        }else{
            reg.pcpp(2);
        }
        
        clock.m(3);
        clock.t(12);
    });
    
    //Call address n if the condtion is true
    Op CALL_C_nn = new Op(0xDC, "CALL C,nn", map, () -> {
        if(reg.carry()){
             //Next inst to stack
            reg.sppp(-2);
            int next = reg.pc() + 2;
            mmu.wb(reg.sp(), next);
            
            //Jump to immediate value
            int jp = mmu.rw(reg.pc());
            reg.pc(jp);
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
    
    //Push present address onto stack and jump to 0x0000 + n
    Op RST_00h = new Op(0xC7, "RST 00H", map, () -> {
        //reg.rsv(); //TODO //Save registry to backup
        reg.sppp(-2);
        mmu.wb(reg.sp(), reg.pc());
        reg.pc(0x00);
        
        clock.m(8); //Maybe 3 and 12
        clock.t(32);
    });
    
    //Push present address onto stack and jump to 0x0000 + n
    Op RST_08h = new Op(0xCF, "RST 08H", map, () -> {
        //reg.rsv();
        reg.sppp(-2);
        mmu.wb(reg.sp(), reg.pc());
        reg.pc(0x08);
        
        clock.m(8); //Maybe 3 and 12
        clock.t(32);
    });
    
    //Push present address onto stack and jump to 0x0000 + n
    Op RST_10h = new Op(0xD7, "RST 10H", map, () -> {
        //reg.rsv();
        reg.sppp(-2);
        mmu.wb(reg.sp(), reg.pc());
        reg.pc(0x10);
        
        clock.m(8); //Maybe 3 and 12
        clock.t(32);
    });
    
    //Push present address onto stack and jump to 0x0000 + n
    Op RST_18h = new Op(0xDF, "RST 18H", map, () -> {
        //reg.rsv();
        reg.sppp(-2);
        mmu.wb(reg.sp(), reg.pc());
        reg.pc(0x18);
        
        clock.m(8); //Maybe 3 and 12
        clock.t(32);
    });
    
    //Push present address onto stack and jump to 0x0000 + n
    Op RST_20h = new Op(0xE7, "RST 20H", map, () -> {
        //reg.rsv();
        reg.sppp(-2);
        mmu.wb(reg.sp(), reg.pc());
        reg.pc(0x20);
        
        clock.m(8); //Maybe 3 and 12
        clock.t(32);
    });
    
    //Push present address onto stack and jump to 0x0000 + n
    Op RST_28h = new Op(0xEF, "RST 28H", map, () -> {
        //reg.rsv();
        reg.sppp(-2);
        mmu.wb(reg.sp(), reg.pc());
        reg.pc(0x28);
        
        clock.m(8); //Maybe 3 and 12
        clock.t(32);
    });
    
    //Push present address onto stack and jump to 0x0000 + n
    Op RST_30h = new Op(0xF7, "RST 30H", map, () -> {
        //reg.rsv();
        reg.sppp(-2);
        mmu.wb(reg.sp(), reg.pc());
        reg.pc(0x30);
        
        clock.m(8); //Maybe 3 and 12
        clock.t(32);
    });
    
    //Push present address onto stack and jump to 0x0000 + n
    Op RST_38h = new Op(0xFF, "RST 38H", map, () -> {
        //reg.rsv();
        reg.sppp(-2);
        mmu.wb(reg.sp(), reg.pc());
        reg.pc(0x38);
        
        clock.m(8); //Maybe 3 and 12
        clock.t(32);
    });
    
    //PAGE 117
    
    ///
    // Returns
    ///
    
    //Pop 2 bytes off stack and jump to that address
    Op RET = new Op(0xC9, "RET", map, () -> {
        reg.pc(mmu.rw(reg.sp()));
        reg.sppp(2);
        
        clock.m(2);
        clock.t(8);
    });
    
    //Return if Z flag is reset
    Op RET_NZ = new Op(0xC0, "RET NZ", map, () -> {
        if(!reg.zero()){
            reg.pc(mmu.rw(reg.sp()));
            reg.sppp(2);
            clock.m(2);
            clock.t(8);
        }else{
            clock.m(1);
            clock.t(4);
        }
    });
    
    //Return if Z flag is set
    Op RET_Z = new Op(0xC8, "RET Z", map, () -> {
        if(reg.zero()){
            reg.pc(mmu.rw(reg.sp()));
            reg.sppp(2);
            clock.m(2);
            clock.t(8);
        }else{
            clock.m(1);
            clock.t(4);
        }
    });
    
    //Return if C flag is reset
    Op RET_CZ = new Op(0xD0, "RET CZ", map, () -> {
        if(!reg.carry()){
            reg.pc(mmu.rw(reg.sp()));
            reg.sppp(2);
            clock.m(2);
            clock.t(8);
        }else{
            clock.m(1);
            clock.t(4);
        }
    });
    
    //Return if C flag is set
    Op RET_C = new Op(0xD8, "RET C", map, () -> {
        if(reg.carry()){
            reg.pc(mmu.rw(reg.sp()));
            reg.sppp(2);
            clock.m(2);
            clock.t(8);
        }else{
            clock.m(1);
            clock.t(4);
        }
    });
    
    //Pop 2 bytes off the stack, and jump there then enable interrupts
    Op RETI = new Op(0xD9, "RETI", map, () -> {
        //reg.rrs(); //Restore registry //TODO
        EI.Invoke();
        reg.pc(mmu.rw(reg.sp()));
        reg.sppp(2);
        
        clock.m(1);
        clock.t(4);
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
}

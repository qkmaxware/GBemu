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
    
    private Op[] map;
    private Op[] cmap;
    
    public Opcodes(Cpu cpu){
        this.cpu = cpu;
        this.reg = cpu.reg;
        this.clock = cpu.clock;
        this.mmu = cpu.GetMmu();
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
        if(this.map == null)
            throw new RuntimeException("Opcodes have not been initialized");
        if(opcode < 0 || opcode > this.map.length){
            throw new RuntimeException("Opcode ("+opcode+")is not valid ");
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
    
}

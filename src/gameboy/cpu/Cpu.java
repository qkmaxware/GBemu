/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameboy.cpu;

import gameboy.MemoryMap;
import gameboy.game.CartridgeAdapter;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 *
 * @author Colin
 */
public class Cpu {
    
    public final Registry reg = new Registry();
    public final Clock clock = new Clock();
    public final Registry cpp = new Registry();
    
    private MemoryMap mmu;
    
    public final Opcodes opcodes;
    
    public final ConcurrentLinkedDeque<String> recentOps = new ConcurrentLinkedDeque<String>();         

    
    public Cpu(MemoryMap mmu){
        this.mmu = mmu;
        opcodes = new Opcodes(this, mmu);
    }

    private void LastOp(String value){
        recentOps.addLast(value);
        while(recentOps.size() > 50)
                recentOps.removeFirst();
    }
    
    public void Reset(){
        reg.Reset();
        cpp.Reset();
        clock.Reset();
        recentOps.clear();
        
        //Initial values expected by the bios
        reg.af((mmu.Get(MemoryMap.ROM_BANK_0) != null && ((CartridgeAdapter)mmu.Get(MemoryMap.ROM_BANK_0)).supportsCGB()) ? 0x11B0 : 0x01B0);
        reg.bc(0x0013);
        reg.de(0x00D8);
        reg.hl(0x014D);
        
        reg.sp(0xFFFE);
        reg.pc(0x0100);
        
        mmu.wb(0xFF05, 0x00);   //TIMA
        mmu.wb(0xFF06, 0x00);   //TMA
        mmu.wb(0xFF07, 0x00);   //TAC
        mmu.wb(0xFF10, 0x80);   //NR10
        mmu.wb(0xFF11, 0xBF);   //NR11
        mmu.wb(0xFF12, 0xF3);   //NR12
        mmu.wb(0xFF14, 0xBF);   //NR14
        mmu.wb(0xFF16, 0x3F);   //NR21
        mmu.wb(0xFF17, 0x00);   //NR22
        mmu.wb(0xFF19, 0xBF);   //NR24
        mmu.wb(0xFF1A, 0x7F);   //NR30
        mmu.wb(0xFF1B, 0xFF);   //NR31
        mmu.wb(0xFF1C, 0x0F);   //NR32
        mmu.wb(0xFF1E, 0xBF);   //NR33
        mmu.wb(0xFF20, 0xFF);   //NR41
        mmu.wb(0xFF21, 0x00);   //NR42
        mmu.wb(0xFF22, 0x00);   //NR43
        mmu.wb(0xFF23, 0xBF);   //NR30
        mmu.wb(0xFF24, 0x77);   //NR50
        mmu.wb(0xFF25, 0xF3);   //NR51
        mmu.wb(0xFF26, 0xF1);   //NR52 
        mmu.wb(0xFF40, 0x91);   //LCDC
        mmu.wb(0xFF42, 0x00);   //SCY
        mmu.wb(0xFF43, 0x00);   //SCX
        mmu.wb(0xFF45, 0x00);   //LYC
        mmu.wb(0xFF47, 0xFC);   //BGP
        mmu.wb(0xFF48, 0xFF);   //0BP0
        mmu.wb(0xFF49, 0xFF);   //0BP1
        mmu.wb(0xFF4A, 0x00);   //WY
        mmu.wb(0xFF4B, 0x00);   //WX
        mmu.wb(0xFFFF, 0x00);   //EI
    }
    
    public int Step(){
        //Fetch intruction
        int opcode = mmu.rb(reg.pc());
        
        //Decode instruction
        Op op = opcodes.Fetch(opcode);
        LastOp(String.format("0x%04X", reg.pc()) + ": "+op.toString());
        
        //Increment PC
        reg.pc(reg.pc() + 1);
        
        //Execute instruction
        op.Invoke(); 
        
        //Increment the clock
        int deltaM = clock.delM();
        clock.Accept();
        
        //Interrupt handler
        if(reg.ime() != 0 && mmu.i_enable != 0 && mmu.i_flags != 0){
            //Mask off ints that arent enabled
            int fired = mmu.i_enable & mmu.i_flags;
            
            //INTERRUPT TABLE-------------------------------
            // Interrupt        ISR Address     Bit Value
            // Vblank           0x40            0
            // LCD stat         0x48            1
            // Timer            0x50            2
            // Serial           0x58            3
            // Joypad Press     0x60            4   
            //----------------------------------------------
            
            if((fired & 0x01) != 0){
                //Vblank
                mmu.i_flags &= 0xFE;
                opcodes.RST_40h.Invoke();
            }
            if((fired & 0b10) != 0){
                //LCD stat
                //mmu.i_flags &= 0xFD;
                //opcodes.RST_48h.Invoke();
            }
            if((fired & 0b100) != 0){
                //Timer
                //mmu.i_flags &= 0xFB;
                //opcodes.RST_50h.Invoke();
            }
            if((fired & 0b1000) != 0){
                //Serial
                //mmu.i_flags &= 0xF7;
                //opcodes.RST_58h.Invoke();
            }
            if((fired & 0b10000) != 0){
                //Joypad press
                //mmu.i_flags &= EF;
                //opcodes.RST_60h.Invoke();
            }
        }
        
        //Increment the clock in case interrupt has fired
        clock.Accept();
        
        return deltaM;
    }
}

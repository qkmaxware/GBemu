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
public class Cpu {
    
    public final Registry reg = new Registry();
    public final Clock clock = new Clock();
    
    private MemoryMap mmu;
    
    private final Opcodes opcodes;
    
    public Cpu(){
        opcodes = new Opcodes(this);
    }
    
    public void SetMmu(MemoryMap map){
        this.mmu = map;
    }
    
    public MemoryMap GetMmu(){
        return mmu;
    }
    
    public void Reset(){
        reg.Reset();
        clock.Reset();
    }
    
    public void Step(){
        //Fetch intruction
        int opcode = mmu.rb(reg.pc());
        
        //Increment PC
        reg.pc(reg.pc() + 1);
        
        //Decode instruction
        Op op = opcodes.Fetch(opcode);
        
        //Execute instruction
        op.Invoke(); 
        
        //Increment the clock
        clock.Accept();
    }
}

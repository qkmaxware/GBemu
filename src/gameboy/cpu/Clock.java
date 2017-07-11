/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameboy.cpu;

/**
 *
 * @author Colin
 */
public class Clock {
    
    private long machine; private long cycles; 
    
    private int inst_cycle;
    private int inst_machine;
    
    public void Reset(){
        machine = 0;
        cycles = 0;
        
        inst_cycle = 0;
        inst_machine = 0;
    }
    
    public long m(){
        return machine;
    }
    
    public void m(long i){
        inst_machine += i;
    }
    
    public int delM(){
        return this.inst_machine;
    }
    
    public int delT(){
        return this.inst_cycle;
    }
    
    public long t(){
        return cycles;
    }
    
    public void t(long i){
       inst_cycle += i;
    }
    
    public void Accept(){
        cycles = inst_cycle;
        inst_cycle = 0;
        
        machine = inst_machine;
        inst_machine = 0;
    }

    public void Reject(){
        inst_machine = 0;
        inst_cycle = 0;
    }
}

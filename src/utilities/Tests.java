/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import gameboy.Gameboy;
import gameboy.MemoryMap;
import gameboy.cpu.Cpu;
import gameboy.cpu.Op;
import java.util.LinkedList;

/**
 *
 * @author Colin Halseth
 */
public class Tests {
    
    public static void main(String[] args){
        Gameboy gb = new Gameboy();
        System.out.println("Running tests ... please wait.\n");
        
        System.out.println("Testing Memory Access");
        TestMemory(gb);
        System.out.println("");
        
        System.out.println("Testing Registry Read/Write");
        TestRegistry(gb);
        System.out.println("");
        
        System.out.println("Testing Opcodes");
        TestOpcodes(gb);
        System.out.println("");
    }
    
    public static void TestMemory(Gameboy gb){
        MemoryMap mmu = gb.mmu;
        
        int start = -1; int end = -1;
        LinkedList<String> ranges = new LinkedList<String>();
        
        for(int i = 0; i <= mmu.MaxAddress(); i++){
            mmu.wb(i, i);
            int r = mmu.rb(i);
            
            if(i != r){
                //Memory unwritable
                if(start == -1)
                    start = i;
                end = i;
            }else{
                //Memory is writable
                if(end != -1){
                    if(start == -1){
                        ranges.add(String.format("0x%04X", end));
                    }else if(end != -1){
                        ranges.add(String.format("0x%04X", start) + " - "+String.format("0x%04X", end));
                    }
                }
                start = -1;
                end = -1;
            }
        }
        
        if(end != -1){
            if(start == -1){
                ranges.add(String.format("0x%04X", end));
            }else if(end != -1){
                ranges.add(String.format("0x%04X", start) + " - "+String.format("0x%04X", end));
            }
        }
        
        System.out.println("Unwritable:"+ranges.toString());
        gb.Reset();
    }
    
    public static void TestRegistry(Gameboy gb){
        gb.Reset();
        Cpu cpu = gb.cpu;
        
        //Test 8bit registers
        for(int i = 0; i <= 0xFF; i++){
            cpu.reg.a(i);
            int read = cpu.reg.a();
            if(read != i){
                System.out.println("Failure on a "+i);
            }
            gb.Reset();

            cpu.reg.b(i);
            read = cpu.reg.b();
            if(read != i){
                System.out.println("Failure on b "+i);
            }
            gb.Reset();

            cpu.reg.c(i);
            read = cpu.reg.c();
            if(read != i){
                System.out.println("Failure on c "+i);
            }
            gb.Reset();

            cpu.reg.d(i);
            read = cpu.reg.d();
            if(read != i){
                System.out.println("Failure on d "+i);
            }
            gb.Reset();

            cpu.reg.e(i);
            read = cpu.reg.e();
            if(read != i){
                System.out.println("Failure on e "+i);
            }
            gb.Reset();

            cpu.reg.f(i);
            read = cpu.reg.f();
            if(read != (i & ~(0xF))){
                System.out.println("Failure on f "+i);
            }
            gb.Reset();

            cpu.reg.h(i);
            read = cpu.reg.h();
            if(read != i){
                System.out.println("Failure on h "+i);
            }
            gb.Reset();

            cpu.reg.l(i);
            read = cpu.reg.l();
            if(read != i){
                System.out.println("Failure on l "+i);
            }
            gb.Reset();
        }
        
        //Test 16bit registers
        for(int i = 0; i <= 0xFFFF; i++){
        
            cpu.reg.pc(i);
            int read = cpu.reg.pc();
            if(read != i){
                System.out.println("Failure on pc "+i);
            }
            gb.Reset();

            cpu.reg.sp(i);
            read = cpu.reg.sp();
            if(read != i){
                System.out.println("Failure on sp "+i);
            }
            gb.Reset();

            cpu.reg.af(i);
            read = cpu.reg.af();
            if(read != (i & ~(0xF))){
                System.out.println("Failure on af "+i);
            }
            gb.Reset();

            cpu.reg.bc(i);
            read = cpu.reg.bc();
            if(read != i){
                System.out.println("Failure on bc "+i);
            }
            gb.Reset();

            cpu.reg.de(i);
            read = cpu.reg.de();
            if(read != i){
                System.out.println("Failure on de "+i);
            }
            gb.Reset();

            cpu.reg.hl(i);
            read = cpu.reg.hl();
            if(read != i){
                System.out.println("Failure on hl "+i);
            }
            gb.Reset();
        }
        
        //Test flags
        boolean flag;
        
        cpu.reg.carry(true);
        if(cpu.reg.carry() != true)
            System.out.println("Failure to set c");
        
        cpu.reg.halfcarry(true);
        if(cpu.reg.halfcarry() != true)
            System.out.println("Failure to set h");
        
        cpu.reg.zero(true);
        if(cpu.reg.zero() != true)
            System.out.println("Failure to set z");
        
        cpu.reg.subtract(true);
        if(cpu.reg.subtract() != true)
            System.out.println("Failure to set n");
        
        cpu.reg.carry(false);
        if(cpu.reg.carry() != false)
            System.out.println("Failure to unset c");
        
        cpu.reg.halfcarry(false);
        if(cpu.reg.halfcarry() != false)
            System.out.println("Failure to unset h");
        
        cpu.reg.zero(false);
        if(cpu.reg.zero() != false)
            System.out.println("Failure to unset z");
        
        cpu.reg.subtract(false);
        if(cpu.reg.subtract() != false)
            System.out.println("Failure to unset n");
        
        System.out.println("Registry test passed");
    }
    
    public static void TestOpcodes(Gameboy gb){
        gb.Reset();
        Cpu cpu = gb.cpu;
        LinkedList<Op> failed = new LinkedList<Op>();
        Op op;
        //Process is as follows
        //Run Opcode, Test results, If results do not match expected record it
        //Reset for next operation
        
        
        //Op 0x00 - NOP takes 1 m time does nothing
        op = cpu.opcodes.Fetch(0x00);
        op.Invoke();
        if(cpu.clock.delM() != 1)
            failed.add(op);
        gb.Reset();
        
        //Op 0x01 - 16Bit immediate into BC
        gb.cpu.reg.pc(0x8000);
        gb.mmu.ww(0x8001, 300);
        cpu.reg.pcpp(1);
        op = cpu.opcodes.Fetch(0x01);
        op.Invoke();
        if(gb.cpu.reg.bc() != 300)
            failed.add(op);
        gb.Reset();
        
        //Op 0x02 - Load into (bc) the value in a
        gb.cpu.reg.a(128);
        gb.cpu.reg.bc(0x8000);
        op = cpu.opcodes.Fetch(0x02);
        op.Invoke();
        if(gb.mmu.rb(0x8000) != 128)
            failed.add(op);
        gb.Reset();
        
        //Op 0x03 - Increment BC
        gb.cpu.reg.bc(5);
        op = cpu.opcodes.Fetch(0x03);
        op.Invoke();
        if(gb.cpu.reg.bc() != 6)
            failed.add(op);
        gb.Reset();
        
        // Op 0x05 - Decrement B
        gb.cpu.reg.b(5);
        op = cpu.opcodes.Fetch(0x05);
        op.Invoke();
        if(gb.cpu.reg.b() != 4)
            failed.add(op);
        gb.Reset();
        
        //Op 0x01 - 8Bit immediate into B
        gb.cpu.reg.pc(0x8000);
        gb.mmu.wb(0x8001, 124);
        cpu.reg.pcpp(1);
        op = cpu.opcodes.Fetch(0x06);
        op.Invoke();
        if(gb.cpu.reg.b() != 124)
            failed.add(op);
        gb.Reset();
        
        //Op 0x07 - Rotate A left
        gb.cpu.reg.a(4);
        op = cpu.opcodes.Fetch(0x07);
        op.Invoke();
        if(gb.cpu.reg.a() != 8)
            failed.add(op);
        gb.Reset();
        
        //To Test CB opcodes, I will test the utility helper functions
        int s0 = 0b00000001;
        if(gb.cpu.opcodes.setBit(0, 0) != s0)
            System.out.println("Failed to set bit 0");
        if(gb.cpu.opcodes.resetBit(s0, 0) != 0)
            System.out.println("Failed to unset bit 0");
        
        s0 = 0b00000010;
        if(gb.cpu.opcodes.setBit(0, 1) != s0)
            System.out.println("Failed to set bit 1");
        if(gb.cpu.opcodes.resetBit(s0, 1) != 0)
            System.out.println("Failed to unset bit 1");
        
        s0 = 0b00000100;
        if(gb.cpu.opcodes.setBit(0, 2) != s0)
            System.out.println("Failed to set bit 2");
        if(gb.cpu.opcodes.resetBit(s0, 2) != 0)
            System.out.println("Failed to unset bit 2");
        
        s0 = 0b00001000;
        if(gb.cpu.opcodes.setBit(0, 3) != s0)
            System.out.println("Failed to set bit 3");
        if(gb.cpu.opcodes.resetBit(s0, 3) != 0)
            System.out.println("Failed to unset bit 3");
        
        s0 = 0b00010000;
        if(gb.cpu.opcodes.setBit(0, 4) != s0)
            System.out.println("Failed to set bit 4");
        if(gb.cpu.opcodes.resetBit(s0, 4) != 0)
            System.out.println("Failed to unset bit 4");
        
        s0 = 0b00100000;
        if(gb.cpu.opcodes.setBit(0, 5) != s0)
            System.out.println("Failed to set bit 5");
        if(gb.cpu.opcodes.resetBit(s0, 5) != 0)
            System.out.println("Failed to unset bit 5");
        
        s0 = 0b01000000;
        if(gb.cpu.opcodes.setBit(0, 6) != s0)
            System.out.println("Failed to set bit 6");
        if(gb.cpu.opcodes.resetBit(s0, 6) != 0)
            System.out.println("Failed to unset bit 6");
        
        s0 = 0b10000000;
        if(gb.cpu.opcodes.setBit(0, 7) != s0)
            System.out.println("Failed to set bit 7");
        if(gb.cpu.opcodes.resetBit(s0, 7) != 0)
            System.out.println("Failed to unset bit 7");
        
        System.out.println("Failed: "+failed.toString());
    }
    
}

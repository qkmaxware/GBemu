/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameboy.game.controller;

import gameboy.game.Cartridge;
import gameboy.game.header.RomInfo;
import java.util.Arrays;
import java.util.Calendar;

/**
 *
 * @author Colin Halseth
 */
public class MBC3 implements MBC{

    private class RTC{
        public int s;
        public int m;
        public int h;
        public int day;
        public boolean halt = false;
        public boolean carry = false;
        
        private Calendar calendar;
        public int rtcRegister = 0x08;
        
        public RTC(){
            calendar = Calendar.getInstance();
            LatchCurrent();
        }
        
        public void LatchCurrent(){
            s = calendar.get(Calendar.SECOND)   & 0xFF;
            m = calendar.get(Calendar.MINUTE)   & 0xFF;
            h = calendar.get(Calendar.HOUR)     & 0xFF;
            
            day = calendar.get(Calendar.DAY_OF_WEEK);
        }
        
        public void Reset(){
            rtcRegister = 0x08;
            halt = false;
            carry = false;
            LatchCurrent();
        }
        
        public int rb(){
            switch(rtcRegister){
                case 0x08: //Seconds
                    return this.s;
                case 0x09: //Minutes
                    return this.m;
                case 0x0A: //Hours
                    return this.h;
                case 0x0B: //Lower 8 bits of the day
                    return this.day & 0xFF;
                case 0x0C: //Upper bit of day plus flags in bits 6 and 7
                    return ((this.day >> 8) & 0b1) | (halt ? 0x40 : 0) | (carry ? 0x80 : 0);
            }
            return 0xFF;
        }
        
        public void wb(int value){
            switch(rtcRegister){
                case 0x08: //Seconds
                    this.s = value & 0xFF;
                    break;
                case 0x09: //Minutes
                    this.m = value & 0xFF;
                    break;
                case 0x0A: //Hours
                    this.h = value & 0xFF;
                    break;
                case 0x0B: //Lower 8 bits of the day
                    this.day &= ~(0xFF);
                    this.day |= (value & 0xFF);
                    break;
                case 0x0C: //Upper 8 bits of day plus flags
                    this.day &= 0xFF;
                    this.day |= (value >> 8) & 0xFF;
                    
                    this.halt = (value & 0x40) != 0;
                    this.carry = (value & 0x80) != 0;
                    break;
            }
        }
    }
    
    public static final int ERAM_SIZE = 32768;  //32KB eram
    private int[] eram = new int[ERAM_SIZE];    //External Cartridge RAM
    
    private RTC rtc = new RTC();
    private int rombank = 1;
    private int rambank = 0;
    private boolean ramEnabled = false;
    private boolean rtcEnabled = false;
    private boolean rtcReadEnabled = false;
    private boolean rtcLatchNext = false;
    
    private Cartridge cart;
    
    public MBC3(Cartridge cart){
        this.cart = cart;
    }
    
    public void Reset(){
        Arrays.fill(eram, 0);
        rtc.Reset();
        
        ramEnabled = false;
        rtcLatchNext = false;
        rtcReadEnabled = false;
        rtcEnabled = false;
        
        rambank = 0;
        rombank = 1;
    }
    
    private static boolean in(int x, int lower, int upper) {
        return lower <= x && x <= upper;
    }
    
    public int[] getRam(){
        return this.eram;
    }
    
    //TODO READ/WRITE RTC registers
    public int rb(int addr){
        //Create the appropriate offsets if required
        int romoff = GetRomOffset(); //Rom bank 1
        int ramoff = GetRamOffset();
        
        if(in(addr, 0, 0x3FFF)){
            //Cartridge ROM (fixed) (rom bank 0)
            if(cart == null)
                return 0;
            return cart.read(addr);
        }
        else if(in(addr, 0x4000, 0x7FFF)){
            //Cartridge ROM (switchable) (rom bank 1)
            if(cart == null)
                return 0;
            return cart.read(romoff + (addr - 0x4000));
        }
        else if(in(addr, 0xA000, 0xBFFF)){
            //External cartridge RAM OR RTC based on the mode
            if(!rtcReadEnabled && ramEnabled){
                return eram[ramoff + (addr - 0xA000)]; 
            }else if(isRtcOn()){
                return rtc.rb();
            }
        }
        return 0;
    }
    
    public void wb(int addr, int value){
        //Create the appropriate offsets if required
        int romoff = GetRomOffset(); //Rom bank 1
        int ramoff = GetRamOffset();
        
        this.hasOccurredWrite(addr, value);
        
        if(in(addr, 0xA000, 0xBFFF)){
            //External cartridge RAM or RTC registers
            if(this.rtcReadEnabled){
                if(isRtcOn())
                    rtc.wb(value);
            }
            else if(ramEnabled){
                eram[ramoff + (addr - 0xA000)] = value; //eram[ramoffs+(addr&0x1FFF)];
            }
        }
    }
    
    public void hasOccurredWrite( int addr, int value) {
        //Ram and timer enable
        if(addr >= 0 && addr <= 0x1FFF){
            //A value of 0x0A will enable reading and writing to ram and to the RTC, 00 will diable both
            ramEnabled = cart.HasRam() && (value & 0x0F) == 0x0A;
            rtcEnabled = (value & 0x0F) == 0x0A;
        }
        //Rom bank number
        else if(addr >= 0x2000 && addr <= 0x3FFF){
            value &= 0x7F;
            if(value <= 0)
                value = 1;
            
            rombank = value;
            rombank &= (cart.header.romClass.banks - 1);
        }
        //Ram bank number - or - RTC select register
        else if(addr >= 0x4000 && addr <= 0x5FFF){
            //Value in range 0x00-0x03 maps the RAM bank to A000, 0x08-0x0C maps the RTC 
            value &= 0xFF;
            
            if(value >= 00 && value <= 0x03){
                //Map rombank to address 0xA000 to 0xBFFF
                this.rtcReadEnabled = false;
                this.rambank = value;
                this.rambank &= (cart.header.eramClass.banks - 1);
            }else if(value >= 0x08 && value <= 0x0C){
                //Map RTC register to address 0xA000 to 0xBFFF
                this.rtcReadEnabled = true;
                this.rtc.rtcRegister = value;
            }
        }
        //Latch clock data
        else if(addr >= 0x6000 && addr <= 0x7FFF){
            //Writing a 0 then a 1 to this register the current time becomes latched to the RTC register
            //Latched data will not change until latched again
            if(this.rtcLatchNext && (value & 0xFF) == 1){
                rtc.LatchCurrent();
                rtcLatchNext = false;
            }
            this.rtcLatchNext = (value == 0x00);
        }
    }

    /**
     * Is the rtc enabled
     * @return 
     */
    public boolean isRtcOn(){
        return rtcEnabled;
    }
    
    /**
     * Get the offset value to use for ram access
     * @return 
     */
    @Override
    public int GetRamOffset(){
        return rambank * 0x2000;
    }
    
    /**
     * Get the offset value to use for rom access
     * @return 
     */
    @Override
    public int GetRomOffset(){
        return rombank * 0x4000;
    }
    
}

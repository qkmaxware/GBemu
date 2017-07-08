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
public class Registry {

    //Internal registry values --------------------------------------------
    private int a, b, c, d, e;
    private int flags;
    private int high, low;
    private int sp, pc;

    //Manipulators --------------------------------------------------------
    public void Reset(){
        a = 0; b = 0; c = 0; d = 0; e = 0;
        flags = 0;
        high = 0; low = 0;
        sp = 0; pc = 0;
    }
    
    //Accessors -----------------------------------------------------------
    //8BIT ----------------------------------------------------------------
    public int a() {
        return a;
    }

    public void a(int i) {
        a = i & Metrics.BIT8;
    }

    public int b() {
        return b;
    }

    public void b(int i) {
        b = i & Metrics.BIT8;
    }
    
    public int c() {
        return c;
    }
    
    public void c(int i) {
        c = i & Metrics.BIT8;
    }

    public int d() {
        return d;
    }

    public void d(int i) {
        d = i & Metrics.BIT8;
    }
    
    public int e() {
        return e;
    }

    public void e(int i) {
        e = i & Metrics.BIT8;
    }
    
    public int f() {
        return flags;
    }

    public void f(int i) {
        //Last 4 digits are always 0 even if one is written to
        flags = i & Metrics.BIT8 & 0b11110000;
    }
    
    public boolean zero(){
        return (flags & 0b10000000) == 0b10000000;
    }
    
    public void zero(boolean b){
        flags = (flags | (b ? 0b11111111 : 0b01111111));
    }
    //TODO what is the 'n' flag
    public boolean subtract(){
        return (flags & 0b01000000) == 0b01000000;
    }
    
    public void subtract(boolean b){
        flags = (flags | (b ? 0b11111111 : 0b10111111));
    }
    
    public boolean halfcarry(){
        return (flags & 0b00100000) == 0b00100000;
    }
    
    public void halfcarry(boolean b){
        flags = (flags | (b ? 0b11111111 : 0b11011111));
    }
    
    public boolean carry(){
        return (flags & 0b00010000) == 0b00010000;
    }
    
    public void cary(boolean b){
        flags = (flags | (b ? 0b11111111 : 0b11101111));
    }
    
    public int h() {
        return high;
    }

    public void h(int i) {
        high = i & Metrics.BIT8;
    }
    
    public int l() {
        return low;
    }
    
    public void l(int i) {
        low = i & Metrics.BIT8; 
    }
    
    //16BIT ---------------------------------------------------------------
    //TODO Confirm high low extraction methods
    public int sp(){
        return sp;
    }
    public void sp(int i){
        sp = i & Metrics.BIT16;
    }
    
    //Increment the sp
    public int sppp(int i){
        int k = sp;
        sp(sp += i);
        return k;
    }
    
    public int pc(){
        return pc;
    }
    
    //Increment the pc
    public int pcpp(int i){
        int k = pc;
        pc(pc += i);
        return k;
    }
   
    public void pc(int i){
        pc = i & Metrics.BIT16;
    }

    public int af(){
        //High a, Low f
        int la = a << 8;
        return la | flags;
    }
    
    public void af(int i){
        //TODO the proper way to do this?
        flags = i & Metrics.BIT8;
        a = (i >> 8) & Metrics.BIT8;
    }
    
    public int bc(){
        //High b, Low c
        int lb = b << 8;
        return lb | c;
    }
    
    public void bc(int i){
        //TODO the proper way to do this?
        c = i & Metrics.BIT8;
        b = (i >> 8) & Metrics.BIT8;
    }
    
    public int de(){
        //High d, Low e
        int ld = d << 8;
        return ld | e;
    }
    
    public void de(int i){
        //TODO the proper way to do this?
        e = i & Metrics.BIT8;
        d = (i >> 8) & Metrics.BIT8;
    }
   
    public int hl(){
        //High h, Low l
        int lh = high << 8;
        return lh | low;
    }

    public void hl(int i){
        //TODO the proper way to do this?
        low = i & Metrics.BIT8;
        high = (i >> 8) & Metrics.BIT8;
    }
}

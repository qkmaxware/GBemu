/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameboy.gpu;

/**
 *
 * @author Colin Halseth
 */
public class Sprite {
    
    public enum Priority{
        AboveBackground(0), BelowBackground(1);
        
        private int val = 0;
        Priority(int v){
            val = v;
        }
        
        public int value(){
            return val;   
        }
    }
    
    public enum XOrientation{
        Normal(0), Flipped(1);
        
        private int val = 0;
        XOrientation(int v){
            val = v;
        }
        
        public int value(){
            return val;   
        }
    }
    
    public enum YOrientation{
        Normal(0), Flipped(1);
        
        private int val = 0;
        YOrientation(int v){
            val = v;
        }
        
        public int value(){
            return val;   
        }
    }
    
    public enum Palette{
        Zero(0), One(1);
        
        private int val = 0;
        Palette(int v){
            val = v;
        }
        
        public int value(){
            return val;   
        }
    }
    
    protected int id;
    
    //Position
    public int y = -16;
    public int x = -8;
    
    //Data tile number
    public int tile = 0;
    
    //Options
    public Priority priority = Priority.AboveBackground;
    public XOrientation xflip = XOrientation.Normal;
    public YOrientation yflip = YOrientation.Normal;
    public Palette objPalette = Palette.One;
    
    public Sprite(int id){
        this.id = id;
    }
    
    public void Reset(){
        y = -16;
        x = -8;
        tile = 0;
        priority = Priority.AboveBackground;
        xflip = XOrientation.Normal;
        yflip = YOrientation.Normal;
        objPalette = Palette.One; 
    }
    
}

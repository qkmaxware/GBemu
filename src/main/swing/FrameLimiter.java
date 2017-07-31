/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.swing;

/**
 *
 * @author Colin Halseth
 */
public class FrameLimiter {
    
    private boolean enabled = false;
    private long framedelay = -1;
    private long lastPoll;
    
    public FrameLimiter(){
        this.lastPoll =  System.currentTimeMillis();
    }
    
    public void setLimit(long i){
        framedelay = i;
        enabled = (i >= 0);
    }
    
    public void waitUntil(){
        if(enabled){
            while(deltaTime() < framedelay){
                //Wait
            }
            step();
        }
    }
    
    public long deltaTime(){
        long l = System.currentTimeMillis();
        long q = l - lastPoll;
        return q;
    }
    
    public void step(){
        lastPoll = System.currentTimeMillis();
    }
}

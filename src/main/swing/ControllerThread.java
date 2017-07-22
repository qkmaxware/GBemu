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
public class ControllerThread extends Thread{
    
    private volatile boolean kill = false;
    private volatile boolean pause = false;
    
    private Action action;
    
    public ControllerThread(Action action){
        this.action = action;
    }
    
    @Override
    public void run(){
        while(!kill){
            if(pause)
                continue;
            
            if(action != null){
                action.Invoke();
            }
        }
    }
    
    public void killThread(){
        this.kill = true;
    }
    
    public void pauseThread(){
        this.pause = true;
    }
    
    public void playThread(){
        if(this.isAlive()){
            pause = false;
        }else{
            pause = false;
            this.start();
        }
    }
    
}

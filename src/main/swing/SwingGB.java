/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.swing;

import gameboy.Gameboy;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.swing.JFrame;

/**
 *
 * @author Colin Halseth
 */
public class SwingGB extends JFrame{
    
    private Gameboy gb = new Gameboy();
    private ControllerThread thread;
    
    private ConcurrentLinkedQueue<Action> stepListeners = new ConcurrentLinkedQueue<Action>();
    private ConcurrentLinkedQueue<Action> onetimeStepListeners = new ConcurrentLinkedQueue<Action>();
    
    public SwingGB(boolean autoplay){
        super();
        
        //Create the render panel
        RenderPanel panel = new RenderPanel(this.gb.gpu.canvas);
        panel.setPreferredSize(new Dimension(166, 144)); //Same size as GB canvas
        this.add(panel);
        this.pack();
        
        //Assign the event listeners
        gb.OnBufferReady(() -> {
            panel.repaint();
        });
        
        thread = new ControllerThread(()-> {
            Step();
        });
        
        //Create the close event
        this.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e){
                Stop();
            }
        });
        
        gb.Reset();
        
        //If told to autoplay, then play 
        if(autoplay)
            thread.playThread();
    }
    
    public void Play(){
        thread.playThread();
    }
    
    public void Pause(){
        thread.pauseThread();
    }
    
    public void Stop(){
        thread.killThread();
    }
    
    public void Once(Action action){
        this.onetimeStepListeners.add(action);
    }
    
    public void On(Action action){
        this.stepListeners.add(action);
    }
    
    public void Off(Action action){
        this.stepListeners.remove(action);
    }
    
    public Gameboy GetGameboy(){
        return this.gb;
    }
    
    private void Step(){
        //Play one step of the Gameboy 
        gb.Dispatch();
        
        //Perform listeners
        for(Action listener : stepListeners){
            listener.Invoke();
        }
        
        while(!this.onetimeStepListeners.isEmpty()){
            this.onetimeStepListeners.remove().Invoke();
        }
    }
    
}
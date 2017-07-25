/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameboy.io;

import gameboy.IMemory;
import gameboy.MemoryMap;
import java.awt.event.KeyEvent;
import java.util.Arrays;

/**
 *
 * @author Colin Halseth
 */
public class Input implements IMemory{
    
    private int[] rows = new int[]{0x0F, 0x0F};
    private int colidx = 0;
    private Timer timer = new Timer();
    
    public static enum Key{
        Up, Down, Left, Right, Select, Start, A, B
    }
    
    private int up = KeyEvent.VK_W;
    private int down = KeyEvent.VK_S;
    private int left = KeyEvent.VK_A;
    private int right = KeyEvent.VK_D;
    private int start = KeyEvent.VK_ENTER;
    private int select = KeyEvent.VK_SHIFT;
    private int a = KeyEvent.VK_X;
    private int b = KeyEvent.VK_Z;
    
    public void Reset(){
        Arrays.fill(rows, 0x0F);
        timer.Reset();
        colidx = 0;
    }
    
    public void SetKey(Key key, KeyEvent evt){
        switch(key){
            case Up:
                this.up = evt.getKeyCode();
                break;
            case Down:
                this.down = evt.getKeyCode();
                break;
            case Left:
                this.left = evt.getKeyCode();
                break;
            case Right:
                this.right = evt.getKeyCode();
                break;
            case Select:
                this.select = evt.getKeyCode();
                break;
            case Start:
                this.start = evt.getKeyCode();
                break;
            case A:
                this.a = evt.getKeyCode();
                break;
            case B:
                this.b = evt.getKeyCode();
                break;
        }
    }
    
    public boolean IsKeyDown(Key key){
        switch(key){
            case Up:
                return (rows[1] &= 0x4) == 0;
            case Down:
                return (rows[1] &= 0x8) == 0;
            case Left:
                return (rows[1] &= 0x2) == 0;
            case Right:
                return (rows[1] &= 0x1) == 0;
            case Select:
                return (rows[1] &= 0x8) == 0;
            case Start:
                return (rows[1] &= 0x4) == 0;
            case A:
                return (rows[1] &= 0x1) == 0;
            case B:
                return (rows[1] &= 0x2) == 0;
        }
        return false;
    }
    
    public void KeyDown(KeyEvent evt){
        int keycode = evt.getKeyCode();
        if(keycode == up){
            rows[1] &= 0xB;
        }
        else if(keycode == down){
            rows[1] &= 0x7;
        }
        else if(keycode == left){
            rows[1] &= 0xD;
        }
        else if(keycode == right){
            rows[1] &= 0xE;
        }
        else if(keycode == start){
            rows[0] &= 0x7;
        }
        else if(keycode == select){
            rows[0] &= 0xB;
        }
        else if(keycode == a){
            rows[0] &= 0xE;
        }
        else if(keycode == b){
            rows[0] &= 0xD;
        }
    }
    
    public void KeyUp(KeyEvent evt){
        int keycode = evt.getKeyCode();
        if(keycode == up){
            rows[1] |= 0x4;
        }
        else if(keycode == down){
            rows[1] |= 0x8;
        }
        else if(keycode == left){
            rows[1] |= 0x2;
        }
        else if(keycode == right){
            rows[1] |= 0x1;
        }
        else if(keycode == start){
            rows[0] |= 0x8;
        }
        else if(keycode == select){
            rows[0] |= 0x4;
        }
        else if(keycode == a){
            rows[0] |= 0x1;
        }
        else if(keycode == b){
            rows[0] |= 0x2;
        }
    }
    
    public int rb(int addr){
        if(addr == 0xFF00){
            switch(colidx){
                case 0x10:
                    return rows[0];
                case 0x20:
                    return rows[1];
            }
        }
        return 0;
    }
    
    public void wb(int addr, int value){
        if(addr == 0xFF00){
            colidx = value & 0x30;
        }
    }

    public void SetMMU(MemoryMap mmu){}
    
}

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
public class Op {
    
   public static interface Action{
       public void Invoke();
   }
   
   private int hex;
   private String name;
   private Action action;
   
   public Op(int hex, String name, Op[] mapping, Action action){
        this.hex = hex;
        this.name = name;
        if(mapping != null){
            if(mapping[hex] != null) //For my sanity
                System.out.println("Opcode "+name+" replacing "+mapping[hex].name+" at: "+String.format("0x%04X", hex));
            mapping[hex] = this;
        }
        this.action = action;
   }

   public String toString(){
       return name;
   }
   
   public int hashCode(){
       return hex;
   }
   
   public void Invoke(){
       this.action.Invoke();
   }
   
}

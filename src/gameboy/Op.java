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
public class Op {
    
   public static interface Action{
       public void Invoke();
   }
   
   private int hex;
   private String name;
   private Action action;
   
   public Op(int hex, String name, Action action){
       this.hex = hex;
       this.name = name;
       this.action = action;
   }

   public String toString(){
       return name + " - " +String.format("0x%04X", hex);
   }
   
   public int hashCode(){
       return hex;
   }
   
   public void Invoke(){
       this.action.Invoke();
   }
   
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameboy;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 *
 * @author Colin Halseth
 */
public class CartridgeFactory {
    
    public static Cartridge Load(String filename){
        File f = new File(filename);
        byte[] result = new byte[(int)f.length()];
        int totalBytesRead = 0;
        
        try{
            InputStream in = new BufferedInputStream(new FileInputStream(f));
            while(totalBytesRead < result.length){
              int bytesRemaining = result.length - totalBytesRead;
              //input.read() returns -1, 0, or more :
              int bytesRead = in.read(result, totalBytesRead, bytesRemaining); 
              if (bytesRead > 0){
                totalBytesRead = totalBytesRead + bytesRead;
              }
            }
            in.close();
            
            int[] unsignedBytes = new int[result.length];
            for(int i = 0; i< unsignedBytes.length; i++){
                unsignedBytes[i] = Byte.toUnsignedInt(result[i]);
            }
            
            Cartridge c = new Cartridge(unsignedBytes);
            
            return c;
        }
        catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
            return null;
        }
    }
    
}

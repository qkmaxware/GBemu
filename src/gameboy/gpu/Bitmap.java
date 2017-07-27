/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameboy.gpu;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.util.LinkedList;
import javax.imageio.ImageIO;

/**
 *
 * @author Colin Halseth
 */
public class Bitmap {
 
    protected BufferedImage img;
    protected int[] argb;
    protected int width;
    protected int height;
    
    /**
     * Create a bitmap from a buffered image
     * @param img 
     */
    public Bitmap(BufferedImage img){
        //Convert buffered image to type int_argb, basically copy the old image into a new one of the right form
        //Idk if this preserves alpha will have to test
        this.img = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = this.img.createGraphics();
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();
        
        //Set the standard parameters
        this.width = img.getWidth();
        this.height = img.getHeight();
        argb  = ((DataBufferInt)this.img.getRaster().getDataBuffer()).getData();
    }
    
    /**
     * Fill the image with a single color
     * @param c 
     */
    public void Fill(Color c){
        for(int x = 0; x < this.width; x++){
            for(int y = 0; y < this.height; y++)
                this.SetColor(x, y, c);
        }
    }
    
    /**
     * Load a bitmap from your file-system
     * @param f 
     */
    public Bitmap(File f){
        try{
            BufferedImage img = ImageIO.read(f);
            
            this.img = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = this.img.createGraphics();
            g2d.drawImage(img, 0, 0, null);
            g2d.dispose();

            //Set the standard parameters
            this.width = img.getWidth();
            this.height = img.getHeight();
            argb  = ((DataBufferInt)this.img.getRaster().getDataBuffer()).getData();
        }
        catch(Exception e){
            this.argb = null; this.img = null;
            this.width = 0; this.height = 0;
            throw new RuntimeException("File "+f.toString()+" was not found. Bitmap creation failed");
        }
    }
    
    /**
     * Create an empty bitmap of a certain size
     * @param width
     * @param height 
     */
    public Bitmap(int width, int height){
        BufferedImage img = new BufferedImage(width,height, BufferedImage.TYPE_INT_ARGB);
        this.img = img;
        
        //Set the standard parameters
        this.width = img.getWidth();
        this.height = img.getHeight();
        argb  = ((DataBufferInt)this.img.getRaster().getDataBuffer()).getData();
    }
    
    /**
     * Copy constructor
     * @param bmp
     */
    public Bitmap(Bitmap bmp){
        BufferedImage img = bmp.GetImage();
        this.img = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = this.img.createGraphics();
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();
        
        //Set the standard parameters
        this.width = img.getWidth();
        this.height = img.getHeight();
        argb  = ((DataBufferInt)this.img.getRaster().getDataBuffer()).getData();
    }
    
    /**
     * Create a new bitmap from several tiled bitmaps, assumes all bitmaps are the same size
     * @param tiledBitmaps 
     */
    public Bitmap(Bitmap[][] tiledBitmaps){
        int subsize = tiledBitmaps[0][0].GetWidth();
        int subheight = tiledBitmaps[0][0].GetHeight();
        this.width = tiledBitmaps[0].length * subsize;
        this.height = tiledBitmaps.length * subheight;
        this.img = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);
        argb  = ((DataBufferInt)this.img.getRaster().getDataBuffer()).getData();
        
        //Copy pixel values into the array
        for(int i = 0; i < tiledBitmaps[0].length; i++){
            for(int j = 0; j < tiledBitmaps.length; j++){
                Bitmap bmp = tiledBitmaps[j][i];
                for(int ii = 0; ii < bmp.GetWidth(); ii++){
                    for(int jj = 0; jj < bmp.GetHeight(); jj++){
                        this.SetARGB(j*subsize + ii, i*subheight + jj, bmp.GetARGB(ii, jj));
                    }
                }
            }
        }
    }
    
    /**
     * Slice a portion image into smaller sub-images
     * @param x starting x coordinate
     * @param y starting y coordinate
     * @param number the number of sub-images
     * @param per_row the number of images per row
     * @param width the width of the sub images
     * @param height the height of the sub images
     * @return 
     */
    public Bitmap[] Slice(int x, int y, int number, int per_row, int width, int height){
        Bitmap[] slices = new Bitmap[number];
        
        int rowval = 0; int colval = 0;
        
        for(int n = 0; n < number; n++){
            Bitmap img = new Bitmap(width, height);
            int px = x + (rowval*width);
            int py = y + (colval*height);
            
            for(int i = 0; i < width; i++){
                for(int j = 0; j < height; j++){
                    if(px+i < this.GetWidth() && py+j < this.GetHeight()){
                        int color = this.GetARGB(px+i, py+j);
                        img.SetARGB(i, j, color);
                    }
                }
            }
            
            slices[n] = img;
            rowval ++;
            if(rowval > per_row){
                rowval = 0;
                colval += 1;
            }
        }
        return slices;
    }
    
    /**
     * Slice a bitmap image into several smaller images
     * @param x
     * @param y
     * @return 
     */
    public Bitmap[] Slice(int x, int y){
        LinkedList<Bitmap> slices = new LinkedList<>();
        int sizeX = this.GetWidth()/x;
        int sizeY = this.GetHeight()/y;
        for(int j = 0; j < y; j++){
            for(int i = 0; i < x; i++){
                Bitmap map = new Bitmap(sizeX,sizeY);
                
                int startX = i*sizeX; int endX = Math.min((i+1)*sizeX, this.GetWidth());
                int startY = j*sizeY; int endY = Math.min((j+1)*sizeY, this.GetHeight());
                
                for(int a = 0; startX + a < endX; a++){
                    for(int b = 0;startY + b < endY; b++){
                        map.SetARGB(a, b, this.GetARGB(startX + a, startY + b));
                    }
                }
                
                slices.add(map);
            }
        }
        Bitmap[] slicS = new Bitmap[slices.size()];
        slices.toArray(slicS);
        return slicS;
    }
    
    /**
     * Create a new bitmap that is a tiled version of this bitmap
     * @param x
     * @param y
     * @return 
     */
    public Bitmap Repeat(int x, int y){
        x = (x < 1)?1:x; y = (y < 1)?1:y;
        
        Bitmap[][] repeated = new Bitmap[x][y];
        for(int i = 0; i < x; i++){
            for(int j = 0; j < y; j++){
                repeated[i][j] = this;
            }
        }
        
        return new Bitmap(repeated);
    }
    
    /**
     * Get the raster representation of this bitmap
     * @return 
     */
    public int[] GetRaster(){
        return this.argb;
    }
    
    /**
     * Set the raster of this bitmap
     * @param raster 
     */
    public void SetRaster(int[] raster){
        System.arraycopy(raster, 0, this.argb, 0, Math.min(raster.length, this.argb.length));
    }
    
    /**
     * Get the buffered image represented by this bitmap
     * @return 
     */
    public BufferedImage GetImage(){
        return this.img;
    }
    
    /**
     * Get the color of this pixel as an integer argb
     * @param x
     * @param y
     * @return 
     */
    public int GetARGB(int x, int y){
        return this.argb[y*width + x];
    }
    
    /**
     * Set the color of this pixel as an integer argb
     * @param x
     * @param y
     * @param argb 
     */
    public void SetARGB(int x, int y, int argb){
        int idx = y*width + x;
        if(idx >= 0 && idx < this.argb.length)
            this.argb[idx] = argb;
    }
    
    /**
     * Get the color of this pixel as a Color object
     * @param x
     * @param y
     * @return 
     */
    public Color GetColor(int x, int y){
        return new Color(GetARGB(x,y),true);
    }
    
    /**
     * Sets the color of this pixel from a Color object
     * @param x
     * @param y
     * @param color 
     */
    public void SetColor(int x, int y, Color color){
        this.SetARGB(x, y, color.getRGB());
    }
   
    /**
     * Gets the width of this bitmap
     * @return 
     */
    public int GetWidth(){
        return this.width;
    }
    
    /**
     * Gets the height of this bitmap
     * @return 
     */
    public int GetHeight(){
        return this.height;
    }
    
}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameboy.gpu;

import gameboy.IMemory;
import gameboy.MemoryMap;
import java.awt.Color;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

/**
 *
 * @author Colin Halseth
 */
public class Gpu implements IMemory{
    
    public static final int VRAM_SIZE = 8192;
    public static final int OAM_COUNT = 40;
    public static final int LCD_HEIGHT = 144;
    public static final int LCD_WIDTH = 160;
    
    public static final int GPU_SCANLINEOAM = 2;
    public static final int GPU_SCANLINEVRAM = 3;
    public static final int GPU_HBLANK = 0;
    public static final int GPU_VBLANK = 1;
    
    //References
    private MemoryMap mmu;
    
    //Drawing board
    public final Bitmap canvas = new Bitmap(LCD_WIDTH, LCD_HEIGHT);
    
    //VRAM
    private final int[] vram = new int[VRAM_SIZE];
    
    //Tilemap
    public final int[][][] tilemap = new int[512][8][8];
    
    //Colour pallet
    private final ColourPallet colours = new ColourPallet();
    private final Color[] bgpallet = new Color[4];
    private final Color[] obj0pallet = new Color[4];
    private final Color[] obj1pallet = new Color[4];
    
    //OAM data
    private final int[] oam = new int[OAM_COUNT * 4];
    public final Sprite[] oam_data = new Sprite[OAM_COUNT];
    private final Sprite[] oam_data_sorted = new Sprite[OAM_COUNT];
    
    //Internal values
    private final HashMap<Integer, Integer> reg = new HashMap<Integer, Integer>();
    private int yscroll = 0;
    private int xscroll = 0;
    private int curline = 0;
    private int gpumode = 2;
    private int clock = 0;
    
    public boolean lcdon = true;
    public boolean largeobj = false;
    public boolean objon = true;
    public boolean bgon = true;
    
    public int bgtilebase = 0x0000;
    public int bgmapbase = 0x1800;
    
    public Gpu(){ 
        //OAM data
        Arrays.fill(oam, 0);
        for(int i = 0; i < oam_data.length; i++){
            oam_data[i] = new Sprite(i);
            oam_data_sorted[i] = oam_data[i];
        }
        
        Reset();
    }
    
    public void Step(int step){
        clock += step;
        switch(gpumode){
            case 2: //Scanline OAM
                
                break;
            case 3: //Scanline VRAM
                
                break;
            case 0: //Horizontal Blank

                break;
            case 1: //Vertical Blank

                break;
        }
    }
    
    @Override
    public void Reset() {
        //Drawing board
        canvas.Fill(Color.WHITE);
        
        //Tilemap
        for (int i = 0; i < 512; i++) {
            for (int j = 0; j < 8; j++) {
                for (int k = 0; k < 8; k++) {
                    tilemap[i][j][k] = 0;
                }
            }
        }
        
        //VRAM
        Arrays.fill(vram, 0);
        
        //Reset OAM
        Arrays.fill(oam, 0);
        for(int i = 0; i < oam_data.length; i++){
            oam_data[i].Reset();
        }
        
        //Colour Pallet
        Arrays.fill(bgpallet, colours.bg.WHITE);
        Arrays.fill(obj0pallet, colours.obj0.WHITE);
        Arrays.fill(obj1pallet, colours.obj1.WHITE);
        
        //Inernal values
        yscroll = 0;
        xscroll = 0;
        curline = 0;
        gpumode = 2;
        
        lcdon = true;
        largeobj = false;
        objon = true;
        bgon = true;
    
        bgtilebase = 0x0000;
        bgmapbase = 0x1800;
        
        reg.clear();
    }
    
    protected void updatetile(int addr, int value){
        int saddr = addr;
        if((addr & 1) != 0){
            saddr --;
            addr --;
        }
        
        int tile = (addr << 4) & 511;
        int y = (addr >> 1) & 7;
        
        int sx;
        for(int x = 0; x < 8; x++){
            sx = 1 << (7 - x);
            tilemap[tile][y][x] = ((vram[saddr] & sx) != 0 ? 1 : 0) | ((vram[saddr + 1] & sx) != 0 ? 2 : 0);
        }
        
    }
    
    protected void updateoam(int addr, int value) {
        addr -= 0xFE00;
        int obj = addr >> 2; //Divide by 4
        if (obj > 40 || obj < 0) {
            return;
        }

        switch (addr & 3) {
            case 0:
                oam_data[obj].y = value - 16;
                break;
            case 1:
                oam_data[obj].x = value - 8;
                break;
            case 2:
                if (largeobj) {
                    oam_data[obj].tile = (value & 0xFE);
                } else {
                    oam_data[obj].tile = value;
                }
                break;
            case 3:
                oam_data[obj].priority = ((value & 0x80) != 0 ? Sprite.Priority.BelowBackground : Sprite.Priority.AboveBackground );
                oam_data[obj].xflip = ((value & 0x20) != 0 ? Sprite.XOrientation.Flipped : Sprite.XOrientation.Normal);
                oam_data[obj].yflip = ((value & 0x40) != 0 ? Sprite.YOrientation.Flipped : Sprite.YOrientation.Normal);
                oam_data[obj].objPalette = ((value & 0x10) != 0 ? Sprite.Palette.One : Sprite.Palette.One);
                break;
        }

	//Resort sorted oam_data;
        Arrays.sort(oam_data_sorted, new Comparator<Sprite>(){
            @Override
            public int compare(Sprite t, Sprite t1) {
                if(t.x > t1.x)
                    return -1;
                if(t.id > t1.id)
                    return -1;
                return 0;
            }
        });
    }

    @Override
    public int rb(int addr) {
        //OAM
        if(addr >= 0xFE00 && addr <= 0xFE9F){
            return oam[addr & 0xFF];
        }
        
        //VRAM
        switch(addr){
            case 0xFF40:    //LCD Control
                return (lcdon ? 0x80 : 0) | (largeobj ? 0x04 : 0) | (objon ? 0x02 : 0) | (bgon ? 0x01 : 0) | ((bgtilebase == 0) ? 0x10 : 0) | ((bgmapbase == 0x1C00) ? 0x08 : 0);
            case 0xFF41:    //LCD Status
                break;
            case 0xFF42:    //Scroll Y         
                return yscroll;
            case 0xFF43:    //Scroll X         
                return xscroll;
            case 0xFF44:    //Current scanline
                return curline;
            case 0xFF45:    //Raster?
                break;
            case 0xFF47:    //Background Pallet
            case 0xFF48:    //Object Pallet 0
            case 0xFF49:    //Object Pallet 1
            case 0xFF4A:    //Window Y
            case 0xFF4B:    //Window X
            default:
                if(reg.containsKey(addr))
                    return reg.get(addr);
                return 0;
        }
        return 0;
    }

    @Override
    public void wb(int addr, int value) {
        //OAM
        if(addr >= 0xFE00 && addr <= 0xFE9F){
            oam[addr & 0xFF] = value;
            updateoam(addr, value);
            return;
        }
        
        reg.put(addr, value);
        switch(addr){
            case 0xFF40:    //LCD Control
                lcdon = (value & 0x80) != 0;
                bgtilebase = ((value&0x10) != 0)?0x0000:0x0800;
                bgmapbase = ((value&0x08) != 0)?0x1C00:0x1800;
                largeobj = (value&0x04) != 0;
                objon = (value&0x02) != 0;
                bgon = (value&0x01) != 0;
                break;
            case 0xFF41:    //LCD Status
                break;
            case 0xFF42:    //Scroll Y         
                yscroll = value;
                break;
            case 0xFF43:    //Scroll X         
                xscroll = value;
                break;
            case 0xFF44:    //Current scanline
                curline = value;
                break;
            case 0xFF45:    //Raster?
                break;
            case 0xFF46:    //Object Attribute Memory OAM
                for(int i = 0; i < 160; i++){
                    int v = mmu.rb((value << 8) + i);
                    oam[i] = v;
                    updateoam(0xFE00 + i, v);
                }
                break;
            case 0xFF47:    //Background Pallet
                for(int i = 0; i < 4; i++){
                    int v = (value >> (i * 2)) & 3; //1,2,3
                    switch(v){
                        case 0: bgpallet[i] = colours.bg.WHITE; break;
                        case 1: bgpallet[i] = colours.bg.LIGHT; break;
                        case 2: bgpallet[i] = colours.bg.MEDIUM; break;
                        case 3: bgpallet[i] = colours.bg.DARK; break;
                    }
                }
                break;
            case 0xFF48:    //Object Pallet 0
                for(int i = 0; i < 4; i++){
                    int v = (value >> (i * 2)) & 3; //1,2,3
                    switch(v){
                        case 0: obj0pallet[i] = colours.obj0.WHITE; break;
                        case 1: obj0pallet[i] = colours.obj0.LIGHT; break;
                        case 2: obj0pallet[i] = colours.obj0.MEDIUM; break;
                        case 3: obj0pallet[i] = colours.obj0.DARK; break;
                    }
                }
                break;
            case 0xFF49:    //Object Pallet 1
                for(int i = 0; i < 4; i++){
                    int v = (value >> (i * 2)) & 3; //1,2,3
                    switch(v){
                        case 0: obj1pallet[i] = colours.obj1.WHITE; break;
                        case 1: obj1pallet[i] = colours.obj1.LIGHT; break;
                        case 2: obj1pallet[i] = colours.obj1.MEDIUM; break;
                        case 3: obj1pallet[i] = colours.obj1.DARK; break;
                    }
                }
                break;
            case 0xFF4A:    //Window Y
                break;
            case 0xFF4B:    //Window X
                break;
        }
    }
    
    @Override
    public void SetMMU(MemoryMap mmu){
        this.mmu = mmu;
    }
    
}

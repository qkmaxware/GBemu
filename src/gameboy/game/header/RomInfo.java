/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameboy.game.header;

/**
 * 
 * @author Colin
 */
public class RomInfo {
    
    //http://gbdev.gg8.se/wiki/articles/The_Cartridge_Header
    
    public final String title;
    public final Region region;
    public final SgbSupport sgb;
    public final CgbSupport cgb;
    public final String manufacturerCode;
    public final int licenceeCode;
    public final LicenceeCode licencee;
    public final CartType cartType;
    public final int romSize;
    public final RomClass romClass;
    public final int ramSize;
    public final RamClass eramClass;
    public final int headerChecksum;
    public final int globalChecksum;
    
    public final int version;
    
    public RomInfo(int[] romBank){
       
        //Title
        String t = "Unknown";
        try{
            byte[] btt = new byte[16];
            for(int i = 0x0134, j=0; i <= 0x0143; i++, j++){
                btt[j] = (byte)romBank[i];
            }
            t = new String(btt,"US-ASCII");
        }catch(Exception e){}
        this.title = t.trim();
        
        //Manufacturer's code (in some older cartridges)
        String mancode = "Unknown";
        try{
            byte[] btt = new byte[4];
            for(int i = 0x013F, j=0; i <= 0x0142; i++, j++){
                btt[j] = (byte)romBank[i];
            }
            mancode = new String(btt,"US-ASCII");
        }catch(Exception e){}
        this.manufacturerCode = mancode.trim();
        
        //CGB Flag
        switch(romBank[0x0143]){
            case 0x80:
                this.cgb = CgbSupport.CbgAllowed;
                break;
            case 0xC0:
                this.cgb = CgbSupport.CbgRequired;
                break;
            default:
                this.cgb = CgbSupport.Unknown;
        }
        
        //New Licensee Code
        int ascii = (romBank[0x0144] << 8) | romBank[0x0145];
        this.licenceeCode = ascii;
        this.licencee = LicenceeCode.decode(ascii);
        
        //SGB Flag
        switch(romBank[0x0146]){
            case 0x00:
                this.sgb = SgbSupport.None;
                break;
            case 0x03:
                this.sgb = SgbSupport.SGB;
                break;
            default:
                this.sgb = SgbSupport.Unknown;
                break;
        }
        
        //Destination code
        this.region = Region.decode(romBank[0x014A]);
        
        //Cart type
        this.cartType = CartType.decode(romBank[0x0147]);
        
        //Rom size
        this.romClass = RomClass.decode(romBank[0x148]);
        this.romSize = romBank[0x148];
        
        //Ram size
        this.ramSize = romBank[0x0149];
        this.eramClass = RamClass.decode(romBank[0x0149]);
        
        //Rom version
        this.version = romBank[0x014C];
        
        //Header checksum
        this.headerChecksum = romBank[0x014D];
        
        //Global checksum
        this.globalChecksum = (romBank[0x014E] << 8) | romBank[0x014F];
        
    }
    
    public String toString(){
        String details = "Rom Details\n: Name: "+this.title+" ("+this.version+")";
        details+="\n: Region: "+this.region;
        details+="\n: Color Type: "+this.cgb;
        details+="\n: SGB Support: "+this.sgb;
        details+="\n: Cartridge Type: "+this.cartType.mbc;
        details+="\n: LicenceCode: "+this.licencee + "("+ this.licenceeCode+")";
        details+="\n: Rom size: "+this.romClass.size+"KB";
        details+="\n   - Banks: "+this.romClass.banks;
        details+="\n: External Ram size: "+this.eramClass.size+"KB";
        details+="\n   - Banks: "+this.eramClass.banks;
        details+="\n: Header Checksum: "+this.headerChecksum;
        details+="\n: Global Checksum: "+this.globalChecksum;
        return details;
    }
}

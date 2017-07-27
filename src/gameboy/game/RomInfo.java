/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameboy.game;

/**
 * 
 * @author Colin
 */
public class RomInfo {
    
    //http://gbdev.gg8.se/wiki/articles/The_Cartridge_Header
    
    public static enum Region{
        Unknown, Japanese, NotJapanese
    }
    
    public static enum SgbSupport{
        Unknown, SGB, None
    }
    
    public static enum CgbSupport{
        Unknown, CbgAllowed, CbgRequired, None
    }
    
    public static enum LicenceeCode{
        Unknown, None, ElectronicArts, Accolade, Kss, SanX, Viacom, Ocean, Taito, Ubisoft, Angel, Absolute, AmericanSammy, LJN, MiltonBradley, LucasArts, Infrogames, Sculptured, TokumaShotenI, VideoSystem, Yonezawa, Konami, Nintendo, Pow, KemcoJapan, Hudson, Atlus, BulletProof, Acclaim, Matchbox, Titus, Interplay, Sci, Misawa, TsukudaOri, Kaneko, Capcom, BAi, PCMComplete, Seta, Bandai, Hector, Banpresto, Malibu, Irem, Activision, HiTechEntertainment, Mattel, Virgin, Broderbund, THQ, Lozc, Chunsoft, Varie, PackInSoft
    }
    
    public static enum CartType{
        Unknown,
        ROM, 
        MBC2,
        MBC1,
        MMM01,
        MBC3, 
        MBC5,
        MBC6, 
        MBC7
    }
    
    public static enum RomSizeClass{
        Unknown, KB32, KB64, KB128, KB256, KB512, MB1, MB2, MB4, MB8, MB1_1, MB1_2, MB1_5
    }
    
    public static enum RamSizeClass{
        Unknown, NONE, KB2, KB8, KB32, KB128, KB64
    }
    
    public final String title;
    public final Region region;
    public final SgbSupport sgb;
    public final CgbSupport cgb;
    public final String manufacturerCode;
    public final int licenceeCode;
    public final LicenceeCode licencee;
    public final CartType cartType;
    public final int romBanks;
    public final int romSize;
    public final RomSizeClass romSizeClass;
    public final int ramBanks;
    public final int ramSize;
    public final RamSizeClass ramSizeClass;
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
        this.title = t;
        
        //Manufacturer's code (in some older cartridges)
        String mancode = "Unknown";
        try{
            byte[] btt = new byte[4];
            for(int i = 0x013F, j=0; i <= 0x0142; i++, j++){
                btt[j] = (byte)romBank[i];
            }
            mancode = new String(btt,"US-ASCII");
        }catch(Exception e){}
        this.manufacturerCode = mancode;
        
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
        switch(ascii){
            case 0x00:
                this.licencee = LicenceeCode.None;
                break;
            case 0x13:
            case 0x69:
                this.licencee = LicenceeCode.ElectronicArts;
                break;
            case 0x20:
                this.licencee = LicenceeCode.Kss;
                break;
            case 0x25:
                this.licencee = LicenceeCode.SanX;
                break;
            case 0x30:
                this.licencee = LicenceeCode.Viacom;
                break;
            case 0x33:
            case 0x67:
            case 0x93:
                this.licencee = LicenceeCode.Ocean;
                break;
            case 0x37:
                this.licencee = LicenceeCode.Taito;
                break;
            case 0x41:
                this.licencee = LicenceeCode.Ubisoft;
                break;
            case 0x46:
                this.licencee = LicenceeCode.Angel;
                break;
            case 0x50:
                this.licencee = LicenceeCode.Absolute;
                break;
            case 0x53:
                this.licencee = LicenceeCode.AmericanSammy;
                break;
            case 0x56:
                this.licencee = LicenceeCode.LJN;
                break;
            case 0x59:
                this.licencee = LicenceeCode.MiltonBradley;
                break;
            case 0x64:
                this.licencee = LicenceeCode.LucasArts;
                break;
            case 0x70:
                this.licencee = LicenceeCode.Infrogames;
                break;
            case 0x73:
                this.licencee = LicenceeCode.Sculptured;
                break;
            case 0x79:
                this.licencee = LicenceeCode.Accolade;
                break;
            case 0x86:
                this.licencee = LicenceeCode.TokumaShotenI;
                break;
            case 0x92:
                this.licencee = LicenceeCode.VideoSystem;
                break;
            case 0x96:
                this.licencee = LicenceeCode.Yonezawa;
                break;
            case 0xA4:
            case 0x34:
            case 0x54:
                this.licencee = LicenceeCode.Konami;
                break;
            case 0x01:
            case 0x31:
                this.licencee = LicenceeCode.Nintendo;
                break;
            case 0x18:
            case 0x38:
                this.licencee = LicenceeCode.Hudson;
                break;
            case 0x42:
                this.licencee = LicenceeCode.Atlus;
                break;
            case 0x47:
                this.licencee = LicenceeCode.BulletProof;
                break;
            case 0x51:
                this.licencee = LicenceeCode.Acclaim;
                break;
            case 0x57:
                this.licencee = LicenceeCode.Matchbox;
                break;
            case 0x60:
                this.licencee = LicenceeCode.Titus;
                break;
            case 0x71:
                this.licencee = LicenceeCode.Interplay;
                break;
            case 0x75:
                this.licencee = LicenceeCode.Sci;
                break;
            case 0x80:
                this.licencee = LicenceeCode.Misawa;
                break;
            case 0x87:
                this.licencee = LicenceeCode.TsukudaOri;
                break;
            case 0x97:
                this.licencee = LicenceeCode.Kaneko;
                break;
            case 0x8:
                this.licencee = LicenceeCode.Capcom;
                break;
            case 0x19:
                this.licencee = LicenceeCode.BAi;
                break;
            case 0x24:
                this.licencee = LicenceeCode.PCMComplete;
                break;
            case 0x29:
                this.licencee = LicenceeCode.Seta;
                break;
            case 0x32:
                this.licencee = LicenceeCode.Bandai;
                break;
            case 0x35:
                this.licencee = LicenceeCode.Hector;
                break;
            case 0x39:
                this.licencee = LicenceeCode.Banpresto;
                break;
            case 0x44:
                this.licencee = LicenceeCode.Malibu;
                break;
            case 0x49:
                this.licencee = LicenceeCode.Irem;
                break;
            case 0x52:
                this.licencee = LicenceeCode.Activision;
                break;
            case 0x55:
                this.licencee = LicenceeCode.HiTechEntertainment;
                break;
            case 0x58:
                this.licencee = LicenceeCode.Mattel;
                break;
            case 0x61:
                this.licencee = LicenceeCode.Virgin;
                break;
            case 0x72:
                this.licencee = LicenceeCode.Broderbund;
                break;
            case 0x78:
                this.licencee = LicenceeCode.THQ;
                break;
            case 0x83:
                this.licencee = LicenceeCode.Lozc;
                break;
            case 0x91:
                this.licencee = LicenceeCode.Chunsoft;
                break;
            case 0x95:
                this.licencee = LicenceeCode.Varie;
                break;
            case 0x99:
                this.licencee = LicenceeCode.PackInSoft;
                break;
            default:
                this.licencee = LicenceeCode.Unknown;
                break;
        }
        
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
        switch(romBank[0x014A]){
            case 0x00:
                this.region = Region.Japanese;
                break;
            case 0x01:
                this.region = Region.NotJapanese;
                break;
            default:
                this.region = Region.Unknown;
                break;
        }
        
        //Cart type
        switch(romBank[0x0147]){
            case 0x08: //Rom + RAM
            case 0x09: //Rom + RAM + Battery
            case 0x00:
                this.cartType = CartType.ROM;
                break;
            case 0x02: //MBC1 + RAM
            case 0x03: //MBC1 + RAM + Battery
            case 0x01:
                this.cartType = CartType.MBC1;
                break;
            case 0x06: //MBC2 + Battery
            case 0x05:
                this.cartType = CartType.MBC2;
                break;
            case 0x0C: //MM01 + RAM
            case 0x0D: //MM01 + RAM + Battery
            case 0x0B:
                this.cartType = CartType.MMM01;
                break;
            case 0x0F: //MBC3 + TIMER + BATTERY
            case 0x10: //MBC3 + TIMER + RAM + Battery
            case 0x12: //MBC3 + RAM
            case 0x13: //MBC3 + RAM + Battery
            case 0x11:
                this.cartType = CartType.MBC3;
                break;
            case 0x1A: //MBC5 + RAM
            case 0x1B: //MBC5 + RAM + Battery
            case 0x1C: //MBC5 + Rumble
            case 0x1D: //MBC5 + Rumble + RAM
            case 0x1E: //MBC5 + Rumble + RAM + Battery
            case 0x19:
                this.cartType = CartType.MBC5;
                break;
            case 0x20:
                this.cartType = CartType.MBC6;
                break;
            case 0x22:
                this.cartType = CartType.MBC7;
                break;
            default:
                this.cartType = CartType.Unknown;
        }
        
        //Rom size
        this.romSize = romBank[0x148];
        switch(this.romSize){
            case 0x00:
                this.romSizeClass = RomSizeClass.KB32;
                this.romBanks = 2;
                break;
            case 0x01:
                this.romSizeClass = RomSizeClass.KB64;
                this.romBanks = 4;
                break;
            case 0x02:
                this.romSizeClass = RomSizeClass.KB128;
                this.romBanks = 8;
                break;
            case 0x03:
                this.romSizeClass = RomSizeClass.KB256;
                this.romBanks = 16;
                break;
            case 0x04:
                this.romSizeClass = RomSizeClass.KB512;
                this.romBanks = 32;
                break;
            case 0x05:
                this.romSizeClass = RomSizeClass.MB1;
                this.romBanks = 64;
                break;
            case 0x06:
                this.romSizeClass = RomSizeClass.MB2;
                this.romBanks = 128;
                break;
            case 0x07:
                this.romSizeClass = RomSizeClass.MB4;
                this.romBanks = 256;
                break;
            case 0x08:
                this.romSizeClass = RomSizeClass.MB8;
                this.romBanks = 512;
                break;
            case 0x52:
                this.romSizeClass = RomSizeClass.MB1_1;
                this.romBanks = 72;
                break;
            case 0x53:
                this.romSizeClass = RomSizeClass.MB1_2;
                this.romBanks = 80;
                break;
            case 0x54:
                this.romSizeClass = RomSizeClass.MB1_5;
                this.romBanks = 96;
                break;
            default:
                this.romSizeClass = RomSizeClass.Unknown;
                this.romBanks = romBank.length / 16383;
                break;
        }
        
        //Ram size
        this.ramSize = romBank[0x0149];
        switch(this.ramSize){
            case 0x01:
                this.ramSizeClass = RamSizeClass.KB2;
                this.ramBanks = 1;
                break;
            case 0x02:
                this.ramSizeClass = RamSizeClass.KB8;
                this.ramBanks = 1;
                break;
            case 0x03:
                this.ramSizeClass = RamSizeClass.KB32;
                this.ramBanks = 4;  //All ram banks are 8kb
                break;
            case 0x04:
                this.ramSizeClass = RamSizeClass.KB128;
                this.ramBanks = 16;
                break;
            case 0x05:
                this.ramSizeClass = RamSizeClass.KB64;
                this.ramBanks = 8;
                break;
            case 0x00:
            default:
                this.ramSizeClass = RamSizeClass.NONE;
                this.ramBanks = 0;
                break;
        }
        
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
        details+="\n: Cartridge Type: "+this.cartType;
        details+="\n: LicenceCode: "+this.licencee + "("+ this.licenceeCode+")";
        details+="\n: Rom size: "+this.romSizeClass;
        details+="\n   - Banks: "+this.romBanks;
        details+="\n: Ram size: "+this.ramSizeClass;
        details+="\n   - Banks: "+this.ramBanks;
        details+="\n: Header Checksum: "+this.headerChecksum;
        details+="\n: Global Checksum: "+this.globalChecksum;
        return details;
    }
}

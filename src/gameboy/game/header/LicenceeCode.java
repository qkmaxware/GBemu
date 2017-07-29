/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameboy.game.header;

/**
 *
 * @author Colin Halseth
 */
public enum LicenceeCode{
    Unknown, 
    None, 
    ElectronicArts, 
    Accolade, 
    Kss, 
    SanX, 
    Viacom, 
    Ocean, 
    Taito, 
    Ubisoft, 
    Angel, 
    Absolute, 
    AmericanSammy, 
    LJN, 
    MiltonBradley, 
    LucasArts,
    Infrogames, 
    Sculptured, 
    TokumaShotenI, 
    VideoSystem, 
    Yonezawa,
    Konami, 
    Nintendo, 
    Pow, 
    KemcoJapan, 
    Hudson, 
    Atlus, 
    BulletProof, 
    Acclaim, 
    Matchbox, 
    Titus, 
    Interplay, 
    Sci, 
    Misawa, 
    TsukudaOri, 
    Kaneko, 
    Capcom,
    BAi, 
    PCMComplete, 
    Seta, 
    Bandai, 
    Hector, 
    Banpresto, 
    Malibu, 
    Irem, 
    Activision, 
    HiTechEntertainment, 
    Mattel, 
    Virgin, 
    Broderbund, 
    THQ, 
    Lozc, 
    Chunsoft, 
    Varie, 
    PackInSoft;
    
    public static LicenceeCode decode(int headerValue){
        switch(headerValue){
            case 0x00:
                return LicenceeCode.None;
            case 0x13:
            case 0x69:
                return LicenceeCode.ElectronicArts;
            case 0x20:
                return LicenceeCode.Kss;
                
            case 0x25:
                return LicenceeCode.SanX;
                
            case 0x30:
                return LicenceeCode.Viacom;
                
            case 0x33:
            case 0x67:
            case 0x93:
                return LicenceeCode.Ocean;
                
            case 0x37:
                return LicenceeCode.Taito;
                
            case 0x41:
                return LicenceeCode.Ubisoft;
                
            case 0x46:
                return LicenceeCode.Angel;
                
            case 0x50:
                return LicenceeCode.Absolute;
                
            case 0x53:
                return LicenceeCode.AmericanSammy;
                
            case 0x56:
                return LicenceeCode.LJN;
                
            case 0x59:
                return LicenceeCode.MiltonBradley;
                
            case 0x64:
                return LicenceeCode.LucasArts;
                
            case 0x70:
                return LicenceeCode.Infrogames;
                
            case 0x73:
                return LicenceeCode.Sculptured;
                
            case 0x79:
                return LicenceeCode.Accolade;
                
            case 0x86:
                return LicenceeCode.TokumaShotenI;
                
            case 0x92:
                return LicenceeCode.VideoSystem;
                
            case 0x96:
                return LicenceeCode.Yonezawa;
                
            case 0xA4:
            case 0x34:
            case 0x54:
                return LicenceeCode.Konami;
                
            case 0x01:
            case 0x31:
                return LicenceeCode.Nintendo;
                
            case 0x18:
            case 0x38:
                return LicenceeCode.Hudson;
                
            case 0x42:
                return LicenceeCode.Atlus;
                
            case 0x47:
                return LicenceeCode.BulletProof;
                
            case 0x51:
                return LicenceeCode.Acclaim;
                
            case 0x57:
                return LicenceeCode.Matchbox;
                
            case 0x60:
                return LicenceeCode.Titus;
                
            case 0x71:
                return LicenceeCode.Interplay;
                
            case 0x75:
                return LicenceeCode.Sci;
                
            case 0x80:
                return LicenceeCode.Misawa;
                
            case 0x87:
                return LicenceeCode.TsukudaOri;
                
            case 0x97:
                return LicenceeCode.Kaneko;
                
            case 0x8:
                return LicenceeCode.Capcom;
                
            case 0x19:
                return LicenceeCode.BAi;
                
            case 0x24:
                return LicenceeCode.PCMComplete;
                
            case 0x29:
                return LicenceeCode.Seta;
                
            case 0x32:
                return LicenceeCode.Bandai;
                
            case 0x35:
                return LicenceeCode.Hector;
                
            case 0x39:
                return LicenceeCode.Banpresto;
                
            case 0x44:
                return LicenceeCode.Malibu;
                
            case 0x49:
                return LicenceeCode.Irem;
                
            case 0x52:
                return LicenceeCode.Activision;
                
            case 0x55:
                return LicenceeCode.HiTechEntertainment;
                
            case 0x58:
                return LicenceeCode.Mattel;
                
            case 0x61:
                return LicenceeCode.Virgin;
                
            case 0x72:
                return LicenceeCode.Broderbund;
                
            case 0x78:
                return LicenceeCode.THQ;
                
            case 0x83:
                return LicenceeCode.Lozc;
                
            case 0x91:
                return LicenceeCode.Chunsoft;
                
            case 0x95:
                return LicenceeCode.Varie;
                
            case 0x99:
                return LicenceeCode.PackInSoft;
                
            default:
                return LicenceeCode.Unknown;
                
        }
    }
}
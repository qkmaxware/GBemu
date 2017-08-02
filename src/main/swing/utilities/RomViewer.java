/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.swing.utilities;

import gameboy.game.Cartridge;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import main.swing.DrawPanel;

/**
 *
 * @author Colin Halseth
 */
public class RomViewer extends JFrame{
    
    private BufferedImage img = null;
    
    public RomViewer(Cartridge cart){
        super();
        
        this.setSize(400, 320);
        this.setTitle("Cart Info: "+cart.toString());
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel content = new JPanel();
        content.setLayout(new BorderLayout());
        
        JTextArea text = new JTextArea();
        text.setEditable(false);
        text.setText(cart.header.toString());
        content.add(new JScrollPane(text), BorderLayout.CENTER);
        
        DrawPanel pic = new DrawPanel();
        pic.draw = (g2) -> {
            int max = Math.max(pic.getWidth(), pic.getHeight());
            int xpos = (pic.getWidth() >> 1) - (max >> 1);
            g2.drawImage(img, xpos, 0, max, max, null);
        };
        
        pic.setPreferredSize(new Dimension(160,0));
        content.add(pic, BorderLayout.EAST);
        
        //Load image async
        Thread t = new Thread(){
            @Override
            public void run(){
                QueryImage(cart);
            }
        };
        t.start();
        
        this.add(content);
    }
    
    private String sendRequest(String req) throws Exception{
        URLConnection conn = (new URL(req)).openConnection();
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        conn.connect();
        InputStream res = conn.getInputStream();
        Scanner response = (new Scanner(res)).useDelimiter("\\A");
        String s = "";
        while(response.hasNext()){
            s += response.next();
        }
        return s;
    }
    
    private String getFirst(String search, String pattern){
        Pattern par = Pattern.compile(pattern);
        Matcher matcher = par.matcher(search);
        if(matcher.find())
            return matcher.group(1);
        return null;
    }
    
    private BufferedImage imageUrl(String urlz) throws Exception{
        URLConnection conn = (new URL(urlz)).openConnection();
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        return ImageIO.read(conn.getInputStream());
    }
    
    public void QueryImage(Cartridge cart){
        String api_base = "http://thegamesdb.net/api/";
        String searchUrl = api_base+"GetGamesList.php?platform=Nintendo%20Game%20Boy&";
        String imageApiUrl = api_base+"GetArt.php?id=";
        
        String cartname = cart.header.title.replace(" ", "%20");
        
        String baseurl = null;
        String specificurl = null;
        int id = 0;
        
        try{
            //Get the game id
            String search = sendRequest(searchUrl+"name="+cartname);
            id = Integer.parseInt(getFirst(search, "<id>(\\d+)<\\/id>"));
            
            //Get the image url
            String imageres = sendRequest(imageApiUrl + id);
            baseurl = getFirst(imageres, "<baseImgUrl>(.*)<\\/baseImgUrl>");
            specificurl = getFirst(imageres, "<boxart.*>(.*)<\\/boxart>");
            if(specificurl == null){
                specificurl = getFirst(imageres, "<screenshot>\\s*<original.*>(.*)<\\/original>");
            }
            if(specificurl == null)
                return;
            String url = baseurl + specificurl;
           
            img = imageUrl(url);
            this.repaint();
        }catch(Exception e){
            System.out.println("Failed to obtain game("+id+") image from "+String.valueOf(baseurl)+" at "+String.valueOf(specificurl));
            e.printStackTrace();
        }
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SSMEngines.util;

import SSMCode.Platform;

import java.util.ArrayList;
import java.awt.*;
import java.awt.image.ImageObserver;

public class MapHandler {
    
    private static ArrayList<String> mapNames;
    private static ArrayList<Image> mapThumbnails;
    private static ArrayList<Image> mapBGs;
    private static ArrayList<Image> mapPlats;
    
    private int mapNumber;
    
    public MapHandler(){
        mapNumber = -1;
    }
    
    public int getMapNumber(){return mapNumber;}
    public void setMapNumber(int c){mapNumber = c;}
    
    public ArrayList<Image> getMapBGs(){return mapBGs;}
    public ArrayList<Image> getMapPlats(){return mapPlats;}
    
    public void drawMapScreen(Graphics g, ImageObserver io){
        int imageSize = 150;
        for(int i=0; i<mapThumbnails.size();i++){
            g.drawImage(mapThumbnails.get(i),(int)(10+(i%2)*(imageSize*1.6+10)+550),10+((imageSize)+10)*(i/2)
                    ,(int)(imageSize*1.6),(imageSize),io);
        }
        
        g.setFont(new Font("Sans Serif", Font.BOLD, 60));
        g.setColor(Color.BLACK);
        g.drawString("Map Select", 20,60);
    }
    public void drawMouseEvents(int mouseX, int mouseY, Graphics g, ImageObserver io){
        ArrayList<Rectangle> imageRectList = new ArrayList<>();
        
        int imageSize = 150;
        for(int i=0; i<mapThumbnails.size();i++){
            imageRectList.add(new Rectangle((int)(10+(i%2)*(imageSize*1.6+10)-5),
                    10-5+(imageSize +10)*(i/2),
                    (int)(imageSize*1.6+10),(int)(imageSize)+10));
        }
        for(Rectangle r: imageRectList){
            r.x+=550;
        }
        
        Graphics2D g2d = (Graphics2D)g;
               
        for(int i=0; i<imageRectList.size();i++){
            Rectangle r = imageRectList.get(i);
            g2d.setColor(Color.red);  
            if(r.contains(mouseX,mouseY)){
                g2d.fill(r);
           
                g2d.setFont(new Font("Sans Serif", Font.BOLD, 60));
                g2d.setColor(Color.WHITE);
                g2d.drawString(mapNames.get(i), 20,490);
                g2d.drawImage(mapThumbnails.get(i), 20,100,(int)(imageSize*2*1.6), imageSize*2,io);
            }
        }
    }
    public boolean handleMouseEvents(int mouseX, int mouseY){
        ArrayList<Rectangle> imageRectList = new ArrayList<>();
        
        int imageSize = 150;
        for(int i=0; i<mapThumbnails.size();i+=1){
            imageRectList.add(new Rectangle((int)(10+(i%2)*(imageSize*1.6+10)-5),
                    10-5+((imageSize)+10)*(i/2),
                    (int)(imageSize*1.6+10),(imageSize)+10));
        }
        for(Rectangle r: imageRectList){
            r.x+=550;
        }
                       
        for(int i=0; i<imageRectList.size();i++){
            Rectangle r = imageRectList.get(i);
            if(r.contains(mouseX,mouseY)){
                mapNumber = i;
                return true;
            }
        }
        return false;
    }
    
    public ArrayList<Platform> initPlats(){
        ArrayList<Platform> platList = new ArrayList<>();
        int width = 1100;
        
        if(mapNumber == 0){
            platList.add(new Platform(-500,2000,width+1000,100));
            platList.add(new Platform(100,400,900,311));
            platList.add(new Platform(250,335,150,20));
            platList.add(new Platform(700,335,150,20));
            platList.add(new Platform(475,260,150,20));
        } else if(mapNumber == 1){
            platList.add(new Platform(-500,2000,width+1000,100));
            platList.add(new Platform(100,400,900,200));
            platList.add(new Platform(250,335,150,20));
            platList.add(new Platform(700,335,150,20));
            platList.add(new Platform(475,260,150,20));
        } else if (mapNumber == 2){
            platList.add(new Platform(-500,2000,width+1000,100));
            platList.add(new Platform(355,480,410,70));
            platList.add(new Platform(115,500,210,45));
            platList.add(new Platform(85,400,220,45));
            platList.add(new Platform(775,380,200,45));
            platList.add(new Platform(345,350,180,40));
            platList.add(new Platform(605,300,200,45));
            platList.add(new Platform(145,250,180,40));
            platList.add(new Platform(445,220,180,40));
            platList.add(new Platform(805,200,200,45));
        } else if (mapNumber == 3){
            platList.add(new Platform(-1000,435,width+1000,100));
        } else if (mapNumber == 4){
            platList.add(new Platform(-500,2000,width+1000,100));
            platList.add(new Platform(40,372,940,100));
        } else if(mapNumber == 5){
            platList.add(new Platform(-500,2000,width+1000,100));
            platList.add(new Platform(245,300,630,50));
            platList.add(new Platform(105,265,150,20));
            platList.add(new Platform(105+150+613,265,150,20));
        }
        
        return platList;
    }
    
    public void initImages(Poolkit toolkit){
        mapThumbnails = new ArrayList<>();
        mapThumbnails.add(toolkit.getImage("SSMImages/Maps/map_1_TN.png"));
        mapThumbnails.add(toolkit.getImage("SSMImages/Maps/map_2_TN.png"));
        mapThumbnails.add(toolkit.getImage("SSMImages/Maps/map_3_TN.png"));
        mapThumbnails.add(toolkit.getImage("SSMImages/Maps/map_4_TN.png"));
        mapThumbnails.add(toolkit.getImage("SSMImages/Maps/map_5_TN.png"));
        mapThumbnails.add(toolkit.getImage("SSMImages/Maps/map_6_TN.png"));
        
        mapNames = new ArrayList<>();
        mapNames.add("Final Destination");
        mapNames.add("       Gaushal");
        mapNames.add("Between Clouds");
        mapNames.add("   Killing Moon");
        mapNames.add("    My Kitchen");
        mapNames.add("Amidst The Stars");
        
        mapBGs = new ArrayList<>();
        mapBGs.add(toolkit.getImage("SSMImages/Maps/background_1.png"));
        mapBGs.add(toolkit.getImage("SSMImages/Maps/background_2.png"));
        mapBGs.add(toolkit.getImage("SSMImages/Maps/background_3.png"));
        mapBGs.add(toolkit.getImage("SSMImages/Maps/background_4.png"));
        mapBGs.add(toolkit.getImage("SSMImages/Maps/background_5.png"));
        mapBGs.add(toolkit.getImage("SSMImages/Maps/background_6.png"));
        
        mapPlats = new ArrayList<>();
        mapPlats.add(toolkit.getImage("SSMImages/Maps/bigPlatform_1.png"));
        mapPlats.add(toolkit.getImage("SSMImages/Maps/smallPlatform_1.png"));
        mapPlats.add(toolkit.getImage("SSMImages/Maps/bigPlatform_2.png"));
        mapPlats.add(toolkit.getImage("SSMImages/Maps/smallPlatform_2.png"));
        mapPlats.add(toolkit.getImage("SSMImages/Maps/platform_3.png"));
        mapPlats.add(toolkit.getImage("SSMImages/Maps/platform_3.png"));
        mapPlats.add(toolkit.getImage("SSMImages/Maps/platform_4.png"));
        mapPlats.add(toolkit.getImage("SSMImages/Maps/platform_4.png"));
        mapPlats.add(toolkit.getImage("SSMImages/Maps/platform_4.png"));
        mapPlats.add(toolkit.getImage("SSMImages/Maps/platform_4.png"));
        mapPlats.add(toolkit.getImage("SSMImages/Maps/platform_4.png"));
        mapPlats.add(toolkit.getImage("SSMImages/Maps/platform_4.png"));
    	}
    }

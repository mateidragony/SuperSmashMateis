/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SSMCode;

import SSMEngines.old.PlayerOld;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

/**
 *
 * @author 22cloteauxm
 */
public class Platform extends GameObject {
    
    private static Image bigPlatImg;
    private static Image smallPlatImg;
    
    public Platform(int x, int y, int w, int h){
        super(x,y,w,h);
    }
        
    public static void setSmallImg(Image c){smallPlatImg = c;}
    public static void setBigImg(Image c){bigPlatImg = c;}
    
    public void draw(Graphics g, ImageObserver io) {
        if(getW()<600)
            g.drawImage(smallPlatImg,(int)getX(),(int)getY(),getW(),getH(), io);
        else
            g.drawImage(bigPlatImg,(int)getX(),(int)getY(),getW(),getH(), io);
    }
    
    public void animate(ArrayList<PlayerOld> playerList, ArrayList<Platform> platList){
        
        for(PlayerOld player: playerList){
            for(Platform p: platList){
                if(p.getW()<600){
                    if((player.getYVel()>=0 
                            && player.getY()<=p.getY()-player.getH()
                            && player.getX()+player.getW()>=p.getX()
                            && player.getX()<=p.getX()+p.getW()))     
                    {
                        player.setGround((int)p.getY());
                    }
                } else {
                    if((player.getYVel()>=0 
                            && player.getY()<=p.getY()+20-player.getH()
                            && player.getX()+20>=p.getX()
                            && player.getX()-20+player.getW()<=p.getX()+p.getW()))     
                    {
                        player.setGround((int)p.getY()+20);
                    }
                }
            }
        }
    }
}

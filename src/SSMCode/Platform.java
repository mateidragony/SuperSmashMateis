/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SSMCode;

import SSMEngines.old.PlayerOld;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.List;

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
    
    public void animate(List<Player> playerList){
        
        for(Player player: playerList){
            if (getW() < 600) {
                if ((player.getYVel() >= 0
                        && player.getY() <= getY() - player.getH()
                        && player.getX() + player.getW() >= getX()
                        && player.getX() <= getX() + getW())) {
                    player.setGround((int) getY());
                }
            } else {
                if ((player.getYVel() >= 0
                        && player.getY() <= getY() + 20 - player.getH()
                        && player.getX() + 20 >= getX()
                        && player.getX() - 20 + player.getW() <= getX() + getW())) {
                    player.setGround((int) getY() + 20);
                }
            }
        }
    }

    public void animate(Player player){
        if (getW() < 600) {
            if ((player.getYVel() >= 0
                    && player.getY() <= getY() - player.getH()
                    && player.getX() + player.getW() >= getX()
                    && player.getX() <= getX() + getW())) {
                player.setGround((int) getY());
            }
        } else {
            if ((player.getYVel() >= 0
                    && player.getY() <= getY() + 20 - player.getH()
                    && player.getX() + 20 >= getX()
                    && player.getX() - 20 + player.getW() <= getX() + getW())) {
                player.setGround((int) getY() + 20);
            }
        }
    }
}

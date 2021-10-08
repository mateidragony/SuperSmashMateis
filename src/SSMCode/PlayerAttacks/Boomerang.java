/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SSMCode.PlayerAttacks;

import SSMCode.Actor;
import SSMCode.Player;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

/**
 *
 * @author 22cloteauxm
 */
public class Boomerang extends Actor{
    
    private String team;
    private int dir;
    private boolean returning;
    private boolean isNull;
    
    private int maxX, minX, finalX;
    
    private static Image sword1, sword2;
    
    private double drawTimer;
    
    public Boomerang(int x,int y, int direction, int finalX_, boolean returning_,
            String team_, boolean isNull_, double draw){
        super(x,y,60,30);
        
        returning = returning_;
        team = team_;
        dir = direction;
        isNull = isNull_;
        drawTimer = draw;
        
        finalX = finalX_;
        
        if(finalX >= x){
            maxX = finalX;
            minX = -1000;
        } else { 
            maxX = 10000;
            minX = finalX;
        }
        
        setXVel(12.5*dir);
    }
    
    public int getDirection(){return dir;}
    public double getDrawTimer(){return drawTimer;}
    public boolean isNull(){return isNull;}
    public boolean isReturning(){return returning;}
    public int getFinalX(){return finalX;}
    public String getTeam(){return team;}
    
    
    public void animateMovement(Player shooter){
        if(this.intersects(shooter) && returning)
            isNull = true;

        
        if((getX() >= maxX || getX() <= minX) && !returning){
            returning = true;
            setXVel(getXVel()*-1);
            dir*=-1;
        }
        
        setX(getX()+getXVel());
    }
    
    public void animateDamage(ArrayList<Player> targets, Player shooter){
        for(Player target: targets){
            if(this.getHitBox().intersects(target.getHitBox()) 
                    && !team.equals(target.getTeam())
                    && !target.isUntargetable())
            {    
                target.setPercentage(target.getPercentage()+1.5);
                target.setXVel((2.5+1*target.getPercentage()/25)*dir);
                target.setYVel(target.getYVel()-0.5*(1.5+target.getPercentage()/100));
            }
        }
    }
    
    public void draw(Graphics g, ImageObserver io){
        if(drawTimer < 0)
            drawTimer = 1;
        drawTimer -=0.1;
        
        Image currentImage;
        if(drawTimer > 0.5)
                currentImage = sword1;
            else
                currentImage = sword2;
        
        g.drawImage(currentImage, (int)getX(),(int)getY(),getW(),getH(), io);
    }
    
    public static void initImages(Toolkit toolkit){
        sword1 = toolkit.getImage("SSMImages/Lawrence/Sword_Boomerang_1.png");
        sword2 = toolkit.getImage("SSMImages/Lawrence/Sword_Boomerang_2.png");
    }    
}

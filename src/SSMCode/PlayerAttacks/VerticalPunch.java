/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SSMCode.PlayerAttacks;

import SSMCode.Player;
import java.awt.*;
import java.awt.image.ImageObserver;

/**
 *
 * @author 22cloteauxm
 */
public class VerticalPunch extends Punch{
    
    public VerticalPunch(int x, int y, int dir, String team_, double cd, boolean hurts){
        super(x,y,dir,team_,cd,hurts);
        
        setSize(15,50);
    }
    
    public Rectangle getHitBox(){
        if(getDirection() == Projectile.LEFT){
            return new Rectangle((int)(getX()-8-getW()/2+20),(int)(getY()+21-getH()+getPunchCD()*10), 
                    getW(),(int)(getH()-getPunchCD()*10));
        } else {
            return new Rectangle((int)(getX()+28-getW()/2+20),(int)(getY()+21-getH()+getPunchCD()*10), 
                    getW(),(int)(getH()-getPunchCD()*10));
        }
    }

    public void animate(Player puncher, Player target){
        if(getPunchCD() > 0)
            setPunchCD(getPunchCD()-1/4.0);
        else
            setPunchCD(0);
        
        setX(puncher.getX());
        setY(puncher.getY());
        
        if(this.getHitBox().intersects(target.getHitBox()) 
                && !getTeam().equals(target.getTeam())
                && !target.isUntargetable()
                && getCanHurt())
        {
            target.setPercentage(target.getPercentage()+5);
            target.setYVel(0.3*(target.getYVel()-10*(1.5+2*target.getPercentage()/75)));
        }
        
        if(getPunchCD() == 0)
            setCanHurt(false);
    }
    
    public void draw(Player puncher, Graphics g, ImageObserver io){
        if(getTeam().equals("blue"))
            g.setColor(Color.BLUE);
        else
            g.setColor(Color.RED);
        
        if(getPunchCD() > 0){
            if(getDirection() == Projectile.LEFT){
                g.drawImage(myImages.get(3),(int)getHitBox().getX(),(int)getHitBox().getY(),
                        (int)getHitBox().getWidth(),(int)getHitBox().getHeight(),io);
            } else {
                g.drawImage(myImages.get(2),(int)getHitBox().getX(),(int)getHitBox().getY(),
                        (int)getHitBox().getWidth(),(int)getHitBox().getHeight(),io);
            }
        }
    }
    
}

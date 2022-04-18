/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SSMCode.PlayerAttacks;

import SSMCode.Player;
import SSMEngines.SSMClient;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

/**
 *
 * @author 22cloteauxm
 */
public class VerticalPunch extends Punch{
    
    public VerticalPunch(int x, int y, int dir, String team_, double cd, boolean hurts, int shooter, int sW, int sH){
        super(x,y,dir,team_,cd,hurts,shooter,sW,sH);
        
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

    public void animate(ArrayList<Player> targets){
        if(getPunchCD() > 0)
            setPunchCD(getPunchCD()-1/4.0);
        else
            setPunchCD(0);

        for(Player target : targets) {
            if (this.getHitBox().intersects(target.getHitBox())
                    && !getTeam().equals(target.getTeam())
                    && !target.isUntargetable()
                    && getCanHurt()) {
                target.setPercentage(target.getPercentage() + 5);
                target.setYVel(0.3 * (target.getYVel() - 10 * (1.5 + 2 * target.getPercentage() / 75)));
            }
        }
        
        if(getPunchCD() == 0)
            setCanHurt(false);
    }
    
    public void draw(Graphics g, ImageObserver io){
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

    //----------------------------------------
    //Packing and unpacking punches
    //----------------------------------------
    public static String pack(VerticalPunch p){
        if(p==null)
            return "null";
        String str = "";
        str += (int)p.getX() + SSMClient.parseChar;
        str += (int)p.getY() + SSMClient.parseChar;
        str += p.getDirection() + SSMClient.parseChar;
        str += p.getTeam() + SSMClient.parseChar;
        str += p.getPunchCD() + SSMClient.parseChar;
        str += p.getCanHurt() + SSMClient.parseChar;
        str += p.getShooter() + SSMClient.parseChar;
        str += p.getShooterSize().getWidth() + SSMClient.parseChar;
        str += p.getShooterSize().getHeight() + SSMClient.parseChar;
        return str;
    }
    public static VerticalPunch unPack(String s){
        if(s.equals("null") || s.isEmpty())
            return null;
        String[] data = s.split(SSMClient.parseChar);
        return new VerticalPunch(Integer.parseInt(data[0]), Integer.parseInt(data[1]), Integer.parseInt(data[2]),
                data[3], Double.parseDouble(data[4]), Boolean.parseBoolean(data[5]), Integer.parseInt(data[6]),
                Integer.parseInt(data[7]), Integer.parseInt(data[8]));
    }
    
}

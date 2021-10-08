/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SSMCode.PlayerAttacks;

import java.awt.*;
import SSMCode.*;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

/**
 *
 * @author 22cloteauxm
 */
public class Stick extends GameObject{
    
    private int dir;
    private String team;
    private boolean isNull;
    private double strength;
    
    private static ArrayList<Image> myImages;
    
    public Stick(int x,int y, int direction, String team_, boolean isItNull, double str){
        super(x,y,54,48);
        
        dir = direction;
        team = team_;
        isNull = isItNull;
        strength = str;
    }
    
    public double getStrength(){return strength;}
    
    public Rectangle getHitBox(Player attacker){
        if(dir == Projectile.LEFT)
            return new Rectangle((int)getX()-getW(),(int)getY(),getW(),getH());
        else
            return new Rectangle((int)getX()+attacker.getW(),(int)getY(),getW(),getH());
    }
    
    public void animate(Player attacker, Player target){
        setX(attacker.getX());
        setY(attacker.getY());
        
        if(this.getHitBox(attacker).intersects(target.getHitBox()) 
                && !team.equals(target.getTeam())
                && !target.isUntargetable())
        {
            target.setPercentage(target.getPercentage()+.5);
            target.setXVel(0);
            target.setStunDuration(strength);
        }
        
    }
    public void draw(Player attacker, Graphics g, ImageObserver io){
        if(dir == Projectile.LEFT)
            g.drawImage(myImages.get(1), (int)getX()-getW(),(int)getY(),getW(),getH(), io);
        else
            g.drawImage(myImages.get(0), (int)getX()+attacker.getW(),(int)getY(),getW(),getH(), io);
    }
    public static void initImages(){
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        myImages = new ArrayList<>();
        
        myImages.add(toolkit.getImage("SSMImages/Kaushal/Stick_L.png"));
        myImages.add(toolkit.getImage("SSMImages/Kaushal/Stick_L_B.png"));
    }
}

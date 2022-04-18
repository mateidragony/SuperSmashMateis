/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SSMCode.PlayerAttacks;

import SSMCode.*;
import SSMCode.Player;
import SSMEngines.SSMClient;
import SSMEngines.util.Poolkit;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.awt.geom.Ellipse2D;

/**
 *
 * @author 22cloteauxm
 */
public class GrowingLAttack extends Actor{
    
    public static final int SUN = 0;
    public static final int HEAD_F = 1;
    public static final int HEAD_B = 2;
    
    private final int dir;
    private final String team;
    private final double damage;
    private final int size;
    private final int shooter;
    
    private static ArrayList<Image> myImages;
    
    public GrowingLAttack(int x,int y,int Direction, String team_, int size, int shooter){
        super(x,y,size,size);
        
        this.size = size;
        team = team_;
        dir = Direction;
        this.shooter = shooter;
        damage = 3.5*((double)(size-10)/700);
        
        setXVel(10*dir);
    }
    
    public int getDir(){return dir;}
    public int getSize(){return size;}
    
    public Ellipse2D.Double getHitBoxCircle(){
        return new Ellipse2D.Double((int)getX()+10,(int)getY()+10,getW()-20,getH()-20);
    }
    
    public void animateMovement(){
        setX(getX() + getXVel());
    }
    
    public void animateDamage(ArrayList<Player> targets){
        for(Player target:  targets){
            if(this.getHitBoxCircle().intersects(target.getHitBox()) 
                    && !team.equals(target.getTeam())
                    && !target.isUntargetable())
            {
                target.setPercentage(target.getPercentage()+damage);
                target.setXVel(damage*(2.5+target.getPercentage()/15)*dir);
                target.setYVel(target.getYVel()-damage*0.9*(0.5*(1.5+target.getPercentage()/100)));
            }
        }
        
    }
    
    public void draw(Graphics g, ImageObserver io){     
        Image myImage;
        //If adam, draw sun
        if(shooter == Player.ADAM)
            myImage = myImages.get(SUN);
        //else draw umer's head
        else if(shooter == Player.UMER && dir == Projectile.RIGHT)
            myImage = myImages.get(HEAD_F);
        else 
            myImage = myImages.get(HEAD_B);
        
        g.drawImage(myImage, (int)getX(),(int)getY(),getW(),getH(), io);
    }
    
    public static void initImage(){
        Poolkit toolkit = new Poolkit();
        
        myImages = new ArrayList<>();
        
        myImages.add(toolkit.getImage("SSMImages/Adam/sun.png"));
        myImages.add(toolkit.getImage("SSMImages/Umer/Umer_Head_F.png"));
        myImages.add(toolkit.getImage("SSMImages/Umer/Umer_Head.png"));
    }


    //----------------------------------------
    //Packing and unpacking punches
    //----------------------------------------

    public static String pack(GrowingLAttack p){
        if(p==null)
            return "null";
        String str = "";
        str += (int)p.getX() + SSMClient.parseChar;
        str += (int)p.getY() + SSMClient.parseChar;
        str += p.dir + SSMClient.parseChar;
        str += p.team + SSMClient.parseChar;
        str += p.getSize() + SSMClient.parseChar;
        str += p.shooter + SSMClient.parseChar;
        return str;
    }
    public static GrowingLAttack unPack(String s){
        if(s.equals("null") || s.isEmpty())
            return null;
        String[] data = s.split(SSMClient.parseChar);
        return new GrowingLAttack(Integer.parseInt(data[0]), Integer.parseInt(data[1]), Integer.parseInt(data[2]),
                data[3], Integer.parseInt(data[4]), Integer.parseInt(data[5]));
    }
    
}

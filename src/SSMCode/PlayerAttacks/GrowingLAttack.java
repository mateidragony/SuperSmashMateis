/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SSMCode.PlayerAttacks;

import SSMCode.*;
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
    
    private int dir;
    private String team;
    private double damage;
    private int sizeAndDamage_;
    
    private Image myImage;
    private static ArrayList<Image> myImages;
    
    public GrowingLAttack(int x,int y,int Direction, String team_, int sizeAndDamage){
        super(x,y,sizeAndDamage,sizeAndDamage);
       
        sizeAndDamage_ = sizeAndDamage;
        
        team = team_;
        dir = Direction;
        damage = 3.5*((double)(sizeAndDamage-10)/700);
        myImage = myImages.get(0);
        
        setXVel(10*dir);
    }
    
    public int getDir(){return dir;}
    public int getSizeAndDamage(){return sizeAndDamage_;}
    
    public Ellipse2D.Double getHitBoxCircle(){
        return new Ellipse2D.Double((int)getX()+10,(int)getY()+10,getW()-20,getH()-20);
    }
    
    public void setImage(int c){myImage = myImages.get(c);}
    
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
        g.drawImage(myImage, (int)getX(),(int)getY(),getW(),getH(), io);
    }
    
    public static void initImage(){
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        
        myImages = new ArrayList<>();
        
        myImages.add(toolkit.getImage("SSMImages/Adam/sun.png"));
        myImages.add(toolkit.getImage("SSMImages/Umer/Umer_Head_F.png"));
        myImages.add(toolkit.getImage("SSMImages/Umer/Umer_Head.png"));
    }
    
}

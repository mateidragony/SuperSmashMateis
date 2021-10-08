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


/**
 *
 * @author 22cloteauxm
 */
    
public class Motorcycle extends GameObject{
    
    private static ArrayList<Image> myImages;
    
    private String team;
    private int dir;
    private boolean isNull;
    
    public Motorcycle(int x, int y, int direction, String team_, boolean isNull_){
        super(x,y,137,84); 
        
        dir = direction;
        team = team_;
        isNull = isNull_;
    }
    
    public boolean isNull(){return isNull;}
    
    public Rectangle getHitBox(){return new Rectangle((int)getX()-20,(int)getY()+20,getW()-60,getH()-20);}
    
    public void setDirection(int c){dir = c;}
    
    public void animate(Player rider,Player target){
        
        setX(rider.getX());
        setY(rider.getY());
        
        if(rider.getXVel()>=-20 && rider.getXVel()<=20)
            rider.setXVel(15*dir);
        
        if(this.getHitBox().intersects(target.getHitBox()) 
                && !team.equals(target.getTeam())
                && !target.isUntargetable()
                && !isNull) 
        {
            target.setPercentage(target.getPercentage()+0.22);
            target.setXVel((2.5+4*target.getPercentage()/25)*dir);
        }   
    }
    
    public void draw(Player rider, Graphics g, ImageObserver io){
        if(dir == Projectile.RIGHT)
            g.drawImage(myImages.get(0), (int)getX()-getW()/3,(int)getY()+5,(int)(getW()),(int)(getH()), io);
        else
            g.drawImage(myImages.get(1), (int)getX()-getW()/3,(int)getY()+5,(int)(getW()),(int)(getH()), io);
    }
    
    public static void initImages(){
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        myImages = new ArrayList<>();
        
        myImages.add(toolkit.getImage("SSMImages/Jack/motorcycle.png"));
        myImages.add(toolkit.getImage("SSMImages/Jack/motorcycle_B.png"));
    }
}

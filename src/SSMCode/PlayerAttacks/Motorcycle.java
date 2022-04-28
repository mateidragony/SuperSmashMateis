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


/**
 *
 * @author 22cloteauxm
 */
    
public class Motorcycle extends GameObject{
    
    private static ArrayList<Image> myImages;
    
    private final String team;
    private int dir;
    private final boolean isNull;
    
    public Motorcycle(int x, int y, int direction, String team_, boolean isNull_){
        super(x,y,137,84); 
        
        dir = direction;
        team = team_;
        isNull = isNull_;
    }
    
    public boolean isNull(){return isNull;}
    
    public Rectangle getHitBox(){return new Rectangle((int)getX()-20,(int)getY()+20,getW()-60,getH()-20);}
    
    public void setDirection(int c){dir = c;}
    
    public void animate(ArrayList<Player> targets){

        for(Player target : targets) {
            if (this.getHitBox().intersects(target.getHitBox())
                    && !team.equals(target.getTeam())
                    && !target.isUntargetable()
                    && !isNull) {
                target.setPercentage(target.getPercentage() + 0.22);
                target.setDamageXVel((2.5 + 4 * target.getPercentage() / 25) * dir);
            }
        }
    }
    
    public void draw(Graphics g, ImageObserver io){
        if(dir == Projectile.RIGHT)
            g.drawImage(myImages.get(0), (int)getX()-getW()/3,(int)getY()+5,(int)(getW()),(int)(getH()), io);
        else
            g.drawImage(myImages.get(1), (int)getX()-getW()/3,(int)getY()+5,(int)(getW()),(int)(getH()), io);
    }
    
    public static void initImages(){
        Poolkit toolkit = new Poolkit();
        myImages = new ArrayList<>();
        
        myImages.add(toolkit.getImage("SSMImages/Jack/motorcycle.png"));
        myImages.add(toolkit.getImage("SSMImages/Jack/motorcycle_B.png"));
    }


    //----------------------------------------
    //Packing and unpacking Motorcycles
    //----------------------------------------
    public static String pack(Motorcycle p){
        if(p==null)
            return "null";
        String str = "";
        str += (int)p.getX() + SSMClient.parseChar;
        str += (int)p.getY() + SSMClient.parseChar;
        str += p.dir + SSMClient.parseChar;
        str += p.team + SSMClient.parseChar;
        str += p.isNull() + SSMClient.parseChar;
        return str;
    }
    public static Motorcycle unPack(String s){
        if(s.equals("null") || s.isEmpty())
            return null;
        String[] data = s.split(SSMClient.parseChar);
        return new Motorcycle(Integer.parseInt(data[0]), Integer.parseInt(data[1]), Integer.parseInt(data[2]),
                data[3], Boolean.parseBoolean(data[4]));
    }
}

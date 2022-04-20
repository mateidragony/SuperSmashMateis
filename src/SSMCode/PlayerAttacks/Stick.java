/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SSMCode.PlayerAttacks;

import java.awt.*;
import SSMCode.*;
import SSMCode.Player;
import SSMEngines.SSMClient;
import SSMEngines.util.Poolkit;

import java.awt.image.ImageObserver;
import java.util.ArrayList;

/**
 *
 * @author 22cloteauxm
 */
public class Stick extends GameObject{
    
    private final int dir;
    private final String team;
    private final double strength;
    private final int attackerWidth;
    
    private static ArrayList<Image> myImages;
    
    public Stick(int x,int y, int direction, String team, double strength, int aW){
        super(x,y,54,48);
        
        dir = direction;
        this.team = team;
        this.strength = strength;
        attackerWidth = aW;
    }
    
    public double getStrength(){return strength;}
    
    public Rectangle getHitBox(){
        if(dir == Projectile.LEFT)
            return new Rectangle((int)getX()-getW(),(int)getY(),getW(),getH());
        else
            return new Rectangle((int)getX()+attackerWidth,(int)getY(),getW(),getH());
    }
    
    public void animate(ArrayList<Player> targets){
        for(Player target : targets) {
            if (this.getHitBox().intersects(target.getHitBox())
                    && !team.equals(target.getTeam())
                    && !target.isUntargetable()) {
                target.setPercentage(target.getPercentage() + .5);
                target.setXVel(0);
                target.setStunDuration(strength);
                target.setStunner(Player.KAUSHAL);
            }
        }
    }
    public void draw(Graphics g, ImageObserver io){
        if(dir == Projectile.LEFT)
            g.drawImage(myImages.get(1), (int)getX()-getW(),(int)getY(),getW(),getH(), io);
        else
            g.drawImage(myImages.get(0), (int)getX()+attackerWidth,(int)getY(),getW(),getH(), io);
    }
    public static void initImages(){
        Poolkit toolkit = new Poolkit();
        myImages = new ArrayList<>();
        
        myImages.add(toolkit.getImage("SSMImages/Kaushal/Stick_L.png"));
        myImages.add(toolkit.getImage("SSMImages/Kaushal/Stick_L_B.png"));
    }

    //----------------------------------------
    //Packing and unpacking sticks
    //----------------------------------------
    public static String pack(Stick p){
        if(p==null)
            return "null";
        String str = "";
        str += (int)p.getX() + SSMClient.parseChar;
        str += (int)p.getY() + SSMClient.parseChar;
        str += p.dir + SSMClient.parseChar;
        str += p.team + SSMClient.parseChar;
        str += p.strength + SSMClient.parseChar;
        str += p.attackerWidth + SSMClient.parseChar;
        return str;
    }
    public static Stick unPack(String s){
        if(s.equals("null") || s.isEmpty())
            return null;
        String[] data = s.split(SSMClient.parseChar);
        return new Stick(Integer.parseInt(data[0]), Integer.parseInt(data[1]), Integer.parseInt(data[2]),
                data[3], Double.parseDouble(data[4]), Integer.parseInt(data[5]));
    }
}

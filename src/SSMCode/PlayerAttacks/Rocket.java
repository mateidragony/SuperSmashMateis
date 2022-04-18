/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SSMCode.PlayerAttacks;

import SSMCode.Actor;
import SSMCode.Player;
import SSMEngines.SSMClient;
import SSMEngines.util.Poolkit;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import javax.sound.sampled.Clip;

/**
 *
 * @author 22cloteauxm
 */
public class Rocket extends Actor{

    private static Image iceCube;
    private static Image myImageRight, myImageLeft, eagle_F, eagle_B, arrow_F, arrow_B;
    
    private final int shooter;
    private final String team;
    private boolean playedSFX;
    private final int dir;
    private static Clip projSFX;
    
    public Rocket(int x,int y, int w, int h, int direction, String team_, int shooter){
        super(x,y,w,h);
        
        dir = direction;
        
        if(dir == Projectile.LEFT)
            setX(getX()-20);
            
        team = team_;
        this.shooter = shooter;
        
        setXVel(12.5*dir);
    }
    
    public int getDir(){return dir;}
    public int getShooter(){return shooter;}
    
    public void animateMovement(){
        if(shooter == Player.NEEL)
            setXVel(20*dir);
        
        setX(getX()+getXVel());
    }
    public void animateDamage(ArrayList<Player> targets){
        for(Player target:  targets){
            if(this.getHitBox().intersects(target.getHitBox()) 
                    && !team.equals(target.getTeam())
                    && !target.isUntargetable())
            {    
                target.setPercentage(target.getPercentage()+1.5);
                target.setXVel((2.5+1*target.getPercentage()/25)*dir);
                target.setYVel(target.getYVel()-0.5*(1.5+target.getPercentage()/100));
                
                if(shooter == Player.LISON){
                    target.setStunDuration(.9);
                    target.setPercentage(target.getPercentage()-.5);
                }
                if(shooter == Player.NEEL){
                    target.setStunDuration(.9);
                    target.setPercentage(target.getPercentage()-.5);
                }
            }
        }
        
    }
    
    public void draw(Graphics g, ImageObserver io){

        if(shooter == Player.LISON)
            g.drawImage(iceCube, (int)getX(),(int)getY(),(getW()),(getH()), io);
        else{
            //Matei
            if(dir == Projectile.RIGHT && shooter == Player.MATEI)
                g.drawImage(myImageRight, (int)getX(),(int)getY(),getW(),(getH()), io);
            else if(dir == Projectile.LEFT && shooter== Player.MATEI)
                g.drawImage(myImageLeft, (int)getX(),(int)getY(),(getW()),(getH()), io);
            //Obama
            else if(dir == Projectile.RIGHT && shooter == Player.OBAMA)
                g.drawImage(eagle_F, (int)getX(),(int)getY(),(getW())+30,(getH())+10, io);
            else if(dir == Projectile.LEFT && shooter == Player.OBAMA)
                g.drawImage(eagle_B, (int)getX(),(int)getY(),(getW())+30,(getH())+10, io);
            //Neel right
            if(dir == Projectile.RIGHT && shooter == Player.NEEL){
                g.drawImage(arrow_F, (int)getX(),(int)getY(),(getW()),(getH()), io);
            }
            //Neel left
            else if(dir == Projectile.LEFT && shooter == Player.NEEL){
                g.drawImage(arrow_B, (int)getX(),(int)getY(),(getW()),(getH()), io);
            }
        }
    }
    
    public static void initImage(){
        Poolkit toolkit = new Poolkit();
        
        myImageRight = toolkit.getImage("SSMImages/Matei/rocket.png");
        myImageLeft = toolkit.getImage("SSMImages/Matei/rocket_L.png");
        
        iceCube = toolkit.getImage("SSMImages/Lison/ice.png");
        eagle_F = toolkit.getImage("SSMImages/Obama/eagle_F.png");
        eagle_B = toolkit.getImage("SSMImages/Obama/eagle_B.png");
        
        arrow_F = toolkit.getImage("SSMImages/Neel/IceArrow_F.png");
        arrow_B = toolkit.getImage("SSMImages/Neel/IceArrow_B.png");
    }



    //----------------------------------------
    //Packing and unpacking projectiles
    //----------------------------------------
    public static String pack(Rocket r){
        if(r == null)
            return "null";
        else{
            String packedProj = "";
            packedProj += r.getX() + SSMClient.parseChar;
            packedProj += r.getY() + SSMClient.parseChar;
            packedProj += r.getW() + SSMClient.parseChar;
            packedProj += r.getH() + SSMClient.parseChar;
            packedProj += r.dir + SSMClient.parseChar;
            packedProj += r.team + SSMClient.parseChar;
            packedProj += r.getShooter() + SSMClient.parseChar;

            return packedProj;
        }
    }
    public static Rocket unPack(String s){
        if(s.equals("null") || s.isEmpty())
            return null;
        String[] myInfo = s.split(SSMClient.parseChar);

        return new Rocket((int)Double.parseDouble(myInfo[0]),(int)Double.parseDouble(myInfo[1]),
                Integer.parseInt(myInfo[2]),Integer.parseInt(myInfo[3]),Integer.parseInt(myInfo[4]),myInfo[5],
                Integer.parseInt(myInfo[6]));
    }
    public static String packArray(ArrayList<Rocket> pList){
        String packedPList = "";
        for(int i=pList.size()-1;i>=0;i--){
            Rocket p = pList.get(i);
            packedPList = packedPList.concat(pack(p)+Projectile.arrayParseChar);
        }
        return packedPList;
    }
    public static ArrayList<Rocket> unPackArray(String packedPList){
        ArrayList<Rocket> pList = new ArrayList<>();
        for(String s: packedPList.split(Projectile.arrayParseChar)){
            pList.add(unPack(s));
        }
        return pList;
    }
}

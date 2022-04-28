/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SSMCode.PlayerAttacks;

/**
 *
 * @author 22cloteauxm
 */

import SSMCode.Actor;
import SSMCode.Player;
import SSMEngines.SSMClient;
import SSMEngines.old.PlayerOld;
import SSMEngines.util.AudioUtility;
import SSMEngines.util.Poolkit;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import javax.sound.sampled.Clip;

public class Projectile extends Actor{
    
    //Constants
    public static final int LEFT = -1;
    public static final int RIGHT = 1;
    
    private static ArrayList<Image> myImages;
    
    //Instance Variables
    private final String team;
    private final int shooter;
    private final boolean bossMode;
    private boolean isNull;
    private boolean playedSFX;
    private int dir;
    private final Color myColor;
    private static Clip projSFX;
   
    public Projectile(int x,int y, int w,int h, int direction, String team_, int shooter, boolean bossMode, boolean sfx){
        super(x,y,w,h);
        
        isNull = false;
        this.shooter = shooter;
        this.bossMode = bossMode;
        dir = direction;
        team = team_;
        playedSFX = sfx;
        
        setXVel(15*dir);

        if(shooter == Player.ADAM)
            myColor = Color.red;
        else if(shooter == Player.LISON)
            myColor = new Color(3,190,252);
        else
            myColor = Color.gray;
    }
    
    public boolean isNull(){return isNull;}
    public boolean playedSFX(){return playedSFX;}
    public int getDirection(){return dir;}
    public int getShooter(){return shooter;}
    public boolean isBossMode(){return bossMode;}

    public void setDirection(int c){dir = c;}
    public void setPlayedSFX(boolean c){playedSFX = c;}
    public void setIsNull(boolean c){isNull = c;}
    
    public void animateMovement(ArrayList<Player> targets){
        //if shooter is Obama, Emi, or Spock not in boss mode
        if(shooter == Player.OBAMA || shooter == Player.EMI
                || (shooter == Player.SPOCK && !bossMode))
            setXVel(8*dir);
        //else if I am Neel,
        else if(shooter == Player.NEEL)
            setXVel(15*dir);

        //move based on speed
        setX(getX()+getXVel());
    }
    public void animateDamage(ArrayList<Player> targets){
        for(Player target: targets){
            //if the projectile hits the target, and wasn't shot by
            //a fellow team member and the target isn't untargetable
            if(this.getHitBox().intersects(target.getHitBox()) 
                    && !team.equals(target.getTeam())
                    && !target.isUntargetable())
            {
                
                target.setPercentage(target.getPercentage()+3.5);
                isNull = true;
                
                if(getW() >= 10){
                    target.setDamageXVel((1.5+3*target.getPercentage()/25)*dir);
                    target.setYVel((target.getYVel()-1.5-2*target.getPercentage()/75));
                } else{
                    target.setDamageXVel(.75*(1.5+3*target.getPercentage()/25)*dir);
                    target.setYVel(.85*(target.getYVel()-2.5));
                }
                   
                if(shooter == PlayerOld.EMI)
                    target.setConfusionDuration(3);
                
                if(shooter == PlayerOld.NEEL && this.getW() >= 80)
                    target.setFlameDuration(3);
            }
        }
    }
    
    public void draw(Graphics g, ImageObserver io){
        //Everyone else
        if(shooter!= PlayerOld.SPOCK && shooter!= PlayerOld.OBAMA
                && shooter != PlayerOld.EMI && shooter != PlayerOld.NEEL){
            g.setColor(myColor);
            g.fillOval((int)getX(),(int)getY(),getW(),getH());
            g.setColor(Color.white);
            g.drawOval((int)getX(),(int)getY(),getW(),getH());
        } 
        //Spock Boss
        else if(bossMode)
            g.drawImage(myImages.get(0), (int)getX(),(int)getY(),getW(),getH(), io);
        //OBAMA
        else if(shooter == PlayerOld.OBAMA){
            if(dir == Projectile.RIGHT)
                g.drawImage(myImages.get(3), (int)getX(),(int)getY(),getW(),getH(), io);
            else
                g.drawImage(myImages.get(4), (int)getX(),(int)getY(),getW(),getH(), io);
        }
        //Emi E-M-I
        else if(shooter == PlayerOld.EMI)
            g.drawImage(myImages.get(5), (int)getX(),(int)getY(),getW(),getH(), io);
        //Neel Regular
        else if(shooter == PlayerOld.NEEL && getW() < 70){
            if(dir == Projectile.RIGHT)
                g.drawImage(myImages.get(6), (int)getX(),(int)getY(),getW(),getH(), io);
            else
                g.drawImage(myImages.get(7), (int)getX(),(int)getY(),getW(),getH(), io);
        //Neel Flame Arrow    
        }else if(shooter == PlayerOld.NEEL && getW() > 70){
            if(dir == Projectile.RIGHT)
                g.drawImage(myImages.get(8), (int)getX(),(int)getY(),getW(),getH(), io);
            else
                g.drawImage(myImages.get(9), (int)getX(),(int)getY(),getW(),getH(), io);
        }
        //Spock non boss
        else{
            if(dir == RIGHT)
                g.drawImage(myImages.get(1), (int)getX(),(int)getY(),getW(),getH(), io);
            else
                g.drawImage(myImages.get(2), (int)getX(),(int)getY(),getW(),getH(), io);
        }
    }
    
    public static void initSFX(){       
        projSFX = AudioUtility.loadClip("SSMMusic/SFX/pew.wav");
    }
    
    public static void playSFX(){
        projSFX.stop();
        projSFX.setFramePosition(0);
        projSFX.start();
    }
    
    public static void initImages(){
        Poolkit toolkit = new Poolkit();
        
        myImages = new ArrayList<>();
        
        myImages.add(toolkit.getImage("SSMImages/Spock/Spock_Missle.png"));
        myImages.add(toolkit.getImage("SSMImages/Spock/R_F_1.png"));
        myImages.add(toolkit.getImage("SSMImages/Spock/R_B_1.png"));
        myImages.add(toolkit.getImage("SSMImages/Obama/BodyGuard_F.png"));
        myImages.add(toolkit.getImage("SSMImages/Obama/Bodyguard_B.png"));
        myImages.add(toolkit.getImage("SSMImages/Emi/confusion.png"));
        myImages.add(toolkit.getImage("SSMImages/Neel/Arrow_F.png"));
        myImages.add(toolkit.getImage("SSMImages/Neel/Arrow_B.png"));
        myImages.add(toolkit.getImage("SSMImages/Neel/FireArrow_F.png"));
        myImages.add(toolkit.getImage("SSMImages/Neel/FireArrow_B.png"));
    }

    //----------------------------------------
    //Packing and unpacking projectiles
    //----------------------------------------
    public static final String arrayParseChar = "&";
    
    public static String pack(Projectile p){
        if(p == null)
            return "null";
        else{
            String packedProj = "";
            packedProj += (int)p.getX() + SSMClient.parseChar;
            packedProj += (int)p.getY() + SSMClient.parseChar;
            packedProj += p.getW() + SSMClient.parseChar;
            packedProj += p.getH() + SSMClient.parseChar;
            packedProj += p.dir + SSMClient.parseChar;
            packedProj += p.team + SSMClient.parseChar;
            packedProj += p.getShooter() + SSMClient.parseChar;
            packedProj += p.isBossMode() + SSMClient.parseChar;
            packedProj += p.playedSFX() + SSMClient.parseChar;

            return packedProj;
        }
    }
    public static Projectile unPack(String s){
        if(s.equals("null") || s.isEmpty())
            return null;
        String[] myInfo = s.split(SSMClient.parseChar);
        return new Projectile((int)Double.parseDouble(myInfo[0]),(int)Double.parseDouble(myInfo[1]),
                Integer.parseInt(myInfo[2]),Integer.parseInt(myInfo[3]),Integer.parseInt(myInfo[4]),myInfo[5],
                Integer.parseInt(myInfo[6]),Boolean.parseBoolean(myInfo[7]),Boolean.parseBoolean(myInfo[8]));
    }
    public static String packArray(ArrayList<Projectile> pList){
        String packedPList = "";
        if(pList.isEmpty())
            return "null";
        for(int i=pList.size()-1;i>=0;i--){
            Projectile p = pList.get(i);
            packedPList = packedPList.concat(pack(p)+arrayParseChar);
        }
        return packedPList;
    }
    public static ArrayList<Projectile> unPackArray(String packedPList){
        ArrayList<Projectile> pList = new ArrayList<>();
        if(packedPList.equals("null"))
            return pList;
        for(String s: packedPList.split(arrayParseChar)){
            pList.add(unPack(s));
        }
        return pList;
    }
}

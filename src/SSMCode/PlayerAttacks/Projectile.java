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
import SSMEngines.AudioUtility;
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
    private String team;
    private boolean isNull;
    private boolean playedSFX;
    private int dir;
    private Color myColor;
    private static Clip projSFX;
   
    public Projectile(int x,int y, int w,int h, int direction, String team_, boolean sfx){
        super(x,y,w,h);
        
        isNull = false;
        dir = direction;
        team = team_;
        playedSFX = sfx;
        
        setXVel(15*dir);
    }
    
    public boolean isNull(){return isNull;}
    public boolean playedSFX(){return playedSFX;}
    public int getDirection(){return dir;}
    
    public void setDirection(int c){dir = c;}
    public void setPlayedSFX(boolean c){playedSFX = c;}    
    public void setColor(Color c){myColor = c;}
    public void setIsNull(boolean c){isNull = c;}
    
    public void animateMovement(Player target){setX(getX()+getXVel());}
    public void animateDamage(ArrayList<Player> targets, Player shooter){              
        for(Player target: targets){
            if(this.getHitBox().intersects(target.getHitBox()) 
                    && !team.equals(target.getTeam())
                    && !target.isUntargetable())
            {
                
                target.setPercentage(target.getPercentage()+3.5);
                isNull = true;
                
                if(getW() >= 10){
                    target.setXVel((1.5+3*target.getPercentage()/25)*dir);
                    target.setYVel((target.getYVel()-1.5-2*target.getPercentage()/75));
                } else{
                    target.setXVel(.75*(1.5+3*target.getPercentage()/25)*dir);
                    target.setYVel(.85*(target.getYVel()-2.5));
                }
                   
                if(shooter.getCharacter() == Player.EMI)
                    target.setConfusionDuration(3);
                
                if(shooter.getCharacter() == Player.NEEL && this.getW() >= 80)
                    target.setFlameDuration(3);
            }
        }
    }
    
    public void draw(Player shooter, Graphics g, ImageObserver io){
        //Everyone else
        if(shooter.getCharacter()!=Player.SPOCK && shooter.getCharacter()!=Player.OBAMA
                && shooter.getCharacter() != Player.EMI && shooter.getCharacter() != Player.NEEL){
            g.setColor(myColor);
            g.fillOval((int)getX(),(int)getY(),getW(),getH());
            g.setColor(Color.white);
            g.drawOval((int)getX(),(int)getY(),getW(),getH());
        } 
        //Spock Boss
        else if(shooter.isBoss())
            g.drawImage(myImages.get(0), (int)getX(),(int)getY(),getW(),getH(), io);
        //OBAMA
        else if(shooter.getCharacter() == Player.OBAMA){
            setSize(shooter.getW(),shooter.getH());
            setXVel(8*dir);
            if(dir == Projectile.RIGHT)
                g.drawImage(myImages.get(3), (int)getX(),(int)getY(),getW(),getH(), io);
            else
                g.drawImage(myImages.get(4), (int)getX(),(int)getY(),getW(),getH(), io);
        }
        //Emi E-M-I
        else if(shooter.getCharacter() == Player.EMI){
            setXVel(8*dir);
            g.drawImage(myImages.get(5), (int)getX(),(int)getY(),getW(),getH(), io);
        }
        //Neel Regular
        else if(shooter.getCharacter() == Player.NEEL && getW() < 70){
            setXVel(20*dir);
            if(dir == Projectile.RIGHT)
                g.drawImage(myImages.get(6), (int)getX(),(int)getY(),getW(),getH(), io);
            else
                g.drawImage(myImages.get(7), (int)getX(),(int)getY(),getW(),getH(), io);
        //Neel Flame Arrow    
        }else if(shooter.getCharacter() == Player.NEEL && getW() > 70){
            setXVel(20*dir);
            if(dir == Projectile.RIGHT)
                g.drawImage(myImages.get(8), (int)getX(),(int)getY(),getW(),getH(), io);
            else
                g.drawImage(myImages.get(9), (int)getX(),(int)getY(),getW(),getH(), io);
        }
        //Spock non boss
        else{
            dir = shooter.getDirection();
            setSize(shooter.getW(),shooter.getH());
            setY(shooter.getY());
            setXVel(8*shooter.getDirection());
            if(shooter.getDirection() == Projectile.RIGHT)
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
    
    public static void initImage(){
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        
        myImages = new ArrayList<>();
        
        myImages.add(toolkit.getImage("SSMImages/Spock/Spock_Missle.png"));
        myImages.add(toolkit.getImage("SSMImages/Spock/R_1_F.png"));
        myImages.add(toolkit.getImage("SSMImages/Spock/R_1_B.png"));
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
    
    private static String parseChar = ",";
    
    public static String pack(Projectile p){
        if(p == null)
            return "null";
        else{
            String packedProj = "";
            packedProj += (int)p.getX() + parseChar;
            packedProj += (int)p.getY() + parseChar;
            packedProj += (int)p.getW() + parseChar;
            packedProj += (int)p.getH() + parseChar;
            packedProj += (int)p.dir + parseChar;
            packedProj += p.team + parseChar;
            packedProj += p.playedSFX() + parseChar;

            return packedProj;
        }
    }
    public static Projectile unPack(String s){
        String[] myInfo = s.split(parseChar);
        
        Projectile unPacked = new Projectile((int)Double.parseDouble(myInfo[0]),(int)Double.parseDouble(myInfo[1]),
                Integer.parseInt(myInfo[2]),Integer.parseInt(myInfo[3]),Integer.parseInt(myInfo[4]),myInfo[5],
                Boolean.parseBoolean(myInfo[6]));
        
        return unPacked;
    }
    public static ArrayList<String> packArray(ArrayList<Projectile> pList){
        ArrayList<String> packedPList = new ArrayList<>();
        
        for(int i=pList.size()-1;i>=0;i--){
            Projectile p = pList.get(i);
            packedPList.add(pack(p));
        }
        
        return packedPList;
    }
    public static ArrayList<Projectile> unPackArray(ArrayList<String> packedPList){
        ArrayList<Projectile> pList = new ArrayList<>();
        
        for(String s: packedPList){
            pList.add(unPack(s));
        }
        
        return pList;
    }
}

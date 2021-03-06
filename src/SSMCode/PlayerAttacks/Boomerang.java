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

/**
 *
 * @author 22cloteauxm
 */
public class Boomerang extends Actor{
    
    private final String team;
    private int dir;
    private boolean returning;
    private boolean isNull;
    private boolean intersecting;
    private final int maxX, minX, finalX;
    private final int shooter;

    private static Image sword1, sword2, ball;
    private double drawTimer;
    
    public Boomerang(int x,int y,int w, int h, int direction, int finalX_, boolean returning_,
            String team_, boolean isNull_, double draw, int shooter){
        super(x,y,w,h);
        
        returning = returning_;
        team = team_;
        dir = direction;
        isNull = isNull_;
        drawTimer = draw;
        this.shooter = shooter;

        finalX = finalX_;
        
        if(finalX >= x){
            maxX = finalX;
            minX = -1000;
        } else { 
            maxX = 10000;
            minX = finalX;
        }
        
        setXVel(12.5*dir);
    }
    
    public int getDirection(){return dir;}
    public double getDrawTimer(){return drawTimer;}
    public boolean isNull(){return isNull;}
    public boolean isReturning(){return returning;}
    public int getFinalX(){return finalX;}
    public String getTeam(){return team;}

    public void setIntersecting(Boolean b){intersecting = b;}

    public void animateMovement(){
        if(intersecting && returning)
            isNull = true;

        if(drawTimer < 0)
            drawTimer = 1;
        drawTimer -=0.1;
        
        if((getX() >= maxX || getX() <= minX) && !returning){
            returning = true;
            setXVel(getXVel()*-1);
            dir*=-1;
        }

        setX(getX()+getXVel());
    }
    
    public void animateDamage(ArrayList<Player> targets){
        for(Player target: targets){
            if(this.getHitBox().intersects(target.getHitBox()) 
                    && !team.equals(target.getTeam())
                    && !target.isUntargetable())
            {    
                target.setPercentage(target.getPercentage()+1.5);
                target.setDamageXVel((2.5+1*target.getPercentage()/25)*dir);
                target.setYVel(target.getYVel()-0.5*(1.5+target.getPercentage()/100));
            }
        }
    }
    
    public void draw(Graphics g, ImageObserver io){
        
        Image currentImage = null;

        if(shooter == Player.LAWRENCE) {
            if (drawTimer > 0.5)
                currentImage = sword1;
            else
                currentImage = sword2;
        }
        else if(shooter == Player.RISHI)
            currentImage = ball;
        
        g.drawImage(currentImage, (int)getX(),(int)getY(),getW(),getH(), io);
    }
    
    public static void initImages(Poolkit toolkit){
        sword1 = toolkit.getImage("SSMImages/Lawrence/Sword_Boomerang_1.png");
        sword2 = toolkit.getImage("SSMImages/Lawrence/Sword_Boomerang_2.png");
        ball = toolkit.getImage("SSMImages/Rishi/tennisBall.png");
    }


    //----------------------------------------
    //Packing and unpacking boomerangs
    //----------------------------------------

    public static String pack(Boomerang r){
        if(r == null)
            return "null";
        else{
            String packedProj = "";
            packedProj += r.getX() + SSMClient.parseChar;
            packedProj += r.getY() + SSMClient.parseChar;
            packedProj += r.getW() + SSMClient.parseChar;
            packedProj += r.getH() + SSMClient.parseChar;
            packedProj += r.dir + SSMClient.parseChar;
            packedProj += r.finalX + SSMClient.parseChar;
            packedProj += r.returning + SSMClient.parseChar;
            packedProj += r.team + SSMClient.parseChar;
            packedProj += r.isNull + SSMClient.parseChar;
            packedProj += r.drawTimer + SSMClient.parseChar;
            packedProj += r.shooter + SSMClient.parseChar;
            return packedProj;
        }
    }
    public static Boomerang unPack(String s){
        if(s.equals("null") || s.isEmpty())
            return null;
        String[] myInfo = s.split(SSMClient.parseChar);

        return new Boomerang((int)Double.parseDouble(myInfo[0]),(int)Double.parseDouble(myInfo[1]),
                Integer.parseInt(myInfo[2]),Integer.parseInt(myInfo[3]), Integer.parseInt(myInfo[4]),
                Integer.parseInt(myInfo[5]), Boolean.parseBoolean(myInfo[6]),myInfo[7],
                Boolean.parseBoolean(myInfo[8]),Double.parseDouble(myInfo[9]),Integer.parseInt(myInfo[10]));
    }
    public static String packArray(ArrayList<Boomerang> pList){
        String packedPList = "";
        if(pList.isEmpty())
            return "null";
        for(int i=pList.size()-1;i>=0;i--){
            Boomerang p = pList.get(i);
            packedPList = packedPList.concat(pack(p)+Projectile.arrayParseChar);
        }
        return packedPList;
    }
    public static ArrayList<Boomerang> unPackArray(String packedPList){
        ArrayList<Boomerang> pList = new ArrayList<>();
        if(packedPList.equals("null"))
            return pList;
        for(String s: packedPList.split(Projectile.arrayParseChar)){
            pList.add(unPack(s));
        }
        return pList;
    }

}

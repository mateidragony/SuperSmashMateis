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
public class RainingCode extends Actor{
    private static Image whitehouse;
    private static Image code;

    private final String team;
    private final int shooter;
    
    public RainingCode(int x, int y, String team, int shooter){
        super(x,y,977,580);
        this.shooter = shooter;
        this.team = team;
        if(shooter == Player.OBAMA)
            this.setSize(600, 356);

        setYVel(5);    
    }
    
    public void animate(ArrayList<Player> targets){
        setY(getY()+getYVel());

        Player bestTarget = targets.get(0);
        int leastDistance = 9999;
        for(Player target : targets){
            if(Math.abs(target.getX()-getX())<leastDistance){
                bestTarget = target;
                leastDistance = (int)Math.abs(target.getX()-getX());
            }
        }

        if(shooter== Player.OBAMA)
            setX(bestTarget.getX()+bestTarget.getW()/2.0-getW()/2.0);

        for(Player target : targets) {
            if (this.getHitBox().intersects(target.getHitBox())
                    && !target.isUntargetable()
                    && !team.equals(target.getTeam())) {
                target.setPercentage(target.getPercentage() + 0.11);
                if (shooter == Player.OBAMA)
                    target.setPercentage(target.getPercentage() + 0.21);
            }
        }
    }
    
    public void draw(Graphics g, ImageObserver io){
        if(shooter == Player.SPOCK){
            g.drawImage(code,(int)getX(),(int)getY(),getW(),getH(), io);
        } else
            g.drawImage(whitehouse,(int)getX(),(int)getY(),getW(),getH(), io);
    }
    
    public static void initImage(Poolkit toolkit){
        whitehouse = toolkit.getImage("SSMImages/Obama/White_House.png");
        code = toolkit.getImage("SSMImages/Spock/Code.png");
    }


    //----------------------------------------
    //Packing and unpacking punches
    //----------------------------------------
    public static String pack(RainingCode p){
        if(p==null)
            return "null";
        String str = "";
        str += (int)p.getX() + SSMClient.parseChar;
        str += (int)p.getY() + SSMClient.parseChar;
        str += p.team + SSMClient.parseChar;
        str += p.shooter + SSMClient.parseChar;
        return str;
    }
    public static RainingCode unPack(String s){
        if(s.equals("null") || s.isEmpty())
            return null;
        String[] data = s.split(SSMClient.parseChar);
        return new RainingCode(Integer.parseInt(data[0]),Integer.parseInt(data[1]),data[2],Integer.parseInt(data[3]));
    }
    
}

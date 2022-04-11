/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SSMCode.PlayerAttacks;

import SSMCode.Player;
import SSMEngines.old.PlayerOld;

import java.awt.*;
import java.util.ArrayList;

/**
 *
 * @author 22cloteauxm
 */
public class HomingShot extends Projectile{
    
    public HomingShot(int x,int y, int direction, String team, int shooter, boolean bossMode, boolean sfx){
        super(x,y,30,30,direction,team,shooter,bossMode,sfx);
    }
    
    public void animateMovement(ArrayList<Player> targets){
        double meTargetHypot = 99999;
        int bestIndex = 0;

        for(int i=0; i<targets.size(); i++){
            Player target = targets.get(i);
            double testHypot = Math.pow(Math.pow(target.getX()-getX(),2)+Math.pow(target.getY()-getY(),2),0.5);
            if(testHypot<meTargetHypot){
                meTargetHypot = testHypot;
                bestIndex = i;
            }
        }

        Player target = targets.get(bestIndex);
        
        setXVel(3*((target.getX()-getX())/meTargetHypot));
        setYVel(3*((target.getY()-getY()) /meTargetHypot));
        
        if(getXVel() > 0)
            setDirection(Projectile.RIGHT);
        else
            setDirection(Projectile.LEFT);
        
        setY(getY()+getYVel());
        super.animateMovement(targets);
    }
}

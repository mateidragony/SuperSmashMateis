/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SSMCode.PlayerAttacks;

import SSMCode.*;
import java.awt.*;

/**
 *
 * @author 22cloteauxm
 */
public class HomingShot extends Projectile{
    
    private static Image myImage;
    
    public HomingShot(int x,int y, int direction, String team, boolean sfx){
        super(x,y,30,30,direction,team,sfx);
    }
    
    public void animateMovement(Player target){
        double meTargetHypot = Math.pow(Math.pow(target.getX()-getX(),2)+Math.pow(target.getY()-getY(),2),0.5);
        
        setXVel(3*((target.getX()-getX())/meTargetHypot));
        setYVel(3*((target.getY()-getY()) /meTargetHypot));
        
        if(getXVel() > 0)
            setDirection(Projectile.RIGHT);
        else
            setDirection(Projectile.LEFT);
        
        setY(getY()+getYVel());
        super.animateMovement(target);
    }
}

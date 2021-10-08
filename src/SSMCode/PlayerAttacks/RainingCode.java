/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SSMCode.PlayerAttacks;

import SSMCode.*;

import java.awt.*;
import java.awt.image.ImageObserver;

/**
 *
 * @author 22cloteauxm
 */
public class RainingCode extends Actor{
    private static Image whitehouse;
    private static Image code;
    
    public RainingCode(int y){
        super(60,y,977,580);

        setYVel(5);    
    }
    
    public void animate(Player target,Player shooter){
        setY(getY()+getYVel());
        
        if(shooter.getCharacter()==Player.OBAMA) {
            setSize(750,280);
            setX(target.getX()+target.getW()/2-getW()/2);
        }
        
        if(this.getHitBox().intersects(target.getHitBox()) 
                && !target.isUntargetable())
        {
            target.setPercentage(target.getPercentage()+0.11);
            if(shooter.getCharacter() == Player.OBAMA)
                target.setPercentage(target.getPercentage()+0.21);
        }
        if(this.getHitBox().intersects(shooter.getHitBox()) && shooter.getCharacter() == Player.OBAMA 
                && !shooter.isUntargetable())
        {
            shooter.setPercentage(shooter.getPercentage()+0.11);
            if(shooter.getCharacter() == Player.OBAMA)
                shooter.setPercentage(shooter.getPercentage()+0.21);
        }
    }
    
    public void draw(Graphics g,ImageObserver io,Player shooter){
        if(shooter.getCharacter() == Player.SPOCK){
            g.drawImage(code,(int)getX(),(int)getY(),getW(),getH(), io);
        } else
            g.drawImage(whitehouse,(int)getX(),(int)getY(),getW(),getH(), io);
    }
    
    public static void initImage(Toolkit toolkit){
        whitehouse = toolkit.getImage("SSMImages/Obama/White_House.png");
        code = toolkit.getImage("SSMImages/Spock/Code.png");
    }
    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SSMCode.PlayerAttacks;

import SSMCode.Actor;
import SSMCode.Player;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import javax.sound.sampled.Clip;

/**
 *
 * @author 22cloteauxm
 */
public class Rocket extends Actor{

    private static Image iceCube;
    
    private String team;
    private boolean playedSFX;
    private int dir;
    private static Clip projSFX;
    private static Image myImageRight, myImageLeft, eagle_F, eagle_B, arrow_F, arrow_B;
    
    public Rocket(int x,int y, int direction, String team_){
        super(x,y,50,50);
        
        dir = direction;
        
        if(dir == Projectile.LEFT)
            setX(getX()-20);
            
        team = team_;
        
        setXVel(12.5*dir);
    }
    
    public int getDir(){return dir;}
    
    public void animateMovement(){setX(getX()+getXVel());}
    public void animateDamage(ArrayList<Player> targets, Player shooter){                
        for(Player target:  targets){
            if(this.getHitBox().intersects(target.getHitBox()) 
                    && !team.equals(target.getTeam())
                    && !target.isUntargetable())
            {    
                target.setPercentage(target.getPercentage()+1.5);
                target.setXVel((2.5+1*target.getPercentage()/25)*dir);
                target.setYVel(target.getYVel()-0.5*(1.5+target.getPercentage()/100));
                
                if(shooter.getCharacter() == Player.LISON){
                    target.setStunDuration(.9);
                    target.setPercentage(target.getPercentage()-.5);
                }
                if(shooter.getCharacter() == Player.NEEL){
                    target.setStunDuration(.9);
                    target.setPercentage(target.getPercentage()-.5);
                }
            }
        }
        
    }
    
    public void draw(Graphics g, ImageObserver io, Player shooter){
        
        if(shooter.getCharacter() == Player.NEEL)
            setSize(65,10);
        
        if(shooter.getCharacter() == Player.LISON)
            g.drawImage(iceCube, (int)getX(),(int)getY(),(int)(getW()),(int)(getH()), io);
        else{
            //Matei
            if(dir == Projectile.RIGHT && shooter.getCharacter() == Player.MATEI)
                g.drawImage(myImageRight, (int)getX(),(int)getY(),(int)(getW()),(int)(getH()), io);
            else if(dir == Projectile.LEFT && shooter.getCharacter() == Player.MATEI)
                g.drawImage(myImageLeft, (int)getX(),(int)getY(),(int)(getW()),(int)(getH()), io);
            //Obama
            else if(dir == Projectile.RIGHT && shooter.getCharacter() == Player.OBAMA)
                g.drawImage(eagle_F, (int)getX(),(int)getY(),(int)(getW())+30,(int)(getH())+10, io);
            else if(dir == Projectile.LEFT && shooter.getCharacter() == Player.OBAMA)
                g.drawImage(eagle_B, (int)getX(),(int)getY(),(int)(getW())+30,(int)(getH())+10, io);
            //Neel
            if(dir == Projectile.RIGHT && shooter.getCharacter() == Player.NEEL){
                g.drawImage(arrow_F, (int)getX(),(int)getY(),(int)(getW()),(int)(getH()), io);
                setXVel(20*dir);
            }
            else if(dir == Projectile.LEFT && shooter.getCharacter() == Player.NEEL){
                g.drawImage(arrow_B, (int)getX(),(int)getY(),(int)(getW()),(int)(getH()), io);
                setXVel(20*dir);
            }
        }
    }
    
    public static void initImage(){
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        
        myImageRight = toolkit.getImage("SSMImages/Matei/rocket.png");
        myImageLeft = toolkit.getImage("SSMImages/Matei/rocket_L.png");
        
        iceCube = toolkit.getImage("SSMImages/Lison/ice.png");
        eagle_F = toolkit.getImage("SSMImages/Obama/eagle_F.png");
        eagle_B = toolkit.getImage("SSMImages/Obama/eagle_B.png");
        
        arrow_F = toolkit.getImage("SSMImages/Neel/IceArrow_F.png");
        arrow_B = toolkit.getImage("SSMImages/Neel/IceArrow_B.png");
    }
    
}

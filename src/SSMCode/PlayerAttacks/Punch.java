/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SSMCode.PlayerAttacks;

import SSMCode.GameObject;
import SSMCode.Player;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
/**
 *
 * @author 22cloteauxm
 */
public class Punch extends GameObject{
    
    protected static ArrayList<Image> myImages;
    
    private String team;
    private int dir;
    private boolean canHurt;
    
    private double punchCD;
    
    public Punch(int x,int y, int direction, String team_, double cd, boolean hurts){
        super(x,y,30,5);
        
        dir = direction;
        team = team_;
        
        canHurt = hurts;
        
        punchCD = cd;
    } 
    
    public int getDirection(){return dir;}
    public double getPunchCD(){return punchCD;}
    public boolean getCanHurt(){return canHurt;}
    public String getTeam(){return team;}
    public Rectangle getHitBox(Player shooter){
        
        //Massive Laser
        if(shooter.getCharacter() == Player.SPOCK || (shooter.getCharacter() == Player.EMI && getW() >=1000)){
            if(dir == Projectile.RIGHT)
                return new Rectangle((int)getX()+40,(int)(getY()+50-3+shooter.getH()/3-getH()/1.5),
                        (int)(getW()),getH()-50);
            else
                return new Rectangle((int)(getX()+3-getW()),(int)(getY()+50-3+shooter.getH()/3-getH()/1.5),
                        (int)(getW()),getH()-50);
        }
        
        //Elastijemi
        if(dir == Projectile.LEFT && shooter.getCharacter() == Player.EMI)
            return new Rectangle((int)(getX()-getW()+punchCD*50),(int)(getY()+30),
                    (int)(getW()+10-punchCD*50),getH());
        else if(dir == Projectile.RIGHT && shooter.getCharacter() == Player.EMI)
            return new Rectangle((int)getX()+40,(int)(getY()+30),
                    (int)(getW()-punchCD*50),getH());
        
        //Lawrence
        if(dir == Projectile.LEFT && shooter.getCharacter()==Player.LAWRENCE){
            return new Rectangle((int)(getX()-getW()+punchCD*10),(int)(getY()-12+shooter.getH()/3),
                    (int)(getW()+10-punchCD*10),getH()*2);
        } else if(dir == Projectile.RIGHT && shooter.getCharacter()==Player.LAWRENCE) {
            return new Rectangle((int)getX()+shooter.getW(),(int)(getY()-12+shooter.getH()/3),
                    (int)(getW()-punchCD*10),getH()*2);
        }
        
        //Normal
        if(dir == Projectile.LEFT)
            return new Rectangle((int)(getX()-getW()+punchCD*10),(int)(getY()+30),
                    (int)(getW()+10-punchCD*10),getH());
        else
            return new Rectangle((int)getX()+40,(int)(getY()+30),
                    (int)(getW()-punchCD*10),getH());
    }
    
    public void setPunchCD(double c){punchCD = c;}
    public void setCanHurt(boolean c){canHurt = c;}
    public void setDirection(int c){dir = c;}
    
    public void animate(Player puncher, Player target){
        if(punchCD > 0)
            punchCD -=1/4.0;
        else
            punchCD = 0;
        
        setX(puncher.getX());
        setY(puncher.getY());
        
        if(this.getHitBox(puncher).intersects(target.getHitBox()) 
                && !team.equals(target.getTeam())
                && !target.isUntargetable()
                && canHurt)
        {
            if(puncher.getCharacter() == Player.UMER){
                target.setPercentage(target.getPercentage()+1.5);
                target.setXVel(2.25*(0.5+1.5*target.getPercentage()/20)*dir);
                target.setYVel(.75*(target.getYVel()-1.5-2*target.getPercentage()/50));
            }else{  
                target.setPercentage(target.getPercentage()+1);
                target.setXVel(1.5*(0.5+1.5*target.getPercentage()/20)*dir);
                target.setYVel(.5*(target.getYVel()-1.5-2*target.getPercentage()/50));
            }
        }
        
        if(punchCD == 0)
            canHurt = false;
    }
    
    public void draw(Player puncher, Graphics g, ImageObserver io){
        if(team.equals("blue"))
            g.setColor(Color.BLUE);
        else
            g.setColor(Color.RED);

        
        if(punchCD > 0){
            
            //Umer
            if(dir == Projectile.LEFT && puncher.getCharacter()==Player.UMER){
                g.drawImage(myImages.get(1),(int)(getX()+9-getW()+punchCD*10),(int)(getY()-17+puncher.getH()/3),
                        (int)(getW()+10-punchCD*10),getH()*2,io);
            } else if(dir == Projectile.RIGHT && puncher.getCharacter()==Player.UMER) {
                g.drawImage(myImages.get(0),(int)getX()-17+puncher.getW(),(int)(getY()-17+puncher.getH()/3),
                        (int)(getW()-punchCD*10),getH()*2,io);
            }
           
            //Gaushal
            if(dir == Projectile.LEFT && puncher.getCharacter()==Player.KAUSHAL){
                g.drawImage(myImages.get(4),(int)(getX()-getW()+punchCD*10),(int)(getY()-5+puncher.getH()/3),
                        (int)(getW()+10-punchCD*10),(int)(getH()*1.5),io);
            } else if(dir == Projectile.RIGHT && puncher.getCharacter()==Player.KAUSHAL) {
                g.drawImage(myImages.get(4),(int)getX()+puncher.getW(),(int)(getY()-5+puncher.getH()/3),
                        (int)(getW()-punchCD*10),(int)(getH()*1.5),io);
            }
            
            //Salome
            if(dir == Projectile.LEFT && puncher.getCharacter()==Player.SALOME){
                g.drawImage(myImages.get(6),(int)(getX()+3-getW()+punchCD*10),(int)(getY()-3+puncher.getH()/3),
                        (int)(getW()+10-punchCD*10),24,io);
            } else if(dir == Projectile.RIGHT && puncher.getCharacter()==Player.SALOME) {
                g.drawImage(myImages.get(5),(int)getX()-14+puncher.getW(),(int)(getY()-3+puncher.getH()/3),
                        (int)(getW()-punchCD*10),24,io);
            }
            
            //Spock
            if(dir == Projectile.LEFT && puncher.getCharacter()==Player.SPOCK){
                g.drawImage(myImages.get(8),(int)(getX()+3-getW()),(int)(getY()-3+puncher.getH()/3-getH()/1.5),
                        (int)(getW()),getH(),io);
            } else if(dir == Projectile.RIGHT && puncher.getCharacter()==Player.SPOCK) {
                g.drawImage(myImages.get(7),(int)getX()+40,(int)(getY()-3+puncher.getH()/3-getH()/1.5),
                        (int)(getW()),getH(),io);
            }
            
            //Jemi (Jumex)
            if(dir == Projectile.LEFT && puncher.getCharacter()==Player.EMI && getW() >= 1000){
                g.drawImage(myImages.get(10),(int)(getX()+13-getW()),(int)(getY()-10+puncher.getH()/3-getH()/1.5),
                        (int)(getW()),getH(),io);
            } else if(dir == Projectile.RIGHT && puncher.getCharacter()==Player.EMI && getW() >= 1000) {
                g.drawImage(myImages.get(9),(int)getX()+40,(int)(getY()-10+puncher.getH()/3-getH()/1.5),
                        (int)(getW()),getH(),io);
            }
            //Jemi (Elastijemi)
            else if(dir == Projectile.LEFT && puncher.getCharacter()==Player.EMI){
                g.drawImage(myImages.get(12),(int)(getX()+14-getW()+punchCD*50),(int)(getY()-14+puncher.getH()/3),
                        (int)(getW()+10-punchCD*50),getH()*2,io);
            } else if(dir == Projectile.RIGHT && puncher.getCharacter()==Player.EMI) {
                g.drawImage(myImages.get(11),(int)getX()-24+puncher.getW(),(int)(getY()-14+puncher.getH()/3),
                        (int)(getW()-punchCD*50),getH()*2,io);
            }
            //Lawrence
            else if(dir == Projectile.LEFT && puncher.getCharacter()==Player.LAWRENCE){
                g.drawImage(myImages.get(14),(int)(getX()-getW()+punchCD*10),(int)(getY()-12+puncher.getH()/3),
                        (int)(getW()+10-punchCD*10),getH()*2,io);
            } else if(dir == Projectile.RIGHT && puncher.getCharacter()==Player.LAWRENCE) {
                g.drawImage(myImages.get(13),(int)getX()+puncher.getW(),(int)(getY()-12+puncher.getH()/3),
                        (int)(getW()-punchCD*10),getH()*2,io);
            }
            
        }
    }
    
    public static void initImages(){
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        myImages = new ArrayList<>();
        
        myImages.add(toolkit.getImage("SSMImages/Umer/Punch_R.png"));
        myImages.add(toolkit.getImage("SSMImages/Umer/Punch_L.png"));
        myImages.add(toolkit.getImage("SSMImages/Umer/V_Punch_R.png"));
        myImages.add(toolkit.getImage("SSMImages/Umer/V_Punch_L.png"));
        
        myImages.add(toolkit.getImage("SSMImages/Kaushal/Stick.png"));
        
        myImages.add(toolkit.getImage("SSMImages/Salome/Kick_F.png"));
        myImages.add(toolkit.getImage("SSMImages/Salome/Kick_B.png"));
        
        myImages.add(toolkit.getImage("SSMImages/Spock/laser.png"));
        myImages.add(toolkit.getImage("SSMImages/Spock/laser_B.png"));
        
        myImages.add(toolkit.getImage("SSMImages/Emi/Jumex_F.png"));
        myImages.add(toolkit.getImage("SSMImages/Emi/Jumex_B.png"));
        
        myImages.add(toolkit.getImage("SSMImages/Emi/Punch_F_E.png"));
        myImages.add(toolkit.getImage("SSMImages/Emi/Punch_B_E.png"));
        
        myImages.add(toolkit.getImage("SSMImages/Lawrence/Sword_F.png"));
        myImages.add(toolkit.getImage("SSMImages/Lawrence/Sword_B.png"));
    }
    
}

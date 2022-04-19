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
public class Lightning extends GameObject{
    
    private static ArrayList<Image> myImages;
    
    private double timer;
    private double drawTimer;
    private final String team;
    private final int shooter;
    private int dir;
    private boolean isNull;
    
    public Lightning(int x, int y, int direction, String team_,double draw, boolean isNull_, 
            double timer_, int shooter){
        super(x,y,50,30);
        
        drawTimer = draw;
        timer = timer_;
        team = team_;
        dir = direction;
        isNull = isNull_;
        this.shooter = shooter;
    }
    
    public int getDirection(){return dir;}
    public double getDrawTimer(){return drawTimer;}
    public double getTimer(){return timer;}
    public boolean isNull(){return isNull;}
    public String getTeam(){return team;}
    public int getShooter(){return shooter;}
    public Rectangle getHitBox(){
        if(dir==Projectile.RIGHT)
            return new Rectangle((int)getX()+60, (int)getY(), getW(),getH());
        else
            return new Rectangle((int)getX()-getW()+10, (int)getY(), getW(),getH());
    }
    
    public void setDirection(int c){dir = c;}
    
    public void animate(ArrayList<Player> targets){
        timer -= 1.0/60;
        if(timer <= 0)
            isNull = true;

        if(drawTimer < 0)
            drawTimer = 1;
        if(shooter == Player.ADAM)
            drawTimer -=0.1;
        else if(shooter == Player.LAWRENCE)
            drawTimer -=0.2;

        for(Player target : targets) {
            if (this.getHitBox().intersects(target.getHitBox())
                    && !team.equals(target.getTeam())
                    && !target.isUntargetable()
                    && !isNull) {
                target.setPercentage(target.getPercentage() + 0.35);
                target.setYVel(target.getYVel() - Actor.GRAVITY);
                if (drawTimer < 0.7) {
                    if (dir == Projectile.RIGHT) {
                        target.setX(getX() + getW() + 40);
                        target.setY(getY() - 40);
                    } else {
                        target.setX(getX() - getW() - 30);
                        target.setY(getY() - 30);
                    }
                }
            }
        }
    }
    
    public void draw(Graphics g, ImageObserver io){

        Image currentImage;
        if(dir==Projectile.RIGHT){
            if(shooter == Player.ADAM){
                if(drawTimer > 0.5)
                    currentImage = myImages.get(0);
                else
                    currentImage = myImages.get(1);
            } else {
                if(drawTimer > 0.5)
                    currentImage = myImages.get(4);
                else
                    currentImage = myImages.get(5);
            }
        }else{
            if(shooter == Player.ADAM){
                if(drawTimer > 0.5)
                    currentImage = myImages.get(2);
                else
                    currentImage = myImages.get(3);
            } else {
                if(drawTimer > 0.5)
                    currentImage = myImages.get(6);
                else
                    currentImage = myImages.get(7);
            }
        }
        
        if(!isNull){
            if(shooter == Player.ADAM){
                if(dir==Projectile.RIGHT)
                    g.drawImage(currentImage, (int)getX()+50, (int)getY()-5,getW(),getH(), io);
                else
                    g.drawImage(currentImage, (int)getX()-getW()+10, (int)getY()-5,getW(),getH(), io);
            } else if(shooter == Player.LAWRENCE){
                if(dir==Projectile.RIGHT)
                    g.drawImage(currentImage, (int)getX()+57, (int)getY()+17,getW()+15,getH()-10, io);
                else
                    g.drawImage(currentImage, (int)getX()-getW()-7, (int)getY()+17,getW()+15,getH()-10, io);
            }
        }
    }
    
    public static void initImages(){
        Poolkit toolkit = new Poolkit();
        
        myImages = new ArrayList<>();
        
        myImages.add(toolkit.getImage("SSMImages/Adam/Lightning_1.png"));
        myImages.add(toolkit.getImage("SSMImages/Adam/Lightning_2.png"));
        myImages.add(toolkit.getImage("SSMImages/Adam/Lightning_1_B.png"));
        myImages.add(toolkit.getImage("SSMImages/Adam/Lightning_2_B.png"));
        
        myImages.add(toolkit.getImage("SSMImages/Lawrence/Sword_F.png"));
        myImages.add(toolkit.getImage("SSMImages/Lawrence/Sword_Small_F.png"));
        myImages.add(toolkit.getImage("SSMImages/Lawrence/Sword_B.png"));
        myImages.add(toolkit.getImage("SSMImages/Lawrence/Sword_Small_B.png"));
    }

    //----------------------------------------
    //Packing and unpacking punches
    //----------------------------------------


    public static String pack(Lightning p){
        if(p==null)
            return "null";
        String str = "";
        str += (int)p.getX() + SSMClient.parseChar;
        str += (int)p.getY() + SSMClient.parseChar;
        str += p.dir + SSMClient.parseChar;
        str += p.team + SSMClient.parseChar;
        str += p.getDrawTimer() + SSMClient.parseChar;
        str += p.isNull + SSMClient.parseChar;
        str += p.getTimer() + SSMClient.parseChar;
        str += p.getShooter() + SSMClient.parseChar;
        return str;
    }
    public static Lightning unPack(String s){
        if(s.equals("null") || s.isEmpty())
            return null;
        String[] data = s.split(SSMClient.parseChar);
        return new Lightning(Integer.parseInt(data[0]), Integer.parseInt(data[1]), Integer.parseInt(data[2]),
                data[3], Double.parseDouble(data[4]), Boolean.parseBoolean(data[5]), Double.parseDouble(data[6]),
                Integer.parseInt(data[7]));
    }

}

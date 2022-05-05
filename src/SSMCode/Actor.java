/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SSMCode;

/**
 *
 * @author 22cloteauxm
 */
public class Actor extends GameObject{
    
    public static double GRAVITY = 0.5;    
    public final double FRICTION = 0.75;
    private final double maxVel = 5.5;
    
    private double xVel;
    private double yVel;
    private double percentage;
    
    private int ground;
    
    private boolean untargetable;
    
    public Actor(double x,double y, int w, int h) {
        super(x,y,w,h);
        xVel = 0;
        yVel = 0;
        percentage = 0;
        ground = 0;
        untargetable = false;
    }
    
    //accessors
    public double getXVel(){return xVel;}
    public double getYVel(){return yVel;}
    public double getPercentage(){return percentage;}
    public boolean isUntargetable(){return untargetable;}
    public int getGround(){return ground;}
    public boolean isOnGround(){return yVel==0 && getY()+getH() == ground;}
    
    //modifiers
    public void setXVel(double c){xVel = c;}
    public void setYVel(double c){yVel = c;}
    public void setPercentage(double c){percentage = c;}
    public void setGround(int c){ground = c;}
    public void setUntargetable(boolean c){untargetable = c;}
    
    public void increaseXVel(double c){xVel += c;}
    
    //methods
    public void basicPhysicsMotion(){        
        if(getY()+getH() > ground) {
            setY(ground-getH());
            yVel = 0;
            setXVel(getXVel()*FRICTION);
        }
        else
            yVel+=GRAVITY;
         
        if(getXVel()<=0.5 && getXVel()>=-0.5)
            setXVel(0);
    }
    
    public void animate(){
        setX(getX()+xVel);
        setY(getY()+yVel);
        
        basicPhysicsMotion();
    }
    
}

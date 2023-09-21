/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SSMCode;


import SSMEngines.AnimationPanel;

import java.awt.*;

public class GameObject {
    
    private double x;
    private double y;
    private int width;
    private int height;
    
    public GameObject(double x_,double y_,int w,int h) {
        x = x_;
        y = y_;
        width = w;
        height = h;        
    }    
    
    //accessors
    public double getX(){return x;}
    public double getY(){return y;}
    public int getW(){return width;}
    public int getH(){return height;}
    public Rectangle getHitBox(){return new Rectangle((int)x,(int)y,width,height);}
    
    //modifiers
    public void setX(double xx) { x=xx; }
    public void setY(double yy) { y=yy; }
    public void setSize(int w, int h) { width = w; height = h; } 
    
    //methods
    public boolean intersects(GameObject go) {
        return (this.getHitBox().intersects(go.getHitBox()));
    }
    
    public void draw(Graphics g){
        g.setColor(Color.BLUE);
        g.fillRect((int)x,(int)y, width, height);
    }
    
    public void animate(){
        
    }

    public boolean outOfBounds(){
        return x<-200 || x> AnimationPanel.width+200;
    }
}

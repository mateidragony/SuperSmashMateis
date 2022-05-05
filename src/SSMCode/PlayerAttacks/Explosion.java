package SSMCode.PlayerAttacks;

import SSMCode.GameObject;
import SSMCode.Player;
import SSMEngines.SSMClient;
import SSMEngines.util.Poolkit;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.*;
import java.util.List;

import static SSMCode.PlayerAttacks.Projectile.arrayParseChar;

public class Explosion extends GameObject {

    private static ArrayList<Image> myImages;

    private double timer;
    private final String team;
    private final int shooter;
    private boolean isNull;

    public Explosion(int x, int y, int w, int h, String team, double timer, boolean isNull, int shooter) {
        //x and y are centered on the explosion, not in the top right corner
        super(x - (w/4.0), y - (h/4.0) , w, h);

        this.team = team;
        this.isNull = isNull;
        this.shooter = shooter;
        this.timer = timer;
    }

    public boolean isNull(){return isNull;}

    public void animate(List<Player> targets) {
        if (timer > 0)
            timer -= 1.0 / 60;
        else
            isNull = true;

        for (Player target : targets) {
            if (this.getHitBox().intersects(target.getHitBox())
                    && !team.equals(target.getTeam())
                    && !target.isUntargetable()
                    && !isNull) {

                target.setPercentage(target.getPercentage() + 1.5);
                target.setYVel(target.getYVel() - .5* (1.5 + target.getPercentage() / 100));
                //Damage Component depends on which side of explosion you're on
                if(target.getX() < getX())
                    target.setDamageXVel((2.5 + 1 * target.getPercentage() / 25) * -1.25);
                else
                    target.setDamageXVel((2.5 + 1 * target.getPercentage() / 25) * 1.25);
            }
        }
    }

    public void draw(Graphics g, ImageObserver io) {
        g.setColor(Color.red);
        //g.fillRect((int)getX(),(int)getY(),getW(),getH());
        int w = (int) (getW() * (.7-timer));
        int h = (int) (getH() * (.7-timer));
        g.drawImage(myImages.get(0), (int) getX() + getW()/2 - w/2, (int) getY() + getH()/2 - h/2, w, h, io);

    }


    public static String pack(Explosion p){
        if(p == null)
            return "null";
        else{
            String packedProj = "";
            packedProj += (int)p.getX() + SSMClient.parseChar;
            packedProj += (int)p.getY() + SSMClient.parseChar;
            packedProj += p.getW() + SSMClient.parseChar;
            packedProj += p.getH() + SSMClient.parseChar;
            packedProj += p.team + SSMClient.parseChar;
            packedProj += p.timer + SSMClient.parseChar;
            packedProj += p.isNull + SSMClient.parseChar;
            packedProj += p.shooter + SSMClient.parseChar;

            return packedProj;
        }
    }
    public static Explosion unPack(String s){
        if(s.equals("null") || s.isEmpty())
            return null;
        String[] myInfo = s.split(SSMClient.parseChar);
        return new Explosion((int)Double.parseDouble(myInfo[0]),(int)Double.parseDouble(myInfo[1]),
                Integer.parseInt(myInfo[2]),Integer.parseInt(myInfo[3]),myInfo[4],
                Double.parseDouble(myInfo[5]),Boolean.parseBoolean(myInfo[6]),Integer.parseInt(myInfo[7]));
    }
    public static String packArray(ArrayList<Explosion> pList){
        String packedPList = "";
        if(pList.isEmpty())
            return "null";
        for(int i=pList.size()-1;i>=0;i--){
            Explosion p = pList.get(i);
            packedPList = packedPList.concat(pack(p)+arrayParseChar);
        }
        return packedPList;
    }
    public static ArrayList<Explosion> unPackArray(String packedPList){
        ArrayList<Explosion> pList = new ArrayList<>();
        if(packedPList.equals("null"))
            return pList;
        for(String s: packedPList.split(arrayParseChar)){
            pList.add(unPack(s));
        }
        return pList;
    }


    @Override
    public String toString(){
        return "X: "+(int)getX()+", Y: "+(int)getY();
    }

    public static void initImages(Poolkit toolkit){
        myImages = new ArrayList<>();
        myImages.add(toolkit.getImage("SSMImages/Bryce/gooExplosion.png"));
    }
}

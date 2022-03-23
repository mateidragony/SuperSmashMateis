package SSMEngines.util;

import SSMEngines.SSMClient;

import java.awt.*;

public class Drawer {

    private final int width,height;

    public Drawer(int w, int h){
        width = w;
        height = h;
    }


    int x,y;

    public void draw(Graphics g){
        g.setColor(Color.white);
        g.fillRect(0,0,width,height);

        g.setColor(Color.RED);
        g.fillRect(x,y,30,30);
    }

    public void unpack(String str){
        String[] data = str.split(SSMClient.parseChar);
        x=Integer.parseInt(data[0]);
        y=Integer.parseInt(data[1]);
    }

}

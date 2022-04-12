package SSMEngines.util;

import SSMCode.Player;
import SSMEngines.AnimationPanel;
import SSMEngines.SSMClient;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;;

public class Drawer {

    private final int width = AnimationPanel.width, height = AnimationPanel.height;
    private final int playerID;
    private final int playerMode;

    private List<Player> players;

    private int serverScreenNumber;
    private Point mouse;

    //Map select variables
    private final MapHandler myMapHandler;
    private int mapNumber;

    //Images
    private Image inGameBG;

    public Drawer(int playerID, int playerMode){
        this.playerID = playerID;
        this.playerMode = playerMode;
        myMapHandler = new MapHandler();
        myMapHandler.initImages(new Poolkit());

        players = IntStream.range(0,4).mapToObj(Player::new).collect(Collectors.toList());
    }

    public void draw(Graphics g, ImageObserver io, Point mouse){
        this.mouse = mouse;

        if (serverScreenNumber == Animator.CHARACTER_SELECT_SCREEN)
            drawCharacterSelect(g, io);
        else if(serverScreenNumber == Animator.MAP_SELECT)
            drawMapSelect(g,io);
        else if(serverScreenNumber == Animator.IN_GAME_SCREEN)
            drawInGame(g,io);
    }

    //for drawing imgs in character select
    int imageSize = 90; double imgHeightRatio = 1.5/1.3;
    int imgsPerRow = 9; int imgOffset = 10;
    int xOffset = 100; int yOffset = 100;

    public void drawCharacterSelect(Graphics g, ImageObserver io){
        //draw bg color
        g.setColor(Color.black);
        g.fillRect(0,0,width,height);

        //draw a red square under the image if the players mouse is on it
        drawMouseMovementsCharacterSelect(g);

        //draw the player images in character select
        for(int i = 0; i< Player.getImages().size()-1; i+=1){
            int imageX = xOffset+(i%imgsPerRow)*(imageSize+imgOffset);
            int imageY = yOffset+((int)(imageSize*imgHeightRatio)+imgOffset)*(i/imgsPerRow);
            int imageHeight = (int)(imageSize*imgHeightRatio);

            g.drawImage(Player.getImages().get(i),imageX,imageY,imageSize,imageHeight,io);
        }

        //draws each of the other players' characters
        //-16 and -39 on width and height are the "drawn" widths and heights
        //Complicated math to always draw the character boxes centered no matter how many players there are
        int xCenter = (width-16)/2-75;
        double xMultiplier = -(playerMode-1)/2.0;
        int separation = 90*(5-playerMode);

        //Player 1 box
        g.setColor(Color.red);
        int x = (int)(xCenter+(xMultiplier*(150+separation)));
        g.fillRect(x,(height-39)-120,150,120);
        g.drawImage(Player.getImages().get(players.get(0).getCharacter()),
                x+15,(height-39)-120+10,120,100,io);
        //Player 2 box
        if(playerMode >= 2) {
            g.setColor(Color.blue);
            x = (int)(xCenter+((xMultiplier+1)*(150+separation)));
            g.fillRect(x,(height-39)-120,150,120);
            g.drawImage(Player.getImages().get(players.get(1).getCharacter()),
                    x+15,(height-39)-120+10,120,100,io);
        }
        //Player 3 box
        if(playerMode >= 3) {
            g.setColor(Color.green);
            x = (int)(xCenter+((xMultiplier+2)*(150+separation)));
            g.fillRect(x,(height-39)-120,150,120);
            g.drawImage(Player.getImages().get(players.get(2).getCharacter()),
                    x+15,(height-39)-120+10,120,100,io);
        }
        //Player 4 box
        if(playerMode >= 4) {
            g.setColor(Color.yellow);
            x = (int)(xCenter+((xMultiplier+3)*(150+separation)));
            g.fillRect(x,(height-39)-120,150,120);
            g.drawImage(Player.getImages().get(players.get(3).getCharacter()),
                    x+15,(height-39)-120+10,120,100,io);
        }
    }
    public void drawMapSelect(Graphics g, ImageObserver io){
        //draw bg color
        g.setColor(Color.black);
        g.fillRect(0,0,width,height);

        myMapHandler.drawMouseEvents(mouse.x, mouse.y, g,io);
        myMapHandler.drawMapScreen(g,io);
        inGameBG = myMapHandler.getMapBGs().get(mapNumber);
    }
    public void drawInGame(Graphics g, ImageObserver io){
        inGameBG = myMapHandler.getMapBGs().get(mapNumber);
        g.drawImage(inGameBG,0,0,width,height,io);
    }




    public void drawMouseMovementsCharacterSelect(Graphics gg){
        Graphics2D g = (Graphics2D)gg;
        g.setColor(Color.red);

        ArrayList<Rectangle> imageRects = new ArrayList<>();

        for(int i = 0; i< Player.getImages().size()-1; i+=1) {
            int imageX = xOffset - 2 +(i % imgsPerRow) * (imageSize + imgOffset);
            int imageY = yOffset + ((int)(imageSize * imgHeightRatio) + imgOffset) * (i / imgsPerRow);
            int imageHeight = (int)(imageSize * imgHeightRatio);

            imageRects.add(new Rectangle(imageX-3,imageY-5,imageSize+10,imageHeight+10));
        }

        for(Rectangle r: imageRects){
            if(r.contains(mouse))
                g.fill(r);
        }

    }

    public void unpack(String str){
        String[] data = str.split(Animator.parseChar);

        String[] gameData = data[0].split(SSMClient.parseChar);
        serverScreenNumber = Integer.parseInt(gameData[0]);
        mapNumber = Integer.parseInt(gameData[1]);

        for(int i=1;i<data.length-1;i++){
            if(data[i].equals("null"))
                players.set(i-1,null);
            else
                players.set(i-1,Player.unPack(data[i]));
        }

    }

}

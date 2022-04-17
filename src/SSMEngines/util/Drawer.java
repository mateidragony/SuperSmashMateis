package SSMEngines.util;

import SSMCode.Player;
import SSMEngines.AnimationPanel;
import SSMEngines.SSMClient;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Drawer {

    private final int width = AnimationPanel.width, height = AnimationPanel.height;
    private final int playerID;
    private final int playerMode;

    private final List<Player> players;

    private int serverScreenNumber;
    private Point mouse;

    //Character select variables
    List<Boolean> characterSelected;

    //Map select variables
    private ArrayList<Point> mice;
    private static ArrayList<Image> miceImgs;
    private final MapHandler myMapHandler;
    private int mapNumber;

    //Images
    private Image inGameBG;
    private static Image characterSelectReadyButton;
    private static Image characterSelectReady;

    public Drawer(int playerID, int playerMode){
        this.playerID = playerID;
        this.playerMode = playerMode;
        myMapHandler = new MapHandler();
        myMapHandler.initImages(new Poolkit());

        players = IntStream.range(0,4).mapToObj(Player::new).collect(Collectors.toList());
        characterSelected = Stream.generate(() -> Boolean.FALSE).limit(4).collect(Collectors.toList());
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
    int xOffset = 100; int yOffset = 50;


    public void drawCharacterSelect(Graphics g, ImageObserver io){
        //draw bg color
        g.setColor(Color.black);
        g.fillRect(0,0,width,height);

        //draw bg behind the character images
        g.setColor(new Color(132, 127, 127));
        g.fillRect(xOffset-15,yOffset-15,900+15,246);

        //draw a red square under the image if the players mouse is on it
        if(!characterSelected.get(playerID))
            drawMouseMovementsCharacterSelect(g);

        //draw the player images in character select
        for(int i = 0; i< Player.getImages().size()-1; i+=1){
            int imageX = xOffset+(i%imgsPerRow)*(imageSize+imgOffset);
            int imageY = yOffset+((int)(imageSize*imgHeightRatio)+imgOffset)*(i/imgsPerRow);
            int imageHeight = (int)(imageSize*imgHeightRatio);


            g.setColor(Color.black);
            g.fillRect(imageX-2,imageY-2,imageSize+4,imageHeight+4);
            g.drawImage(Player.getImages().get(i),imageX,imageY,imageSize,imageHeight,io);
        }

        if(characterSelected.get(playerID)){
            g.setColor(new Color(201, 8, 8, 77));
            g.fillRect(0,0,width,height);
        }

        //Player 1 box
        drawCharacterSelectBox(0,g,io);
        //Player 2 box
        if(playerMode >= 2)
            drawCharacterSelectBox(1,g,io);
        //Player 3 box
        if(playerMode >= 3)
            drawCharacterSelectBox(2,g,io);
        //Player 4 box
        if(playerMode >= 4)
            drawCharacterSelectBox(3,g,io);

        //draw ready button
        g.drawImage(characterSelectReadyButton,(width-16)/2-205,415,410,50,io);

    }
    public void drawMapSelect(Graphics g, ImageObserver io){
        //draw bg color
        g.setColor(Color.black);
        g.fillRect(0,0,width,height);

        myMapHandler.drawMouseEvents(mouse.x, mouse.y, g,io);
        myMapHandler.drawMapScreen(g,io);
        inGameBG = myMapHandler.getMapBGs().get(mapNumber);

        //draw enemy mice and not your mouse
        for(int i=0; i<mice.size(); i++){
            if(i!=playerID)
                g.drawImage(miceImgs.get(i), mice.get(i).x, mice.get(i).y,30,25, io);
        }
    }
    public void drawInGame(Graphics g, ImageObserver io){
        inGameBG = myMapHandler.getMapBGs().get(mapNumber);
        g.drawImage(inGameBG,0,0,width,height,io);
    }




    public void drawMouseMovementsCharacterSelect(Graphics gg){
        Graphics2D g = (Graphics2D)gg;
        g.setColor(new Color(0x9B0D0D));

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

        Rectangle ready = new Rectangle((width-16)/2-205 - 5,415 - 5,410 + 10,50 + 10);
        g.setColor(new Color(0x9B0D0D));
        if(ready.contains(mouse))
            g.fill(ready);
    }
    public void drawCharacterSelectBox(int playerNumber, Graphics g, ImageObserver io){
        //draws each of the other players' characters
        //-16 and -39 on width and height are the "drawn" widths and heights
        //Complicated math to always draw the character boxes centered no matter how many players there are
        double xMultiplier = -(playerMode-1)/2.0;
        int separation = 20+75*(4-playerMode);
        int boxWidth = 250; int boxHeight = 120;
        int xCenter = (width-16)/2-boxWidth/2;
        Font font = new Font(Font.MONOSPACED,Font.BOLD,28);

        g.setColor(players.get(playerNumber).getColor());
        int x = (int)(xCenter+((xMultiplier+playerNumber)*(boxWidth+separation)));

        g.fillRect(x,(height-39)-boxHeight,boxWidth,boxHeight);
        g.drawImage(Player.getImages().get(players.get(playerNumber).getCharacter()),
                x+10,(height-39)-boxHeight+10,93,100,io);

        g.setFont(font);

        String name = Player.getCharacterNames().get(players.get(playerNumber).getCharacter());
        if(name.length() > 8)
            g.setFont(new Font(Font.MONOSPACED,Font.BOLD, (int)( (136.0/name.length()) / .6) )); //Font size = (pixels per letter) / 0.6
        g.setColor(Color.lightGray);
        g.drawString(name,x+10+93+5,(height-39)-boxHeight+30); //Draw character name

        g.setColor(Color.gray);
        g.drawString("Player "+(playerNumber+1), x+10+93+5,(height-39)-boxHeight+60); //Draw Player number

        name = players.get(playerNumber).getPlayerName();
        if(name.length() > 8)
            g.setFont(new Font(Font.MONOSPACED,Font.BOLD, (int)( (136.0/name.length()) / .6) ));
        g.setColor(Color.darkGray);
        g.drawString(name, x+10+93+5,(height-39)-boxHeight+85); //Draw Player name

        if(characterSelected.get(playerNumber))
            g.drawImage(characterSelectReady,x+10+93+5,(height-39)-boxHeight+95,80,20,io);
    }



    public void unpack(String str){
        String[] data = str.split(Animator.parseChar);

        String[] gameData = data[0].split(SSMClient.parseChar);
        serverScreenNumber = Integer.parseInt(gameData[0]);
        mapNumber = Integer.parseInt(gameData[1]);
        characterSelected.set(0, Boolean.parseBoolean(gameData[2]));
        characterSelected.set(1, Boolean.parseBoolean(gameData[3]));
        characterSelected.set(2, Boolean.parseBoolean(gameData[4]));
        characterSelected.set(3, Boolean.parseBoolean(gameData[5]));
        mice = Animator.unPackMice(gameData[6]);

        for(int i=1;i<data.length;i++){
            if(data[i].equals("null"))
                players.set(i-1,null);
            else
                players.set(i-1,Player.unPack(data[i]));
        }

    }

    public static void initImages(Poolkit toolkit){
        miceImgs = new ArrayList<>();

        characterSelectReadyButton = toolkit.getImage("SSMImages/readyButton.png");
        characterSelectReady = toolkit.getImage("SSMImages/ready.png");

        for(int i=1; i<=4; i++){
            miceImgs.add(toolkit.getImage("SSMImages/mouse"+i+".png"));
        }
    }


}

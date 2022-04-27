package SSMEngines.util;

import SSMCode.Platform;
import SSMCode.Player;
import SSMEngines.AnimationPanel;
import SSMEngines.SSMClient;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.text.DecimalFormat;
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
    private ArrayList<Platform> platList;
    private Player dummy;
    private double startGameTimer, gameTimer;
    private double jCooldown, kCooldown;
    private boolean bossMode;

    private int serverScreenNumber;
    private Point mouse;

    private boolean enteredGameScreen = true;

    //Character select variables
    List<Boolean> characterSelected;

    //Map select variables
    private ArrayList<Point> mice;
    private static ArrayList<Image> miceImgs;
    private final MapHandler myMapHandler;
    private int mapNumber;

    private final Font myFont = new Font("Sans Serif", Font.BOLD, 60);
    private final DecimalFormat df = new DecimalFormat("####.#");

    public Drawer(int playerID, int playerMode){
        this.playerID = playerID;
        this.playerMode = playerMode;
        myMapHandler = new MapHandler();
        myMapHandler.initImages(new Poolkit());

        players = IntStream.range(0,playerMode).mapToObj(Player::new).collect(Collectors.toList());
        characterSelected = Stream.generate(() -> Boolean.FALSE).limit(playerMode).collect(Collectors.toList());
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
        for(int i=0; i<playerMode; i++){
            if(i!=playerID)
                g.drawImage(miceImgs.get(i), mice.get(i).x, mice.get(i).y,30,25, io);
        }
    }
    public void drawInGame(Graphics g, ImageObserver io){
        if(enteredGameScreen) {
            myMapHandler.setMapNumber(mapNumber);
            Platform.setBigImg(myMapHandler.getMapPlats().get(myMapHandler.getMapNumber() * 2));
            Platform.setSmallImg(myMapHandler.getMapPlats().get(myMapHandler.getMapNumber() * 2 + 1));
            platList = myMapHandler.initPlats();
            inGameBG = myMapHandler.getMapBGs().get(mapNumber);

            enteredGameScreen = false;
        }

        //draw background
        g.drawImage(inGameBG,0,0,width,height,io);
        //draw Platforms
        for(Platform p : platList)
            p.draw(g,io);
        //draw the dummy if it is not null
        if(dummy != null){
            dummy.draw(g,io);
            g.setFont(new Font("Sans Serif", Font.BOLD, 60));
            g.setColor(Color.white);
            g.drawString("Percentage: "+df.format(dummy.getPercentage()),250,100);
        }
        //draw players
        for(Player p : players) {
            p.draw(g, io);
            p.drawAttacks(g,io);
        }
        //draw the fire if boss mode
        if(bossMode) {
            g.drawImage(fireFG,0,0,width,height,io);
        }
        //draw the player's character, name, lives, and percentage
        drawBottomInGameScreen(g,io);
        //draws the cooldowns for each of your abilities
        drawAbilityBar(g);
        //draw the 3,2,1,go
        drawStartingGame(g,io);
        //draw the game timer
        g.setColor(Color.gray);
        g.fillRect(875,10,125,40);
        g.setColor(Color.black);
        g.setFont(new Font("Sans Serif",Font.BOLD,30));
        if(gameTimer %60 < 10)
            g.drawString(""+(int)(gameTimer/60)+" : 0"+(int)(gameTimer%60), 900,40);
        else
            g.drawString(""+(int)(gameTimer/60)+" : "+(int)(gameTimer%60), 900,40);
    }


    public void drawStartingGame(Graphics g, ImageObserver io){
        if(startGameTimer > 0)
            startGameTimer -= 1.0/80;
        else
            startGameTimer = 0;

        if((Math.ceil(startGameTimer)) == 4)
            g.drawImage(img3, 530-167,155,167*2,124*2, io);
        else if((Math.ceil(startGameTimer)) == 3)
            g.drawImage(img2, 530-157,150,157*2,126*2, io);
        else if(Math.ceil(startGameTimer) == 2)
            g.drawImage(img1, 530-193,140,189*2,138*2, io);
        else if(Math.ceil(startGameTimer) == 1)
            g.drawImage(imgGo, 540-556,60,(int)(855*1.3),(int)(357*1.3), io);
    }
    public void drawBottomInGameScreen(Graphics g, ImageObserver io){
        //Player 1 box
        drawInGameCharacterBox(0,g,io);
        //Player 2 box
        if(playerMode >= 2)
            drawInGameCharacterBox(1,g,io);
        //Player 3 box
        if(playerMode >= 3)
            drawInGameCharacterBox(2,g,io);
        //Player 4 box
        if(playerMode >= 4)
            drawInGameCharacterBox(3,g,io);
    }
    public void drawInGameCharacterBox(int playerNumber, Graphics g, ImageObserver io){
        //draws each of the other players' characters
        //-16 and -39 on width and height are the "drawn" widths and heights
        //Complicated math to always draw the character boxes centered no matter how many players there are
        double xMultiplier = -(playerMode-1)/2.0;
        int separation = 20+75*(4-playerMode);
        int boxWidth = 200; int boxHeight = 100;
        int xCenter = (width-16)/2-boxWidth/2;
        int yOffset = 0;
        Font font = new Font(Font.MONOSPACED,Font.BOLD,18);

        g.setColor(Color.black);
        int x = (int)(xCenter+((xMultiplier+playerNumber)*(boxWidth+separation)));

        g.fillRect(x,(height-39)-boxHeight - yOffset,boxWidth,boxHeight);
        g.drawImage(Player.getImages().get(players.get(playerNumber).getCharacter()),
                x+10,(height-39)-boxHeight+10 - yOffset,76,80,io);

        g.setFont(font);

        String name = Player.getCharacterNames().get(players.get(playerNumber).getCharacter());
        if(name.length() > 8)
            g.setFont(new Font(Font.MONOSPACED,Font.BOLD, (int)( (86.0/name.length()) / .6) )); //Font size = (pixels per letter) / 0.6
        g.setColor(Color.lightGray);
        g.drawString(name,x+10+76+5,(height-39)-boxHeight+85 - yOffset); //Draw character name

        g.setColor(Color.lightGray);
        g.drawString(df.format(players.get(playerNumber).getPercentage())+"%", x+10+76+5,(height-39)-boxHeight+60 - yOffset); //Draw Player number

        name = players.get(playerNumber).getPlayerName();
        if(name.length() > 8)
            g.setFont(new Font(Font.MONOSPACED,Font.BOLD, (int)( (86.0/name.length()) / .6) ));
        g.setColor(Color.lightGray);
        g.drawString(name, x+10+76+5,(height-39)-boxHeight+30 - yOffset); //Draw Player name

        int livesY = (height - 39) - boxHeight + 95 - yOffset;
        if(players.get(playerNumber).getLives() <= 4) {
            for (int i = 0; i < players.get(playerNumber).getLives(); i++)
                g.drawImage(life, x + 10 + 76 + 5 + 15 * i, livesY, io);
        } else {
            g.drawImage(life, x + 10 + 76 + 5, livesY, io);
            g.drawString("x"+players.get(playerNumber).getLives(),x+10+76+5+30, livesY);
        }
    }
    public void drawAbilityBar(Graphics g){

        int x = 10; int y = 100;

        g.setColor(Color.black);
        g.fillRect(x, y, 50, 180);

        g.setColor(Color.white);
        g.fillOval(x+10,y+20,30,30);
        g.fillOval(x+10,y+75,30,30);
        g.fillOval(x+10,y+130,30,30);
        g.setColor(Color.black);
        g.setFont(new Font("Sans Serif", Font.BOLD, 20));
        g.drawString("J", x+20,y+45);
        g.drawString("K", x+20,y+100);
        g.drawString("L", x+20,y+155);

        if(jCooldown > 0){
            g.setColor(new Color(100,100,100,220)); g.fillOval(x+10,y+20,30,30);
            g.setColor(Color.red); g.setFont(new Font("Sans Serif", Font.BOLD, 22));
            g.drawString(String.valueOf(df.format(jCooldown)),x+12,y+45);
        }
        if(kCooldown > 0){
            g.setColor(new Color(100,100,100,220)); g.fillOval(x+10,y+75,30,30);
            g.setColor(Color.red); g.setFont(new Font("Sans Serif", Font.BOLD, 22));
            g.drawString(String.valueOf(df.format(kCooldown)),x+12,y+100);
        }
        if(players.get(playerID).getLCooldown() > 0){
            g.setColor(new Color(100,100,100,220)); g.fillOval(x+10,y+130,30,30);
            g.setColor(Color.red); g.setFont(new Font("Sans Serif", Font.BOLD, 22));
            g.drawString(String.valueOf(df.format(players.get(playerID).getLCooldown())),x+12,y+155);
        }
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
        mice = Animator.unPackMice(gameData[2]);
        characterSelected = Animator.unPackCharacterSelect(gameData[3]);
        startGameTimer = Double.parseDouble(gameData[4]);
        jCooldown = Animator.unPackAttackCooldowns(gameData[5]).get(playerID);
        kCooldown = Animator.unPackAttackCooldowns(gameData[6]).get(playerID);
        gameTimer = Double.parseDouble(gameData[7]);
        bossMode = Boolean.parseBoolean(gameData[8]);

        for(int i=1;i<data.length-1;i++){
            if(data[i].equals("null"))
                players.set(i-1,null);
            else
                players.set(i-1,Player.unPack(data[i]));
        }

        if(data[data.length-1].equals("null"))
            dummy = null;
        else
            dummy = Player.unPack(data[data.length-1]);

    }


    //Images
    private Image inGameBG;
    private static Image img3, img2, img1, imgGo;
    private static Image characterSelectReadyButton;
    private static Image characterSelectReady;
    private static Image life, fireFG;

    public static void initImages(Poolkit toolkit){
        miceImgs = new ArrayList<>();

        characterSelectReadyButton = toolkit.getImage("SSMImages/readyButton.png");
        characterSelectReady = toolkit.getImage("SSMImages/ready.png");

        miceImgs.add(toolkit.getImage("SSMImages/mouse1.png"));
        miceImgs.add(toolkit.getImage("SSMImages/mouse2.png"));
        miceImgs.add(toolkit.getImage("SSMImages/mouse3.png"));
        miceImgs.add(toolkit.getImage("SSMImages/mouse4.png"));

        img3 = toolkit.getImage("SSMImages/3-2-1/3.png");
        img2 = toolkit.getImage("SSMImages/3-2-1/2.png");
        img1 = toolkit.getImage("SSMImages/3-2-1/1.png");
        imgGo = toolkit.getImage("SSMImages/3-2-1/Go.png");

        life = toolkit.getImage("SSMImages/heart.png");
        fireFG = toolkit.getImage("SSMImages/fire_fg.png");
    }


}

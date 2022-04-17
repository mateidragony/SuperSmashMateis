/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SSMEngines.old;

import SSMCode.PlayerAttacks.*;
import SSMEngines.AnimationPanel;
import SSMEngines.util.AudioUtility;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import javax.sound.sampled.Clip;
import SSMCode.*;
import SSMEngines.SSMLauncher;
import SSMEngines.util.MapHandler;

import java.net.*;
import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.*;

/**
 *
 * @author 22cloteauxm
 */
public class GameEngine extends AnimationPanel {

    //------------------------------------------------------------
    //Constants
    //------------------------------------------------------------    
    public static final int INTRO_SCREEN = 0;
    public static final int LOADING_SCREEN = 1;
    public static final int CHARACTER_SELECT_SCREEN = 2;
    public static final int MAP_SELECT = 3;
    public static final int IN_GAME_SCREEN = 4;
    public static final int END_GAME_SCREEN = 5;
    public static final int DC_SCREEN = 6;
    
    private final Font myFont = new Font("Sans Serif", Font.BOLD, 60);
    private final DecimalFormat df = new DecimalFormat("####.#");
    
    //------------------------------------------------------------
    //Instance Bariables
    //------------------------------------------------------------
    private final int width = 1100;
    private final int height = 650;
    
    private int screenNumber;
    
    private final MapHandler myMapHandler;
    
    private PlayerOld me, enemy;
    private final ArrayList<PlayerOld> playerList;
    private ArrayList<Platform> platList;
    private ArrayList<Projectile> myPList;
    private ArrayList<Projectile> enemyPList;
    private boolean left, right, bossUp, bossDown;
    private int timesJumped;
    
    private double respawnTimer;
    private double jAttackTimer, kAttackTimer, walkingTimer;
    private double jAnimationTimer, kAnimationTimer, lAnimationTimer;
    private double startGameTimer, gameTimer, fpsTimer, actualFPS;
    
    private boolean startedIntro, startedCharacterSelect, startedInGame, startedBossTheme;
    
    private int enemyMouseX, enemyMouseY;
    
    private String connecting;
    private String myName;
    private String ipAddress;
    private int playerMode;
    private String bossCode = "Zbogck";

    private int playerID;
    private ReadFromServer rfsRunnable;
    private WriteToServer wtsRunnable;
    
    private Clock clock;
    private Clock pingClock;
    private double ping, drawnPing;
    
    private boolean readyToPlay;
    private boolean meReadyInGame, enemyReadyInGame;
    private boolean mePlayAgain, enemyPlayAgain;
    private boolean enemyDC;
    private boolean choseMap, enemyChoseMap;
    private double myDeathX,myDeathY,myDeathTimer;
    private double enemyDeathX,enemyDeathY,enemyDeathTimer;

    private boolean chargingL, releaseL;
    
    private final PlayerOld dummy;
    
    private String cheatCode;
       
    //------------------------------------------------------------
    //Constructor
    //------------------------------------------------------------
    public GameEngine() {
        super("Super Smash Mateis");
        connecting = "to Connect ";
        
        myPList = new ArrayList<>();
        enemyPList = new ArrayList<>();
        playerList = new ArrayList<>();
        
        me = new PlayerOld(140,300,40,60,"blue");
        enemy = new PlayerOld(900,300,40,60,"red");
        
        playerList.add(me); playerList.add(enemy); 
                
        myMapHandler = new MapHandler();

        setUpLauncher();
        setUpClock();

        if(playerMode > 1)
            connectToServer();

        initPlayers();
                
        if(playerID == 0){
            enemyPlayAgain = true;
            dummy = new PlayerOld(500,100,60,90,"blue");
            dummy.setCharacter(PlayerOld.DUMMY);
            playerList.add(dummy);

            enemy = new PlayerOld(500,100,60,90,"blue");
            enemy.setCharacter(PlayerOld.DUMMY);

            playerList.remove(enemy);
            startedBossTheme = false;

        } else {
            dummy = null;
        }
        
        startGameTimer = 5;
        walkingTimer = 0.7;        
        cheatCode = "";
    }
    
    public int getPlayerID(){return playerID;}
    
    
    //------------------------------------------------------------
    //Rendering the frames
    //------------------------------------------------------------    
    protected void renderFrame(Graphics g){                
        if(enemyDC)
            screenNumber = DC_SCREEN;
        
        fpsTimer++;
        
        if(clock != null && clock.getTime() >= 1){
            actualFPS = clock.getFPS();
            drawnPing = ping;
        }
        
        if(screenNumber == INTRO_SCREEN)
            renderIntroFrame(g);
        else if(screenNumber == LOADING_SCREEN)
            renderLoadingScreen(g);
        else if(screenNumber == CHARACTER_SELECT_SCREEN)
            renderCharacterSelectScreen(g);
        else if(screenNumber == MAP_SELECT)
            renderMapSelectScreen(g);
        else if(screenNumber == IN_GAME_SCREEN)
            renderInGameScreen(g);
        else if(screenNumber == END_GAME_SCREEN)
            renderEndGameScreen(g);
        else if(screenNumber == DC_SCREEN)
            renderDCScreen(g);
        g.setColor(Color.white);
        g.fillRect(10, 5, 60, 26);
        g.setColor(Color.black);
        g.setFont(new Font("Dialog",Font.PLAIN,10));
        g.drawString("FPS " + df.format(actualFPS), 10, 15);
        g.drawString("Ping " + df.format(drawnPing)+" ms", 10, 28);
    }
    
    public void renderIntroFrame(Graphics g){
        if(!startedIntro){
            if(IntroMusic!=null)
                IntroMusic.start();
            startedIntro = true;
        }
        
        g.drawImage(introScreen, 0,0,width,height,this);
    }
    public void renderLoadingScreen(Graphics g){
        g.setColor(Color.black);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.white);
        g.setFont(myFont);
        
        g.drawImage(Waiting_Meme,750,250,300,300,this);
        
        if(!readyToPlay){
            g.drawString("Waiting for Player 2", 150, 150);

            if(frameNumber%60==0)
                connecting+=".";
            if(frameNumber%240==0)
                connecting = "to Connect ";

            g.drawString(connecting, 200, 250);
        }
        else
        {
            if(playerID == 1)
                g.drawString("Player 2 has Connected", 150, 150);
            else
                g.drawString("Player 1 has Connected", 150, 150);
            g.drawString("Press S to Start", 150, 250);
        }
    }
    public void renderCharacterSelectScreen(Graphics g){
        
        myMapHandler.setMapNumber(-1);

        //music
        if(!startedCharacterSelect){
            if(characterMusic != null){
                IntroMusic.stop();
                inGameMusic.stop();
                characterMusic.start();
            }
            startedCharacterSelect = true;
        }
        
        if(enemyReadyInGame && meReadyInGame)
            screenNumber++;
               
        handleDrawingMouseMovementsInCharacterSelect(g);
        drawReadyCheckCharacterSelect(g);
        
        //draws the images of the characters
        int imageSize = 92;
        for(int i = 0; i< PlayerOld.getImages().size()-1; i+=1){
            g.drawImage(PlayerOld.getImages().get(i),10+(i%5)*(imageSize+10),100+((int)(imageSize*1.5/2)+10)*(i/5)
                    ,imageSize,(int)(imageSize*1.5/2),this);
            
            g.drawImage(PlayerOld.getImages().get(i),10+(i%5)*(imageSize+10)+550,100+((int)(imageSize*1.5/2)+10)*(i/5)
                    ,imageSize,(int)(imageSize*1.5/2),this);
        }
        
        if(playerID == 1 || playerID == 0){
            if(meReadyInGame){
                g.setColor(new Color(255,0,0,50));
                g.fillRect(0,0,width/2,height);
            }
            if(enemyReadyInGame){
                g.setColor(new Color(255,0,0,50));
                g.fillRect(width/2,0,width/2,height);
            }
        } else {
            if(meReadyInGame){
                g.setColor(new Color(255,0,0,50));
                g.fillRect(width/2,0,width/2,height);
            }
            if(enemyReadyInGame){
                g.setColor(new Color(255,0,0,50));
                g.fillRect(0,0,width/2,height);
            }
        }
        
        //Draws the line between player 1 characters and player 2 characters
        g.setColor(Color.black);
        g.fillRect(530,0,20,height);
        
        g.setFont(new Font("Sans Serif", Font.BOLD, 45));
        if(playerID == 1 || playerID == 0){
            g.drawString("You", 75, 50);
            g.drawString("Player 2", 290+520+50, 50);
            g.setColor(new Color(100,100,155,50));
            g.fillRect(width/2,0,width/2,height);
        } else {
            g.drawString("Player 1", 50, 50);
            g.drawString("You", 290+520+75, 50);
            g.setColor(new Color(100,100,100,50));
            g.fillRect(0,0,width/2,height);
        }
        
        g.setColor(Color.black);
        g.fillRect(290,0,520,60);
        g.setFont(myFont);
        g.setColor(Color.white);
        g.drawString("Character Select", 310, 50);
        
        g.drawImage(enemyMouse,enemyMouseX,enemyMouseY,22,20,this);
    }
    public void renderMapSelectScreen(Graphics g){
        myMapHandler.drawMouseEvents(mouseX, mouseY, g,this);
        myMapHandler.drawMapScreen(g,this);
        
        g.drawImage(enemyMouse,enemyMouseX,enemyMouseY,22,20,this);
        
        
        if(choseMap || enemyChoseMap){            
            inGameBG = myMapHandler.getMapBGs().get(myMapHandler.getMapNumber());
            Platform.setBigImg(myMapHandler.getMapPlats().get(myMapHandler.getMapNumber()*2));
            Platform.setSmallImg(myMapHandler.getMapPlats().get(myMapHandler.getMapNumber()*2+1));
            platList = myMapHandler.initPlats();
            screenNumber++;
        }
    }
    public void renderInGameScreen(Graphics g){        
        gameTimer += 1.0/60;
        
        handleCheatCodes();
        
        if(!startedInGame && !enemy.isBoss() && startGameTimer < 3.6){
            if(inGameMusic != null){
                characterMusic.stop();
                inGameMusic.start();
            }
            startedInGame = true;
        }
        if(enemy.isBoss() && !startedBossTheme){
            inGameMusic.stop();
            playSFXClip(spockBossMusic);
            startedBossTheme = true;
            startedInGame = false;
        } else if(!enemy.isBoss() && playerID != 0){
            spockBossMusic.stop();
            startedBossTheme = false;
        }
        
        if(me.getLives()<=0 || enemy.getLives()<=0)
            screenNumber++;
        
        myPList = me.getPList();
        
        g.drawImage(inGameBG,0,0,width,height,this);
        
        if(me.isOnGround())
            timesJumped = 0;
        
//        for(Platform p: platList){
//            p.draw(g,this);
//            p.animate(playerList, platList);
//        }
        
        if(me.getChargingLAttackStrength() >= PlayerOld.MAX_L)
            releaseL = true;
        
        if(chargingL)
            me.chargeLAttack();
        if(releaseL){
            chargingL = false;
            me.releaseLAttack();
            releaseL = false;
            lAnimationTimer = 0.25;
        }
        
        if(dummy == null) {
            me.animateAndDrawJAttack(playerList, enemyPList, enemy, g, this);
            me.animateAndDrawKAttack(playerList, enemy, g, this);
            me.animateAndDrawLAttack(playerList, enemy, g, this);
        }

        handlePlayerMovement();
        handleBossMode();
        me.animate();

        handleWalkingAnimation();
        handlePlayerImages();
        handleGoingOffScreen(g);
        handleAttackTimer();
        handleStartingGame(g);

        //------------------------------------------------------------------------------------------------------------------------------------------
        playerList.set(0,me); 
        if(playerID != 0)
            playerList.set(1,enemy);
        
        me.draw(g, this, playerID,enemy);
        
        if(playerID == 1)
            enemy.draw(g, this,2,me);
        else if(playerID == 2)
            enemy.draw(g,this,1,me);
        
        if(me.isBoss() || enemy.isBoss()){
            g.drawImage(fireFG,0,0,width,height,this);
        }
                
        drawBottomOfInGameScreen(g);
        drawAbilityBar(g);
        
        g.setColor(Color.white);
        g.setFont(myFont);
               
        g.setColor(Color.gray);
        g.fillRect(875,10,125,40);
        g.setColor(Color.black);
        g.setFont(new Font("Sans Serif",Font.BOLD,30));
        if(gameTimer %60 < 10)
            g.drawString(""+(int)(gameTimer/60)+" : 0"+(int)(gameTimer%60), 900,40);
        else
            g.drawString(""+(int)(gameTimer/60)+" : "+(int)(gameTimer%60), 900,40);
    }
    public void renderEndGameScreen(Graphics g){
        cheatCode = "";
        spockBossMusic.stop();
        gameTimer = 0;
        
        g.setColor(Color.black);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.white);
        g.setFont(myFont);
        
        if(me.getLives()<=0){
            g.drawString(enemy.getPlayerName()+" Wins!", 150, 150);
            g.drawImage(Lose_Meme,750,50,300,300,this);
        }
        else{
            g.drawString("You Win!!", 150,150);
            g.drawImage(Win_Meme,650,50,300,180,this);
        }
        
        g.fillRect(100, 300,550,150);
        g.setColor(Color.black);
        g.setFont(myFont);
        if(!mePlayAgain)
            g.drawString("Play Again?",120,400);
        else
            g.drawString("Waiting for other" ,120,400);
        
        if(enemyPlayAgain){
            g.setColor(Color.white);
            g.setFont(new Font("Sans Serif", Font.BOLD, 45));
            g.drawString(enemy.getPlayerName()+" wants to play again",120,550);
        }
        
        if(mePlayAgain){
            me.nullifyAll(enemyPList);
            choseMap = false;
            
            if(playerID == 1){
                me.setX(140);
                me.setDirection(Projectile.RIGHT);
            }
            else{
                me.setX(900);
                me.setDirection(Projectile.LEFT);
            }
                
            respawnTimer = 0;
            myDeathTimer = 0;
            me.setXVel(0);
            me.setYVel(0);
            me.setY(0);
            me.setPercentage(0);
            me.setIsBoss(false);
            me.setSize(60,90);
            
            right = false;
            left = false;
            
            meReadyInGame = false;
        }
        
        if(enemyPlayAgain && mePlayAgain){
            startedCharacterSelect = false;
            choseMap = false;
            enemyChoseMap = false;
                
            me.setLives(3);
            inGameMusic.setFramePosition(0);
            characterMusic.setFramePosition(0);
            startedInGame = false;
            startGameTimer = 5;
            
            screenNumber = CHARACTER_SELECT_SCREEN;
            
            try{
                Thread.sleep(500);
            } catch(InterruptedException ex){
                ex.printStackTrace();
            }
            
            mePlayAgain = false;
            enemyPlayAgain = false;
        }
            
    }
    public void renderDCScreen(Graphics g){
        g.setColor(Color.black);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.white);
        g.setFont(myFont);
        
        g.drawString(enemy.getPlayerName()+" has Disconnected.", 50, 100);
        g.drawString("Lol Out Loud!", 50, 210);
        
        g.drawImage(DC_Meme, 500,150,450,450, this);
    }
    
    public void initPlayers(){
        if(playerID == 1){
            me = new PlayerOld(140,300,60,90,"blue");
            enemy = new PlayerOld(900,300,60,90,"red");
            me.setDirection(Projectile.RIGHT);
        }
        else{
            enemy = new PlayerOld(140,300,60,90,"blue");
            me = new PlayerOld(900,300,60,90,"red");
            me.setDirection(Projectile.LEFT);
        }
        if(myName.isEmpty())
            myName = "null";
        if(myName.length()>12)
            myName = myName.substring(0,12);
        me.setPlayerName(myName);
    }
    
    public void handleGoingOffScreen(Graphics g){
        Rectangle screenBounds = new Rectangle(-200,-200,width+200,height+400);
        if(dummy != null){
            if(!screenBounds.intersects(dummy.getHitBox())){
                dummy.setX(500);
                dummy.setY(0);
                dummy.setXVel(0);
                dummy.setYVel(0);
            }
        }
        
        for(Projectile p: myPList){
            if(p!=null){
                if(!screenBounds.intersects(p.getHitBox()))
                    p.setIsNull(true);
            }
        }

        if(me.getY()<-300 || me.getX()<-200 || me.getX() > width+200 || me.getY() > height+400) {
            myDeathX = me.getX();
            myDeathY = me.getY();
            myDeathTimer = 1;
            respawnTimer = 2;
            me.setPercentage(0);
            me.setLives(me.getLives()-1);
            me.setMyMoto(null);
            for(Projectile p: myPList){
                if(p!=null)
                    p.setIsNull(true);
            }
        }
        if(enemy.getY()==100 && enemy.getX() == 530) {
            enemy.setPercentage(0);
            enemy.setUntargetable(true);
        }
        else
            enemy.setUntargetable(false);
        
        if(respawnTimer > 0){
            me.setX(530);
            me.setY(100);
            me.setXVel(0);
            me.setYVel(0);
            respawnTimer -= 1.0/60;
            me.setUntargetable(true);
        }else{
            respawnTimer = 0;
            me.setUntargetable(false);
        }
        
        if(myDeathTimer > 0){
            if(myDeathX>=200 && myDeathX<900 && myDeathY>300)
                g.drawImage(blastImages.get(0),(int)myDeathX,650-320,50,300,this);
            else if(myDeathX>=200 && myDeathX<900 && myDeathY<100)
                g.drawImage(blastImages.get(1),(int)myDeathX,0,50,300,this);
            else if(myDeathX<200 && myDeathY <500 && myDeathY >= 100)
                g.drawImage(blastImages.get(3),0,(int)myDeathY,300,50,this);
            else if(myDeathX>=900 && myDeathY <500 && myDeathY >= 100)
                g.drawImage(blastImages.get(2),800,(int)myDeathY,300,50,this);
            else if(myDeathX<200 && myDeathY >= 500)
                g.drawImage(blastImages.get(4),0,650-320,300,300, this);
            else if(myDeathX>=900 && myDeathY >= 500)
                g.drawImage(blastImages.get(5),800,650-320,300,300, this);
            else if(myDeathX<200 && myDeathY < 100)
                g.drawImage(blastImages.get(6),0,0,300,300, this);
            else if(myDeathX>900 && myDeathY < 100)
                g.drawImage(blastImages.get(7),800,0,300,300, this);
            
            myDeathTimer -= 1.0/60;
        } else
            myDeathTimer = 0;
        
        if(enemyDeathTimer > 0){
            if(enemyDeathX>=200 && enemyDeathX<900 && enemyDeathY>300)
                g.drawImage(blastImages.get(0),(int)enemyDeathX,650-320,50,300,this);
            else if(enemyDeathX>=200 && enemyDeathX<900 && enemyDeathY<100)
                g.drawImage(blastImages.get(1),(int)enemyDeathX,0,50,300,this);
            else if(enemyDeathX<200 && enemyDeathY <500 && enemyDeathY >= 100)
                g.drawImage(blastImages.get(3),0,(int)enemyDeathY,300,50,this);
            else if(enemyDeathX>=900 && enemyDeathY <500 && enemyDeathY >= 100)
                g.drawImage(blastImages.get(2),800,(int)enemyDeathY,300,50,this);
            else if(enemyDeathX<200 && enemyDeathY >= 500)
                g.drawImage(blastImages.get(4),0,650-320,300,300, this);
            else if(enemyDeathX>=900 && enemyDeathY >= 500)
                g.drawImage(blastImages.get(5),800,650-320,300,300, this);
            else if(enemyDeathX<200 && enemyDeathY < 100)
                g.drawImage(blastImages.get(6),0,0,300,300, this);
            else if(enemyDeathX>900 && enemyDeathY < 100)
                g.drawImage(blastImages.get(7),800,0,300,300, this);
        }        
    }
    public void handleAttackTimer(){
        if(jAttackTimer > 0)
            jAttackTimer -= 1.0/60;
        else
            jAttackTimer = 0;
        if(kAttackTimer > 0)
            kAttackTimer -= 1.0/60;
        else
            kAttackTimer = 0;                   
        
        if(jAnimationTimer > 0)
            jAnimationTimer -=1.0/60;
        else
            jAnimationTimer = 0;
        if(kAnimationTimer > 0)
            kAnimationTimer -=1.0/60;
        else
            kAnimationTimer = 0;  
        if(lAnimationTimer>0)
            lAnimationTimer -= 1.0/60;
        else
            lAnimationTimer = 0;
        
        if(me.getBoomerang()==null && me.getCharacter() == PlayerOld.LAWRENCE)
            kAttackTimer = 0;
    }
    public void handleStartingGame(Graphics g){
        if(startGameTimer > 0)
            startGameTimer -= 1.0/80;
        else
            startGameTimer = 0;
        
        if((Math.ceil(startGameTimer)) == 4)
            g.drawImage(img3, 530-167,155,167*2,124*2, this);
        else if((Math.ceil(startGameTimer)) == 3)
            g.drawImage(img2, 530-157,150,157*2,126*2, this);
        else if(Math.ceil(startGameTimer) == 2)
            g.drawImage(img1, 530-193,140,189*2,138*2, this);
        else if(Math.ceil(startGameTimer) == 1)
            g.drawImage(imgGo, 540-556,60,(int)(855*1.3),(int)(357*1.3), this);
    }
    public void handlePlayerImages(){
        if(me.getDirection() == Projectile.RIGHT){
            if(me.isLAttacking() || (lAnimationTimer > 0 && me.getCharacter() != PlayerOld.MATEI)
                    || (me.getCharacter() == PlayerOld.SPOCK && me.getPunch()!=null)
                    || (me.getCharacter() == PlayerOld.EMI && me.getPunch()!=null && me.getPunch().getW()>=1000)){
                me.setMyImageIndex(PlayerOld.L_ATTACK_FORWARD);
            }
            else if(me.chargingL()){
                me.setMyImageIndex(PlayerOld.CHARGE_L_ATTACK_FORWARD);
            }
            else if(me.getVPunch()!=null || me.getLightning() != null || me.getMoto()!=null || me.isHealing()){
                me.setMyImageIndex(PlayerOld.K_ATTACK_FORWARD);
            }
            else if(me.getCharacter()!= PlayerOld.UMER && kAnimationTimer>0){
                me.setMyImageIndex(PlayerOld.K_ATTACK_FORWARD);
            }
            else if(me.getPunch()!=null && me.getCharacter()!= PlayerOld.SPOCK){
                me.setMyImageIndex(PlayerOld.J_ATTACK_FORWARD);
            }
            else if(me.getCharacter()!= PlayerOld.UMER && jAnimationTimer>0){
                me.setMyImageIndex(PlayerOld.J_ATTACK_FORWARD);
            }
            else if(me.getXVel() == 0){
                me.setMyImageIndex(PlayerOld.STANDING_FORWARD);
            }
        } else {
            if(me.isLAttacking() || (lAnimationTimer > 0 && me.getCharacter() != PlayerOld.MATEI)
                    || (me.getCharacter() == PlayerOld.SPOCK && me.getPunch()!=null)
                    || (me.getCharacter() == PlayerOld.EMI && me.getPunch()!=null && me.getPunch().getW()>=1000)){
                me.setMyImageIndex(PlayerOld.L_ATTACK_BACKWARD);
            }
            else if(me.chargingL()){
                me.setMyImageIndex(PlayerOld.CHARGE_L_ATTACK_BACKWARD);
            }
            else if(me.getVPunch()!=null || me.getLightning() != null || me.getMoto()!=null || me.isHealing()){
                me.setMyImageIndex(PlayerOld.K_ATTACK_BACKWARD);
            }
            else if(me.getCharacter()!= PlayerOld.UMER && kAnimationTimer>0){
                me.setMyImageIndex(PlayerOld.K_ATTACK_BACKWARD);
            }
            else if(me.getPunch()!=null  && me.getCharacter()!= PlayerOld.SPOCK){
                me.setMyImageIndex(PlayerOld.J_ATTACK_BACKWARD);
            }
            else if(me.getCharacter()!= PlayerOld.UMER && jAnimationTimer>0){
                me.setMyImageIndex(PlayerOld.J_ATTACK_BACKWARD);
            }
            else if(me.getXVel() == 0){
                me.setMyImageIndex(PlayerOld.STANDING_BACKWARD);
            }
        }
            
    }
    public void handleWalkingAnimation(){
        if(walkingTimer > 0)
            walkingTimer -= 1.0/60;
        else
            walkingTimer = 0.4;
        
        if(walkingTimer > 0.2 && me.getDirection() == Projectile.RIGHT)
            me.setMyImageIndex(PlayerOld.RUNNING_FORWARD1);
        else if(walkingTimer > 0.2 && me.getDirection() == Projectile.LEFT)
            me.setMyImageIndex(PlayerOld.RUNNING_BACKWARD1);
        if(walkingTimer < 0.2 && me.getDirection() == Projectile.RIGHT)
            me.setMyImageIndex(PlayerOld.RUNNING_FORWARD2);
        else if(walkingTimer < 0.2 && me.getDirection() == Projectile.LEFT)
            me.setMyImageIndex(PlayerOld.RUNNING_BACKWARD2);
        
    }
    public void handleCheatCodes(){
        if(cheatCode.contains("big")){
            cheatCode = "";
            me.setY(me.getY()-180);
            me.setX(me.getX()-120);
            me.setSize(180,270);
        }
        if(cheatCode.contains("small")){
            cheatCode = "";
            me.setSize(30,40);
        }
        if(cheatCode.contains("=")){
            cheatCode = "";
            me.setCharacter(me.getCharacter()+1);
            if(me.getCharacter() >= PlayerOld.getImages().size())
                me.setCharacter(0);
        }
        if(cheatCode.contains("-")){
            cheatCode = "";
            me.setCharacter(me.getCharacter()-1);
            if(me.getCharacter() < 0)
                me.setCharacter(PlayerOld.getImages().size()-1);
        }
        if(cheatCode.contains(bossCode) && me.getCharacter()== PlayerOld.SPOCK){
            cheatCode = "";
            inGameMusic.stop();
            playSFXClip(spockBossMusic);
            me.setIsBoss(true);
            me.setSize(180,270);
        }
        if(cheatCode.contains("normal")){
            cheatCode = "";
            spockBossMusic.stop();
            inGameMusic.start();
            me.setIsBoss(false);
            me.setSize(60,90);
        }
    }
    public void handleBossMode(){        
        if(enemy.isBoss()){
            Actor.GRAVITY = 0.1;
            if(me.getXVel() > 1.7 || me.getXVel() < -1.7)
                me.setXVel(me.getXVel()/3);
            for(Projectile p: me.getPList())
                p.setXVel(p.getDirection()*4);
            if(me.getRocket()!=null)
                me.getRocket().setXVel(me.getRocket().getDir()*3);
            if(me.getLAttack()!=null)
                me.getLAttack().setXVel(me.getLAttack().getDir()*2.5);
        }
        else if(myMapHandler.getMapNumber() == 3)
            Actor.GRAVITY = 0.3;
        else
            Actor.GRAVITY = 0.5;
    }
    public void handlePlayerMovement(){
        if(left && me.getMoto() == null && !me.isDashing() && me.getXVel()>-7 && me.getXVel()<7)
            me.setXVel(-5);
        if(right && me.getMoto() == null && !me.isDashing() && me.getXVel()>-7 && me.getXVel()<7)
            me.setXVel(5);
        
        if(me.isBoss() && bossUp)
            me.setYVel(-7);
        if(me.isBoss() && bossDown)
            me.setYVel(7);
        if(me.isBoss() && left)
            me.setXVel(-7);
        if(me.isBoss() && right)
            me.setXVel(7);
    }
    
    public void drawBottomOfInGameScreen(Graphics g){
        g.setColor(Color.black);
        g.fillRect(125,460,115,140);
        g.fillRect(width-260-15,460,115,140);
                
        g.setColor(Color.white);
        g.setFont(new Font("Sans Serif", Font.PLAIN, 20));
        if(playerID == 1){
            g.drawString(df.format(me.getPercentage())+"%", 150,590);
            g.drawString(df.format(enemy.getPercentage())+"%", width-250,590);
            
            g.drawString(me.getPlayerName(),180-me.getPlayerName().length()*5,485);
            g.drawString(enemy.getPlayerName(),width-220-enemy.getPlayerName().length()*5,485);
            
            g.drawImage(PlayerOld.getImages().get(me.getCharacter()),150,510,60,60, this);
            g.drawImage(PlayerOld.getImages().get(enemy.getCharacter()),width-250,510,60,60, this);
            
            for(int i=0;i<me.getLives();i++){
                g.drawImage(life, 150+15*i, 485, this);
            }
            for(int i=0;i<enemy.getLives();i++){
                g.drawImage(life, width-250+15*i, 485, this);
            }
        } else {
            g.drawString(df.format(enemy.getPercentage())+"%", 150,590);
            g.drawString(df.format(me.getPercentage())+"%", width-250,590);
            
            g.drawString(enemy.getPlayerName(),180-enemy.getPlayerName().length()*5,485);
            g.drawString(me.getPlayerName(),width-220-me.getPlayerName().length()*5,485);
            
            g.drawImage(PlayerOld.getImages().get(enemy.getCharacter()),150,510,60,60, this);
            g.drawImage(PlayerOld.getImages().get(me.getCharacter()),width-250,510,60,60, this);
            
            for(int i=0;i<enemy.getLives();i++){
                g.drawImage(life, 150+15*i, 485, this);
            }
            for(int i=0;i<me.getLives();i++){
                g.drawImage(life, width-250+15*i, 485, this);
            }
        }
      
        g.setColor(Color.blue);
        g.drawRect(150,510,60,60);

        g.setColor(Color.red);
        g.drawRect(width-250,510,60,60);
    }
    public void drawAbilityBar(Graphics g){
        
        g.setColor(Color.black);
        g.fillRect(440, 550, 180, 50);
        
        g.setColor(Color.white); 
        g.fillOval(460,560,30,30);
        g.fillOval(515,560,30,30);
        g.fillOval(570,560,30,30);
        g.setColor(Color.black);
        g.setFont(new Font("Sans Serif", Font.BOLD, 20));
        g.drawString("J", 470,585);
        g.drawString("K", 525,585);
        g.drawString("L", 580,585);
        
        if(jAttackTimer > 0){
            g.setColor(new Color(100,100,100,220)); g.fillOval(460,560,30,30);
            g.setColor(Color.red); g.setFont(new Font("Sans Serif", Font.BOLD, 22));
            g.drawString(String.valueOf(df.format(jAttackTimer)),462,585);
        }
        if(kAttackTimer > 0){
            g.setColor(new Color(100,100,100,220)); g.fillOval(515,560,30,30);
            g.setColor(Color.red); g.setFont(new Font("Sans Serif", Font.BOLD, 22));
            g.drawString(String.valueOf(df.format(kAttackTimer)),517,585);
        }
        if(me.getLCooldown() > 0){
            g.setColor(new Color(100,100,100,220)); g.fillOval(570,560,30,30);
            g.setColor(Color.red); g.setFont(new Font("Sans Serif", Font.BOLD, 22));
            g.drawString(String.valueOf(df.format(me.getLCooldown())),572,585);
        }
    }
    public void drawReadyCheckCharacterSelect(Graphics g){
        Rectangle readyP1 = new Rectangle(90,500,350,80);
        Rectangle readyP2 = new Rectangle(640,500,350,80);        
        
        Graphics2D g2d = (Graphics2D)g;
        
        g2d.setColor(Color.black);
        g2d.fill(readyP1);
        g2d.fill(readyP2);
        
        g2d.setFont(myFont);
        g2d.setColor(Color.red);
        
        if(playerID == 1){
            if(!meReadyInGame)
                g2d.drawString("Ready?", 160,560);
            else
                g2d.drawString("Ready", 170,560);
            if(enemyReadyInGame)
                g2d.drawString("Ready", 720,560);
        } else {
            if(!meReadyInGame)
                g2d.drawString("Ready?", 710,560);
            else
                g2d.drawString("Ready", 720,560);
            if(enemyReadyInGame)
                g2d.drawString("Ready", 170,560);
        }
    }

    public void handleDummyActions(Graphics g){
        me.animateAndDrawJAttack(playerList, enemyPList, dummy, g, this);
        me.animateAndDrawKAttack(playerList,dummy, g,this);
        me.animateAndDrawLAttack(playerList,dummy,g,this);

        //animate/draw
        dummy.animate();
        dummy.draw(g, this,1,me);

        //draw percentage number
        g.drawString("Percentage: "+df.format(dummy.getPercentage()),250,100);

        //going off-screen
        Rectangle screenBounds = new Rectangle(-200,-200,width+200,height+400);
        if (!screenBounds.intersects(dummy.getHitBox())) {
            dummy.setX(500);
            dummy.setY(0);
            dummy.setXVel(0);
            dummy.setYVel(0);
        }

    }
    public void handleMultiplayerActions(Graphics g){

    }

    //------------------------------------------------------------
    //Launcher Methods
    //------------------------------------------------------------

    public void setUpLauncher(){
        SSMLauncher launcher = new SSMLauncher();

        JFrame myFrame = new JFrame("SSM Launcher");
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myFrame.setVisible(true);
        myFrame.add(launcher);
        myFrame.setResizable(false);
        myFrame.setSize(launcher.getPreferredSize());
        myFrame.setLocation(300,50);

    //    launcher.idkWhyIHaveToDoThisButThisIsKindaDum();

        while(!launcher.shouldLaunch()){
            myFrame.getComponent(0).repaint();
            launcher.setComponentLocations();

            try{Thread.sleep(16);}catch(InterruptedException ex){ex.printStackTrace();}
        }

        myFrame.setVisible(false);
      //  launcher.noVisible();

        playerMode = launcher.getPlayerMode();
        myName = launcher.getPlayerName();
        ipAddress =  launcher.getIP();
    }
    public void setUpClock(){
        clock = new Clock();
        pingClock = new Clock();
        Thread clockThread = new Thread(clock);
        Thread pingThread = new Thread(pingClock);
        clockThread.start();
        pingThread.start();
    }

    //------------------------------------------------------------
    //Networking 
    //------------------------------------------------------------
    
    private void connectToServer(){
        try{
            String port = "80";

            //Networking variables
            Socket socket = new Socket(ipAddress, Integer.parseInt(port));
            System.out.println("Connected!");
            
            InputStream inStream = socket.getInputStream();
            OutputStream outStream = socket.getOutputStream();
            
            ObjectOutputStream out = new ObjectOutputStream(outStream);
            ObjectInputStream in = new ObjectInputStream(inStream);
            
            playerID = in.readInt();
            bossCode = in.readUTF();
            System.out.println("You are player #"+playerID);
            if(playerID == 1)
                System.out.println("Waiting for player 2 to connect...");
            rfsRunnable = new ReadFromServer(in);
            wtsRunnable = new WriteToServer(out);
            rfsRunnable.startThreads();
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
    
    private class ReadFromServer implements Runnable {
        
        private final ObjectInputStream dataIn;
        
        public ReadFromServer(ObjectInputStream in){
            dataIn = in;
        }
        
        public void run(){
            try{
                
                while(true){
                    pingClock.sendPing();
                    PlayerOld.unPack(dataIn.readUTF(), enemy, me);
                    unPackGameInfo(dataIn.readUTF());
                    
//                    ArrayList<String> tempPList = (ArrayList<String>)dataIn.readObject();
//                    enemyPList = Projectile.unPackArray(tempPList);
                    
                    enemyReadyInGame = dataIn.readBoolean();
                    enemyPlayAgain = dataIn.readBoolean();
                    enemyDC = dataIn.readBoolean();
                    
                    readyToPlay = dataIn.readBoolean();
                }
                
            }catch(IOException ex){
                ex.printStackTrace();
            }
        }
        
        public void startThreads(){
            Thread readThread = new Thread(rfsRunnable);
            Thread writeThread = new Thread(wtsRunnable);
            readThread.start();
            writeThread.start();
        }
    }
    private class WriteToServer implements Runnable{
        
        private final ObjectOutputStream dataOut;
        
        public WriteToServer(ObjectOutputStream out){
            dataOut = out;
        }
        
        public void run(){
            try{
                
                while(true){                    
                    dataOut.writeUTF(PlayerOld.pack(me));
                    dataOut.writeUTF(packGameInfo());
                    
                    dataOut.writeObject(Projectile.packArray(myPList));
                    
                    dataOut.writeBoolean(meReadyInGame);
                    dataOut.writeBoolean(mePlayAgain);
                    dataOut.flush();
                    ping = pingClock.getping();
                    
                    try{
                        Thread.sleep(16);
                    }catch(InterruptedException ex){
                        ex.printStackTrace();
                    }
                }
                
            }catch(IOException ex){
               ex.printStackTrace();
            }
        }
    }
    private class Clock implements Runnable{
        private double time;
        private double pingTime;
        
        public Clock(){
            time = 0;
            pingTime = 0;
        }
        
        public void run(){
            while(true){
                try{
                    Thread.sleep(10);
                } catch(InterruptedException e){
                    e.printStackTrace();
                }
                time+= 0.01;
            }
        }
        
        public double getTime(){return time;}
        
        public double getFPS(){
            double fps = fpsTimer/time;
            fpsTimer = 0;
            time = 0;
            return fps;
        }
        
        public void sendPing(){
            pingTime = System.currentTimeMillis();
        }
        public double getping(){
            return System.currentTimeMillis()-pingTime;
        }
    }
    
    private final String parseChar = ",";
    private String packGameInfo(){
        String mapInfo = "";
        mapInfo += mouseX + parseChar;
        mapInfo += mouseY + parseChar;
        mapInfo += myMapHandler.getMapNumber() + parseChar;
        mapInfo += choseMap + parseChar;
        
        mapInfo += myDeathX + parseChar;
        mapInfo += myDeathY + parseChar;
        mapInfo += myDeathTimer + parseChar;
        
        return mapInfo;
    }
    private void unPackGameInfo(String s){
        String[] mapInfo = s.split(parseChar);
        
        enemyMouseX = Integer.parseInt(mapInfo[0]);
        enemyMouseY = Integer.parseInt(mapInfo[1]);
        if(Integer.parseInt(mapInfo[2])>=0)
            myMapHandler.setMapNumber(Integer.parseInt(mapInfo[2]));
        enemyChoseMap = Boolean.parseBoolean(mapInfo[3]);
        
        enemyDeathX = Double.parseDouble(mapInfo[4]);
        enemyDeathY = Double.parseDouble(mapInfo[5]);
        enemyDeathTimer = Double.parseDouble(mapInfo[6]);
    }
    
    
    //------------------------------------------------------------
    //Respond to Mouse Events
    //------------------------------------------------------------
    
    
    public void mouseClicked(MouseEvent e){    
        if(screenNumber == CHARACTER_SELECT_SCREEN)
            handleMouseMovementsInCharacterSelect();
        if(screenNumber == MAP_SELECT)
            choseMap = myMapHandler.handleMouseEvents(mouseX, mouseY);
        if(screenNumber == END_GAME_SCREEN)
            handleMouseMovementsInEndScreen();
    }
    public void mousePressed(MouseEvent e){
    }
    public void mouseReleased(MouseEvent e){
    }
    
    
    public void handleDrawingMouseMovementsInCharacterSelect(Graphics g){
        
        ArrayList<Rectangle> imageRectList = new ArrayList<>();
        
        int imageSize = 92;
        for(int i = 0; i< PlayerOld.getImages().size()-1; i+=1){
            int imageX = 10+(i%5)*(imageSize+10)-5;
            int imageY = 100-5+((int)(imageSize*1.5/2)+10)*(i/5);
            int imageWidth = imageSize+10;
            int imageHeight = (int)(imageSize*1.5/2)+10;
            
            imageRectList.add(new Rectangle(imageX,imageY,imageWidth,imageHeight));
        }
                
        if(playerID == 2){
            for(Rectangle r: imageRectList)
                r.x+=550;
        }
        
        Graphics2D g2d = (Graphics2D)g;
       
        g2d.setColor(Color.red);       
        
        for(Rectangle r: imageRectList){
            if(r.contains(mouseX,mouseY) && !meReadyInGame)
                g2d.fill(r);
        }
        
        g2d.setFont(myFont);
        g2d.setColor(Color.BLACK);
        
        if(playerID == 1){
            g2d.drawString(PlayerOld.getCharacterNames().get(me.getCharacter()),100,450);
            g2d.drawString(PlayerOld.getCharacterNames().get(enemy.getCharacter()), 650,450);
        } else if(playerID == 2) {
            g2d.drawString(PlayerOld.getCharacterNames().get(enemy.getCharacter()),100,450);
            g2d.drawString(PlayerOld.getCharacterNames().get(me.getCharacter()), 650,450);
        }
    }
    public void handleMouseMovementsInCharacterSelect(){
        
        ArrayList<Rectangle> imageRectList = new ArrayList<>();        
        
        int imageSize = 92;
        for(int i = 0; i< PlayerOld.getImages().size()-1; i+=1){
            int imageX = 10+(i%5)*(imageSize+10);
            int imageY = 100+((int)(imageSize*1.5/2)+10)*(i/5);
            int imageHeight = (int)(imageSize*1.5/2);
            
            imageRectList.add(new Rectangle(imageX,imageY, imageSize,imageHeight));
        }
        
        Rectangle readyP1 = new Rectangle(90,500,350,80);
        Rectangle readyP2 = new Rectangle(640,500,350,80);  
        
        if(playerID == 2){
            for(Rectangle r: imageRectList)
                r.x+=550;
        }
        
        if(!meReadyInGame) {            
            for(int i=0;i<imageRectList.size();i++){
                if(imageRectList.get(i).contains(mouseX,mouseY)){
                    me.setCharacter(i);
                    if(sfxClips.get(i)!=null)
                        playSFXClip(sfxClips.get(i));
                }
            }
        }
        if(playerID == 1 && readyP1.contains(mouseX,mouseY))
            meReadyInGame = !meReadyInGame;
        else if(playerID == 2 && readyP2.contains(mouseX,mouseY))
            meReadyInGame = !meReadyInGame;
    }
    public void handleMouseMovementsInEndScreen(){
        if(new Rectangle(100, 300,700,150).contains(mouseX,mouseY))
            mePlayAgain = !mePlayAgain;
    }
    
    //------------------------------------------------------------
    //Respond to Keyboard Events
    //------------------------------------------------------------
    public void keyTyped(KeyEvent e) 
    {
        char c = e.getKeyChar();
        
        cheatCode += c;
        
        if(startGameTimer == 0 && screenNumber == IN_GAME_SCREEN && me.getLightning() == null && !me.isStunned()){
            if((c=='j' || c=='J') && jAttackTimer == 0 && me.getMoto() == null && !me.isHealing()) {
                me.doJAttack();
                jAttackTimer = 0.25;
                jAnimationTimer = 0.25;
                if(me.getCharacter() == PlayerOld.SPOCK || me.getCharacter() == PlayerOld.EMI)
                    jAttackTimer = 0.5;
                if(me.getCharacter() == PlayerOld.OBAMA)
                    jAttackTimer = 1;
            }
            if((c=='k'||c=='K') && kAttackTimer == 0){
                me.doKAttack();
                kAttackTimer = 1.25;
                kAnimationTimer = .25;
                if(me.getCharacter() == PlayerOld.ADAM || me.getCharacter() == PlayerOld.EMI)
                    kAttackTimer = 5.0;
                if(me.getCharacter() == PlayerOld.SALOME)
                    kAttackTimer = .25;
                if(me.getCharacter() == PlayerOld.KAUSHAL)
                    kAttackTimer  = .75;
                if(me.getCharacter() == PlayerOld.SPOCK || me.getCharacter() == PlayerOld.LAWRENCE){
                    kAttackTimer = 8.0;
                    kAnimationTimer = .6;
                }
                if(me.getCharacter() == PlayerOld.LISON)
                    kAttackTimer = 3.0;
                if(me.getCharacter() == PlayerOld.OBAMA){
                    kAttackTimer = .3;
                    kAnimationTimer = .4;
                }
            }
            if(c=='p'){
                if(dummy!= null)
                    dummy.setPercentage(0);
            }
        }
    }
    public void keyPressed(KeyEvent e)
    {
        int v = e.getKeyCode();
        
        if((screenNumber == INTRO_SCREEN || playerID==0) && v==KeyEvent.VK_ENTER) {
            screenNumber++;
        }
        if(screenNumber == LOADING_SCREEN && readyToPlay && v==KeyEvent.VK_S)
            screenNumber++;

        
        if(screenNumber == IN_GAME_SCREEN && startGameTimer == 0 && !me.isStunned()){
            if((v==KeyEvent.VK_A && !me.isConfused())
                    || (v==KeyEvent.VK_D && me.isConfused())){
                left = true;
                me.setDirection(Projectile.LEFT);
            }
            if((v==KeyEvent.VK_D && !me.isConfused())
                    || (v==KeyEvent.VK_A && me.isConfused())){
                right = true;
                me.setDirection(Projectile.RIGHT);
            }
            if((v==KeyEvent.VK_W && timesJumped<2 && !me.isConfused())
                    || (v==KeyEvent.VK_S && timesJumped<2 && me.isConfused())){
                if(!enemy.isBoss())
                    me.setYVel(-10);
                else
                    me.setYVel(-4.5);
                timesJumped++;
            }
            if(v==KeyEvent.VK_W)
                bossUp = true;
            if((v==KeyEvent.VK_S && !me.isConfused())
                    || (v==KeyEvent.VK_W && me.isConfused())){
                if(!me.isBoss())
                    me.setTaunting(true);
                bossDown = true;
            }
            
            if(v==KeyEvent.VK_L && !me.isLAttacking() && me.getLCooldown() <=0 && me.getLightning() == null
                    && !me.isHealing())
                chargingL = true;
        }
            
    }
    public void keyReleased(KeyEvent e)
    {
        int v = e.getKeyCode();
        
        if(screenNumber == IN_GAME_SCREEN){
            if((v==KeyEvent.VK_A && !me.isConfused())
                    || (v==KeyEvent.VK_D && me.isConfused()))
                left = false;
            if((v==KeyEvent.VK_D && !me.isConfused())
                    || (v==KeyEvent.VK_A && me.isConfused()))
                right = false;
            if((v==KeyEvent.VK_S && !me.isConfused())
                    || (v==KeyEvent.VK_W && me.isConfused())){
                me.setTaunting(false);
                bossDown = false;
            }
            if(v==KeyEvent.VK_W)
                bossUp = false;
            
            if(v==KeyEvent.VK_L && me.getLCooldown() <=0 && me.getLightning() == null
                    && !me.isHealing())
                releaseL = true;
        }
    }
    
    
    //------------------------------------------------------------
    //Initialize Graphics
    //------------------------------------------------------------
    Image introScreen;
    Image inGameBG;
    Image life;
    Image fireFG;
    Image enemyMouse;
    
    Image img3;
    Image img2;
    Image img1;
    Image imgGo;
    
    Image Waiting_Meme;
    Image Win_Meme;
    Image Lose_Meme;
    Image DC_Meme;
    
    ArrayList<Image> blastImages;
    
    public void initGraphics() {      
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        PlayerOld.initImages();
        Rocket.initImage();
        Lightning.initImages();
        GrowingLAttack.initImage();
        Motorcycle.initImages();
        Stick.initImages();
        Projectile.initImages();
        //MapHandler.initImages(toolkit);
        //RainingCode.initImage(toolkit);
        //Boomerang.initImages(toolkit);
        
        introScreen = toolkit.getImage("SSMImages/introScreen.png");
        inGameBG = toolkit.getImage("SSMImages/Maps/background_1.png");
        life = toolkit.getImage("SSMImages/heart.png");
        fireFG = toolkit.getImage("SSMImages/fire_fg.png");
        enemyMouse = toolkit.getImage("SSMImages/mouse.png");
        
        img3 = toolkit.getImage("SSMImages/3-2-1/3.png");
        img2 = toolkit.getImage("SSMImages/3-2-1/2.png");
        img1 = toolkit.getImage("SSMImages/3-2-1/1.png");
        imgGo = toolkit.getImage("SSMImages/3-2-1/Go.png");
        
        Waiting_Meme = toolkit.getImage("SSMImages/Memes/Waiting_Meme.jpg");
        Win_Meme = toolkit.getImage("SSMImages/Memes/Win_Meme.jpg");
        Lose_Meme = toolkit.getImage("SSMImages/Memes/Lose_Meme.jpg");
        DC_Meme = toolkit.getImage("SSMImages/Memes/DC_Meme.jpg");
        
        blastImages = new ArrayList<>();
        blastImages.add(toolkit.getImage("SSMImages/BlastLines/Red_Up.png"));
        blastImages.add(toolkit.getImage("SSMImages/BlastLines/Red_Down.png"));
        blastImages.add(toolkit.getImage("SSMImages/BlastLines/Red_R.png"));
        blastImages.add(toolkit.getImage("SSMImages/BlastLines/Red_L.png"));
        blastImages.add(toolkit.getImage("SSMImages/BlastLines/Red_D_R.png"));
        blastImages.add(toolkit.getImage("SSMImages/BlastLines/Red_D_L.png"));
        blastImages.add(toolkit.getImage("SSMImages/BlastLines/Red_D_R_Down.png"));
        blastImages.add(toolkit.getImage("SSMImages/BlastLines/Red_D_L_Down.png"));
    }
    
    
    //------------------------------------------------------------
    //Initialize Sounds
    //------------------------------------------------------------
//-----------------------------------------------------------------------
/*  Music section... 
 *  To add music clips to the program, do four things.
 *  1.  Make a declaration of the AudioClip by name ...  AudioClip clipname;
 *  2.  Actually make/get the .wav file and store it in the same directory as the code.
 *  3.  Add a line into the initMusic() function to load the clip. 
 *  4.  Use the play(), stop() and loop() functions as needed in your code.
//-----------------------------------------------------------------------*/
    Clip IntroMusic;
    Clip characterMusic;
    Clip inGameMusic;
    Clip spockBossMusic;
    
    ArrayList<Clip> sfxClips;
    
    public void initMusic() 
    {        
        Projectile.initSFX();
        
        IntroMusic = AudioUtility.loadClip("SSMMusic/introSong.wav");
        characterMusic = AudioUtility.loadClip("SSMMusic/CharacterSelectTheme.wav");
        inGameMusic = AudioUtility.loadClip("SSMMusic/inGameMusic.wav");
        spockBossMusic = AudioUtility.loadClip("SSMMusic/spockBossTheme.wav");
        
        sfxClips = new ArrayList<>();
        
        sfxClips.add(AudioUtility.loadClip("SSMMusic/SFX/mateiSFX.wav"));
        sfxClips.add(AudioUtility.loadClip("SSMMusic/SFX/umerSFX.wav"));
        sfxClips.add(AudioUtility.loadClip("SSMMusic/SFX/adamSFX.wav"));
        sfxClips.add(AudioUtility.loadClip("SSMMusic/SFX/jackSFX.wav"));
        sfxClips.add(AudioUtility.loadClip("SSMMusic/SFX/kaushalSFX.wav"));
        sfxClips.add(AudioUtility.loadClip("SSMMusic/SFX/bobSFX.wav"));
        sfxClips.add(AudioUtility.loadClip("SSMMusic/SFX/spockSFX.wav"));
        sfxClips.add(AudioUtility.loadClip("SSMMusic/SFX/lison sfx.wav"));
        sfxClips.add(AudioUtility.loadClip("SSMMusic/SFX/obamaSFX.wav"));
        sfxClips.add(AudioUtility.loadClip("SSMMusic/SFX/emiSFX.wav"));
        sfxClips.add(AudioUtility.loadClip("SSMMusic/SFX/lawrenceSFX.wav"));
        sfxClips.add(AudioUtility.loadClip("SSMMusic/SFX/lawrenceSFX.wav"));
    }
    private void playSFXClip(Clip c){
        if(c!=null){
            c.stop();
            c.setFramePosition(0);
            c.start();
        }
    }
}

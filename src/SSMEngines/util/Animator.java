package SSMEngines.util;

import SSMCode.Actor;
import SSMCode.Platform;
import SSMCode.Player;
import SSMCode.PlayerAttacks.Projectile;
import SSMEngines.AnimationPanel;
import SSMEngines.SSMClient;
import SSMEngines.old.PlayerOld;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Animator {



    //------------------------------------------------------------
    //Constants
    //------------------------------------------------------------
    private final int width = AnimationPanel.width, height = AnimationPanel.height;

    public static final int INTRO_SCREEN = 0;
    public static final int LOADING_SCREEN = 1;
    public static final int CHARACTER_SELECT_SCREEN = 2;
    public static final int MAP_SELECT = 3;
    public static final int IN_GAME_SCREEN = 4;
    public static final int END_GAME_SCREEN = 5;
    public static final int DC_SCREEN = 6;

    public static final int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3;
    public static final int J = 4, K = 5, L = 6, P = 7, ENTER = 8;

    //------------------------------------------------------------
    //Instance Bariables
    //------------------------------------------------------------

    private int frameNumber;
    private final int numPlayers;
    private int screenNumber;
    private String bossCode = "Zbogck";
    private boolean justEnteredScreen;

    //timers
    private List<Double> deathTimer; //timer for the blast lines
    private List<Double> respawnTimers;
    private List<List<Double>> attackAnimationTimers;//J=0, K=1, L=2
    private List<List<Double>> playerTimers;//J=0, K=1, Walking=2 (J and K are attack cooldowns)
    private double gameTimer, startGameTimer;

    //in game variables
    private List<Player> players;
    private List<Platform> platList;
    private List<Point> deathPoints;
    private List<Integer> timesJumped;
    private List<Boolean> chargingL;
    private List<Boolean> releaseL;
    private boolean bossMode; //if any player is boss, bossMode = true
    private List<Integer> frameJumpedAt; //To prevent instant double jump bug

    //Sent from client (Player inputs)
    private List<List<Boolean>> playerMoves;
    private List<Point> mouseCoords;
    private List<Boolean> clicks;

    //character select variables
    private List<Boolean> characterSelected;

    //map screen variables
    private int frameEnteredMapSelect;
    private int mapNumber;
    private final MapHandler mapHandler;
    private boolean choseMap;

    //dc and end screen variables
    private List<Boolean> playAgain;
    private boolean disconnected;

    public Animator(int maxPlayers){
        screenNumber = CHARACTER_SELECT_SCREEN;
        mapHandler = new MapHandler();
        numPlayers = maxPlayers;
        initArrayLists();
        Player.initImages();
    }

    public void initArrayLists(){
        //Sent from client
        players = IntStream.range(0,numPlayers).mapToObj(Player::new).collect(Collectors.toList());
        playerMoves = Stream.generate(() -> Stream.generate(() -> Boolean.FALSE).limit(8).collect(Collectors.toList())).limit(numPlayers).collect(Collectors.toList());
        mouseCoords = Stream.generate(() -> new Point(-100,-100)).limit(numPlayers).collect(Collectors.toList());
        clicks = Stream.generate(() -> Boolean.FALSE).limit(numPlayers).collect(Collectors.toList());
        //character select and end screen
        characterSelected = Stream.generate(() -> Boolean.FALSE).limit(numPlayers).collect(Collectors.toList());
        playAgain = Stream.generate(() -> Boolean.FALSE).limit(numPlayers).collect(Collectors.toList());
        //timers
        deathTimer = Stream.generate(() -> (double)0).limit(numPlayers).collect(Collectors.toList());
        respawnTimers = Stream.generate(() -> (double)0).limit(numPlayers).collect(Collectors.toList());
        attackAnimationTimers = Stream.generate(() -> Stream.generate(() -> (double)0).limit(3).collect(Collectors.toList())).limit(numPlayers).collect(Collectors.toList());
        playerTimers = Stream.generate(() -> Stream.generate(() -> (double)0).limit(3).collect(Collectors.toList())).limit(numPlayers).collect(Collectors.toList());
        //in game
        deathPoints = Stream.generate(Point::new).limit(numPlayers).collect(Collectors.toList());
        timesJumped = Stream.generate(()->0).limit(numPlayers).collect(Collectors.toList());
        chargingL = Stream.generate(() -> Boolean.FALSE).limit(numPlayers).collect(Collectors.toList());
        releaseL = Stream.generate(() -> Boolean.FALSE).limit(numPlayers).collect(Collectors.toList());
        frameJumpedAt = Stream.generate(()->0).limit(numPlayers).collect(Collectors.toList());
    }

    public void animate(){
        frameNumber++;

        if(screenNumber == CHARACTER_SELECT_SCREEN)
            animateCharacterSelect();
        else if(screenNumber == MAP_SELECT)
            animateMapSelect();
        else if(screenNumber == IN_GAME_SCREEN)
            animateGame();
    }

    public void animateCharacterSelect(){
        //if a player clicks on an image they become that character
        handleMouseEventsCharacterSelect();
        //if a player presses ready button, and they selected a character, they are ready
        for(int i=0; i<mouseCoords.size(); i++){
            Point mouse = mouseCoords.get(i);
            if(new Rectangle((width-16)/2-205 - 5,415 - 5,410 + 10,50 + 10).contains(mouse)
                    && clicks.get(i) && players.get(i).getCharacter() != Player.DUMMY){
                characterSelected.set(i,true);
            }
        }
        //if all players are ready, go to map select
        if(!characterSelected.subList(0,numPlayers).contains(false)) {
            screenNumber++;
            frameEnteredMapSelect = frameNumber;
        }
    }
    public void animateMapSelect(){
        //to fix bug where if you click the ready button it also thinks you clicked a map
        if(frameNumber-frameEnteredMapSelect > 10) {
            //checks to see if any player clicked on a map
            for (int i = 0; i < clicks.size(); i++) {
                boolean click = clicks.get(i);
                if (click) {
                    choseMap = mapHandler.handleMouseEvents(mouseCoords.get(i).x, mouseCoords.get(i).y);
                }
            }
            //if a map has been chosen, set the map to that, and go into in game
            if (choseMap) {
                mapNumber = mapHandler.getMapNumber();
                Platform.setBigImg(mapHandler.getMapPlats().get(mapHandler.getMapNumber() * 2));
                Platform.setSmallImg(mapHandler.getMapPlats().get(mapHandler.getMapNumber() * 2 + 1));
                platList = mapHandler.initPlats();
                justEnteredScreen = true;

                for(int i=0; i<players.size(); i++) {
                    if(i==0)
                        players.get(i).setX(140);
                    else if(i==1)
                        players.get(i).setX(900);
                    else if(i==2)
                        players.get(i).setX(393);
                    else if(i==3)
                        players.get(i).setX(646);
                }

                screenNumber++;
            }
        }
    }
    public void animateGame(){
        justEnteredScreen = false;
        //increment the game time
        gameTimer += 1.0/60;
        //if any player has 0 lives they can't play anymore
        int numPlayersDead = 0;
        for(Player p : players){
            if(p.getLives() <= 0){
                p.setX(100); p.setY(-1000);
                p.setStunDuration(10000);
                numPlayersDead++;
            }
        }
        //if only one player is alive, end game
        if(numPlayersDead >= numPlayers-1 && numPlayers != 1)
            screenNumber++;
        //check if an enemy is boss
        bossMode = false;
        for(Player p : players){
            if(p.isBoss()){
                bossMode = true;
                break;
            }
        }
        //if a player lands on ground, reset their jumps
        for(int i=0; i<players.size(); i++){
            if(players.get(i).isOnGround())
                timesJumped.set(i,0);
        }
        //animate the platforms
        for(Platform plat : platList)
            plat.animate(players);
        //handle L attacks
        for(int i=0; i<players.size(); i++){
            Player p = players.get(i);
            //If player is at max L charge, release L
            if(p.getChargingLAttackStrength() >= Player.MAX_L)
                releaseL.set(i,true);
            //if charging, make player charge L
            if(chargingL.get(i))
                p.chargeLAttack();
            //If release L, release it and set animation timer
            if(releaseL.get(i)){
                chargingL.set(i,false);
                p.releaseLAttack();
                releaseL.set(i,false);
                attackAnimationTimers.get(i).set(2,0.25);
            }
        }
        //animate the players
        for(int i=0; i<players.size(); i++) {
            Player p = players.get(i);

            handlePlayerInputs(p, i);

            handlePlayerMovement(p,i);
            handleGravity(p);
            p.animate(players);
            
            handleWalkingAnimation(p,i);
            handlePlayerImages(p,i);
            handleDeath(p,i);
            handleAttackTimers(p,i);
        }

        if(startGameTimer > 0)
            startGameTimer -= 1.0/80;
        else
            startGameTimer = 0;

    }


    int imageSize = 90; double imgHeightRatio = 1.5/1.3;
    int imgsPerRow = 9; int imgOffset = 10;
    int xOffset = 100; int yOffset = 50;

    public void handleMouseEventsCharacterSelect(){
        ArrayList<Rectangle> imageRects = new ArrayList<>();

        for(int i = 0; i< Player.getImages().size()-1; i+=1) {
            int imageX = xOffset+(i%imgsPerRow)*(imageSize+imgOffset);
            int imageY = yOffset+((int)(imageSize*imgHeightRatio)+imgOffset)*(i/imgsPerRow);
            int imageHeight = (int)(imageSize*imgHeightRatio);

            imageRects.add(new Rectangle(imageX-5,imageY-5,imageSize+10,imageHeight+10));
        }

        for(int i=0; i<mouseCoords.size();i++) {
            Point mouse = mouseCoords.get(i);

            if(clicks.get(i) && !characterSelected.get(i)) {
                for (int j = 0; j < imageRects.size(); j++) {
                    Rectangle r = imageRects.get(j);
                    if (r.contains(mouse)) {
                        players.get(i).setCharacter(j);
                    }
                }
            }
        }
    }


    /**
     * In game methods
     */
    public void handlePlayerMovement(Player me, int index){
        //Movement only if you pressed the button to move, you're not on motorcycle
        //you're not dashing, and you aren't exceeding speed limit
        if(playerMoves.get(index).get(LEFT) && !me.isStunned()
                && !me.isDashing() && me.getXVel()>-7 && me.getXVel()<7) {
            me.setXVel(-5);
            me.setDirection(Projectile.LEFT);
            //confusion inverts controls
            if(me.isConfused()){
                me.setXVel(5);
                me.setDirection(Projectile.RIGHT);
            }

        }
        if(playerMoves.get(index).get(RIGHT) && !me.isStunned()
                && !me.isDashing() && me.getXVel()>-7 && me.getXVel()<7) {
            me.setXVel(5);
            me.setDirection(Projectile.RIGHT);
            //confusion inverts controls
            if(me.isConfused()){
                me.setXVel(-5);
                me.setDirection(Projectile.LEFT);
            }
        }
        //When you press W
        if(playerMoves.get(index).get(UP) && timesJumped.get(index)<2
                && !me.isStunned()){
            //confusion inverts controls and not instant double jump
            if(!me.isConfused() && frameNumber - frameJumpedAt.get(index) > 20) {
                if (!bossMode)
                    me.setYVel(-10);
                else
                    me.setYVel(-4.5);
                timesJumped.set(index, timesJumped.get(index) + 1);
                frameJumpedAt.set(index, frameNumber);
            } else {
                if(!me.isBoss())
                    me.setTaunting(true);
            }
        }
        //When you press S
        if(playerMoves.get(index).get(DOWN) && timesJumped.get(index)<2
                && !me.isStunned()){
            //confusion inverts controls and not instant double jump
            if(me.isConfused() && frameNumber - frameJumpedAt.get(index) > 20) {
                if (!bossMode)
                    me.setYVel(-10);
                else
                    me.setYVel(-4.5);
                timesJumped.set(index, timesJumped.get(index) + 1);
                frameJumpedAt.set(index, frameNumber);
            } else {
                if(!me.isBoss())
                    me.setTaunting(true);
            }
        }

        //Stop taunting
        if(!playerMoves.get(index).get(DOWN) && !me.isConfused())
            me.setTaunting(false);
        if(!playerMoves.get(index).get(UP) && me.isConfused())
            me.setTaunting(false);


        //Just change the direction of player
        if(playerMoves.get(index).get(LEFT) && !me.isStunned())
            me.setDirection(Projectile.LEFT);
        if(playerMoves.get(index).get(RIGHT) && !me.isStunned())
            me.setDirection(Projectile.RIGHT);

        //boss movement
        if(me.isBoss() && playerMoves.get(index).get(UP))
            me.setYVel(-7);
        if(me.isBoss() && playerMoves.get(index).get(DOWN))
            me.setYVel(7);
        if(me.isBoss() && playerMoves.get(index).get(LEFT))
            me.setXVel(-7);
        if(me.isBoss() && playerMoves.get(index).get(RIGHT))
            me.setXVel(7);
    }
    public void handleGravity(Player me){
        //if an enemy is the boss, slow everything down
        if(bossMode){
            Actor.GRAVITY = 0.1;
            if(me.getXVel() > 1.7 || me.getXVel() < -1.7)
                me.setXVel(me.getXVel()/3);
        }
        //if you're on moon, low gravity
        else if(mapNumber == 3)
            Actor.GRAVITY = 0.3;
        //regular gravity
        else
            Actor.GRAVITY = 0.5;
    }
    public void handleWalkingAnimation(Player me, int index){
        //Incrementing timer
        if(playerTimers.get(index).get(2) > 0)
            playerTimers.get(index).set(2, playerTimers.get(index).get(2)-1.0/60);
        else
            playerTimers.get(index).set(2,0.4);

        //Switching from running 1 and running 2 to animate
        if(playerTimers.get(index).get(2) > 0.2 && me.getDirection() == Projectile.RIGHT)
            me.setMyImageIndex(Player.RUNNING_FORWARD1);
        else if(playerTimers.get(index).get(2) > 0.2 && me.getDirection() == Projectile.LEFT)
            me.setMyImageIndex(Player.RUNNING_BACKWARD1);
        if(playerTimers.get(index).get(2) < 0.2 && me.getDirection() == Projectile.RIGHT)
            me.setMyImageIndex(Player.RUNNING_FORWARD2);
        else if(playerTimers.get(index).get(2) < 0.2 && me.getDirection() == Projectile.LEFT)
            me.setMyImageIndex(Player.RUNNING_BACKWARD2);

    }
    public void handlePlayerImages(Player me, int index) {
        if (me.getDirection() == Projectile.RIGHT) {
            //if l attacking; or l animating, and I'm not Matei; or I'm spock and lasering; or I'm emi and lasering
            if (me.isLAttacking() || (attackAnimationTimers.get(index).get(2) > 0 && me.getCharacter() != Player.MATEI)
                    || (me.getCharacter() == Player.SPOCK && me.getPunch() != null)
                    || (me.getCharacter() == Player.EMI && me.getPunch() != null && me.getPunch().getW() >= 1000)) {
                me.setMyImageIndex(Player.L_ATTACK_FORWARD);
            } 
            //if im charging L
            else if (me.chargingL()) {
                me.setMyImageIndex(Player.CHARGE_L_ATTACK_FORWARD);
            } 
            //If I'm k attacking
            else if (me.getVPunch() != null || me.getLightning() != null || me.getMoto() != null || me.isHealing()) {
                me.setMyImageIndex(Player.K_ATTACK_FORWARD);
            } 
            //If I'm not umer and k attacking
            else if (me.getCharacter() != Player.UMER && attackAnimationTimers.get(index).get(1) > 0) {
                me.setMyImageIndex(Player.K_ATTACK_FORWARD);
            } 
            //if I'm regular punching
            else if (me.getPunch() != null && me.getCharacter() != Player.SPOCK) {
                me.setMyImageIndex(Player.J_ATTACK_FORWARD);
            } 
            //If I'm not umer and animating J
            else if (me.getCharacter() != Player.UMER && attackAnimationTimers.get(index).get(0) > 0) {
                me.setMyImageIndex(Player.J_ATTACK_FORWARD);
            }
            //If I'm not moving
            else if (me.getXVel() == 0) {
                me.setMyImageIndex(Player.STANDING_FORWARD);
            }
        } else {
            //if l attacking; or l animating, and I'm not Matei; or I'm spock and lasering; or I'm emi and lasering
            if (me.isLAttacking() || (attackAnimationTimers.get(index).get(2) > 0 && me.getCharacter() != Player.MATEI)
                    || (me.getCharacter() == Player.SPOCK && me.getPunch() != null)
                    || (me.getCharacter() == Player.EMI && me.getPunch() != null && me.getPunch().getW() >= 1000)) {
                me.setMyImageIndex(Player.L_ATTACK_BACKWARD);
            }
            //if im charging L
            else if (me.chargingL()) {
                me.setMyImageIndex(Player.CHARGE_L_ATTACK_BACKWARD);
            }
            //If I'm k attacking
            else if (me.getVPunch() != null || me.getLightning() != null || me.getMoto() != null || me.isHealing()) {
                me.setMyImageIndex(Player.K_ATTACK_BACKWARD);
            }
            //If I'm not umer and k attacking
            else if (me.getCharacter() != Player.UMER && attackAnimationTimers.get(index).get(1) > 0) {
                me.setMyImageIndex(Player.K_ATTACK_BACKWARD);
            }
            //if I'm regular punching
            else if (me.getPunch() != null && me.getCharacter() != Player.SPOCK) {
                me.setMyImageIndex(Player.J_ATTACK_BACKWARD);
            }
            //If I'm not umer and animating J
            else if (me.getCharacter() != Player.UMER && attackAnimationTimers.get(index).get(0) > 0) {
                me.setMyImageIndex(Player.J_ATTACK_BACKWARD);
            }
            //If I'm not moving
            else if (me.getXVel() == 0) {
                me.setMyImageIndex(Player.STANDING_BACKWARD);
            }
        }
    }
    public void handleDeath(Player me, int index){
        Rectangle screenBounds = new Rectangle(-200,-200,width+200,height+400);
//        if(dummy != null){
//            if(!screenBounds.intersects(dummy.getHitBox())){
//                dummy.setX(500);
//                dummy.setY(0);
//                dummy.setXVel(0);
//                dummy.setYVel(0);
//            }
//        }

        //if I'm not on the map, I died
        if(!screenBounds.intersects(me.getHitBox())) {
            deathPoints.set(index,new Point((int)me.getX(),(int)me.getY()));
            deathTimer.set(index,1.0);
            respawnTimers.set(index,2.0);
            me.setPercentage(0);
            me.setLives(me.getLives()-1);
            me.setMyMoto(null);
            for(Projectile p : me.getPList()){
                if(p!=null)
                    p.setIsNull(true);
            }
        }
        //If I'm respawning, put me in the respawn spot and make me untargetable
        if(respawnTimers.get(index) > 0 && players.get(index).getLives() > 0){
            me.setX(530);
            me.setY(100);
            me.setXVel(0);
            me.setYVel(0);
            respawnTimers.set(index, respawnTimers.get(index) - 1.0/60);
            me.setUntargetable(true);
        }else{
            respawnTimers.set(index,0.0);
            me.setUntargetable(false);
        }
        
        //update death timers for blast lines
        if(deathTimer.get(index) > 0){
            deathTimer.set(index, deathTimer.get(index)-1.0/60);
        } else
            deathTimer.set(index,0.0);
    }
    public void handleAttackTimers(Player me, int index){
        //J attack Cooldown
        if(playerTimers.get(index).get(0) > 0)
            playerTimers.get(index).set(0,playerTimers.get(index).get(0) - 1.0/60);
        else
            playerTimers.get(index).set(0,0.0);
        //K attack Cooldown
        if(playerTimers.get(index).get(1) > 0)
            playerTimers.get(index).set(1,playerTimers.get(index).get(1) - 1.0/60);
        else
            playerTimers.get(index).set(1,0.0);
        //J animation
        if(attackAnimationTimers.get(index).get(0) > 0)
            attackAnimationTimers.get(index).set(0,attackAnimationTimers.get(index).get(0) - 1.0/60);
        else
            attackAnimationTimers.get(index).set(0,0.0);
        //k animation
        if(attackAnimationTimers.get(index).get(1) > 0)
            attackAnimationTimers.get(index).set(1,attackAnimationTimers.get(index).get(1) - 1.0/60);
        else
            attackAnimationTimers.get(index).set(1,0.0);
        //l animation
        if(attackAnimationTimers.get(index).get(2) > 0)
            attackAnimationTimers.get(index).set(2,attackAnimationTimers.get(index).get(2) - 1.0/60);
        else
            attackAnimationTimers.get(index).set(2,0.0);
        //if you caught boomerang, you reset cooldown
        if(me.getBoomerangList().isEmpty() && me.getCharacter() == Player.LAWRENCE)
            playerTimers.get(index).set(1,0.0);
    }
    public void handlePlayerInputs(Player me, int index){

        boolean j = playerMoves.get(index).get(J);
        boolean k = playerMoves.get(index).get(K);
        boolean l = playerMoves.get(index).get(L);

        //J and K attacks
        //if the game has started, you're in game, you're not using lightning, and you're not stunned, you can attack
        if(startGameTimer == 0 && screenNumber == IN_GAME_SCREEN && me.getLightning() == null && !me.isStunned()) {
            //J attack, cooldown = 0, and no motorcycle, and not healing
            if (j && playerTimers.get(index).get(0) == 0 && me.getMoto() == null && !me.isHealing()) {
                me.doJAttack();
                //set j cooldown to 0.25
                playerTimers.get(index).set(0,0.25);
                //j animation
                attackAnimationTimers.get(index).set(0,0.25);
                //spock and emi and obama have a longer cooldown
                if (me.getCharacter() == PlayerOld.SPOCK || me.getCharacter() == PlayerOld.EMI)
                    playerTimers.get(index).set(0,0.5);
                if (me.getCharacter() == PlayerOld.OBAMA)
                    playerTimers.get(index).set(0,1.0);
            }
            //K attack, cooldown = 0
            if (k && playerTimers.get(index).get(1) == 0) {
                me.doKAttack();
                //set k cooldown to 1.25
                playerTimers.get(index).set(1,1.25);
                //k animation
                attackAnimationTimers.get(index).set(1,0.25);
                if (me.getCharacter() == PlayerOld.ADAM || me.getCharacter() == PlayerOld.EMI)
                    playerTimers.get(index).set(1,5.0); //Adam and emi have cooldown of 5
                else if (me.getCharacter() == PlayerOld.SALOME)
                    playerTimers.get(index).set(1,.25); //salome has cooldown of .25
                else if (me.getCharacter() == PlayerOld.KAUSHAL)
                    playerTimers.get(index).set(1,.75); //Kaushal has cooldown on .75
                else if (me.getCharacter() == PlayerOld.SPOCK || me.getCharacter() == PlayerOld.LAWRENCE) {
                    playerTimers.get(index).set(1,8.0); //spock and lawrence have cooldown of 8
                    attackAnimationTimers.get(index).set(1,0.6); //k animation
                } else if (me.getCharacter() == PlayerOld.LISON)
                    playerTimers.get(index).set(1,3.0); //Lison has cooldown of 3
                else if (me.getCharacter() == PlayerOld.OBAMA) {
                    playerTimers.get(index).set(1,.3); //obama has cooldown of .3
                    attackAnimationTimers.get(index).set(1,0.4);
                }
            }
        }
        //L attacks
        if(l && !me.isLAttacking() && me.getLCooldown() <=0 && me.getLightning() == null
                && !me.isHealing())
            chargingL.set(index, true);
        else if(!l && me.getChargingLAttackStrength() > 0 && me.getLightning() == null
                && !me.isHealing())
            releaseL.set(index, true);
    }

    public static String parseChar = ";";

    public String pack(){
        String data = "";
        //Game data
        data+= screenNumber + SSMClient.parseChar; //0
        data+= mapNumber + SSMClient.parseChar; //1
        data+= packMice() + SSMClient.parseChar; //2
        data+= packCharacterSelect() + SSMClient.parseChar; //3

        data+= parseChar;

        //Player info
        for(Player p: players) {
            if(p == null)
                data = data.concat("null"+parseChar);
            else
                data = data.concat(p.pack() + parseChar);
        }

        return data;
    }
    public void unpack(String str, int pID){
        String[] strData = str.split(SSMClient.parseChar);
        List<Boolean> data = new ArrayList<>();


        for (int i = 0; i <= 8; i++) {
            data.add(Boolean.parseBoolean(strData[i]));
        }
        playerMoves.set(pID, data);

        mouseCoords.set(pID, new Point(Integer.parseInt(strData[9]), Integer.parseInt(strData[10])));
        clicks.set(pID, Boolean.parseBoolean(strData[11]));

        players.get(pID).setPlayerName(strData[12]);
    }

    private String packMice(){
        StringBuilder data = new StringBuilder();
        for(Point p : mouseCoords){
            data.append(p.x).append("@").append(p.y).append(Projectile.arrayParseChar);
        }
        return data.toString();
    }
    public static ArrayList<Point> unPackMice(String s){
        ArrayList<Point> mice = new ArrayList<>();
        String[] data = s.split(Projectile.arrayParseChar);
        for(String mouse : data){
            String[] mouseData = mouse.split("@");
            mice.add(new Point(Integer.parseInt(mouseData[0]), Integer.parseInt(mouseData[1])));
        }
        return mice;
    }
    private String packCharacterSelect(){
        String data = "";
        for(boolean b : characterSelected)
            data = data.concat(b + Projectile.arrayParseChar);
        return data;
    }
    public static ArrayList<Boolean> unPackCharacterSelect(String s){
        ArrayList<Boolean> bools = new ArrayList<>();
        String[] data = s.split(Projectile.arrayParseChar);
        for(String b : data)
            bools.add(Boolean.parseBoolean(b));
        return bools;
    }
}


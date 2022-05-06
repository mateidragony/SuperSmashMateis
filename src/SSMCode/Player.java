package SSMCode;

import SSMCode.PlayerAttacks.*;
import SSMEngines.AnimationPanel;
import SSMEngines.SSMClient;
import SSMEngines.old.PlayerOld;
import SSMEngines.util.AudioUtility;
import SSMEngines.util.Poolkit;

import javax.imageio.ImageIO;
import javax.sound.sampled.Clip;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Player extends Actor{

    /**
     * Character Image Indices
     */
    public static final int MATEI = 0;
    public static final int UMER = 1;
    public static final int ADAM = 2;
    public static final int JACK = 3;
    public static final int KAUSHAL = 4;
    public static final int SALOME = 5;
    public static final int SPOCK = 6;
    public static final int LISON = 7;
    public static final int OBAMA = 8;
    public static final int EMI = 9;
    public static final int LAWRENCE = 10;
    public static final int NEEL = 11;
    public static final int BRYCE = 12;
    public static final int RISHI = 13;
    public static final int DUMMY = 14;

    //Lol forward is right backward is left
    //No idea why I put forward and backward
    public static final int STANDING_FORWARD = 0;
    public static final int STANDING_BACKWARD = 1;
    public static final int RUNNING_FORWARD1 = 2;
    public static final int RUNNING_BACKWARD1 = 3;
    public static final int RUNNING_FORWARD2 = 4;
    public static final int RUNNING_BACKWARD2 = 5;
    public static final int J_ATTACK_FORWARD = 6;
    public static final int J_ATTACK_BACKWARD = 7;
    public static final int K_ATTACK_FORWARD = 8;
    public static final int K_ATTACK_BACKWARD = 9;
    public static final int CHARGE_L_ATTACK_FORWARD = 10;
    public static final int CHARGE_L_ATTACK_BACKWARD = 11;
    public static final int L_ATTACK_FORWARD = 12;
    public static final int L_ATTACK_BACKWARD = 13;


    /**
     * Variables
     */
    private static ArrayList<Image> myImages;
    private static ArrayList<ArrayList<Image>> myInGameImageLists;
    private static ArrayList<Image> miscImages;
    private static ArrayList<String> myNames;
    private static ArrayList<String> myTaunts;

    private ArrayList<String> mySFX = new ArrayList<>();

    private static final double testPrintTimer = 1;

    private int numStickyProjectiles;
    private List<Double> stickyTimers;
    private List<String> stickyTeam;
    private List<Point> stickyLocs;

    private final int playerID;
    private final String team;
    private final Color color;
    private String playerName;
    private int direction;
    private int character;
    private int lives;
    private int stunner;
    private double stunDuration, stunDrawTimer, confusionDuration, flameDuration, flameDrawTimer;

    private double inputXVel, damageXVel, airInputXVel;

    private boolean taunting;
    private boolean isBoss;

    public static final double MAX_L = 0.3;
    private double lAttackStrength, lAttackTimer, chargingLAttackStrength, lAttackCooldown, bossAttackTimer, nadoTimer;

    private int myImageIndex;


    /**
     * Constructors
     */
    public Player(double x, double y, int w, int h, int playerID) {
        super(x,y,w,h);

        this.playerID = playerID;
        character = DUMMY;
        myImageIndex = 1;
        direction = 1;

        initializeAttacks();

        lives = 3;

        playerName = "I have no name :(";

        switch(playerID){
            case(0) -> {team = "red"; color = Color.red;}
            case(1) -> {team = "blue"; color = Color.blue;}
            case(2) -> {team = "green"; color = Color.green;}
            default -> {team = "yellow"; color = Color.yellow;}
        }
    }
    public Player(int playerID) {
        super(0,0,60,90);

        this.playerID = playerID;
        character = DUMMY;
        myImageIndex = 1;
        direction = 1;

        initializeAttacks();

        lives = 3;

        playerName = "I have no name :(";

        switch(playerID){
            case(0) -> {team = "red"; color = Color.red;}
            case(1) -> {team = "blue"; color = Color.blue;}
            case(2) -> {team = "green"; color = Color.green;}
            default -> {team = "yellow"; color = Color.yellow;}
        }
    }

    /**
     *  Player Attack Lists (The player sends their attacks to the server and receives the other players' attack)
     */
    private ArrayList<Projectile> myProjectiles;
    private ArrayList<Rocket> myRockets;
    private Punch myPunch;
    private VerticalPunch myVPunch;
    private Lightning myLightning;
    private GrowingLAttack myLAttack;
    private Motorcycle myMoto;
    private boolean isHealing;
    private Stick myStick;
    private RainingCode myRain;
    private ArrayList<Boomerang> myBoomerangs;
    private ArrayList<Explosion> myExplosions;
    private Player myMimic;

    /**
     * Attack Accessors and Modifiers
     */
    public ArrayList<Projectile> getPList(){return myProjectiles;}
    public Punch getPunch(){return myPunch;}
    public ArrayList<Rocket> getRocketList(){return myRockets;}
    public VerticalPunch getVPunch(){return myVPunch;}
    public Lightning getLightning(){return myLightning;}
    public GrowingLAttack getLAttack(){return myLAttack;}
    public Motorcycle getMoto(){return myMoto;}
    public boolean isHealing(){return isHealing;}
    public Stick getStick(){return myStick;}
    public RainingCode getRain(){return myRain;}
    public ArrayList<Boomerang> getBoomerangList(){return myBoomerangs;}
    public ArrayList<Explosion> getExplosions(){return myExplosions;}
    public Player getMimic(){return myMimic;}

    public void setPList(ArrayList<Projectile> pList){myProjectiles = pList;}
    public void setMyPunch(Punch p){myPunch = p;}
    public void setRocketList(ArrayList<Rocket> rList){myRockets = rList;}
    public void setMyVPunch(VerticalPunch p){myVPunch = p;}
    public void setMyLightning(Lightning p){myLightning = p;}
    public void setMyMoto(Motorcycle p){myMoto = p;}
    public void setLAttack(GrowingLAttack g){myLAttack = g;}
    public void setMyStick(Stick p){myStick = p;}
    public void setMyRain(RainingCode p){myRain = p;}
    public void setBoomerangs(ArrayList<Boomerang> bList){myBoomerangs = bList;}
    public void setExplosions(ArrayList<Explosion> eList){myExplosions = eList;}
    public void setMimic(Player r){myMimic = r;}

    /**
     * Accessors
     */
    public String getTeam(){return team;}
    public int getDirection(){return direction;}
    public int getCharacter(){return character;}
    public String getPlayerName(){return playerName;}
    public int getLives(){return lives;}
    public double getLStrength(){return lAttackStrength;}
    public double getLCooldown(){return lAttackCooldown;}
    public double getChargingLAttackStrength(){return chargingLAttackStrength;}
    public double getStunDuration(){return stunDuration;}
    public double getFlameDuration(){return flameDuration;}
    public double getConfusionDuration(){return confusionDuration;}
    public double getLAttackTimer(){return lAttackTimer;}
    public double getNadoTimer(){return nadoTimer;}
    public Color getColor(){return color;}
    public double getInputXVel(){return inputXVel;}
    public double getDamageXVel(){return damageXVel;}
    public ArrayList<String> getMySFX(){return mySFX;}
    public int getNumStickies(){return numStickyProjectiles;}

    public boolean isBoss(){return isBoss;}
    public boolean isTaunting(){return taunting;}
    public boolean chargingL(){return chargingLAttackStrength >0;}
    public boolean isLAttacking(){return lAttackTimer>0;}
    public boolean isStunned(){return stunDuration>0;}
    public boolean isFlaming(){return flameDuration>0;}
    public boolean isConfused(){return confusionDuration>0;}
    public boolean isDashing(){return lAttackTimer > 0 && character == SALOME;}

    public static ArrayList<Image> getImages(){return myImages;}
    public static ArrayList<String> getCharacterNames(){return myNames;}

    public int getMyImageIndex(){return myImageIndex;}

    /**
     * Modifiers
     */
    public void setDirection(int c){direction = c;}
    public void setCharacter(int c){character = c;}
    public void setPlayerName(String c){playerName = c;}
    public void setLives(int c){lives = c;}
    public void setLCooldown(double c){lAttackCooldown = c;}
    public void setStunDuration(double c){stunDuration = c;}
    public void setFlameDuration(double c){flameDuration = c;}
    public void setConfusionDuration(double c){confusionDuration = c;}
    public void setTaunting(boolean c){taunting = c;}
    public void setIsBoss(boolean c){isBoss = c;}
    public void setLAttackTimer(double c){lAttackTimer = c;}
    public void setMyImageIndex(int c){myImageIndex = c;}
    public void setChargingLAttackStrength(double c){chargingLAttackStrength = c;}
    public void setNadoTimer(double c){nadoTimer = c;}
    public void setStunner(int c){stunner = c;}
    public void setHealing(boolean c){isHealing = c;}
    public void setFlameDrawTimer(double c){flameDrawTimer = c;}
    public void setStunDrawTimer(double c){stunDrawTimer = c;}
    public void setInputXVel(double c){inputXVel = c;}
    public void setDamageXVel(double c){damageXVel = c;}
    public void setAirInputXVel(double c){airInputXVel = c;}
    public void setMySFX(ArrayList<String> c){mySFX = c;}
    public void setNumStickies(int c){numStickyProjectiles = c;}
    public void addStickyTimer(double c){stickyTimers.add(c);}
    public void setStickyLocs(List<Point> c){stickyLocs = c;}
    public void addStickyLoc(Point c){stickyLocs.add(c);}
    public void addStickyTeam(String c){stickyTeam.add(c);}

    /**
     * Initializing Methods
     */
    public void initializeAttacks(){
        myProjectiles = new ArrayList<>();
        myRockets = new ArrayList<>();
        myPunch = null;
        myVPunch = null;
        myLightning = null;
        myLAttack = null;
        myMoto = null;
        isHealing = false;
        myStick = null;
        myRain = null;
        myBoomerangs = new ArrayList<>();
        myExplosions = new ArrayList<>();
        myMimic = null;

        stickyTimers = new ArrayList<>();
        stickyLocs = new ArrayList<>();
        stickyTeam = new ArrayList<>();
        numStickyProjectiles = 0;
    }
    public static void initImages(){
        Poolkit toolkit = new Poolkit();
        myImages = new ArrayList<>();
        myInGameImageLists = new ArrayList<>();
        miscImages = new ArrayList<>();
        myNames = new ArrayList<>();
        myTaunts = new ArrayList<>();

        myNames.add("Matei");
        myNames.add("Umer");
        myNames.add("Adam");
        myNames.add("Jack");
        myNames.add("Kaushal");
        myNames.add("Bob");
        myNames.add("Spock");
        myNames.add("Lison");
        myNames.add("Obama");
        myNames.add("Emi");
        myNames.add("Lawrence");
        myNames.add("Neel");
        myNames.add("Bryce");
        myNames.add("Rishi");
        myNames.add("Dummy");

        Punch.initImages();

        myImages.add(toolkit.getImage("SSMImages/Matei/Matei.jpg"));
        myImages.add(toolkit.getImage("SSMImages/Umer/IMG_1290.jpg"));
        myImages.add(toolkit.getImage("SSMImages/Adam/Adam.jpg"));
        myImages.add(toolkit.getImage("SSMImages/Jack/jack.jpg"));
        myImages.add(toolkit.getImage("SSMImages/Kaushal/kaushal.jpg"));
        myImages.add(toolkit.getImage("SSMImages/Bob/Salome.jpg"));
        myImages.add(toolkit.getImage("SSMImages/Spock/Spock.png"));
        myImages.add(toolkit.getImage("SSMImages/Lison/Lison.png"));
        myImages.add(toolkit.getImage("SSMImages/Obama/obama.png"));
        myImages.add(toolkit.getImage("SSMImages/Emi/Emi.jpg"));
        myImages.add(toolkit.getImage("SSMImages/Lawrence/lawrence.jpg"));
        myImages.add(toolkit.getImage("SSMImages/Neel/Neel.jpg"));
        myImages.add(toolkit.getImage("SSMImages/Bryce/Bryce.png"));
        myImages.add(toolkit.getImage("SSMImages/Rishi/rishi.png"));
        myImages.add(toolkit.getImage("SSMImages/Dummy.png"));

        for(String name: myNames){
            if(!name.equals("Dummy"))
                myInGameImageLists.add(initPlayerImages(toolkit,name));
        }

        miscImages.add(toolkit.getImage("SSMImages/stun_1.png"));
        miscImages.add(toolkit.getImage("SSMImages/stun_2.png"));
        miscImages.add(toolkit.getImage("SSMImages/BossMode.png"));
        miscImages.add(toolkit.getImage("SSMImages/stun_3.png"));
        miscImages.add(toolkit.getImage("SSMImages/Lison/L_C_F.png"));
        miscImages.add(toolkit.getImage("SSMImages/confusion.png"));
        miscImages.add(toolkit.getImage("SSMImages/Neel/Flaming1.png"));
        miscImages.add(toolkit.getImage("SSMImages/Neel/Flaming2.png"));

        myTaunts.add("Jemi Jumex Belbiba");
        myTaunts.add("Chupapi Muñeño");
        myTaunts.add("Wow Thats Fun");
        myTaunts.add("Nae nae fo u get a woopin");
        myTaunts.add("*Gandhi Screech*");
        myTaunts.add("What a loser!");
        myTaunts.add("Construct Yourself!");
        myTaunts.add("Silo");
        myTaunts.add("My Fellow Americans");
        myTaunts.add("Hey! That's Mine!");
        myTaunts.add("*Evil Laugh*");
        myTaunts.add("Neel!");
        myTaunts.add("Cellular Soil!");
        myTaunts.add("Ur mom");
        myTaunts.add("Im a dummy the f*** you expect me to say?");
    }
    private static ArrayList<Image> initPlayerImages(Poolkit toolkit, String playerName){
        ArrayList<Image> images = new ArrayList<>();

        images.add(toolkit.getImage("SSMImages/"+playerName+"/S_F.png"));
        images.add(toolkit.getImage("SSMImages/"+playerName+"/S_B.png"));
        images.add(toolkit.getImage("SSMImages/"+playerName+"/R_F_1.png"));
        images.add(toolkit.getImage("SSMImages/"+playerName+"/R_B_1.png"));
        images.add(toolkit.getImage("SSMImages/"+playerName+"/R_F_2.png"));
        images.add(toolkit.getImage("SSMImages/"+playerName+"/R_B_2.png"));
        images.add(toolkit.getImage("SSMImages/"+playerName+"/J_F.png"));
        images.add(toolkit.getImage("SSMImages/"+playerName+"/J_B.png"));
        images.add(toolkit.getImage("SSMImages/"+playerName+"/K_F.png"));
        images.add(toolkit.getImage("SSMImages/"+playerName+"/K_B.png"));
        images.add(toolkit.getImage("SSMImages/"+playerName+"/L_C_F.png"));
        images.add(toolkit.getImage("SSMImages/"+playerName+"/L_C_B.png"));
        images.add(toolkit.getImage("SSMImages/"+playerName+"/L_F.png"));
        images.add(toolkit.getImage("SSMImages/"+playerName+"/L_B.png"));

        return images;
    }


    /**
     * Methods
     */
    public void draw(Graphics g, ImageObserver io){

        int[] triangle1XPoints = {20,50,50};
        int[] triangle2XPoints = {1070,1040,1040};
        int[] triangleYPoints = {(int)getY(),(int)getY()-10,(int)getY()+10};

        g.setColor(color);
        if(getX()<0)
            g.fillPolygon(triangle1XPoints,triangleYPoints,3);
        if(getX()>1050)
            g.fillPolygon(triangle2XPoints,triangleYPoints,3);

        Image myCurrentImage;

        if(character==LISON && lAttackTimer>0){
            if(nadoTimer>0.1)
                myImageIndex = L_ATTACK_FORWARD;
            else
                myImageIndex = L_ATTACK_BACKWARD;
        }

        if(character<myInGameImageLists.size() && !myInGameImageLists.get(character).isEmpty())
            myCurrentImage = myInGameImageLists.get(character).get(myImageIndex);
        else
            myCurrentImage = myImages.get(character);

        if(isBoss())
            myCurrentImage = miscImages.get(2);

        //g.drawRect((int)getX(),(int)getY(),getW(),getH());
        g.setFont(new Font("Sans Serif", Font.BOLD, 18));
        g.drawString(playerName, (int)getX()+getW()/2-playerName.length()*5,(int)getY()-10);

        if(taunting){
            g.setFont(new Font("Sans Serif", Font.BOLD, 20));
            g.setColor(Color.white);
            g.drawString(myTaunts.get(character), (int)getX()+(int)getW()/2-60,(int)getY()-30);
            g.drawImage(myCurrentImage,(int)getX(),(int)getY()+getH()/2,getW(),getH()/2, io);
        } else {
            g.drawImage(myCurrentImage,(int)getX(),(int)getY(),getW(),getH(), io);
        }

        if(isStunned()){
            if(stunner == PlayerOld.LISON || stunner == PlayerOld.NEEL){
                g.drawImage(miscImages.get(3), (int)getX()-5,(int)getY()-5,getW()+10,getH()+10, io);
            }
            else{
                if(stunDrawTimer > 0.35)
                    g.drawImage(miscImages.get(0), (int)getX(),(int)getY()-37,getW(),37,io);
                else
                    g.drawImage(miscImages.get(1), (int)getX(),(int)getY()-37,getW(),37,io);
            }
        }
        if(isFlaming()){
            if(flameDrawTimer > 0.35)
                g.drawImage(miscImages.get(6),(int)getX()-5,(int)getY()-5,getW()+10,getH()+10, io);
            else
                g.drawImage(miscImages.get(7),(int)getX()-5,(int)getY()-5,getW()+10,getH()+10, io);
        }

        if(isConfused())
            g.drawImage(miscImages.get(5), (int)getX(),(int)getY()-50,getW(),37,io);

        //draw the sticky projectiles on you
        for(int i=0; i<stickyLocs.size(); i++){
            g.setColor(Color.green);
            g.fillOval(stickyLocs.get(i).x+(int)getX(),stickyLocs.get(i).y+(int)getY(),10,10);
        }


        //testing
        if(myMimic != null){
            g.setColor(Color.WHITE);
            //g.drawString(team+" mimic: "+myMimic.getTeam(),20,100);
        }
    }
    public void baseAnimate(){
        setXVel(damageXVel+inputXVel + airInputXVel);

        if(getY()+getH() >= getGround()) {
            damageXVel *= FRICTION;
            inputXVel *= FRICTION;
            airInputXVel *= FRICTION;
        }

        super.animate();
    }
    public void animate(){
        baseAnimate();
        //animate spock's boss mode
        if(isBoss)
            animateBoss();
        //handle stun timers and such
        handleTimers();
        //You can't heal past 0 percentage
        if(getPercentage()<0)
            setPercentage(0);
        //can't be on motorcycle if you are stunned
        if(isStunned())
            myMoto = null;
        //If i'm dashing slow down rapidly
        if(isDashing()) {
            damageXVel *= .75;
            inputXVel *= .75;
        }
        //Handle being on fire. You move kind of sporadically
        if(isFlaming()){
            setPercentage(getPercentage() + .075);

            if((int)(Math.random()*2) == 1)
                setX(getX()+2);
            else
                setX(getX()-2);
        }
    }
    public void animateBoss(){
        if(!isOnGround())
            setYVel(getYVel()-Actor.GRAVITY);
        setYVel(getYVel()*.75);
        damageXVel *= .75;
        inputXVel *= .75;

        setSize(180,270);

        bossAttackTimer -= 1.0/60;

        if(bossAttackTimer < 0){
            HomingShot myProj = new HomingShot((int)getX()+getW()/2,(int)getY()+getH()/4-10,
                    getDirection(),getTeam(), character, isBoss,false);
            myProjectiles.add(myProj);
            if(myRain != null){
                if(myRain.getY()>700)
                    doKAttack();
            }
            bossAttackTimer = .5;
        }
    }

    public void handleTimers(){
        if(stunDuration > 0)
            stunDuration -= 1.0/60;
        else
            stunDuration = 0;

        if(flameDuration > 0)
            flameDuration -= 1.0/60;
        else
            flameDuration = 0;

        if(confusionDuration > 0)
            confusionDuration -= 1.0/60;
        else
            confusionDuration = 0;

        if(nadoTimer > 0)
            nadoTimer-=1.0/60;
        else
            nadoTimer = 0.2;

        if(lAttackCooldown > 0)
            lAttackCooldown -=1.0/60;
        else
            lAttackCooldown = 0;

        if(flameDrawTimer > 0)
            flameDrawTimer -= 1.0/60;
        else
            flameDrawTimer = .7;

        if(stunDrawTimer > 0)
            stunDrawTimer -= 1.0/60;
        else
            stunDrawTimer = .7;

        if(lAttackTimer > 0)
            lAttackTimer -= 1.0/60;
        else{
            lAttackTimer = 0;
            myStick = null;
        }

    }

    public void drawAttacks(Graphics g, ImageObserver io){
        //Streams through projectiles and draws them
        myProjectiles.stream().filter(Objects::nonNull).forEach(p-> p.draw(g, io));
        //Streams through rockets and draws them
        myRockets.stream().filter(Objects::nonNull).forEach(r->r.draw(g,io));
        //draw punch
        if(myPunch!=null)
            myPunch.draw(g,io);
        //draw vertical punch
        if(myVPunch!=null)
            myVPunch.draw(g,io);
        //draw lightning
        if(myLightning!=null)
            myLightning.draw(g,io);
        //draw L attack
        if(myLAttack!=null)
            myLAttack.draw(g,io);
        //draw moto
        if(myMoto!=null)
            myMoto.draw(g,io);
        //draw stick
        if(myStick!=null)
            myStick.draw(g,io);
        //draw rain
        if(myRain!=null)
            myRain.draw(g,io);
        //Streams through boomerangs and draws them
        myBoomerangs.stream().filter(Objects::nonNull).forEach(r->r.draw(g,io));
        //Draw the explosion
        myExplosions.stream().filter(Objects::nonNull).forEach(p-> p.draw(g, io));
        //draw mitosis bryce
        if(myMimic != null){
            if(lAttackTimer > 0) {
                if(getX() + getW() / 2.0 < AnimationPanel.width / 2.0)
                    g.drawImage(myInGameImageLists.get(BRYCE).get(L_ATTACK_FORWARD),
                            (int)myMimic.getX(),(int)myMimic.getY(),myMimic.getW(),myMimic.getH(),io);
                else
                    g.drawImage(myInGameImageLists.get(BRYCE).get(L_ATTACK_BACKWARD),
                            (int)myMimic.getX(),(int)myMimic.getY(),myMimic.getW(),myMimic.getH(),io);
            }
            else{
                g.drawImage(myInGameImageLists.get(BRYCE).get(myImageIndex + direction),
                        (int)myMimic.getX(),(int)myMimic.getY(),myMimic.getW(),myMimic.getH(), io);
            }
        }
    }
    public void animateAttacks(ArrayList<Player> players){
        //Animate only my Projectiles
        for(int i=myProjectiles.size()-1; i>=0; i--){
            Projectile p = myProjectiles.get(i);
            if(p!=null){
                //if I am spock my J Attack switches direction when I move
                if(character == SPOCK && !isBoss){
                    p.setDirection(direction);
                    p.setY(getY());
                }
                p.animateMovement(players);
                p.animateDamage(players);

                if(p.isNull())
                    myProjectiles.remove(i);
                else if(p.outOfBounds())
                    myProjectiles.remove(i);
                else if(p.getXVel() == 0)
                    myProjectiles.remove(i);
            }else
                myProjectiles.remove(i);
        }
        //Animate only my Rockets
        for(int i=myRockets.size()-1; i>=0; i--){
            Rocket r = myRockets.get(i);
            if(r!=null){
                r.animateMovement();
                r.animateDamage(players);

                if(r.outOfBounds())
                    myRockets.remove(i);
                else if(r.getXVel() == 0)
                    myRockets.remove(i);
            }else
                myRockets.remove(i);
        }
        //Animate my punch
        if(myPunch!=null){
            //Make sure the punch is always at your current position
            myPunch.setX(getX());
            myPunch.setY(getY());
            myPunch.setShooterSize(getW(),getH());

            myPunch.animate(players);
            //If done animating punch, nullify it
            if(!myPunch.getCanHurt())
                myPunch = null;
        }
        //Animate my VerticalPunch
        if(myVPunch!=null){
            //Make sure the punch is always at your current position
            myVPunch.setX(getX());
            myVPunch.setY(getY());
            myVPunch.setShooterSize(getW(),getH());

            myVPunch.animate(players);
            //If done animating punch, nullify it
            if(!myVPunch.getCanHurt())
                myVPunch = null;
        }
        //Animate Lightning
        if(myLightning != null){
            //Put lightning in front of your hands
            myLightning.setX(getX());
            myLightning.setY(getY());
            myLightning.setDirection(direction);

            myLightning.animate(players);
            if(myLightning.isNull())
                myLightning = null;
        }
        //Animate LAttack
        if(myLAttack != null){
            myLAttack.animateMovement();
            myLAttack.animateDamage(players);
            if(myLAttack.outOfBounds())
                myLAttack = null;
        }
        //Animate Motorcycle
        if(myMoto != null){
            //Make sure the motorcycle is in the right spot
            myMoto.setDirection(direction);
            myMoto.setX(getX());
            myMoto.setY(getY());

            motoMove(direction);

            myMoto.animate(players);
            if(myMoto.isNull())
                myMoto = null;
        }
        //Animate Stunning Stick
        if(myStick != null){
            //Set the stick location to my location
            myStick.setX(getX());
            myStick.setY(getY());

            myStick.animate(players);
        }
        //Animate raining codes
        if(myRain != null){
            myRain.animate(players);
            //If rain is offscreen stop drawing it
            if(myRain.getY()>=700)
                myRain = null;
        }
        //Animate boomerangs
        for(int i=myBoomerangs.size()-1; i>=0; i--){
            Boomerang b = myBoomerangs.get(i);
            if(b!=null){
                b.setIntersecting(this.intersects(b));
                b.animateMovement();
                b.animateDamage(players);
                if(b.isNull())
                    myBoomerangs.remove(i);
            }else
                myBoomerangs.remove(i);
        }
        //Kaushal's healing
        if(isHealing){
            if(getPercentage() > 0)
                setPercentage(getPercentage()-0.3);
            if(getXVel() < -3 || getXVel() > 3)
                isHealing = false;
        }
        //Animate the explosions
        for(int i=myExplosions.size()-1; i>=0; i--){
            Explosion e = myExplosions.get(i);
            if(e != null){
                e.animate(players);
                if(e.isNull())
                    myExplosions.remove(e);
            } else
                myExplosions.remove(e);
        }
        //animate Bryce's mimic
        if(myMimic != null){
            if(lAttackTimer <= 0) {
                myMimic.animate();
                //if the mimic dies, it's null
                Rectangle screenBounds = new Rectangle(-200,-300,AnimationPanel.width+450,AnimationPanel.height+400);
                if(!screenBounds.contains(myMimic.getHitBox()))
                    myMimic = null;
            }
        }
        //sticky projectile timers
        for(int i = stickyTimers.size()-1; i>=0; i--){
            if(stickyTimers.get(i) > 0)
                stickyTimers.set(i, stickyTimers.get(i) - 1.0/60);
            else{
                Point p = stickyLocs.get(i);
                myExplosions.add(new Explosion(p.x+(int)getX(),p.y+(int)getY(),100,100,
                        stickyTeam.get(i),.4,false,character));
                stickyTimers.remove(i);
                stickyLocs.remove(i);
                stickyTeam.remove(i);
                numStickyProjectiles--;
                mySFX.add("stickyExplosion");
            }
        }

        //Animate Matei's Minigun, Lison's tornado, and Salome's dash, and Bryce's split drawing
        animateLAttacks(players);
    }
    public void animateLAttacks(ArrayList<Player> players){
        //Shoot a stream of projectiles
        if(getCharacter() == MATEI){
            if(lAttackTimer > 0){
                Projectile myProj;
                if(direction == Projectile.RIGHT)
                    myProj = new Projectile((int)getX()+getW(),(int)getY()+4+getH()/4,
                            9,9,getDirection(),getTeam(), character,false,false);
                else
                    myProj = new Projectile((int)getX()-10,(int)getY()+4+getH()/4,
                            9,9,getDirection(),getTeam(), character,false,false);

                myProjectiles.add(myProj);
                mySFX.add("gun");
            }
        }
        //Animate Lison's tornado
        if(getCharacter() == LISON){
            if(lAttackTimer > 0){
                setSize(116,90); //change the size so the image isn't squished
                inputXVel = 8*direction; //You can't stand still if you're a tornado

                for(Player enemy : players) {
                    if (this.getHitBox().intersects(enemy.getHitBox())
                            && !team.equals(enemy.getTeam())
                            && !enemy.isUntargetable()) {
                        enemy.setPercentage(enemy.getPercentage() + 0.3);
                        enemy.setDamageXVel((2.5 + 4 * enemy.getPercentage() / 25) * direction);
                    }
                }
            } else //If she's not L attacking make her normal again
                setSize(60,90);
        }
        //Animate salome's damaging dash
        if(isDashing()){
            for(Player enemy : players){
                if(getHitBox().intersects(enemy.getHitBox())){
                    enemy.setPercentage(enemy.getPercentage()+5.5);
                    enemy.setDamageXVel(1.5*getXVel()/60*(0.5+1.5*enemy.getPercentage()/20));
                    enemy.setYVel(.5*(enemy.getYVel()-1.5-2*enemy.getPercentage()/50));
                }
            }
        }
        //Animate drawing Bryce's split
        if(character == BRYCE){
            if(lAttackTimer > 0 && myMimic != null){
                myMimic.setX((int)getX() + (int)((AnimationPanel.width - getX()*2 - getW()) * ((MAX_L - lAttackTimer)/MAX_L)));
                lAttackTimer += 1.0/120;
            }
        }
    }

    public void doJAttack(){
        //If I am umer, kaushal, salome, emi, or lawrence; my J attack acts as a punch
        if(getCharacter() == UMER || character == KAUSHAL || character == SALOME
                || character == EMI || character == LAWRENCE){
            myPunch = new Punch((int)getX(),(int)getY(),getDirection(),getTeam(),2, true,character,getW(),getH());
            if(character == EMI){
                myPunch.setSize(180,5);
                myPunch.setPunchCD(myPunch.getPunchCD()*2);
            } else if(character == LAWRENCE){
                myPunch.setSize(60,5);
                myPunch.setPunchCD(myPunch.getPunchCD()*1.25);
            }
        }
        //If I am Matei, my J attack acts as a projectile
        else if(character == MATEI){
            Projectile myProj = new Projectile((int)getX()+getW()/2,(int)getY()+getH()/4,
                    10,10,getDirection(),getTeam(), character, false, false);
            myProjectiles.add(myProj);
            mySFX.add("gun");
        }
        //If I am Obama, my J attack is a rocket
        else if(character == OBAMA){
            Rocket myRocket = new Rocket((int)getX()+getW()/2,(int)getY()-20+character*3,50,50,getDirection(),
                    getTeam(),character);
            myRocket.setXVel(myRocket.getXVel());
            myRockets.add(myRocket);
        }
        //If I am neel, my J attack acts as a projectile with direction and different size
        else if(character == NEEL){
            if(direction == Projectile.LEFT){
                Projectile myProj = new Projectile((int)getX()+getW()/2-40,(int)getY()+getH()/4-10,
                        65,10,getDirection(),getTeam(),character, false, false);
                myProjectiles.add(myProj);
            } else {
                Projectile myProj = new Projectile((int)getX()+getW()/2,(int)getY()+getH()/4-10,
                        65,10,getDirection(),getTeam(),character, false, false);
                myProjectiles.add(myProj);
            }
        }
        //If I am spock, my J attack is a projectile that is as big as me
        else if(character == SPOCK){
            Projectile myProj = new Projectile((int)getX()+getW()/2,(int)getY()+getH()/4-10,
                    getW(),getH(),getDirection(),getTeam(),character, isBoss,false);
            myProjectiles.add(myProj);
        }
        //If I am Bryce my J attack is a sticky projectile that blows up later
        else if(character == BRYCE){
            StickyProjectile myProj = new StickyProjectile((int)getX()+getW()/2,(int)getY()+getH()/4-10,
                    20,20,getDirection(),getTeam(),character, false);
            myProjectiles.add(myProj);
            mySFX.add("stickyShot");

            if(myMimic != null) {
                myProj = new StickyProjectile((int) myMimic.getX() + getW() / 2, (int) myMimic.getY() + getH() / 4 - 10,
                        20, 20, -1 * getDirection(), getTeam(), character, false);
                myProjectiles.add(myProj);
                mySFX.add("stickyShot");
            }
        }
        //If I am not a character jack or adam, my J attack is a simple projectile
        else {
            Projectile myProj = new Projectile((int)getX()+getW()/2,(int)getY()+getH()/4-10,
                    10,10,getDirection(),getTeam(),character, false, false);
            myProjectiles.add(myProj);
            if(character == JACK)
                mySFX.add("gun");
            if(character == ADAM)
                mySFX.add("fireball");
        }

        isHealing = false;
    }
    public void doKAttack(){
        //Umer Vertical Punch
        if(getCharacter() == UMER){
            myVPunch = new VerticalPunch((int)getX(),(int)getY(), getDirection(), getTeam(),
                    2, true,character,getW(),getH());
        }
        //Matei Lison and Neel
        else if(getCharacter() == MATEI || character == LISON || character == NEEL){
            Rocket rocket = new Rocket((int)getX()+getW()/2,(int)getY()-20+character*3,50,50,getDirection(),
                    getTeam(),character);
            if(character == NEEL)
                rocket.setSize(65,10);
            if(character == MATEI)
                mySFX.add("rocket");

            myRockets.add(rocket);
        }
        //Adam Lightning
        else if(getCharacter() == ADAM){
            myLightning = new Lightning((int)getX(),(int)getY(),getDirection(),getTeam(),
                    1, false,1.9,character);
            mySFX.add("electricity");
        }
        //Jack Motorcycle
        else if(getCharacter() == JACK){
            if(myMoto == null)
                myMoto = new Motorcycle((int)getX(),(int)getY(),getDirection(),getTeam(),false);
            else
                myMoto = null;
        }
        //Kaushal healing
        else if(getCharacter() == KAUSHAL){
            isHealing = !isHealing;
        }
        //Salome Projectile
        else if(character == SALOME){
            Projectile myProj = new Projectile((int)getX()+getW()/2,(int)getY()+getH()/4-10,
                    10,10,getDirection(),getTeam(), character,false,false);
            myProjectiles.add(myProj);
            mySFX.add("gun");
        }
        //Spock Raining Code
        else if(character == SPOCK){
            myRain = new RainingCode(60,-800, team,character);
        }
        //Obama Secret Service
        else if(character == OBAMA){
            Projectile bg1 = new Projectile((int)getX(),(int)getY(),
                    getW(),getH(),Projectile.LEFT,getTeam(), character ,false,false);
            Projectile bg2 = new Projectile((int)getX(),(int)getY(),
                    getW(),getH(),Projectile.RIGHT,getTeam(), character,false,false);
            myProjectiles.add(bg1); myProjectiles.add(bg2);
        }
        //Emi Confusion
        else if(character == EMI){
            Projectile myProj = new Projectile((int)getX()+getW()/2,(int)getY()+getH()/4-10,
                    30,45,getDirection(),getTeam(), character,false,false);
            myProjectiles.add(myProj);
        }
        //Lawrence Boomerang
        else if(character == LAWRENCE){
            myBoomerangs.add(new Boomerang((int)getX(),(int)getY(),getDirection(),
                    (int)getX()+getDirection()*800, false, getTeam(),
                    false, 1, character));
        }
        //Bryce switch + explosion
        else if(character == BRYCE){
            double oldX = getX(); double oldY = getY();

            if(myMimic != null) {
                setX(myMimic.getX());
                setY(myMimic.getY());
                myMimic.setX(oldX);
                myMimic.setY(oldY);

                myExplosions.add(new Explosion((int) (oldX + getW() / 2.0), (int) (oldY + getH() / 2.0), 150,
                        150, team, .4, false, character));
            }

            myExplosions.add(new Explosion((int) (getX() + getW() / 2.0), (int) (getY() + getH() / 2.0), 150,
                    150, team, .4, false, character));
            mySFX.add("stickyExplosion");
        }
    }
    public void chargeLAttack(){
        if(character == ADAM || character == UMER )
            chargingLAttackStrength+=1.0/300;
        else if(character == JACK)
            chargingLAttackStrength+=1.0/60;
        else if(character == SALOME)
            chargingLAttackStrength+=1.0/100;
        else if(character == MATEI || character == KAUSHAL)
            chargingLAttackStrength+=1.0/160;
        else if(character == SPOCK || character == EMI)
            chargingLAttackStrength+=1.0/450;
        if(chargingLAttackStrength > MAX_L || character == LISON
                || character == OBAMA || character == LAWRENCE
                || character == NEEL || character == BRYCE)
            chargingLAttackStrength = MAX_L;

        isHealing = false;
    }
    public void releaseLAttack(){
        lAttackStrength = chargingLAttackStrength;
        lAttackTimer = chargingLAttackStrength;
        chargingLAttackStrength = 0;

        lAttackCooldown = 2.5;

        if(character == ADAM || character == UMER){
            int size = (int)(10+lAttackStrength*700);
            myLAttack = new GrowingLAttack((int)getX(),(int)getY()+getH()/2-size/2-17,
                    direction,getTeam(),size,character);
            lAttackTimer = 0;

            if(character == ADAM)
                mySFX.add("sun");
        }
        else if(character == JACK){
            setYVel(-lAttackStrength*60);
            lAttackTimer = 0;
        }
        else if(character == KAUSHAL){
            myStick = new Stick((int)getX(),(int)getY(),direction,getTeam(),lAttackStrength * 3,getW());
            lAttackTimer *= 2;
        }
        else if(character == SALOME){
            setInputXVel(lAttackStrength*300*direction);
            lAttackCooldown = 1.5;
            lAttackTimer -= 0.1;
        }
        else if(character == SPOCK || character == EMI){
            myPunch = new Punch((int)getX(),(int)getY(),direction,getTeam(),
                    lAttackStrength*60,true,character,getW(),getH());
            myPunch.setSize(1100,200);
            lAttackCooldown = 9;
        }
        else if(character == LISON){
            lAttackTimer = 2;
            lAttackCooldown = 7;
        }
        else if(character == OBAMA){
            myRain=new RainingCode(60,-600,getTeam(),character);
            lAttackTimer *= 3;
            lAttackCooldown = 8;
        }
        else if(character == LAWRENCE){
            myLightning = new Lightning((int)getX(),(int)getY(),getDirection(),getTeam(),1,
                    false,1.9,character);
            lAttackTimer = 1.9;
            lAttackCooldown = 5.0;
        }
        else if(character == NEEL){
            if(direction == Projectile.LEFT){
                Projectile myProj = new Projectile((int)getX()+getW()/2-40,(int)getY()+getH()/4-10,
                        81,13,getDirection(),getTeam(),character,false, false);
                myProjectiles.add(myProj);
            } else {
                Projectile myProj = new Projectile((int)getX()+getW()/2,(int)getY()+getH()/4-10,
                        81,13,getDirection(),getTeam(),character,false, false);
                myProjectiles.add(myProj);
            }
            lAttackCooldown = 5.0;
        }
        else if(character == BRYCE){
            myMimic = new Player((int)getX(),(int)getY(),getW(),getH(),playerID);
            lAttackCooldown = 20.0;
        }
    }

    public void resetStickies(){
        stickyTimers = new ArrayList<>();
        stickyLocs = new ArrayList<>();
        stickyTeam = new ArrayList<>();
        numStickyProjectiles = 0;
    }

    //movement
    public void move(int dir){

        if(dir == -1 && getXVel() > -5)
            inputXVel = 5 * dir;
        else if(dir == 1 && getXVel() < 5)
            inputXVel = 5 * dir;

        if(!isOnGround() && (inputXVel+damageXVel+airInputXVel) < 5 && dir == Projectile.RIGHT)
            airInputXVel += .1;
        else if(!isOnGround() && (inputXVel+damageXVel+airInputXVel) > -5 && dir == Projectile.LEFT)
            airInputXVel -= .1;

        if(myMimic != null){
            myMimic.move(-dir);
        }
    }
    public void motoMove(int dir){
        if(dir == -1 && getXVel() > -15)
            inputXVel = 15 * dir;
        else if(dir == 1 && getXVel() < 15)
            inputXVel = 15 * dir;

        if(!isOnGround() && (inputXVel+damageXVel+airInputXVel) < 15 && dir == Projectile.RIGHT)
            airInputXVel += .1;
        else if(!isOnGround() && (inputXVel+damageXVel+airInputXVel) > -15 && dir == Projectile.LEFT)
            airInputXVel -= .1;
    }


    /**
     * Packing and Unpacking Players
     */

    public static final String parseChar = "/";
    private int printTimer = 0;

    public String pack(){
        String packedPlayersInfo = "";

        if(printTimer <= 0){
            printTimer = 150;
            //System.out.println("SFX size: "+mySFX.size());
        } else
            printTimer--;

        //Pack the player's individual data
        packedPlayersInfo += (int)getX()+ SSMClient.parseChar; //0
        packedPlayersInfo += (int)getY()+SSMClient.parseChar; //1
        packedPlayersInfo += getW()+SSMClient.parseChar; //2
        packedPlayersInfo += getH()+SSMClient.parseChar; //3
        packedPlayersInfo += playerID + SSMClient.parseChar; //4
        packedPlayersInfo += getPercentage()+SSMClient.parseChar; //5
        packedPlayersInfo += getCharacter()+SSMClient.parseChar; //6
        packedPlayersInfo += getPlayerName()+SSMClient.parseChar; //7
        packedPlayersInfo += getLives()+SSMClient.parseChar; //8
        packedPlayersInfo += getDirection()+SSMClient.parseChar; //9
        packedPlayersInfo += isTaunting()+SSMClient.parseChar; //10
        packedPlayersInfo += getMyImageIndex() + SSMClient.parseChar; //11
        packedPlayersInfo += getStunDuration() + SSMClient.parseChar; //12
        packedPlayersInfo += getXVel() + SSMClient.parseChar; //13
        packedPlayersInfo += isBoss() + SSMClient.parseChar; //14
        packedPlayersInfo += getConfusionDuration() + SSMClient.parseChar; //15
        packedPlayersInfo += getLAttackTimer() + SSMClient.parseChar; //16
        packedPlayersInfo += getFlameDuration() + SSMClient.parseChar; //17
        packedPlayersInfo += chargingLAttackStrength + SSMClient.parseChar; //18
        packedPlayersInfo += stunner + SSMClient.parseChar; //19
        packedPlayersInfo += stunDrawTimer + SSMClient.parseChar; //20
        packedPlayersInfo += flameDrawTimer + SSMClient.parseChar; //21
        packedPlayersInfo += lAttackCooldown + SSMClient.parseChar; //22
        packedPlayersInfo += packSFX() + SSMClient.parseChar; //23
        packedPlayersInfo += numStickyProjectiles + SSMClient.parseChar; //24
        packedPlayersInfo += packStickyLocs() + SSMClient.parseChar; //25

        packedPlayersInfo += parseChar;

        //pack the attacks
        packedPlayersInfo += Projectile.packArray(myProjectiles) + parseChar;
        packedPlayersInfo += Rocket.packArray(myRockets) + parseChar;
        packedPlayersInfo += Punch.pack(myPunch) + parseChar;
        packedPlayersInfo += VerticalPunch.pack(myVPunch) + parseChar;
        packedPlayersInfo += Lightning.pack(myLightning) + parseChar;
        packedPlayersInfo += GrowingLAttack.pack(myLAttack) + parseChar;
        packedPlayersInfo += Motorcycle.pack(myMoto) + parseChar;
        packedPlayersInfo += isHealing + parseChar;
        packedPlayersInfo += Stick.pack(myStick) + parseChar;
        packedPlayersInfo += RainingCode.pack(myRain) + parseChar;
        packedPlayersInfo += Boomerang.packArray(myBoomerangs) + parseChar;
        packedPlayersInfo += Explosion.packArray(myExplosions) + parseChar;
        packedPlayersInfo += packMimic() + parseChar;

        return packedPlayersInfo;
    }
    public static Player unPack(String str){

        String[] playerData = str.split(parseChar);
        String[] data = playerData[0].split(SSMClient.parseChar);

        Player player = new Player(Integer.parseInt(data[0]),Integer.parseInt(data[1]),Integer.parseInt(data[2]),
                Integer.parseInt(data[3]),Integer.parseInt(data[4]));
        player.setPercentage(Double.parseDouble(data[5]));
        player.setCharacter(Integer.parseInt(data[6]));
        player.setPlayerName(data[7]);
        player.setLives(Integer.parseInt(data[8]));
        player.setDirection(Integer.parseInt(data[9]));
        player.setTaunting(Boolean.parseBoolean(data[10]));
        player.setMyImageIndex(Integer.parseInt(data[11]));
        player.setStunDuration(Double.parseDouble(data[12]));
        player.setXVel(Double.parseDouble(data[13]));
        player.setIsBoss(Boolean.parseBoolean(data[14]));
        player.setConfusionDuration(Double.parseDouble(data[15]));
        player.setLAttackTimer(Double.parseDouble(data[16]));
        player.setFlameDuration(Double.parseDouble(data[17]));
        player.setChargingLAttackStrength(Double.parseDouble(data[18]));
        player.setStunner(Integer.parseInt(data[19]));
        player.setStunDrawTimer(Double.parseDouble(data[20]));
        player.setFlameDrawTimer(Double.parseDouble(data[21]));
        player.setLCooldown(Double.parseDouble(data[22]));
        player.setMySFX(player.unPackSFX(data[23]));
        player.setNumStickies(Integer.parseInt(data[24]));
        player.setStickyLocs(player.unPackStickyLocs(data[25]));

        player.setPList(Projectile.unPackArray(playerData[1]));
        player.setRocketList(Rocket.unPackArray(playerData[2]));
        player.setMyPunch(Punch.unPack(playerData[3]));
        player.setMyVPunch(VerticalPunch.unPack(playerData[4]));
        player.setMyLightning(Lightning.unPack(playerData[5]));
        player.setLAttack(GrowingLAttack.unPack(playerData[6]));
        player.setMyMoto(Motorcycle.unPack(playerData[7]));
        player.setHealing(Boolean.parseBoolean(playerData[8]));
        player.setMyStick(Stick.unPack(playerData[9]));
        player.setMyRain(RainingCode.unPack(playerData[10]));
        player.setBoomerangs(Boomerang.unPackArray(playerData[11]));
        player.setExplosions(Explosion.unPackArray(playerData[12]));
        player.setMimic(player.unPackMimic(playerData[13],player));

        return player;
    }

    //Sound effect codes:
    //"gun" = Matei, Salome, Jack projectiles
    public String packSFX(){
        String data = " ";
        for(int i=0; i<mySFX.size(); i++)
            data = data.concat(mySFX.get(i) + Projectile.arrayParseChar);
        return data;
    }
    public ArrayList<String> unPackSFX(String s){
        String[] data = s.split(Projectile.arrayParseChar);
        return new ArrayList<>(Arrays.asList(data));
    }
    public static ArrayList<Clip> convertSFX(List<String> data){
        ArrayList<Clip> sfx = new ArrayList<>();
        for(String str : data){
            str = str.trim();
            switch(str){
                case("gun") -> sfx.add(AudioUtility.loadClip("SSMMusic/SFX/MateiAttacks/pew.wav"));
                case("rocket") -> sfx.add(AudioUtility.loadClip("SSMMusic/SFX/MateiAttacks/rocketSFX.wav"));
                case("stickyShot") -> sfx.add(AudioUtility.loadClip("SSMMusic/SFX/BryceAttacks/stickyShot.wav"));
                case("stickyExplosion") -> sfx.add(AudioUtility.loadClip("SSMMusic/SFX/BryceAttacks/stickyExplosion.wav"));
                case("fireball") -> sfx.add(AudioUtility.loadClip("SSMMusic/SFX/AdamAttacks/fireballSFX.wav"));
                case("electricity") -> sfx.add(AudioUtility.loadClip("SSMMusic/SFX/AdamAttacks/electricity.wav"));
                case("sun") -> sfx.add(AudioUtility.loadClip("SSMMusic/SFX/AdamAttacks/sunSFX.wav"));
            }
        }
        return sfx;
    }
    public String packStickyLocs(){
        String data = " ";
        for(int i = stickyLocs.size()-1; i>=0; i--) {
            Point p = stickyLocs.get(i);
            data = data.concat(p.x + "#" + p.y + Projectile.arrayParseChar);
        }
        return data;
    }
    public List<Point> unPackStickyLocs(String s){
        String[] data = s.split(Projectile.arrayParseChar);
        List<Point> locs = new ArrayList<>();
        for(String str : data) {
            if(!str.equals(" ")) {
                String[] point = str.split("#");
                locs.add(new Point(Integer.parseInt(point[0].trim()), Integer.parseInt(point[1].trim())));
            }
        }
        return locs;
    }
    public String packMimic(){
        String data = " ";
        if(myMimic == null)
            return "null";
        data += myMimic.getX() + Projectile.arrayParseChar;
        data += myMimic.getY() + Projectile.arrayParseChar;
        data += myMimic.getW() + Projectile.arrayParseChar;
        data += myMimic.getH() + Projectile.arrayParseChar;
        return data;
    }
    public Player unPackMimic(String in, Player me){
        String[] data = in.trim().split(Projectile.arrayParseChar);
        if(in.equals("null"))
            return null;
        return new Player(Double.parseDouble(data[0]),Double.parseDouble(data[1]),
                Integer.parseInt(data[2]),Integer.parseInt(data[3]), me.playerID);
    }
}


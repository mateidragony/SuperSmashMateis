package SSMCode;

import SSMCode.PlayerAttacks.*;
import SSMEngines.SSMClient;
import SSMEngines.old.PlayerOld;
import SSMEngines.util.Poolkit;

import javax.imageio.ImageIO;
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
    public static final int DUMMY = 12;

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
    
    private static final double testPrintTimer = 1;

    private final int playerID;
    private final String team;
    private final Color color;
    private String playerName;
    private int direction;
    private int character;
    private int lives;
    private int stunner;
    private double stunDuration, stunDrawTimer, confusionDuration, flameDuration, flameDrawTimer;

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

        initializeAttacks();

        lives = 3;

        playerName = "UmerMain123";

        switch(playerID){
            case(0) -> {team = "red"; color = Color.red;}
            case(1) -> {team = "blue"; color = Color.blue;}
            case(2) -> {team = "green"; color = Color.green;}
            default -> {team = "yellow"; color = Color.yellow;}
        }
    }
    public Player(int playerID) {
        super(0,0,40,60);

        this.playerID = playerID;
        character = DUMMY;
        myImageIndex = 1;

        initializeAttacks();

        lives = 3;

        playerName = "UmerMain123";

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
    private static List<List<Projectile>> projectiles;
    private static List<List<Rocket>> rockets;
    private static List<Punch> punches;
    private static List<VerticalPunch> vPunches;
    private static List<Lightning> lightningList;
    private static List<GrowingLAttack> lAttacks;
    private static List<Motorcycle> motoList;
    private static List<Stick> sticks;
    private static List<RainingCode> rainList;
    private static List<List<Boomerang>> boomerangs;

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

    public void setMyPunch(Punch p){myPunch = p;}
    public void setMyVPunch(VerticalPunch p){myVPunch = p;}
    public void setMyLightning(Lightning p){myLightning = p;}
    public void setMyMoto(Motorcycle p){myMoto = p;}
    public void setMyStick(Stick p){myStick = p;}
    public void setMyRain(RainingCode p){myRain = p;}


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

    /**
     * Initializing Methods
     */
    public void initializeAttacks(){
        projectiles = Stream.generate(ArrayList<Projectile>::new).limit(4).collect(Collectors.toList());
        rockets = Stream.generate(ArrayList<Rocket>::new).limit(4).collect(Collectors.toList());
        punches = Stream.generate(()->(Punch)null).limit(4).collect(Collectors.toList());
        vPunches = Stream.generate(()->(VerticalPunch)null).limit(4).collect(Collectors.toList());
        lightningList = Stream.generate(()->(Lightning)null).limit(4).collect(Collectors.toList());
        lAttacks = Stream.generate(()->(GrowingLAttack)null).limit(4).collect(Collectors.toList());
        motoList = Stream.generate(()->(Motorcycle)null).limit(4).collect(Collectors.toList());
        sticks = Stream.generate(()->(Stick)null).limit(4).collect(Collectors.toList());
        rainList = Stream.generate(()->(RainingCode)null).limit(4).collect(Collectors.toList());
        boomerangs = Stream.generate(ArrayList<Boomerang>::new).limit(4).collect(Collectors.toList());

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

        handleTimers();

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
            if(stunDrawTimer > 0)
                stunDrawTimer -= 1.0/60;
            else
                stunDrawTimer = .7;

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
            if(flameDrawTimer > 0)
                flameDrawTimer -= 1.0/60;
            else
                flameDrawTimer = .7;

            if(flameDrawTimer > 0.35)
                g.drawImage(miscImages.get(6),(int)getX()-5,(int)getY()-5,getW()+10,getH()+10, io);
            else
                g.drawImage(miscImages.get(7),(int)getX()-5,(int)getY()-5,getW()+10,getH()+10, io);
        }

        if(isConfused())
            g.drawImage(miscImages.get(5), (int)getX(),(int)getY()-50,getW(),37,io);
    }
    public void animate(List<Player> players){
        super.animate();
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
        if(isDashing())
            setXVel(getXVel()*.75);
        //Handle being on fire. You move kind of sporadically
        if(isFlaming()){
            setPercentage(getPercentage() + .075);

            if((int)(Math.random()*2) == 1)
                setX(getX()+2);
            else
                setX(getX()-2);
        }

        animateAttacks((ArrayList<Player>) players);
        addAttacksToLists();
    }
    public void animateBoss(){
        if(!isOnGround())
            setYVel(getYVel()-Actor.GRAVITY);
        setYVel(getYVel()*.75);
        setXVel(getXVel()*.75);

        bossAttackTimer -= 1.0/60;

        if(bossAttackTimer < 0){
            HomingShot myProj = new HomingShot((int)getX()+getW()/2,(int)getY()+getH()/4-10,
                    getDirection(),getTeam(), character, isBoss,false);
            myProjectiles.add(myProj);
            if(myRain != null){
                if(myRain.getY()>700)
                    doKAttack();
            }
            bossAttackTimer = .1;
        }
    }

    public void addAttacksToLists(){
        //Set your attacks to the static global arrayLists
        projectiles.set(playerID,myProjectiles);
        rockets.set(playerID,myRockets);
        punches.set(playerID,myPunch);
        vPunches.set(playerID,myVPunch);
        lightningList.set(playerID,myLightning);
        lAttacks.set(playerID,myLAttack);
        motoList.set(playerID,myMoto);
        sticks.set(playerID,myStick);
        rainList.set(playerID,myRain);
        boomerangs.set(playerID,myBoomerangs);
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

        if(lAttackTimer > 0)
            lAttackTimer -= 1.0/60;
        else{
            lAttackTimer = 0;
            myStick = null;
        }
    }

    public static void drawAttacks(Graphics g, ImageObserver io){
        //Streams through all the lists in projectiles then streams through those and draws the projectile
        projectiles.stream().flatMap(List::stream).filter(Objects::nonNull).forEach(p->p.draw(g,io));
        //Streams through all the lists in rockets then streams through those and draws the rocket
        rockets.stream().flatMap(List::stream).filter(Objects::nonNull).forEach(r->r.draw(g,io));
        //Streams through punches and draws each punch
        punches.stream().filter(Objects::nonNull).forEach(p->p.draw(g,io));
        //Streams through vertical punches and draws each vertical punch
        vPunches.stream().filter(Objects::nonNull).forEach(vP->vP.draw(g,io));
        //Streams through lightnings and draws each lightning
        lightningList.stream().filter(Objects::nonNull).forEach(l->l.draw(g,io));
        //Streams through LAttacks and draws each LAttack
        lAttacks.stream().filter(Objects::nonNull).forEach(l->l.draw(g,io));;
        //Streams through Motorcycles and draws each motorcycle
        motoList.stream().filter(Objects::nonNull).forEach(m->m.draw(g,io));;
        //Streams through sticks and draws each stick
        sticks.stream().filter(Objects::nonNull).forEach(s->s.draw(g,io));;
        //Streams through rains and draws each rain
        rainList.stream().filter(Objects::nonNull).forEach(r->r.draw(g,io));;
        //Streams through all the lists in boomerangs then streams through those and draws the boomerang
        boomerangs.forEach(list->list.stream().filter(Objects::nonNull).forEach(b->b.draw(g,io)));;
    }
    public void animateAttacks(ArrayList<Player> players){
        //Animate only my Projectiles
        for(int i=myProjectiles.size()-1; i>=0; i--){
            Projectile p = myProjectiles.get(i);
            if(p!=null){
                //if I am spock my J Attack switches direction when I move
                if(character == SPOCK){
                    p.setDirection(direction);
                    p.setY(getY());
                }
                p.animateMovement(players);
                p.animateDamage(players);

                if(p.isNull())
                    myProjectiles.remove(i);
                if(p.outOfBounds())
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
            //The if is to balance, so you can't save yourself always with your motorcycle
            if(getXVel()>=-20 && getXVel()<=20)
                setXVel(15*direction);

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
            }else
                myBoomerangs.remove(i);
        }

        //Animate Matei's Minigun, Lison's tornado, and Salome's dash
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
            }
        }
        //Animate Lison's tornado
        if(getCharacter() == LISON){
            if(lAttackTimer > 0){
                setSize(116,90); //change the size so the image isn't squished
                setXVel(8*direction); //You can't stand still if you're a tornado

                for(Player enemy : players) {
                    if (this.getHitBox().intersects(enemy.getHitBox())
                            && !team.equals(enemy.getTeam())
                            && !enemy.isUntargetable()) {
                        enemy.setPercentage(enemy.getPercentage() + 0.3);
                        enemy.setXVel((2.5 + 4 * enemy.getPercentage() / 25) * direction);
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
                    enemy.setXVel(1.5*getXVel()/60*(0.5+1.5*enemy.getPercentage()/20));
                    enemy.setYVel(.5*(enemy.getYVel()-1.5-2*enemy.getPercentage()/50));
                }
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
        //If I am not a character, my J attack is a simple projectile
        else {
            Projectile myProj = new Projectile((int)getX()+getW()/2,(int)getY()+getH()/4-10,
                    10,10,getDirection(),getTeam(),character, false, false);
            myProjectiles.add(myProj);
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

            myRockets.add(rocket);
        }
        //Adam Lightning
        else if(getCharacter() == ADAM){
            myLightning = new Lightning((int)getX(),(int)getY(),getDirection(),getTeam(),
                    1, false,1.9,character);
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
                || character == NEEL)
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
            setXVel(lAttackStrength*300*direction);
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
    }


    /**
     * Packing and Unpacking Players
     */
    public String pack(){
        String packedPlayersInfo = "";

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

        //        packedPlayersInfo += parseChar;
//
//        //pack the attacks
//        packedPlayersInfo += Projectile.packArray(myProjectiles) + parseChar;
//        packedPlayersInfo += Rocket.packArray(myRockets) + parseChar;
//        packedPlayersInfo += Punch.pack(myPunch) + parseChar;
//        packedPlayersInfo += VerticalPunch.pack(myVPunch) + parseChar;
//        packedPlayersInfo += Lightning.pack(myLightning) + parseChar;
//        packedPlayersInfo += GrowingLAttack.pack(myLAttack) + parseChar;
//        packedPlayersInfo += Motorcycle.pack(myMoto) + parseChar;
//        packedPlayersInfo += isHealing + parseChar;
//        packedPlayersInfo += Stick.pack(myStick) + parseChar;
//        packedPlayersInfo += RainingCode.pack(myRain) + parseChar;
//        packedPlayersInfo += Boomerang.packArray(myBoomerangs) + parseChar;

        return packedPlayersInfo;
    }
    public static Player unPack(String str){
        String[] data = str.split(SSMClient.parseChar);

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

        return player;
    }
}


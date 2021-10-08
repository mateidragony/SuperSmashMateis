package SSMCode;
/**
 *
 * @author 22cloteauxm
 */

import SSMCode.PlayerAttacks.*;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

public class Player extends Actor{
    
    //--------------------------------------------
    //Character Image Indices
    //--------------------------------------------
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
    //No idea why i put forward and backward
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
    
    
    //--------------------------------------------
    //Variables
    //--------------------------------------------
    private static ArrayList<Image> myImages;
    private static ArrayList<ArrayList<Image>> myInGameImageLists;
    private static ArrayList<Image> miscImages;
    private static ArrayList<String> myNames;
    private static ArrayList<String> myTaunts;
    
    private static double testPrintTimer = 1;
    
    private ArrayList<Projectile> pList;
    private String team;
    private String playerName;
    private int direction;
    private int character;
    private int lives;
    private double stunDuration, stunDrawTimer, confusionDuration, flameDuration, flameDrawTimer;
    
    private boolean taunting;
    private boolean isBoss;
    
    private Punch myPunch;
    private Punch enemyPunch;
    
    private Rocket myRocket;
    private Rocket enemyRocket;
    
    private VerticalPunch myVPunch;
    private VerticalPunch enemyVPunch;
    
    private Lightning myLightning;
    private Lightning enemyLightning;
    
    private GrowingLAttack myLAttack;
    private GrowingLAttack enemyLAttack;
    
    private Motorcycle myMoto;
    private Motorcycle enemyMoto;
    
    private Stick myStick;
    private Stick enemyStick;
    
    private boolean isHealing;
    private boolean enemyIsHealing;
    
    private RainingCode myRain;
    private RainingCode enemyRain;
    
    private Boomerang myBoomerang;
    private Boomerang enemyBoomerang;
    
    private boolean enemyIsDashing;
    
    public static final double MAX_L = 0.3;
    private double lAttackStrength, lAttackTimer, chargingLAttackStrength, lAttackCooldown, bossAttackTimer, nadoTimer;
    
    private int myImageIndex;
    private int myImageIndexFrame;
    
    public Player(double x, double y, int w, int h, String team_) {
        super(x,y,w,h);
        
        team = team_;
        character = 0;
        myImageIndex = 1;
        pList = new ArrayList<>();
        
        lives = 3;
        
        playerName = "Dummy";
	
    }

    //--------------------------------------------
    //Dealing with attacks
    //--------------------------------------------
    public ArrayList<Projectile> getPList(){return pList;}
    public Punch getPunch(){return myPunch;}
    public Rocket getRocket(){return myRocket;}
    public VerticalPunch getVPunch(){return myVPunch;}
    public Lightning getLightning(){return myLightning;}
    public GrowingLAttack getLAttack(){return myLAttack;}
    public Motorcycle getMoto(){return myMoto;}
    public boolean isHealing(){return isHealing;}
    public Stick getStick(){return myStick;}
    public RainingCode getRain(){return myRain;}
    public Boomerang getBoomerang(){return myBoomerang;}
    
    public void setEnemyPunch(Punch c){enemyPunch = c;}
    public void setEnemyRocket(Rocket c){enemyRocket = c;}
    public void setEnemyVPunch(VerticalPunch c){enemyVPunch = c;}
    public void setEnemyLightning(Lightning c){enemyLightning = c;}
    public void setEnemyLAttack(GrowingLAttack c){enemyLAttack = c;}
    public void setEnemyMoto(Motorcycle c){enemyMoto = c;}
    public void setMyMoto(Motorcycle c){myMoto = c;}
    public void setEnemyHealing(boolean c){enemyIsHealing = c;}
    public void setEnemyStick(Stick c){enemyStick = c;}
    public void setEnemyDashing(boolean c){enemyIsDashing = c;}
    public void setEnemyRain(RainingCode c){enemyRain = c;}
    public void setEnemyBoomerang(Boomerang c){enemyBoomerang = c;}
    
    //--------------------------------------------
    //Accessors
    //--------------------------------------------
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
    
    public boolean isBoss(){return isBoss;}
    public boolean isTaunting(){return taunting;}
    public boolean chargingL(){return chargingLAttackStrength >0;}
    public boolean isLAttacking(){return lAttackTimer>0;}
    public boolean isStunned(){return stunDuration>0;}
    public boolean isFlaming(){return flameDuration>0;}
    public boolean isConfused(){return confusionDuration>0;}
    public boolean isDashing(){return lAttackTimer > 0 && character == SALOME;}
    public boolean getEnemyDashing(){return enemyIsDashing;}
    
    public static ArrayList<Image> getImages(){return myImages;}
    public static ArrayList<String> getCharacterNames(){return myNames;}

    //--------------------------------------------
    //Modifiers
    //--------------------------------------------
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
    
    public void draw(Graphics g, ImageObserver io, int playerID, Player enemy){
        
        int[] triangle1XPoints = new int[3];
        int[] triangle2XPoints = new int[3];
        int[] triangleYPoints = new int[3];
        
        triangle1XPoints[0] = 20;triangle1XPoints[1] = 50;triangle1XPoints[2] = 50;
        triangle2XPoints[0] = 1070;triangle2XPoints[1] = 1040;triangle2XPoints[2] = 1040;
        triangleYPoints[0] = (int)getY();triangleYPoints[1] = (int)getY()-10;triangleYPoints[2] = (int)getY()+10;
        
        if(team.equals("blue"))
            g.setColor(Color.blue);
        else
            g.setColor(Color.red);
        
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
        g.drawString(playerName, (int)getX()+(int)getW()/2-playerName.length()*5,(int)getY()-10);
        
        if(taunting){
            g.setFont(new Font("Sans Serif", Font.BOLD, 20));
            g.setColor(Color.white);
            g.drawString(myTaunts.get(character), (int)getX()+(int)getW()/2-60,(int)getY()-30);
            g.drawImage(myCurrentImage,(int)getX(),(int)getY()+getH()/2,getW(),getH()/2, io);
        } else {
            g.drawImage(myCurrentImage,(int)getX(),(int)getY(),getW(),getH(), io);
        }
        
        if(getX()<0)
            g.fillPolygon(triangle1XPoints,triangleYPoints,3);
        if(getX()>1050)
            g.fillPolygon(triangle2XPoints,triangleYPoints,3);
        
        if(isStunned()){
            if(stunDrawTimer > 0)
                stunDrawTimer -= 1.0/60;
            else
                stunDrawTimer = .7;
            
            if(enemy.getCharacter() == Player.LISON || enemy.getCharacter() == Player.NEEL){
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
    
    public void animate(){
        super.animate();
        if(isBoss){
            animateBoss();
        }
        
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
        
        if(getPercentage()<0)
            setPercentage(0);
        
        if(isStunned())
            myMoto = null;
        
        if(isDashing())
            setXVel(getXVel()*.75);
        
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
        setXVel(getXVel()*.75);
        
        bossAttackTimer -= 1.0/60;
        
        if(bossAttackTimer < 0){
            HomingShot myProj = new HomingShot((int)getX()+getW()/2,(int)getY()+getH()/4-10,
                    getDirection(),getTeam(), false);
            pList.add(myProj);
            if(myRain != null){
                if(myRain.getY()>700)
                    doKAttack();
            }
            bossAttackTimer = .1;
        }
    }
    
    public void nullifyAll(ArrayList<Projectile> enemyPList){
        pList.clear();
        enemyPList.clear();
        
        myPunch = null;
        enemyPunch= null;

        myRocket= null;
        enemyRocket= null;

        myVPunch= null;
        enemyVPunch= null;

        myLightning= null;
        enemyLightning= null;

        myLAttack= null;
        enemyLAttack= null;
        
        myMoto = null;
        enemyMoto = null;
        
        isHealing = false;
        enemyIsHealing = false;
        
        myStick = null;
        enemyStick = null;
        
        myRain = null;
        enemyRain = null;
        
        myBoomerang = null;
        enemyBoomerang = null;
    }
    
    //--------------------------------------------
    //Dealing with Images
    //--------------------------------------------
    public int getMyImageIndex(){return myImageIndex;}
    public int getMyImageIndexFrame(){return myImageIndexFrame;}
    
    public void setMyImageIndex(int c){myImageIndex = c;}
    public void setMyImageIndexFrame(int c){myImageIndexFrame = c;}
    
    public static void initImages(){
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        myImages = new ArrayList<>();
        myInGameImageLists = new ArrayList<>();
        miscImages = new ArrayList<>();
        myNames = new ArrayList<>();
        myTaunts = new ArrayList<>();
        
        Punch.initImages();
        
        myImages.add(toolkit.getImage("SSMImages/Matei/Matei.jpg"));
        myImages.add(toolkit.getImage("SSMImages/Umer/IMG_1290.jpg"));
        myImages.add(toolkit.getImage("SSMImages/Adam/Adam.jpg"));
        myImages.add(toolkit.getImage("SSMImages/Jack/jack.jpg"));
        myImages.add(toolkit.getImage("SSMImages/Kaushal/kaushal.jpg"));
        myImages.add(toolkit.getImage("SSMImages/Salome/Salome.jpg"));
        myImages.add(toolkit.getImage("SSMImages/Spock/Spock.png"));
        myImages.add(toolkit.getImage("SSMImages/Lison/Lison.png"));
        myImages.add(toolkit.getImage("SSMImages/Obama/obama.png"));
        myImages.add(toolkit.getImage("SSMImages/Emi/Emi.jpg"));
        myImages.add(toolkit.getImage("SSMImages/Lawrence/lawrence.jpg"));
        myImages.add(toolkit.getImage("SSMImages/Neel/Neel.jpg"));
        myImages.add(toolkit.getImage("SSMImages/Dummy.png"));
        
        myInGameImageLists.add(initMateiImages());
        myInGameImageLists.add(initUmerImages());
        myInGameImageLists.add(initAdamImages());
        myInGameImageLists.add(initJackImages());
        myInGameImageLists.add(initKaushalImages());
        myInGameImageLists.add(initSalomeImages());
        myInGameImageLists.add(initSpockImages());
        myInGameImageLists.add(initLisonImages()); 
        myInGameImageLists.add(initObamaImages());
        myInGameImageLists.add(initEmiImages());
        myInGameImageLists.add(initLawrenceImages());
        myInGameImageLists.add(initNeelImages());
        
        miscImages.add(toolkit.getImage("SSMImages/stun_1.png"));
        miscImages.add(toolkit.getImage("SSMImages/stun_2.png"));
        miscImages.add(toolkit.getImage("SSMImages/BossMode.png"));
        miscImages.add(toolkit.getImage("SSMImages/stun_3.png"));
        miscImages.add(toolkit.getImage("SSMImages/Lison/L_1.png"));
        miscImages.add(toolkit.getImage("SSMImages/confusion.png"));
        miscImages.add(toolkit.getImage("SSMImages/Neel/Flaming1.png"));
        miscImages.add(toolkit.getImage("SSMImages/Neel/Flaming2.png"));
        
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
        
        myTaunts.add("Jemi Jumex Belbiba");
        myTaunts.add("Chupapi Muñeño");
        myTaunts.add("Wow Thats Fun");
        myTaunts.add("Nae nae fo u get a woopin");
        myTaunts.add("*Gandhi Screech*");
        myTaunts.add("What a loser!");
        myTaunts.add("Construct Yourself!");
        myTaunts.add("Oh My Ktty Kitty");
        myTaunts.add("My Fellow Americans");
        myTaunts.add("Hey! That's Mine!");
        myTaunts.add("*Evil Laugh*");
        myTaunts.add("Neel!");
        myTaunts.add("Im a dummy the f*** you expect me to say?");
    }
    
    public static ArrayList<Image> initMateiImages(){
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        ArrayList<Image> MateiImages = new ArrayList<>();

        MateiImages.add(toolkit.getImage("SSMImages/Matei/Matei_S_F.png"));
        MateiImages.add(toolkit.getImage("SSMImages/Matei/Matei_S_B.png"));
        MateiImages.add(toolkit.getImage("SSMImages/Matei/Matei_R_F_1.png"));
        MateiImages.add(toolkit.getImage("SSMImages/Matei/Matei_R_B_1.png"));
        MateiImages.add(toolkit.getImage("SSMImages/Matei/Matei_R_F_2.png"));
        MateiImages.add(toolkit.getImage("SSMImages/Matei/Matei_R_B_2.png"));
        MateiImages.add(toolkit.getImage("SSMImages/Matei/Matei_J_F.png"));
        MateiImages.add(toolkit.getImage("SSMImages/Matei/Matei_J_B.png"));
        MateiImages.add(toolkit.getImage("SSMImages/Matei/Matei_K_F.png"));
        MateiImages.add(toolkit.getImage("SSMImages/Matei/Matei_K_B.png"));
        MateiImages.add(toolkit.getImage("SSMImages/Matei/Matei_L_C_F.png"));
        MateiImages.add(toolkit.getImage("SSMImages/Matei/Matei_L_C_B.png"));
        MateiImages.add(toolkit.getImage("SSMImages/Matei/Matei_L_F.png"));
        MateiImages.add(toolkit.getImage("SSMImages/Matei/Matei_L_B.png"));
        
        return MateiImages;
    }
    public static ArrayList<Image> initUmerImages(){
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        ArrayList<Image> UmerImages = new ArrayList<>();
        
        UmerImages.add(toolkit.getImage("SSMImages/Umer/Umer_S_F.png"));
        UmerImages.add(toolkit.getImage("SSMImages/Umer/Umer_S_B.png"));
        UmerImages.add(toolkit.getImage("SSMImages/Umer/Umer_F_R.png"));
        UmerImages.add(toolkit.getImage("SSMImages/Umer/Umer_B_R.png"));
        UmerImages.add(toolkit.getImage("SSMImages/Umer/Umer_F_R_2.png"));
        UmerImages.add(toolkit.getImage("SSMImages/Umer/Umer_B_R_2.png"));
        UmerImages.add(toolkit.getImage("SSMImages/Umer/Umer_P_F.png"));
        UmerImages.add(toolkit.getImage("SSMImages/Umer/Umer_P_B.png"));
        UmerImages.add(toolkit.getImage("SSMImages/Umer/Umer_P_F_U.png"));
        UmerImages.add(toolkit.getImage("SSMImages/Umer/Umer_P_B_U.png"));
        UmerImages.add(toolkit.getImage("SSMImages/Umer/Umer_L_C_F.png"));
        UmerImages.add(toolkit.getImage("SSMImages/Umer/Umer_L_C_B.png"));
        UmerImages.add(toolkit.getImage("SSMImages/Umer/Umer_L_F.png"));
        UmerImages.add(toolkit.getImage("SSMImages/Umer/Umer_L_B.png"));
        
        return UmerImages;
    }
    public static ArrayList<Image> initAdamImages(){
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        ArrayList<Image> adamList = new ArrayList<>();
        
        adamList.add(toolkit.getImage("SSMImages/Adam/Adam_S_F.png"));
        adamList.add(toolkit.getImage("SSMImages/Adam/Adam_S_B.png"));
        adamList.add(toolkit.getImage("SSMImages/Adam/Adam_W_F_1.png"));
        adamList.add(toolkit.getImage("SSMImages/Adam/Adam_W_B_1.png"));
        adamList.add(toolkit.getImage("SSMImages/Adam/Adam_W_F_2.png"));
        adamList.add(toolkit.getImage("SSMImages/Adam/Adam_W_B_2.png"));
        adamList.add(toolkit.getImage("SSMImages/Adam/Adam_J_F.png"));
        adamList.add(toolkit.getImage("SSMImages/Adam/Adam_J_B.png"));
        adamList.add(toolkit.getImage("SSMImages/Adam/Adam_K_F.png"));
        adamList.add(toolkit.getImage("SSMImages/Adam/Adam_K_B.png"));
        adamList.add(toolkit.getImage("SSMImages/Adam/Adam_L_C_F.png"));
        adamList.add(toolkit.getImage("SSMImages/Adam/Adam_L_C_B.png"));
        adamList.add(toolkit.getImage("SSMImages/Adam/Adam_L_F.png"));
        adamList.add(toolkit.getImage("SSMImages/Adam/Adam_L_B.png"));
        
        return adamList;
    }
    public static ArrayList<Image> initJackImages(){
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        ArrayList<Image> jackImages = new ArrayList<>();
        
        jackImages.add(toolkit.getImage("SSMImages/Jack/Jack_S_F.png"));
        jackImages.add(toolkit.getImage("SSMImages/Jack/Jack_S_B.png"));
        jackImages.add(toolkit.getImage("SSMImages/Jack/Jack_R_F_1.png"));
        jackImages.add(toolkit.getImage("SSMImages/Jack/Jack_R_B_1.png"));
        jackImages.add(toolkit.getImage("SSMImages/Jack/Jack_R_F_2.png"));
        jackImages.add(toolkit.getImage("SSMImages/Jack/Jack_R_B_2.png"));
        jackImages.add(toolkit.getImage("SSMImages/Jack/Jack_J_F.png"));
        jackImages.add(toolkit.getImage("SSMImages/Jack/Jack_J_B.png"));
        jackImages.add(toolkit.getImage("SSMImages/Jack/Jack_K_F.png"));
        jackImages.add(toolkit.getImage("SSMImages/Jack/Jack_K_B.png"));
        jackImages.add(toolkit.getImage("SSMImages/Jack/Jack_L_C_F.png"));
        jackImages.add(toolkit.getImage("SSMImages/Jack/Jack_L_C_B.png"));
        jackImages.add(toolkit.getImage("SSMImages/Jack/Jack_L_F.png"));
        jackImages.add(toolkit.getImage("SSMImages/Jack/Jack_L_B.png"));
        
        return jackImages;
    }
    public static ArrayList<Image> initKaushalImages(){
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        ArrayList<Image> kaushalImages = new ArrayList<>();
        
        kaushalImages.add(toolkit.getImage("SSMImages/Kaushal/S_F.png"));
        kaushalImages.add(toolkit.getImage("SSMImages/Kaushal/S_B.png"));
        kaushalImages.add(toolkit.getImage("SSMImages/Kaushal/R_F.png"));
        kaushalImages.add(toolkit.getImage("SSMImages/Kaushal/R_B.png"));
        kaushalImages.add(toolkit.getImage("SSMImages/Kaushal/R_F_2.png"));
        kaushalImages.add(toolkit.getImage("SSMImages/Kaushal/R_B_2.png"));
        kaushalImages.add(toolkit.getImage("SSMImages/Kaushal/J_F.png"));
        kaushalImages.add(toolkit.getImage("SSMImages/Kaushal/J_B.png"));
        kaushalImages.add(toolkit.getImage("SSMImages/Kaushal/K.png"));
        kaushalImages.add(toolkit.getImage("SSMImages/Kaushal/K.png"));
        kaushalImages.add(toolkit.getImage("SSMImages/Kaushal/L_C_F.png"));
        kaushalImages.add(toolkit.getImage("SSMImages/Kaushal/L_C_B.png"));
        kaushalImages.add(toolkit.getImage("SSMImages/Kaushal/L_F.png"));
        kaushalImages.add(toolkit.getImage("SSMImages/Kaushal/L_B.png"));
        
        return kaushalImages;
    }
    public static ArrayList<Image> initSalomeImages(){
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        ArrayList<Image> bobImages = new ArrayList<>();
        
        bobImages.add(toolkit.getImage("SSMImages/Salome/Standing_F.png"));
        bobImages.add(toolkit.getImage("SSMImages/Salome/Standing_B.png"));
        bobImages.add(toolkit.getImage("SSMImages/Salome/R_F_1.png"));
        bobImages.add(toolkit.getImage("SSMImages/Salome/R_B_1.png"));
        bobImages.add(toolkit.getImage("SSMImages/Salome/R_F_2.png"));
        bobImages.add(toolkit.getImage("SSMImages/Salome/R_B_2.png"));
        bobImages.add(toolkit.getImage("SSMImages/Salome/J_F.png"));
        bobImages.add(toolkit.getImage("SSMImages/Salome/J_B.png"));
        bobImages.add(toolkit.getImage("SSMImages/Salome/K_F.png"));
        bobImages.add(toolkit.getImage("SSMImages/Salome/K_B.png"));
        bobImages.add(toolkit.getImage("SSMImages/Salome/L_C_F.png"));
        bobImages.add(toolkit.getImage("SSMImages/Salome/L_C_B.png"));
        bobImages.add(toolkit.getImage("SSMImages/Salome/L_F.png"));
        bobImages.add(toolkit.getImage("SSMImages/Salome/L_B.png"));
        
        return bobImages;
    }
    public static ArrayList<Image> initSpockImages(){
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        ArrayList<Image> spockImages = new ArrayList<>();
        
        spockImages.add(toolkit.getImage("SSMImages/Spock/S_F.png"));
        spockImages.add(toolkit.getImage("SSMImages/Spock/S_B.png"));
        spockImages.add(toolkit.getImage("SSMImages/Spock/R_1_F.png"));
        spockImages.add(toolkit.getImage("SSMImages/Spock/R_1_B.png"));
        spockImages.add(toolkit.getImage("SSMImages/Spock/R_2_F.png"));
        spockImages.add(toolkit.getImage("SSMImages/Spock/R_2_B.png"));
        spockImages.add(toolkit.getImage("SSMImages/Spock/J_F.png"));
        spockImages.add(toolkit.getImage("SSMImages/Spock/J_B.png"));
        spockImages.add(toolkit.getImage("SSMImages/Spock/K.png"));
        spockImages.add(toolkit.getImage("SSMImages/Spock/K.png"));
        spockImages.add(toolkit.getImage("SSMImages/Spock/L_C_F.png"));
        spockImages.add(toolkit.getImage("SSMImages/Spock/L_C_B.png"));
        spockImages.add(toolkit.getImage("SSMImages/Spock/L_F.png"));
        spockImages.add(toolkit.getImage("SSMImages/Spock/L_B.png"));
        
        return spockImages;
    }
    public static ArrayList<Image> initLisonImages(){
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        ArrayList<Image> lisonImages = new ArrayList<>();
        
        lisonImages.add(toolkit.getImage("SSMImages/Lison/S_F.png"));
        lisonImages.add(toolkit.getImage("SSMImages/Lison/S_B.png"));
        lisonImages.add(toolkit.getImage("SSMImages/Lison/R_1_F.png"));
        lisonImages.add(toolkit.getImage("SSMImages/Lison/R_1_B.png"));
        lisonImages.add(toolkit.getImage("SSMImages/Lison/R_2_F.png"));
        lisonImages.add(toolkit.getImage("SSMImages/Lison/R_2_B.png"));
        lisonImages.add(toolkit.getImage("SSMImages/Lison/J_F.png"));
        lisonImages.add(toolkit.getImage("SSMImages/Lison/J_B.png"));
        lisonImages.add(toolkit.getImage("SSMImages/Lison/K_F.png"));
        lisonImages.add(toolkit.getImage("SSMImages/Lison/K_B.png"));
        lisonImages.add(toolkit.getImage("SSMImages/Lison/L_C_F.png"));
        lisonImages.add(toolkit.getImage("SSMImages/Lison/L_C_B.png"));
        lisonImages.add(toolkit.getImage("SSMImages/Lison/L_1.png"));
        lisonImages.add(toolkit.getImage("SSMImages/Lison/L_2.png"));

        return lisonImages;
    }
    public static ArrayList<Image> initObamaImages(){
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        ArrayList<Image> ObamaImages = new ArrayList<>();
        
        ObamaImages.add(toolkit.getImage("SSMImages/Obama/S_F.png"));
        ObamaImages.add(toolkit.getImage("SSMImages/Obama/S_B.png"));
        ObamaImages.add(toolkit.getImage("SSMImages/Obama/R_1_F.png"));
        ObamaImages.add(toolkit.getImage("SSMImages/Obama/R_1_B.png"));
        ObamaImages.add(toolkit.getImage("SSMImages/Obama/R_2_F.png"));
        ObamaImages.add(toolkit.getImage("SSMImages/Obama/R_2_B.png"));
        ObamaImages.add(toolkit.getImage("SSMImages/Obama/J_F.png"));
        ObamaImages.add(toolkit.getImage("SSMImages/Obama/J_B.png"));
        ObamaImages.add(toolkit.getImage("SSMImages/Obama/K.png"));
        ObamaImages.add(toolkit.getImage("SSMImages/Obama/K.png"));
        ObamaImages.add(toolkit.getImage("SSMImages/Obama/L_F.png"));
        ObamaImages.add(toolkit.getImage("SSMImages/Obama/L_B.png"));
        ObamaImages.add(toolkit.getImage("SSMImages/Obama/L_F.png"));
        ObamaImages.add(toolkit.getImage("SSMImages/Obama/L_B.png"));

        return ObamaImages;
    }
    public static ArrayList<Image> initEmiImages(){
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        ArrayList<Image> EmiImages = new ArrayList<>();
        
        EmiImages.add(toolkit.getImage("SSMImages/Emi/S_F.png"));
        EmiImages.add(toolkit.getImage("SSMImages/Emi/S_B.png"));
        EmiImages.add(toolkit.getImage("SSMImages/Emi/R_1_F.png"));
        EmiImages.add(toolkit.getImage("SSMImages/Emi/R_1_B.png"));
        EmiImages.add(toolkit.getImage("SSMImages/Emi/R_2_F.png"));
        EmiImages.add(toolkit.getImage("SSMImages/Emi/R_2_B.png"));
        EmiImages.add(toolkit.getImage("SSMImages/Emi/J_F.png"));
        EmiImages.add(toolkit.getImage("SSMImages/Emi/J_B.png"));
        EmiImages.add(toolkit.getImage("SSMImages/Emi/K_F.png"));
        EmiImages.add(toolkit.getImage("SSMImages/Emi/K_B.png"));
        EmiImages.add(toolkit.getImage("SSMImages/Emi/L_C_F.png"));
        EmiImages.add(toolkit.getImage("SSMImages/Emi/L_C_B.png"));
        EmiImages.add(toolkit.getImage("SSMImages/Emi/L_F.png"));
        EmiImages.add(toolkit.getImage("SSMImages/Emi/L_B.png"));

        return EmiImages;
    }
    public static ArrayList<Image> initLawrenceImages(){
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        ArrayList<Image> lawrenceImages = new ArrayList<>();
        
        lawrenceImages.add(toolkit.getImage("SSMImages/Lawrence/S_F.png"));
        lawrenceImages.add(toolkit.getImage("SSMImages/Lawrence/S_B.png"));
        lawrenceImages.add(toolkit.getImage("SSMImages/Lawrence/R_F_1.png"));
        lawrenceImages.add(toolkit.getImage("SSMImages/Lawrence/R_B_1.png"));
        lawrenceImages.add(toolkit.getImage("SSMImages/Lawrence/R_F_2.png"));
        lawrenceImages.add(toolkit.getImage("SSMImages/Lawrence/R_B_2.png"));
        lawrenceImages.add(toolkit.getImage("SSMImages/Lawrence/J_F.png"));
        lawrenceImages.add(toolkit.getImage("SSMImages/Lawrence/J_B.png"));
        lawrenceImages.add(toolkit.getImage("SSMImages/Lawrence/K_F.png"));
        lawrenceImages.add(toolkit.getImage("SSMImages/Lawrence/K_B.png"));
        lawrenceImages.add(toolkit.getImage("SSMImages/Lawrence/L_C_F.png"));
        lawrenceImages.add(toolkit.getImage("SSMImages/Lawrence/L_C_B.png"));
        lawrenceImages.add(toolkit.getImage("SSMImages/Lawrence/L_F.png"));
        lawrenceImages.add(toolkit.getImage("SSMImages/Lawrence/L_B.png"));
        
        return lawrenceImages;
    }
    public static ArrayList<Image> initNeelImages(){
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        ArrayList<Image> neelImages = new ArrayList<>();
                
        neelImages.add(toolkit.getImage("SSMImages/Neel/S_F.png"));
        neelImages.add(toolkit.getImage("SSMImages/Neel/S_B.png"));
        neelImages.add(toolkit.getImage("SSMImages/Neel/R_F_1.png"));
        neelImages.add(toolkit.getImage("SSMImages/Neel/R_B_1.png"));
        neelImages.add(toolkit.getImage("SSMImages/Neel/R_F_2.png"));
        neelImages.add(toolkit.getImage("SSMImages/Neel/R_B_2.png"));
        neelImages.add(toolkit.getImage("SSMImages/Neel/J_F.png"));
        neelImages.add(toolkit.getImage("SSMImages/Neel/J_B.png"));
        neelImages.add(toolkit.getImage("SSMImages/Neel/K_F.png"));
        neelImages.add(toolkit.getImage("SSMImages/Neel/K_B.png"));
        neelImages.add(toolkit.getImage("SSMImages/Neel/L_C_F.png"));
        neelImages.add(toolkit.getImage("SSMImages/Neel/L_C_B.png"));
        neelImages.add(toolkit.getImage("SSMImages/Neel/L_F.png"));
        neelImages.add(toolkit.getImage("SSMImages/Neel/L_B.png"));
        
        return neelImages;
    }
    
    //----------------------------------------
    //Attacks and Abilities
    //----------------------------------------
    public void doJAttack(){
        if(getCharacter() == UMER || character == KAUSHAL || character == SALOME 
                || character == EMI || character == LAWRENCE){
            myPunch = new Punch((int)getX(),(int)getY(),getDirection(),getTeam(),2, true);
            if(character == EMI){
                myPunch.setSize(180,5);
                myPunch.setPunchCD(myPunch.getPunchCD()*2);
            } else if(character == LAWRENCE){
                myPunch.setSize(60,5);
                myPunch.setPunchCD(myPunch.getPunchCD()*1.25);
            }
        }
        else if(character == MATEI){
            Projectile myProj = new Projectile((int)getX()+getW()/2,(int)getY()+getH()/4,
                    10,10,getDirection(),getTeam(), false);
            pList.add(myProj);
        } else if(character == OBAMA){
            myRocket = new Rocket((int)getX()+getW()/2,(int)getY()-20+character*3,getDirection(),getTeam());
            myRocket.setXVel(myRocket.getXVel());
        }else if(character == NEEL){
            if(direction == Projectile.LEFT){
                Projectile myProj = new Projectile((int)getX()+getW()/2-40,(int)getY()+getH()/4-10,
                        65,10,getDirection(),getTeam(), false);
                pList.add(myProj);
            } else {
                Projectile myProj = new Projectile((int)getX()+getW()/2,(int)getY()+getH()/4-10,
                        65,10,getDirection(),getTeam(), false);
                pList.add(myProj);
            }
        } else {
            Projectile myProj = new Projectile((int)getX()+getW()/2,(int)getY()+getH()/4-10,
                    10,10,getDirection(),getTeam(), false);
            pList.add(myProj);
        }
        
        isHealing = false;
    }
    public void animateAndDrawJAttack(ArrayList<Player> playerList, ArrayList<Projectile> enemyPList,
            Player enemy, Graphics g, ImageObserver io){
        if(myPunch!=null){
            myPunch.draw(this, g,io);
            myPunch.animate(this, enemy);
            if(!myPunch.getCanHurt())
                myPunch = null;
        }
        if(enemyPunch != null){
            enemyPunch.draw(enemy,g,io);
            enemyPunch.animate(enemy,this);
        }

        for(int i=pList.size()-1;i>=0;i--){
            if(pList.get(i)!= null){
                Projectile p = pList.get(i);
                if(!p.playedSFX() && (character == MATEI 
                        || character == SALOME || character == JACK)){
                    Projectile.playSFX();
                    p.setPlayedSFX(true);
                }
                if(getCharacter() == ADAM)
                    p.setColor(Color.red);
                else if(getCharacter() == LISON)
                    p.setColor(new Color(3,190,252));
                else
                    p.setColor(Color.gray);
                p.draw(this, g, io);
                p.animateDamage(playerList, this);
                p.animateMovement(enemy);
                if(p.isNull())
                    pList.remove(p);
            }
            else
                pList.remove(i);
        }
        for(Projectile p: enemyPList){
            if(p!= null){
                if(!p.playedSFX() && (enemy.getCharacter() == MATEI 
                        || enemy.getCharacter() == SALOME || enemy.getCharacter() == JACK)){
                    Projectile.playSFX();
                    p.setPlayedSFX(true);
                }
                if(enemy.getCharacter() == ADAM)
                    p.setColor(Color.red);
                else if(enemy.getCharacter() == LISON)
                    p.setColor(new Color(3,190,252));
                else
                    p.setColor(Color.gray);
                p.draw(enemy, g, io);
                p.animateDamage(playerList, enemy);
            }
        }

    }
    
    public void doKAttack(){
        //Umer Vertical Punch
        if(getCharacter() == UMER){
            myVPunch = new VerticalPunch((int)getX(),(int)getY(), getDirection(), getTeam(),2,true);
        }
        //Matei Lison and Neel
        else if(getCharacter() == MATEI || character == LISON || character == NEEL){
            myRocket = new Rocket((int)getX()+getW()/2,(int)getY()-20+character*3,getDirection(),getTeam());
        } 
        //Adam Lightning
        else if(getCharacter() == ADAM){
            myLightning = new Lightning((int)getX(),(int)getY(),getDirection(),getTeam(),1, false,1.9);
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
                    10,10,getDirection(),getTeam(), false);
            pList.add(myProj);
        } 
        //Spock Raining Code
        else if(character == SPOCK){
           myRain = new RainingCode(-800);
        } 
        //Obama Secret Service
        else if(character == OBAMA){
            Projectile bg1 = new Projectile((int)getX(),(int)getY(),
                    getW(),getH(),Projectile.LEFT,getTeam(), false);
            Projectile bg2 = new Projectile((int)getX(),(int)getY(),
                    getW(),getH(),Projectile.RIGHT,getTeam(), false);
            pList.add(bg1); pList.add(bg2);
        } 
        //Emi Confusion
        else if(character == EMI){
            Projectile myProj = new Projectile((int)getX()+getW()/2,(int)getY()+getH()/4-10,
                    30,45,getDirection(),getTeam(), false);
            pList.add(myProj);
        } 
        //Lawrence Boomerang
        else if(character == LAWRENCE){
                myBoomerang = new Boomerang((int)getX(),(int)getY(),getDirection(),
                        (int)getX()+getDirection()*800, false, getTeam(), false, 1);
        }
    }
    public void animateAndDrawKAttack(ArrayList<Player> players,Player enemy, 
            Graphics g, ImageObserver io){

        if(myVPunch!=null){
            myVPunch.draw(this, g,io);
            myVPunch.animate(this, enemy);
            if(!myVPunch.getCanHurt())
                myVPunch = null;
        }
        if(enemyVPunch!=null){
            enemyVPunch.draw(enemy,g,io);
            enemyVPunch.animate(enemy,this);
        }
        
        if(myRocket!=null){
            myRocket.draw(g,io,this);
            myRocket.animateDamage(players,this);
            myRocket.animateMovement();
        }
        if(enemyRocket != null){
            enemyRocket.draw(g,io,enemy);
            enemyRocket.animateDamage(players,enemy);
        }
        if(myLightning != null){
            myLightning.setDirection(direction);
            myLightning.animate(this,enemy);
            myLightning.draw(this,g, io);
            if(myLightning.isNull())
                myLightning = null;
        }
        if(enemyLightning != null){
            enemyLightning.animate(enemy,this);
            enemyLightning.draw(enemy,g, io);
        }
        
        if(myMoto != null){
            myMoto.setDirection(direction);
            myMoto.animate(this,enemy);
            myMoto.draw(this,g, io);
            if(myMoto.isNull())
                myMoto = null;
        }
        if(enemyMoto != null){
            enemyMoto.animate(enemy,this);
            enemyMoto.draw(this,g, io);
            if(enemyMoto.isNull())
                enemyMoto = null;
        }
        if(isHealing){
            if(getPercentage() > 0)
                setPercentage(getPercentage()-0.3);
            if(getXVel() < -3 || getXVel() > 3)
                isHealing = false;
        }
        if(myRain != null){
            myRain.animate(enemy,this);
            myRain.draw(g,io,this);
	    if(myRain.getY()>=700)
		myRain = null;
        }
        if(enemyRain != null){
            enemyRain.animate(this,enemy);
            enemyRain.draw(g,io,enemy);
        }
        
        if(myBoomerang != null){
            myBoomerang.animateDamage(players, this);
            myBoomerang.animateMovement(this);
            myBoomerang.draw(g, io);
            if(myBoomerang.isNull())
                myBoomerang = null;
        }
        if(enemyBoomerang != null){
            enemyBoomerang.animateDamage(players, enemy);
            enemyBoomerang.draw(g, io);
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
            myLAttack = new GrowingLAttack((int)getX(),(int)getY()+getH()/2-size/2-17,direction,getTeam(),size);
            lAttackTimer = 0;
        } else if(character == JACK){
            setYVel(-lAttackStrength*60);
            lAttackTimer = 0;
        } else if(character == KAUSHAL){
            myStick = new Stick((int)getX(),(int)getY(),direction,getTeam(), false, lAttackStrength * 3);
            lAttackTimer *= 2;
        } else if(character == SALOME){
            setXVel(lAttackStrength*300*direction);
            lAttackCooldown = 1.5;
            lAttackTimer -= 0.1;
        } else if(character == SPOCK || character == EMI){
            myPunch = new Punch((int)getX(),(int)getY(),direction,getTeam(),lAttackStrength*60,true);
            myPunch.setSize(1100,200);
            lAttackCooldown = 9;
        } else if(character == LISON){
            lAttackTimer = 2;
            lAttackCooldown = 7;
        } else if(character == OBAMA){
            myRain=new RainingCode(-600);
            lAttackTimer *= 3;
            lAttackCooldown = 8;
        } else if(character == LAWRENCE){
            myLightning = new Lightning((int)getX(),(int)getY(),getDirection(),getTeam(),1, false,1.9);
            lAttackTimer = 1.9;
            lAttackCooldown = 5.0;
        } else if(character == NEEL){
            if(direction == Projectile.LEFT){
                Projectile myProj = new Projectile((int)getX()+getW()/2-40,(int)getY()+getH()/4-10,
                        81,13,getDirection(),getTeam(), false);
                pList.add(myProj);
            } else {
                Projectile myProj = new Projectile((int)getX()+getW()/2,(int)getY()+getH()/4-10,
                        81,13,getDirection(),getTeam(), false);
                pList.add(myProj);
            }
            lAttackCooldown = 5.0;
        }
    }
    public void animateAndDrawLAttack(ArrayList<Player> players,Player enemy,
            Graphics g, ImageObserver io){
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
        
        if(getCharacter() == MATEI){
            if(lAttackTimer > 0){
                Projectile myProj;
                if(direction == Projectile.RIGHT)
                    myProj = new Projectile((int)getX()+getW(),(int)getY()+4+getH()/4,
                            9,9,getDirection(),getTeam(), false);
                else
                    myProj = new Projectile((int)getX()-10,(int)getY()+4+getH()/4,
                            9,9,getDirection(),getTeam(), false);
                
                pList.add(myProj);
            }
        }
        if(getCharacter() == LISON){
            if(lAttackTimer > 0){
                setSize(116,90);
                setXVel(8*direction);
                
                if(this.getHitBox().intersects(enemy.getHitBox())
                        && !team.equals(enemy.getTeam())
                        && !enemy.isUntargetable()){
                    enemy.setPercentage(enemy.getPercentage()+0.3);
                    enemy.setXVel((2.5+4*enemy.getPercentage()/25)*direction);
                }
            } else
                setSize(60,90);
        }
        if(enemy.getCharacter() == LISON){
            if(enemy.getLAttackTimer() > 0){                
                if(enemy.getHitBox().intersects(getHitBox())){
                    setPercentage(getPercentage()+0.3);
                    setXVel((2.5+4*getPercentage()/25)*enemy.getDirection());
                }
            }
        }
        if(myLAttack!=null){
            //ADAM
            if(character == ADAM)
                myLAttack.setImage(GrowingLAttack.SUN);
            //UMER
            else if(character == UMER && direction == Projectile.RIGHT)
                myLAttack.setImage(GrowingLAttack.HEAD_F);
            else if(character == UMER && direction == Projectile.LEFT)
                myLAttack.setImage(GrowingLAttack.HEAD_B);
            
            myLAttack.animateDamage(players);
            myLAttack.animateMovement();
            myLAttack.draw(g,io);
        }
        if(enemyLAttack!=null){
            if(enemy.getCharacter() == ADAM)
                enemyLAttack.setImage(GrowingLAttack.SUN);
            else if(enemy.getCharacter() == UMER && enemy.getDirection() == Projectile.RIGHT)
                enemyLAttack.setImage(GrowingLAttack.HEAD_F);
            else if(enemy.getCharacter() == UMER && enemy.getDirection() == Projectile.LEFT)
                enemyLAttack.setImage(GrowingLAttack.HEAD_B);
            enemyLAttack.animateDamage(players);
            enemyLAttack.draw(g,io);
        }
        
        if(myStick != null){
            myStick.animate(this,enemy);
            myStick.draw(this,g, io);
        }
        if(enemyStick != null){
            enemyStick.animate(enemy,this);
            enemyStick.draw(this,g, io);
        }
        
        if(isDashing() && getHitBox().intersects(enemy.getHitBox())){
            enemy.setPercentage(enemy.getPercentage()+5.5);
            enemy.setXVel(1.5*getXVel()/60*(0.5+1.5*enemy.getPercentage()/20));
            enemy.setYVel(.5*(enemy.getYVel()-1.5-2*enemy.getPercentage()/50));
        }
        if(enemyIsDashing && enemy.getHitBox().intersects(getHitBox())){
            this.setPercentage(this.getPercentage()+5.5);
            this.setXVel(1.5*enemy.getXVel()/60*(0.5+1.5*this.getPercentage()/20));
            this.setYVel(.5*(this.getYVel()-1.5-2*this.getPercentage()/50));
        }
    }
    
    
    //----------------------------------------
    //Packing and unpacking players
    //----------------------------------------
    
    private static String parseChar = ",";
    
    public static String pack(Player me){
        String packedPlayersInfo = "";
        
        packedPlayersInfo += me.getX()+parseChar;
        packedPlayersInfo += me.getY()+parseChar;
        packedPlayersInfo += me.getPercentage()+parseChar;
        packedPlayersInfo += me.getCharacter()+parseChar;
        packedPlayersInfo += me.getPlayerName()+parseChar;
        packedPlayersInfo += me.getLives()+parseChar;
        packedPlayersInfo += me.getDirection()+parseChar;
                
        if(me.getPunch() != null){
            packedPlayersInfo += me.getPunch().getPunchCD() + parseChar;
            packedPlayersInfo += me.getPunch().getCanHurt() + parseChar;
        }else{
            packedPlayersInfo += "null" + parseChar;
            packedPlayersInfo += "false" + parseChar;
        }
        
        packedPlayersInfo += me.isTaunting()+parseChar;

        if(me.getRocket() != null){
            packedPlayersInfo += me.getRocket().getX() + parseChar;
            packedPlayersInfo += me.getRocket().getY() + parseChar;
            packedPlayersInfo += me.getRocket().getDir() + parseChar;
        }else{
            packedPlayersInfo += "null" + parseChar;
            packedPlayersInfo += "null" + parseChar;
            packedPlayersInfo += "null" + parseChar;
        }
        
        if(me.getVPunch() != null){
            packedPlayersInfo += me.getVPunch().getPunchCD() + parseChar;
            packedPlayersInfo += me.getVPunch().getCanHurt() + parseChar;
        } else {
            packedPlayersInfo += "null" + parseChar;
            packedPlayersInfo += "false" + parseChar;
        }
        
        packedPlayersInfo += me.getMyImageIndex() + parseChar;
        
        if(me.getLightning() != null){
            packedPlayersInfo += me.getLightning().getDrawTimer() + parseChar;
            packedPlayersInfo += me.getLightning().isNull() + parseChar;
            packedPlayersInfo += me.getLightning().getTimer() + parseChar;
        } else {
            packedPlayersInfo += "null" + parseChar;
            packedPlayersInfo += "true" + parseChar;
            packedPlayersInfo += "null" + parseChar;
        }
        
        if(me.getLAttack() != null){
            packedPlayersInfo += me.getLAttack().getX() + parseChar;
            packedPlayersInfo += me.getLAttack().getY() + parseChar;
            packedPlayersInfo += me.getLAttack().getDir() + parseChar;
            packedPlayersInfo += me.getLAttack().getSizeAndDamage() + parseChar;
        }
        else{
            packedPlayersInfo += "null" + parseChar;
            packedPlayersInfo += "null" + parseChar;
            packedPlayersInfo += "null" + parseChar;
            packedPlayersInfo += "null" + parseChar;
        }
        if(me.getMoto() != null)
            packedPlayersInfo += "false" + parseChar;
        else
            packedPlayersInfo += "true" + parseChar;
        
        packedPlayersInfo += me.getW()+parseChar;
        packedPlayersInfo += me.getH()+parseChar;
        
        if(me.getStick() != null){
            packedPlayersInfo += me.getStick().getStrength() + parseChar;
        } else {
            packedPlayersInfo += "null" + parseChar;
        }
                
        packedPlayersInfo += me.getStunDuration() + parseChar;
        packedPlayersInfo += me.isDashing() + parseChar;
        packedPlayersInfo += me.getXVel() + parseChar;
        packedPlayersInfo += me.isBoss() + parseChar;
        
        if(me.getRain()!=null)
            packedPlayersInfo += me.getRain().getY() + parseChar;
        else
            packedPlayersInfo += "null" + parseChar;
        
        packedPlayersInfo += me.getConfusionDuration() + parseChar;
        
        if(me.getPunch() != null){
            packedPlayersInfo += me.getPunch().getW() + parseChar;
            packedPlayersInfo += me.getPunch().getH() + parseChar;
            packedPlayersInfo += me.getPunch().getDirection() + parseChar;
        }else{
            packedPlayersInfo += "null" + parseChar;
            packedPlayersInfo += "false" + parseChar;
            packedPlayersInfo += "false" + parseChar;
        }
        
        packedPlayersInfo += me.getLAttackTimer() + parseChar;
        
        if(me.getBoomerang() != null){
            packedPlayersInfo += (int)me.getBoomerang().getX() + parseChar;
            packedPlayersInfo += (int)me.getBoomerang().getY() + parseChar;
            packedPlayersInfo += me.getBoomerang().getDirection() + parseChar;
            packedPlayersInfo += me.getBoomerang().getFinalX() + parseChar;
            packedPlayersInfo += me.getBoomerang().isReturning() + parseChar;
            packedPlayersInfo += me.getBoomerang().isNull() + parseChar;
            packedPlayersInfo += me.getBoomerang().getDrawTimer() + parseChar;
        } else {
            packedPlayersInfo += "null" + parseChar;
            packedPlayersInfo += "null" + parseChar;
            packedPlayersInfo += "null" + parseChar;
            packedPlayersInfo += "null" + parseChar;
            packedPlayersInfo += "false" + parseChar;
            packedPlayersInfo += "false" + parseChar;
            packedPlayersInfo += "null" + parseChar;
        }
        
        packedPlayersInfo += me.getFlameDuration() + parseChar;
        
        return packedPlayersInfo;
    }
    public static void unPack(String s, Player enemy, Player me){
        
        if(testPrintTimer < 0){
            testPrintTimer = 1;
            System.out.println(s);
        } else
            testPrintTimer -=1.0/60;
        
        String[] playersInfo = s.split(parseChar);
        
        enemy.setX(Double.parseDouble(playersInfo[0]));        
        enemy.setY(Double.parseDouble(playersInfo[1]));
        enemy.setPercentage(Double.parseDouble(playersInfo[2]));
        enemy.setCharacter(Integer.parseInt(playersInfo[3]));
        enemy.setPlayerName(playersInfo[4]);
        enemy.setLives(Integer.parseInt(playersInfo[5]));
        enemy.setDirection(Integer.parseInt(playersInfo[6]));
        
        if(!playersInfo[7].equals("null")){
            me.setEnemyPunch(new Punch((int)enemy.getX(),(int)enemy.getY(),
                    enemy.getDirection(),enemy.getTeam(),Double.parseDouble(playersInfo[7]), 
                    Boolean.parseBoolean(playersInfo[8])));
        }
        else{
            me.setEnemyPunch(null);
        }
        
        enemy.setTaunting(Boolean.parseBoolean(playersInfo[9]));

        if(!playersInfo[10].equals("null")){
            me.setEnemyRocket(new Rocket((int)Double.parseDouble(playersInfo[10]), 
                    (int)Double.parseDouble(playersInfo[11]), 
                    Integer.parseInt(playersInfo[12]), enemy.getTeam()));
        }else{
            me.setEnemyRocket(null);
        }
        
        if(!playersInfo[13].equals("null")){
            me.setEnemyVPunch(new VerticalPunch((int)enemy.getX(),(int)enemy.getY(),
                    enemy.getDirection(), enemy.getTeam(),Double.parseDouble(playersInfo[13]),
                    Boolean.parseBoolean(playersInfo[14])));
        } else {
            me.setEnemyVPunch(null);
        }
        
        enemy.setMyImageIndex(Integer.parseInt(playersInfo[15]));
        
        if(!playersInfo[16].equals("null")){
            me.setEnemyLightning(new Lightning((int)enemy.getX(), (int)enemy.getY(),
                    enemy.getDirection(), enemy.getTeam(), Double.parseDouble(playersInfo[16]),
                    Boolean.parseBoolean(playersInfo[17]), Double.parseDouble(playersInfo[18])));
        }
        
        if(!playersInfo[19].equals("null")){
            me.setEnemyLAttack(new GrowingLAttack((int)Double.parseDouble(playersInfo[19]), 
                    (int)Double.parseDouble(playersInfo[20]),Integer.parseInt(playersInfo[21]), 
                    enemy.getTeam(), Integer.parseInt(playersInfo[22])));
        } else
            me.setEnemyLAttack(null);
        
        if(playersInfo[23].equals("false")){
            me.setEnemyMoto(new Motorcycle((int)enemy.getX(),(int)enemy.getY(),enemy.getDirection(),
                    enemy.getTeam(), Boolean.parseBoolean(playersInfo[23])));
        } else
            me.setEnemyMoto(null);
        
        enemy.setSize(Integer.parseInt(playersInfo[24]),Integer.parseInt(playersInfo[25]));
        
        if(!playersInfo[26].equals("null")){
            me.setEnemyStick(new Stick((int)enemy.getX(),(int)enemy.getY(),enemy.getDirection(),
                    enemy.getTeam(), false, Double.parseDouble(playersInfo[26])));
        } else
            me.setEnemyStick(null);
        
        enemy.setStunDuration(Double.parseDouble(playersInfo[27]));
        me.setEnemyDashing(Boolean.parseBoolean(playersInfo[28]));
        enemy.setXVel(Double.parseDouble(playersInfo[29]));
        enemy.setIsBoss(Boolean.parseBoolean(playersInfo[30]));
        
        if(!playersInfo[31].equals("null")){
            me.setEnemyRain(new RainingCode((int)Double.parseDouble(playersInfo[31])));
        } else
            me.setEnemyRain(null);
        
        enemy.setConfusionDuration(Double.parseDouble(playersInfo[32]));
        
        if(!playersInfo[33].equals("null")){
            me.enemyPunch.setSize(Integer.parseInt(playersInfo[33]), Integer.parseInt(playersInfo[34]));
            me.enemyPunch.setDirection(Integer.parseInt(playersInfo[35]));
        }
        else{
            me.setEnemyPunch(null);
        }
        
        enemy.setLAttackTimer(Double.parseDouble(playersInfo[36]));
        
        if(!playersInfo[37].equals("null")){
            me.setEnemyBoomerang(new Boomerang(Integer.parseInt(playersInfo[37]), Integer.parseInt(playersInfo[38]),
                Integer.parseInt(playersInfo[39]),Integer.parseInt(playersInfo[40]), Boolean.parseBoolean(playersInfo[41]),
                enemy.getTeam(), Boolean.parseBoolean(playersInfo[42]), Double.parseDouble(playersInfo[43])));
        } else {
            me.setEnemyBoomerang(null);
        }
        
        enemy.setFlameDuration(Double.parseDouble(playersInfo[44]));
        
    }    
}

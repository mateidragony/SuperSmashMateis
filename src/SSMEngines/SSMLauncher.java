package SSMEngines;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

public class SSMLauncher extends JPanel implements MouseListener, MouseMotionListener {

    private int mouseX;
    private int mouseY;

    private int playerMode;

    private JFrame helpFrame;
    private JFrame newsFrame;
    private JFrame inputFrame;
    private JTextField nameInput;

    private JTextField ipInput;

    private String patchNotes;
    private String helpNotes;
    private String ipAddress = " . . ";
    private String playerName = "Player Dwo";

    private boolean shouldLaunch;

    public SSMLauncher(){

        mouseX = 0;
        mouseY = 0;

        this.setPreferredSize(new Dimension(700,500));
        this.addMouseListener(this);
        this.addMouseMotionListener(this);

        initStrings();
        initHelpFrame();
        initNewsFrame();
        initInputFrame();
    }


    public void paintComponent(Graphics g){

        Toolkit toolkit = Toolkit.getDefaultToolkit();

        Image bg = toolkit.getImage("SSMImages/launcherScreen.png");
        Image fg = toolkit.getImage("SSMImages/launcherScreenFG.png");
        Image bigManButt = toolkit.getImage("SSMImages/launchButton.png");
        g.drawImage(bg,-2,0,702,500,this);
        drawMouseMovements(g);
        g.drawImage(fg,-2,0,702,500,this);

        g.setColor(Color.red);
        g.drawString("X: "+mouseX+", Y: "+mouseY + " PlayerMode: "+playerMode
                + "     "+ipAddress + "  len: ",10,10);

        g.setColor(Color.black);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 25));


        if(playerMode > 1) {
            ipInput.setVisible(true);
        } else {
            ipInput.setVisible(false);
        }
        inputFrame.repaint();

        ipAddress = ipInput.getText();
        playerName = nameInput.getText();

        if(readyToLaunch())
            g.drawImage(bigManButt,350-99,385,198,68,this);

    }


    public boolean shouldLaunch(){return shouldLaunch;}
    public int getPlayerMode(){return playerMode;}
    public String getIP(){return ipAddress;}
    public String getPlayerName(){return playerName;}


    public void handleMouseClicks(){

        ArrayList<Rectangle> rects = new ArrayList<>();

        rects.add(new Rectangle(57,221,103,103));
        rects.add(new Rectangle(217,221,103,103));
        rects.add(new Rectangle(379,221,103,103));
        rects.add(new Rectangle(539,221,103,103));

        Ellipse2D help = new Ellipse2D.Float(36,118,64,64);
        Ellipse2D news = new Ellipse2D.Float(603,118,64,64);

        for(int i=0; i<rects.size(); i++){
            if(rects.get(i).contains(mouseX,mouseY)) {
                playerMode = i + 1;
                ipInput.setText("IP");
            }
        }

        if(help.contains(mouseX,mouseY)) {
            helpFrame.setVisible(true);
            helpFrame.toFront();
            helpFrame.requestFocus();
        }
        if(news.contains(mouseX,mouseY)) {
            newsFrame.setVisible(true);
            newsFrame.toFront();
            newsFrame.requestFocus();
        }

        if(new Rectangle(350-99,385,198,68).contains(mouseX,mouseY) && readyToLaunch())
            shouldLaunch = true;
    }
    public void drawMouseMovements(Graphics graph){

        Graphics2D g = (Graphics2D)graph;

        ArrayList<Rectangle> rects = new ArrayList<>();

        rects.add(new Rectangle(53,218,111,109));
        rects.add(new Rectangle(213,218,110,109));
        rects.add(new Rectangle(375,218,111,109));
        rects.add(new Rectangle(535,218,111,109));

        Ellipse2D help = new Ellipse2D.Float(31,114,73,73);
        Ellipse2D news = new Ellipse2D.Float(595,115,72,72);


        g.setColor(Color.red);
        if(playerMode > 0)
            g.fill(rects.get(playerMode-1));


        g.setColor(Color.black);
        for(int i=0; i<rects.size(); i++){
            Rectangle r = rects.get(i);

            if(r.contains(mouseX,mouseY))
                g.fill(r);
        }

        if(help.contains(mouseX,mouseY))
            g.fill(help);
        if(news.contains(mouseX,mouseY))
            g.fill(news);

    }

    public void mouseClicked(MouseEvent e) {
        handleMouseClicks();

        inputFrame.requestFocus();
    }
    public void mousePressed(MouseEvent e){}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseDragged(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    public void setFrameLocations(JFrame myFrame){
        inputFrame.setLocation(myFrame.getX()+260,myFrame.getY()+200);
    }

    public void initHelpFrame(){
        helpFrame = new JFrame("Help");
        helpFrame.setSize(new Dimension(400,500));
        helpFrame.setVisible(false);
        helpFrame.setResizable(false);
        helpFrame.setLocation(0,50);

        JTextArea titleTextArea = new JTextArea(1,12);
        titleTextArea.setEditable(false);
        titleTextArea.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        JScrollPane titleScrollPane = new JScrollPane(titleTextArea);

        JTextArea textArea = new JTextArea(22,10);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        JScrollPane scrollPane = new JScrollPane(textArea);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(titleScrollPane);
        panel.add(Box.createRigidArea(new Dimension(0,5)));
        panel.add(scrollPane);
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        textArea.setFont(new Font(Font.SERIF, Font.BOLD, 15));
        titleTextArea.setFont(new Font(Font.SERIF, Font.BOLD, 30));

        titleTextArea.setText("How to Play SSM");
        textArea.setText(helpNotes);
        helpFrame.getContentPane().add(panel);
        helpFrame.pack();
    }
    public void initNewsFrame(){
        newsFrame = new JFrame("Patch Notes");
        newsFrame.setSize(new Dimension(400,500));
        newsFrame.setVisible(false);
        newsFrame.setResizable(false);
        newsFrame.setLocation(1000,50);

        JTextArea titleTextArea = new JTextArea(1,12);
        titleTextArea.setEditable(false);
        titleTextArea.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        JScrollPane titleScrollPane = new JScrollPane(titleTextArea);

        JTextArea textArea = new JTextArea(22,10);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        JScrollPane scrollPane = new JScrollPane(textArea);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(titleScrollPane);
        panel.add(Box.createRigidArea(new Dimension(0,5)));
        panel.add(scrollPane);
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        textArea.setFont(new Font(Font.SERIF, Font.BOLD, 15));
        titleTextArea.setFont(new Font(Font.SERIF, Font.BOLD, 30));

        titleTextArea.setText("What's New in SSM?");
        textArea.setText(patchNotes);
        newsFrame.getContentPane().add(panel);
        newsFrame.pack();
    }
    public void initStrings(){
        patchNotes = """
                Patch 2.1.1
                Added a game launcher which allows players to choose between 1,2,3, and 4 player mode easily. Laucher also includes patch notes and help box.

                Patch 2.1.0
                Introducing 2 new fighters! Please welcome Lawrence the swordsman and Neel the bow master. Lawrence uses displays his master swordsmanship in battle by controling his enemies with precise jabs to knock them out. Lawrence also has a unique ability to throw his sword like a boomerang. This ability's cooldown is greatly reduced if Lawrence is able to catch it. Finally, Lawrence can also quickly thrust his sword backwards and forwards to hold his enemy in place. Neel shoots three types of arrows. Regular damaging arrows, freeze arrows, and fire arrows to obliterate his enemies.

                Patch 2.0.0
                Super Smash Mateis is back with a new, sleeker look. This patch was very small with only a new intro screen created, but big updates for SSM are planned. Plans include but are not limited to: Options for up to 4 player battles, many new fighters, items, sound effects, better GUI, and many more!""";

        helpNotes = """
                Welcome to Super Smash Mateis Java edition! SSM is a java based battling game with 10 unique characters to choose from. Each character has three different attacks: a basic attack, a special attack, and a charge up ultimate attack. The controls are explained on the next page.

                Playing on 1P mode doesn't require an IP adress. If you wish to play with other players, you will need to enter the IP adress of the computer that the server is hosted on.

                When you load in, press enter to continue to the waiting screen. When player 2 connects, press s to continue to the character select screen. Once you have chosen your character, click on the rectangle that says ready. When both players are ready, You will be able to\s
                choose which map you will battle on.\s

                Have fun!

                ============
                The Controls
                ============

                Movement:
                W to jump
                A to move left
                D to move right

                Attacks:
                J to basic attack
                K  to special attack
                Hold and release L to ultimate attack

                Special:
                S to taunt
                P to reset Dummy
                Don't press Z\s
                When playing solo, press enter to skip past waiting screen and character select

                Cheat Codes:
                You really thought I would tell you what the cheat codes are?

                ============
                The Fighters
                ============
                Matei the gunman
                Matei was the first character added to the game. He is a gunman with many different weapons
                J Attack: Shoot your gun\s
                K Attack: Launch a rocket
                L Attack: Charge up and fire your minigun

                Umer the brawler
                Umer is the greatest brawler in the west, east, north, and south. He is relentless. Some even call him a brawl star
                J Attack: Forward Punch
                K Attack: Uppercut
                L Attack: Charge up your inner power and     launch your head at the enemy

                Adam the dark mage
                No one knows where Adam’s powers come from. They just know that he uses his power with\s
                terrifying and malevolent precision
                J Attack: Lob a fireball
                K Attack: Trap your enemies with lighting
                L Attack: Charge up and launch the sun at your enemies

                Jack the mad biker
                He’s british and he has a bike. Nothing else can strike more fear into an enemy
                J Attack: Shoot your gun
                K Attack: Ride your motorcycle and run your enemies over
                L Attack: Charge up a super jump

                Kaushal the wandering wise man
                A wise man once said,”Are you using a biomechanical weapon? That's cheating!” Guess who.
                J Attack: Jab the enemy with your stick
                K Attack: Channel your inner life force and heal
                L attack: Charge up and stun the enemy  with your ancient earthly weapon with powers beyond\s
                your imagination

                Salome (Bob) the assassin
                She will kill you. That’s it. No remorse, no guilt, just business
                J Attack: Kick the target
                K Attack: Shoot the target
                L Attack: Charge up and dash through the target

                Mr. Spock the Computer Scientist
                Mr. Spock could literally code himself to one shot you. There’s absolutely no way to defeat this man
                J Attack: Construct a clone of yourself to attack the enemy
                K Attack: Call down the matrix upon your enemy
                L Attack: MASSIVE LASER!

                Lison the Ice Witch
                Have fun spending half the battle frozen in ice. Lison is as cold as ice… get it?.. She’s the ice witch
                J Attack: Throw a snowball that somehow deals as much damage as a bullet
                K Attack: Encase your enemy in a block of ice
                L Attack (no charge up): Surround yourself in a storm of ice and snow

                Obama the ex-president
                He was the president of the US of A, but now… He’s in SSM.\s
                J Attack: Send out the eagle of freedom to kill
                K Attack: Cower behind secret service agents
                L Attack (no charge up): Call down the white house to crush your enemies

                Emi the Jumex Jemi
                Jumex, Jemi, and Belbiba. No one can stop Emi when he has the Sacred Triumvirate
                J Attack: Elastijemi. Stretch your lean and fit body to slap the enemy
                K Attack: Send forth a perfect replica of your face to confuse the enemy with its beauty
                L Attack: Chug down a succulent bottle of mango Jumex and spit it out at the enemy in a\s
                powerful vortex\s""";

    }
    public void initInputFrame(){
        inputFrame = new JFrame();
        inputFrame.setResizable(false);
        inputFrame.setVisible(false);
        inputFrame.setUndecorated(true);
        inputFrame.setLocation(360,360);
        inputFrame.setSize(new Dimension(200,200));

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));

        nameInput = new JTextField();
        nameInput.setSize(new Dimension(150,30));
        nameInput.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
        nameInput.setFont(new Font(Font.SERIF, Font.PLAIN, 20));

        ipInput = new JTextField("IP");
        ipInput.setSize(new Dimension(150,30));
        ipInput.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
        ipInput.setFont(new Font(Font.SERIF, Font.PLAIN, 20));

        ipInput.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ipInput.setText("");
            }
        });

        panel.add(nameInput);
        panel.add(Box.createRigidArea(new Dimension(0,130)));
        panel.add(ipInput);

        inputFrame.add(panel);

        panel.setBackground(new Color(0,0,0,0));
        inputFrame.setBackground(new Color(0,0,0,0));
    }

    public boolean readyToLaunch(){
        return playerMode == 1 || (ipAddress.split("\\.").length == 4) || ipAddress.equals("localhost");
    }
    public void idkWhyIHaveToDoThisButThisIsKindaDum(){
        inputFrame.setVisible(true);
        inputFrame.toFront();
        inputFrame.requestFocus();
    }

    public void noVisible(){inputFrame.setVisible(false);}

    public static void main(String[] args){

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception ex){ ex.printStackTrace(); }

        SSMLauncher launcher = new SSMLauncher();

        JFrame myFrame = new JFrame("SSM Launcher");
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myFrame.setVisible(true);
        myFrame.add(launcher);
        myFrame.setResizable(false);
        myFrame.setSize(launcher.getPreferredSize());
        myFrame.setLocation(300,50);

        launcher.idkWhyIHaveToDoThisButThisIsKindaDum();


        Timer t = new Timer(30/1000, timerRepaint -> {
            myFrame.getComponent(0).repaint();
            launcher.setFrameLocations(myFrame);
        });

        t.start();
    }

}

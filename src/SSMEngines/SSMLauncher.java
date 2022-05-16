package SSMEngines;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SSMLauncher extends JPanel {

    private int playerMode;

    private JFrame helpFrame;
    private JFrame newsFrame;

    private HintTextField nameInput;
    private HintTextField ipInput;
    private JComboBox<String> playerModeSelector;
    private JCheckBox hostServer;
    private JButton helpButt;
    private JButton patchNotesButt;
    private JButton startButt;
    private JProgressBar loading;

    private int progress;
    private String patchNotes;
    private String helpNotes;
    private String ipAddress = " . . ";
    private String playerName = "Player Dwo";

    private boolean pressedStart;

    private final Image bg;
    private final Image bg2;

    public SSMLauncher() {

        this.setPreferredSize(new Dimension(800, 600));

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        bg = toolkit.getImage("SSMImages/launcherScreen.png");
        bg2 = toolkit.getImage("SSMImages/launcherScreenFG.png");

        initStrings();
        initHelpFrame();
        initNewsFrame();
        initInputFrame();

        setComponentLocations();
    }


    public void paintComponent(Graphics g) {

        //Image bigManButt = toolkit.getImage("SSMImages/launchButton.png");
        g.drawImage(bg, 0, 0, this);
        g.drawImage(bg2,249,118,this);

        g.setColor(Color.black);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 25));

        playerMode = playerModeSelector.getSelectedIndex();


        if(playerMode == 1) {
            hostServer.setSelected(true);
            hostServer.setEnabled(false);
        } else
            hostServer.setEnabled(true);

        if(playerMode == 1 || hostServer.isSelected()){
            ipInput.setText("localhost");
            ipInput.setForeground(Color.BLACK);
            ipInput.setFont(new Font("Tahoma", Font.PLAIN, 15));
            ipInput.setEnabled(false);
        } else{
            if(ipInput.getText().equals("localhost"))
                ipInput.resetHint();
            ipInput.setEnabled(true);
        }

        ipAddress = ipInput.getText();
        playerName = nameInput.getText();
        startButt.setVisible(readyToLaunch());
        loading.setValue(progress);

        if(pressedStart){
            loading.setVisible(true);

            ipInput.setEnabled(false);
            nameInput.setEnabled(false);
            hostServer.setEnabled(false);
            playerModeSelector.setEnabled(false);

            int addition = (int)(Math.random()*60)-30;
            addition = Math.max(addition, 0);
            progress+=addition;
        }

        setComponentLocations();
    }


    //public boolean shouldLaunch() {return loading.getPercentComplete() == 1;}
    public boolean shouldLaunch() {return pressedStart;}
    public int getPlayerMode() {return playerMode;}
    public String getIP() {return ipAddress;}
    public String getPlayerName() {return playerName;}
    public boolean hostServer(){return hostServer.isSelected();}

    public void setComponentLocations() {
        nameInput.setLocation(25, 190);
        ipInput.setLocation(25, 250);
        hostServer.setLocation(25,310);
        playerModeSelector.setLocation(25, 370);
        startButt.setLocation(25,430);
        loading.setLocation(25,515);

        patchNotesButt.setLocation(350,23);
        helpButt.setLocation(570,23);
    }

    public void initHelpFrame() {
        helpFrame = new JFrame("Help");
        helpFrame.setSize(new Dimension(400, 500));
        helpFrame.setVisible(false);
        helpFrame.setResizable(false);
        helpFrame.setLocation(0, 50);

        JTextArea titleTextArea = new JTextArea(1, 12);
        titleTextArea.setEditable(false);
        titleTextArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane titleScrollPane = new JScrollPane(titleTextArea);

        JTextArea textArea = new JTextArea(22, 10);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(textArea);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(titleScrollPane);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(scrollPane);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        textArea.setFont(new Font(Font.SERIF, Font.BOLD, 15));
        titleTextArea.setFont(new Font(Font.SERIF, Font.BOLD, 30));

        titleTextArea.setText("How to Play SSM");
        textArea.setText(helpNotes);
        helpFrame.getContentPane().add(panel);
        helpFrame.pack();
    }
    public void initNewsFrame() {
        newsFrame = new JFrame("Patch Notes");
        newsFrame.setSize(new Dimension(400, 500));
        newsFrame.setVisible(false);
        newsFrame.setResizable(false);
        newsFrame.setLocation(1000, 50);

        JTextArea titleTextArea = new JTextArea(1, 12);
        titleTextArea.setEditable(false);
        titleTextArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane titleScrollPane = new JScrollPane(titleTextArea);

        JTextArea textArea = new JTextArea(22, 10);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(textArea);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(titleScrollPane);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(scrollPane);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        textArea.setFont(new Font(Font.SERIF, Font.BOLD, 15));
        titleTextArea.setFont(new Font(Font.SERIF, Font.BOLD, 30));

        titleTextArea.setText("What's New in SSM?");
        textArea.setText(patchNotes);
        newsFrame.getContentPane().add(panel);
        newsFrame.pack();
    }
    public void initStrings() {
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
    public void initInputFrame() {
        String nameHint = "Enter Player Name";
        nameInput = new HintTextField(nameHint);
        nameInput.setPreferredSize(new Dimension(200, 40));
        nameInput.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        String ipHint = "Enter IP Address";
        ipInput = new HintTextField(ipHint);
        ipInput.setPreferredSize(new Dimension(200, 40));
        ipInput.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        playerModeSelector = new JComboBox<>(new String[]{"Player Mode", "Single Player", "2 Players", "3 players", "4 players"});
        playerModeSelector.setPreferredSize(new Dimension(200, 40));
        playerModeSelector.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        playerModeSelector.setFont(new Font("Tahoma", Font.PLAIN, 15));

        hostServer = new JCheckBox("Host the Game Server");
        hostServer.setPreferredSize(new Dimension(200, 40));
        hostServer.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        hostServer.setFont(new Font("Tahoma", Font.PLAIN, 15));

        patchNotesButt = new JButton("Patch Notes");
        patchNotesButt.setPreferredSize(new Dimension(180,60));
        patchNotesButt.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        patchNotesButt.setFont(new Font("Tahoma", Font.PLAIN, 20));
        patchNotesButt.addActionListener(e -> {
            newsFrame.setVisible(true);
            newsFrame.toFront();
            newsFrame.requestFocus();
        });

        helpButt = new JButton("How to Play");
        helpButt.setPreferredSize(new Dimension(180,60));
        helpButt.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        helpButt.setFont(new Font("Tahoma", Font.PLAIN, 20));
        helpButt.addActionListener(e -> {
            helpFrame.setVisible(true);
            helpFrame.toFront();
            helpFrame.requestFocus();
        });

        startButt = new JButton(new ImageIcon("SSMImages/launchButton.png"));
        startButt.setPreferredSize(new Dimension(200,74));
        startButt.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        startButt.setFont(new Font("Tahoma", Font.PLAIN, 20));
        startButt.setVisible(false);
        startButt.addActionListener(e -> pressedStart=true);
        startButt.setOpaque(false);
        startButt.setBorderPainted(false);

        loading = new JProgressBar();
        loading.setPreferredSize(new Dimension(200,25));
        loading.setMaximum(1000);
        loading.setVisible(false);

        this.add(playerModeSelector);
        this.add(nameInput);
        this.add(ipInput);
        this.add(hostServer);
        this.add(patchNotesButt);
        this.add(helpButt);
        this.add(startButt);
        this.add(loading);

        startButt.requestFocus();
    }

    public boolean readyToLaunch() {
        return (playerMode == 1 || (ipAddress.split("\\.").length == 4) || ipAddress.equals("localhost"))
                && playerMode != 0;
    }

//    public static void main(String[] args) {
//
//        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
//        SSMLauncher launcher = new SSMLauncher();
//
//        JFrame myFrame = new JFrame("SSM Launcher");
//        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        myFrame.setVisible(true);
//        myFrame.add(launcher);
//        myFrame.setResizable(false);
//        myFrame.setSize(launcher.getPreferredSize());
//        myFrame.setLocation(300, 50);
//
//        Timer t = new Timer(1000/60, timerRepaint -> myFrame.getComponent(0).repaint());
//
//        t.start();
//    }


    private static class HintTextField extends JTextField {

        Font gainFont = new Font("Tahoma", Font.PLAIN, 15);
        Font lostFont = new Font("Tahoma", Font.ITALIC, 15);

        String hintText;

        public void resetHint(){
            setText(hintText);
            setFont(lostFont);
            setForeground(Color.GRAY);
        }


        public HintTextField(final String hint) {

            hintText = hint;

            setText(hint);
            setFont(lostFont);
            setForeground(Color.GRAY);

            this.addFocusListener(new FocusAdapter() {

                @Override
                public void focusGained(FocusEvent e) {
                    if (getText().equals(hint)) {
                        setForeground(Color.BLACK);
                        setText("");
                        setFont(gainFont);
                    } else {
                        setText(getText());
                        setFont(gainFont);
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if (getText().equals(hint) || getText().length() == 0) {
                        setText(hint);
                        setFont(lostFont);
                        setForeground(Color.GRAY);
                    } else {
                        setText(getText());
                        setFont(gainFont);
                        setForeground(Color.BLACK);
                    }
                }
            });
        }
    }
}

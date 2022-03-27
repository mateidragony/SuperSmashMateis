package SSMEngines.util;

import SSMCode.MapHandler;
import SSMCode.Platform;
import SSMCode.Player;
import SSMCode.PlayerAttacks.Projectile;
import SSMEngines.SSMClient;
import SSMEngines.old.GameEngine;

import java.awt.*;
import java.text.DecimalFormat;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Animator {


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

    public static final int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3;
    public static final int J = 4, K = 5, L = 6, P = 7, ENTER = 8;
    public static final int MOUSEX = 9, MOUSEY = 10, CLICK = 11;

    private final Font myFont = new Font("Sans Serif", Font.BOLD, 60);
    private final DecimalFormat df = new DecimalFormat("####.#");


    //------------------------------------------------------------
    //Instance Bariables
    //------------------------------------------------------------

    private int screenNumber;
    private List<Player> players;

    private List<Double> deathInfo;
    private List<Double> respawnTimers;
    private List<Double> attackTimers;
    private List<Double> animationTimers;
    private List<Double> gameTimers;

    private List<Boolean> readyToPlay;
    private List<Boolean> readyToPlayGame;
    private List<Boolean> playAgain;
    private boolean disconnected;

    private List<Point> mouseCoords;
    private List<Boolean> clicks;

    private MapHandler mapHandler;
    private List<List<Boolean>> playerMoves;

    private int x,y;

    public Animator(){
        x=100;
        y=400;

        playerMoves = Stream.generate(ArrayList<Boolean>::new).limit(4).collect(Collectors.toList());
        mouseCoords = Stream.generate(Point::new).limit(4).collect(Collectors.toList());
    }


    public void initArrayLists(){
        
    }


    public void animate(){

        if(!playerMoves.isEmpty() && !playerMoves.get(0).isEmpty()) {
            if (playerMoves.get(0).get(0)) {
                y -= 5;
            }
            if (playerMoves.get(0).get(1)) {
                y += 5;
            }
            if (playerMoves.get(0).get(2)) {
                x -= 5;
            }
            if (playerMoves.get(0).get(3)) {
                x += 5;
            }
        }

    }

    public String pack(){
        String data = "";
        data+= x + SSMClient.parseChar;
        data+= y + SSMClient.parseChar;
        return data;
    }
    public void unpack(String str, int pID){
        String[] strData = str.split(SSMClient.parseChar);
        List<Boolean> data = new ArrayList<>();

        for(int i=0; i<=8; i++){
            data.add(Boolean.parseBoolean(strData[i]));
        }
        playerMoves.set(pID,data);

        mouseCoords.set(pID,new Point(Integer.parseInt(strData[MOUSEX]),Integer.parseInt(strData[MOUSEY])));
        clicks.set(pID,Boolean.parseBoolean(strData[CLICK]));
    }
}

package SSMEngines.util;

import SSMCode.Platform;
import SSMCode.Player;
import SSMEngines.SSMClient;

import java.awt.*;
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
    public static final int INTRO_SCREEN = 0;
    public static final int LOADING_SCREEN = 1;
    public static final int CHARACTER_SELECT_SCREEN = 2;
    public static final int MAP_SELECT = 3;
    public static final int IN_GAME_SCREEN = 4;
    public static final int END_GAME_SCREEN = 5;
    public static final int DC_SCREEN = 6;

    public static final int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3;
    public static final int J = 4, K = 5, L = 6, P = 7, ENTER = 8;

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

    //Sent from client (Player inputs)
    private List<List<Boolean>> playerMoves;
    private List<Point> mouseCoords;
    private List<Boolean> clicks;

    //map screen variables
    private int mapNumber;
    private final MapHandler mapHandler;
    private boolean choseMap;

    //in game variables
    private List<Platform> platList;

    public Animator(){
        screenNumber = CHARACTER_SELECT_SCREEN;
        mapHandler = new MapHandler();
        initArrayLists();
        Player.initImages();
    }

    public void initArrayLists(){
        //List of 4 dummies at (0,0) size (40,60) id 0->3
        players = IntStream.range(0,4).mapToObj(Player::new).collect(Collectors.toList());
        //4 blank arraylists
        playerMoves = Stream.generate(ArrayList<Boolean>::new).limit(4).collect(Collectors.toList());
        //4 points at 0,0
        mouseCoords = Stream.generate(Point::new).limit(4).collect(Collectors.toList());
        //3 false booleans
        clicks = Stream.generate(() -> Boolean.FALSE).limit(4).collect(Collectors.toList());
    }



    public void animate(){
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
        for(List<Boolean> list : playerMoves){
            if(!list.isEmpty() && list.get(J))
                screenNumber++;
        }
    }
    public void animateMapSelect(){
        for(int i=0; i< clicks.size(); i++){
            boolean click = clicks.get(i);

            if(click){
                choseMap = mapHandler.handleMouseEvents(mouseCoords.get(i).x, mouseCoords.get(i).y);
            }
        }

        if(choseMap){
            mapNumber = mapHandler.getMapNumber();
            Platform.setBigImg(mapHandler.getMapPlats().get(mapHandler.getMapNumber()*2));
            Platform.setSmallImg(mapHandler.getMapPlats().get(mapHandler.getMapNumber()*2+1));
            platList = mapHandler.initPlats();
            screenNumber++;
        }
    }
    public void animateGame(){

    }


    int imageSize = 90; double imgHeightRatio = 1.5/1.3;
    int imgsPerRow = 9; int imgOffset = 10;
    int xOffset = 100; int yOffset = 100;

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

            if(clicks.get(i)) {
                for (int j = 0; j < imageRects.size(); j++) {
                    Rectangle r = imageRects.get(j);
                    if (r.contains(mouse)) {
                        players.get(i).setCharacter(j);
                    }
                }
            }
        }
    }



    public static String parseChar = ";";

    public String pack(){
        String data = "";
        //Game data
        data+=screenNumber+SSMClient.parseChar;
        data+=mapNumber+SSMClient.parseChar;

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
}

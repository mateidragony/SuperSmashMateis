package SSMEngines.util;

import SSMEngines.SSMClient;

import java.util.List;

public class Animator {

    int x,y;

    public Animator(){
        x=100;
        y=400;
    }

    public void animate(List<List<Boolean>> playerMoves){

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

}

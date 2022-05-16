package SSMEngines.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;

public class Poolkit {

    public Image getImage(String name) {
        Image bob = null;

        try {
            bob = ImageIO.read(new File(name));
        }catch(Exception e) {
            System.out.println("Couldn't find file: "+name);
            e.printStackTrace();
        }

        return bob;

    }

}
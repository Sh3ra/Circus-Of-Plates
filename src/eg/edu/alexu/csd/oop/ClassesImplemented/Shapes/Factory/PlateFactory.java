package eg.edu.alexu.csd.oop.ClassesImplemented.Shapes.Factory;

import eg.edu.alexu.csd.oop.ClassesImplemented.ImageLoader;
import eg.edu.alexu.csd.oop.ClassesImplemented.Shapes.Plates.PlateWithBase;
import eg.edu.alexu.csd.oop.ClassesImplemented.Shapes.Plates.Shape;
import eg.edu.alexu.csd.oop.ClassesImplemented.Shapes.Pool.PlatePool;
import eg.edu.alexu.csd.oop.game.GameObject;

import java.awt.image.BufferedImage;
import java.util.Random;

public class PlateFactory implements IPlateFactory {
    private static IPlateFactory pf = null;
    private static ImageLoader imgLoad;
    private static PlatePool platePool;
    private BufferedImage spriteImages;
    private Random rand;
    private String[] avaColors = new String[]{"BLUE", "BLACK", "RED", "PURBLE", "LIGHT PURBLE", "DARKPURBLE", "DARKBLACK"};
    private String dest = "Resources/plates/", fileType = ".png";
    private String[] Types = new String[]{"PlateWithoutBase", "PlateWithDeepBase", "PlateWithBase", "Pot"};

    private PlateFactory() {
        platePool = PlatePool.getInstance();
        imgLoad = ImageLoader.getInstance();
        rand = new Random();
    }

    public static IPlateFactory getInstance() {
        if (pf == null) {
            pf = new PlateFactory();
        }
        return (IPlateFactory) pf;
    }

    public GameObject makePlate() {
        int randNum = rand.nextInt(6);
        String color = avaColors[randNum];
        String plateType = Types[rand.nextInt(3)];
        Shape plate = new PlateWithBase(0,0,0,0,"",spriteImages); // edit this after dynamic loading
        if (platePool.exists(color + fileType)) {
            plate = platePool.get(color + fileType);
            plate.setX(rand.nextInt(1960));
            plate.setY(0);
            plate.isVisible = true;
            return (GameObject) plate;
        }


        // dynamic loading

        else {
            spriteImages = imgLoad.getImage(dest + color + plateType + fileType);
            try {
                plate = (Shape) Class.forName(plateType).newInstance();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
        // plate = new PlateWithBase(rand.nextInt(1960), 0, spriteImages.getWidth(), spriteImages.getHeight(), color, spriteImages);
        return (GameObject) plate;
    }
}

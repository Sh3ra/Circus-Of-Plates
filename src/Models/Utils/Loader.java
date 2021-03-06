package Models.Utils;

import Models.Logger.GameLogger;
import Models.Plates.ISpecial;
import Models.Plates.Plate;
import View.game.GameObject;
import org.imgscalr.Scalr;
import org.reflections.Reflections;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

public class Loader {
    private static Loader loader = null;
    private Reflections ref = new Reflections();
    private ClassLoader classLoader;
    private HashMap<String, BufferedImage> loaders;
    GameLogger logger ;

    private Loader() {
        loaders = new HashMap<String, BufferedImage>();
        logger = GameLogger.getInstance();
        loadAllImages();
    }

    public static synchronized Loader getInstance() {
        if (loader == null) {
            loader = new Loader();
        }
        return loader;
    }

    private void loadAllImages() {
      File dir2 = new File("Resources/Plates");
        File[] f = dir2.listFiles();
        for (File child : f) {
            String s="Plates/"+child.getName();
            loaders.put(s, getImage(s));
        }
        logger.addLog("fine","All images has been loaded Successfully");
    }

    public BufferedImage getImage(String path) {
        BufferedImage image = null;
        if (loaders.containsKey(path)) {
            return loaders.get(path);
        } else {
            try {
                image = ImageIO.read(getClass().getClassLoader().getResource(path));
            } catch (IOException e) {
                GameLogger logger = GameLogger.getInstance();
                logger.addLog("severe", "Image not Found");
                e.printStackTrace();
            }
        }
        logger.addLog("fine","image located at "+path+"has loaded Successfully");
        return image;
    }

    public BufferedImage getImage(String path, double scale) {
        BufferedImage bufferedImage;
        bufferedImage = getImage(path);
        int width = (int) (bufferedImage.getWidth() * scale), height = (int) (bufferedImage.getHeight() * scale);
        bufferedImage = Scalr.resize(bufferedImage, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_TO_WIDTH, width, height, Scalr.OP_ANTIALIAS);
        return bufferedImage;
    }

    public BufferedImage getImageWithLengthAndWidth(String path, int length, int width) {
        classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream(path.toLowerCase());
        BufferedImage bufferedImage;
        bufferedImage = getImage(path);
        bufferedImage = Scalr.resize(bufferedImage, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_EXACT, width, length, Scalr.OP_ANTIALIAS);
        return bufferedImage;
    }


    public String[] getSupportedClasses(Class<?> classToFind) {
        Set<Class<? extends GameObject>> c = ref.getSubTypesOf(GameObject.class);
        ArrayList<String> supportedClasses = new ArrayList<String>();
        Iterator<Class<? extends GameObject>> it = c.iterator();
        int i = 0;
        while (it.hasNext()) {
            Class<?> classTemp = it.next();
            if (classToFind.isAssignableFrom(classTemp) && !classToFind.equals(classTemp) && !ISpecial.class.isAssignableFrom(classTemp)) {
                supportedClasses.add(classTemp.getName());
                i++;
            }
        }
        String[] s = new String[supportedClasses.size()];
        for (int j = 0; j < supportedClasses.size(); j++) s[j] = supportedClasses.get(j);
        GameLogger logger = GameLogger.getInstance();
        logger.addLog("finer","Classes related to "+classToFind.getName()+" has loaded Successfully");
        return s;
    }

    public String[] getSupportedPlateTypes(String[] classNames) {
        int sz = classNames.length;
        String[] supportedTypes = new String[sz];
        for (int i = 0; i < sz; i++) {
            Plate g = (Plate) this.getNewInstance(classNames[i]);
            if (g != null) supportedTypes[i] = g.getType();

        }
        GameLogger logger = GameLogger.getInstance();
        logger.addLog("finer", "Supported Plate Types Found");
        return supportedTypes;
    }

    public GameObject getNewInstance(String s) {
        try {
            logger.addLog("finer","Class "+s+" has been found");
            return (GameObject) Class.forName(s).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        logger.addLog("warning","Class "+s+" wasn't found");
        return null;
    }
}

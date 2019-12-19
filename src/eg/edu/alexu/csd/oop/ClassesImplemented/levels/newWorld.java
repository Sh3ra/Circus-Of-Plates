package eg.edu.alexu.csd.oop.ClassesImplemented.levels;

import eg.edu.alexu.csd.oop.ClassesImplemented.Utils.ArrayIterator;
import eg.edu.alexu.csd.oop.ClassesImplemented.Utils.ArrayListIterator;
import eg.edu.alexu.csd.oop.ClassesImplemented.Clowns.Clown;
import eg.edu.alexu.csd.oop.ClassesImplemented.Stick.Stick;
import eg.edu.alexu.csd.oop.ClassesImplemented.Shapes.Factory.PlateFactory;
import eg.edu.alexu.csd.oop.ClassesImplemented.Shapes.Plates.Plate;
import eg.edu.alexu.csd.oop.ClassesImplemented.Shapes.Pool.PlatePool;
import eg.edu.alexu.csd.oop.ClassesImplemented.States.StackedState;
import eg.edu.alexu.csd.oop.ClassesImplemented.Shelfs.Utils.ShelfHandler;
import eg.edu.alexu.csd.oop.ClassesImplemented.Utils.intersectPlates;
import eg.edu.alexu.csd.oop.ClassesImplemented.replay.SaveAndLoad;
import eg.edu.alexu.csd.oop.game.GameObject;
import eg.edu.alexu.csd.oop.game.World;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class newWorld implements World {
    PlateFactory pf;
    int time = 0,time_2=0;
    PlatePool pp;
    Stick stick1;
    Stick stick2;
    int width, height;
    String status = "Score: ";
    int score = 0, levelMode;
    long startTime = System.currentTimeMillis();
    List<GameObject> constantObjects;
    List<GameObject> movableObjects;
    List<GameObject> controlableObjects;
    ArrayList<Pair<Stick, Integer>> sticksArray = new ArrayList<>();
    ArrayList<Clown> clownsArray = new ArrayList<>();
    intersectPlates intersection = new intersectPlates();
    static ArrayList <ArrayList <GameObject> > allData;
    Integer[] clownsX;
    ShelfHandler shelfhandler;

    public void addClownsAndEverything(ArrayList<Clown> clownsArray, ArrayList<Pair<Stick, Integer>> sticksArray, List<GameObject> movableObjects, List<GameObject> controlableObjects, Integer[] clownsX) {
        ArrayListIterator iterator = new ArrayListIterator(clownsArray);
        int currentClown = 0;
        while (iterator.hasNext()) {
            Clown clown = (Clown) iterator.next();
            clownsX[currentClown++] = clown.getX();
            controlableObjects.add(clown);
            movableObjects.add(clown.stick1);
            movableObjects.add(clown.stick2);
            sticksArray.add(new Pair<>(clown.stick1, clown.stick1.getY()));
            sticksArray.add(new Pair<>(clown.stick2, clown.stick2.getY()));
            clown.registerObserver(clown.stick1);
            clown.registerObserver(clown.stick2);
        }
    }

    @Override
    public List<GameObject> getConstantObjects() {
        return constantObjects;
    }

    @Override
    public List<GameObject> getMovableObjects() {
        return movableObjects;
    }

    @Override
    public List<GameObject> getControlableObjects() {
        return controlableObjects;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public boolean refresh() {
        if(score == 1) endGame();
        Iterator it = constantObjects.iterator();
        ArrayList removed = new ArrayList();
        for (int i = 0; i < levelMode+1 && (it.hasNext()); i++)
            it.next();

        time++;
        time_2++;
        if (time == 100) {
            time = 0;
        }
       shelfhandler.updateShelfs();
        while (it.hasNext()) {
            Plate m = (Plate) it.next();
            m.update(1);
            if (m.getY() == getHeight()) {
                it.remove();
            }
            int[] z = {score};
            if (intersection.intersect(m, sticksArray, z, movableObjects)) {
                m.setState(new StackedState(m));
                removed.add(m);
            }
            score = z[0];
        }
        if (time == 0)
            shelfhandler.throwPlates();
        if(time_2==900){shelfhandler.makeSpecialPlates();time_2=0;}
        it = removed.iterator();
        while (it.hasNext()) {
            constantObjects.remove(it.next());
        }
        ArrayIterator iterator = new ArrayIterator(clownsX);
        ArrayListIterator iterator1 = new ArrayListIterator(clownsArray);
        int diff = clownsArray.get(0).getX() - clownsX[0];
        Clown clown;
        while (iterator.hasNext()) {
            clown = (Clown) iterator1.next();
            Integer x = (Integer) iterator.next();
            if (diff != clown.getX() - x) {
                iterator = new ArrayIterator(clownsX);
                iterator1 = new ArrayListIterator(clownsArray);
                while (iterator.hasNext()) {
                    clown = (Clown) iterator1.next();
                    clown.setX((Integer) iterator.next());
                }
                break;
            }
        }
        iterator1 = new ArrayListIterator(clownsArray);
        int counter = 0;
        while (iterator1.hasNext()) {
            clown = (Clown) iterator1.next();
            clownsX[counter++] = clown.getX();
        }
        addMomentToArray();
        return true;
    }

    private void addMomentToArray(){
        ArrayList <GameObject> allArray = new ArrayList<>();
        allArray.addAll(constantObjects);
        allArray.addAll(movableObjects);
        allArray.addAll(controlableObjects);
        allData.add(allArray);
    }
    void endGame(){
        SaveAndLoad saveAndLoad = new SaveAndLoad();
        //saveAndLoad.save(allData);
    }

    @Override
    public String getStatus() {
        return status + score;
    }

    @Override
    public int getSpeed() {
        return 0;
    }

    @Override
    public int getControlSpeed() {
        return 0;
    }
}

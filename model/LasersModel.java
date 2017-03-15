/** LasersModel.java
 *  Backend for both the PUTI and GUI
 *  implementations of Lasers. Stores
 *  all data then is accessed by PTUI
 *  or GUI to be displayed.
 *  @author Michael Wohl
 *  @author John DeBrino
 */
package model;

import backtracking.Backtracker;
import backtracking.Configuration;
import backtracking.SafeConfig;
import java.io.FileNotFoundException;
import java.util.Observable;
import java.util.Optional;

public class LasersModel extends Observable {

    private SafeConfig safe;
    /** Quit status*/
    public int quit;

    /**Create Laser Model*/
    public LasersModel(String filename) throws FileNotFoundException {
        safe = new SafeConfig(filename);
        quit = 0;
    }

    /**
     * Returns the safe the model is working with
     *
     * @return A current SafeConfig for the model
     */
    public SafeConfig getSafe() {
        return safe;
    }

    public String SolveSafe() //TODO make work
    /**The intended functionality of this
     * method is to solve the safe
     * via backtracking. Sets the safe
     * to the solved solution if solveable*/
    {
        Backtracker tracker = new Backtracker(false);
        Optional<Configuration> solved = tracker.solve(safe);
        if(solved.isPresent()){
            this.safe = (SafeConfig)solved.get();
            announceChange();
            return "Solution";
        } else{
            return "Not possible to solve";
        }
    }
    /**
     * A utility method that indicates the model has changed and
     * notifies observers
     */
    public void announceChange() {
        setChanged();
        notifyObservers();
    }

    public int getHeight()
    /**Access layer for GUI implementation */
    {
        return safe.getRows();
    }

    public int getWidth()
    /**Access layer for GUI implementation */
    {
        return safe.getCollumns();
    }
}

package ptui;

import java.io.FileNotFoundException;
import java.util.Observable;
import java.util.Observer;

import model.LasersModel;

/**
 * This class represents the view portion of the plain text UI.  It
 * is initialized first, followed by the controller (ControllerPTUI).
 * You should create the model here, and then implement the update method.
 *
 * @author Sean Strout @ RIT CS
 * @author Michael Wohl
 */
public class LasersPTUI implements Observer {
    /**
     * The UI's connection to the model
     */
    private LasersModel model;

    /**
     * Construct the PTUI.  Create the model and initialize the view.
     *
     * @param filename the safe file name
     * @throws FileNotFoundException if file not found
     */
    public LasersPTUI(String filename) throws FileNotFoundException {
        try {
            this.model = new LasersModel(filename);
            System.out.print(model.getSafe().toString());
            System.out.print("> ");
        } catch (FileNotFoundException fnfe) {
            System.out.println(fnfe.getMessage());
            System.exit(-1);
        }
        this.model.addObserver(this);
    }

    /**
     * Gets the model for the PTUI
     *
     * @return The model handling this PTUI
     */
    public LasersModel getModel() {
        return model;
    }

    @Override
    public void update(Observable o, Object arg) {
        if(model.quit == 1){
            return;
        }
        System.out.println(model.getSafe().toString());
        System.out.print("> ");
    }
}

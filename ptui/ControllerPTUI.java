package ptui;

import model.LasersModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

/**
 * This class represents the controller portion of the plain text UI.
 * It takes the model from the view (LasersPTUI) so that it can perform
 * the operations that are input in the run method.
 *
 * @author Sean Strout @ RIT CS
 * @author Michael Wohl
 */
public class ControllerPTUI  {
    /** The UI's connection to the model */
    private LasersModel model;

    /**
     * Construct the PTUI.  Create the model and initialize the view.
     * @param model The laser model
     */
    public ControllerPTUI(LasersModel model) {
        this.model = model;
    }

    /**
     * Run the main loop.  This is the entry point for the controller
     * @param inputFile The name of the input input file, if specified
     */
    public void run(String inputFile) {
        if (inputFile == null) {
            Scanner io = new Scanner(System.in);
            while (true) {
                if (model.quit == 1) {
                    break;
                }
                String input = io.nextLine();
                String[] cstring = input.split(" ");
                if (cstring[0].equals("")) {
                } else if (cstring[0].toLowerCase().charAt(0) == 'a') {
                    if (cstring.length != 3) {
                        System.out.print("Incorrect coordinates\n");
                        return;
                    }
                    System.out.println(model.getSafe().SetLaser(Integer.parseInt(cstring[1]), Integer.parseInt(cstring[2])));
                } else if (cstring[0].toLowerCase().charAt(0) == 'q') {
                    model.quit = 1;
                } else if (cstring[0].toLowerCase().charAt(0) == 'r') {
                    if (cstring.length != 3) {
                        System.out.print("Incorrect coordinates\n");
                        return;
                    }
                    System.out.println(model.getSafe().RemoveLaser(Integer.parseInt(cstring[1]), Integer.parseInt(cstring[2])));
                }
                else if (cstring[0].toLowerCase().equals("h")) {
                    System.out.print("a|add r c: Add laser to (r,c)\n" +
                            "d|display: Display safe\n" +
                            "h|help: Print this help message\n" +
                            "q|quit: Exit program\n" +
                            "r|remove r c: Remove laser from (r,c)\n" +
                            "v|verify: Verify safe correctness\n");
                }
                else if(cstring[0].toLowerCase().charAt(0) == 'v') {
                    if (model.getSafe().isValid())
                    {
                        System.out.print("Safe is fully verified! \n");
                    }
                    else{
                        System.out.println("Error verifying at ( " + model.getSafe().error[0] + "," + model.getSafe().error[1] + ")");
                    }
                }
                model.announceChange();
            }
        }
        else{
            try {
                Scanner io = new Scanner(new File(inputFile));
                while (io.hasNextLine()){
                    String string = io.nextLine();
                    String[] cstring = string.split(" ");
                    model.getSafe().SetLaser(Integer.parseInt(cstring[1]), Integer.parseInt(cstring[2]));
                }
            }
            catch (FileNotFoundException e){
                return;
            }
            model.announceChange();
            run(null);
        }
    }
}

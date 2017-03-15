package gui;

import backtracking.Backtracker;
import backtracking.Configuration;
import com.sun.deploy.config.Config;
import com.sun.org.apache.regexp.internal.RE;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;

import model.*;

import javax.swing.*;

/**
 * The main class that implements the JavaFX UI.   This class represents
 * the view/controller portion of the UI.  It is connected to the model
 * and receives updates from it.
 *
 * @author Sean Strout @ RIT CS
 * @author John DeBrino
 * @author Michael Wohl
 */
public class LasersGUI extends Application implements Observer {
    /** The UI's connection to the model */
    private LasersModel model;

    /**Create objects necessary for proper
     * model UI interaction.*/
    private Label message;
    private BorderPane UI;

    /** The current test file being engaged */
    private String currFile;

    /** this can be removed - it is used to demonstrates the button toggle */
    private static boolean status = true;

    @Override
    public void init() throws Exception {
        // the init method is run before start.  the file name is extracted
        // here and then the model is created.
        try {
            Parameters params = getParameters();
            currFile = params.getRaw().get(0);
            this.model = new LasersModel(currFile);
        } catch (FileNotFoundException fnfe) {
            System.out.println(fnfe.getMessage());
            System.exit(-1);
        }
        this.model.addObserver(this);
    }

    /**
     * Removes or Adds a laser when a button is clicked, accordingly
     *
     * @param button The button clicked which adds or removes a laser
     */
     private void toggleLaser(Button button){
         GridPane field = (GridPane) UI.getCenter();
         List buttons = field.getChildren();
         if(model.getSafe().getGrid()[buttons.indexOf(button)%model.getHeight()][buttons.indexOf(button)/model.getHeight()].equals("L")){
            message.setText(model.getSafe().RemoveLaser(buttons.indexOf(button)%model.getHeight(), buttons.indexOf(button)/model.getHeight()));
         }
         else{
            message.setText(model.getSafe().SetLaser(buttons.indexOf(button)%model.getHeight(), buttons.indexOf(button)/model.getHeight()));
         }
         model.announceChange();
    }

    /**
     * Checks the model's safe to see if it is valid and adjusts GUI accordingly
     */
    private void validGUI(){
        GridPane field = (GridPane) UI.getCenter();
        List buttons = field.getChildren();
        if(model.getSafe().isGoal()){
            message.setText("Safe is fully verified!");
        }
        else{
            Button temp = (Button) buttons.get((model.getHeight()*model.getSafe().error[1]) + model.getSafe().error[0]);
            if(!model.getSafe().getGrid()[buttons.indexOf(temp)%model.getHeight()][buttons.indexOf(temp)/model.getHeight()].equals("X") &&
                    !model.getSafe().getGrid()[buttons.indexOf(temp)%model.getHeight()][buttons.indexOf(temp)/model.getHeight()].matches("\\d")) {
                Image laserImg = new Image(getClass().getResourceAsStream("resources/red.png"));
                ImageView laserIcon = new ImageView(laserImg);
                temp.setGraphic(laserIcon);
            }
            setButtonBackground(temp, "red.png");
            message.setText("Error verifying at ( " + model.getSafe().error[0] + "," + model.getSafe().error[1] + " )");
        }
    }

    /**
     * Resets the GUI and model
     */
    private void restart(){
        try {
            model = new LasersModel(currFile);
            message.setText("Please place a laser.");
            model.addObserver(this);
            model.announceChange();
        }
        catch (FileNotFoundException e){
            System.out.println("file not found");
        }
    }

    private Configuration getHint()
    /**Gets hint for next move*/
    {
        Backtracker tracker = new Backtracker(false);
        List<Configuration> path = tracker.solveWithPath(this.model.getSafe());
        return path.get(0);
    }

    private void solveit()
    {
        message.setText(this.model.SolveSafe());
    }


    /**
     * Uses the File Chooser to load a new SafeConfig
     */
    private void loadFile(){
        JFileChooser choose = new JFileChooser();
        choose.setCurrentDirectory(new File("tests"));
        choose.showOpenDialog(new Canvas());
        currFile = "tests/" + choose.getSelectedFile().getName();
        restart();
    }

    /**
     * A private utility function for setting the background of a button to
     * an image in the resources subdirectory.
     *
     * @param button the button control
     * @param bgImgName the name of the image file
     */
    private void setButtonBackground(Button button, String bgImgName) {
        BackgroundImage backgroundImage = new BackgroundImage(
                new Image( getClass().getResource("resources/" + bgImgName).toExternalForm(), 75 , 75, true,true),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                BackgroundSize.DEFAULT);
        Background background = new Background(backgroundImage);
        button.setBackground(background);
    }

    /**
     * This is a private demo method that shows how to create a button
     * and attach a foreground image with a background image that
     * toggles from yellow to red each time it is pressed.
     *
     * @param stage the stage to add components into
     */
    private void buttonDemo(Stage stage) {
        // this demonstrates how to create a button and attach a foreground and
        // background image to it.
        Button button = new Button();
        Image laserImg = new Image(getClass().getResourceAsStream("resources/laser.png"));
        ImageView laserIcon = new ImageView(laserImg);
        button.setGraphic(laserIcon);
        setButtonBackground(button, "yellow.png");
        button.setOnAction(e -> {
            // toggles background between yellow and red
            if (!status) {
                setButtonBackground(button, "yellow.png");
            } else {
                setButtonBackground(button, "red.png");
            }
            status = !status;
        });

        Scene scene = new Scene(button);
        stage.setScene(scene);
    }

    /**
     * The
     * @param stage the stage to add UI components into
     */
    private void init(Stage stage) throws Exception {
        this.model.addObserver(this);
        stage.setTitle("Lasers");
        UI = new BorderPane();
        UI.setBackground(new Background(new BackgroundFill(Paint.valueOf("LIGHTGRAY"), null, null)));
        message = new Label();
        message.setText("Please place a laser.");
        UI.setTop(MessageCenter(message));
        UI.setCenter(ButtonField());
        UI.setBottom(ControlMe());
        Scene thisScene = new Scene(UI,550,550);
        stage.setScene(thisScene);
        stage.sizeToScene();
        model.announceChange();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        init(primaryStage);
        primaryStage.setTitle("Lasers");
        primaryStage.show();
    }

    @Override
    public void update(Observable o, Object arg)
    {
        GridPane buttons = (GridPane) ButtonField();
        for(int r = 0; r < model.getHeight(); r++){
            for(int c = 0; c < model.getWidth(); c++){
                if(model.getSafe().getGrid()[r][c].equals("L")){
                    Button button = (Button) buttons.getChildren().get((c * model.getHeight()) + r);
                    Image laserImg = new Image(getClass().getResourceAsStream("resources/laser.png"), 60, 60, true, true);
                    ImageView laserIcon = new ImageView(laserImg);
                    button.setGraphic(laserIcon);
                    setButtonBackground(button, "yellow.png");
                }
                else if(model.getSafe().getGrid()[r][c].equals("*")){
                    Button button = (Button) buttons.getChildren().get((c * model.getHeight()) + r);
                    Image laserImg = new Image(getClass().getResourceAsStream("resources/beam.png"), 60, 60, true,true);
                    ImageView laserIcon = new ImageView(laserImg);
                    button.setGraphic(laserIcon);
                    setButtonBackground(button, "yellow.png");
                }
                else if(model.getSafe().getGrid()[r][c].matches("\\d")){
                    Button button = (Button) buttons.getChildren().get((c * model.getHeight()) + r);
                    switch (model.getSafe().getGrid()[r][c]){
                        case "0":
                            Image laserImg = new Image(getClass().getResourceAsStream("resources/pillar0.png"), 60,60,true,true);
                            ImageView laserIcon = new ImageView(laserImg);
                            button.setGraphic(laserIcon);
                            break;
                        case "1":
                            Image laserImg1 = new Image(getClass().getResourceAsStream("resources/pillar1.png"),60,60,true,true);
                            ImageView laserIcon1 = new ImageView(laserImg1);
                            button.setGraphic(laserIcon1);
                            break;
                        case "2":
                            Image laserImg2 = new Image(getClass().getResourceAsStream("resources/pillar2.png"),60,60,true,true);
                            ImageView laserIcon2 = new ImageView(laserImg2);
                            button.setGraphic(laserIcon2);
                            break;
                        case "3":
                            Image laserImg3 = new Image(getClass().getResourceAsStream("resources/pillar3.png"),60,60,true,true);
                            ImageView laserIcon3 = new ImageView(laserImg3);
                            button.setGraphic(laserIcon3);
                            break;
                        case "4":
                            Image laserImg4 = new Image(getClass().getResourceAsStream("resources/pillar4.png"),60,60,true,true);
                            ImageView laserIcon4 = new ImageView(laserImg4);
                            button.setGraphic(laserIcon4);
                            break;
                    }
                    setButtonBackground(button, "white.png");
                }
                else if(model.getSafe().getGrid()[r][c].equals("X")){
                    Button button = (Button) buttons.getChildren().get((c * model.getHeight()) + r);
                    Image laserImg = new Image(getClass().getResourceAsStream("resources/pillarX.png"),60,60,true,true);
                    ImageView laserIcon = new ImageView(laserImg);
                    button.setGraphic(laserIcon);
                    setButtonBackground(button, "white.png");
                }
                else if(model.getSafe().getGrid()[r][c].equals(".")){
                    Button button = (Button) buttons.getChildren().get((c * model.getHeight()) + r);
                    Image laserImg = new Image(getClass().getResourceAsStream("resources/white.png"));
                    ImageView laserIcon = new ImageView(laserImg);
                    button.setGraphic(laserIcon);
                    setButtonBackground(button, "white.png");
                }
            }
        }
        UI.setCenter(buttons);
    }

    private Node MessageCenter(Label message)
    /**Create the area to display messages*/
    {
        HBox MC = new HBox(5);
        MC.getChildren().add(message);
        MC.setAlignment(Pos.CENTER);
        MC.setStyle("-fx-font: 24 Arial");
        return MC;
    }

    private Node ButtonField()
    /** Creates the main interaction field
     *  field consits of buttons that toggle
     *  between lasers and nothing*/
    {
        GridPane BF = new GridPane();
        BF.setAlignment(Pos.CENTER);
        BF.setHgap(30);
        BF.setVgap(30);
        for(int i = 0; i < model.getWidth(); i++)
        {
            for(int j = 0; j < model.getHeight(); j++)
            {
                Button temp = new Button();
                temp.setMinSize(75,75);
                temp.setOnAction((ActionEvent e) -> toggleLaser(temp));
                BF.add(temp,i,j);
            }
        }
        return BF;
    }

    private Node ControlMe()
    /**Creates the buttons on the bottom
     * that enter commands such as check
     * and load*/
    {
        HBox Ctrl = new HBox(15);
        Ctrl.setAlignment(Pos.CENTER);
        Button Check = new Button("Check");
        Check.setOnAction((ActionEvent e) -> validGUI());
        Check.setMinSize(70,40);
        Button Hint = new Button("Hint");
        Hint.setMinSize(70,40);
        //TODO write a getHint method for button's use
        //Hint.setOnAction((ActionEvent e) -> getHint());
        Button Solve = new Button("Solve");
        Solve.setOnAction(event -> solveit()); //TODO make this method work
        Solve.setMinSize(70,40);
        Button Restart =new Button("Restart");
        Restart.setMinSize(70,40);
        Restart.setOnAction((ActionEvent e) -> restart());
        Button Load = new Button("Load");
        Load.setMinSize(70,40);
        Load.setOnAction((ActionEvent e) -> loadFile());
        Ctrl.getChildren().add(Check);
        Ctrl.getChildren().add(Hint);
        Ctrl.getChildren().add(Solve);
        Ctrl.getChildren().add(Restart);
        Ctrl.getChildren().add(Load);
        return Ctrl;
    }
}

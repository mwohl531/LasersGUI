package backtracking;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;

/**
 * The class represents a single configuration of a safe.  It is
 * used by the backtracker to generate successors, check for
 * validity, and eventually find the goal.
 *
 * This class is given to you here, but it will undoubtedly need to
 * communicate with the model.  You are free to move it into the model
 * package and/or incorporate it into another class.
 *
 * @author Sean Strout @ RIT CS
 * @author Michael Wohl
 * @author John DeBrino
 */
public class SafeConfig implements Configuration {

    //INPUT CONSTANTS
    /** An empty cell */
    private final static String EMPTY = ".";
    /** A cell occupied by a pillar for any number of lasers */
    private final static String PILLAR = "X";
    /** A cell occupied by a laser */
    private final static String LASER = "L";
    /** A cell occupied by Laser beam */
    private final static String BEAM = "*";

    //OUTPUT CONSTANTS
    /** A horizontal divider */
    private final static char HORI_DIVIDE = '-';
    /** A vertical divider */
    private final static char VERT_DIVIDE = '|';

    //VARIABLES
    /** The number of rows in the grid */
    private int rows;
    /** The number of columns in the grid */
    private int columns;
    /** A grid representing the safe */
    private String[][] grid;
    /** The current node being looked at */
    int[] cur;
    /** Yhe first block to cause an error */
    public int[] error;

    public SafeConfig(String filename) throws FileNotFoundException {
        /**Construct the config from a file*/
        Scanner in = new Scanner(new File(filename));
        rows = in.nextInt();
        columns = in.nextInt();
        grid = new String[rows][columns];
        error = new int[2];

        for(int r = 0; r < rows; r++){
            for(int c = 0; c < columns; c++){
                grid[r][c] = in.next();
            }
        }
        in.close();
    }

    //Copy Constructor
    public SafeConfig(SafeConfig other){
        /**Copies a config to be worked with*/
        this.rows = other.rows;
        this.columns = other.columns;
        cur = Arrays.copyOf(other.cur, 2);
        error = Arrays.copyOf(other.error, 2);
        this.grid = new String[rows][columns];
        for(int r = 0; r < other.rows; r++){
            this.grid[r] = Arrays.copyOf(other.grid[r], other.columns);
        }
    }

    /**
     * Finds the next block in the grid which is not a pillar
     *
     * @param cur The current block in the grid being looked at
     * @return An int[] which represents the next block to look at
     */
    public int[] getNext(int[] cur){
        int[] next = new int[2];
        if(cur == null){
            next[0] = 0;
            next[1] = 0;
        }
        else if(cur[1] == columns - 1){
            next[0] = cur[0] + 1;
            next[1] = 0;
        }
        else{
            next[0] = cur[0];
            next[1] = cur[1] + 1;
        }
        if(grid[next[0]][next[1]].equals(PILLAR) || grid[next[0]][next[1]].matches("\\d")){
            return getNext(next);
        }
        else {
            return next;
        }
    }


    public int getCollumns()
    /**returns the number of collumns
     * for creating GUI */
    {
        return this.columns;
    }

    public int getRows()
    /**returns the number of rows
     * for creating GUI */
    {
        return this.rows;
    }

    /**
     * Gets the grid for updating the GUI
     *
     * @return This SafeConfig's grid
     */
    public String[][] getGrid() {
        return grid;
    }

    @Override
    //TODO HEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEERRRRRRRRRRRRRRREEEEEE
    public Collection<Configuration> getSuccessors() {
        cur = getNext(cur);
        SafeConfig laser = new SafeConfig(this);
        SafeConfig beam = new SafeConfig(this);
        SafeConfig floor = new SafeConfig(this);
        laser.grid[cur[0]][cur[1]] = LASER;
        beam.grid[cur[0]][cur[1]] = BEAM;
        return Arrays.asList(laser, beam);
    }

    /**SetLaser
     * This method  places a laser and then adds
     * beams omnidirectional to the laser
     * @param x coordinate
     * @param y coordinate
     */
    public String SetLaser(int x, int y) {
        if(x>=rows || x<0 || y>=columns|| y<0)
        {
            return "Error adding laser at: ( " + x + ", " + y + ")\n";
        }
        if(!grid[x][y].equals(EMPTY))
        {
            return "Error adding laser at: ( " + x + ", " + y + ")\n";
        }
        int xcoor = x;
        int ycoor = y;
        grid[x][y] = LASER;
        xcoor++;
        while(xcoor < rows && (grid[xcoor][ycoor].equals(EMPTY) || grid[xcoor][ycoor].equals(BEAM)))
        {
            grid[xcoor][ycoor] = BEAM;
            xcoor++;
        }
        xcoor = x - 1;
        while(xcoor >= 0 && (grid[xcoor][ycoor].equals(EMPTY) || grid[xcoor][ycoor].equals(BEAM)))
        {
            grid[xcoor][ycoor] = BEAM;
            xcoor--;
        }
        xcoor=x;
        ycoor++;
        while(ycoor < columns && (grid[xcoor][ycoor].equals(EMPTY) || grid[xcoor][ycoor].equals(BEAM)))
        {
            grid[xcoor][ycoor] = BEAM;
            ycoor++;
        }
        ycoor = y;
        ycoor--;
        while(ycoor >= 0 && (grid[xcoor][ycoor].equals(EMPTY) || grid[xcoor][ycoor].equals(BEAM)))
        {
            grid[xcoor][ycoor] = BEAM;
            ycoor--;
        }
        return "Laser added at: ( " + x + ", " + y + ")\n";
    }

    /**Remove Laser
     * Method removes a laser and it's corresponding
     * omnidirectional beams
     * @param x coordinate
     * @param y coordinate
     */
    public String RemoveLaser(int x, int y)
    {
        if(x>=rows || x<0 || y>=columns|| y<0)
        {
            return "Error removing laser at: ( " + x + ", " + y + ")\n";
        }
        if(!grid[x][y].equals(LASER))
        {
            return "Error removing laser at: ( " + x + ", " + y + ")\n";
        }
        int xcoor = x;
        int ycoor = y;
        grid[x][y] = EMPTY;
        xcoor++;
        while(xcoor < rows && grid[xcoor][ycoor].equals(BEAM))
        {
            if(!LaserCheck(xcoor,ycoor))
            {
                xcoor++;
                continue;
            }
            grid[xcoor][ycoor] = EMPTY;
            xcoor ++;
        }
        xcoor = x - 1;
        while(xcoor >= 0 && grid[xcoor][ycoor].equals(BEAM))
        {
            if(!LaserCheck(xcoor,ycoor))
            {
                xcoor--;
                continue;
            }
            grid[xcoor][ycoor] = EMPTY;
            xcoor --;
        }
        xcoor = x;
        ycoor++;
        while(ycoor < columns && grid[xcoor][ycoor].equals(BEAM))
        {
            if(!LaserCheck(xcoor,ycoor))
            {
                ycoor ++;
                continue;
            }
            grid[xcoor][ycoor] = EMPTY;
            ycoor ++;
        }
        ycoor = y;
        ycoor--;
        while(ycoor >= 0 && grid[xcoor][ycoor].equals(BEAM))
        {
            if(!LaserCheck(xcoor,ycoor))
            {
                ycoor--;
                continue;
            }
            grid[xcoor][ycoor] = EMPTY;
            ycoor --;
        }
        return "Laser removed at: ( " + x + ", " + y + ")\n";
    }

    public boolean LaserCheck(int x, int y)
    /**Checks to see if a laser is able to be placed as
     * a result of other lasers creating conflicts*/
    {
        int xcoor = x;
        int ycoor = y;
        xcoor++;
        while(xcoor < rows)
        {
            if(grid[xcoor][ycoor].equals(LASER))
            {
                return false;
            }
            else if (grid[xcoor][ycoor].equals(PILLAR)||grid[xcoor][ycoor].matches("\\d"))
            {
                break;
            }
            xcoor ++;
        }
        xcoor = x - 1;
        while(xcoor >= 0)
        {
            if(grid[xcoor][ycoor].equals(LASER))
            {
                return false;
            }
            else if (grid[xcoor][ycoor].equals(PILLAR)||grid[xcoor][ycoor].matches("\\d"))
            {
                break;
            }
            xcoor --;
        }
        xcoor = x;
        ycoor++;
        while(ycoor < columns)
        {
            if(grid[xcoor][ycoor].equals(LASER))
            {
                return false;
            }
            else if (grid[xcoor][ycoor].equals(PILLAR)||grid[xcoor][ycoor].matches("\\d"))
            {
                break;
            }
            ycoor ++;
        }
        ycoor = y;
        ycoor--;
        while(ycoor >= 0)
        {
            if(grid[xcoor][ycoor].equals(LASER))
            {
                return false;
            }
            else if (grid[xcoor][ycoor].equals(PILLAR)||grid[xcoor][ycoor].matches("\\d"))
            {
                break;
            }
            ycoor --;
        }
        return true;
    }

    /**
     * Gets the number of lasers surrounding a pillar
     *
     * @param r The row of the pillar
     * @param c The column of the pillar
     * @return The number of lasers around the coordinate
     */
    private int checkLasers(int r, int c){
        int lasers = 0;
        for(int n = r - 1; n > r - 2 && n >= 0; n--){
            if(grid[n][c].equals(LASER)){
                lasers += 1;
            }
        }
        for(int s = r + 1; s < r + 2 && s < rows; s++){
            if(grid[s][c].equals(LASER)){
                lasers += 1;
            }
        }
        for(int e = c - 1; e > c - 2 && e >= 0; e--){
            if(grid[r][e].equals(LASER)){
                lasers += 1;
            }
        }
        for(int w = c + 1; w < c + 2 && w < columns; w++){
            if(grid[r][w].equals(LASER)){
                lasers += 1;
            }
        }
        return lasers;
    }

    @Override
    public boolean isValid()
    {
        boolean valid = true;
        for(int r = 0; r < rows; r++){
            for(int c = 0; c < columns; c++){
                //Checks that no lasers collide with one another
                //May be redundant, but still a good test
                if(grid[r][c].equals(LASER)) {
                    if (valid) {
                        valid = LaserCheck(r, c);
                    }
                }

                /*if(grid[r][c].equals(BEAM)){
                    if(valid){
                        valid = !LaserCheck(r, c);
                    }
                }*/
                //Checks to ensure each numbered pillar has the correct number of lasers

                if (grid[r][c].matches("\\d")) {
                    if(valid) {
                        int capacity = Integer.parseInt(grid[r][c]);
                        int lasers = checkLasers(r, c);
                        valid = (lasers <= capacity);
                        if(!valid)
                        {
                            error[0] = r;
                            error[1] = c;
                        }
                    }
                }
            }
        }
        return valid;
    }

    @Override
    public boolean isGoal() {
        for(int r = 0; r < rows; r++){
            for(int c = 0; c < columns; c++){
                if(grid[r][c].equals(EMPTY)){
                    error[0] = r;
                    error[1] = c;
                    return false;
                }
                if(grid[r][c].matches("\\d")){
                    int capacity = Integer.parseInt(grid[r][c]);
                    int lasers = checkLasers(r, c);
                    if(lasers != capacity) {
                        error[0] = r;
                        error[1] = c;
                        return false;
                    }
                }
            }
        }
        return isValid();
    }

    @Override
    public String toString() {
        //Writes the first line of integers
        String string = " ";
        for(int i = 0; i < columns; i++){
            String number = Integer.toString(i);
            string += " " + Character.toString(number.charAt(number.length() - 1));
        }
        string += "\n";

        //Writes the line of horizontal dividers
        string += "  ";
        for(int i = 0; i < (2 * columns) - 1; i++){
            string += HORI_DIVIDE;
        }
        string += "\n";

        //Writes each row of symbols
        for(int r = 0; r < rows; r++){
            String number = Integer.toString(r);
            string += Character.toString(number.charAt(number.length() - 1)) + VERT_DIVIDE;
            for(int c = 0; c < columns - 1; c++){
                string += grid[r][c] + " ";
            }
            string += grid[r][columns - 1] + "\n";
        }

        return string;
    }
}

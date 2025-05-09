import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.List;
import java.util.ArrayList;
//import java.lang.;

/**
 *
 * @author Sjaak Smetsers & Renske Smetsers-Weeda
 * @version 3.0 -- 20-01-2017
 */
public class MyDodo extends Dodo
{
    private int myNrOfEggsHatched;
    
    public int lastX = 0, lastY = 0, x = 0, y = 0, preSplitX = 0, preSplitY = 0, firstXAfterSplit = 0, firstYAfterSplit = 0;
    
    boolean[] fences = new boolean[4];
    
    
    public boolean foundNest = false, firstStep = false;
    
    public MyDodo() {
        super( EAST );
        myNrOfEggsHatched = 0;
    }

    public void act() {
    }

    /**
     * Move one cell forward in the current direction.
     * 
     * <P> Initial: Dodo is somewhere in the world
     * <P> Final: If possible, Dodo has moved forward one cell
     *
     */
    public void move() {
        if ( canMove() ) {
            step();
        } else {
            showError( "I'm stuck!" );
        }
    }

    /**
     * Test if Dodo can move forward, (there are no obstructions
     *    or end of world in the cell in front of her).
     * 
     * <p> Initial: Dodo is somewhere in the world
     * <p> Final:   Same as initial situation
     * 
     * @return boolean true if Dodo can move (no obstructions ahead)
     *                 false if Dodo can't move
     *                      (an obstruction or end of world ahead)
     */
    public boolean canMove() {
        if (borderAhead()){
                return false;
            } else {
                return true;
            }
        }

    /**
     * Hatches the egg in the current cell by removing
     * the egg from the cell.
     * Gives an error message if there is no egg
     * 
     * <p> Initial: Dodo is somewhere in the world. There is an egg in Dodo's cell.
     * <p> Final: Dodo is in the same cell. The egg has been removed (hatched).     
     */    
    public void hatchEgg () {
        if ( onEgg() ) {
            pickUpEgg();
            myNrOfEggsHatched++;
        } else {
            showError( "There was no egg in this cell" );
        }
    }
    
    /**
     * Returns the number of eggs Dodo has hatched so far.
     * 
     * @return int number of eggs hatched by Dodo
     */
    public int getNrOfEggsHatched() {
        return myNrOfEggsHatched;
    }
    
    /**
     * Move given number of cells forward in the current direction.
     * 
     * <p> Initial:   
     * <p> Final:  
     * 
     * @param   int distance: the number of steps made
     */
    public void jump( int distance ) {
        int nrStepsTaken = 0;               // set counter to 0
        while ( nrStepsTaken < distance ) { // check if more steps must be taken  
            move();                         // take a step
            nrStepsTaken++;                 // increment the counter
            System.out.println(nrStepsTaken);
        }
    }

    /**
     * Walks to edge of the world printing the coordinates at each step
     * 
     * <p> Initial: Dodo is on West side of world facing East.
     * <p> Final:   Dodo is on East side of world facing East.
     *              Coordinates of each cell printed in the console.
     */

    public void walkToWorldEdge(){
        while( ! borderAhead() ){
            move();
        }
    }

    /**
     * Test if Dodo can lay an egg.
     *          (there is not already an egg in the cell)
     * 
     * <p> Initial: Dodo is somewhere in the world
     * <p> Final:   Same as initial situation
     * 
     * @return boolean true if Dodo can lay an egg (no egg there)
     *                 false if Dodo can't lay an egg
     *                      (already an egg in the cell)
     */

    public boolean canLayEgg( ){
        if( onEgg() ){
            return false;
        }else{
            return true;
        }
    }  
    
    public void turn180(){
        turnRight();
        turnRight();
    }
    
    public void climbOverFence(){
        turnLeft();
        move();
        turnRight();
        move();
        move();
        turnRight();
        move();
        turnLeft();
    }
    
    public boolean grainAhead(){
        return getActorAhead(Grain.class) != null;
    }
    
    public void gotoEgg(){
        while(onEgg() == false){
            step();
        }
        
        System.out.println("found egg");
    }
    
    public void goBackToStartOfRowAndFaceBack( ){
        turn180();
        walkToWorldEdge();
        turn180();
    }
    
    public void walkToWorldEdgeClimbingOverFences(){
        while(!borderAhead()){
            if (fenceAhead()){
                climbOverFence();
            } else {
                move();   
            }
        }
    }
    
    public void pickUpGrainsAndPrintCoordinates(){
        while (!borderAhead()){
            if (onGrain()){
                pickUpGrain();
                System.out.println("grain at:\nX:" + getX() + "\nY:" + getY());
            } else {
                move();
            }
        }
        
        if (onGrain()){
            pickUpGrain();
            System.out.println("grain at:\nX:" + getX() + "\nY:" + getY());
        }
    }
    
    public void stepOneCellBackwards(){
        turn180();
        move();
        turn180();
    }
    
    public void walkToEdgeDodgingFencesAndLayEggs(){
        while (!borderAhead()){
            if (fenceAhead()){
                climbOverFence();
            } else if(onNest()){
                layEgg();
                move();
            } else {
                move();
            }
        }
        
        if(onNest()){
            layEgg();
        }
    }
    
    public void dodgeFencesAndLayEgg(){
        boolean foundNest = false;
        
        while(!borderAhead() && !foundNest){
            if (fenceAhead()){
                climbOverFence();
                if (checkForNestAndLayEgg()){
                    foundNest = true;    
                }
            } else {
                move();
                if (checkForNestAndLayEgg()){
                    foundNest = true;
                }
            }
        }
    }
    
    public void walkAroundFence(){
        while (!onEgg()){
            if (!getFenceUnder()){
                turnRight();
                move();
            } else {
                move();
            }
        }
    }
    
    public void eggTrailToNest(){
      while (!nestAhead()  && !onNest()){
          int dir = detDirOfEgg();
          setDirection(dir);
          setLastCoords();
          move();
          getNest();
        }
    }
    
    public void mazeNoDead(){
        List<int[]> route = buildRoute();
        int index = 0;
        while(!onNest()){
           int[] target = route.get(index);
           say("target:" + "x:" + target[0] + " y:" + target[1]);
           int dir = detDirOfTarget(target);
           say("dir:" + dir);
           setDirection(dir);
           move();
           if (index < route.size() && route.get(index) !=  null){
               index++;
           }
        }
    }
    
     // sub functies voor walkAroundFence
    
    private boolean getFenceUnder(){
        int myDir = getDirection();
        int x = 0, y = 0;
        
        if (fenceAhead()){
                turnLeft();
            }
            
        switch (myDir){
            case NORTH:
                x = 1;
                y = 0;
                break;
                
            case EAST:
                x = 0;
                y = 1;
                break;
            
            case SOUTH:
                x = -1;
                y = 0;
                break;
            
            case WEST:
                x = 0;
                y = -1;
                break;
        }
        
        return fenceAt(x, y);
    }
    
    private boolean fenceAt(int X, int Y){
        if (getOneObjectAtOffset(X, Y, Fence.class) != null){
            return true;
        } else {
            return false;   
        }
    }
    
    private boolean checkForNestAndLayEgg(){
        if (onNest()){
            layEgg();
            return true;
        } else {
            return false;
        }
    }
    
    //sub functies voor eggTrailToNest
    public void walkTrail(){
        
    }
    
    public int detDirOfEgg(){
        List<Egg> list = getNeighbours(1, false, Egg.class);
        int[] target = new int [2];
        
        for(Egg e : list){
            if (e.getX() != lastX || e.getY() != lastY){
                target[0] = e.getX();
                target[1] = e.getY();
            }
        }
        
        if (target[0] < getX()){
            return WEST;
        } else if(target[0] > getX()){
            return EAST;
        } else if(target[1] > getY()){
            return SOUTH;
        } else if (target[1] < getY()){
            return NORTH;
        } else {
            return -99;   
        }
    }
    
    public void setLastCoords(){
        lastX = getX();
        lastY = getY();
    }
    
    public void getNest(){
        List<Nest> nestList = getNeighbours(1, false, Nest.class);
        if (nestList.isEmpty()){

        } else {
            while(!nestAhead()){
                turnRight();
            }
            move();
        }
    }
    
    //sub voor maze
    public void turnUntilFree(){
        while(fenceAhead()){
            turnRight();
        }
        move();
    }
    
    //build route
    public List<int[]> buildRoute(){
        List<int[]> route = new ArrayList();
        
        foundNest = false;
        
        int[] splitCoords = new int[2];
        
        boolean split = false;
        
        World world = getWorld();
        
        x = getX();
        y = getY();
        lastX = getX();
        lastY = getY();
        
        if (getX() + 1 > 10 && world.getObjectsAt(x - 1, y, Fence.class).size() < 1 ){
            x = getX() - 1;
        } else if (getX() + 1 < 10 && world.getObjectsAt(x + 1, y, Fence.class).size() < 1) {
            x = getX() + 1;
        } else {
            x = getX();
        }
        
        if (getY() + 1 > 10 && world.getObjectsAt(x, y - 1, Fence.class).size() < 1 ){
            y = getY() - 1;
        } else if (getY() + 1 < 10 && world.getObjectsAt(x, y + 1, Fence.class).size() < 1 ){
            y = getY() + 1;
        } else {
            y = getY();
        }
                
        while (foundNest == false){
            if (checkAround().size() < 3){
                List<int[]> tempRoute = new ArrayList();
                
                split = true;
                
                splitCoords[0] = x;
                splitCoords[1] = y;
                
                if (x == lastX){
                    preSplitX = x;
                } else if ( x < lastX){
                    preSplitX = x + 1;
                } else if ( x > lastX){
                    preSplitX = x - 1;
                }
                
                if (y == lastY){
                    preSplitY = y;
                } else if ( y < lastY){
                    preSplitY = y + 1;
                } else if ( y > lastY){
                    preSplitY = y - 1;
                }
                
                boolean fwdFree = false;
                
                while(fwdFree == false && foundNest == false){
                        int[] test = new int[2];
                        
                        test = getNode();
                        
                        if (!firstStep){
                            firstXAfterSplit = x;
                            firstYAfterSplit = y;
                            firstStep = true;
                        }
                        
                        if (test != null){
                            tempRoute.add(test);
                            say("added to temp" + test[0] + "," + test[1]);
                        } else {
                            say("null");
                            fwdFree = true;
                        }
                }
                
                if (foundNest){
                    for (int i = 0; i < tempRoute.size(); i++){
                        say("building Route...");
                        route.add(tempRoute.get(i));
                    }
                } else {
                    say("back to split");
                    x = splitCoords[0];
                    y = splitCoords[1];
                    System.out.println(x + "," + y);

                    lastX = firstXAfterSplit;
                    lastY = firstYAfterSplit;
                    
                    firstXAfterSplit = 0;
                    firstYAfterSplit = 0;
                    
                    firstStep = false;
                    
                    System.out.println("last:" + lastX + "," + lastY);
                }
            }
            
            if (foundNest == false){
                route.add(getNode());
            }
        }
        
        return route;
    }
    //sub for routeBuilder
    public int[] getNode(){
        World world = getWorld();
        
        if (world.getObjectsAt(x + 1, y, Fence.class).size() < 1 && lastX != x + 1 && (preSplitX != x + 1 || preSplitY != y) && fences[1] == false ){
                int[] validCoords = {x, y};
                say(x + "," + y);
                lastX = x;
                lastY = y;
                x++; 
                return validCoords;
            } else if (world.getObjectsAt(x, y - 1, Fence.class).size() < 1 && lastY != y - 1 &&( preSplitY != y -1 || preSplitX != x) && fences[2] == false){
                int[] validCoords  = {x, y};
                say(x + "," + y);
                lastX = x;
                lastY = y;
                y--;
                return validCoords;
            } else if (world.getObjectsAt(x - 1, y, Fence.class).size() < 1 && lastX != x - 1 && (preSplitX != x -1 || preSplitY != y) && fences[3] == false){
                int[] validCoords  = {x, y};
                say(x + "," + y);
                lastX = x;
                lastY = y;
                x--;
                return validCoords;
            } else if (world.getObjectsAt(x, y + 1, Fence.class).size() < 1 && lastY != y + 1 && (preSplitY != y + 1 || preSplitX != x) && fences[0] == false){
                int[] validCoords  = {x, y};
                say(x + "," + y);
                lastX = x;
                lastY = y;
                y++;
                return validCoords;
            } else if (world.getObjectsAt(x, y, Nest.class).size() > 0) {
                int[] validCoords  = {x, y};
                say(x + "," + y);
                lastX = x;
                lastY = y;
                foundNest = true;
                return validCoords;
            } else {                
                say("null");
                return null;
            }
    }
    
    public List<Fence> checkAround(){
        World world = getWorld();
        
        List<Fence> around = new ArrayList();
    
        if (world.getObjectsAt(x + 1, y, Fence.class).size() > 0){
            around.add(world.getObjectsAt(x + 1, y, Fence.class).get(0));
            fences[1] = true;
            say("fence east");
        } else if (x + 1 == lastX && y == lastY || x + 1 == preSplitX && y == preSplitY){
            around.add(new Fence());
            fences[1] = true;
        } else {
            fences[1] = false;
        }
        
        if (world.getObjectsAt(x, y + 1, Fence.class).size() > 0){
            around.add(world.getObjectsAt(x, y + 1, Fence.class).get(0));
            fences[0] = true;
        } else if (y + 1 == lastY && lastX == x || y + 1 == preSplitY && preSplitX == x){
            around.add(new Fence());
            fences[0] = true;
        } else {
            fences[0] = false;
        }
        
        if (world.getObjectsAt(x - 1, y, Fence.class).size() > 0){
            around.add(world.getObjectsAt(x - 1, y, Fence.class).get(0));
            fences[3] = true;
        } else if (x - 1 == lastX && y == lastY || x - 1 == preSplitX && preSplitY == y){
            around.add(new Fence());
            fences[3] = true;
        } else {
            fences[3] = false;
        }
        
        if (world.getObjectsAt(x, y - 1, Fence.class).size() > 0){
            around.add(world.getObjectsAt(x, y - 1, Fence.class).get(0));
            fences[2] = true;
        } else if (y - 1 == lastY && x == lastX || y - 1 == preSplitY && preSplitX == x){
            around.add(new Fence());
            fences[2] = true;
        } else {
            fences[2] = false;
        }
        
        return around;
    }
    
    //qol functions
    public int detDirOfTarget(int[] target){
        if (target[0] < getX()){
            return WEST;
        } else if(target[0] > getX()){
            return EAST;
        } else if(target[1] > getY()){
            return SOUTH;
        } else if (target[1] < getY()){
            return NORTH;
        } else {
            return -99;   
        }
    }
    
    public void say(String val){
        System.out.println(val);
    }
}
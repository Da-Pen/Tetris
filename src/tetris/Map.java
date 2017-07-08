/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tetris;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Daniel Peng
 */
public class Map {
    
    private boolean gameOver = false;
    
    private int score;
    final private int DOWNMULTIPLE = 7;//the multiplier to the normal speed when down is clicked
    long lastTime; // the time of the beginning of the last interval
    final private int initialLoopTime = 600;// milliseconds per loop
    private int normalLoopTime = initialLoopTime;
    private int currLoopTime = initialLoopTime;
    final double speedUpRate = 0.99; //every time a new block is created, the ms / loop is multiplied by this
    
    private boolean leftClicked;
    private boolean rightClicked;
    private boolean upClicked;
    private boolean downClicked;
    private boolean spaceClicked;
    private boolean enterClicked;
    
    
    final private int mapW = 10;
    final private int mapH = 24;//20 of the grid squares show up on the screen, the rest are above
    final private int squareSize = 30;//pixels per square
    final private int blockSpacing = 1;//spacing between squares
    //0 = empty, 1 = landed block, 2 = active block
    private int[][] grid = new int[mapH][mapW];

    int num5 = 18;
    final private int[][][] blockTypes = {
        ////5 squares////
        { {0, 0}, {1, 0}, {2, 0}, {3, 0}, {4, 0} },
        { {0, 0}, {1, 0}, {2, 0}, {3, 0}, {3, 1} },
        { {0, 0}, {1, 0}, {2, 0}, {3, 0}, {1, 1} },
        { {0, 0}, {1, 0}, {2, 0}, {3, 0}, {2, 1} },
        { {0, 0}, {1, 0}, {2, 0}, {3, 0}, {0, 1} },
        { {0, 0}, {1, 0}, {2, 0}, {1, 1}, {1, 2} },
        { {0, 0}, {1, 0}, {2, 0}, {0, 1}, {2, 1} },
        { {0, 0}, {1, 0}, {2, 0}, {1, 1}, {2, 1} },
        { {0, 0}, {1, 0}, {2, 0}, {1, 1}, {0, 1} },
        { {0, 0}, {1, 0}, {2, 0}, {2, 2}, {2, 1} },
        { {0, 0}, {1, 0}, {1, 1}, {2, 1}, {3, 1} },
        { {0, 1}, {1, 1}, {2, 1}, {3, 0}, {2, 0} },
        { {1, 0}, {0, 1}, {1, 1}, {2, 1}, {1, 2} },
        { {0, 1}, {1, 1}, {2, 1}, {2, 0}, {0, 2} },
        { {2, 0}, {2, 1}, {1, 1}, {1, 2}, {0, 1} },
        { {0, 0}, {0, 1}, {1, 1}, {2, 1}, {2, 2} },
        { {0, 0}, {0, 1}, {1, 1}, {1, 2}, {2, 2} },        
        { {0, 0}, {0, 1}, {1, 1}, {1, 2}, {2, 1} },//index 17

        ////4 squares////
        { {0, 0}, {0, 1}, {1, 0}, {1, 1}, {0, 0} },//Square
        { {1, 0}, {2, 0}, {0, 1}, {1, 1}, {1, 0} },//"S"
        { {0, 0}, {1, 0}, {1, 1}, {2, 1}, {0, 0} },//"Z"
        { {0, 0}, {1, 0}, {2, 0}, {3, 0}, {0, 0} },//Line
        { {1, 0}, {1, 1}, {1, 2}, {0, 2}, {1, 0} },//"J"
        { {0, 0}, {1, 0}, {2, 0}, {3, 0}, {0, 0} },//"L"
        { {0, 0}, {1, 0}, {2, 0}, {1, 1}, {0, 0} },//"T"
        
        ////3 squares////
        { {0, 0}, {1, 0}, {2, 0}, {0, 0}, {0, 0} },//straight line
        { {0, 0}, {1, 0}, {0, 1}, {0, 0}, {0, 0} },//"Corner"
        
        ////2 squares////
        { {0, 0}, {1, 0}, {0, 0}, {0, 0}, {0, 0} },//line
        
        ////1 square////
        { {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0} }
    };
    public int randRange(int min, int max){
        return (int)(Math.random()*(max-min+1)) + min;
    }
    public void newBlock(){//add a new block to the map
        
        int blockNum;
        
        int fiveNum = randRange(0,3);//there is a 1/3 chance to be a 5-square block
        if(fiveNum==0){//it is a 5-square block
            blockNum = randRange(0, num5-1);
        }else{
            blockNum = randRange(num5, blockTypes.length-1);
        }
        int x = 4;
        int y = 2;
        for (int i = 0; i < 5; i++) {//for each square in the block
            grid[y+blockTypes[blockNum][i][1]][x+blockTypes[blockNum][i][0]] = 2;
        }
        //speed up the game
        normalLoopTime = (int)Math.floor(speedUpRate*(double)normalLoopTime);
    }
    public Map(){
        reset();
    }
    public void reset(){
        gameOver = false;
        score = 0;
    
        normalLoopTime = initialLoopTime;
        currLoopTime = initialLoopTime;

        leftClicked = false;
        rightClicked = false;
        upClicked = false;
        downClicked = false;
        spaceClicked = false;
        enterClicked = false;

        //0 = empty, 1 = landed block, 2 = active block
        grid = new int[mapH][mapW];
        
        newBlock();
    }
    public void update(){
        if(System.currentTimeMillis()-lastTime > currLoopTime){
            lastTime = System.currentTimeMillis();
        
            //if it is not colliding, move it down
            if(!isColliding()){
                moveActiveDown();
            }

            //if it collided, change it into a landed block and create a new active block
            else{
                //change the active block to a landed block
                for (int row = 0; row < mapH; row++) {
                    for (int col = 0; col < mapW; col++) {
                        if (grid[row][col] == 2) {
                            grid[row][col] = 1;
                        }
                    }
                }
                //create a new active block
                newBlock();
            }
            
        }
        if(leftClicked && canMoveLeft()){
            moveActiveLeft();
            leftClicked = false;
        }
        if(rightClicked && canMoveRight()){
            moveActiveRight();
            rightClicked = false;                
        }
        if(upClicked){
            rotateActive();
            upClicked = false;
        }if(downClicked){
            //speed up
            currLoopTime = normalLoopTime/DOWNMULTIPLE;
        }else{
            currLoopTime = normalLoopTime;
        }
        if(spaceClicked){
            spaceClicked();
            spaceClicked = false;
        }
        if(enterClicked && gameOver){
            reset();
        }
        //check for full rows
        checkFullRows();
        //check if the player has lost the game
        if(gameOver){
            
        }else{//if they have not lost yet, check if it they have lost by now
            if(lostGame()){
                gameOver = true;
            }
        }
        
    }
    public void spaceClicked(){
        boolean end = false;
        while(!end){
            if(!isColliding()){
                moveActiveDown();
            }else{
                end = true;
            }
        }
    }
    public boolean lostGame(){
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < mapW; col++) {
                if (grid[row][col] == 1) {
                    return true;
                }
            }
        }
        return false;
    }
    public void checkFullRows(){
        ArrayList<Integer> fullRows = new ArrayList<>();
        boolean currRowFull;
        
        for (int row = 0; row < mapH; row++) {
            currRowFull = true;
            for (int col = 0; col < mapW; col++) {
                if (grid[row][col] != 1) {
                    currRowFull = false;
                }
            }
            if(currRowFull){
                fullRows.add(row);
            }
        }
        if(fullRows.size()>0){
            for (Integer fullRow : fullRows) {
                removeRow(fullRow);
            }
        }
    }
    public void removeRow(int fullRow){
        //increase the score
        score++;
        //convert the row to empyt spaces
        for (int col = 0; col < mapW; col++) {
            grid[fullRow][col] = 0;
        }
        //move all blocks above it down
        for (int row = fullRow; row >= 0; row--) {
            for (int col = 0; col < mapW; col++) {
                if (grid[row][col] == 1) {
                    grid[row+1][col] = 1;
                    grid[row][col] = 0;
                }
            }
        }
    }
    public void moveActiveDown(){
        for (int row = mapH-2; row >= 0; row--) {
            for (int col = 0; col < mapW; col++) {
                if (grid[row][col] == 2) {
                    grid[row+1][col]=2;
                    grid[row][col]=0;
                }
            }
        }
    }
    public void moveActiveLeft(){
        for (int row = 0; row < mapH; row++) {
            for (int col = 0; col < mapW; col++) {
                if (grid[row][col] == 2) {
                    grid[row][col-1]=2;
                    grid[row][col]=0;
                }
            }
        }
    }
    public void moveActiveRight(){
        for (int row = 0; row < mapH; row++) {
            for (int col = mapW-1; col >=0; col--) {
                if (grid[row][col] == 2) {
                    grid[row][col+1]=2;
                    grid[row][col]=0;
                }
            }
        }
    }
    public boolean mirrorDiagonal(){
        //get the corners of the rectangle bounding the block
        int left = mapW;
        int top = mapH;
        int right = 0;
        int bottom = 0;
        
        int[][] grid2 = new int[mapH][mapW];
        for (int i = 0; i < mapH; i++) {
          grid2[i] = Arrays.copyOf(grid[i], mapW);
        }
        
        
        //check for top left bottom right. At the same time, remove the active block from grid2
        for (int row = 0; row < mapH; row++) {
            for (int col = 0; col < mapW; col++) {
                if (grid[row][col] == 2) {
                    if(row < top) top = row;
                    if(row > bottom) bottom = row;
                    if(col < left) left = col;
                    if(col > right) right = col;
                    
                    grid2[row][col] = 0;
                }
            }
        }
        //the horizontal shift after flipping
        int shiftX = left - top;
        //the vertical shift
        int shiftY = top - left;        
        
        boolean possible = true;
        
        //for each square in the active block, move it to the new position in grid2
        for (int row = 0; row < mapH; row++) {
            for (int col = 0; col < mapW; col++) {
                if (grid[row][col] == 2) {
                    //check if possible (does not pass boundaries of map and it is not occupied by a landed block)
                    if(col+shiftY < mapH && row+shiftX < mapW
                    && grid2[col+shiftY][row+shiftX] == 0){
                            
                        grid2[col+shiftY][row+shiftX] = 2;
                        
                    }else{
                        possible = false;
                    }
                }
            }
        
        }
        if(possible){
            for (int i = 0; i < mapH; i++) {
              grid[i] = Arrays.copyOf(grid2[i], mapW);
            }
        }
        return possible;
    }
    public void mirrorVertical(){
        
        //get the corners of the rectangle bounding the block
        int left = mapW;
        int top = mapH;
        int right = 0;
        int bottom = 0;
        
        int[][] grid2 = new int[mapH][mapW];
        for (int i = 0; i < mapH; i++) {
          grid2[i] = Arrays.copyOf(grid[i], mapW);
        }
        
        boolean possible = true;
        //check for top left bottom right. At the same time, remove the active block from grid2
        for (int row = 0; row < mapH; row++) {
            for (int col = 0; col < mapW; col++) {
                if (grid[row][col] == 2) {
                    if(row < top) top = row;
                    if(row > bottom) bottom = row;
                    if(col < left) left = col;
                    if(col > right) right = col;
                    
                    grid2[row][col] = 0;
                }
            }
        }
        
        int shiftY = bottom + top;
        for (int row = 0; row < mapH; row++) {
            for (int col = 0; col < mapW; col++) {
                if (grid[row][col] == 2) {
                    //check if possible (does not pass boundaries of map and it is not occupied by a landed block)
                    if(grid2[-row+shiftY][col] == 0){
                        grid2[-row+shiftY][col] = 2;
                    }else{
                        possible = false;
                    }
                }
            }
        }
        if(possible){
            for (int i = 0; i < mapH; i++) {
              grid[i] = Arrays.copyOf(grid2[i], mapW);
            }
        }
    }
    public void rotateActive(){
        //the block is rotated so that the top left corner of the block remains in the same spot.
        //to rotate it, it is first flipped diagonally, then flipped vertically.
        
        //if it is possible to mirror it diagonally, mirror it vertically.
        //for both methods, the grid will not be changed if it is not possible.
        if(mirrorDiagonal()){
            mirrorVertical();
        }
        
    }
    public int getSpeed(){
        return currLoopTime;
    }
    public boolean isColliding(){
        for (int row = 0; row < mapH; row++) {
            for (int col = 0; col < mapW; col++) {
                if (grid[row][col] == 2) {
                    //if reached the bottom of the map or if it has hit a landed block
                    if(row >= mapH-1 || grid[row+1][col] == 1){  
                        return true;
                        
                    }
                }
            }
        }
        return false;
    }
    public boolean canMoveLeft(){
        //check if it can move left
        for (int row = 0; row < mapH; row++) {
            for (int col = 0; col < mapW; col++) {
                if (grid[row][col] == 2) {
                    if(col<=0){
                        return false;
                    }
                    if(grid[row][col-1]==1){
                        return false;
                    }
                }
            }
        }
        return true;
    }
    public boolean canMoveRight(){
        //check if it can move left
        for (int row = 0; row < mapH; row++) {
            for (int col = 0; col < mapW; col++) {
                if (grid[row][col] == 2) {
                    if(col>=mapW-1){
                        return false;
                    }
                    if(grid[row][col+1]==1){
                        return false;
                    }
                }
            }
        }
        return true;
    }
    public void draw(Graphics g){
        int yShift = 120;//30x4
        Graphics2D g2d = (Graphics2D) g;
        
        if(gameOver){
            g2d.setColor(Color.LIGHT_GRAY);
        }
        
        for (int row = 0; row < mapH; row++) {
            for (int col = 0; col < mapW; col++) {
                //if it is active or landed
                if (grid[row][col] == 1 || grid[row][col] == 2) {
                    g2d.fillRect(col*squareSize + blockSpacing, row*squareSize-yShift + blockSpacing, squareSize - 2*blockSpacing, squareSize - 2*blockSpacing);
                }
            }
        }
        //display score
        Font f = new Font("Verdana", Font.PLAIN, 15);
        g2d.setColor(Color.red);
        g2d.setFont(f);
        g2d.drawString("Score: " + score, 10, 20);        
        
        if(gameOver){
            f = new Font("Verdana", Font.BOLD, 80);
            g2d.setFont(f);
            g2d.drawString("GAME", 18, mapH*squareSize/2 - 100);
            g2d.drawString("OVER", 24, mapH*squareSize/2);
            f = new Font("Verdana", Font.BOLD, 14);
            g2d.setFont(f);
            g2d.drawString("(press ENTER to start a new game)", 18, mapH*squareSize/2 + 50);
        }
    }
    
    public void keyPressed(KeyEvent e) {
        double key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
           leftClicked = true;
        }

        if (key == KeyEvent.VK_RIGHT) {
           rightClicked = true;
        }

        if (key == KeyEvent.VK_UP) {
           upClicked = true;           
        }

        if (key == KeyEvent.VK_DOWN) {
           downClicked = true;        
        }
        
        if (key == KeyEvent.VK_SPACE) {
            spaceClicked = true;
        }
        if (key == KeyEvent.VK_ENTER) {
            enterClicked = true;
        }
    }

    public void keyReleased(KeyEvent e) {
        
        double key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
           leftClicked = false;            
        }

        if (key == KeyEvent.VK_RIGHT) {
           rightClicked = false;            
                 
        }

        if (key == KeyEvent.VK_UP) {
           upClicked = false;            
        }

        if (key == KeyEvent.VK_DOWN) {
           downClicked = false;            
        }
        
        if (key == KeyEvent.VK_SPACE) {
           spaceClicked = false;            
        }
        
        if (key == KeyEvent.VK_ENTER) {
           enterClicked = false;            
        }
    }
    
    
    
}
//Erhan Yaln�z   150117905
//Erdem A�ca     150117043
//The game consists of 5 different levels. The purpose of this game is to connect the pipes to provide that the ball moves.
// Game class consists of the game's back-end, mechanics and actions.

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

//This enum is created for method gameEnded and will be explained.
enum Position{TOP,LEFT,RIGHT,DOWN}

class Game {
    // array to store cells in order of indexes: x and y
    public CellData[][] board;

    // this variable will store last unlocked level in case of level change, saving and loading.
    public int unlockedLevel;

    public Game(){
        //Set default value of unlockedLevel to 0 inside constructor so in case of level cannot be loaded it will be first level(0).
        unlockedLevel=0;

        //Set board to 4 by 4 array to simulate gameboard for pipe game.
        board=new CellData[4][4];
    }

    //this method will generate gameboard inside the backend using inputFilePath
    public void generateMapData(String inputFilePath) {
        //Read level from inputFilePath line by line with BufferedReader
        BufferedReader br = null;
        //We will read lines to this string from BufferedReader
        String line = "";
        try {
            br = new BufferedReader(new FileReader(inputFilePath));
            int i = 0;
            //Read while more lines remain inside file.
            while ((line = br.readLine()) != null) {
                // Split by comma(,) in order to get id,type,property seperated and store inside info array.
                String[] info = line.split(",");
                try {
                    //Build CellData object according to values inside info array. In order of id,type,property.
                    CellData cellData = new CellData(Integer.parseInt(info[0]), info[1], info[2]);
                    //add to board array as if it was a coordinate system starting from 0,0 to 4,4 in order of x,y
                    //i/4 gives us the row (y)
                    //i%4 gives us the column (x)
                    board[i % 4][i / 4] =cellData;
                    i++;
                } catch (NumberFormatException e) {
                    //If an error happens while converting id to integer, print error to debug.
                    System.out.printf("Error at line -> %s\n", line);
                    //Print error message to debug.
                    System.out.println(e.getMessage());
                }
            }
        } catch (FileNotFoundException e) {
            //Print error message if an error happens while reading
            e.printStackTrace();
        } catch (IOException e) {
            //Print error message if an error happens while reading
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    //close BufferedReader whatever happens.
                    br.close();
                } catch (IOException e) {
                    //If cannot be closed print error message.
                    e.printStackTrace();
                }
            }
        }
    }

    //This method checks if move is legal(applicable) by given coordinates of cell that is being moved (x0,y0) and cell that is new position for cell to stay (x1,y1) finally it returns legality of move.
    public boolean isLegal(int x0,int y0,int x1,int y1){
        if(board[x0][y0].type.equals("Starter")||board[x0][y0].type.equals("End")||board[x0][y0].type.equals("PipeStatic")||board[x0][y0].property.equals("Free")){
            //If block is immoveable (Starter pipe, End pipe, PipeStatic or Empty Free) than return false.
            return false;
        }else{
            if(board[x1][y1].property.equals("Free")){
                //Check if cell that is getting moved is near the cell that is the new position.
                //This logic is applied by getting absolute difference between positions.(Row or Column)
                return (Math.abs(x0-x1)==1 && Math.abs(y0-y1)==0)||(Math.abs(y0-y1)==1 && Math.abs(x0-x1)==0);
            }else{
                return false;
            }
        }
    }

    //this method just swaps cells given coordinates of cells (x0,y0) and (x1,y1).
    public void makeMove(int x0,int y0,int x1,int y1){
        CellData temp = board[x0][y0];
        board[x0][y0] = board[x1][y1];
        board[x1][y1] = temp;
    }

    //This method checks all pipes if they are all connected returns true else returns false as the end of game depends on connection of pipes.
    public boolean gameEnded(){
        //We have to remember Pipe before to continue to another pipe.
        //This because there is 2 ways to enter for all pipes if we know pipe before we can determine which way we entered the pipe by getting differences.
        int x0 = 0 ,y0 = 0;
        //This is the position for current pipe we will iterate with this variables.
        int x1,y1;
        //We will change this result to false if pipes are connection between pipes didn't go as expected.
        boolean result=true;
        //This loop is used to find Starter pipe.
        while(!board[x0][y0].type.equals("Starter")){
            //Increment column while Starter pipe is not found
            x0++;
            if(x0>=4){
                //Increment row number if end of column is reached.
                y0++;
                //Set column to zero as we continue with new row.
                x0 = 0;
            }
        }
        if(board[x0][y0].property.equals("Vertical")) {
            //If Starter pipe is Vertical then next pipe will be under first. So x1 will be same as x0 and y1 will be y0 + 1
            x1 = x0;
            y1 = y0 + 1;
        }else{
            //If Starter pipe is Horizontal then next pipe will be at left side of first. So y1 will be same as y0 and x1 will be x0 - 1
            x1 = x0 - 1;
            y1 = y0;
        }
        //We create an infinite loop but we will break the loop after reaching End pipe or the pipes are connected faulty.
        while(true) {
            //This is a type created before with enum to store where the pipe before is connected to current pipe.
            Position positionEntered;
            //This variables used to get differences between pipes, later they will be used in order to determine where pipe before is connected to current pipe.
            int deltaX = x1 - x0;
            int deltaY = y1 - y0;
            //If deltaX is 0  pipes are at the same column. Then there is two possibilities to connect from either LEFT or RIGHT.
            if (deltaX != 0) {
                //If deltaX is 1 then the pipe after is at the left-side of current pipe.
                if (deltaX == 1) {
                    positionEntered = Position.LEFT;
                }
                //If deltaX is not 1 so deltaX is -1 then the pipe after is at the left-side of current pipe.
                else {
                    positionEntered = Position.RIGHT;
                }
            }
            //If deltaX is not 0  pipes aren't at the same column. Then there is two possibilities to connect from either TOP or DOWN.
            else {
                //If deltaY is 1 then the pipe after is at the top of current pipe.
                if (deltaY == 1) {
                    positionEntered = Position.TOP;
                }
                //If deltaY is 1 so deltaY is -1 then the pipe after is at the down of current pipe.
                else {
                    positionEntered = Position.DOWN;
                }
            }
            //currentCell will store current cell to get type and property.
            //currrentCell value is taken from board by giving positions as indexes.
            CellData currentCell = board[x1][y1];
            //If currentCell is a normal pipe or an immovable pipe then block below will execute.
            if (currentCell.type.equals("Pipe")||currentCell.type.equals("PipeStatic")) {
                //If currentCell is a curved pipe of an arc from left to top then block below will execute.
                if (currentCell.property.equals("00")) {
                    //If currentCell is a curved pipe of an arc from left to top and entered from top then block below will execute.
                    if (positionEntered == Position.TOP) {
                        //If currentCell is a curved pipe of an arc from left to top and entered from top we have to assign (x1,y1) to positions of pipe before then decrement column of current pipe.
                        y0 = y1;
                        x0 = x1--;
                    }
                    //If currentCell is a curved pipe of an arc from left to top and entered from left-side then block below will execute.
                    else {
                        //If currentCell is a curved pipe of an arc from left to top and entered from top we have to assign (x1,y1) to positions of pipe before then decrement row of current pipe.
                        y0 = y1--;
                        x0 = x1;
                    }
                }
                //If currentCell is a curved pipe of an arc from top to right then block below will execute.
                else if (currentCell.property.equals("01")) {
                    //If currentCell is a curved pipe of an arc from top to right and entered from top then block below will execute.
                    if (positionEntered == Position.TOP) {
                        //If currentCell is a curved pipe of an arc from top to right and entered from top we have to assign (x1,y1) to positions of pipe before then increment column of current pipe.
                        y0 = y1;
                        x0 = x1++;
                    } else {
                        //If currentCell is a curved pipe of an arc from top to right and entered from right we have to assign (x1,y1) to positions of pipe before then decrement row of current pipe.
                        y0 = y1--;
                        x0 = x1;
                    }
                }
                //If currentCell is a curved pipe of an arc from left to down then block below will execute.
                else if (currentCell.property.equals("10")) {
                    //If currentCell is a curved pipe of an arc from left to down and entered from left then block below will execute.
                    if (positionEntered == Position.LEFT) {
                        //If currentCell is a curved pipe of an arc from left to down and entered from left we have to assign (x1,y1) to positions of pipe before then increment row of current pipe.
                        y0 = y1++;
                        x0 = x1;
                    } else {
                        //If currentCell is a curved pipe of an arc from left to down and entered from down we have to assign (x1,y1) to positions of pipe before then decrement column of current pipe.
                        y0 = y1;
                        x0 = x1--;
                    }
                }
                //If currentCell is a curved pipe of an arc from down to right then block below will execute.
                else if (currentCell.property.equals("11")) {
                    //If currentCell is a curved pipe of an arc from down to right and entered from down then block below will execute.
                    if (positionEntered == Position.DOWN) {
                        //If currentCell is a curved pipe of an arc from down to right and entered from down we have to assign (x1,y1) to positions of pipe before then increment column of current pipe.
                        y0 = y1;
                        x0 = x1++;
                    } else {
                        //If currentCell is a curved pipe of an arc from down to right and entered from down we have to assign (x1,y1) to positions of pipe before then increment row of current pipe.
                        y0 = y1++;
                        x0 = x1;
                    }
                }
                //If currentCell is a horizontal pipe then block below will execute.
                else if (currentCell.property.equals("Horizontal")) {
                    //If currentCell is a horizontal pipe and entered from left-side then block below will execute.
                    if (positionEntered == Position.LEFT) {
                        //If currentCell is a horizontal pipe and entered from left-side we have to assign x1 to column of pipe before then increment column of current pipe.
                        x0 = x1++;
                    } else {
                        //If currentCell is a horizontal pipe and entered from right-side we have to assign x1 to column of pipe before then decrement column of current pipe.
                        x0 = x1--;
                    }
                } else {
                    if (positionEntered == Position.TOP) {
                        //If currentCell is a vertical pipe and entered from top we have to assign y1 to column of pipe before then increment row of current pipe.
                        y0 = y1++;
                    } else {
                        //If currentCell is a vertical pipe and entered from top we have to assign y1 to column of pipe before then decrement row of current pipe.
                        y0 = y1--;
                    }
                }
            }
            //If current pipe is End then block below will execute.
            else if(currentCell.type.equals("End")){
                //If current pipe is End and entered from left and End pipe is horizontal then all pipes are connected. So result should stay true and loop is broken.
                if(positionEntered==Position.LEFT && currentCell.property.equals("Horizontal")){
                    break;
                }
                //If current pipe is End and entered from down and End pipe is vertical then all pipes are connected. So result should stay true and loop is broken.
                else if(positionEntered==Position.DOWN && currentCell.property.equals("Vertical")){
                    break;
                }
                //If End pipe is connected any another way then specified above it is a faulty connection so result is false and loop is broken.
                else{
                    result=false;
                    break;
                }
            }
            //If any other type (Ex: Empty) comes rather than conditions specified above then connection is faulty so loop is broken.
            else{
                result=false;
                break;
            }
        }
        //Final result of connections is returned here.
        return result;

    }
}
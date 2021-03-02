//Erhan Yaln�z   150117905
//Erdem A�ca     150117043
//The game consists of 5 different levels. The purpose of this game is to connect the pipes to provide that the ball moves.
//The Main Class is the class that contains the front-end of the application and the necessary algorithms.
import javafx.animation.PathTransition;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.util.Formatter;


public class Main extends Application {
    private Game game;
    private int level;
    private int numberOfMoves;
    private String[] gameDatas;
    private Rectangle[][] boardGrid;
    private GridPane boardPane;
    private Label gameStatusLabel;
    private Label moveCounter;
    private Circle ball;
    private Scene welcomeScene;
    private Scene gameScene;
    private final ObjectProperty<Point2D> selectedRectangle = new SimpleObjectProperty<>();
    @Override
    public void start(Stage window) throws Exception{
        //We create an instance of our class Game to start backend of our game.
        game = new Game();
        //We create Rectangle array which we later use to show our game elements in gui.
        boardGrid = new Rectangle[4][4];
        //We define gameDatas string array here which we later use when generating gameboard.
        gameDatas = new String[]{"level1.txt","level2.txt","level3.txt","level4.txt","level5.txt"};
        //We load unlocked level from save file with loadGame method
        loadGame();
        //We construct GridPane layout which will later store our Rectangles in 4x4 row and column structure.
        boardPane = new GridPane();
        //We set padding for this our boardPane (GridPane object)
        boardPane.setPadding(new Insets(46,46,46,46));
        //We set gap between Rectangles to 0
        boardPane.setVgap(0);
        boardPane.setHgap(0);
        //We define our rectangles, set constraints and add them to boardPane with this loop.
        for(int i=0;i<16;i++){
            //We construct each Rectangle.
            boardGrid[i %4 ][i / 4] = new Rectangle(92,92,Color.WHITE);
            //We set constraints for each Rectangle.
            GridPane.setConstraints(boardGrid[i%4][i/4],i%4,i/4);
            //We add them to boardGrid (GridPane object)
            boardPane.add(boardGrid[i%4][i/4],i%4,i/4);
        }
        //We construct our ball (Circle object) with 10 radius and filled with ImagePattern of "ball.png".
        ball = new Circle(10,new ImagePattern(new Image(new File("ball.png").toURI().toString())));
        //We place ball to Center of Starter element as it is position is not changing we hardcoded it to x=92 y=91
        ball.setCenterX(92);
        ball.setCenterY(91);
        //We place a nextLevelButton (Button object) to check if level is finished.
        Button nextLevelButton = new Button("Check");
        //We define how nextLevelButton behave when clicked.
        nextLevelButton.setOnAction(e -> {
            //We use method of Game class object game to find if game is ended which means all the pipes are connected correctly.
            if(game.gameEnded()){
                //We check if all levels unlocked if unlocked if not the block below will execute
                if(game.unlockedLevel<4) {
                    //We play animation and store it inside to determine the behavior after it is finished by placing a event handler.
                    PathTransition p = playAnimation();
                    p.setOnFinished(t -> {
                        //When animation is finished we set move number to 0
                        numberOfMoves = 0;
                        //Then we increment current level
                        level++;
                        //if last level is done we set level to first level
                        if(level>4){
                            level = 0;
                        }
                        //We generate board of next game and increment unlocked level.
                        nextLevel();
                        //We set gameStatusLabel (Label object) to text we defined below and update level number.
                        gameStatusLabel.setText("Try to solve level-"+Integer.toString(level+1));
                    });
                }else{
                    //We play animation and store it inside to determine the behavior after it is finished by placing a event handler.
                    PathTransition p = playAnimation();
                    p.setOnFinished(t -> {
                        //When animation is finished we set move number to 0
                        numberOfMoves = 0;
                        //Then we increment current level
                        level++;
                        //if last level is done we set level to first level
                        if(level>4){
                            level = 0;
                        }
                        //We generate backend board (4x4 CellData array) of next level.
                        game.generateMapData(gameDatas[level]);
                        //We generate frontend board (4x4 Rectangle array) of next level.
                        generateGridPane();
                        //We set gameStatusLabel (Label object) to text we defined below and update level number.
                        gameStatusLabel.setText("Try to solve level-"+Integer.toString(level+1));
                    });
                }
            }else{
                //We set gameStatusLabel (Label object) to text we defined below and update level number if game is not ended yet (All pipes are not connected yet.).
                gameStatusLabel.setText("Something seems to be wrong. Try again to solve level-"+Integer.toString(level+1));
                //We end ActionEvent
                e.consume();
            }
        });
        //We create mainMenuButton (Button object) to return to main menu
        Button mainMenuButton = new Button("Return to Main Menu");
        //We define the behavior when button is clicked by changing scene to welcomeScene.
        mainMenuButton.setOnAction(e -> window.setScene(welcomeScene));
        //We construct our gameStatusLabel (Label object) and update level number with text.
        gameStatusLabel = new Label("Try to solve level-"+Integer.toString(game.unlockedLevel+1));
        //We construct our moveCounter (Label object) which will show move number.
        moveCounter = new Label();
        //We update text to numberOfMoves
        moveCounter.setText("Moves: "+numberOfMoves);
        //We create our layouts
        HBox gameStatusLayout = new HBox(10);
        VBox gameLayout = new VBox(10);
        Pane gameView = new Pane();
        //We place our GUI elements nextLevelButton,gameStatusLabel,moveCounter,mainMenuButton inside gameStatusLayout
        gameStatusLayout.getChildren().addAll(nextLevelButton,gameStatusLabel,moveCounter,mainMenuButton);
        //We place our boardPane (GridPane object) and ball inside gameView layout
        gameView.getChildren().addAll(boardPane,ball);
        //We place gameView and gameStatusLayout inside gameLayout
        gameLayout.getChildren().addAll(gameView,gameStatusLayout);
        //We create our scene that will show our game by constructing it with gameLayout
        gameScene = new Scene(gameLayout,600,600);
        //We create a title for main menu
        Label title = new Label("Welcome to PipePuzzle Game");
        //We set it to a font to make it look more like a title
        title.setFont(new Font("Helvetica",40));
        //We create a VBox object which will store levels.
        VBox levelsBox = new VBox(20);
        //We create a buttons for levels
        Button[] levelButtons = new Button[5];
        //We create level 1 button.
        levelButtons[0]=new Button("Play Level 1");
        //We define what level 1 button will do.
        levelButtons[0].setOnAction(e -> {
            //Set level to first level.
            level = 0;
            //Set move number to zero
            numberOfMoves = 0;
            //Update moveCounter text
            moveCounter.setText("Moves: "+numberOfMoves);
            //We generate backend board (4x4 CellData array) of first level.
            game.generateMapData(gameDatas[0]);
            //We generate frontend board (4x4 Rectangle array) of first level.
            generateGridPane();
            //We update gameStatusLabel
            gameStatusLabel.setText("Try to solve level-1");
            //We set scene to show game
            window.setScene(gameScene);
        });
        //We create level 2 button.
        levelButtons[1]=new Button("Play Level 2");
        //We define what level 2 button will do.
        levelButtons[1].setOnAction(e -> {
            if(game.unlockedLevel>0) {
                //Set level to second level.
                level = 1;
                //Set move number to zero
                numberOfMoves = 0;
                //Update moveCounter text
                moveCounter.setText("Moves: "+numberOfMoves);
                //We generate backend board (4x4 CellData array) of second level.
                game.generateMapData(gameDatas[1]);
                //We generate frontend board (4x4 Rectangle array) of second level.
                generateGridPane();
                //We update gameStatusLabel
                gameStatusLabel.setText("Try to solve level-2");
                //We set scene to show game
                window.setScene(gameScene);
            }
        });
        //We create level 3 button.
        levelButtons[2]=new Button("Play Level 3");
        //We define what level 3 button will do.
        levelButtons[2].setOnAction(e -> {
            if(game.unlockedLevel>1) {
                level = 2;
                //Set move number to zero
                numberOfMoves = 0;
                //Update moveCounter text
                moveCounter.setText("Moves: "+numberOfMoves);
                //We generate backend board (4x4 CellData array) of third level.
                game.generateMapData(gameDatas[2]);
                //We generate frontend board (4x4 Rectangle array) of third level.
                generateGridPane();
                //We update gameStatusLabel
                gameStatusLabel.setText("Try to solve level-3");
                //We set scene to show game
                window.setScene(gameScene);
            }
        });
        //We create level 4 button.
        levelButtons[3]=new Button("Play Level 4");
        //We define what level 4 button will do.
        levelButtons[3].setOnAction(e -> {
            if(game.unlockedLevel>2) {
                level = 3;
                //Set move number to zero
                numberOfMoves = 0;
                //Update moveCounter text
                moveCounter.setText("Moves: "+numberOfMoves);
                //We generate backend board (4x4 CellData array) of fourth level.
                game.generateMapData(gameDatas[3]);
                //We generate frontend board (4x4 Rectangle array) of fourth level.
                generateGridPane();
                //We update gameStatusLabel
                gameStatusLabel.setText("Try to solve level-4");
                //We set scene to show game
                window.setScene(gameScene);
            }
        });
        //We create level 5 button.
        levelButtons[4]=new Button("Play Level 5");
        //We define what level 5 button will do.
        levelButtons[4].setOnAction(e -> {
            if(game.unlockedLevel>3) {
                level = 4;
                //Set move number to zero
                numberOfMoves = 0;
                //Update moveCounter text
                moveCounter.setText("Moves: "+numberOfMoves);
                //We generate backend board (4x4 CellData array) of fifth level.
                game.generateMapData(gameDatas[4]);
                //We generate frontend board (4x4 Rectangle array) of fifth level.
                generateGridPane();
                //We update gameStatusLabel
                gameStatusLabel.setText("Try to solve level-5");
                //We set scene to show game
                window.setScene(gameScene);
            }
        });
        //We add all buttons to levelsBox (VBox object)
        levelsBox.getChildren().addAll(levelButtons);
        //We create the main layout
        BorderPane welcomePane = new BorderPane();
        //We set title to Top section of welcomePane (BorderPane object)
        welcomePane.setTop(title);
        //We set levelsBox to Center section of welcomePane (Border object)
        welcomePane.setCenter(levelsBox);
        //We construct our main scene welcomeScene
        welcomeScene = new Scene(welcomePane,600,600);
        //We set scene to welcomeScene to open when application starts
        window.setScene(welcomeScene);
        //We set title of window to "PipePuzzle"
        window.setTitle("PipePuzzle");
        //We defined behavior when application is closed normally by saving with method saveGame method.
        window.setOnCloseRequest(e -> saveGame());
        //We show window.
        window.show();
    }

    //This method unlocks next level and generates gameboard (backend (4x4 CellData array) and frontend (4x4 Rectangle array)).
    private void nextLevel(){
        //Increment unlockedLevel by one to unlock next level
        game.unlockedLevel++;
        //Generate backend (4x4 CellData array) with parameter gameDatas[level]. gameDatas is an array which stores input level file names.
        game.generateMapData(gameDatas[level]);
        //Generate frontend (4x4 Rectangle array) (boardGrid)
        generateGridPane();
    }

    //This method generates boardGrid (4x4 Rectangle array) in order to fill rectangles with images to show in frontend.
    private void generateGridPane(){
        //This loop will run for all boardGrid elements to fill all rectangles with matching images.
        for(int i=0;i<16;i++){
            //Get type of cell to find matching image.
            String type = game.board[i % 4][i / 4].type;
            //Get property of cell to find matching image.
            String property = game.board[i % 4][i / 4].property;
            //Create Image object which we later construct with if-else.
            Image temp;
            //If the type is Pipe then block below will execute.
            if(type.equals("Pipe")){
                //If the type is Pipe and property is Vertical then block below will execute.
                if(property.equals("Vertical")) {
                    //If the type is Pipe and property is Vertical then matching image "Pipe_Vertical.png" and construct Image object by URI path using File object.
                    temp=new Image(new File("Pipe_Vertical.png").toURI().toString());
                    //boardGrid element is filled with ImagePattern constructed with temp image which we created above.
                    (boardGrid[i % 4][i / 4]).setFill(new ImagePattern(temp));
                    //Pipe Vertical is a moveable element so we set it draggable with our setDraggable method.
                    setDraggable(boardGrid[i%4][i/4]);
                }
                //If the type is Pipe and property is Horizontal then block below will execute.
                else if(property.equals("Horizontal")){
                    //If the type is Pipe and property is Horizontal then matching image "Pipe_Horizontal.png" and construct Image object by URI path using File object.
                    temp=new Image(new File("Pipe_Horizontal.png").toURI().toString());
                    //boardGrid element is filled with ImagePattern constructed with temp image which we created above.
                    (boardGrid[i % 4][i / 4]).setFill(new ImagePattern(temp));
                    //Pipe Horizontal is a moveable element so we set it draggable with our setDraggable method.
                    setDraggable(boardGrid[i%4][i/4]);
                }
                //If the type is Pipe and property is 00 then block below will execute.
                else if(property.equals("00")){
                    //If the type is Pipe and property is 00 then matching image "Pipe_00.png" and construct Image object by URI path using File object.
                    temp=new Image(new File("CurvedPipe_00.png").toURI().toString());
                    //boardGrid element is filled with ImagePattern constructed with temp image which we created above.
                    (boardGrid[i % 4][i / 4]).setFill(new ImagePattern(temp));
                    //Pipe 00 is a moveable element so we set it draggable with our setDraggable method.
                    setDraggable(boardGrid[i%4][i/4]);
                }
                //If the type is Pipe and property is 01 then block below will execute.
                else if(property.equals("01")){
                    //If the type is Pipe and property is 01 then matching image "Pipe_00.png" and construct Image object by URI path using File object.
                    temp=new Image(new File("CurvedPipe_01.png").toURI().toString());
                    //boardGrid element is filled with ImagePattern constructed with temp image which we created above.
                    (boardGrid[i % 4][i / 4]).setFill(new ImagePattern(temp));
                    //Pipe 01 is a moveable element so we set it draggable with our setDraggable method.
                    setDraggable(boardGrid[i%4][i/4]);
                }
                //If the type is pipe and property is 10 then block below will execute.
                else if(property.equals("10")){
                    //If the type is Pipe and property is 01 then matching image "Pipe_00.png" and construct Image object by URI path using File object.
                    temp=new Image(new File("CurvedPipe_10.png").toURI().toString());
                    //boardGrid element is filled with ImagePattern constructed with temp image which we created above.
                    (boardGrid[i % 4][i / 4]).setFill(new ImagePattern(temp));
                    //Pipe 10 is a moveable element so we set it draggable with our setDraggable method.
                    setDraggable(boardGrid[i%4][i/4]);
                }
                //If the type is Pipe and property is 11 then block below will execute.
                else{
                    //If the type is Pipe and property is 11 then matching image "Pipe_00.png" and construct Image object by URI path using File object.
                    temp=new Image(new File("CurvedPipe_11.png").toURI().toString());
                    //boardGrid element is filled with ImagePattern constructed with temp image which we created above.
                    (boardGrid[i % 4][i / 4]).setFill(new ImagePattern(temp));
                    //Pipe 11 is a moveable element so we set it draggable with our setDraggable method.
                    setDraggable(boardGrid[i%4][i/4]);
                }
            }
            //If the type is Empty block below will execute.
            else if(type.equals("Empty")){
                //If the type is Empty and property is Free then block below will execute.
                if(property.equals("Free")){
                    //If the type is Empty and property is Free then matching image "Empty_Free.png" and construct Image object by URI path using File object.
                    temp=new Image(new File("Empty_Free.png").toURI().toString());
                    //boardGrid element is filled with ImagePattern constructed with temp image which we created above.
                    (boardGrid[i % 4][i / 4]).setFill(new ImagePattern(temp));
                    //Empty Free is a element that we can drag other elements into so we set it as a slot with our setSlot method.
                    setSlot(boardGrid[i%4][i/4]);
                }
                //If the type is Empty and property is none then block below will execute.
                else{
                    //If the type is Empty and property is none then matching image "Empty.png" and construct Image object by URI path using File object.
                    temp=new Image(new File("Empty.png").toURI().toString());
                    //boardGrid element is filled with ImagePattern constructed with temp image which we created above.
                    (boardGrid[i % 4][i / 4]).setFill(new ImagePattern(temp));
                    //Empty none is a moveable element so we set it draggable with our setDraggable method.
                    setDraggable(boardGrid[i%4][i/4]);
                }
            }
            //If the type is PipeStatic block below will execute.
            else if(type.equals("PipeStatic")){
                //If the type is PipeStatic and property Horizontal block below will execute.
                if(property.equals("Horizontal")){
                    //If the type is PipeStatic and property is Horizontal then matching image "PipeStatic_Horizontal.png" and construct Image object by URI path using File object.
                    temp=new Image(new File("PipeStatic_Horizontal.png").toURI().toString());
                    //boardGrid element is filled with ImagePattern constructed with temp image which we created above.
                    (boardGrid[i % 4][i / 4]).setFill(new ImagePattern(temp));
                }
                //If the type is PipeStatic and property 00 block below will execute.
                else if(property.equals("00")){
                    //If the type is PipeStatic and property is 00 then matching image "PipeStatic_00.png" and construct Image object by URI path using File object.
                    temp=new Image(new File("PipeStatic_00.png").toURI().toString());
                    //boardGrid element is filled with ImagePattern constructed with temp image which we created above.
                    (boardGrid[i % 4][i / 4]).setFill(new ImagePattern(temp));
                }
                //If the type is PipeStatic and property 01 block below will execute.
                else if(property.equals("01")){
                    //If the type is PipeStatic and property is 01 then matching image "PipeStatic_01.png" and construct Image object by URI path using File object.
                    temp=new Image(new File("PipeStatic_01.png").toURI().toString());
                    //boardGrid element is filled with ImagePattern constructed with temp image which we created above.
                    (boardGrid[i % 4][i / 4]).setFill(new ImagePattern(temp));
                }
                //If the type is PipeStatic and property 10 block below will execute.
                else if(property.equals("10")){
                    //If the type is PipeStatic and property is 10 then matching image "PipeStatic_10.png" and construct Image object by URI path using File object.
                    temp=new Image(new File("PipeStatic_10.png").toURI().toString());
                    //boardGrid element is filled with ImagePattern constructed with temp image which we created above.
                    (boardGrid[i % 4][i / 4]).setFill(new ImagePattern(temp));
                }
                //If the type is PipeStatic and property 11 block below will execute.
                else if(property.equals("11")){
                    //If the type is PipeStatic and property is 11 then matching image "PipeStatic_11.png" and construct Image object by URI path using File object.
                    temp=new Image(new File("PipeStatic_11.png").toURI().toString());
                    //boardGrid element is filled with ImagePattern constructed with temp image which we created above.
                    (boardGrid[i % 4][i / 4]).setFill(new ImagePattern(temp));
                }
                //If the type is PipeStatic and property Vertical block below will execute.
                else{
                    //If the type is PipeStatic and property is Vertical then matching image "PipeStatic_Vertical.png" and construct Image object by URI path using File object.
                    temp=new Image(new File("PipeStatic_Vertical.png").toURI().toString());
                    //boardGrid element is filled with ImagePattern constructed with temp image which we created above.
                    (boardGrid[i % 4][i / 4]).setFill(new ImagePattern(temp));
                }
            }
            //If the type is Starter block below will execute.
            else if(type.equals("Starter")){
                //If the type is Starter and property Horizontal block below will execute.
                if(property.equals("Horizontal")){
                    //If the type is Starter and property is Horizontal then matching image "Starter_Horizontal.png" and construct Image object by URI path using File object.
                    temp=new Image(new File("Starter_Horizontal.png").toURI().toString());
                    //boardGrid element is filled with ImagePattern constructed with temp image which we created above.
                    (boardGrid[i % 4][i / 4]).setFill(new ImagePattern(temp));
                }
                //If the type is Starter and property Vertical block below will execute.
                else{
                    //If the type is Starter and property is Vertical then matching image "Starter_Vertical.png" and construct Image object by URI path using File object.
                    temp=new Image(new File("Starter_Vertical.png").toURI().toString());
                    //boardGrid element is filled with ImagePattern constructed with temp image which we created above.
                    (boardGrid[i % 4][i / 4]).setFill(new ImagePattern(temp));
                }
            }
            //If the type is End block below will execute.
            else if(type.equals("End")){
                //If the type is End and property Horizontal block below will execute.
                if(property.equals("Horizontal")) {
                    //If the type is End and property is Horizontal then matching image "End_Horizontal.png" and construct Image object by URI path using File object.
                    temp=new Image(new File("End_Horizontal.png").toURI().toString());
                    //boardGrid element is filled with ImagePattern constructed with temp image which we created above.
                    (boardGrid[i % 4][i / 4]).setFill(new ImagePattern(temp));
                }
                //If the type is End and property Vertical block below will execute.
                else{
                    //If the type is End and property is Vertical then matching image "End_Vertical.png" and construct Image object by URI path using File object.
                    temp=new Image(new File("End_Vertical.png").toURI().toString());
                    //boardGrid element is filled with ImagePattern constructed with temp image which we created above.
                    (boardGrid[i % 4][i / 4]).setFill(new ImagePattern(temp));
                }
            }
            //If the type is anything other then specified above board cannot be generated as there is no resource for that parts. (No Image or No Info)
            else {
                System.out.println("Board cannot be generated.");
                System.exit(-1);
            }
        }
    }


    //This method makes Rectangle moveable(draggable).
    private void setDraggable(Rectangle r){
        //When drag is detected for Rectangle r code written with lambda expression below will be executed.
        r.setOnDragDetected(e -> {
            //We get source of event by getSource method of event and store the Rectangle inside temp.
            Rectangle temp=(Rectangle) e.getSource();
            int i;
            //We find x and y coordinates (indexes) of temp using loop below.
            for(i=0;i<16;i++){
                //If temp is found inside boardGrid (4x4 Rectangle array) loop will break.
                if(boardGrid[i%4][i/4].equals(temp)){
                    break;
                }
            }
            //We store this coordinates using an ObjectProperty in this case it is a Point2D object property
            selectedRectangle.set(new Point2D(i%4,i/4));
            //We initiate dragging event
            r.startFullDrag();
            //We end DragDetected event
            e.consume();
        });
    }

    //This method makes Rectangle a slot which is a place other draggable elements can be moved into.
    private void setSlot(Rectangle r){
        //When DragEvent exits, which is when mouse is released from drag code written with lambda expression below will be executed.
        r.setOnMouseDragExited(e -> {
            //We store Rectangle that will be new positions for dragged Rectangle.
            Rectangle target = (Rectangle) e.getSource();
            int i;
            //We find x and y coordinates (indexes) of target using loop below.
            for(i=0;i<16;i++){
                //If target is found inside boardGrid (4x4 Rectangle array) loop will break.
                if(boardGrid[i%4][i/4].equals(target)){
                    break;
                }
            }
            //We get the coordinates of dragged rectangle by accessing Point2D ObjectProperty we set before.
            int x0=(int) selectedRectangle.get().getX();
            int y0=(int) selectedRectangle.get().getY();
            //We get coordinates of slot (housing) element with code below.
            int x1=i%4;
            int y1=i/4;
            //Then we check if the move is legal (applicable) by passing coordinates to method of Game class object game.
            if(game.isLegal(x0,y0,x1,y1)){
                //If move is legal then we make move (swap elements) by passing coordinates to method of Game class object game.
                game.makeMove(x0,y0,x1,y1);
                //Now we Exchange rectangles in frontend by exchanging Rectangles inside boardGrid by our method exchangeRectangles.
                exchangeRectangles(x0,y0,x1,y1);
                //We increment our move by 1 as this move is made.
                numberOfMoves++;
                //We update the label to show our move number.
                moveCounter.setText("Moves: "+numberOfMoves);
            }
            //We end DragExited event.
            e.consume();
        });
    }

    //We exchange rectangles with this method.
    private void exchangeRectangles(int x0,int y0,int x1,int y1){
        //We remove Rectangles we will exchange from boardPane (object of GridPane) first
        boardPane.getChildren().remove(boardGrid[x0][y0]);
        boardPane.getChildren().remove(boardGrid[x1][y1]);
        //We swap their coordinates and add to boardPane (object of GridPane)
        boardPane.add(boardGrid[x1][y1],x0,y0);
        boardPane.add(boardGrid[x0][y0],x1,y1);
        //We swap Rectangles inside boardGrid too to make exchange successful.
        Rectangle temp = boardGrid[x0][y0];
        boardGrid[x0][y0] = boardGrid[x1][y1];
        boardGrid[x1][y1] = temp;

    }

    //We save unlocked level to "save.dat" file by this method.
    private void saveGame() {
        try {
            //We create object of file "save.dat" first.
            File f = new File("save.dat");
            //We use Formatter class to write to "save.dat" file.
            Formatter fo = new Formatter(f);
            //We write unlocked level to our file
            fo.format("%d", game.unlockedLevel);
            //We close our file.
            fo.close();
        }catch (IOException e){
            //In case of any error happens we print error message to debug.
            e.printStackTrace();
        }
    }

    //We load unlocked level from "save.dat" file by this method.
    private void loadGame(){
        //We create object of file "save.dat" first.
        File f = new File("save.dat");
        //We check if save file exists.
        if(f.exists()){
            try{
                //We start reading from file with BufferedReader.
                BufferedReader b = new BufferedReader(new FileReader("save.dat"));
                //We read one character from BufferedReader and convert it into integer by difference with character 0
                game.unlockedLevel=b.read() - ((int) '0');
            }catch(FileNotFoundException e){
                //We print error message if file not found.
                e.printStackTrace();
            }catch (IOException e){
                //We print error message if IO exception happens.
                e.printStackTrace();
            }
        }else{
            //If save file doesn't exist we start from first level.
            game.unlockedLevel=0;
        }
    }

    //This method is used to play animation (ball rolling inside pipe to end) after level is finished.
    private PathTransition playAnimation(){
        //We create a Path object which we later add directions for animation translation(�teleme).
        Path path = new Path();
        //This variables will be used for the positions of animation to start from.
        double x = 93;
        double y = 100;
        //Animation starts with element moving to x,y position.
        path.getElements().add(new MoveTo(x,y));
        //Level 1,2,3 have same animation so we defined them under this if block with condition of level less then 3
        if(level<3){
            //We start going down 360 pixels
            path.getElements().add(new VLineTo(360));
            //We go to x=160 and y=368 with 160 starting radius and 160 ending radius. The last 2 parameters are defining arc.
            path.getElements().add(new ArcTo(160,160,0,160,368,false,false));
            //We go 370 pixels to left
            path.getElements().add(new HLineTo(370));
        }else{
            //We start going down 240 pixels
            path.getElements().add(new VLineTo(240));
            //We go to x=160 and y=280 with 160 starting radius and 160 ending radius. The last 2 parameters are defining arc.
            path.getElements().add(new ArcTo(160,160,0,160,280,false,false));
            //We go 370 pixels to left.
            path.getElements().add(new HLineTo(370));
            //We go to x=370 and y=200 with 160 starting radius and 160 ending radius. The last 2 parameters are defining arc.
            path.getElements().add(new ArcTo(160,160,0,370,200,false,false));
            //We go 180 pixels up.
            path.getElements().add(new VLineTo(180));
        }
        //We create a PathTransition object called animation. This will be used to define our animation.
        PathTransition animation = new PathTransition();
        //We set our path to Path object path.
        animation.setPath(path);
        //We apply this animation on ball (Circle object)
        animation.setNode(ball);
        //We set duration to 3 seconds.
        animation.setDuration(Duration.seconds(3));
        //We set cycle count of animation to 1 to play animation once.
        animation.setCycleCount(1);
        //We play animation
        animation.play();
        //We return animation to set our level to  advance when animation is finished using setOnFinished event handler.
        return animation;
    }

    //We start JavaFx GUI here in main.
    public static void main(String[] args) {
        launch(args);
    }
}
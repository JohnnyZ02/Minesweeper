package summer2022Project;


import java.io.FileNotFoundException;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import summer2022Project.GridSpace.GridSquares;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * 
 * @author Johnny Zheng
 * @Version: August 2022
 * 
 *           Minesweeper class stets the stage of the main window and contains the difficulty
 *           buttons, allowing them to interact with the size of the window.
 */

public class Main extends Application {

  MenuItem easyBtn = new MenuItem("Easy: 9x9");
  MenuItem normalBtn = new MenuItem("Normal: 16x16");
  MenuItem hardBtn = new MenuItem("Hard: 16x30");

  static Button retryBtn = new Button();

  GridSquares gridFunction;
  GridSpace grid;

  String currentDifficulty = "NORMAL";

  int height = 780;
  int width = 860;
  int boxSize = 44;
  int gridHeight = 16;
  int gridWidth = 16;

  static int mineCount = 40;
  String color = "teal";



  /**
   * start method
   * @param stage main stage
   * @throws FileNotFoundException
   */
  @Override
  public void start(Stage stage) throws FileNotFoundException {

    stage.setTitle("Minesweeper - " + currentDifficulty);
    stage.setHeight(height);
    stage.setWidth(width);
    stage.centerOnScreen();

    grid = new GridSpace(gridHeight, gridWidth, boxSize, mineCount);
    BorderPane bPane = new BorderPane(grid);
    BorderPane.setAlignment(grid, Pos.CENTER);
    bPane.setStyle("-fx-background-color: " + color + ";");

    MenuButton newGameBtn = new MenuButton("New Game", null, easyBtn, normalBtn, hardBtn);

    VBox vBox = new VBox();
    vBox.setAlignment(Pos.TOP_CENTER);
    vBox.setSpacing(20);

    FlagTracker flagBox = new FlagTracker(grid);

    vBox.getChildren().addAll(flagBox, newGameBtn);

    vBox.setPadding(new Insets(10, 0, 0, 10));
    BorderPane.setAlignment(vBox, Pos.TOP_LEFT);

    bPane.setLeft(vBox);

    Scene scene = new Scene(bPane);
    stage.setResizable(false);
    stage.setAlwaysOnTop(true);
    stage.setScene(scene);
    stage.show();


    easyBtn.setOnAction(e -> {
      try {
        newStage(stage, "EASY");
      } catch (FileNotFoundException e1) {
      }
    });

    normalBtn.setOnAction(e -> {
      try {
        newStage(stage, "NORMAL");
      } catch (FileNotFoundException e1) {
      }
    });

    hardBtn.setOnAction(e -> {
      try {
        newStage(stage, "HARD");
      } catch (FileNotFoundException e1) {
      }
    });

    retryBtn.setOnMouseClicked(e -> {
      try {
        Message.popup.close();
        newStage(stage, currentDifficulty);
      } catch (FileNotFoundException e1) {
      }
    });

  }


  /*
   * Recreates the stage, resets grid variables, and sets the properties of both the stage and the grid.
   * @param stage: The stage that will be passed to the start method
   * @param difficulty: A string representing the difficulty of the new stage
   */
  public void newStage(Stage stage, String difficulty) throws FileNotFoundException {
    if (difficulty.equals("EASY"))
      setProperties("EASY", 780, 860, 9, 9, 80, 10, "green");
    else if (difficulty.equals("NORMAL"))
      setProperties("NORMAL", 780, 860, 16, 16, 44, 40, "teal");
    else
      setProperties("HARD", 780, 1480, 16, 30, 44, 99, "navy");
    grid.reset();
    start(stage);
  }

  /**
   * Sets the properties of the grid
   * @param difficulty
   * @param height number of rows
   * @param width number of columns
   * @param gHeight grid height
   * @param gWidth grid width
   * @param size length and width of boxes
   * @param mines
   * @param color
   */
  public void setProperties(String difficulty, int height, int width, int gHeight, int gWidth,
      int size, int mines, String color) {
    this.height = height;
    this.width = width;
    gridHeight = gHeight;
    gridWidth = gWidth;
    boxSize = size;
    mineCount = mines;
    this.color = color;
    currentDifficulty = difficulty;
  }

  public static void main(String[] args) {
    launch(args);
  }
}

package summer2022Project;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Random;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 * 
 * @author Johnny Zheng
 * @Version August 2022
 * 
 *          GridSpace class creates the grid by calling on the nested GridSquare class. The
 *          GridSquare class contains information on each individual cell.
 *          
 */

public class GridSpace extends GridPane {

  GridSquares[][] square;

  String difficulty;

  int boxSize;
  int minesCount;
  int flagsLeft;

  static int gridHeight;
  static int gridWidth;

  static int minesFound = 0;
  static int squaresUncovered = 0;

  static long startTime;
  static long endTime;

  int gridValue = 0;


  // The coordinates of surrounding squares and their relativity to the center square (the square object that it belongs to). It starts in the top left and goes clockwise)
  static int adjacentIndexConversion[][] =
      {{-1, -1}, {-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}};

  static int mineCoords[][];


  boolean revealChecked = false;

  static boolean firstClick = true;
  static boolean gameOver = false;
  static boolean flagMode = false;

  Button flagBtn = new Button();
  Text flagCounter = new Text();

  Tooltip on = new Tooltip("Toggle Flag Mode");
  Tooltip off = new Tooltip("Untoggle Flag Mode");

  ImageView flagImg;


  /**
   * 
   * @param height Number of rows
   * @param width Number of columns
   * @param size Length and width of squares
   * @param mines Number of mines
   * @throws FileNotFoundException
   */
  public GridSpace(int height, int width, int size, int mines) throws FileNotFoundException {
    gridHeight = height;
    gridWidth = width;
    boxSize = size;
    minesCount = mines;
    flagsLeft = minesCount;

    flagCounter.setText(flagsLeft + "");
    flagCounter.setFont(Font.font(37));
    flagCounter.setTextAlignment(TextAlignment.CENTER);

    flagImg = displayImage("flag", 37);
    flagImg.setOnMouseClicked(e -> toggleFlagMode());
    Tooltip.install(flagImg, on);


    generateMines();
    // Creating the squares in the grid
    square = new GridSquares[gridHeight][gridWidth];
    for (int i = 0; i < gridHeight; i++) {
      for (int j = 0; j < gridWidth; j++) {
        square[i][j] = new GridSquares(boxSize);
        this.add(square[i][j], boxSize * j, boxSize * i);
        if (mineHere(i, j))
          square[i][j].isMine = true;
        square[i][j].setBoundary(i, j);
      }
    }
    this.setStyle("-fx-padding: 10 10 10 10;");

    assignGridValues();
  }


  /*
   * @param itemName: The file name of the image without the extension
   * 
   * @return view: The ImageView of the image
   * 
   */
  public ImageView displayImage(String itemName, int imageSize) throws FileNotFoundException {

    Image image = new Image(new FileInputStream(
        "Images/" + itemName + ".png"));


    ImageView view = new ImageView(image);
    view.setFitWidth(imageSize);
    view.setFitHeight(imageSize);
    return view;
  }

  /*
   * Generates mines and disallows repeating coordinates by rewriting the same index until a
   * new coordinate is generated.
   */
  public void generateMines() {
    mineCoords = new int[minesCount][2];
    for (int i = 0; i < minesCount; i++) {
      mineCoords[i][0] = generateRand(gridHeight);
      mineCoords[i][1] = generateRand(gridWidth);
      if (mineCoordsExists(i))
        i--;
    }
  }

  /**
   * generateRand
   * @param range Range of random number from 0 to range - 1
   * @return random number
   */
  public int generateRand(int range) {
    Random rand = new Random();
    int random = rand.nextInt(range+1);
    return random;
  }

  /*
   * Checks if the set of integers in mineCoords are identical to existing integers. Returns true if
   * so and else otherwise.
   */
  public boolean mineCoordsExists(int currentIndex) {
    for (int i = 0; i < currentIndex; i++) {
      if (mineCoords[currentIndex][0] == mineCoords[i][0]
          && mineCoords[currentIndex][1] == mineCoords[i][1]) {
        return true;
      }
    }
    return false;
  }

  public void fillIntArray(int arr[]) {
    for (int i = 0; i < arr.length; i++) {
      arr[i] = 0;
    }
  }

  /*
   * Assigns gridValue to each object in square[][] by identifying the number of adjacent squares
   * where isMine is true. tempI and tempJ are the pair of coordinates to one of the squares
   * surrounding the current square[][].
   */
  public void assignGridValues() {
    int tempI;
    int tempJ;
    for (int i = 0; i < gridHeight; i++) {
      for (int j = 0; j < gridWidth; j++) {
        for (int k = 0; k < 8; k++) {
          // To check if a mine exists, you first have to check if an adjacent square exists
          if (square[i][j].adjacentInBoundary[k] && !square[i][j].isMine) {
            tempI = i + adjacentIndexConversion[k][0];
            tempJ = j + adjacentIndexConversion[k][1];
            if (square[tempI][tempJ].isMine)
              square[i][j].gridValue++;
          }
          if (square[i][j].isMine)
            square[i][j].gridValue = -1;
        }
      }
    }
  }

  /*
   * Returns true if the x and y parameters perfectly matches a set of integers in mineCoords else
   * false is returned.
   */
  public boolean mineHere(int x, int y) {
    for (int i = 0; i < minesCount; i++) {
      if (mineCoords[i][0] == x && mineCoords[i][1] == y)
        return true;
    }
    return false;
  }


  /*
   * Runs a series of methods to reset the grid back to what it once was.
   */
  public void reset() throws FileNotFoundException {
    generateMines();
    resetStatic();
    for (int i = 0; i < gridHeight; i++) {
      for (int j = 0; j < gridWidth; j++) {
        square[i][j].setStyle("-fx-border-color: gray;-fx-background-color: lightgray;");
        square[i][j].resetNonStatic();
        square[i][j].getChildren().clear();
        ImageView imageObject = displayImage("cover", boxSize);
        square[i][j].getChildren().add(imageObject);
        if (mineHere(i, j))
          square[i][j].isMine = true;
      }
    }
    assignGridValues();
    setCursor(Cursor.DEFAULT);
  }

  public void resetStatic() {
    firstClick = true;
    gameOver = false;
    squaresUncovered = 0;
    minesFound = 0;
    flagMode = false;

  }


  public void toggleFlagMode() {
    if (flagMode) {
      flagMode = false;
      Tooltip.install(flagImg, on);
      setCursor(Cursor.DEFAULT);
    } else {
      flagMode = true;
      Tooltip.install(flagImg, off);
      setCursor(Cursor.OPEN_HAND);
    }
  }


  /**
   * 
   */
  public class GridSquares extends Pane {

    // Represents squares adjacent to square object it belongs to. The array starts in the top left and goes clockwise. A boolean is assigned based on whether or not the square exists.//
    boolean adjacentInBoundary[] = new boolean[8];

    static int newlyRevealedCoords[] = new int[2];

    int gridValue;

    boolean flagged = false;
    boolean revealed = false;
    boolean isMine = false;
    boolean revealChecked = false;

    /**
     * @param boxSize length and width of the box
     * @throws FileNotFoundException
     */
    public GridSquares(int boxSize) throws FileNotFoundException {
      setPrefSize(boxSize, boxSize);
      setStyle("-fx-border-color: gray;-fx-background-color: lightgray;");
      ImageView imageObject = displayImage("cover", boxSize);
      this.getChildren().add(imageObject);

      this.setOnMouseClicked(e -> {
        if (!gameOver) {

          if (e.getButton() == MouseButton.PRIMARY && !flagMode)
            try {
              revealSquare();
            } catch (FileNotFoundException e2) {
            }

          else if (e.getButton() == MouseButton.SECONDARY || flagMode)
            try {
              flag();
            } catch (FileNotFoundException e1) {
            }
        }
      });
    }
    

    /*
     * A series of conditions and method calls that goes into uncovering a square.
     */
    public void revealSquare() throws FileNotFoundException {
      if (!flagged && !revealed) {
        if (firstClick)
          startTime = System.currentTimeMillis();
        if (isMine && !firstClick) { // The conditions for a loss
          endTime = System.currentTimeMillis();
          clearAndDisplay("mine_red");
          gameOver = true;
          revealed = true;
          revealAllMines();
          countUncoveredSquares();
          new Message("You Lose!");
        }
        /*
         * Repeatedly generates new mine positions and grid values on the first click until, the
         * user reveals a gridValue of 0.
         */
        while ((isMine || gridValue != 0) && firstClick) {
          replaceMines();
          generateGridValues();
        }
        showSquare();
        firstClick = false;
      }
      if (minesFound == minesCount && areNonMinesRevealed()) { // The conditions for a win
        endTime = System.currentTimeMillis();
        revealAllSquares();
        countUncoveredSquares();
        new Message("You Win!");
      }
    }


    /*
     * Clears whatever node is present and displays a new image in its place
     * 
     * @param str: The file name without the extension
     */
    public void clearAndDisplay(String str) throws FileNotFoundException {
      this.getChildren().clear();
      ImageView imageObject = displayImage(str, boxSize);
      this.setCenterShape(true);
      this.getChildren().add(imageObject);
    }


    /*
     * Reveals the gridValue of the square
     */
    public void showSquare() throws FileNotFoundException {
      revealed = true;
      displayGridValue();
      revealNearbyZeros();
      this.setStyle("-fx-border-color: lightgray;-fx-background-color: darkgray;");
    }


    /*
     * Method calls and conditions pertaining to flagging and unflagging squares
     */
    public void flag() throws FileNotFoundException {
      if (!flagged && !revealed) {
        flagged = true;
        ImageView imageObject = displayImage("flag", boxSize);
        this.getChildren().add(imageObject);
        flagsLeft--;
        displayFlagCounter();
        if (isMine)
          minesFound++;
      } else if (flagged) {
        flagged = false;
        ImageView imageObject = displayImage("cover", boxSize);
        this.getChildren().add(imageObject);
        flagsLeft++;
        displayFlagCounter();
        if (isMine)
          minesFound--;
      }
      if (minesFound == minesCount && areNonMinesRevealed()) {
        endTime = System.currentTimeMillis();
        revealAllSquares();
        countUncoveredSquares();
        new Message("You Win!");
      }
    }

    /*
     * Recursively generates new mine locations and gridValues until mines do not end up in revealed
     * squares.
     */
    public void replaceMines() {
      clearMines();
      generateMines();
      for (int i = 0; i < gridHeight; i++) {
        for (int j = 0; j < gridWidth; j++) {
          if (mineHere(i, j) && !square[i][j].revealed)
            square[i][j].isMine = true;
          else if (mineHere(i, j) && square[i][j].revealed) {
            replaceMines();
          }
        }
      }
    }

    public void clearMines() {
      for (int i = 0; i < gridHeight; i++) {
        for (int j = 0; j < gridWidth; j++) {
          square[i][j].isMine = false;
        }
      }
    }

    /*
     * Sets the boundary array by comparing the both indeces of center square location to determine
     * what boolean should be assigned to them. True: corresoponding square is within bounds/does
     * exist. False: not within bounds/does not exists.
     */
    public void setBoundary(int x, int y) {
      fillBooleanArrayTrue(square[x][y].adjacentInBoundary);
      if (x == 0) {
        for (int i = 0; i < 3; i++)
          square[x][y].adjacentInBoundary[i] = false;
      }
      if (y == gridWidth - 1) {
        for (int i = 2; i < 5; i++)
          square[x][y].adjacentInBoundary[i] = false;
      }
      if (x == gridHeight - 1) {
        for (int i = 4; i < 7; i++)
          square[x][y].adjacentInBoundary[i] = false;
      }
      if (y == 0) {
        square[x][y].adjacentInBoundary[6] = false;
        square[x][y].adjacentInBoundary[7] = false;
        square[x][y].adjacentInBoundary[0] = false;
      }
    }

    public void fillBooleanArrayTrue(boolean arr[]) {
      for (int i = 0; i < arr.length; i++)
        arr[i] = true;
    }

    public void generateGridValues() {
      clearGridValues();
      assignGridValues();
    }

    public void clearGridValues() {
      for (int i = 0; i < gridHeight; i++) {
        for (int j = 0; j < gridWidth; j++) {
          square[i][j].gridValue = 0;
        }
      }
    }

    public void displayGridValue() throws FileNotFoundException {
      if (gridValue == 0) {
        displayValue("0");
      } else if (gridValue == 1) {
        displayValue("1");
      } else if (gridValue == 2) {
        displayValue("2");
      } else if (gridValue == 3) {
        displayValue("3");
      } else if (gridValue == 4) {
        displayValue("4");
      } else if (gridValue == 5) {
        displayValue("5");
      } else if (gridValue == 6) {
        displayValue("6");
      } else if (gridValue == 7) {
        displayValue("7");
      } else if (gridValue == 8) {
        displayValue("8");
      }
    }

    public void displayValue(String x) throws FileNotFoundException {
      ImageView view = displayImage(x, boxSize);
      this.getChildren().add(view);
    }

    /*
     * If the gridValue of the square is 0, reveal the gridValues of the adjacent squares
     */
    public void revealAdjacent(int iCoord, int jCoord) throws FileNotFoundException {
      if (square[iCoord][jCoord].gridValue == 0) {
        for (int k = 0; k < 8; k++) {
          if (square[iCoord][jCoord].adjacentInBoundary[k]) {
            int tempICoord = iCoord + adjacentIndexConversion[k][0];
            int tempJCoord = jCoord + adjacentIndexConversion[k][1];
            if (!square[tempICoord][tempJCoord].flagged) {
              square[tempICoord][tempJCoord].showSquare();
            }
          }
        }
      }
    }

    /*
     * Scans through newly revealed squares to invoke revealAdjacent() on
     */
    public void revealNearbyZeros() throws FileNotFoundException {
      for (int i = 0; i < gridHeight; i++) {
        for (int j = 0; j < gridWidth; j++) {
          if (square[i][j].revealed && !square[i][j].revealChecked) {
            square[i][j].revealChecked = true;
            revealAdjacent(i, j);
          }
        }
      }
    }

    public void revealAllMines() throws FileNotFoundException {
      for (int i = 0; i < gridHeight; i++) {
        for (int j = 0; j < gridWidth; j++) {
          if (square[i][j].isMine && !square[i][j].revealed)
            square[i][j].clearAndDisplay("Mine");
        }
      }
    }

    public void revealAllSquares() throws FileNotFoundException {
      for (int i = 0; i < gridHeight; i++) {
        for (int j = 0; j < gridWidth; j++) {
          if (!square[i][j].revealed && !square[i][j].isMine)
            square[i][j].displayGridValue();
        }
      }
    }

    public int countUncoveredSquares() {
      for (int i = 0; i < gridHeight; i++) {
        for (int j = 0; j < gridWidth; j++) {
          if (square[i][j].revealed && !square[i][j].isMine)
            squaresUncovered++;
        }
      }
      return squaresUncovered;
    }


    public boolean areNonMinesRevealed() {
      for (int i = 0; i < gridHeight; i++) {
        for (int j = 0; j < gridWidth; j++) {
          if (!square[i][j].revealed & !square[i][j].isMine) {
            return false;
          }
        }
      }
      return true;
    }

    public void resetNonStatic() {
      flagged = false;
      revealed = false;
      isMine = false;
      revealChecked = false;
      gridValue = 0;
    }

    /*
     * Displays and formats flagCounter
     */
    public void displayFlagCounter() {
      if (flagsLeft > 9 && flagsLeft > 0)
        flagCounter.setText(flagsLeft + "");
      else if (flagsLeft == 0) {
        flagCounter.setFill(Color.GREEN);
        flagCounter.setText("00");
      } else if (flagsLeft < 10 && flagsLeft > -1)
        flagCounter.setText("0" + flagsLeft);
      else if (flagsLeft < 0 && flagsLeft > -10) {
        flagCounter.setFill(Color.RED);
        flagCounter.setText(flagsLeft + "");
      } else {
        flagCounter.setFill(Color.RED);
        flagCounter.setText("-X");
      }
    }
  }
}
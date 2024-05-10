package summer2022Project;

import java.io.FileNotFoundException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;

/**
 * 
 * @author Johnny Zheng
 * 
 *         FlagTracker class creates a white box in the Minesweeper stage, containing flagCounter
 *         and flagImg.
 *
 */

public class FlagTracker extends BorderPane {

  public FlagTracker(GridSpace grid) throws FileNotFoundException {
    setPrefSize(90, 80);
    setStyle("-fx-background-color: white;");

    setLeft(grid.flagCounter);
    setRight(grid.flagImg);

    setAlignment(grid.flagCounter, Pos.CENTER_LEFT);
    setAlignment(grid.flagImg, Pos.CENTER_RIGHT);

    setMargin(grid.flagCounter, new Insets(0, 0, 0, 5));
    setMargin(grid.flagImg, new Insets(0, 5, 0, 0));
  }
}

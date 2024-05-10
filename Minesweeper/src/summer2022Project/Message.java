package summer2022Project;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

/**
 * 
 * @author Johnny Zheng
 * @version August 2022
 * 
 *          Message class displays an end-of-the-game popup with game statistics and a reset button.
 *
 *          CHANGE THE FILE DIRECTORY LOCATED IN THIS CLASS!
 */
public class Message {

  static Stage popup = new Stage();


  public Message(String message) {

    popup.setHeight(200);
    popup.setWidth(300);
    popup.setTitle(message);

    VBox vBox = new VBox();

    Text text = new Text();
    text.setFont(Font.font(20));
    vBox.getChildren().add(text);
    vBox.setAlignment(Pos.CENTER);
    text.setTextAlignment(TextAlignment.CENTER);
    text.setText("Time Spent - " + timeConversion() + "\nMines Found - " + GridSpace.minesFound
        + "\nProgress - " + gridProgressCalculation() + "%\n");

    vBox.getChildren().add(Main.retryBtn);


    // CHANGE THE FILE DIRECTOY BELOW!
    // ADD AN EXTRA "\" EVERY TIME IT SHOWS UP (i.e. \\downloads\\homework).
    Image image = new Image("Images/retry.png");


    ImageView imageView = new ImageView(image);
    imageView.setFitHeight(35);
    imageView.setPreserveRatio(true);
    Main.retryBtn.setGraphic(imageView);


    BorderPane bPane = new BorderPane(vBox);

    Scene scene = new Scene(bPane);
    popup.setScene(scene);
    popup.show();

    popup.setResizable(false);
    popup.setAlwaysOnTop(true);


    scene.setOnMouseClicked(e -> popup.close());
  }

  /*
   * Calculates the time elapsed into minutes:seconds
   * 
   * @return: The formatted time elapsed
   */
  public String timeConversion() {
    int seconds = (int) ((GridSpace.endTime - GridSpace.startTime) / 1000);
    int minutes = 0;
    while (seconds / 60 > 0) {
      seconds = seconds - 60;
      minutes++;
    }
    return timeFormat(minutes) + ":" + timeFormat(seconds);
  }

  /*
   * Formats the extra 0 if needed
   */
  public String timeFormat(int x) {
    if (x < 10)
      return 0 + "" + x;
    return "" + x;
  }

  /*
   * Calculates the grid progress and formats it to ##.##%
   */
  public int gridProgressCalculation() {
    double num = (double) (GridSpace.squaresUncovered)
        / (GridSpace.gridHeight * GridSpace.gridWidth - 10) * 100;
    return (int) (Math.round(num));
  }
}

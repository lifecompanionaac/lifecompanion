package scripts;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.Random;

public class TestColorPicker extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        LCColorPicker colorPicker = new LCColorPicker();
        VBox vbox = new VBox(10.0, new Label("Test"), colorPicker);
        vbox.setMinHeight(400);
        vbox.setMinWidth(400);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));
        Scene scene = new Scene(vbox);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    //https://ourcodeworld.com/articles/read/81/top-5-best-javascript-and-jquery-color-picker-plugins


    private static class LCColorPicker extends HBox {
        Rectangle rectangle;

        LCColorPicker() {
            this.setAlignment(Pos.CENTER);
            Rectangle rectangle = new Rectangle(50, 30);

            Button button = new Button();
            button.setGraphic(rectangle);

            this.getChildren().add(button);

            button.setOnAction(me -> {

                Popup popupControl = new Popup();

                TilePane tilePaneColor = new TilePane();
                tilePaneColor.setHgap(5);
                tilePaneColor.setVgap(5);
                tilePaneColor.setStyle("-fx-background-color:white;");

                for (int i = 0; i < 20; i++) {
                    Rectangle rect = new Rectangle(20, 20);
                    Random rand = new Random();
                    rect.fillProperty().set(Color.rgb(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)));
                    tilePaneColor.getChildren().add(rect);
                    rect.setOnMouseClicked(m -> {
                        rectangle.fillProperty().set(rect.getFill());
                        popupControl.hide();
                    });
                }


                popupControl.autoFixProperty().set(true);
                popupControl.autoHideProperty().set(true);
                popupControl.getContent().add(tilePaneColor);
                Scene scene = this.getScene();
                Window window = scene.getWindow();
                Point2D point2D = button.localToScene(0, 0);
                popupControl.show(button, window.getX() + scene.getX() + point2D.getX(), window.getY() + scene.getY() + point2D.getY() + button.getHeight());

                //                Scene scene = this.getScene();
                //                Window window = scene.getWindow();
                //
                //                Stage popStage = new Stage();
                //                popStage.initStyle(StageStyle.TRANSPARENT);
                //                popStage.initOwner(window);
                //
                //                Scene colorScene = new Scene(new Label("HELLOO"));
                //
                //                popStage.setResizable(false);
                //                popStage.setWidth(300);
                //                popStage.setHeight(300);
                //
                //
                //
                //                popStage.setScene(colorScene);
                //                popStage.show();
            });
        }
    }
}

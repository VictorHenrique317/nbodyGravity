package sample.ui;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SelectionWindow {
    private Timeline timeline;
    @FXML
    private ImageView closeButton;
    @FXML
    private VBox contentBox;
    @FXML
    private HBox topBox;

    public void initialize(){
        Image image = new Image(Objects.requireNonNull(
                getClass().getClassLoader().getResource("closeButton.png")).toExternalForm());
        closeButton.setImage(image);
    }

    @FXML
    private void onClose(){ Platform.exit(); }
    @FXML
    private void onSolarSystem(){
        createLoadingPage();
        Main.createSolarSystem();
    }
    @FXML
    private void onCustomSimulation(){
        createLoadingPage();
        Main.createCustomSimulation();
    }

    private void createLoadingPage(){
        Image image = new Image(Objects.requireNonNull(
                getClass().getClassLoader().getResource("loading.png")).toExternalForm());
        ImageView loadingImage = new ImageView(image);
        loadingImage.setFitWidth(250);
        loadingImage.setFitHeight(250);
        VBox vbox = new VBox(loadingImage);
        Label label = new Label("Loading...");
        label.setFont(new Font("Arial", 24));
        label.setTextFill(Color.WHITE);
        vbox.getChildren().add(label);
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.setSpacing(20);

        contentBox.getChildren().clear();
        topBox.getChildren().clear();
        contentBox.getChildren().add(vbox);
        timeline = new Timeline(new KeyFrame(
                Duration.millis(3000),
                new KeyValue(loadingImage.rotateProperty(), 360)
        ));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void stop(){
        if (timeline != null) timeline.stop();
    }
}

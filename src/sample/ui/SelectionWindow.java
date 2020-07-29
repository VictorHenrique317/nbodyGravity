package sample.ui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class SelectionWindow {
    @FXML
    private ImageView closeButton;

    public void initialize(){
        Image image = new Image(Objects.requireNonNull(
                getClass().getClassLoader().getResource("closeButton.png")).toExternalForm());
        closeButton.setImage(image);
    }

    @FXML
    private void onClose(){ Platform.exit(); }
    @FXML
    private void onSolarSystem(){
        Main.createSolarSystem();
        Main.createMainScene();
    }
    @FXML
    private void onCustomSimulation(){

    }
}

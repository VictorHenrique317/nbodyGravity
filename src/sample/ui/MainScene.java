package sample.ui;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXRadioButton;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sample.physics.models.Body;
import sample.physics.GravityPool;

import java.io.IOException;
import java.util.*;

public class MainScene {
    private static Collection<ImageView> arrows;
    private static Collection<DrawerIcon> drawerIcons;
    private GravityPool pool;
    private double speed;
    private Collection<Body> bodies;

    private enum Arrows {First, Second, Third, Fourth};
    private Arrows currentArrow;

    private final Image filledArrow = new Image(Objects.requireNonNull(
            getClass().getClassLoader().getResource("next_arrow_filled.png")).toExternalForm());
    private final Image emptyArrow = new Image(Objects.requireNonNull(
            getClass().getClassLoader().getResource("next_arrow.png")).toExternalForm()
    );
    private VBox drawerContent;
    @FXML
    private HBox lockCameraBox;
    @FXML
    private HBox centerBox;
    @FXML
    private JFXHamburger hamburger;
    @FXML
    private JFXDrawer drawer;
    @FXML
    private BorderPane borderPane;
    @FXML
    private ImageView firstArrow, secondArrow, thirdArrow, fourthArrow;
    @FXML
    private JFXRadioButton hyperwarpButton;
    private int hyperwarpMultiplier = 1;

    public void initialize(){
        hamburger.setOnMouseEntered((mouseEvent -> {
            for (Node stackPane: hamburger.getChildren()){
                stackPane.setStyle("-fx-background-color: black;");
            }
        }));
        hamburger.setOnMouseExited((mouseEvent -> {
            for (Node stackPane: hamburger.getChildren()){
                stackPane.setStyle("-fx-background-color: white;");
            }
        }));
        arrows = List.of(firstArrow, secondArrow, thirdArrow, fourthArrow);
        for (ImageView arrow: arrows){
            arrow.setImage(emptyArrow);
        }
        firstArrow.setImage(filledArrow);
        currentArrow = Arrows.First;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Objects.requireNonNull(
                    getClass().getClassLoader().getResource("DrawerContent.fxml")));
            drawerContent = fxmlLoader.load();
            drawer.setSidePane(drawerContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
        drawer.setDirection(JFXDrawer.DrawerDirection.RIGHT);
        if (Main.getBodies() == null){
            throw new IllegalStateException("Controller called before creating simulation bodies");
        }
        bodies = Main.getBodies();
        for (Body body: bodies){
            DrawerIcon icon = new DrawerIcon(body);
            drawerContent.getChildren().add(icon);
        }
    }


    @FXML
    private void onLockCamera(){
        Main.lockCamera();
        Image img;
        Label label = null;
        ImageView icon = null;
        for(Node node: lockCameraBox.getChildren()){
            if (node instanceof ImageView)icon = (ImageView) node;
            if (node instanceof Label) label = (Label) node;
        }
        assert icon != null;
        assert label != null;
        boolean isCameraLocked = Main.isIsCameraLocked();
        if (isCameraLocked){
            // unlock option
            img = new Image(Objects.requireNonNull(
                    getClass().getClassLoader().getResource("unlock.png")).toExternalForm());

            icon.setImage(img);
            label.setText("Unlock camera");
        }else{
            // lock option
            img = new Image(Objects.requireNonNull(
                    getClass().getClassLoader().getResource("lock.png")).toExternalForm());
            icon.setImage(img);
            label.setText("Lock camera");
        }
    }
    @FXML
    private void handleDrawer(){
        if (drawer.isOpened()){
            drawer.close();
        }else{
            drawer.open();
        }
    }

    @FXML
    private void onHyperwarpClick(){ // maximum speed is 400x
        if (hyperwarpMultiplier == 1){ // enable
            System.out.println("Activating hyperwarp");
            hyperwarpMultiplier = 10;
            this.speed *= 10;
            pool.changeSpeed(this.speed);
            return;
        }
        if (hyperwarpMultiplier == 10){ // disable
            System.out.println("Deactivating hyperwarp");
            hyperwarpMultiplier = 1;
            this.speed /= 10;
            pool.changeSpeed(this.speed);
        }
    }

    @FXML
    private void onFirstArrowClick(){
        controlSpeed(firstArrow, 1 * this.hyperwarpMultiplier);
        makeArrowEmpty(secondArrow);
        makeArrowEmpty(thirdArrow);
        makeArrowEmpty(fourthArrow);
        this.currentArrow = Arrows.First;
    }
    @FXML
    private void onSecondArrowClick(){
        makeArrowFilled(firstArrow);
        controlSpeed(secondArrow, 2 * this.hyperwarpMultiplier);
        makeArrowEmpty(thirdArrow);
        makeArrowEmpty(fourthArrow);
        this.currentArrow = Arrows.Second;

    }
    @FXML
    private void onThirdArrowClick(){
        makeArrowFilled(firstArrow);
        makeArrowFilled(secondArrow);
        controlSpeed(thirdArrow, 4  * this.hyperwarpMultiplier);
        makeArrowEmpty(fourthArrow);
        this.currentArrow = Arrows.Third;
    }
    @FXML
    private void onFourthArrowClick(){
        makeArrowFilled(firstArrow);
        makeArrowFilled(secondArrow);
        makeArrowFilled(thirdArrow);
        controlSpeed(fourthArrow, 8 * this.hyperwarpMultiplier);
        this.currentArrow = Arrows.Fourth;
    }

    private void makeArrowEmpty(ImageView arrow){ arrow.setImage(emptyArrow); }
    private void makeArrowFilled(ImageView arrow){ arrow.setImage(filledArrow); }

    private void controlSpeed(ImageView arrow, double speed){
        if (arrow.getImage() == emptyArrow) arrow.setImage(filledArrow);
        pool.changeSpeed(speed);
        this.speed = speed;
    }

    public void accelerateSimulation(){
        switch (currentArrow){
            case First:
                onSecondArrowClick();
                break;
            case Second:
                onThirdArrowClick();
                break;
            case Third:
                onFourthArrowClick();
                break;
            default:
                break;
        }
    }

    public void decelerateSimulation(){
        switch (currentArrow){
            case Fourth:
                onThirdArrowClick();
                break;
            case Third:
                onSecondArrowClick();
                break;
            case Second:
                onFirstArrowClick();
                break;
            default:
                break;
        }
    }

    public void setGravityPool(GravityPool pool) {
        this.pool = pool;
        onFirstArrowClick();
    }

    public BorderPane getBorderPane() {
        return borderPane;
    }

    public void setCenter(SubScene subScene, Scene scene) {
        subScene.widthProperty().bind(scene.widthProperty().subtract(200));
        subScene.heightProperty().bind(scene.heightProperty().subtract(75));
        centerBox.getChildren().add(subScene);

    }

}

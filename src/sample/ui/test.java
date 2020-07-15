package sample.ui;

import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import sample.physics.models.Body;
import sample.physics.GravityPool;
import sample.physics.models.Planet;
import sample.physics.models.Star;

import java.util.*;
import java.util.List;

public class test extends Application {
    public static final Group objectGroup = new Group();
    private static final Group mainGroup = new Group();
    private static Rotate xRotate;
    private static Rotate yRotate;

    public static final Camera camera = new PerspectiveCamera(true);
    private static Collection<Body> bodies;
    private MainScene controller;

    private static double xAnchor, yAnchor, xAngleAnchor, yAngleAnchor;
    private static SimpleDoubleProperty xAngle = new SimpleDoubleProperty(0);
    private static SimpleDoubleProperty yAngle = new SimpleDoubleProperty(0);

    private static final GravityPool pool = new GravityPool();
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        camera.setNearClip(0.01);
        camera.setFarClip(10_000_000);
        camera.setTranslateZ(-2000);

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getClassLoader().getResource("MainScene.fxml"));
        Parent root = fxmlLoader.load();
        controller = fxmlLoader.getController();
        Scene mainScene = new Scene(root, 1200, 800, true);
        SubScene subScene = new SubScene(objectGroup, 1200, 700, true, SceneAntialiasing.BALANCED);
        subScene.setFill(Color.BLACK);
        subScene.setCamera(camera);
        subScene.widthProperty().bind(mainScene.widthProperty());


        controller.setCenter(subScene);
        controller.setGravityPool(pool);

        primaryStage = stage;
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(mainScene);

        objectGroup.getChildren().addAll(bodies);

        pool.addAll(bodies);
        pool.reduceScaleBy(1e4);
        pool.startSimulation(false);//TODO return
        primaryStage.show(); //TODO return

    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() throws Exception {
        pool.stopSimulation();
        super.stop();
    }
}




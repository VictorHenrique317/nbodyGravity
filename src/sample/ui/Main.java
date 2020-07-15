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

public class Main extends Application {
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
        createBodies();
//        Camera camera = new PerspectiveCamera(true);
        camera.setNearClip(0.01);
        camera.setFarClip(10_000_000);
        camera.setTranslateZ(-2202020);

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getClassLoader().getResource("MainScene.fxml"));
        Parent root = fxmlLoader.load();
        controller = fxmlLoader.getController();
        Scene mainScene = new Scene(root, 1200, 800, true);
        SubScene subScene = new SubScene(objectGroup, 1200, 700, true, SceneAntialiasing.BALANCED);
        subScene.setFill(Color.BLACK);
        subScene.setCamera(camera);
        subScene.widthProperty().bind(mainScene.widthProperty());
//        subScene.widthProperty().bin


        controller.setCenter(subScene);
        controller.setGravityPool(pool);

        primaryStage = stage;
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(mainScene);

        objectGroup.getChildren().addAll(bodies);

        pool.addAll(bodies);
        pool.reduceScaleBy(1e4);
//        pool.reduceScaleBy(3000);
        initMouseCommand(mainScene);
        initKeyboardControl(controller);
        pool.startSimulation(false);//TODO return
        primaryStage.show(); //TODO return

    }

    private void createBodies() {
        //sun radius 696_340e3
        Star sun = Star.sun();
        ImageView sunFlare = Star.addFlare(sun);
        objectGroup.getChildren().add(sunFlare);

        Body mercury = Planet.mercury();
        Body thirdBody = new Body(2_439e5, 0, 0, -6.1e10, 6E10, "thirdBody", null);
        bodies = List.of(sun, mercury);

        pool.orbit(List.of(mercury), sun);
//        controller.addBodies(bodies);

        PhongMaterial material = new PhongMaterial();
        PhongMaterial material1 = new PhongMaterial();
        PhongMaterial material2 = new PhongMaterial();

        material.setDiffuseMap(new Image(Objects.requireNonNull(getClass().getClassLoader().
                getResource("moon_text.jpg")).toExternalForm()));
        material1.setDiffuseColor(Color.RED);
        material2.setDiffuseColor(Color.RED);

        mercury.setMaterial(material1);
        thirdBody.setMaterial(material2);
    }

    private void initKeyboardControl(MainScene controller) {
        primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, (event) -> {
            if (event.getCode() == KeyCode.PERIOD) controller.accelerateSimulation();
            if (event.getCode() == KeyCode.COMMA) controller.decelerateSimulation();
        });
    }

    private void initMouseCommand(Scene scene) {
        Main.objectGroup.getTransforms().addAll(
                xRotate = new Rotate(0, Rotate.X_AXIS),
                yRotate = new Rotate(0, Rotate.Y_AXIS)
        );

        Main.objectGroup.getChildren().forEach((node) -> {
            node.setOnMouseClicked((mouseEvent) -> {
                System.out.println("Locking on object");
                if (mouseEvent.getClickCount() == 2) {
                    lockCameraAt(node);
                }
            });
        });
        xRotate.angleProperty().bind(xAngle);
        yRotate.angleProperty().bind(yAngle);

        scene.setOnMousePressed(mouseEvent -> {
            xAnchor = mouseEvent.getSceneX();
            yAnchor = mouseEvent.getSceneY();
            xAngleAnchor = xAngle.get();
            yAngleAnchor = yAngle.get();
        });

        scene.setOnMouseDragged(mouseEvent -> {
            xAngle.set(xAngleAnchor - ((yAnchor - mouseEvent.getSceneY()) / 10));
            yAngle.set(yAngleAnchor + ((xAnchor - mouseEvent.getSceneX()) / 10));
        });

        primaryStage.addEventHandler(ScrollEvent.SCROLL, (scrollEvent -> {
            double delta = scrollEvent.getDeltaY();
            if (camera.getTranslateZ() >= 0){
                camera.setTranslateZ(0);
                return;
            }
            double scrollingVelocity = (camera.getTranslateZ() * -1) /1e3;
            System.out.println(scrollingVelocity);
            camera.setTranslateZ(camera.getTranslateZ() + delta * scrollingVelocity);
        }));
    }

    public static void lockCameraAt(Node body) {
        xRotate.pivotXProperty().bind(body.translateXProperty());
        xRotate.pivotYProperty().bind(body.translateYProperty());
        xRotate.pivotZProperty().bind(body.translateZProperty());

        yRotate.pivotXProperty().bind(body.translateXProperty());
        yRotate.pivotYProperty().bind(body.translateYProperty());
        yRotate.pivotZProperty().bind(body.translateZProperty());

        camera.translateXProperty().bind(body.translateXProperty());
        camera.translateYProperty().bind(body.translateYProperty());
    }

    public static Collection<Body> getBodies() {
        return bodies;
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

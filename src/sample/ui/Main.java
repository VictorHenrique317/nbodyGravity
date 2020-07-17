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
    // ================================= Visuals ================================= //
    private static Stage primaryStage;
    private MainScene controller;
    public static final Camera camera = new PerspectiveCamera(true);
    private static boolean isCameraLocked = false;
    // ================================= Mechanics ================================= //
    public static final Group objectGroup = new Group();
    private static final Group mainGroup = new Group();
    private static final GravityPool pool = new GravityPool();
    private static Body lockedObject;
    // ================================= Rotation ================================= //
    private static double xAnchor, yAnchor, xAngleAnchor, yAngleAnchor;
    private static Collection<Body> bodies;
    private static SimpleDoubleProperty xAngle = new SimpleDoubleProperty(0);
    private static SimpleDoubleProperty yAngle = new SimpleDoubleProperty(0);
    private static Rotate xRotate;
    private static Rotate yRotate;


    @Override
    public void start(Stage stage) throws Exception {
        createBodies();
//        Camera camera = new PerspectiveCamera(true);
        camera.setNearClip(0.01);
        camera.setFarClip(10_000_000);
        camera.setTranslateZ(-2_202_020);

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getClassLoader().getResource("MainScene.fxml"));
        Parent root = fxmlLoader.load();
        controller = fxmlLoader.getController();
        Scene mainScene = new Scene(root, 1200, 800, true);
        SubScene subScene = new SubScene(mainGroup, 1200, 700, true, SceneAntialiasing.BALANCED);
        subScene.setFill(Color.BLACK);
        subScene.setCamera(camera);
        subScene.widthProperty().bind(mainScene.widthProperty());
//        subScene.widthProperty().bin


        controller.setCenter(subScene);
        controller.setGravityPool(pool);

        primaryStage = stage;
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(mainScene);

        mainGroup.getChildren().add(objectGroup);

        pool.addAll(bodies);
        pool.reduceScaleBy(500);
//        pool.reduceScaleBy(3000);
        initMouseCommand(mainScene);
        initKeyboardControl(controller);
        trackObject((Body) objectGroup.getChildren().get(0));
        pool.startSimulation(false);//TODO return
        primaryStage.show(); //TODO return

    }

    private void createBodies() {
        Star sun = Star.sun();
//        PointLight sunLight = new PointLight();
//        sunLight.translateXProperty().bind(sun.translateXProperty().add(sun.radiusProperty()));
//        sunLight.translateYProperty().bind(sun.translateYProperty().add(sun.radiusProperty()));
//        sunLight.translateZProperty().bind(sun.translateZProperty().add(sun.radiusProperty()));
//        sunLight.minWidth(sun.getRadius() *2);
//        sunLight.minHeight(sun.getRadius() * 2);
//        ImageView sunFlare = Star.addFlare(sun);
        Body mercury = Planet.mercury();
        Body venus = Planet.venus();

        bodies = List.of(sun, mercury, venus); // todo venus not orbiting
        objectGroup.getChildren().addAll(bodies);
        pool.orbit(List.of(mercury, venus), sun);

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
        xRotate.angleProperty().bind(xAngle);
        yRotate.angleProperty().bind(yAngle);

        final ArrayList<ImageView> flares = new ArrayList<>(); // TODO Only supports one sun
        Rotate xFlareRotate = new Rotate(0, Rotate.X_AXIS);
        Rotate yFlareRotate = new Rotate(0, Rotate.Y_AXIS);
        Rotate zFlareRotate = new Rotate(0, Rotate.Z_AXIS);

        for (Node child : objectGroup.getChildren()) {
            child.setOnMouseClicked((mouseEvent) -> {
                if (mouseEvent.getClickCount() == 2) {
                    trackObject((Body) child);
                }
            });
            if (child instanceof ImageView) {
                child.getTransforms().addAll(yFlareRotate, xFlareRotate, zFlareRotate);
                flares.add((ImageView) child);
            }
        }
        if (flares.size() > 0) {
            xFlareRotate.pivotXProperty().bind(flares.get(0).layoutXProperty().multiply(-1));
            xFlareRotate.pivotYProperty().bind(flares.get(0).layoutYProperty().multiply(-1));

            yFlareRotate.pivotXProperty().bind(flares.get(0).layoutXProperty().multiply(-1));
            yFlareRotate.pivotYProperty().bind(flares.get(0).layoutYProperty().multiply(-1));

            zFlareRotate.pivotXProperty().bind(flares.get(0).layoutXProperty().multiply(-1));
            zFlareRotate.pivotYProperty().bind(flares.get(0).layoutYProperty().multiply(-1));
        }

        // ================================== Rotation ================================== //
        scene.setOnMousePressed(mouseEvent -> {
            xAnchor = mouseEvent.getSceneX();
            yAnchor = mouseEvent.getSceneY();
            xAngleAnchor = xAngle.get();
            yAngleAnchor = yAngle.get();
        });

        scene.setOnMouseDragged(mouseEvent -> {
            double xRotationValue;
            double yRotationValue;

            xRotationValue = xAngleAnchor - ((yAnchor - mouseEvent.getSceneY()) / 10);
            yRotationValue = yAngleAnchor + ((xAnchor - mouseEvent.getSceneX()) / 10);

            xAngle.set(xRotationValue);
            yAngle.set(yRotationValue);
            xFlareRotate.setAngle(-xRotationValue);
            yFlareRotate.setAngle(-yRotationValue);
            zFlareRotate.setAngle((xRotationValue + yRotationValue) / 10);
        });
        // ================================== Rotation ================================== //

        // ================================== Scrolling ================================== //

        primaryStage.addEventHandler(ScrollEvent.SCROLL, (scrollEvent -> {
            double mouseDelta = scrollEvent.getDeltaY(); // zoom in > 0 / zoom out < 0
            double zDelta = camera.getTranslateZ() - lockedObject.getTranslateZ();

            if ((int) zDelta >= (int) (lockedObject.getRadius() * -2)) {
                if (mouseDelta > 0) {
//                    System.out.println(lockedObject.getName() + "/ Locking camera at position " + lockedObject.getRadius() * -2);
                    camera.setTranslateZ(lockedObject.getTranslateZ() + (lockedObject.getRadius() * -2));
                    return;
                }
            }
            double scrollingVelocity = (zDelta * -1) / 1e3;
            try {
                camera.setTranslateZ(camera.getTranslateZ() + (mouseDelta * scrollingVelocity));
            } catch (java.lang.RuntimeException e) {
                //dumb code
            }

        }));
        // ================================== Scrolling ================================== //
    }

    public static void trackObject(Body body) {
        lockedObject = body;
        System.out.println("Locking camera at object " + lockedObject.getName());
        xRotate.pivotXProperty().bind(lockedObject.translateXProperty());
        xRotate.pivotYProperty().bind(lockedObject.translateYProperty());
        xRotate.pivotZProperty().bind(lockedObject.translateZProperty());

        yRotate.pivotXProperty().bind(lockedObject.translateXProperty());
        yRotate.pivotYProperty().bind(lockedObject.translateYProperty());
        yRotate.pivotZProperty().bind(lockedObject.translateZProperty());

        camera.translateXProperty().bind(lockedObject.translateXProperty());
        camera.translateYProperty().bind(lockedObject.translateYProperty());
        camera.setTranslateZ(lockedObject.getTranslateZ() + (lockedObject.getRadius() * -4));
    }

    public static void lockCamera() {
        if (!isCameraLocked) {
            isCameraLocked = true;
            camera.translateZProperty().bind(lockedObject.translateZProperty().add(lockedObject.getRadius() * -20));
        } else {
            isCameraLocked = false;
            camera.translateZProperty().unbind();
        }
    }

    public static Collection<Body> getBodies() {
        return bodies;
    }

    public static boolean isIsCameraLocked() {
        return isCameraLocked;
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

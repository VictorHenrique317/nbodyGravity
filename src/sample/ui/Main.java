package sample.ui;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import sample.physics.models.Body;
import sample.physics.GravityPool;
import sample.physics.models.Planet;
import sample.physics.models.Star;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

public class Main extends Application {
    // ================================= Visuals ================================= //
    private static Stage primaryStage;
    private static final Camera camera = new PerspectiveCamera(true);
    private static boolean isCameraLocked = false;
    private static boolean isRadiusIncreased = false;
    private static ExecutorService radiusHandler = Executors.newSingleThreadExecutor();
    private static SimpleDoubleProperty cameraZ = new SimpleDoubleProperty(0);
    // ================================= Mechanics ================================= //
    private static Stage mainStage;
    private static Stage selectionStage;
    private static Scene mainScene;
    private static SubScene subScene;
    private static MainScene controller;
    private static SelectionWindow selectionWindowController;
    public static final Group objectGroup = new Group();
    private static final Group mainGroup = new Group();
    private static GravityPool pool;
    private static Body lockedObject;
    private static Body centralBody = null;
    private static ExecutorService thread = Executors.newSingleThreadExecutor();
    // ================================= Rotation ================================= //
    private static double xAnchor, yAnchor, xAngleAnchor, yAngleAnchor;
    private static ObservableList<Body> bodies;
    private static SimpleDoubleProperty xAngle = new SimpleDoubleProperty(0);
    private static SimpleDoubleProperty yAngle = new SimpleDoubleProperty(0);
    private static Rotate xRotate;
    private static Rotate yRotate;
    private static boolean custom;


    @Override
    public void start(Stage stage) throws Exception {
        mainStage = stage;
        selectionStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getClassLoader().getResource("SelectionWindow.fxml"));
        Parent root = fxmlLoader.load();
        selectionWindowController = fxmlLoader.getController();
        Scene scene = new Scene(root, 600, 400);
        selectionStage.setScene(scene);
        selectionStage.initStyle(StageStyle.UNDECORATED);
        selectionStage.show();
    }

    static void createMainScene(boolean hasCreationBox) {
        thread.execute(() -> {
            camera.setNearClip(0.01);
            camera.setFarClip(1_000_000);
            camera.setTranslateZ(-50);

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Main.class.getClassLoader().getResource("MainScene.fxml"));
            Parent root = null;
            try {
                root = fxmlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert root != null;
            controller = fxmlLoader.getController();
            mainScene = new Scene(root, 1200, 800, true);
            subScene = new SubScene(mainGroup, 1200, 700, true, SceneAntialiasing.BALANCED);
            subScene.setFill(Color.BLACK);
            subScene.setCamera(camera);
            primaryStage = mainStage;
            primaryStage.setTitle("");
            Platform.runLater(() -> primaryStage.setScene(mainScene));

            controller.setCenter(subScene, mainScene);
            controller.setGravityPool(pool);

            mainGroup.getChildren().add(objectGroup);

            if (hasCreationBox) {
                controller.createOptionBox();
                controller.disableDeleteButton();
                subScene.widthProperty().bind(mainScene.widthProperty().subtract(400));
            } else {
                subScene.widthProperty().bind(mainScene.widthProperty().subtract(200));
            }
            subScene.heightProperty().bind(mainScene.heightProperty().subtract(75));

            if (custom) {
                controller.createCustomOptions();
            }
            initMouseCommand(mainScene);
            initKeyboardControl(controller);
            trackObject(centralBody);
            Platform.runLater(() -> {
                selectionStage.close();
                selectionWindowController.stop();
                primaryStage.show();
            });
        });
    }

    static void createCustomSimulation() {
        custom = true;
        bodies = FXCollections.observableArrayList();
        bodies.addListener(new ListChangeListener<Body>() {
            @Override
            public void onChanged(Change<? extends Body> change) {
                if (bodies.size() == 0) {
                    controller.disableDeleteButton();
                } else {
                    controller.enableDeleteButton();
                }
            }
        });
        pool = new GravityPool(GravityPool.Types.Nbody, bodies);
        pool.reduceScaleBy(1);
        createMainScene(true);
    }

    static void createSolarSystem() {
        custom = false;
        thread.execute(() -> {
            createBodies();
            pool = new GravityPool(GravityPool.Types.classic, bodies.get(0), bodies);
            pool.reduceScaleBy(1e8);
            pool.startSimulation();
        });
        createMainScene(false);
    }

    private static void createBodies() {
        Star sun = Star.sun();
        centralBody = sun;
        AmbientLight ambientLight = new AmbientLight();
        ambientLight.setColor(Color.rgb(61, 61, 61));
        PointLight light = new PointLight();

        Body mercury = Planet.mercury();
        Body venus = Planet.venus();
        Body earth = Planet.earth();
        Body mars = Planet.mars();
        Body jupiter = Planet.jupiter();
        ArrayList<Node> saturnAndRing = Planet.saturn();
        ArrayList<Node> uranusAndRing = Planet.uranus();
        Body neptune = Planet.neptune();
        bodies = FXCollections.observableArrayList(List.of(sun, mercury, venus, earth, mars, jupiter,
                (Body) saturnAndRing.get(0), (Body) uranusAndRing.get(0), neptune));

        objectGroup.getChildren().addAll(bodies);
        objectGroup.getChildren().add(saturnAndRing.get(1));
        objectGroup.getChildren().add(uranusAndRing.get(1));
        objectGroup.getChildren().add(ambientLight);
        objectGroup.getChildren().add(light);
    }

    static void addBody(Body body) {
        bodies.add(body);
        objectGroup.getChildren().add(body);
    }

    public static void deleteLastObject() {
        int objectListSize = objectGroup.getChildren().size();
        int bodyListSize = bodies.size();
        objectGroup.getChildren().remove(objectListSize - 1);
        bodies.remove(bodyListSize - 1);
        stopSimulation();
    }

    public static void clearObjects() {
        objectGroup.getChildren().clear();
        bodies.clear();
        stopSimulation();
    }

    public static void startSimulation() {
        pool.stopSimulation();
        pool.startSimulation();
    }

    static void stopSimulation() {
        pool.stopSimulation();
    }


    private static void initKeyboardControl(MainScene controller) {
        primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, (event) -> {
            if (event.getCode() == KeyCode.PERIOD) controller.accelerateSimulation();
            if (event.getCode() == KeyCode.COMMA) controller.decelerateSimulation();
        });
    }

    private static void initMouseCommand(Scene scene) {
        Main.objectGroup.getTransforms().addAll(
                xRotate = new Rotate(0, Rotate.X_AXIS),
                yRotate = new Rotate(0, Rotate.Y_AXIS)
        );

        xRotate.angleProperty().bind(xAngle);
        yRotate.angleProperty().bind(yAngle);

        for (Node child : objectGroup.getChildren()) {
            child.setOnMouseClicked((mouseEvent) -> {
                if (mouseEvent.getClickCount() == 2) {
                    trackObject((Body) child);
                }
            });
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
        });
        // ================================== Rotation ================================== //
        // ================================== Scrolling ================================== //

        primaryStage.addEventHandler(ScrollEvent.SCROLL, (scrollEvent -> {
            double mouseDelta = scrollEvent.getDeltaY(); // zoom in > 0 / zoom out < 0
            double zDelta;
            if (lockedObject == null) {
                zDelta = camera.getTranslateZ();
            } else {
                zDelta = camera.getTranslateZ() - lockedObject.getTranslateZ();
                if (zDelta >= (lockedObject.getRadius() * -2)) {
                    if (mouseDelta > 0) {
                        return;
                    }
                }
            }


            double scrollingVelocity = (zDelta * -1) / 1e3;
//            double scrollingVelocity = (zDelta * -1);
            try {
                cameraZ.set(cameraZ.doubleValue() + (mouseDelta * scrollingVelocity));
                handleRadius();
            } catch (java.lang.RuntimeException e) {
                //dumb code
            }

        }));
        // ================================== Scrolling ================================== //
    }

    public static void trackObject(Body body) {
        if (centralBody == null) {
            System.out.println("No object to track");
            cameraZ.set(-50);
            camera.translateZProperty().bind(cameraZ);
            return;
        }
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

        cameraZ.set(0);
        camera.translateZProperty().bind(cameraZ.add(lockedObject.translateZProperty().add
                (lockedObject.radiusProperty().multiply(-4))));
        handleRadius();
    }

    static void lockCamera() {
        if (lockedObject == null) return;
        if (!isCameraLocked) {
            isCameraLocked = true;
            camera.translateZProperty().bind(lockedObject.translateZProperty().add
                    (lockedObject.radiusProperty().multiply(-20)));
            changeRadius(1);
        } else {
            isCameraLocked = false;
            trackObject(lockedObject);
        }
    }

    private static void handleRadius() {
        if (custom) return;
        double baseRatio = 196;
        if ((camera.getTranslateZ() <= -1.5e3 && !isRadiusIncreased) || camera.getTranslateZ() >= -1.5e3 && isRadiusIncreased) {
            if (!isRadiusIncreased) {
                System.out.println("increasing");
                isRadiusIncreased = true;
            } else {
                System.out.println("decreasing");
                baseRatio = 1;
                isRadiusIncreased = false;
            }
            changeRadius(baseRatio);
        }
    }

    private static void changeRadius(double ratio) {
        for (Body body : bodies) {
            double usableRatio = ratio;
            if (bodies.indexOf(body) == 0) usableRatio = Math.sqrt(ratio);
            Timeline til = new Timeline(new KeyFrame(
                    Duration.millis(1500),
                    new KeyValue(body.radiusProperty(), body.getBaseRadius() * usableRatio)
            ));
            radiusHandler.execute(til::play);
        }
    }

    static boolean isIsCameraLocked() {
        return isCameraLocked;
    }

    static Scene getMainScene() {
        return mainScene;
    }

    public static SubScene getSubscene() {
        return subScene;
    }

    public static Camera getCamera() {
        return camera;
    }

    public static double getLengthHeightRatio() {
        return mainStage.getWidth() / mainStage.getHeight();
    }

    static Collection<Body> getBodies() {
        return bodies;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() throws Exception {
        try {
            pool.stopSimulation();
            radiusHandler.shutdown();
        } catch (NullPointerException e) {
            // pass
        }
        for (Body body : bodies) {
            if (body instanceof Planet) {
                ((Planet) body).stopRotation();
            }
        }
        thread.shutdown();
        super.stop();
    }
}

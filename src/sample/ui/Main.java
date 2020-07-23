package sample.ui;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;
import sample.physics.models.Body;
import sample.physics.GravityPool;
import sample.physics.models.Planet;
import sample.physics.models.Star;

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
    private static SimpleDoubleProperty cameraZ =  new SimpleDoubleProperty(0);
    // ================================= Mechanics ================================= //
    public static final Group objectGroup = new Group();
    private static final Group mainGroup = new Group();
    private static GravityPool pool;
    private static Body lockedObject;
    private static Body centralBody = null;
    // ================================= Rotation ================================= //
    private static double xAnchor, yAnchor, xAngleAnchor, yAngleAnchor;
    private static ArrayList<Body> bodies;
    private static SimpleDoubleProperty xAngle = new SimpleDoubleProperty(0);
    private static SimpleDoubleProperty yAngle = new SimpleDoubleProperty(0);
    private static Rotate xRotate;
    private static Rotate yRotate;


    @Override
    public void start(Stage stage) throws Exception {
        createBodies();
        pool = new GravityPool(GravityPool.Types.classic, bodies.get(0));
//        pool = new GravityPool(GravityPool.Types.Nbody);
        pool.addAll(bodies);
        pool.reduceScaleBy(1e8);
//        pool.reduceScaleBy(1);

        camera.setNearClip(0.01);
        camera.setFarClip(1_000_000);
        camera.setTranslateZ(-2_202_020);

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getClassLoader().getResource("MainScene.fxml"));
        Parent root = fxmlLoader.load();
        MainScene controller = fxmlLoader.getController();
        Scene mainScene = new Scene(root, 1200, 800, true);
        SubScene subScene = new SubScene(mainGroup, 1200, 700, true, SceneAntialiasing.BALANCED);
        subScene.setFill(Color.BLACK);
        subScene.setCamera(camera);
        subScene.widthProperty().bind(mainScene.widthProperty());
        controller.setCenter(subScene);
        controller.setGravityPool(pool);
        primaryStage = stage;
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(mainScene);

        mainGroup.getChildren().add(objectGroup);

        initMouseCommand(mainScene);
        initKeyboardControl(controller);
        trackObject(centralBody);
        pool.startSimulation();
        primaryStage.show();

    }

    private void createBodies() {
        Star sun = Star.sun();
        centralBody = sun;

//        double radius = 10;
//        radius.bind(sun.radiusProperty());

//        PointLight light1 = new PointLight();
//        light1.setTranslateX(10);

//        PointLight light2 = new PointLight();
//        light2.setTranslateX(-10);

//        objectGroup.getChildren().addAll(light1, light2);
        AmbientLight ambientLight = new AmbientLight();
        ambientLight.setColor(Color.rgb(61, 61, 61));
        PointLight light = new PointLight();

//        for(int x = -20; x <= 20 ; x++){ //x^2 + z^2 = r^2
//            double zValue = Math.sqrt(radius*radius - x*x );
//            Sphere sphere1 = new Sphere(1);
//            PointLight light1 = new PointLight();
//            light1.setTranslateX(x);
//            sphere1.setTranslateX(x);
//            light1.setTranslateZ(zValue);
//            sphere1.setTranslateZ(zValue);
//
//            Sphere sphere2 = new Sphere(1);
//            PointLight light2 = new PointLight();
//            light2.setTranslateX(x);
//            sphere2.setTranslateX(x);
//            light2.setTranslateZ(zValue * -1);
//            sphere2.setTranslateZ(zValue * -1);
//            objectGroup.getChildren().addAll(light1, light2, sphere1, sphere2);
//
//        }

        Body mercury = Planet.mercury();
        Body venus = Planet.venus();
        Body earth = Planet.earth();
        Body mars = Planet.mars();
        Body jupiter = Planet.jupiter();
        ArrayList<Node> saturnAndRing = Planet.saturn();
        bodies = new ArrayList<>(List.of(sun, mercury, venus, earth, mars, jupiter, (Body) saturnAndRing.get(0)));
//        ArrayList<Body> planets = new ArrayList<>(bodies);
//        planets.remove((Body) saturnAndRing.get(0));

        objectGroup.getChildren().addAll(bodies);
        objectGroup.getChildren().add(saturnAndRing.get(1));
        objectGroup.getChildren().add(ambientLight);
        objectGroup.getChildren().add(light);
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


//        final ArrayList<ImageView> flares = new ArrayList<>();
//        Rotate xFlareRotate = new Rotate(0, Rotate.X_AXIS);
//        Rotate yFlareRotate = new Rotate(0, Rotate.Y_AXIS);
//        Rotate zFlareRotate = new Rotate(0, Rotate.Z_AXIS);

        for (Node child : objectGroup.getChildren()) {
            child.setOnMouseClicked((mouseEvent) -> {
                if (mouseEvent.getClickCount() == 2) {
                    trackObject((Body) child);
                }
            });
//            if (child instanceof ImageView) {
//                child.getTransforms().addAll(yFlareRotate, xFlareRotate, zFlareRotate);
//                flares.add((ImageView) child);
//            }
        }
//        if (flares.size() > 0) {
//            xFlareRotate.pivotXProperty().bind(flares.get(0).layoutXProperty().multiply(-1));
//            xFlareRotate.pivotYProperty().bind(flares.get(0).layoutYProperty().multiply(-1));
//
//            yFlareRotate.pivotXProperty().bind(flares.get(0).layoutXProperty().multiply(-1));
//            yFlareRotate.pivotYProperty().bind(flares.get(0).layoutYProperty().multiply(-1));
//
//            zFlareRotate.pivotXProperty().bind(flares.get(0).layoutXProperty().multiply(-1));
//            zFlareRotate.pivotYProperty().bind(flares.get(0).layoutYProperty().multiply(-1));
//        }

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
//            xFlareRotate.setAngle(-xRotationValue);
//            yFlareRotate.setAngle(-yRotationValue);
//            zFlareRotate.setAngle((xRotationValue + yRotationValue) / 10);
        });
        // ================================== Rotation ================================== //

        // ================================== Scrolling ================================== //

        primaryStage.addEventHandler(ScrollEvent.SCROLL, (scrollEvent -> {
            double mouseDelta = scrollEvent.getDeltaY(); // zoom in > 0 / zoom out < 0
            double zDelta = camera.getTranslateZ() - lockedObject.getTranslateZ();

            if (zDelta >= (lockedObject.getRadius() * -2)) {
                if (mouseDelta > 0) {
                    return;
                }
            }
            double scrollingVelocity = (zDelta * -1) / 1e3;
            try {
                cameraZ.set(cameraZ.doubleValue() + (mouseDelta * scrollingVelocity));
                handleRadius();
            } catch (java.lang.RuntimeException e) {
                //dumb code
            }

        }));
        // ================================== Scrolling ================================== //
    }

    private static void handleRadius() {
        double baseRatio = 144;
        if ((camera.getTranslateZ() <= -2e3 && !isRadiusIncreased) || camera.getTranslateZ() >= -2e3 && isRadiusIncreased) {
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

        cameraZ.set(0);
        camera.translateZProperty().bind(cameraZ.add(lockedObject.translateZProperty().add
                (lockedObject.radiusProperty().multiply(-4))));
        handleRadius();
    }

    static void lockCamera() {
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

    static Collection<Body> getBodies() {
        return bodies;
    }

    static boolean isIsCameraLocked() {
        return isCameraLocked;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() throws Exception {
        pool.stopSimulation();
        radiusHandler.shutdown();
        super.stop();
    }
}

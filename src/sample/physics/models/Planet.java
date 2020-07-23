package sample.physics.models;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.security.Key;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Planet extends Body {
    private static final double secondsInADay = 86_400;
    private final double rotationPeriod; // in earth days
    private ExecutorService rotationExecutor;

    public Planet(double radius, double x, double y, double z, double mass, String name, Image icon, double rotationPeriod) {
        super(radius, x, y, z, mass, name, icon);
        this.rotationPeriod = rotationPeriod;
    }

    public void startRotation(double speed) {
        this.setRotationAxis(Rotate.Y_AXIS);
        Timeline til = new Timeline(new KeyFrame(
                Duration.seconds(rotationPeriod * secondsInADay/speed),
                new KeyValue(this.rotateProperty(), 360)
        ));
        System.out.println("starting rotation of "  + this.getName());
        rotationExecutor = Executors.newSingleThreadExecutor();
        rotationExecutor.execute(()->{
            new Timeline(new KeyFrame(
                    Duration.seconds(rotationPeriod/ secondsInADay / speed),
                    (event)->{
                        til.play();
                    }
            )).play();
        });
    }

    public void stopRotation(){
        if (rotationExecutor != null) rotationExecutor.shutdown();
        rotationExecutor = null;
    }

    public static Body mercury() {
        Image icon = new Image(Objects.requireNonNull(
                Planet.class.getClassLoader().getResource("mercury.png")).toExternalForm());
        Image text = new Image(Objects.requireNonNull(
                Planet.class.getClassLoader().getResource("mercury_text.jpg")).toExternalForm());
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseMap(text);
        //5.8e10
        Planet mercury = new Planet(2_439e3, 0, 0, 0.46e11, 0.32E24, "Mercury", icon, 0.0001);
        mercury.setxVelocity(5.8e4);
        mercury.setMaterial(material);
        return mercury;
    }

    public static Body venus() {
        Image icon = new Image(Objects.requireNonNull(
                Planet.class.getClassLoader().getResource("venus 1.png")).toExternalForm());
        Image text = new Image(Objects.requireNonNull(
                Planet.class.getClassLoader().getResource("venus_texture.jpg")).toExternalForm());
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseMap(text);

        Planet venus = new Planet(6_051e3, 0, 0, 1.07e11, 4.867e24, "Venus", icon, 1);
        venus.setxVelocity(3.5e4);
        venus.setMaterial(material);
        return venus;
    }

    public static Body earth() {
        Image icon = new Image(Objects.requireNonNull(
                Planet.class.getClassLoader().getResource("earth.png")).toExternalForm());
        Image text = new Image(Objects.requireNonNull(
                Planet.class.getClassLoader().getResource("earth_texture.jpg")).toExternalForm());
        Image spec = new Image(Objects.requireNonNull(
                Planet.class.getClassLoader().getResource("earth_pec.tif")).toExternalForm());
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseMap(text);

        Planet earth = new Planet(6_378e3, 0, 0, 1.47e11, 5.9e24, "Earth", icon, 1);
        earth.setxVelocity(3e4);
        earth.setMaterial(material);
        return earth;
    }

    public static Body mars() {
        Image icon = new Image(Objects.requireNonNull(
                Planet.class.getClassLoader().getResource("mars.png")).toExternalForm());
        Image text = new Image(Objects.requireNonNull(
                Planet.class.getClassLoader().getResource("mars_texture.jpg")).toExternalForm());
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseMap(text);

        Planet mars = new Planet(3_396e3, 0, 0, 2.06e11, 0.64e24, "Mars", icon, 1);
        mars.setxVelocity(2.6e4);
        mars.setMaterial(material);
        return mars;
    }

    public static Body jupiter() {
        Image icon = new Image(Objects.requireNonNull(
                Planet.class.getClassLoader().getResource("jupiter.png")).toExternalForm());
        Image text = new Image(Objects.requireNonNull(
                Planet.class.getClassLoader().getResource("jupiter_texture.jpg")).toExternalForm());
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseMap(text);

        Planet jupiter = new Planet(71_398e3, 0, 0, 7.4e11, 1_898e24, "Jupiter", icon, 1);
        jupiter.setxVelocity(1.3e4);
        jupiter.setMaterial(material);
        return jupiter;
    }

    public static ArrayList<Node> saturn() {
        Image icon = new Image(Objects.requireNonNull(
                Planet.class.getClassLoader().getResource("saturn.png")).toExternalForm());
        Image text = new Image(Objects.requireNonNull(
                Planet.class.getClassLoader().getResource("saturn_texture.jpg")).toExternalForm());
        Image ringText = new Image(Objects.requireNonNull(
                Planet.class.getClassLoader().getResource("saturn_ring.png")).toExternalForm());
        PhongMaterial material = new PhongMaterial();
        PhongMaterial ringMaterial = new PhongMaterial();
        material.setDiffuseMap(text);
        ringMaterial.setDiffuseMap(ringText);

        Planet saturn = new Planet(60_268e3, 0, 0, 13.52e11, 568e24, "Saturn", icon, 1);
        SimpleDoubleProperty ringRadius = new SimpleDoubleProperty(0);
        ringRadius.bind(saturn.radiusProperty().multiply(3));

        Cylinder ring = new Cylinder(ringRadius.doubleValue(), 0.01);
        ring.setMaterial(ringMaterial);

        ring.radiusProperty().bind(ringRadius);
        ring.translateXProperty().bind(saturn.translateXProperty());
        ring.translateYProperty().bind(saturn.translateYProperty());
        ring.translateZProperty().bind(saturn.translateZProperty());

        saturn.setxVelocity(0.9e4);
        saturn.setMaterial(material);
        return new ArrayList<>(List.of(saturn, ring));
    }

}

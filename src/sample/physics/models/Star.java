package sample.physics.models;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.PhongMaterial;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Star extends Body {
    public Star(double radius, double x, double y, double z, double mass, String name, Image icon) {
        super(radius, x, y, z, mass, name, icon);
    }

    public static Star sun() {
        Image icon = new Image(Objects.requireNonNull(
                Star.class.getClassLoader().getResource("sun.png")).toExternalForm());
        Image texture = new Image(Objects.requireNonNull(
                Star.class.getClassLoader().getResource("1.jpg")).toExternalForm());
        Image ilm_map = new Image(Objects.requireNonNull(
                Star.class.getClassLoader().getResource("ilm_map.jpg")).toExternalForm());
        Star sun = new Star(696_340e3, 0, 0, 0, 1.9e30, "Sun", icon);
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseMap(texture);
        material.setSelfIlluminationMap(ilm_map);
        sun.setMaterial(material);

        return sun;
    }

    public static ImageView addFlare(Star sun) {
        Image image = new Image(Objects.requireNonNull(
                Star.class.getClassLoader().getResource("sun/sunflare.png")).toExternalForm());
        ImageView flare = new ImageView(image);
        flare.setFitWidth(2e7);
        flare.setFitHeight(2e7);
        flare.setLayoutX(-1e7);
        flare.setLayoutY(-1e7);

//        flare.translateXProperty().bind(sun.translateXProperty().subtract(flare.getFitWidth()/2));
//        flare.translateYProperty().bind(sun.translateYProperty().subtract(flare.getFitHeight()/2));
//        flare.translateZProperty().bind(sun.translateZProperty());
        flare.translateXProperty().bind(sun.translateXProperty());
        flare.translateYProperty().bind(sun.translateYProperty());
        flare.translateZProperty().bind(sun.translateZProperty());


//        flare.setTranslateX(-1 * flare.getFitWidth()/2);
//        flare.setTranslateY(-1 * flare.getFitHeight()/2);
//        flare.setTranslateZ(0);
        return flare;
    }
}

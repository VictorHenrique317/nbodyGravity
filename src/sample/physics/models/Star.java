package sample.physics.models;

import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.PhongMaterial;

import java.util.Objects;

public class Star extends Body {
    public Star(double radius, double x, double y, double z, double mass, String name, Image icon) {
        super(radius, x, y, z, mass, name, icon);
    }

    public static Star sun(){
        Image icon = new Image(Objects.requireNonNull(
                Star.class.getClassLoader().getResource("sun.png")).toExternalForm());
        Image texture = new Image(Objects.requireNonNull(
                Star.class.getClassLoader().getResource("sun_text.jpg")).toExternalForm());
        Star sun = new Star(696_340e3, 0, 0, 0,1.9e30, "Sun", icon);
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseMap(texture);
        Glow glow = new Glow();
        glow.setLevel(0.8);
        sun.setMaterial(material);
        sun.setEffect(glow);
        return sun;
    }

    public static ImageView addFlare(Star sun){
        Image image = new Image(Objects.requireNonNull(
                Star.class.getClassLoader().getResource("sunflare.png")).toExternalForm());
        ImageView flare = new ImageView(image);
        flare.setFitWidth(2e7);
        flare.setFitHeight(2e7);

        flare.translateXProperty().bind(sun.translateXProperty().subtract(flare.getFitWidth()/2));
        flare.translateYProperty().bind(sun.translateYProperty().subtract(flare.getFitHeight()/2));
        flare.translateZProperty().bind(sun.translateZProperty());



//        flare.setTranslateX(-1 * flare.getFitWidth()/2);
//        flare.setTranslateY(-1 * flare.getFitHeight()/2);
//        flare.setTranslateZ(0);
        return flare;
    }
}

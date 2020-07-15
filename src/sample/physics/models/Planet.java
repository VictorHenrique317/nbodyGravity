package sample.physics.models;

import javafx.scene.image.Image;
import sample.physics.models.Body;

import java.util.Objects;

public class Planet extends Body {
    public Planet(double radius, double x, double y, double z, double mass, String name, Image icon) {
        super(radius, x, y, z, mass, name, icon);
    }

    public static Body mercury() {
        Image icon = new Image(Objects.requireNonNull(
                Planet.class.getClassLoader().getResource("mercury.png")).toExternalForm());
        return new Planet(2_439e3,0 ,0 ,6.1e10, 3.2E23, "Mercury", icon);
    }

}

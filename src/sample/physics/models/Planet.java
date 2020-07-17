package sample.physics.models;

import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;

import java.util.Objects;

public class Planet extends Body {
    public Planet(double radius, double x, double y, double z, double mass, String name, Image icon) {
        super(radius, x, y, z, mass, name, icon);
    }

    public static Body mercury() {
        Image icon = new Image(Objects.requireNonNull(
                Planet.class.getClassLoader().getResource("mercury.png")).toExternalForm());
        Image text = new Image(Objects.requireNonNull(
                Planet.class.getClassLoader().getResource("mercury_text.jpg")).toExternalForm());
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseMap(text);

        Planet mercury = new Planet(2_439e3,0 ,0 ,6.1e10, 3.2E23, "Mercury", icon);
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

        Planet venus = new Planet(6_051e3,0 ,0 ,1.082e11,  4.867e24 , "Venus", icon);
        venus.setMaterial(material);
        return venus;
    }
}

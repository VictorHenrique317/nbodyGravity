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
        //5.8e10
        Planet mercury = new Planet(2_439e3,0 ,0 ,0.46e11, 0.32E24, "Mercury", icon);
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

        Planet venus = new Planet(6_051e3,0 ,0 ,1.07e11,  4.867e24 , "Venus", icon);
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
//        material.setSpecularMap(spec);

        Planet earth = new Planet(6_378e3,0 ,0 ,1.47e11,  5.9e24 , "Earth", icon);
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

        Planet mars = new Planet(3_396e3,0 ,0 ,2.06e11,  0.64e24 , "Mars", icon);
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

        Planet jupiter   = new Planet(71_398e3,0 ,0 ,7.4e11,  1_898e24 , "Jupiter", icon);
        jupiter.setxVelocity(1.3e4);
        jupiter.setMaterial(material);
        return jupiter;
    }

}

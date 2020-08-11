package sample.physics.models;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import sample.ui.Main;

import java.util.Collection;

public class Body extends Sphere {
    private static final PhongMaterial pathMaterial = new PhongMaterial();
    private final String name;
    private final Image icon;
    private int counter = 0;
    private double baseRadius;
    private final double mass;

    private double xVelocity = 0;
    private double yVelocity = 0;
    private double zVelocity = 0;

    public Body(double baseRadius, double mass, String name, Image icon) {
        this(baseRadius, 0, 0, 0, mass, name, icon);
    }

    public Body(double baseRadius, double z, double mass, String name, Image icon) {
        this(baseRadius, 0, 0, z, mass, name, icon);
    }

    public Body(double baseRadius, double z, double y, double mass, String name, Image icon) {
        this(baseRadius, 0, y, z, mass, name, icon);
    }

    public Body(double baseRadius, double x, double y, double z, double mass, String name, Image icon) {
        super(baseRadius);
        this.setTranslateX(x);
        this.setTranslateY(y);
        this.setTranslateZ(z);
        this.setRadius(baseRadius);

        this.baseRadius = baseRadius;
        this.mass = mass;
        this.name = name;
        this.icon = icon;
        pathMaterial.setDiffuseColor(Color.RED);
    }

    public void setxVelocity(double vel) {
        this.xVelocity = vel;
    }

    public void setyVelocity(double vel) {
        this.yVelocity = vel;
    }

    public void setzVelocity(double vel) {
        this.zVelocity = vel;
    }

    public void beAttractedBy(Collection<Body> bodies, double precision, double simulationG) {
        for (Body body : bodies) {
            if (body == this && bodies.size() > 1) { // multiple bodies, no self attraction
                continue;
            }else if (bodies.size() == 1){ // single body
                move(precision);
            }else { // multiple bodies, this being attracted by another
                beAttractedBy(body, precision, simulationG);
            }
        }
    }

    public synchronized void beAttractedBy(Body body, double precision, double simulationG) {
        counter++;
        if (this == body) return;
        double xOffset = body.getTranslateX() - this.getTranslateX();
        double yOffset = body.getTranslateY() - this.getTranslateY();
        double zOffset = body.getTranslateZ() - this.getTranslateZ();

        double distance = distanceTo(body);

        double force = simulationG * (this.mass * body.mass / Math.pow(distance, 2)); // newton's gravitation
        if (distance <= this.getRadius()) {
            System.out.println(this.name + " bumped, distance is " + distance + " radius is " + this.getRadius());
            return;
        }
        double sine = yOffset / distance;
        double cosine = xOffset / distance;
        double zSine = zOffset / distance;

        double forceX = force * cosine;
        double forceY = force * sine;
        double forceZ = force * zSine;

        // f/m * s^2 = distance to travel on the axis
        this.xVelocity += (forceX / this.mass) * precision;
        this.yVelocity += (forceY / this.mass) * precision;
        this.zVelocity += (forceZ / this.mass) * precision;
        move(precision);
    }

    private void move(double precision) {
        double xDistance = this.xVelocity * precision;
        double yDistance = this.yVelocity * precision;
        double zDistance = this.zVelocity * precision;

        this.setTranslateX(this.getTranslateX() + xDistance);
        this.setTranslateY(this.getTranslateY() + yDistance);
        this.setTranslateZ(this.getTranslateZ() + zDistance);

    }

    public double distanceTo(Body body) {
        double xOffset = body.getTranslateX() - this.getTranslateX();
        double yOffset = body.getTranslateY() - this.getTranslateY();
        double zOffset = body.getTranslateZ() - this.getTranslateZ();

        double distance = Math.sqrt((Math.pow(xOffset, 2) + Math.pow(yOffset, 2))); // a^2 = b^2 + c^2
        distance = Math.sqrt(Math.pow(distance, 2) + Math.pow(zOffset, 2));
        return distance;
    }

    public void showPath(boolean showPath) {
        if (showPath) {
            Sphere sphere = new Sphere(0.5);
            sphere.setTranslateX(this.getTranslateX());
            sphere.setTranslateY(this.getTranslateY());
            sphere.setTranslateZ(this.getTranslateZ());
            sphere.setMaterial(pathMaterial);
            Main.objectGroup.getChildren().add(sphere);
        }
    }

    public double getXVelocity() {
        return this.xVelocity;
    }

    public double getMass() {
        return this.mass;
    }

    public String getName() {
        return name;
    }

    public Image getIcon() {
        return icon;
    }

    public void setBaseRadius(double baseRadius) {
        this.baseRadius = baseRadius;
        this.setRadius(baseRadius);
    }

    public double getBaseRadius() {
        return baseRadius;
    }


}

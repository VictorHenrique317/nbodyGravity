package sample.physics.models;

import javafx.beans.property.DoubleProperty;
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
    private final double mass;

    private double xVelocity = 0;
    private double yVelocity = 0;
    private double zVelocity = 0;

//    private DoubleProperty xTranslate = this.translateXProperty().multiply(9000);
//    private DoubleProperty yTranslate = this.translateYProperty().multiply(9000);
//    private DoubleProperty zTranslate = this.translateXProperty().multiply(9000);

    public Body(double radius, double mass, String name, Image icon ){
        this(radius, 0, 0, 0, mass, name, icon);
    }

    public Body(double radius, double z, double mass, String name, Image icon){
        this(radius, 0, 0, z, mass, name, icon);
    }

    public Body(double radius, double z, double y, double mass, String name, Image icon){
        this(radius, 0, y, z, mass, name, icon);
    }

    public Body(double radius, double x, double y, double z, double mass, String name, Image icon) {
        super(radius);
        this.setTranslateX(x);
        this.setTranslateY(y);
        this.setTranslateZ(z);
        this.setRadius(radius);

        this.mass = mass;
        this.name = name;
        this.icon = icon;
        pathMaterial.setDiffuseColor(Color.RED);
//        this.setOnMouseClicked((mouseEvent -> {
//            System.out.println("LOCKING");
//        }));
    }

    public void setxVelocity(double vel){
        this.xVelocity = vel;
    }

    public void setyVelocity(double vel){
//        this.yVelocity = vel;
    }

    public void beAttractedBy(Collection<Body> bodies, double precision, double simulationG){
        for (Body body : bodies){
            if (body == this) continue;
            beAttractedBy(body, precision, simulationG);
        }
    }

    public synchronized void beAttractedBy(Body body, double precision, double simulationG) {
//        System.out.println("Being attracted, xVelocity is " + xVelocity );
        counter++;
        double xOffset = body.getTranslateX() - this.getTranslateX();
        double yOffset = body.getTranslateY() - this.getTranslateY();
        double zOffset = body.getTranslateZ() - this.getTranslateZ();

//        xOffset *= 9000;
//        yOffset *= 9000;
//        zOffset *= 9000;
        double distance = distanceTo(body);

        double force = simulationG * (this.mass * body.mass / Math.pow(distance, 2)); // newton's gravitation
        if (distance <= this.getRadius()){
            System.out.println(this.name + " bumped, distance is " + distance + " radius is " + this.getRadius());
            return;
        }

        double sine =  yOffset/distance;
        double cosine = xOffset/distance;

        double zSine = zOffset / distance;


//        if (this.name.equals("Mercury") && body.getName().equals("Venus")){
//            System.out.println("force mercury- venus is " + force);
//        }

        double forceX = force * cosine;
        double forceY = force * sine;
        double forceZ = force * zSine;

        // f/m * s^2 = distance to travel on the axis
        this.xVelocity += (forceX/this.mass) * precision;
        this.yVelocity += (forceY/this.mass) * precision;
        this.zVelocity += (forceZ/this.mass) * precision;

        double xDistance = this.xVelocity * precision;
        double yDistance = this.yVelocity * precision;
        double zDistance = this.zVelocity * precision;


        if (this.name.equals("Mercury") && body.getName().equals("Venus")){
            System.out.println("distance " + distance);
//            System.out.println("Xforce is " +  forceX);
//            System.out.println("    Yforce is " +  forceY);
//            System.out.println("        Zforce is " +  forceZ);
//            System.out.println("            force is " +  force);
        }


        this.setTranslateX(this.getTranslateX() + xDistance);
        this.setTranslateY(this.getTranslateY() + yDistance);
        this.setTranslateZ(this.getTranslateZ() + zDistance);
//        System.out.println("horizontal velocity " + xVelocity);
//        System.out.println("=== DISTANCE === " + distance);
//        System.out.println("Force on x axis " + forceX);
//        System.out.println("    position " + this.getTranslateX());
//        System.out.println("        velocity " + this.xVelocity);

    }
    public double distanceTo(Body body){
        double xOffset = body.getTranslateX() - this.getTranslateX();
        double yOffset = body.getTranslateY() - this.getTranslateY();
        double zOffset = body.getTranslateZ() - this.getTranslateZ();
//        xOffset *= 9000;
//        yOffset *= 9000;
//        zOffset *= 9000;

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
    //=== DISTANCE == 292.4
    //=================================================
    //========== 2.808E17
    //========== 128.1
    //========== -262.8
    //========== -64.3
    //========== -69.8


}

package sample.physics;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import sample.physics.models.Body;
import sample.physics.models.Planet;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class GravityPool {
    private final Body centralBody;
    private ArrayList<Body> bodies;
    private Collection<Timeline> translationTimeLines;
    private double G = -1;
    private boolean flag = true;

    public enum Types {Nbody, classic}
    private Types simulationType;

    private double speed;
    private double delay;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private boolean showPath;
    private double scaleReduction = 1;

    public GravityPool(Types simulationType) {
        this(simulationType, null);
        if (simulationType == Types.classic){
            throw new IllegalArgumentException("Didn't set central body for \"classic\" simulation type");
        }
    }
    public GravityPool(Types simulationType, Body centralBody) {
        bodies = new ArrayList<>();
        translationTimeLines = new ArrayList<>();
        this.speed = 1;
        this.centralBody = centralBody;
        this.simulationType = simulationType;
    }

    public void stopSimulation() {
        for (Timeline timeline : this.translationTimeLines) {
            timeline.stop();
        }
//        for (Body body: bodies){
//            if (body instanceof Planet){
//                ((Planet) body).stopRotation();
//            }
//        }
        this.translationTimeLines.clear();
        this.executor.shutdown();
        this.executor = null;
    }


    public void add(Body body) {
        this.bodies.add(body);
    }

    public void addAll(Collection<Body> bodies) {
        this.bodies.addAll(bodies);
    }

    public Collection<Body> getBodies() {
        return this.bodies;
    }

    public void startSimulation() {
        configureGravity(centralBody);
        executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            for (Timeline timeline : translationTimeLines) {
                timeline.play();
            }
            if (flag){
                flag = false;
                for (Body body: bodies){
                    if (body instanceof Planet){
                        ((Planet) body).startRotation();
                    }
                }
            }
        });
    }

    private void configureGravity(Body centralBody) {
        this.showPath = false;
        this.delay = 50;
        Timeline til;
        for (Body i : bodies) {
            if (simulationType == Types.Nbody) {
                til = new Timeline(new KeyFrame(
                        Duration.millis(delay / (speed / 2)),
                        (e) -> {
                            i.beAttractedBy(bodies, delay * speed / 1000d, G);
                            i.showPath(showPath);
                        }));
            }else {
                til = new Timeline(new KeyFrame(
                        Duration.millis(delay / (speed / 2)),
                        (e) -> {
                            i.beAttractedBy(centralBody, delay * speed / 1000d, G);
                            i.showPath(showPath);
                        }));
            }
            til.setCycleCount(Timeline.INDEFINITE);
            translationTimeLines.add(til);
        }
    }

    public void changeSpeed(double speed) {
//        speed *= 1/10;
        stopSimulation();
        if (speed > 0) this.speed = speed;
        System.out.println("Changing speed to " + this.speed);
        startSimulation();
    }

    public void reduceScaleBy(double scaleReduction) {
        if (bodies.isEmpty()){
            throw new IllegalStateException("No bodies");
        }
        this.scaleReduction = scaleReduction;
        double radiusBaseValue = 1.05;
        this.G = 6.66e-11;

        for (Body body : bodies) {
            body.setTranslateX(body.getTranslateX() / scaleReduction);
            body.setTranslateY(body.getTranslateY() / scaleReduction);
            body.setTranslateZ(body.getTranslateZ() / scaleReduction);
            double xFactor = 1 / Math.sqrt(scaleReduction);
            body.setxVelocity(body.getXVelocity() * xFactor);

            double exponent = 0;
            for (double i = body.getRadius(); i >= 10; i /= 10) {
                exponent += 10;
            }
//            exponent *= 2;
            System.out.println("================== exponent for " + body.getName() + " is " +exponent);
//            double newRadius = Math.pow(radiusBaseValue, exponent);
            double newRadius = body.getBaseRadius()/10e8;
            body.setBaseRadius(10 * newRadius);
            System.out.println("Scaled velocity is " + body.getXVelocity());
            System.out.println("    Scaled radius is " + body.getRadius());

        }
        this.G /= Math.pow(scaleReduction, 2);
    }

    public void orbit(Collection<Body> bodies, Body target) {
        if (G != -1) { //didn't set the scale
            throw new IllegalStateException("Cannot orbit after setting simulation scale");
        } else {
            this.G = 6.66e-11;
        }
        for (Body body : bodies) {
            double orbitalVelocity = Math.sqrt(G * target.getMass() / body.distanceTo(target));
            body.setxVelocity(orbitalVelocity);
            System.out.println("Orbital velocity is " + orbitalVelocity);
        }
    }


    public void setRadius(double radius){

    }

    public void setG(double value) { //todo remove
        this.G = value;
    }

    public double getG() {
        return G;
    }
}

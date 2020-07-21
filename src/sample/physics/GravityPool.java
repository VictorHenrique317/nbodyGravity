package sample.physics;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import sample.physics.models.Body;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class GravityPool {
    private final Body centralBody;
    private ArrayList<Body> bodies;
    private Collection<Timeline> timeLines;
    private double G = -1;

    public enum Types {Nbody, classic}
    private Types simulationType;

    private double speed;
    private double delay;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private boolean showPath;
    private double scaleReduction;

    public GravityPool(Types simulationType) {
        this(simulationType, null);
        if (simulationType == Types.classic){
            throw new IllegalArgumentException("Didn't set central body for \"classic\" simulation type");
        }
    }
    public GravityPool(Types simulationType, Body centralBody) {
        bodies = new ArrayList<>();
        timeLines = new ArrayList<>();
        this.speed = 1;
        this.centralBody = centralBody;
        this.simulationType = simulationType;
    }

    public void stopSimulation() {
        for (Timeline timeline : this.timeLines) {
            timeline.stop();
        }
        this.timeLines.clear();
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

    public void startSimulation(boolean showPath) {
        configureGravity(showPath, centralBody);
        executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            for (Timeline timeline : timeLines) {
                timeline.play();
            }
        });
    }

    private void configureGravity(boolean showPath, Body centralBody) {
        this.showPath = showPath;
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
            timeLines.add(til);
        }
    }

    public void changeSpeed(double speed) {
        speed *= scaleReduction / 50;
        stopSimulation();
        if (speed > 0) this.speed = speed;
        System.out.println("Changing speed to " + this.speed);
        startSimulation(showPath);
    }

    public void reduceScaleBy(double scaleReduction) {
        this.scaleReduction = scaleReduction;
        double radiusBaseValue = 2e3;
//        if (scaleReduction == 1){
//            this.G = 6.66e-11;
//            return;
//        }
        this.G = 6.66e-11;

        for (Body body : bodies) {
            body.setTranslateX(body.getTranslateX() / scaleReduction);
            body.setTranslateY(body.getTranslateY() / scaleReduction);
            body.setTranslateZ(body.getTranslateZ() / scaleReduction);
            double xFactor = 1 / Math.sqrt(scaleReduction);
            body.setxVelocity(body.getXVelocity() * xFactor);

            double exponent = 0;
            for (double i = body.getRadius(); i >= 10; i /= 10) {
                exponent++;
            }

            body.setRadius(Math.pow(radiusBaseValue, exponent / 4));
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

    public void setG(double value) { //todo remove
        this.G = value;
    }

    public double getG() {
        return G;
    }
}

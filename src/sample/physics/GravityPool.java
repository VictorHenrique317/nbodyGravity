package sample.physics;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.util.Duration;
import sample.physics.models.Body;
import sample.physics.models.Planet;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class GravityPool {
    private final Body centralBody;
    private ObservableList<Body> bodies;
    private Collection<Timeline> translationTimeLines;
    private double G = -1;
    private boolean flag = true;

    public enum Types {Nbody, classic}
    private Types simulationType;

    private double speed;
    private double delay;
    private ExecutorService threadPool = Executors.newCachedThreadPool();
    private boolean showPath;
    private double scaleReduction = 1;

    public GravityPool(Types simulationType, ObservableList<Body> bodyList) {
        this(simulationType, null, bodyList);
        if (simulationType == Types.classic){
            throw new IllegalArgumentException("Didn't set central body for \"classic\" simulation type");
        }
    }
    public GravityPool(Types simulationType, Body centralBody, ObservableList<Body> bodies) {
        translationTimeLines = new ArrayList<>();
        this.bodies = bodies;
        this.speed = 1;
        this.centralBody = centralBody;
        this.simulationType = simulationType;
    }

    public void stopSimulation() {
        for (Timeline timeline : this.translationTimeLines) {
            timeline.stop();
        }
        if (threadPool != null){
            System.out.println("stopping");
            this.threadPool.shutdownNow();
            this.threadPool = null;
        }
        this.translationTimeLines.clear();
    }

    public Collection<Body> getBodies() {
        return this.bodies;
    }

    public void startSimulation() {
        if (this.G == -1){
            throw new IllegalStateException("Started simulation without specifying scale reduction");
        }
        stopSimulation();
        threadPool = Executors.newCachedThreadPool();
        configureGravity(centralBody);
            for (Timeline timeline : translationTimeLines) {
                threadPool.execute(timeline::play);
//                timeline.play();
            }
            if (flag){
                flag = false;
                for (Body body: bodies){
                    if (body instanceof Planet){
                        System.out.println("starting rotation");
                        ((Planet) body).startRotation();
                    }
                }
            }
    }

    private void configureGravity(Body centralBody) {
        this.showPath = false;
        this.delay = 50;
        Timeline til;
        for (Body i : bodies) {
            if (simulationType == Types.Nbody) {
                System.out.println("new body");
                til = new Timeline(new KeyFrame(
                        Duration.millis(delay),
                        (e) -> {
                            i.beAttractedBy(bodies, delay * speed / 1000d, G);
                            i.showPath(showPath);
                        }));
            }else {
                System.out.println("wrong");
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
        stopSimulation();
        if (speed > 0) this.speed = speed;
        System.out.println("Changing speed to " + this.speed);
        startSimulation();
    }

    public void reduceScaleBy(double scaleReduction) {
        this.scaleReduction = scaleReduction;
        this.G = 6.66e-11;

        for (Body body : bodies) {
            body.setTranslateX(body.getTranslateX() / scaleReduction);
            body.setTranslateY(body.getTranslateY() / scaleReduction);
            body.setTranslateZ(body.getTranslateZ() / scaleReduction);
            double xFactor = 1 / Math.sqrt(scaleReduction);
            body.setxVelocity(body.getXVelocity() * xFactor);

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

    public double getG() {
        return G;
    }
}

package sample.ui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.Camera;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import sample.physics.models.Body;

import java.awt.event.MouseEvent;
import java.util.Objects;

public class CreationBox {
    @FXML
    private JFXButton createButton, deleteButton;
    @FXML
    private JFXTextField massField, radiusField, horizontalField, verticalField;

    @FXML
    private void onCreateButton() {
        Main.stopSimulation();
        double mass = Double.parseDouble(massField.getText());
        double radius = Double.parseDouble(radiusField.getText());
        double horizontalVelocity = Double.parseDouble(horizontalField.getText());
        double verticalVelocity = Double.parseDouble(verticalField.getText());

        Camera camera = Main.getCamera();
        Scene mainScene = Main.getMainScene();
        double x = camera.getTranslateX();
        double y = camera.getTranslateY();
        double z = camera.getTranslateZ() + 50;
        Image image = new Image(Objects.requireNonNull(
                getClass().getClassLoader().getResource("sphereIcon.png")).toExternalForm());
        Body body = new Body(radius, x, y, z, mass, "Custom Body", image);
        Main.addBody(body);
//        System.out.println("Camera x: " + camera.getTranslateX() + " / " + "Camera y: " + camera.getTranslateY());
//        System.out.println("Object x: " + body.getTranslateX() + " / " + "Object y: " + body.getTranslateY());
//        System.out.println("Body z: " + body.getTranslateZ());

        mainScene.setOnMouseMoved(mouseEvent -> {
            double cameraDistance = Main.getCamera().getTranslateZ();
            cameraDistance = cameraDistance < 0 ? cameraDistance * -1 : cameraDistance; // eliminates negative
            double deltaX = mouseEvent.getSceneX() - (mainScene.getWidth() / 2);
            double deltaY = mouseEvent.getSceneY() - (mainScene.getHeight() / 2);
            deltaX = deltaX / (mainScene.getWidth() / 2); // percentage of motion
            deltaY = deltaY / (mainScene.getHeight() / 2); // percentage of motion

            double backgroundLength = (cameraDistance * 2 / 3d) * Math.sqrt(3);
            double backgroundHeight = backgroundLength / Main.getLengthHeightRatio();

            body.setTranslateX(deltaX * (backgroundLength / 2));
            body.setTranslateY(deltaY * (backgroundHeight / 2));
        });
        mainScene.setOnMouseClicked(mouseEvent -> {
            mainScene.setOnMouseMoved(null);
            Main.startSimulation(body);
            mainScene.setOnMouseClicked(null);
        });
    }

    @FXML
    private void onDeleteButton() {

    }

}

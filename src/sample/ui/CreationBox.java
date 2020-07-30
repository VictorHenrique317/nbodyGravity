package sample.ui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.Camera;
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
    private void onCreateButton(){
        double mass = Integer.parseInt(massField.getText());
        double radius = Integer.parseInt(radiusField.getText());
        double horizontalVelocity = Integer.parseInt(horizontalField.getText());
        double verticalVelocity = Integer.parseInt(verticalField.getText());

        Camera camera = Main.getCamera();
        Scene mainScene = Main.getMainScene();
        double x = camera.getTranslateX();
        double y = camera.getTranslateY();
        double z = camera.getTranslateZ() + 50;
        Image image = new Image(Objects.requireNonNull(
                getClass().getClassLoader().getResource("sphereIcon.png")).toExternalForm());
        Body body = new Body(radius, x, y ,z, mass, "Custom Body", image);
        Main.addBody(body);
        System.out.println("Camera x: " + camera.getTranslateX() + " / " + "Camera y: " + camera.getTranslateY());
        System.out.println("Object x: " + body.getTranslateX() + " / " + "Object y: " + body.getTranslateY());
        System.out.println("Body z: " + body.getTranslateZ());


        new Thread(()->{
            try{
                Thread.sleep(3000);
                mainScene.setOnMouseMoved(mouseEvent -> {

                    double deltaX = mouseEvent.getSceneX() - (mainScene.getWidth()/2);
                    double deltaY = mouseEvent.getSceneY() - (mainScene.getHeight()/2);
                    deltaX = deltaX / (mainScene.getWidth()/2); // percentage of motion
                    deltaY = deltaY / (mainScene.getHeight()/2); // percentage of motion
                    double backgroundLength = 25;
//                    System.out.println(deltaX + deltaX/2);
//                    System.out.println("y: " + mouseEvent.getSceneY());
                    body.setTranslateX(deltaX * backgroundLength);
                    body.setTranslateY(deltaY * backgroundLength);
//                    body.setTranslateY((mouseEvent.getSceneY() - (mainScene.getHeight()/2)) / 50);
                });
                mainScene.setOnMouseClicked(mouseEvent -> mainScene.setOnMouseMoved(null));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }).start();



    }
    @FXML
    private void onDeleteButton(){

    }

}

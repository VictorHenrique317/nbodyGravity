package sample.ui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.Camera;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import sample.physics.models.Body;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CreationBox {
    private final List<JFXTextField> inputs = new ArrayList<>();
    @FXML
    private JFXButton createButton, deleteButton;
    @FXML
    private JFXTextField massField, radiusField, horizontalField, verticalField, xField, yField, zField, zVelocityField;

    public void initialize() {
        createButton.setDisable(true);
        inputs.addAll(List.of(massField, radiusField, horizontalField,
                verticalField, xField, yField, zField, zVelocityField));

    }

    public void enableDeleteButton(){
        deleteButton.setDisable(false);
    }

    public void disableDeleteButton(){
        deleteButton.setDisable(true);
    }
    @FXML
    private void onInputReleased() {
        try {
            if (Double.parseDouble(massField.getText()) <= 0 ||
                Double.parseDouble(radiusField.getText()) <= 0){ // invalid
                throw new NumberFormatException();
            }
            for (JFXTextField field : inputs) {
                Double.parseDouble(field.getText()); // valid input
            }
            createButton.setDisable(false); // all  inputs are valid
        } catch (NumberFormatException e) {
            createButton.setDisable(true);
        }
    }

    @FXML
    private void onCreateButton() {
        try {
            Main.stopSimulation();
            double mass = Double.parseDouble(massField.getText());
            double radius = Double.parseDouble(radiusField.getText());
            double horizontalVelocity = Double.parseDouble(horizontalField.getText());
            double verticalVelocity = Double.parseDouble(verticalField.getText());
            double zVelocity = Double.parseDouble(zVelocityField.getText());

            double x = Double.parseDouble(xField.getText());
            double y = Double.parseDouble(yField.getText());
            double z = Double.parseDouble(zField.getText());
            Image image = new Image(Objects.requireNonNull(
                    getClass().getClassLoader().getResource("sphereIcon.png")).toExternalForm());
            Body body = new Body(radius, x, y, z, mass, "Custom Body", image);
            body.setxVelocity(horizontalVelocity);
            body.setyVelocity(verticalVelocity);
            body.setzVelocity(zVelocity);
            Main.addBody(body);
        } catch (NumberFormatException e) {

        }
    }

    @FXML
    private void onDeleteButton() {
        Main.deleteLastObject();
    }
}

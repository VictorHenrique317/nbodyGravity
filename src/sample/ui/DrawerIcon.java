package sample.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import sample.physics.models.Body;


final class DrawerIcon extends HBox{
    DrawerIcon(Body body) {
        ImageView icon = new ImageView(body.getIcon());
        icon.setFitWidth(40);
        icon.setFitHeight(40);
        Label bodyName = new Label(body.getName());
        bodyName.setTextFill(Color.WHITE);
        bodyName.setFont(new Font(18));
        this.setOnMouseClicked((mouseEvent -> {
            Main.lockCameraAt(body);
        }));
        this.setSpacing(10);
        this.setPadding(new Insets(0,0,0,20));
        this.setAlignment(Pos.CENTER_LEFT);
        this.getChildren().add(icon);
        this.getChildren().add(bodyName);
    }

}

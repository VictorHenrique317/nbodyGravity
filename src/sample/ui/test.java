package sample.ui;

import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import sample.physics.models.Body;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.*;

public class test {
    public static void main(String[] args) {
        new Thread(()->{
            try{
                Thread.sleep(5000);
                int counter = 1;
                Robot bot = new Robot();
                while(counter < 1000){
                    bot.mousePress(InputEvent.BUTTON1_MASK);
                    bot.mouseRelease(InputEvent.BUTTON1_MASK);
                    counter++;
                }

            } catch (InterruptedException | AWTException e) {
                e.printStackTrace();
            }
        }).start();

    }
}




package sample.ui;

import javafx.fxml.FXML;

public class CustomOptions {

    @FXML
    private void onStart(){
        Main.startSimulation();
    }
    @FXML
    private void onStop(){
        Main.stopSimulation();
    }
}

package com.example.elcapitano;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class AddMatches_controller {


    @FXML
    private void handleButtonClick(ActionEvent event) {
        // Cast the source of the event to Button
        Button clickedButton = (Button) event.getSource();

        // Check if the button is already highlighted
        if (clickedButton.getStyle().contains("-fx-background-color: #4bdb6f;")) {
            // Button is not highlighted, add the highlight
            clickedButton.setStyle("-fx-background-color: #5764f7;");
        } else if (clickedButton.getStyle().contains("-fx-background-color: #5764f7;")) {
            // Button is highlighted, remove the highlight
            clickedButton.setStyle("-fx-background-color: #4bdb6f;");
        } else {
            // No style applied yet, set the initial blue highlight
            clickedButton.setStyle("-fx-background-color: #5764f7;");
        }
    }


}

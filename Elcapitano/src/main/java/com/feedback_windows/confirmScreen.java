// ConfirmScreen class
package com.feedback_windows;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class confirmScreen {
    private String pitchNo;
    private String dateOfReservation;
    private String nameOfPerson;
    private int noOfHours;
    private String startTime;

    public confirmScreen(String pitchNo, String dateOfReservation, String nameOfPerson, int noOfHours, String startTime) {
        this.pitchNo = pitchNo;
        this.dateOfReservation = dateOfReservation;
        this.nameOfPerson = nameOfPerson;
        this.noOfHours = noOfHours;
        this.startTime = startTime;
    }

    public boolean showConfirmationDialog() {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation");
        confirmationAlert.setHeaderText("Please confirm the reservation details");
        confirmationAlert.setContentText(
                "Pitch No: " + pitchNo + "\n" +
                        "Date: " + dateOfReservation + "\n" +
                        "Name: " + nameOfPerson + "\n" +
                        "Number of Hours: " + noOfHours + "\n" +
                        "Start Time: " + startTime
        );

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}

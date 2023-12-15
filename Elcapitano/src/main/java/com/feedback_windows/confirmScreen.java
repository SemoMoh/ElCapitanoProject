// ConfirmScreen class
package com.feedback_windows;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

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

    public static boolean confirmCancellation() {
        // Display a confirmation dialog
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Confirm Cancellation");
        alert.setContentText("Are you sure you want to cancel the reservation?");

        Optional<ButtonType> result = alert.showAndWait();

        // Check if the user clicked OK (resulting in a true confirmation) or Cancel
        return result.isPresent() && result.get() == ButtonType.OK;
    }


    private static int returnAmount; // New class variable to store return amount

    public static int showReturnMoneyConfirmation(int previousAmountPaid) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Confirmation");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter the amount to return:");

        // Set the default value to the previous amount paid
        dialog.getEditor().setText(String.valueOf(previousAmountPaid));

        // Show the dialog and wait for the user's response
        Optional<String> result = dialog.showAndWait();

        // Parse the user input to an integer if available
        return result.map(returnAmount -> {
            try {
                int parsedAmount = Integer.parseInt(returnAmount);

                // Check if the entered return amount is valid (not negative and not higher than the paid amount)
                if (parsedAmount >= 0 && parsedAmount <= previousAmountPaid) {
                    return parsedAmount;
                }
                else
                {
                    errorScreen.showAlert("خطأ في ادخال مبلغ الاسترداد" ,"لا يمكن للمبلغ المسترد تجاوز المدفوع مسبقا");
                }
            } catch (NumberFormatException ignored) {
                // Ignore parsing errors, and return 0 if parsing fails
            }

            // Return 0 if the user enters an invalid amount
            return 0;
        }).orElse(0);
    }


    public static int getReturnAmount() {
        return returnAmount;
    }

}

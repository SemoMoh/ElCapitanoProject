package com.example.elcapitano;

import com.elcapitano_system.ElcapitanoSystem;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.apache.poi.xdgf.usermodel.section.geometry.EllipticalArcTo;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.ResourceBundle;

public class AddMatches_controller implements Initializable {

    public Button searchButton;
    private ElcapitanoSystem databaseSystem;
    @FXML
    private DatePicker chooseDate;
    @FXML
    private Spinner noOfHours;
    @FXML
    private ChoiceBox<String> choosePitch;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        databaseSystem = new ElcapitanoSystem();
    }

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


    @FXML
    public void searchReservations(ActionEvent actionEvent) {
        LocalDate date = chooseDate.getValue();
        int dayOfMonth = date.getDayOfMonth();
        String selectedPitch = choosePitch.getValue();

        // Map the selected pitch to the parameter expected by the function
        String mappedPitch = mapToFunctionParameter(selectedPitch);

        // Format the date to MM-yyyy
        String formattedDate = date.format(DateTimeFormatter.ofPattern("MM-yyyy"));

        System.out.println(formattedDate + " " + dayOfMonth + " pitch: " + mappedPitch);
        // Call your method with the extracted values
        boolean[] reservationsOfThisDay = getReservationOfThisDay(formattedDate, dayOfMonth, mappedPitch);
        System.out.println(Arrays.toString(reservationsOfThisDay));
    }

    public boolean[] getReservationOfThisDay(String MMYYYY, int day, String pitchNo) {
        return databaseSystem.fieldSystem.getDayReservation(MMYYYY, day, pitchNo);
    }

    private String mapToFunctionParameter(String selectedPitch) {
        // Implement your mapping logic here
        // Example: map "ملعب 1" to "No.1", "ملعب 2-1" to "No.2", and so on
        switch (selectedPitch) {
            case "ملعب 1":
                return "No.1";
            case "ملعب 2-1":
                return "No.2";
            case "ملعب 2-2":
                return "No.3";
            case "ملعب 2-3":
                return "No.4";
            case "ملعب 2 9x9":
                return "No.5";
            // Add more cases as needed
            default:
                return selectedPitch; // If no mapping is found, return the original string
        }
    }


}
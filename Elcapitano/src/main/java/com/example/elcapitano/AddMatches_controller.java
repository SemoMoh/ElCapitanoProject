package com.example.elcapitano;

import com.backend.fields.Field;
import com.backend.fields.Reservation;
import com.elcapitano_system.ElcapitanoSystem;
import com.feedback_windows.confirmScreen;
import com.feedback_windows.errorScreen;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class AddMatches_controller implements Initializable {

    public Button searchButton;
    public Button confirmReservation;
    public TextField nameField;
    public TextField phoneField;
    public TextField piadAmount;
    public TextArea detailsField;
    private ElcapitanoSystem databaseSystem;
    @FXML
    private DatePicker chooseDate;
    @FXML
    private Spinner noOfHours;
    @FXML
    private ChoiceBox<String> choosePitch;

    // Declare the buttonList as a field
    private List<Button> buttonList;

    @FXML
    private Button button00, button01, button02, button03, button04, button05, button06, button07, button08, button09,
            button10, button11, button12, button13, button14, button15, button16, button17, button18, button19,
            button20, button21, button22, button23;

    private List<Button> selectedButtonList;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        databaseSystem = new ElcapitanoSystem();

        // Add a listener to the valueProperty of the ChoiceBox
        choosePitch.valueProperty().addListener((observable, oldValue, newValue) -> {
            // Call the searchReservations method when the choice is selected
            searchReservations(new ActionEvent());
        });

        // Initialize the buttonList
        buttonList = Arrays.asList(
                button00, button01, button02, button03, button04, button05, button06, button07, button08, button09,
                button10, button11, button12, button13, button14, button15, button16, button17, button18, button19,
                button20, button21, button22, button23
        );

        selectedButtonList = new ArrayList<>();

        chooseDate.setValue(LocalDate.now());

    }

    @FXML
    private void handleButtonClick(ActionEvent event) {
        // Cast the source of the event to Button
        Button clickedButton = (Button) event.getSource();

        // Check if the button is already highlighted
        if (clickedButton.getStyle().contains("-fx-background-color: red;")) {

            selectedButtonList.clear();
            System.out.println("No of selected : " + selectedButtonList.size());
            searchReservations(new ActionEvent());
            clickedButton.setStyle("-fx-background-color: orange;");
            // TODO: show resevation details and set spinner to related reservations number
            ShowDetailsOfReservation((String) ((Button) event.getSource()).getId());
            System.out.println((String) ((Button) event.getSource()).getId());

        } else if (clickedButton.getStyle().contains("-fx-background-color: orange;")) {

            clickedButton.setStyle("-fx-background-color: red;");
            updateSpinner(0);


        }
        if (clickedButton.getStyle().contains("-fx-background-color: #4bdb6f;")) {
            if (selectedButtonList.size() == 0) clearAllFieldsExceptChoosenButton();
            clearReservedIfSelected(buttonList);//for orange
            // Button is not highlighted, add the highlight
            clickedButton.setStyle("-fx-background-color: #5764f7;");
            selectedButtonList.add(clickedButton);
            updateSpinner(selectedButtonList.size());
            System.out.println("No of selected : " + selectedButtonList.size());

        } else if (clickedButton.getStyle().contains("-fx-background-color: #5764f7;")) {
            // Button is highlighted, remove the highlight
            clickedButton.setStyle("-fx-background-color: #4bdb6f;");
            selectedButtonList.remove(clickedButton);
            updateSpinner(selectedButtonList.size());
            System.out.println("No of selected : " + selectedButtonList.size());

        }
    }

    private void clearAllFieldsExceptChoosenButton() {
        nameField.clear();
        phoneField.clear();
        piadAmount.clear();
        detailsField.clear();
    }

    private void ShowDetailsOfReservation(String buttonId) {
        List<Object> dateList = getSelectedDate(buttonId);
        System.out.println(dateList);

        Reservation reservation = ElcapitanoSystem.fieldSystem.getReservationDetails(
                (String) dateList.get(2),    // Assuming getReservationDetails expects a String as the first parameter
                (String) dateList.get(0),    // Assuming getReservationDetails expects a String as the second parameter
                (int) dateList.get(1),       // Assuming getReservationDetails expects an int as the third parameter
                (int) dateList.get(3)     // Assuming getReservationDetails expects an int as the fourth parameter
        );
        System.out.println(reservation);
        showRelatedReservationButtons(reservation.date, reservation.day, reservation.hour, reservation.noHours);

        nameField.setText(reservation.name);
        phoneField.setText(reservation.mobile);
        piadAmount.setText(String.valueOf(reservation.paid));
        updateSpinner(reservation.noHours);
        detailsField.setText(reservation.description);

    }

    private void showRelatedReservationButtons(String date, int day, int hour, int noHours) {
        // get the date in this form "MM-yyyy" TODO
        String pageDate = null;
        String[] dateParts = pageDate.split("-");
        int pageDay = 0;
        Calendar pageCal = new Calendar.Builder().setDate(Integer.parseInt(dateParts[1]),
                Integer.parseInt(dateParts[0]) - 1, pageDay).build();
        //loop for the number of reservation hours
        for (int i = 0; i < noHours; i++) {
            dateParts = date.split("-");
            Calendar cal = new Calendar.Builder().setDate(Integer.parseInt(dateParts[1]),
                    Integer.parseInt(dateParts[0]) - 1, pageDay).build();
            //if it is the same day, set the needed buttons into their color.
            if (date.equals(pageDate)) {
                if (day == pageDay) {
                    //color your button using the value of hour. TODO

                } else if (day > pageDay) {
                    //that means it's in the next day, add indication. TODO

                } else {
                    // in the prev day, add indication. TODO
                }
            } else if (cal.compareTo(pageCal) < 0) {
                //in the prev day, add indication TODO
            }else{
                // in the next days, add indication TODO
            }

            //increment hour
            hour++;
            if (hour == 24) {
                hour = 0;
                String[] parts = Field.nextDayDate(date, day, 1);
                day = Integer.parseInt(parts[0]);
                date = parts[1];
            }
        }
    }


    private List<Object> getSelectedDate(String buttonId) {

        LocalDate date = chooseDate.getValue();
        int dayOfMonth = date.getDayOfMonth();
        String mappedPitch = mapToFunctionParameter(choosePitch.getValue());
        String formattedDate = date.format(DateTimeFormatter.ofPattern("MM-yyyy"));
        int hour = extractButtonNumber(buttonId);
        List<Object> dateDayPitchHour = Arrays.asList(formattedDate, dayOfMonth, mappedPitch, hour);
        return dateDayPitchHour;
    }


    public void clearReservedIfSelected(List<Button> ButtonList) {
        for (int i = 0; i < ButtonList.size(); i++) {
            if (buttonList.get(i).getStyle().contains("-fx-background-color: orange;")) {
                buttonList.get(i).setStyle("-fx-background-color: red;");
            }
        }
    }


    @FXML
    public void searchReservations(Event actionEvent) {
        selectedButtonList.clear();
        LocalDate date = chooseDate.getValue();
        int dayOfMonth = date.getDayOfMonth();
        String selectedPitch = choosePitch.getValue();

        System.out.println(selectedPitch + "  " + date);
        // Check if both date and pitch are selected
        if (date == null || selectedPitch == null || selectedPitch.equals("Select a pitch")) {
            System.out.println("Please choose both a date and a pitch.");
            return;
        }

        // Map the selected pitch to the parameter expected by the function
        String mappedPitch = mapToFunctionParameter(selectedPitch);

        // Format the date to MM-yyyy
        String formattedDate = date.format(DateTimeFormatter.ofPattern("MM-yyyy"));

        System.out.println(formattedDate + " " + dayOfMonth + " pitch: " + mappedPitch);
        // Call your method with the extracted values
        boolean[] reservationsOfThisDay = getReservationOfThisDay(formattedDate, dayOfMonth, mappedPitch);
        System.out.println(Arrays.toString(reservationsOfThisDay));
        updateButtonsOnSearch(reservationsOfThisDay);
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

    public void updateButtonsOnSearch(boolean[] dayReservations) {
        for (int i = 0; i < dayReservations.length; i++) {
            if (dayReservations[i]) {
                buttonList.get(i).setStyle("-fx-background-color: red;");
            } else {
                // Set the background color to the default color if the reservation is false
                buttonList.get(i).setStyle("-fx-background-color: #4bdb6f;");
            }
        }
    }


    public void confitmResetvations(ActionEvent actionEvent) {
        LocalDate date = chooseDate.getValue();
        int dayOfMonth = date.getDayOfMonth();
        String mappedPitch = mapToFunctionParameter(choosePitch.getValue());
        String formattedDate = date.format(DateTimeFormatter.ofPattern("MM-yyyy"));
        int amountPaid = 0;

        if (piadAmount.getText().isEmpty()) {
            errorScreen.showAlert("قيمة الحجز فارغة", " رجاء ادخال قيمة الحجز");
            return;
        } else {
            amountPaid = Integer.parseInt(piadAmount.getText());
        }

        int hour = -1;  // Initialize to an invalid value
        if (!selectedButtonList.isEmpty()) {
            hour = extractButtonNumber(selectedButtonList.get(0).getId());
        }

        if (isConsecutiveButtons(selectedButtonList)) {
            confirmScreen confirmScreen = new confirmScreen(mappedPitch, formattedDate, nameField.getText(), selectedButtonList.size(), String.valueOf(hour));
            if (confirmScreen.showConfirmationDialog()) {
                Boolean confirmed = ElcapitanoSystem.fieldSystem.addReservation(mappedPitch, formattedDate, dayOfMonth, hour, selectedButtonList.size(), amountPaid, nameField.getText(), phoneField.getText(), detailsField.getText());
                clearAllFields();
            }
        } else {
            errorScreen.showAlert("تحديد مواعيد غير صحيح", "يرجى اختيار اوقات متتالية");
        }
    }


    public static int extractButtonNumber(String buttonName) {
        // Remove non-numeric characters
        String numericPart = buttonName.replaceAll("[^\\d]", "");

        // Remove leading zeros if any
        numericPart = numericPart.replaceFirst("^0+", "");

        if (buttonName.equals("button00")) return 0; //Solving a hardcodded error when button00 is selected
        // Convert the string to an integer
        return Integer.parseInt(numericPart);
    }

    private void clearAllFields() {
        nameField.clear();
        phoneField.clear();
        piadAmount.clear();
        detailsField.clear();
        searchReservations(new ActionEvent());
        // You can add more fields to clear as needed
    }

    private void updateSpinner(int amount) {
        // Get the current value factory
        SpinnerValueFactory<Integer> valueFactory = noOfHours.getValueFactory();
        // Check if the valueFactory is null, and create a new one if it is
        if (valueFactory == null) {
            valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 0);
            noOfHours.setValueFactory(valueFactory);
        }

        // Set the new value
        valueFactory.setValue(amount);

        // Update the value factory
        noOfHours.setValueFactory(valueFactory);
    }

    private boolean isConsecutiveButtons(List<Button> buttons) {
        if (buttons.size() < 2) {
            return true;  // A single button is always considered consecutive
        }

        // Extract button numbers and sort them
        List<Integer> buttonNumbers = buttons.stream()
                .map(button -> extractButtonNumber(button.getId()))
                .sorted()
                .collect(Collectors.toList());

        // Check if the buttons are consecutive
        for (int i = 0; i < buttonNumbers.size() - 1; i++) {
            if (buttonNumbers.get(i + 1) - buttonNumbers.get(i) != 1) {
                return false;  // Buttons are not consecutive
            }
        }

        return true;  // Buttons are consecutive
    }

}
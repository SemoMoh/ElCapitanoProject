package com.example.elcapitano;

import com.backend.fields.Field;
import com.backend.fields.Reservation;
import com.elcapitano_system.ElcapitanoSystem;
import com.feedback_windows.confirmScreen;
import com.feedback_windows.errorScreen;
import com.opencsv.ResultSetHelperService;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.feedback_windows.confirmScreen.confirmCancellation;

public class EditMatch_controller implements Initializable {

    public Button searchButton;
    public Button confirmReservation;
    public TextField nameField;
    public TextField phoneField;
    public TextField piadAmount;
    public TextArea detailsField;
    public TextField hourPrice;
    public TextField totalAmount;
    public TextField remainingAmount;
    public Button cancelChoices;
    public TextField piadAmountNow;
    public Button cancelAndReturnMoney;
    public Button completeReservationButton;
    public Button cancelReservationButton;
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

        piadAmount.textProperty().addListener((observable, oldValue, newValue) -> {
            updatePriceFields();
        });

        initializeFields();

    }

    private void initializeFields() {
        int spinnerInitialValue = 0;
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, spinnerInitialValue);
        noOfHours.setValueFactory(valueFactory);

        // Initialize totalAmount to 0
        totalAmount.setText("0");

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
            ShowDetailsOfReservation((String) ((Button) event.getSource()).getId());
            System.out.println((String) ((Button) event.getSource()).getId());

        } else if (clickedButton.getStyle().contains("-fx-background-color: orange;")) {

            selectedButtonList.clear();
            System.out.println("No of selected : " + selectedButtonList.size());
            searchReservations(new ActionEvent());
            clickedButton.setStyle("-fx-background-color: orange;");
            ShowDetailsOfReservation((String) ((Button) event.getSource()).getId());
            System.out.println((String) ((Button) event.getSource()).getId());


        }//if green
        if (clickedButton.getStyle().contains("-fx-background-color: #4bdb6f;")) {
            if ((selectedButtonList.size() == 0) && isThereOrangeButon()) clearAllFieldsExceptChoosenButton();
            clearReservedIfSelected(buttonList);//for orange

            errorScreen.showAlert("اختيار غير صحيح","لا يمكن التعديل في معاد غير محجوز");
        }
    }

    private boolean isThereOrangeButon() {
        for (int i = 0; i < buttonList.size(); i++) {
            if (buttonList.get(i).getStyle().contains("-fx-background-color: orange;"))
                return true;
        }
        return false;
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
        showRelatedReservationButtons(reservation.date, reservation.day, reservation.hour, reservation.noHours, (String) dateList.get(0), (int) dateList.get(1));

        nameField.setText(reservation.name);
        phoneField.setText(reservation.mobile);
        piadAmount.setText(String.valueOf(reservation.paid));
        updateSpinner(reservation.noHours);
        detailsField.setText(reservation.description);

    }

    private void showRelatedReservationButtons(String date, int day, int hour, int noHours, String PageDate, int PageDay) {

        String pageDate = PageDate;
        String[] dateParts = pageDate.split("-");
        int pageDay = PageDay;
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
                    //color your button using the value of hour.
                    buttonList.get(hour).setStyle("-fx-background-color: orange;");
                    System.out.println("Hours to be updated to yellow" + hour);
                } else if (day > pageDay) {
                    //that means it's in the next day, add indication. TODO

                } else {
                    // in the prev day, add indication. TODO
                }
            } else if (cal.compareTo(pageCal) < 0) {
                //in the prev day, add indication TODO
            } else {
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
        if (mappedPitch.equals("No.5")) {
            hourPrice.setText("400");
        } else hourPrice.setText("250");

        // Format the date to MM-yyyy
        String formattedDate = date.format(DateTimeFormatter.ofPattern("MM-yyyy"));

        System.out.println(formattedDate + " " + dayOfMonth + " pitch: " + mappedPitch);
        // Call your method with the extracted values
        boolean[] reservationsOfThisDay = getReservationOfThisDay(formattedDate, dayOfMonth, mappedPitch);
        System.out.println(Arrays.toString(reservationsOfThisDay));
        updateButtonsOnSearch(reservationsOfThisDay);
    }


    @FXML
    private void updatePriceFields() {
        int selectedHours = selectedButtonList.size();
        int spinnerValue = (int) noOfHours.getValueFactory().getValue();

        // Ensure the spinner value is at least the number of selected hours
        spinnerValue = Math.max(spinnerValue, selectedHours);

        int hourPriceValue = Integer.parseInt(hourPrice.getText());
        int totalAmountValue = spinnerValue * hourPriceValue;

        // Check if the paid amount is empty
        if (piadAmount.getText().isEmpty()) {
            // If paid amount is not entered, set remaining amount equal to total amount
            remainingAmount.setText(String.valueOf(totalAmountValue));
        } else {
            int remainingAmountValue = totalAmountValue - Integer.parseInt(piadAmount.getText());
            remainingAmount.setText(String.valueOf(Math.max(0, remainingAmountValue)));
        }

        totalAmount.setText(String.valueOf(totalAmountValue));
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


        // Check if User choosed a pitch
        String mappedPitch;
        if (checkIfChoosePitch()) {
            mappedPitch = mapToFunctionParameter(choosePitch.getValue());
        } else {
            errorScreen.showAlert("تحديد غير صحيح", "يرجي اختيار ملعب");
            return;
        }


        String formattedDate = date.format(DateTimeFormatter.ofPattern("MM-yyyy"));
        int amountPaid = 0;


        int totalAmountValue = Integer.parseInt(totalAmount.getText());
        int paidAmountValue = piadAmount.getText().isEmpty() ? 0 : Integer.parseInt(piadAmount.getText());


        // if condition at line 371 does the same , initially it was if the paid more the total
 /*       if ((paidAmountValue > totalAmountValue) && selectedButtonList.isEmpty()) {
            errorScreen.showAlert("تحديد مواعيد غير صحيح", "رجاء اختيار بداية الحجز");
            return;
        }
*/
        if (piadAmount.getText().isEmpty()) {
            errorScreen.showAlert("قيمة الحجز فارغة", " رجاء ادخال قيمة الحجز");
            return;
        } else {
            amountPaid = Integer.parseInt(piadAmount.getText());
        }

        int hour = -1;  // Initialize to an invalid value
        if (!selectedButtonList.isEmpty()) {
            // Find the minimum hour from the selectedButtonList
            hour = selectedButtonList.stream()
                    .map(button -> extractButtonNumber(button.getId()))
                    .min(Integer::compare)
                    .orElse(-1);


            //check if the hour and date has already passed
            // Check if the selected date and time have already passed
            if (!isDateTimeInFuture(date, hour)) {
                errorScreen.showAlert("تحديد مواعيد غير صحيح", "لا يمكن حجز في تاريخ أو وقت قديم");
                return;
            }
        } else {
            errorScreen.showAlert("تحديد مواعيد غير صحيح", "رجاء اختيار مواعيد الحجز");
            return;
        }

        int hoursNoToBeConfirmed = Math.max((int) noOfHours.getValueFactory().getValue(), selectedButtonList.size());

        confirmScreen confirmScreen = new confirmScreen(mappedPitch, formattedDate, nameField.getText(), hoursNoToBeConfirmed, String.valueOf(hour));
        if (isConsecutiveButtons(selectedButtonList)) {

            if (confirmScreen.showConfirmationDialog()) {
                Boolean confirmed = ElcapitanoSystem.fieldSystem.addReservation(mappedPitch, formattedDate, dayOfMonth, hour, hoursNoToBeConfirmed, amountPaid, nameField.getText(), phoneField.getText(), detailsField.getText());
                if (confirmed) {
                    clearAllFields();
                    updateSpinner(0);
                } else {
                    errorScreen.showAlert("تحديد مواعيد غير صحيح", "احد المواعيد غير متاحة");
                }
            }
        } else if (!isConsecutiveButtons(selectedButtonList)) {
            errorScreen.showAlert("تحديد مواعيد غير صحيح", "يرجى اختيار اوقات متتالية");
        }

    }

    private boolean isDateTimeInFuture(LocalDate selectedDate, int selectedHour) {
        // Combine the selected date and time into a LocalDateTime object
        LocalDateTime selectedDateTime = LocalDateTime.of(selectedDate, LocalTime.of(selectedHour, 0));

        // Get the current date and time
        LocalDateTime currentDateTime = LocalDateTime.now();

        // Check if the selected date and time are in the future
        return selectedDateTime.isAfter(currentDateTime);
    }


    private boolean checkIfChoosePitch() {

        if (choosePitch.getValue() != null) {
            return true;
        }
        return false;

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
        remainingAmount.clear();
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

        // If the spinner is decremented, reset the selection of the farthest selected button
        if (amount < selectedButtonList.size()) {
            int farthestSelectedIndex = selectedButtonList.size() - 1;
            for (int i = selectedButtonList.size() - 1; i >= amount; i--) {
                Button farthestSelectedButton = selectedButtonList.get(i);
                farthestSelectedButton.setStyle("-fx-background-color: #4bdb6f;");
                selectedButtonList.remove(farthestSelectedButton);
            }
        }

        // Update the value factory
        noOfHours.setValueFactory(valueFactory);
        updatePriceFields();
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


    public void cancelEverythingAndInitialize(ActionEvent actionEvent) {
        searchReservations(new ActionEvent());
        clearAllFields();
        initializeFields();
    }

    {
    }

    public void cancelReservationsAndReturnMoneyBack(ActionEvent actionEvent) {
        String mappedPitch;
        if (checkIfChoosePitch()) {
            mappedPitch = mapToFunctionParameter(choosePitch.getValue());
        } else {
            errorScreen.showAlert("تحديد غير صحيح", "يرجي اختيار ملعب");
            return;
        }

        Reservation reservationToBeCanceled = getDetailsOfReservation(getLeastHourOfOrangeButton());

        if (reservationToBeCanceled == null) return;

        // Confirmation dialog for returning money
        int previousAmountPaid = Integer.parseInt(piadAmount.getText());
        int returnAmount = confirmScreen.showReturnMoneyConfirmation(previousAmountPaid);

        // Check if the return amount is valid
        if (returnAmount > previousAmountPaid) {
            errorScreen.showAlert("مبلغ غير صحيح", "لا يمكن لمبلغ الاسترجاع تجاوز المدفوع");
            return;
        }

        // Backend method for canceling reservation and returning money
        Boolean checkIfRemoved = ElcapitanoSystem.fieldSystem.deleteReservation(
                mappedPitch,
                reservationToBeCanceled.date,
                reservationToBeCanceled.day,
                reservationToBeCanceled.hour,
                returnAmount
        );
            if (checkIfRemoved) {
                // Update UI or perform any necessary actions
                clearAllFields();
                updateSpinner(0);
            } else {
                errorScreen.showAlert("غطأ في العملية", "لا يمكن الغاء الحجز واسترجاع المبلغ");
            }
        }




    public void completeReservation(ActionEvent actionEvent) {

    }


    // TODO: يرجي اخيار ميعاد للاغاء
    public void cancelReservations(ActionEvent actionEvent) {
        String mappedPitch;
        if (checkIfChoosePitch()) {
            mappedPitch = mapToFunctionParameter(choosePitch.getValue());
        } else {
            errorScreen.showAlert("تحديد غير صحيح", "يرجي اختيار ملعب");
            return;
        }

        String leastHourButtonId = getLeastHourOfOrangeButton();
        if (leastHourButtonId.isEmpty()) {
            // No orange buttons selected
            return;
        }

        Reservation reservationToBeCanceled = getDetailsOfReservation(leastHourButtonId);

        if (reservationToBeCanceled == null) {
            // Invalid reservation details
            return;
        }

        // Ask for confirmation before canceling
        if (confirmCancellation()) {
            // Attempt to cancel the reservation
            boolean checkIfRemoved = ElcapitanoSystem.fieldSystem.deleteReservation(
                    mappedPitch,
                    reservationToBeCanceled.date,
                    reservationToBeCanceled.day,
                    reservationToBeCanceled.hour,
                    0
            );

            if (checkIfRemoved) {
                clearAllFields();
                updateSpinner(0);
            } else {
                errorScreen.showAlert("غطأ في العملية", "لا يمكن الغاء الحجز");
            }
        }
    }



    private Reservation getDetailsOfReservation(String buttonId) {

        if (buttonId.equals("")) {errorScreen.showAlert("تحديد مواعيد غير صحيح","يرجي اختيار ميعاد الحجز للاغاء"); return null;}
        List<Object> dateList = getSelectedDate(buttonId);
        System.out.println(dateList);

        Reservation reservation = ElcapitanoSystem.fieldSystem.getReservationDetails(
                (String) dateList.get(2),    // Assuming getReservationDetails expects a String as the first parameter
                (String) dateList.get(0),    // Assuming getReservationDetails expects a String as the second parameter
                (int) dateList.get(1),       // Assuming getReservationDetails expects an int as the third parameter
                (int) dateList.get(3)     // Assuming getReservationDetails expects an int as the fourth parameter
        );
        System.out.println(reservation);

        return reservation;
    }



    private String getLeastHourOfOrangeButton() {
        // Filter orange buttons
        List<Button> orangeButtons = buttonList.stream()
                .filter(button -> button.getStyle().contains("-fx-background-color: orange;"))
                .toList();

        // Find the button with the least hour among the orange buttons
        Button leastHourButton = orangeButtons.stream()
                .min(Comparator.comparingInt(button -> extractButtonNumber(button.getId())))
                .orElse(null);

        // Return the buttonId or an empty string if no orange buttons are found
        return (leastHourButton != null) ? leastHourButton.getId() : "";
    }



}

package com.example.elcapitano;

import com.feedback_windows.errorScreen;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.print.PrinterJob;
import javafx.scene.control.*;
import javafx.util.Callback;

public class Incomes_controller {
    ObservableList<String[]> data;

    @FXML
    private TextField incomeName;

    @FXML
    private TextField incomeCash;

    @FXML
    private DatePicker date;

    @FXML
    private TextField description;

    @FXML
    private TableView<String[]> incomeTable;

    @FXML
    private TableColumn<String[], String> incomeColumn;

    @FXML
    private TableColumn<String[], String> cashColumn;

    @FXML
    private TableColumn<String[], String> dateColumn;

    @FXML
    private TableColumn<String[], String> desColumn;

    @FXML
    private void initialize() {
        incomeColumn.setCellValueFactory(createColumnValueFactory(0));
        cashColumn.setCellValueFactory(createColumnValueFactory(1));
        dateColumn.setCellValueFactory(createColumnValueFactory(2));
        desColumn.setCellValueFactory(createColumnValueFactory(3));
        data = FXCollections.observableArrayList();
        incomeTable.setItems(data);
        incomeTable.setEditable(true);
    }

    @FXML
    public void saveData() {
        String name = incomeName.getText();
        String cash = incomeCash.getText();
        String day = date.getEditor().getText();
        String des = description.getText();

        if(!name.equals("") && !cash.equals("") && !day.equals("") && !des.equals("")) {
            addData(name, cash, day, des);
            System.out.println(convertTableToString());
            incomeName.clear();
            incomeCash.clear();
            description.clear();
        } else {
            errorScreen.showAlert("Not Valid Input", "Please enter a valid input");
        }
    }

    @FXML
    public void printTableContent() {
        PrinterJob printerJob = PrinterJob.createPrinterJob();
        if (printerJob != null && printerJob.showPrintDialog(incomeTable.getScene().getWindow())) {
            if (printerJob.printPage(incomeTable)) {
                printerJob.endJob();
            }
        }
    }

    @FXML
    public void deleteLastAccount() {
        if (data.size() > 0) {
            data.removeLast();
        }
    }

    private void addData(String value1, String value2, String value3, String value4) {
        data.add(new String[]{value1, value2, value3, value4});
    }


    private Callback<TableColumn.CellDataFeatures<String[], String>, ObservableValue<String>> createColumnValueFactory(int columnIndex) {
        return param -> {
            String[] entry = param.getValue();
            return new SimpleStringProperty(entry != null && entry.length > columnIndex ? entry[columnIndex] : null);
        };
    }

    private String convertTableToString() {
        StringBuilder sb = new StringBuilder();
        for (String[] row : data) {
            for (String cell : row) {
                sb.append(cell).append(", ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

}


package com.example.elcapitano;

import com.elcapitano_system.DevsLogin;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ForDevelopers {

    public TextField devusername;
    public TextField devPassword;
    public TextField pathtoDatabase;
    public Button confirmDatabase;

    private String successPathtoDatabase;

    public void confirmDatabase(ActionEvent actionEvent) {
        DevsLogin d = HelloApplication.d;
        String username, password, path;

        // get strings from text fields
        username = devusername.getText();
        password = devPassword.getText();
        path = pathtoDatabase.getText();

        boolean successLogin = d.newLogin(username, password, path);

        if (successLogin) {
            successPathtoDatabase = path;

            // Close the stage if login is successful
            closeStage(actionEvent);
        } else {
            // Show an alert for incorrect credentials
            showAlert("Wrong Developer Credentials", "The provided credentials are incorrect. Please try again.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void closeStage(ActionEvent event) {
        // Get the source Node (Button) and its associated Stage
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();

        // Close the stage
        stage.close();
    }
}

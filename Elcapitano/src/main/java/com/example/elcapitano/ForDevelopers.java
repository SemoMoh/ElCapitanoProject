package com.example.elcapitano;

import com.elcapitano_system.DevsLogin;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class ForDevelopers {

    public TextField devusername;
    public TextField devPassword;
    public TextField pathtoDatabase;
    public Button confirmDatabase;

    private String successPathtoDatabase;

    public void confirmDatabase(ActionEvent actionEvent) throws IOException {
        DevsLogin d = HelloApplication.d;
        String username, password, path;

        // get strings from text fields
        username = devusername.getText();
        password = devPassword.getText();
        path = pathtoDatabase.getText();

        boolean successLogin = d.newLogin(username, password, path);

        if (successLogin) {
            successPathtoDatabase = path;

            // For a successful login, open the accounts log in and continue the app as normal.
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
            fxmlLoader.setControllerFactory(c -> new HelloController());
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            Stage stage = new Stage();
            stage.setTitle("Login!");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
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
}

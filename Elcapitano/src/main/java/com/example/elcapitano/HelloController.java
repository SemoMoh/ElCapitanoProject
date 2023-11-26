package com.example.elcapitano;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class HelloController {
/*
    @FXML
    private ImageView imageView;

    @FXML
    private void initialize() {
        Image image = new Image(getClass().getClassLoader().getResourceAsStream("/assets/test_Logo.png"));
        imageView.setImage(image);
    }
*/

    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }


    /* PASSWORD FIELDS PART IN LOGIN SCREEN */

    @FXML
    private PasswordField passwordField;

    @FXML
    private CheckBox showPasswordCheckBox;

    @FXML
    private void updateDisplay() {
        if (showPasswordCheckBox.isSelected()) {
            passwordField.setPromptText(passwordField.getText());
            passwordField.setText("");
            passwordField.setDisable(true);
        } else {
            passwordField.setDisable(false);
            passwordField.setText(passwordField.getPromptText());
            /*passwordField.setPromptText("");*/
        }
    }
    /* END OF  PASSWORD FIELDS PART IN LOGIN SCREEN  */


}
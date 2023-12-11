package com.example.elcapitano;

import com.backend.accounts.AccountDB;
import com.elcapitano_system.ElcapitanoSystem;
import com.elcapitano_system.User;
import com.feedback_windows.errorScreen;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloController extends HelloApplication {
    public TextField usernameField;

/*
    @FXML
    private ImageView imageView;

    @FXML
    private void initialize() {
        Image image = new Image(getClass().getClassLoader().getResourceAsStream("/assets/test_Logo.png"));
        imageView.setImage(image);
    }
*/




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


    public Button loginButton;

    @FXML
    private void checkPassword() throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();
        boolean correctAccount = false;
        try {
            correctAccount = AccountDB.checkAccount(username, password);
        } catch (IOException e) {
            errorScreen.showAlert("Accounts Database not found exception.",
                    "Please, return to the developments.");
        }

        if (correctAccount) {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("main_page.fxml"));
            fxmlLoader.setControllerFactory(c -> new HelloController());
            Stage newStage = new Stage();
            Scene scene = new Scene(fxmlLoader.load(), 1920, 800);
            newStage.setTitle("Elcapitano");
            // newStage.setResizable(false);
            newStage.setScene(scene);
            newStage.setMaximized(true);
            //newStage.setFullScreen(true);
            newStage.show();

            // initialize the user.
            ElcapitanoSystem.user = new User();
            ElcapitanoSystem.user.username = username;
            ElcapitanoSystem.user.password = password;
            ElcapitanoSystem.user.isAdmin = AccountDB.getAccountType(username, password);
            System.out.println(ElcapitanoSystem.user.isAdmin);
            // Close the current login stage
            Stage currentStage = (Stage) loginButton.getScene().getWindow();
            currentStage.close();
        } else {

            new errorScreen().showAlert("Invalid Login", "Invalid username or password");
        }
    }

    @FXML
    private StackPane contentContainer;

    @FXML
    private void addMatches() {
        loadPage("addMatch.fxml");
    }

    @FXML
    private void editMatch() {
        loadPage("editMatch.fxml");
    }

    @FXML
    private void incomes() {
        loadPage("incomes.fxml");
    }

    @FXML
    private void expenses() {
        loadPage("expenses.fxml");
    }

    @FXML
    private void addBall() {
        loadPage("addBall.fxml");
    }

    @FXML
    private void garage() {
        loadPage("garage.fxml");
    }

    @FXML
    private void reports() {
        loadPage("reports.fxml");
    }

    @FXML
    private void settings() {
        loadPage("settings.fxml");
    }


    // Add more methods for other pages

    private void loadPage(String pageFXML) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(pageFXML));
            StackPane page = fxmlLoader.load();
            contentContainer.getChildren().setAll(page);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}



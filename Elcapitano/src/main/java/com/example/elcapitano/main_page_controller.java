package com.example.elcapitano;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class main_page_controller extends HelloApplication implements Initializable {



    @FXML
    private StackPane contentContainer;
    private URL location;
    private ResourceBundle resources;

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
    private void settings() { loadPage("settings.fxml");}

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load the "addMatches" page when the main page is initialized
        loadPage("addMatch.fxml");
    }


}
package com.example.elcapitano;

import com.elcapitano_system.DevsLogin;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;


import java.io.IOException;

public class HelloApplication extends Application {
    public static DevsLogin d;

    @Override
    public void start(Stage stage) throws IOException {
        // check device
        d = new DevsLogin();
        if(!d.checkDevice()){
            // open dev login.
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("forDevelopers.fxml"));
            ForDevelopers devController = new ForDevelopers();
            fxmlLoader.setControllerFactory(c -> devController);
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            stage.setTitle("Login!");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } else{
       /* this.newStage =stage;*/
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
            fxmlLoader.setControllerFactory(c -> new HelloController());
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            stage.setTitle("Login!");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        }
    }

    public static void main(String[] args) {
        launch();
    }
    /*
    protected  Stage newStage;
    */

}
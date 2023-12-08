package com.example.elcapitano;

import com.elcapitano_system.DevsLogin;
import com.elcapitano_system.ElcapitanoSystem;
import com.elcapitano_system.SystemInit;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    public static DevsLogin d;
    public static String DB_path; // without the end backslash
    public static ElcapitanoSystem system;
    public static Application app;

    @Override
    public void start(Stage stage) throws IOException {
        // to call this function from anywhere.
        app = this;
        // check device
        d = new DevsLogin();
        if (!d.checkDevice()) {
            // open dev login.
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("forDevelopers.fxml"));
            ForDevelopers devController = new ForDevelopers();
            fxmlLoader.setControllerFactory(c -> devController);
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            stage.setTitle("Login!");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } else {
            DB_path = DevsLogin.pathToDB + SystemInit.DB_name;

            //System initialization.
            system = new ElcapitanoSystem();

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
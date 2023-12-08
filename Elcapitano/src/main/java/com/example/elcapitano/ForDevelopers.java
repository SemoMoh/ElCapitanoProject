package com.example.elcapitano;

import com.elcapitano_system.DevsLogin;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class ForDevelopers {

    public TextField devusername;
    public TextField devPassword;
    public TextField pathtoDatabase;
    public Button confirmDatabase;
    private String pathtoDatabse;
    
    

    public String getPathtoDatabse() {
        return pathtoDatabse;
    }

    public void confirmDatabase(ActionEvent actionEvent) {
        DevsLogin d = HelloApplication.d;
        String username,password,path;
        // get strings from text fields

        boolean successLogin = d.newLogin(username,password,path);
        
    }
}

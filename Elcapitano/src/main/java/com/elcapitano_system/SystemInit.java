package com.elcapitano_system;

import com.backend.accounts.AccountDB;
import com.example.elcapitano.HelloApplication;

import java.io.File;
import java.io.IOException;

public class SystemInit {
    public static final String DB_name = "Capitano Database/";

    /**
     * Creates the folders of the DB.
     */
    public static void initializeDB() {
        String path = HelloApplication.DB_path + DB_name;
        File dir = new File(path);
        if (dir.exists()) {
            throw new RuntimeException("Can't overwrite an existing directory");
        }
        dir.mkdir();

        dir = new File(path + "Fields");
        dir.mkdir();

        dir = new File(path + "Fields/No.1");
        dir.mkdir();
        dir = new File(path + "Fields/No.2");
        dir.mkdir();
        dir = new File(path + "Fields/No.3");
        dir.mkdir();
        dir = new File(path + "Fields/No.4");
        dir.mkdir();

        dir = new File(path + "Reports");
        dir.mkdir();
        //init accounts with one account added.
        AccountDB.dbPath = path;
        try {
            AccountDB.createDB();
        } catch (IOException e) {
            throw new RuntimeException("Can't create accounts database");
        }
    }

    public static void main(String[] args) {
        initializeDB();
    }
}

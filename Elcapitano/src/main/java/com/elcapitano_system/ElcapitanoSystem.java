package com.elcapitano_system;

import com.backend.accounts.AccountDB;
import com.backend.accounts.ExpensesSheet;
import com.backend.accounts.IncomeSheet;
import com.backend.accounts.Report;
import com.backend.fields.FieldSystem;
import com.example.elcapitano.HelloApplication;

import java.io.IOException;


public class ElcapitanoSystem {
    public static AccountDB accountDB;
    public static FieldSystem fieldSystem;
    public static IncomeSheet incomeSheet;
    public static ExpensesSheet expensesSheet;
    public static Report report;
    public static User user;

    public static void SystemLoad() throws IOException {
        {
            fieldSystem = new FieldSystem(400, 250);
            // initialize the accounts.
            AccountDB.dbPath = HelloApplication.DB_path;
            accountDB = AccountDB.getInstance();
        }
    }
}
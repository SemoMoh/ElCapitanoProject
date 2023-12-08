package com.elcapitano_system;

import com.backend.accounts.AccountDB;
import com.backend.accounts.ExpensesSheet;
import com.backend.accounts.IncomeSheet;
import com.backend.accounts.Report;
import java.io.IOException;


public class ElcapitanoSystem {
    public static AccountDB accountDB;
    public static IncomeSheet incomeSheet;
    public static ExpensesSheet expensesSheet;
    public static Report report;

    public static void SystemLoad() throws IOException {

        // initialize the accounts.
        accountDB = AccountDB.getInstance();
    }
}

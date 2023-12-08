package com.elcapitano_system;

import com.backend.accounts.AccountDB;

public class ElcapitanoSystem {
    public static AccountDB accountDB;

    public static void SystemLoad(){
        // initialize the accounts.
        accountDB = AccountDB.getInstance();

    }
}

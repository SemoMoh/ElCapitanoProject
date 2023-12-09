package com.elcapitano_system;

import com.backend.accounts.AccountDB;
import com.backend.fields.FieldSystem;

public class ElcapitanoSystem {
    public static AccountDB accountDB;
    public static FieldSystem fieldSystem;
    public static void SystemLoad(){
        fieldSystem = new FieldSystem(400,250);
        // initialize the accounts.
        accountDB = AccountDB.getInstance();

    }
}

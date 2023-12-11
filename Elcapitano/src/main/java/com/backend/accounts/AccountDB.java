package com.backend.accounts;

import com.elcapitano_system.SystemInit;
import com.example.elcapitano.HelloApplication;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class AccountDB {
    private static final String DB_NAME = "accountDB.csv";
    public static ArrayList<String> accounts;
    private static volatile AccountDB accountDB;
    public static String dbPath = "";


    private AccountDB(String d) {
        dbPath = d;
        accounts = new ArrayList<>();
    }

    /**
     * مش عارف عملتها ليه بس كان شكلها فكرة كويسة في الأول
     *
     * @return
     */
    public static AccountDB getInstance() {
        if (accountDB == null) {
            synchronized (AccountDB.class) {
                if (accountDB == null) {
                    accountDB = new AccountDB(dbPath);
                }
            }
        }
        return accountDB;
    }


    /**
     * Sets the path to the database
     *
     * @param path the path to the database file
     */
    public synchronized static void setPath(String path) {
        dbPath = path;
    }

    /**
     * Creates file for database if not already created
     *
     * @return true if database already exists
     * false otherwise
     * @throws IOException
     */
    public static boolean createDB() throws IOException {
        File file = new File(dbPath + DB_NAME);
        if (!file.exists()) {
            boolean isCreated = file.createNewFile();
            if (isCreated) {
                addAccount("user", "admin", "TRUE", "admin", "0");
                write();
            }
            return isCreated;
        }
        return false;
    }

    /**
     * Loads the accounts from the database to accounts list
     *
     * @return accounts loaded
     * @throws IOException
     */
    public static ArrayList<String> loadAccounts() throws IOException {
        FileReader reader = new FileReader(dbPath + DB_NAME);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line;
        if (accounts.size() == 0) {
            while ((line = bufferedReader.readLine()) != null) {
                accounts.add(line);
            }
        }
        return accounts;
    }

    /**
     * Adds a new account to the list of accounts and modifies the database
     *
     * @param accountName The name of the account
     * @param password    The password of the account
     * @param accountType The type of account
     * @param jobTitle    The title of the job of the account
     * @param mobile      The mobile number of the account
     * @throws IOException
     */

    public static void addAccount(String accountName, String password, String accountType
            , String jobTitle, String mobile) throws IOException {
        String account = accountName + "," + password + "," + accountType + "," + jobTitle + "," + mobile;
        loadAccounts();
        accounts.add(account);
        write();
    }

    /**
     * Find the account by username
     *
     * @param username the username to search
     * @return index of the account in the database if found and -1 if not found
     */
    public static int find(String username) {
        for (int i = 0; i < accounts.size(); i++) {
            String[] s = accounts.get(i).split(",");
            if (s[0].equals(username)) {
                return i;
            }
        }
        return -1;
    }


    /**
     * Removes an account from the database & list of accounts
     *
     * @param accountName the name of the account
     * @return true if the account was removed
     * & false otherwise
     * @throws IOException
     */
    public static boolean removeAccount(String accountName) throws IOException {
        int i = find(accountName);
        if (i != -1) {
            accounts.remove(i);
            write();
            return true;
        }
        return false;
    }

    /**
     * Modifies an existing account in the database
     *
     * @param username     the username to find the account
     * @param index        the cell index you want to modify
     * @param modification the new modification
     * @return true if successfully modified the account
     * & false otherwise
     * @throws IOException
     */
    public static boolean modify(String username, int index, String modification) throws IOException {
        int i = find(username);
        if (i != -1) {
            String[] attributes = accounts.get(i).split(",");
            attributes[index] = modification;
            String row = attributes[0] + ","
                    + attributes[1] + ","
                    + attributes[2] + ","
                    + attributes[3] + ","
                    + attributes[4];
            ArrayList<String> list = new ArrayList<String>();
            for (int j = 0; j < accounts.size(); j++) {
                if (j == i) {
                    list.add(row);
                } else {
                    list.add(accounts.get(j));
                }
            }
            accounts = list;
            write();
            return true;
        }
        return false;
    }

    public static boolean checkLogin(String username, String password) throws IOException {
        loadAccounts();
        for (String account : accounts) {
            String[] data = account.split(",");
            if (data[0].equals(username) && data[1].equals(password)) {
                return true;
            }
        }
        return false;
    }

    public static boolean getAccountType(String username, String password) throws IOException {
        loadAccounts();
        for (String account : accounts) {
            String[] data = account.split(",");
            if (data[0].equals(username) && data[3].equals(password)) {
                return Boolean.parseBoolean(data[2]);
            }
        }
        return false;
    }


    private static void write() throws IOException {
        File file = new File(dbPath + DB_NAME);
        FileOutputStream fos = new FileOutputStream(file);

        StringBuilder sb = new StringBuilder();
        for (String account : accounts) {
            sb.append(account).append("\n");
        }

        fos.write(sb.toString().getBytes(StandardCharsets.UTF_8));
        fos.write("".getBytes(StandardCharsets.UTF_8));
    }

    private static void empty() throws IOException {
        File file = new File(dbPath + DB_NAME);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write("".getBytes(StandardCharsets.UTF_8));
    }

    public static boolean checkAccount(String username, String password) throws IOException {
        // TODO: Bakr, implement this method..
        boolean result = false;

        result = checkLogin(username, password);

        return result;
    }
}

package com.elcapitano_system;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class DevsLogin {
    // an absolute path starting from the path of this program.
    public final String Dev_DB_File = "Elcapitano/data/DevLogin.txt";
    public static String pathToDB;

    public DevsLogin() {
    }

    public boolean checkDevice() {
        //First load the data from the Dev_DB_File
        User user = readDB();
        if (user.getMac() == null) {
            // that means we require the user to contact us the devs for login.
            return false;
        }
        //if the MAC address is available, compare it with the one obtained form the device.
        String deviceMAC = encrypt(getMACAddress());
        return deviceMAC.equals(encrypt(user.getMac()));
    }

    public boolean newLogin(String username, String password, String databasePath) {
        username = encrypt(username);
        password = encrypt(password);
        User user = readDB();
        if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
            user.setMac(getMACAddress());
            pathToDB = databasePath;
            user.setPathToDB(pathToDB);
            saveDB(user);
            return true;
        }
        return false;
        //close the app.
    }

    private void saveDB(User user) {
        File file = new File(Dev_DB_File);
        FileWriter writer;
        try {
            writer = new FileWriter(file);
            writer.write(user.getUsername() + "\n");
            writer.write(user.getPassword() + "\n");
            writer.write(user.getMac() + "\n");
            writer.write(user.getPathToDB());
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getMACAddress() {
        StringBuilder macAddress = new StringBuilder();
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            NetworkInterface ni = NetworkInterface.getByInetAddress(localHost);
            byte[] hardwareAddress = ni.getHardwareAddress();
            macAddress = new StringBuilder();
            for (byte b : hardwareAddress) {
                macAddress.append(String.format("%02X", b));
                macAddress.append("-");
            }
            macAddress.deleteCharAt(macAddress.length() - 1);
        } catch (UnknownHostException | SocketException e) {
            e.printStackTrace();
        }
        return macAddress.toString();
    }

    private User readDB() {
        File file = new File(Dev_DB_File);
        Scanner scanner;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        // read the 1st line that contains the username.
        String username = scanner.nextLine().split("\n")[0];
        // the 2nd line contains the password.
        String password = scanner.nextLine().split("\n")[0];
        User user = new User(username, password);
        // the 3rd line contains the MAC address of the device if found.
        if (scanner.hasNextLine()) {
            String mac = scanner.nextLine().split("\n")[0];
            user.setMac(mac);
        }
        if (scanner.hasNextLine()) {
            user.setPathToDB(scanner.nextLine().split("\n")[0]);
            pathToDB = user.getPathToDB();
        }
        return user;
    }

    private static String encrypt(String text) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] hashedPassword = md.digest(text.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hashedPassword) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        DevsLogin d = new DevsLogin();
        boolean flag = d.checkDevice();
        if (flag) {
            System.out.println("App opened, this is the database");
            System.out.println(DevsLogin.pathToDB);
        } else {
            System.out.println("Enter password and user name and database path");
            Scanner scanner = new Scanner(System.in);
            String username = scanner.next();
            String password = scanner.next();
            String database = scanner.next();
            System.out.println(d.newLogin(username, password, database));
        }
    }
}

class User {
    private String username;
    private String password;
    private String mac;
    private String pathToDB;

    public String getPathToDB() {
        return pathToDB;
    }

    public void setPathToDB(String pathToDB) {
        this.pathToDB = pathToDB;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public User(String username, String password, String mac) {
        this.username = username;
        this.password = password;
        this.mac = mac;
    }

}

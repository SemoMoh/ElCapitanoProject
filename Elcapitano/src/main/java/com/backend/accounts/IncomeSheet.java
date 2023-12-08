package com.backend.accounts;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class IncomeSheet {
    private static final String PATH = "";
    private String fileName;
    private String kindFile;
    private List<String> kinds;
    private ArrayList<String> incomes;
    private String date;
    private boolean isClosed = false;


    public IncomeSheet(String d) throws IOException {
        fileName = PATH + "Income Sheet " + LocalDateTime.now().getMonth() + ", " + LocalDateTime.now().getYear() + ".csv";

        kindFile = PATH + "kindFile.csv";
        kinds = new ArrayList<>();
        File file = new File(kindFile);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line;

        while ((line = bufferedReader.readLine()) != null) {
            String[] parts = line.split(",");
            for (String part : parts) {
                kinds.add(part);
            }
        }


        incomes = new ArrayList<>();
        date = d;
    }

    private static String getTime() {
        String time = " A.M";
        int hour = LocalDateTime.now().getHour();
        if (hour > 12) {
            hour -= 12;
            time = " P.M";
        }
        return hour + ":" + LocalDateTime.now().getMinute() + ":" + LocalDateTime.now().getSecond() + time;
    }

    public boolean createFile() throws IOException {
        File file = new File(fileName);
        if (!file.exists()) {
            return file.createNewFile();
        }
        return false;
    }

    public ArrayList<String> loadIncomes() throws IOException {
        FileReader reader = new FileReader(fileName);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line;
        if (incomes.size() == 0) {
            while ((line = bufferedReader.readLine()) != null) {
                incomes.add(line);
                if (line.equals("closed " + date))
                    isClosed = true;
            }
        }
        return incomes;
    }

    public void setClosed() throws IOException {
        if (!isClosed) {
            incomes.add("closed " + date);
            write();
        }
        this.isClosed = true;
    }

    public void addIncome(String incomeName, String kind, double cash, String description, String user) throws IOException {
        if (!this.isClosed) {
            this.loadIncomes();
            String income = LocalDate.now() + ","
                    + getTime() + ","
                    + incomeName + ","
                    + kind + ","
                    + cash + ","
                    + description + ","
                    + user;
            incomes.add(income);
            write();
        }

        int flag = 0;
        for (String k : kinds) {
            if (k.equals(kind)) {
                flag = 1;
                break;
            }
        }
        if (flag == 0) {
            kinds.add(kind);
            FileOutputStream fos = new FileOutputStream(kindFile);
            StringBuilder sb = new StringBuilder();
            sb.append("ملاعب,");
            for (int i = 0; i < kinds.size(); i++) {
                sb.append(kinds.get(i)).append(",");
            }
            fos.write(sb.toString().getBytes(StandardCharsets.UTF_8));
        }
    }

    public void addReservation(String incomeName, double cash, String description, String user) throws IOException {
        if (!this.isClosed) {
            this.loadIncomes();
            String income = LocalDate.now() + ","
                    + getTime() + ","
                    + incomeName + ","
                    + "ملاعب" + ","
                    + cash + ","
                    + description + ","
                    + user;
            incomes.add(income);
            write();
        }
    }

    public boolean removeIncome(String name) throws IOException {
        int i = find(name);
        if (!isClosed) {
            if (i != -1) {
                incomes.remove(i);
                this.write();
                return true;
            }
        }
        return false;
    }

    public boolean modify(String username, int index, String modification) throws IOException {
        if (!isClosed && index != 0 && index != 1) {
            int i = find(username);
            if (i != -1) {
                String[] attributes = incomes.get(i).split(",");
                attributes[index] = modification;
                String row = attributes[0] + ","
                        + attributes[1] + ","
                        + attributes[2] + ","
                        + attributes[3] + ","
                        + attributes[4] + ","
                        + attributes[5] + ","
                        + attributes[6];
                ArrayList<String> list = new ArrayList<>();
                for (int j = 0; j < incomes.size(); j++) {
                    if (j == i) {
                        list.add(row);
                    } else {
                        list.add(incomes.get(j));
                    }
                }
                incomes = list;
                write();
                return true;
            }
        }
        return false;
    }

    public int find(String name) {
        for (int i = 0; i < incomes.size(); i++) {
            String[] s = incomes.get(i).split(",");
            if (s.length != 7)
                continue;
            if (s[2].equals(name)) {
                return i;
            }
        }
        return -1;
    }

    private void write() throws IOException {
        File file = new File(fileName);
        FileOutputStream fos = new FileOutputStream(file);
        StringBuilder sb = new StringBuilder();
        for (String income : incomes) {
            sb.append(income).append("\n");
        }

        fos.write(sb.toString().getBytes(StandardCharsets.UTF_8));
        fos.write("".getBytes(StandardCharsets.UTF_8));
    }

    private void empty() throws IOException {
        File file = new File(fileName);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write("".getBytes(StandardCharsets.UTF_8));
    }
}


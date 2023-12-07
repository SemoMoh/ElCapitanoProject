package com.backend.fields;

import com.backend.helper.table.Table;
import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.util.Calendar;
import java.util.Scanner;

public class Field5x5 extends Field {

    /**
     * This construct initializes the data fields.
     * And The table will be initialized from the saved File for that field in the database.
     * Add to your knowledge that the table will use the current date's month and year.
     *
     * @param fieldName name of the field, make sure it the same as the field's folder in the database.
     * @param price     the price of one-hour reservation in this field.
     */
    public Field5x5(String fieldName, int price) throws IOException, CsvException {
        super(fieldName, price);
        //set the Table
        //1st get the current date.
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);
        String m;
        if (month < 10) {
            m = "0" + month;
        } else {
            m = String.valueOf(month);
        }
        String y = String.valueOf(year);
        //import the table from the DB
        this.table = FieldDB.getDB().readCSVFileWithDate(m + "-" + y, this.fieldName);
        this.tableChangeFlag = false;
    }


    public static void main(String[] args) {
        Field f;
        try {
            f = new Field5x5("No.1", 50);
        } catch (IOException | CsvException e) {
            throw new RuntimeException(e);
        }
        try {
            f.addReservation("1-2023", 20, 5, 6, 30, "fs", "545", "F", "dsc");
        } catch (IOException | IllegalReservationException | CsvException e) {
            throw new RuntimeException(e);
        }

        Scanner scanner = new Scanner(System.in);
        scanner.nextInt();
        f.editReservation("12-2023", 20, 7, 3, 20, 10, "sing", "545", "F", "dsc");
        scanner.nextInt();
/*
        f.deleteReservation("12-2023", 20, 7);
*/
        scanner.close();
    }

    @Override
    protected Table tableThatHoldsTheDate(String date) {
        /*
         * - We need to choose the correct table to read from.
         * 1) IF the passed date was the date of the month we are in now, use the table in the
         * class's data field.
         * 2) IF not, check if we have accessed that Table before, if yes it will be found in the tempTables.
         * 3) If not, use the TableDB to read the csv file from the Database.
         */
        Table result = null;
        if (date.equals(this.table.getTableName())) {
            result = this.table;
            this.tableChangeFlag = true;
        } else {
            // search in the temp
            int counter = 0;
            for (Table t : this.tempTables) {
                if (date.equals(t.getTableName())) {
                    result = t;
                    this.tempTablesChangeFlag.set(counter, true);
                    break;
                }
                counter++;
            }
            // if not found, then read from the DB and add into the temp table list.
            if (result == null) {
                try {
                    result = FieldDB.getDB().readCSVFileWithDate(date, this.fieldName);
                    this.tempTables.add(result);
                    this.tempTablesChangeFlag.add(true);

                } catch (IOException | CsvException ignored) {
                }
            }

        }
        return result;
    }

    /**
     * Save the changed tables into the database.
     */
    @Override
    public void saveChanges() {
        if (this.tableChangeFlag) {
            this.table.saveTableInDB(this.fieldName);
            this.tableChangeFlag = false;
        }
        for (int i = 0; i < this.tempTablesChangeFlag.size(); i++) {
            if (this.tempTablesChangeFlag.get(i)) {
                this.tempTables.get(i).saveTableInDB(this.fieldName);
                this.tempTablesChangeFlag.set(i, false);
            }
        }
    }

    /**
     * Reread all the changed tables if any error happened.
     */
    @Override
    public void reReadTable() throws IOException, CsvException {
        FieldDB db = FieldDB.getDB();
        if (this.tableChangeFlag) {
            this.table = db.readCSVFileWithDate(this.table.getTableName(), this.fieldName);
            this.tableChangeFlag = false;
        }
        for (int i = 0; i < this.tempTablesChangeFlag.size(); i++) {
            if (this.tempTablesChangeFlag.get(i)) {
                this.tempTables.set(
                        i, db.readCSVFileWithDate(this.tempTables.get(i).getTableName(), this.fieldName)
                );
                this.tempTablesChangeFlag.set(i, false);
            }
        }
    }
}

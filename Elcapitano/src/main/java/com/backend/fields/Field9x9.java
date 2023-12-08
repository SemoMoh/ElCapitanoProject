package com.backend.fields;

import com.backend.helper.table.Table;
import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Field9x9 extends Field {
    private final List<Field5x5> smallFields;
    private final int noOfSmallFields;

    /**
     * Construct the object.
     *
     * @param fieldName     Name of the field "No."+ number.
     * @param bigFieldPrice The price of the big field.
     * @param smallFields   The small fields that construct the big field.
     */
    Field9x9(String fieldName, int bigFieldPrice, List<Field5x5> smallFields) {
        super(fieldName, bigFieldPrice);
        this.noOfSmallFields = smallFields.size();
        this.smallFields = smallFields;

        //construct the big table.
        List<Table> smallTables = new ArrayList<>(noOfSmallFields);
        for (Field5x5 f : smallFields) {
            smallTables.add(f.table);
        }
        this.table = new Table(smallTables);
    }


    /**
     * This function returns the table of that field that represents that month.
     *
     * @param date The date of the reservation in this format: "MM-yyyy".
     * @return the required Table
     * @brief First it checks this month table, if it doesn't match the date, it will check the tables in the
     * temp table list, if it was not there it will get it from the DB.
     */
    @Override
    protected Table tableThatHoldsTheDate(String date) {
        if (this.table.getTableName().equals(date)) {
            return this.table;
        }
        for (Table t : this.tempTables) {
            if (t.getTableName().equals(date)) {
                return t;
            }
        }
        List<Table> smallerTables = new ArrayList<>(this.noOfSmallFields);
        for (Field5x5 f : this.smallFields) {
            smallerTables.add(f.tableThatHoldsTheDate(date));
        }
        Table t = new Table(smallerTables);
        this.tempTables.add(t);
        return t;
    }

    /**
     * Save the changed tables into the database.
     */
    @Override
    public void saveChanges() {
        for (Field5x5 f : this.smallFields) {
            f.saveChanges();
        }
        this.tableChangeFlag = false;
        Collections.fill(this.tempTablesChangeFlag, false);
    }

    /**
     * Reread all the changed tables if any error happened.
     * It recreates a new table of this month.
     * And clear all the temp tables.
     *
     * @note You should call this function after any changes in the bif field.
     */
    @Override
    public void reReadTable() throws IOException, CsvException {
        // Recreate the table of this month.
        List<Table> tables = new ArrayList<>(this.noOfSmallFields);
        for (Field5x5 f : this.smallFields) {
            f.reReadTable();
            tables.add(f.table);
        }
        tableChangeFlag = false;
        this.table = new Table(tables);

        // clear all temp tables.
        this.tempTablesChangeFlag.clear();
        this.tempTables.clear();
    }

    @Override
    public void addReservation(String date, int day, int time, int noOfHours, int paid, String name,
                               String mobile, String bigField, String description)
            throws IOException, CsvException, IllegalReservationException {
        int amount = this.price * noOfHours;
        // This reservation as for the big field only,
        // So first add the reservation on all the small fields, then edit the amount at each small field.
        // Then save the changes.
        for (Field5x5 field : this.smallFields) {
            field.addReservation(date, day, time, noOfHours, paid, name, mobile, bigField, description);
            field.editReservation(date, day, time, noOfHours, amount, paid, name, mobile, bigField, description);
            field.saveChanges();
        }
        // Reread the tables for the big field.
        this.reReadTable();
    }

    @Override
    public void editReservation(String date, int day, int time, int noOfHours, int amount, int paid, String name, String mobile, String bigField, String description) {
        for (Field5x5 field : this.smallFields) {
            field.editReservation(date, day, time, noOfHours, amount, paid, name, mobile, bigField, description);
            field.saveChanges();
        }
        // Reread the tables for the big field.
        try {
            this.reReadTable();
        } catch (IOException | CsvException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteReservation(String date, int day, int time) {
        for (Field5x5 field : this.smallFields) {
            field.deleteReservation(date, day, time);
            field.saveChanges();
        }
        try {
            this.reReadTable();
        } catch (IOException | CsvException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        List<Field5x5> smallF = new ArrayList<>(3);
        try {
            smallF.add(new Field5x5("No.2", 250));
            smallF.add(new Field5x5("No.3", 250));
            smallF.add(new Field5x5("No.4", 250));

        } catch (IOException | CsvException e) {
            throw new RuntimeException(e);
        }


        Field9x9 f = new Field9x9("No.5", 500, smallF);
        try {
            f.addReservation("12-2023", 31, 10, 3, 500, "dsd", "5612", "T", "big");
            f.reReadTable();

        } catch (IOException | CsvException | IllegalReservationException e) {
            throw new RuntimeException(e);
        }
        System.out.println(f.table);
    }
    public List<Field5x5> getSmallFields(){
        return this.smallFields;
    }
}

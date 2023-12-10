package com.backend.fields;

import com.example.elcapitano.HelloApplication;
import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

// This class is designed only for an Elcapitano fields system, which contains only one small field (5x5)
// and one big field (9x9) which can be divided into three small fields.
public class FieldSystem {
    private Field5x5 smallField;
    //TODO: getter and setter for prices
    private Field9x9 bigField;

    public FieldSystem(int priceBigField, int priceSmallField) {
        FieldDB.pathBeforeAbs = HelloApplication.DB_path + "Fields/";
        try {
            this.smallField = new Field5x5("No.1", priceSmallField);
            List<Field5x5> smallFields = Arrays.asList(
                    new Field5x5("No.2", priceSmallField),
                    new Field5x5("No.3", priceSmallField),
                    new Field5x5("No.4", priceSmallField)
            );
            this.bigField = new Field9x9("No.5", priceBigField, smallFields);
        } catch (IOException | CsvException e) {
            throw new RuntimeException(e);
        }
    }


    private Field getField(String fieldName) {
        if (fieldName.equals("No.1")) {
            return this.smallField;
        }
        if (fieldName.equals("No.5")) {
            return this.bigField;
        }
        List<Field5x5> smallFields = this.bigField.getSmallFields();
        if (fieldName.equals("No.2")) {
            return smallFields.get(0);
        }
        if (fieldName.equals("No.3")) {
            return smallFields.get(1);
        }
        return smallFields.get(2);
    }

    /**
     * @param date      The date of the reservation in this format: "MM-yyyy".
     * @param day       The day of the month of the reservation.
     * @param fieldName the number of the required field.
     *                  Make sure to put it in this format: "No.1".
     *                  - For small field : "No.1".
     *                  - For large field : "No.5".
     *                  - For Large fields' fields : "No.2", "No.3", "No.4".
     * @return An array of boolean, each position stands for an hour (starting from 00).
     * If the value was true, then this time slot is reserved.
     * Else then it's not reserved.
     */
    public boolean[] getDayReservation(String date, int day, String fieldName) {
        Field field = this.getField(fieldName);
        List<List<String>> dayTable = field.getDayTable(date, day);
        boolean[] result = new boolean[24];
        for (int i = 0; i < 24; i++) {
            List<String> row = dayTable.get(i);
            String reservedField = row.get(2);
            //not reserved --> number of hours == 0 --> False
            //reserved --> True
            result[i] = !reservedField.equals("0");
        }
        return result;
    }

    public boolean addReservation(String fieldName, String date, int day, int hour, int noHours, int paid,
                                  String name, String mobile, String description) {
        Field field = this.getField(fieldName);
        String big_FieldFlag = "F";

        if (field == this.bigField) {
            big_FieldFlag = "T";
        }
        try {
            field.addReservation(date, day, hour, noHours, paid, name, mobile, big_FieldFlag, description);
        } catch (IOException | CsvException | IllegalReservationException e) {
            return false;
        }
        return true;
    }



    public Reservation getReservationDetails(String fieldName, String date, int day, int hour) {
        Reservation reservation = new Reservation();
        Field field = this.getField(fieldName);
        List<String> row= field.getReservation(date,day,hour);
        //TODO: fuckin do this
        reservation.date= row.get(9);
        reservation.day=Integer.parseInt(row.get(0));
        reservation.hour=Integer.parseInt(row.get(1));
        reservation.noHours=Integer.parseInt(row.get(2));
        reservation.totalAmount=Integer.parseInt(row.get(3));
        reservation.paid=Integer.parseInt(row.get(4));
        reservation.name=row.get(5);
        reservation.mobile=row.get(6);
        reservation.description= row.get(8);

        return null;
    }


    public static void main(String[] args) {
        FieldSystem f = new FieldSystem(50, 100);
    }


    public static int getNoOfHoursOfRelatedReservation(String date, int day, String fieldName, int hour) {

        return 1;
    }
}

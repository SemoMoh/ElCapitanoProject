package com.backend.fields;

import com.backend.helper.table.Table;
import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.util.*;

public abstract class Field {
    protected String fieldName;
    protected Table table; //Table of this month.
    protected boolean tableChangeFlag;
    protected List<Table> tempTables; //Accessed other months tables. Stored to improve performance.
    protected List<Boolean> tempTablesChangeFlag;
    protected int price;

    /**
     * This construct initializes the data fields.
     * And The table will be initialized from the saved File for that field in the database.
     * Add to your knowledge that the table will use the current date's month and year.
     *
     * @param fieldName name of the field, make sure it the same as the field's folder in the database.
     * @param price     the price of one-hour reservation in this field.
     */
    public Field(String fieldName, int price) {
        // set the field name
        this.fieldName = fieldName;
        // set the price
        this.price = price;
        // set the temp tables list.
        this.tempTables = new LinkedList<>();
        this.tempTablesChangeFlag = new LinkedList<>();
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    /**
     * This method returns a reservation for a given date.
     *
     * @param date The date of the reservation in this format: "MM-yyyy".
     * @param time The starting time of the time slot (or the reservation).
     * @return List of strings, arrange in the Table field Keys order.
     * "Day", "Time slot", "Number Of Hours", "Amount", "Paid",
     * "Name", "Mobile", "Big Field", "Description".
     */
    public List<String> getReservation(String date, int day, int time) {
        List<String> result = this.findHeaderOfReservation(date, day, time, null);
        // Reset all changed flags.
        this.tableChangeFlag = false;
        Collections.fill(this.tempTablesChangeFlag, false);
        //return result.
        return result;
    }

    /**
     * This function returns the table of that field that represents that month.
     *
     * @param date The date of the reservation in this format: "MM-yyyy".
     * @return the required Table
     * @brief First it checks this month table, if it doesn't match the date, it will check the tables in the
     * temp table list, if it was not there it will get it from the DB.
     */
    protected abstract Table tableThatHoldsTheDate(String date);

    /**
     * This function returns the date of this day + number of days.
     *
     * @param date  The date of the reservation in this format: "MM-yyyy".
     * @param day   The reference day.
     * @param shift Number of days to find the day:
     *              - after if shift was positive.
     *              - before if shift was negative.
     * @return A string of two parts:
     * - First part: A string that holds the day number.
     * - Second part: A string that holds the date in the format "MM-yyyy".
     */
    protected String[] nextDayDate(String date, int day, int shift) {
        //To know the date of the next date.
        String[] parts = date.split("-");
        int month = Integer.parseInt(parts[0]);
        int year = Integer.parseInt(parts[1]);

        // Build a calendar that saves the passed (current) date value
        Calendar cal = new Calendar.Builder().setDate(year, month - 1, day).build();
        // Update the calendar to hold the date after the shift.
        cal.add(Calendar.DAY_OF_MONTH, shift);
        // get the needed values.
        month = cal.get(Calendar.MONTH) + 1;
        year = cal.get(Calendar.YEAR);
        day = cal.get(Calendar.DAY_OF_MONTH);
        // format the month to be in 2 digits.
        String m;
        if (month < 10) {
            m = "0" + month;
        } else {
            m = String.valueOf(month);
        }
        // format the year
        String y = String.valueOf(year);
        // return the result string.
        return new String[]{String.valueOf(day), m + "-" + y};
    }

    /**
     * found the header row of the reservation.
     *
     * @param date The date of the reservation in this format: "MM-yyyy".
     * @param day  The day of the month of the reservation.
     * @param time The starting time of the time slot (or the reservation).
     * @return List of strings, arrange in the Table field Keys order.
     * "Day", "Time slot", "Number Of Hours", "Amount", "Paid",
     * "Name", "Mobile", "Big Field", "Description".
     * @brief This method starts by going directly into the passed time slot,
     * - If the time slot was not reserved (number of hours field == "0")
     * --> it returns the list as it's
     * - if the time slot was reserved, it searches for the starting hour of this reservation
     * --> when found it returns list that contains the reservation as it's.
     */
    protected List<String> findHeaderOfReservation(String date, int day, int time, StringBuilder headerDate) {
        boolean dateChanged = true;
        Table toSearchIN = this.table;
        while (true) {
            // if date changed load the new table to read from.
            if (dateChanged) {
                if (headerDate != null) {
                    if (!headerDate.isEmpty()) {
                        headerDate.delete(0, headerDate.length());
                    }
                    headerDate.append(date);
                }
                //Check which table to search on
                toSearchIN = this.tableThatHoldsTheDate(date);
                dateChanged = false;
            }
            //get the row we need
            List<String> row = toSearchIN.getRow(day, time);
            //check the Number of hour fields if it was "0" or not "" return the list.
            if (row.get(2).equals("0") || !row.get(2).equals("")) {
                row.add(date);
                return row;
            } else {
                //look up in the table until finding the header.
                time--;
                // Reached a prev day.
                if (time < 0) {
                    time = 23;
                    //get the next day date and number.
                    String[] parts = this.nextDayDate(date, day, -1);
                    date = parts[1];
                    day = Integer.parseInt(parts[0]);
                    // to update the table.
                    dateChanged = true;
                }
            }
        }
    }

    /**
     * This method adds a reservation to the table then saves it in the database.
     *
     * @param date        The date of the reservation in this format: "MM-yyyy".
     * @param day         The day number of the reservation.
     * @param time        The starting time of the time slot (or the reservation).
     * @param noOfHours   Number of hours to reserve.
     * @param paid        Amount of advance payment.
     * @param name        The assigned name to this reservation.
     * @param mobile      The assigned mobile number to this reservation.
     * @param description Additional description for the reservation.
     */
    public void addReservation(String date, int day, int time, int noOfHours, int paid,
                               String name, String mobile, String bigField, String description)
            throws IOException, CsvException, IllegalReservationException {
        int amount = this.price * noOfHours;
        List<String> temp = Arrays.asList(String.valueOf(day), String.valueOf(time), String.valueOf(noOfHours),
                String.valueOf(amount), String.valueOf(paid), name, mobile, bigField, description);
        // add the list to the table.
        try {
            this.editTable(date, day, time, temp, false);
        } catch (IllegalReservationException e) {
            this.reReadTable();
            //throw exception here, to show that an error happened on the screen.
            throw e;
        }
        noOfHours--; // this means we only saved information row.

        // change the values of the row into the values of the reserved fields.
        temp.set(2, "");
        temp.set(3, "");
        temp.set(4, "");
        temp.set(5, "");
        temp.set(6, "");
        temp.set(8, "");
        //To know the date of the next date.
        String[] parts;
        while (noOfHours > 0) {
            //change parameters
            time++;
            if (time == 24) {
                time = 0;
                parts = this.nextDayDate(date, day, 1);
                date = parts[1];
                day = Integer.parseInt(parts[0]);
            }

            //edit the row
            temp.set(0, String.valueOf(day));
            temp.set(1, String.valueOf(time));
            //call the edit function
            try {
                this.editTable(date, day, time, temp, false);
            } catch (IllegalReservationException e) {
                this.reReadTable();
                //TODO: throw exception here, to show that an error happened on the screen.
                throw e;
            }
            // Decrement the remaining values.
            noOfHours--;
        }
        this.saveChanges();
    }

    /**
     * This method edits a reservation to the table then saves it in the database.
     *
     * @param date        The date of the reservation in this format: "MM-yyyy".
     * @param time        The starting time of the time slot (or the reservation).
     * @param noOfHours   Number of hours to reserve.
     * @param amount      The total amount of the reservation.
     * @param paid        Amount of advance payment.
     * @param name        The assigned name to this reservation.
     * @param mobile      The assigned mobile number to this reservation.
     * @param description Additional description for the reservation.
     */
    public void editReservation(String date, int day, int time, int noOfHours, int amount, int paid, String name, String mobile, String bigField, String description) {
        StringBuilder headerDate = new StringBuilder();
        List<String> rowToEdit = this.findHeaderOfReservation(date, day, time, headerDate);
        int oldNoOfHours = Integer.parseInt(rowToEdit.get(2));
        rowToEdit.set(2, String.valueOf(noOfHours));
        rowToEdit.set(3, String.valueOf(amount));
        rowToEdit.set(4, String.valueOf(paid));
        rowToEdit.set(5, name);
        rowToEdit.set(6, mobile);
        rowToEdit.set(7, bigField);
        rowToEdit.set(8, description);
        day = Integer.parseInt(rowToEdit.get(0));
        time = Integer.parseInt(rowToEdit.get(1));
        try {
            editTable(date, day, time, rowToEdit, true);
        } catch (IOException | CsvException | IllegalReservationException ignored) {
        }

        // Check if the number of hours is changed to delete or add empty rows.
        if (oldNoOfHours == noOfHours) {
            this.saveChanges();
            return;
        }


        List<String> emptyRows = Arrays.asList(String.valueOf(day), String.valueOf(time), "", "", "", "", "", bigField, "");
        List<String> defaultRows = new ArrayList<>(Table.columnsNo);
        defaultRows.add(String.valueOf(day));
        defaultRows.add(String.valueOf(time));
        defaultRows.add(String.valueOf(Table.defaultValues.get(2)));
        defaultRows.add(String.valueOf(Table.defaultValues.get(3)));
        defaultRows.add(String.valueOf(Table.defaultValues.get(4)));
        defaultRows.add(String.valueOf(Table.defaultValues.get(5)));
        defaultRows.add(String.valueOf(Table.defaultValues.get(6)));
        defaultRows.add(String.valueOf(Table.defaultValues.get(7)));
        defaultRows.add(String.valueOf(Table.defaultValues.get(8)));


        time++;
        String[] parts;
        int counter = 1; // to skip the needed number of rows.


        // we need to delete some rows from the table and replace it with the default values.
        if (oldNoOfHours > noOfHours) {
            while (counter != oldNoOfHours) {
                if (time == 24) {
                    time = 0;
                    parts = this.nextDayDate(date, day, 1);
                    day = Integer.parseInt(parts[0]);
                    date = parts[1];
                }
                if (counter >= noOfHours) {
                    try {
                        defaultRows.set(0, String.valueOf(day));
                        defaultRows.set(1, String.valueOf(time));
                        this.editTable(date, day, time, defaultRows, true);
                    } catch (IOException | IllegalReservationException | CsvException ignored) {
                    }
                }
                time++;
                counter++;
            }
        }

        // we need to add some rows from the table and replace it with the empty (reserved) values.
        else {
            while (counter != noOfHours) {
                if (time == 24) {
                    time = 0;
                    parts = this.nextDayDate(date, day, 1);
                    day = Integer.parseInt(parts[0]);
                    date = parts[1];
                }

                try {
                    emptyRows.set(0, String.valueOf(day));
                    emptyRows.set(1, String.valueOf(time));
                    this.editTable(date, day, time, emptyRows, true);
                } catch (IOException | IllegalReservationException | CsvException ignored) {
                }
                time++;
                counter++;
            }
        }
        this.saveChanges();
    }

    /**
     * This method deletes a reservation and inserts into the table the default values.
     *
     * @param date The date of the reservation in this format: "MM-yyyy".
     * @param day  The day of the reservation.
     * @param time The starting time of the time slot (or the reservation).
     * @note The default values are:
     * "not default", "not default", "0", "0", "0", "", "", "F", "".
     * - The order of keys:
     * "Day", "Time slot", "Number Of Hours", "Amount", "Paid",
     * "Name", "Mobile", "Big Field", "Description".
     */
    public void deleteReservation(String date, int day, int time) {
        this.editReservation(date, day, time, 0, 0, 0, "", "", "F", "");
    }

    /**
     * @param date The date of the table in this format: "MM-yyyy".
     * @param day  The day of the table.
     * @return List of rows.
     * Each row is a list of strings arranged in the following order:
     * "Day", "Time slot", "Number Of Hours", "Amount", "Paid",
     * "Name", "Mobile", "Big Field", "Description".
     */
    public List<List<String>> getDayTable(String date, int day) {
        List<List<String>> table = new ArrayList<>();
        // find the required month table
        Table tableMonth = this.tableThatHoldsTheDate(date);
        // add the required rows to the table
        for (int i = 0; i < 24; i++) {
            table.add(tableMonth.getRow(day, i));
        }
        return table;
    }

    /**
     * A method that is used to replace the old values of a table's row into the new values.
     *
     * @param date           The date of the reservation in this format: "MM-yyyy".
     * @param day            The day number of the reservation.
     * @param time           The starting time of the time slot (or the reservation).
     * @param newRow         the new row with the new values.
     * @param ignoreReserved If it was true, it accepts overriding a reserved time slot.
     * @note this method doesn't save the table to the DB after editing.
     * @note This is a helper method used by other methods.
     */
    protected void editTable(String date, int day, int time, List<String> newRow, boolean ignoreReserved)
            throws IOException, CsvException, IllegalStateException, IllegalReservationException {
        // To store the row that needs to be edited.
        Table focusedTable = this.tableThatHoldsTheDate(date);
        List<String> oldRow = focusedTable.getRow(day, time);

        if (!ignoreReserved && (oldRow.get(2).equals("") || !oldRow.get(2).equals("0"))) {
            throw new IllegalReservationException();
        }
        // now set the oldRow's values into the new row's values.
        for (int i = 0; i < oldRow.size(); i++) {
            oldRow.set(i, newRow.get(i));
        }
    }

    /**
     * Save the changed tables into the database.
     */
    public abstract void saveChanges();

    /**
     * Reread all the changed tables if any error happened.
     */
    public abstract void reReadTable() throws IOException, CsvException;
}

package com.backend.helper.table;

import com.backend.fields.FieldDB;
import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// TODO: update by adding a class like an object poll pattern to provide with the needed tables.
/**
 * A class to save and access the Fields CSV files easily.
 */
public class Table {
    public final static List<String> tableKeys = Arrays.asList(
            "Day", "Time slot", "Number Of Hours", "Amount", "Paid", "Name", "Mobile", "Big Field", "Description"
    );
    public final static List<String> defaultValues = Arrays.asList(
            "not default", "not default", "0", "0", "0", "", "", "F", ""
    );
    public final static int columnsNo = 9;
    private final List<List<String>> table;
    private int rowsNo;
    private String tableName; // stores the date of the table.

    /**
     * Initializes the table with a specified number of columns and rows.
     *
     * @param date The date in "month-year" format representing the table's data, where:
     *             - The month is represented by two digits (e.g., "01" for January).
     *             - The year is represented by four digits (e.g., "2023").
     * @note The number of rows is determined based on the month and year that the table represents,
     * multiplied by 24 (24 rows per day, one for each hour).
     */
    public Table(String date) {
        this.tableName = date;
        /*
         * Split the date string into month number string, and year number string.
         * The first String in the array will contain the month number.
         * The second String will contain the year number.
         */
        String[] parts = date.split("-");
        /*
         * Determine the number of days in that month based on the date passed.
         * YearMonth's "of" static function is used to create a YearMonth object, then the lengthOfMonth
         * function returns the number of days in that month.
         */
        int daysInMonth = (YearMonth.of(Integer.parseInt(parts[1]), Integer.parseInt(parts[0])).lengthOfMonth());

        table = new ArrayList<>(daysInMonth * 24 + 1);
        rowsNo = daysInMonth * 24;

    }

    /**
     * @brief This constructor is used to initialize the List<List<String>> table
     * with no initial capacity.
     */
    public Table() {
        table = new ArrayList<>();
    }

    /**
     * This constructor initializes the table with data collected from a CSV file.
     *
     * @param fileContents A list of String[] that is obtained from a CSV file using the
     *                     openCSV library. Note that: -Each Array of strings is a row.
     *                     - Each String in array is a cell's data. - The List collects all the rows.
     * @param date         The date in "month-year" format representing the table's data, where:
     *                     - The month is represented by two digits (e.g., "01" for January).
     *                     - The year is represented by four digits (e.g., "2023").
     */
    public Table(List<String[]> fileContents, String date) {
        // initialize the table with another constructor.
        this(date);
        // to treat the first row with different way than other rows.
        boolean firstRow = true;
        //Loop for each row (position on the list).
        for (String[] row : fileContents) {
            // create a list and store the array of strings as a list on it.
            // note that the objects in the List have the same reference of the string[].
            List<String> cells = new ArrayList<>(columnsNo);
            cells.addAll(Arrays.asList(row));

            // store the first row in the tableKeys list.
            if (firstRow) {
                firstRow = false;
                continue;
            }
            // add the row to the table.
            table.add(cells);
        }
    }

    /**
     * A constructor for big fields (like Field9x9) that merge the smaller Fields of that
     * big Field.
     *
     * @param smallFieldsTables A list of all the smallest fields' tables.
     * @brief All the based tables should have the same day and time slot
     * columns, otherwise the function will throw an exception.
     * The function will create a table with rows that contain data from the smaller tables.
     * - If the ime slot was reserved for the big table,the row must be the same in all tables.
     * - If the time slot was reserved for any small field, it must not be reserved for
     * big field in and other table (bit field flag must be false).
     * In this case, the big table will contain the reservation data in its description,
     * and its name will be "Reserved".
     * _ If no reservations were made in any smaller tables for a specific row,
     * the big table will contain the default values.
     */
    public Table(List<Table> smallFieldsTables) {
        //create a new table for the needed date.
        this(smallFieldsTables.get(0).getTableName());
        // check the passed tables.
        if (smallFieldsTables.size() == 1) {
            throw new IllegalArgumentException("There is no enough tables sent, they must be more than one.");
        }
        String date = smallFieldsTables.get(0).getTableName();
        for (int i = 1; i < smallFieldsTables.size(); i++) {
            String dateToCompare = smallFieldsTables.get(i).getTableName();
            if (!dateToCompare.equals(date)) {
                throw new IllegalArgumentException("All passed tables must represent the same date");
            }
        }

        /*
         * Combine all the tables into the new table, if one of the small table's rows contain a reservation, check:
         * - If the reservation for a small table,
         * the Bif Field flag must be false in all the other tables at the same row.
         * - If the reservation for a large table,
         * the Bif Field flag must be true in all the other tables in the same row
         * and all tables have the same values for that row.
         */
        // Loop for each row
        for (int i = 0; i < this.rowsNo; i++) {
            // initialize a List that contains the same time slot for each table.
            List<List<String>> smallRows = new ArrayList<>(smallFieldsTables.size());
            //add all the focused on rows to the list.
            for (Table smallTable : smallFieldsTables) {
                smallRows.add(smallTable.getRow(i));
            }
            // Check of the date cell, all must have the same number, if one is changed, so there is an error.
            // If no error was found, add this cell into the big table's row.
            String dayCell = smallRows.get(0).get(0);
            for (int j = 1; j < smallRows.size(); j++) {
                String dayCellToCompare = smallRows.get(j).get(0);
                if (!dayCellToCompare.equals(dayCell)) {
                    throw new IllegalArgumentException("All passed tables must represent the same days in the same order");
                }
            }

            // Repeat for the hour cell.
            String hourCell = smallRows.get(0).get(1);
            for (int j = 1; j < smallRows.size(); j++) {
                String hourCellToCompare = smallRows.get(j).get(1);
                if (!hourCellToCompare.equals(hourCell)) {
                    throw new IllegalArgumentException("All passed tables must represent the same days in the same order");
                }
            }

            /*
             * Reservation part:
             * - If check the Big Field flag, if it was true,
             * check that the same values are in all the other small tables,
             * and in the big field's table insert the same data there.
             * _ If the Big field flag == false, check that is the same in all tables
             * then add all the data in the big table's description, and associate to the name field
             * "Reserved", and add the time "1".
             */
            // First check that all flags have the same value.
            String bigFieldFlagCell = smallRows.get(0).get(7);
            for (int j = 1; j < smallRows.size(); j++) {
                String bigFieldFlagCellToCompare = smallRows.get(j).get(7);
                if (!bigFieldFlagCellToCompare.equals(bigFieldFlagCell)) {
                    throw new IllegalArgumentException("All passed tables must represent the same days in the same order");
                }
            }

            // for a big field reservation
            if (bigFieldFlagCell.equalsIgnoreCase("T")) {
                // make sure that all of them should have the same values.
                List<String> toCompare = smallRows.get(0);
                for (List<String> row : smallRows) {
                    if (!row.equals(toCompare)) {
                        throw new IllegalArgumentException("All passed tables must represent the same days in the same order");
                    }
                }

                // create a list of strings that have the same values as any smaller table's row,
                // then add it to the big table.
                List<String> newRow = new ArrayList<>(Table.columnsNo);
                for (String cell : smallRows.get(0)) {
                    newRow.add(String.valueOf(cell));
                }
                this.addRow(newRow);
            }
            // for a small field reservation
            else {
                // in the big table's row, put the default values for a reserved row
                // Day and time cells as any other table.
                String day = smallRows.get(0).get(0);
                String timeSlot = smallRows.get(0).get(1);
                // If any field was reserved, put "Number of hours == 1", else "0"
                boolean reserved = false;
                // loop for all other tables.
                // get the data of each smaller table to add it to the description of the big table.
                List<String> description = new ArrayList<>(smallRows.size());
                int counter = 2;
                for (List<String> row : smallRows) {
                    int numberOfHoursField = 0;
                    try {
                        numberOfHoursField = Integer.parseInt(row.get(2));
                    } catch (NumberFormatException e) {
                        numberOfHoursField = -1;
                    }
                    if (numberOfHoursField >= 1) {
                        reserved = true;
                        String sb = "-No." + counter + ":" +
                                "Number of hours: " + row.get(2) + ". " +
                                "Name: " + row.get(5) + ". " +
                                "Mobile: " + row.get(6) + ". ";
                        description.add(sb);
                    } else if (numberOfHoursField == -1) {
                        reserved = true;
                        // for more than an hour reservation, say where it is located only.
                        description.add("-No." + counter + ": Resumed from the last hour");
                    }
                    counter++;
                }
                int numberOfHours = reserved ? 1 : 0;
                int amount = 0;
                int paid = 0;
                String name = reserved ? "reserved" : "";
                String mobile = "";
                char bigField = 'F';
                List<String> newRow = new ArrayList<>(Table.columnsNo);
                newRow.add(day);
                newRow.add(timeSlot);
                newRow.add(String.valueOf(numberOfHours));
                newRow.add(String.valueOf(amount));
                newRow.add(String.valueOf(paid));
                newRow.add(name);
                newRow.add(mobile);
                newRow.add(String.valueOf(bigField));
                newRow.add(description.toString());
                //add the new row to the table
                this.addRow(newRow);
            }
        }
    }

    /**
     * A function that creates a table with default values.
     *
     * @param date The date in "month-year" format representing the table's data, where:
     *             - The month is represented by two digits (e.g., "01" for January).
     *             - The year is represented by four digits (e.g., "2023").
     * @return A default table.
     * @brief - The default table contains a row for each hour in a whole month, and the other fields
     * contain the default values associated with each key.
     * The default values: - Day: according to the day order. - Time Slot: according to the hour order.
     * - Number Of Hours: "0". - Amount: "0". - Paid: "0". - Name: "". Mobile: "".
     * - Big Field: "F". - Description: "".
     * - The number of days in the month is determined by the passed date, taking into accountant the year.
     * - The number of rows = number of days in this month * 24 time slot + one row for keys.
     */
    public static Table createEmptyTable(String date) {
        // To store the result.
        // The constructor will initialize the row number with the number of days * 24 time slots.
        Table result = new Table(date);
        int rowsNo = result.rowsNo;
        //starting values
        int hour = 0;
        int day = 1;
        // loop to create each row.
        for (int i = 0; i < rowsNo; i++) {
            result.addRow(result.createDefaultRow(day, hour));
            hour++;
            // every 24 hours reset the hour counter and increment the day counter.
            if (hour == 24) {
                hour = 0;
                day++;
            }
        }
        //return the new default table.
        return result;
    }


    /**
     * This function returns a list that contains the value of each cell in the row.
     *
     * @param row The number of the needed row.
     *            Row number constrains: Zero <= number < number of rows in the table.
     * @return A list that contains the value of each cell in the row.
     * @throws IndexOutOfBoundsException thrown if the number of the passed row doesn't achieve:
     *                                   Zero <= Row Number < number of rows in the table.
     */
    public List<String> getRow(int row) throws IndexOutOfBoundsException {
        if (row >= this.rowsNo || row < 0) {
            throw new IndexOutOfBoundsException(
                    "You tried to reach an a row number that does not belong to this table."
            );
        }
        return table.get(row);
    }

    /**
     * This function returns a list that contains the value of each cell in the row.
     *
     * @param day  The number of the needed row.
     *             Row number constrains: Zero < row < day in the month represented by this table.
     * @param hour the needed time slot.
     * @return A list that contains the value of each cell in the row.
     * @throws IndexOutOfBoundsException thrown if the number of the passed row doesn't achieve:
     *                                   Zero <= Row Number < number of rows in the table.
     */
    public List<String> getRow(int day, int hour) throws IndexOutOfBoundsException {
        int row = (day - 1) * 24 + hour;
        return this.getRow(row);
    }

    /**
     * This function returns a list that contains the value of Key in the column.
     *
     * @return A list that contains the value of each key.
     */
    public List<String> getKeys() {
        return tableKeys;
    }

    /**
     * This function returns the value (as a string) of the required cell.
     *
     * @param row The number of the needed row.
     *            Row number constrains: Zero <= Row Number < number of rows in the table.
     * @param key The required Key or column.
     * @return A String that contains the value of that cell.
     * @throws IndexOutOfBoundsException thrown if the number of the passed row doesn't achieve:
     *                                   Zero <= Row Number < number of rows in the table.
     * @throws IllegalArgumentException  thrown when the key required is not in the table.
     */
    public String getCell(int row, String key) throws IndexOutOfBoundsException, IllegalArgumentException {
        // determine the column needed to access.
        int column = tableKeys.indexOf(key);
        return this.getRow(row).get(column);
    }

    /**
     * This function returns the value (as a string) of the required cell.
     *
     * @param row    The number of the needed row.
     *               Row number constrains: Zero <= Row Number < number of rows in the table.
     * @param column The column number.
     *               Column number constrains: Zero <= Column Number < number of columns in the table.
     * @return A String that contains the value of that cell.
     * @throws IndexOutOfBoundsException thrown if the number of the passed row doesn't achieve:
     *                                   Zero <= Row Number < number of rows in the table.
     * @throws IllegalArgumentException  thrown if the number of the passed row doesn't achieve:
     *                                   Zero <= Column Number < number of Columns in the table.
     */
    public String getCell(int row, int column) throws IndexOutOfBoundsException, IllegalArgumentException {
        if (column < 0 || column >= columnsNo) {
            throw new IllegalArgumentException("The provided key is not in the table");
        }
        // Get the list of the required row list, then return the specific String (cell) needed.
        return this.getRow(row).get(column);
    }

    /**
     * Ths function returns the table in the List of string arrays format.
     * This format is used by the FieldDB class, specifically by the CSV writer class.
     *
     * @return The table in List of string arrays format.
     */
    public List<String[]> getTable() {
        // To store the result.
        List<String[]> result = new ArrayList<>(rowsNo);
        // A temp array to store the contents of each row.
        String[] temp = new String[Table.columnsNo];
        // loop to get the key's row.
        for (int i = 0; i < Table.columnsNo; i++) {
            temp[i] = Table.tableKeys.get(i);
        }
        // add the keys to the List.
        result.add(temp);
        // now loop for each row in the table and do the same.
        for (List<String> row : this.table) {
            // reallocating the temp array because its string contents are already
            // referenced to in the previous iteration in the result list.
            temp = new String[9];
            for (int i = 0; i < Table.columnsNo; i++) {
                temp[i] = row.get(i);
            }
            result.add(temp);
        }
        // return the result.
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Table.tableKeys.toString()).append('\n');
        for (int i = 0; i < rowsNo; i++) {
            sb.append(this.getRow(i)).append('\n');
        }
        return sb.toString();
    }

    /**
     * a helper function that creates a list with default values.
     *
     * @param day  day number for the row
     * @param hour hour number for the row
     * @return a list of the row with its default values.
     */
    private List<String> createDefaultRow(int day, int hour) {
        List<String> row = new ArrayList<>(9);
        row.add(String.valueOf(day));  // day
        row.add(String.valueOf(hour)); // hour
        row.add(defaultValues.get(2)); // number of hours
        row.add(defaultValues.get(3)); // amount
        row.add(defaultValues.get(4)); // paid
        row.add(defaultValues.get(5)); // name
        row.add(defaultValues.get(6)); // mobile
        row.add(defaultValues.get(7)); // big field
        row.add(defaultValues.get(8)); // description
        return row;
    }

    /**
     * Adds a new row to the table. Used only when creating an empty table with the default values.
     *
     * @param row the required row to add.
     */
    private void addRow(List<String> row) {
        this.table.add(row);
    }

    public static void main(String[] args) throws IOException, CsvException {
        FieldDB d = FieldDB.getDB();
        Table t1 = d.readCSVFile("No.2/12-2023.csv");
        System.out.println("T1----------------------------------------------------------------");
        System.out.println(t1);
        Table t2 = d.readCSVFile("No.3/12-2023.csv");
        System.out.println("T2----------------------------------------------------------------");
        System.out.println(t2);
        Table t3 = d.readCSVFile("No.4/12-2023.csv");
        System.out.println("T3----------------------------------------------------------------");
        System.out.println(t3);
        List<Table> smallTables = Arrays.asList(t1, t2, t3);
        Table bigTable = new Table(smallTables);
        System.out.println("merge----------------------------------------------------------------");
        System.out.println(bigTable);
    }
    //used for testing.

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * This method saves the table to the database.
     * @param fieldName the name of the field that is related to the table.
     * @note Use this method after any modifications to save it.
     * @note USe this method with small fields only.
     */
    public void saveTableInDB(String fieldName){
        FieldDB.getDB().writeCSVFileWithDate(this.tableName, fieldName,this);
    }
}

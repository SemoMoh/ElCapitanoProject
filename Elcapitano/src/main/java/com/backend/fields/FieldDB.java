package com.backend.fields;

import com.backend.helper.search.Reservation;
import com.backend.helper.table.ReservationTable;
import com.backend.helper.table.Table;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * This class used to interfere with the db stored in the system.
 * The class uses the singleton pattern so clients only use one instance of it, so no interference happens.
 *
 * @author Eslam Mohamed
 */
public class FieldDB {
    private static FieldDB DB;
    public static String pathBeforeAbs;
    /*
     * example: File located at ".../Database/Fields/No.1/11-2023.csv"
     * pathBeforeAbs: ".../Database/Fields/"
     */


    //private construct so no one can create an instance of it.
    private FieldDB() {
    }

    public static FieldDB getDB() {
        if (DB == null) {
            DB = new FieldDB();
        }
        return DB;
    }


    /**
     * A function that reads a CSV file from a specific location then returns a List String[] contains the table.
     * If the file does not exist, the function will create a new file with the needed path, that new file will contain
     * the default values of any table
     *
     * @param filePath The total path from the abs path, note: don't write the first backslash.
     * @return List of String[] : each String[] represents a row, and each String will represent a cell.
     */
    public Table readCSVFile(String filePath) throws IOException, CsvException {
        //get the date string from the file name to create the table.
        String[] parts = filePath.split("/");
        String fileName = parts[parts.length - 1];
        parts = fileName.split("\\.");

        // Check if the file exists first.
        /*
         * If not, create a table with the default values.
         * Then create a new file with the path that contains this default value.
         * Then return the default table.
         */
        File file = new File(this.pathBeforeAbs + filePath);
        if (!(file.exists())) {
            Table defaultTable = Table.createEmptyTable(parts[0]);
            this.writeCSVFile(filePath, defaultTable);
            return defaultTable;
        }

        /*
         * It the file was found, parse it and return it in the Table format.
         */

        //To store the result table.
        Table result = null;
        // Using openCSV library to read a CSV file.
        // The "windows-1256" is the encoding for writing and reading arabic characters.
        // The Charset.forName() method is used to map the names of an encoding into its constant.
        try (
                CSVReader reader = new CSVReader(
                        new InputStreamReader(new FileInputStream(file),
                                Charset.forName("windows-1256"))
                );
        ) {
            // read all the table and create the table object.
            List<String[]> fileData = reader.readAll();
            result = new Table(fileData, parts[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * A method that reads a csv file using the name of the field (E.g., "No.1"), and the date.
     *
     * @param date      The date in "month-year" format representing the table's data, where:
     *                  - The month is represented by two digits (e.g., "01" for January).
     *                  - The year is represented by four digits (e.g., "2023").
     * @param fieldName the number of the required field. Make sure to put it in this format: "No.1".
     * @return the required Table.
     * @note this method uses readCSVFile method.
     */
    public Table readCSVFileWithDate(String date, String fieldName) throws IOException, CsvException {
        String absPath = fieldName + "/" + date + ".csv";
        return this.readCSVFile(absPath);
    }

    /**
     * A method that writes a Table into a specific location of a CSV file.
     *
     * @param filePath The total path from the abs path, note: don't write the first backslash.
     * @param data     Data (in table format) to store in the CSV file.
     */
    public void writeCSVFile(String filePath, Table data) {
        // Using openCSV library to write a CSV file.
        // The "windows-1256" is the encoding for writing and reading arabic characters.
        // The Charset.forName() method is used to map the names of an encoding into its constant.

        try (
                CSVWriter writer = new CSVWriter(
                        new OutputStreamWriter(new FileOutputStream(this.pathBeforeAbs + filePath),
                                "windows-1256")
                );
        ) {
            // write all the table to the csv file.
            // the getTable method converts the List<List<String>> table type int List<String[]> type.
            if (data != null) {
                writer.writeAll(data.getTable());
            }
        } catch (IOException e) {
            // Due to some errors in writing, an IOException is thrown, so we need to trace the last thing
            // that has been written to the file and continue from there.
            e.printStackTrace();
        }

    }

    /**
     * A method that writes a Table into a specific location of a CSV file.
     *
     * @param date      The date in "month-year" format representing the table's data, where:
     *                  - The month is represented by two digits (e.g., "01" for January).
     *                  - The year is represented by four digits (e.g., "2023").
     * @param fieldName The number of the required field. Make sure to put it in this format: "No.1".
     * @param data      Data (in table format) to store in the CSV file.
     * @note this method uses writeCSVFile method.
     */
    public void writeCSVFileWithDate(String date, String fieldName, Table data) {
        String absPath = fieldName + "/" + date + ".csv";
        this.writeCSVFile(absPath, data);
    }

    public List<Reservation> readTXTFile(String path) throws IOException {
        Scanner scanner = null;
        List<Reservation> reservationList = new ArrayList<>();
        try {
            scanner = new Scanner(new File(path));
        } catch (FileNotFoundException e) {
            writeTXTFile(path,null);
            return reservationList;
        }
        while(scanner.hasNextLine()) {
            reservationList.add(new Reservation(scanner.nextLine()));
        }
        return reservationList;
    }

    public void writeTXTFile(String path, List<Reservation> reservations) throws IOException {
        FileWriter writer;
        try {
            writer = new FileWriter(new File(pathBeforeAbs+path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(reservations == null){
            writer.write("");
            return;
        }
        for(Reservation reservation : reservations){
            writer.write(reservation.toString() + "\n");
        }
    }


    // for testing
    public static void main(String[] args) throws IOException, CsvException {
        FieldDB d = FieldDB.getDB();
        Table data = d.readCSVFileWithDate("11-2023", "No.1");
        System.out.println(data);
        d.writeCSVFileWithDate("11-2023", "No.1", data);
    }
}

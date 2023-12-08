package com.backend.accounts;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Report {
    private static final String PATH = "";
    private String name;
    private XSSFSheet incomeSheet;
    private XSSFSheet expenseSheet;
    private XSSFSheet totalSheet;
    private XSSFSheet ledgerSheet;
    private XSSFWorkbook report;

    private ArrayList<String> expensesList;
    private ArrayList<String> incomesList;
    private String srcFile;
    private String date;

    private int totalIncomeOps;
    private int totalExpensesOps;

    public Report(String srcFile, String date, String name) {
        report = new XSSFWorkbook();
        this.srcFile = srcFile;
        this.date = date;
        this.name = name;
        incomeSheet = report.createSheet("الإيرادات");
        expenseSheet = report.createSheet("المصروفات");
        totalSheet = report.createSheet("الإجمالي");
        ledgerSheet = report.createSheet("الجداول");

        expenseSheet.setColumnWidth(0, 20 * 256);
        expenseSheet.setColumnWidth(1, 20 * 256);
        expenseSheet.setColumnWidth(2, 20 * 256);
        expenseSheet.setColumnWidth(3, 20 * 256);
        expenseSheet.setColumnWidth(4, 20 * 256);
        expenseSheet.setColumnWidth(5, 60 * 256);
        expenseSheet.setColumnWidth(6, 20 * 256);

        incomeSheet.setColumnWidth(0, 20 * 256);
        incomeSheet.setColumnWidth(1, 20 * 256);
        incomeSheet.setColumnWidth(2, 20 * 256);
        incomeSheet.setColumnWidth(3, 20 * 256);
        incomeSheet.setColumnWidth(4, 20 * 256);
        incomeSheet.setColumnWidth(5, 60 * 256);
        incomeSheet.setColumnWidth(6, 20 * 256);

        totalSheet.setColumnWidth(0, 50 * 256);
        totalSheet.setColumnWidth(1, 50 * 256);
        totalSheet.setColumnWidth(2, 50 * 256);
        totalSheet.setColumnWidth(3, 50 * 256);

        ledgerSheet.setColumnWidth(0, 20 * 256);
        ledgerSheet.setColumnWidth(1, 20 * 256);
        ledgerSheet.setColumnWidth(2, 20 * 256);
        ledgerSheet.setColumnWidth(3, 20 * 256);
        ledgerSheet.setColumnWidth(4, 20 * 256);
        ledgerSheet.setColumnWidth(5, 60 * 256);
        ledgerSheet.setColumnWidth(6, 20 * 256);

        expensesList = new ArrayList<>();
        incomesList = new ArrayList<>();
    }

    public ArrayList<String> loadExpenses(String fileName, String date) throws IOException {
        FileReader reader = new FileReader("Expenses Sheet " + fileName);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line;
        if (expensesList.size() == 0) {
            while ((line = bufferedReader.readLine()) != null) {
                expensesList.add(line);
                if (line.equals("closed " + date))
                    break;
            }
        }
        return expensesList;
    }

    public ArrayList<String> loadIncomes(String fileName, String date) throws IOException {
        FileReader reader = new FileReader("Income Sheet " + fileName);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line;
        if (incomesList.size() == 0) {
            while ((line = bufferedReader.readLine()) != null) {
                incomesList.add(line);
                if (line.equals("closed " + date))
                    break;
            }
        }
        return incomesList;
    }

    public void createExpenseSheet() {
        Row header = expenseSheet.createRow(0);
        CellStyle headerStyle = report.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Cell[] headerCells = new Cell[7];
        for (int i = 0; i < headerCells.length; i++) {
            headerCells[i] = header.createCell(i);
            headerCells[i].setCellStyle(headerStyle);
        }
        headerCells[0].setCellValue("اليوم");
        headerCells[1].setCellValue("الوقت");
        headerCells[2].setCellValue("المعاملة");
        headerCells[3].setCellValue("النوع");
        headerCells[4].setCellValue("المبلغ");
        headerCells[5].setCellValue("الوصف");
        headerCells[6].setCellValue("المستخدم");


        int j = 0;
        for (int i = 0; i < expensesList.size(); i++) {
            if (expensesList.get(i).equals("closed " + date)) {
                break;
            } else {
                if (expensesList.get(i).startsWith("closed")) {
                    continue;
                } else {
                    String[] expense = expensesList.get(i).split(",");
                    j++;
                    Row row = expenseSheet.createRow(j);
                    Cell[] cells = new Cell[7];
                    for (int k = 0; k < cells.length; k++) {
                        if (k == 4) {
                            cells[k] = row.createCell(k);
                            cells[k].setCellValue(Double.parseDouble(expense[k]));
                        } else {
                            cells[k] = row.createCell(k);
                            cells[k].setCellValue(expense[k]);
                        }
                    }
                }
            }
        }

        Row sumRow = expenseSheet.createRow(j + 2);
        Cell sumCell = sumRow.createCell(0);
        sumCell.setCellValue("المجموع");
        Cell sumCellValue = sumRow.createCell(1);
        sumCellValue.setCellFormula("SUM(E2:E" + (j + 1) + ")");
        totalExpensesOps = j;
        Cell noOfExpenses = sumRow.createCell(2);
        noOfExpenses.setCellValue("مجموع العمليات");
        Cell noOfExpensesValue = sumRow.createCell(3);
        noOfExpensesValue.setCellValue(totalExpensesOps);

        CellStyle sumStyle = report.createCellStyle();
        sumStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        sumStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font font = report.createFont();
        font.setColor(IndexedColors.WHITE.getIndex());
        sumStyle.setFont(font);
        sumCell.setCellStyle(sumStyle);
        noOfExpenses.setCellStyle(sumStyle);

        CellStyle sumValueStyle = report.createCellStyle();
        sumValueStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        sumValueStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        sumCellValue.setCellStyle(sumValueStyle);
        noOfExpensesValue.setCellStyle(sumValueStyle);

    }

    public void createIncomeSheet() {
        Row header = incomeSheet.createRow(0);
        CellStyle headerStyle = report.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Cell[] headerCells = new Cell[7];
        for (int i = 0; i < headerCells.length; i++) {
            headerCells[i] = header.createCell(i);
            headerCells[i].setCellStyle(headerStyle);
        }
        headerCells[0].setCellValue("اليوم");
        headerCells[1].setCellValue("الوقت");
        headerCells[2].setCellValue("المعاملة");
        headerCells[3].setCellValue("النوع");
        headerCells[4].setCellValue("المبلغ");
        headerCells[5].setCellValue("الوصف");
        headerCells[6].setCellValue("المستخدم");


        int j = 0;
        for (int i = 0; i < incomesList.size(); i++) {
            if (incomesList.get(i).equals("closed " + date)) {
                break;
            } else {
                if (incomesList.get(i).startsWith("closed")) {
                    continue;
                } else {
                    String[] income = incomesList.get(i).split(",");
                    j++;
                    Row row = incomeSheet.createRow(j);
                    Cell[] cells = new Cell[7];
                    for (int k = 0; k < cells.length; k++) {
                        if (k == 4) {
                            cells[k] = row.createCell(k);
                            cells[k].setCellValue(Double.parseDouble(income[k]));
                        } else {
                            cells[k] = row.createCell(k);
                            cells[k].setCellValue(income[k]);
                        }
                    }
                }
            }
        }

        Row sumRow = incomeSheet.createRow(j + 2);
        Cell sumCell = sumRow.createCell(0);
        sumCell.setCellValue("المجموع");
        Cell sumCellValue = sumRow.createCell(1);
        sumCellValue.setCellFormula("SUM(E2:E" + (j + 1) + ")");
        totalIncomeOps = j;
        Cell noOfOpsTxt = sumRow.createCell(2);
        noOfOpsTxt.setCellValue("مجموع العمليات");
        Cell noOfOps = sumRow.createCell(3);
        noOfOps.setCellValue(totalIncomeOps);

        CellStyle sumStyle = report.createCellStyle();
        sumStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        sumStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font font = report.createFont();
        font.setColor(IndexedColors.WHITE.getIndex());
        sumStyle.setFont(font);
        sumCell.setCellStyle(sumStyle);
        noOfOpsTxt.setCellStyle(sumStyle);

        CellStyle sumValueStyle = report.createCellStyle();
        sumValueStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        sumValueStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        sumCellValue.setCellStyle(sumValueStyle);
        noOfOps.setCellStyle(sumValueStyle);
    }

    public void createTotalSheet() {
        Row row1 = totalSheet.createRow(0);
        Row row2 = totalSheet.createRow(1);
        Row row3 = totalSheet.createRow(2);
        Cell[] incomeCells = {row1.createCell(0), row1.createCell(1)};
        Cell[] expenseCells = {row2.createCell(0), row2.createCell(1)};
        Cell[] totalCells = {row3.createCell(0), row3.createCell(1)};

        incomeCells[0].setCellValue("مجموع الإيرادات");
        incomeCells[1].setCellFormula("SUM(الإيرادات!E2:E" + (totalIncomeOps + 1) + ")");

        expenseCells[0].setCellValue("مجموع المصروفات");
        expenseCells[1].setCellFormula("SUM(المصروفات!E2:E" + (totalExpensesOps + 1) + ")");

        totalCells[0].setCellValue("إجمالي الصافي");
        totalCells[1].setCellFormula("B1-B2");

        CellStyle style1 = report.createCellStyle();
        style1.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        style1.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font font = report.createFont();
        font.setColor(IndexedColors.WHITE.getIndex());
        style1.setFont(font);
        incomeCells[0].setCellStyle(style1);

        expenseCells[0].setCellStyle(style1);

        totalCells[0].setCellStyle(style1);


        CellStyle style2 = report.createCellStyle();
        style2.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        style2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        incomeCells[1].setCellStyle(style2);

        expenseCells[1].setCellStyle(style2);

        totalCells[1].setCellStyle(style2);

    }

    public void createLedgerSheet() throws IOException {
        String[] kinds = readKinds();

        CellStyle tableNameStyle = report.createCellStyle();
        tableNameStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
        tableNameStyle.setFillPattern(FillPatternType.BRICKS);
        tableNameStyle.setAlignment(HorizontalAlignment.CENTER);
        tableNameStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle sumStyle = report.createCellStyle();
        sumStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        sumStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        int rowCount = 0;
        int totalRowCount = 4;


        for (int i = 0; i < kinds.length; i++) {
            List<String> incomeAccount = readAccount(incomesList, kinds[i]);
            Row tableNameRow = ledgerSheet.createRow(rowCount);
            Cell cell = tableNameRow.createCell(0);
            cell.setCellValue(kinds[i] + " إيرادات");
            cell.setCellStyle(tableNameStyle);
            ledgerSheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 0, 6));
            rowCount++;

            Row headerRow1 = ledgerSheet.createRow(rowCount);
            CellStyle headerStyle = report.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Cell[] headerCells1 = new Cell[7];
            for (int l = 0; l < headerCells1.length; l++) {
                headerCells1[l] = headerRow1.createCell(l);
                headerCells1[l].setCellStyle(headerStyle);
            }
            headerCells1[0].setCellValue("اليوم");
            headerCells1[1].setCellValue("الوقت");
            headerCells1[2].setCellValue("المعاملة");
            headerCells1[3].setCellValue("النوع");
            headerCells1[4].setCellValue("المبلغ");
            headerCells1[5].setCellValue("الوصف");
            headerCells1[6].setCellValue("المستخدم");
            rowCount++;

            int incomeInit = rowCount;
            for (int j = 0; j < incomeAccount.size(); j++) {
                Row row = ledgerSheet.createRow(rowCount);
                String[] income = incomeAccount.get(j).split(",");

                Cell[] incomeCells = new Cell[7];
                for (int k = 0; k < incomeCells.length; k++) {
                    incomeCells[k] = row.createCell(k);
                    if (k == 4) {
                        incomeCells[k] = row.createCell(k);
                        incomeCells[k].setCellValue(Double.parseDouble(income[k]));
                    } else {
                        incomeCells[k] = row.createCell(k);
                        incomeCells[k].setCellValue(income[k]);
                    }
                }
                rowCount++;
            }
            int incomeEnd = rowCount;
            Row sumIncome = ledgerSheet.createRow(rowCount);
            Cell sumCell = sumIncome.createCell(4);
            sumCell.setCellStyle(sumStyle);
            sumCell.setCellFormula("SUM(E" + incomeInit + ":E" + incomeEnd + ")");
            rowCount += 2;


            List<String> expenseAccount = readAccount(expensesList, kinds[i]);
            Row row2 = ledgerSheet.createRow(rowCount);
            Cell cell1 = row2.createCell(0);
            cell1.setCellValue(kinds[i] + " مصروفات");
            cell1.setCellStyle(tableNameStyle);
            ledgerSheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 0, 6));
            rowCount++;

            Row headerRow2 = ledgerSheet.createRow(rowCount);
            Cell[] headerCells2 = new Cell[7];
            for (int l = 0; l < headerCells2.length; l++) {
                headerCells2[l] = headerRow2.createCell(l);
                headerCells2[l].setCellStyle(headerStyle);
            }
            headerCells2[0].setCellValue("اليوم");
            headerCells2[1].setCellValue("الوقت");
            headerCells2[2].setCellValue("المعاملة");
            headerCells2[3].setCellValue("النوع");
            headerCells2[4].setCellValue("المبلغ");
            headerCells2[5].setCellValue("الوصف");
            headerCells2[6].setCellValue("المستخدم");
            rowCount++;

            int expenseInit = rowCount;
            for (int j = 0; j < expenseAccount.size(); j++) {
                Row row = ledgerSheet.createRow(rowCount);
                String[] expense = expenseAccount.get(j).split(",");

                Cell[] expenseCells = new Cell[7];
                for (int k = 0; k < expenseCells.length; k++) {
                    expenseCells[k] = row.createCell(k);
                    if (k == 4) {
                        expenseCells[k] = row.createCell(k);
                        expenseCells[k].setCellValue(Double.parseDouble(expense[k]));
                    } else {
                        expenseCells[k] = row.createCell(k);
                        expenseCells[k].setCellValue(expense[k]);
                    }
                }
                rowCount++;
            }
            int expenseEnd = rowCount;
            Row sumExpense = ledgerSheet.createRow(rowCount);
            Cell sumCell2 = sumExpense.createCell(4);
            sumCell2.setCellStyle(sumStyle);
            sumCell2.setCellFormula("SUM(E" + expenseInit + ":E" + expenseEnd + ")");
            rowCount += 2;

            Row incomeTotal = totalSheet.createRow(totalRowCount);
            totalRowCount++;
            Cell incomeTotalCell = incomeTotal.createCell(0);
            incomeTotalCell.setCellStyle(headerStyle);

            incomeTotalCell.setCellValue("مجموع إيرادات " + kinds[i]);
            Cell incomeTotalValue = incomeTotal.createCell(1);
            incomeTotalValue.setCellStyle(sumStyle);
            incomeTotalValue.setCellFormula("الجداول!" + "E" + (incomeEnd + 1));

            Row expenseTotal = totalSheet.createRow(totalRowCount);
            totalRowCount++;
            Cell expenseTotalCell = expenseTotal.createCell(0);
            expenseTotalCell.setCellStyle(headerStyle);

            expenseTotalCell.setCellValue("مجموع مصروفات " + kinds[i]);
            Cell expenseTotalValue = expenseTotal.createCell(1);
            expenseTotalValue.setCellStyle(sumStyle);
            expenseTotalValue.setCellFormula("الجداول!" + "E" + (expenseEnd + 1));
            totalRowCount++;
        }
    }

    private List<String> readAccount(List<String> list, String kind) {
        List<String> result = new ArrayList<String>();
        for (int i = 0; i < list.size(); i++) {
            String s = list.get(i);
            if (!s.startsWith("close")) {
                String[] parts = s.split(",");
                if (parts[3].equals(kind)) {
                    result.add(s);
                }
            }
        }
        return result;
    }

    public void createReport() throws IOException {
        loadExpenses(srcFile, date);
        loadIncomes(srcFile, date);
        createIncomeSheet();
        createExpenseSheet();
        createTotalSheet();
        createLedgerSheet();
        write();
    }

    private void write() throws IOException {
        File file = new File(PATH + name + ".xlsx");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        report.write(fileOutputStream);
    }

    private String[] readKinds() throws IOException {
        FileReader reader = new FileReader("kinds.csv");
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line = bufferedReader.readLine();
        return line.split(",");
    }
}

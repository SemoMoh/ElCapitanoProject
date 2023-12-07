package com.backend.helper.table;

import java.util.List;

public class FieldTable{
    List<Table> dayByDayTable;
    String date;
    public FieldTable(String month_year){
        this.date = month_year;
    }
}

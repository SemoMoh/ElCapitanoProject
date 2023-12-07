package com.backend.helper.search;

public class Reservation {
    private String name;
    private String field;
    private String mobile;

    private String date;
    private int day;
    private int hour;

    public Reservation(String name, String field, String mobile, String date, int day, int hour) {
        this.name = name;
        this.field = field;
        this.mobile = mobile;
        this.date = date;
        this.day = day;
        this.hour = hour;
    }

    public Reservation(String reservation) {
        String[] parts = reservation.split("=");
        this.name = parts[1];
        this.field = parts[3];
        this.mobile = parts[5];
        this.date = parts[7];
        this.day = Integer.parseInt(parts[9]);
        this.hour = Integer.parseInt(parts[11].split("\n")[0]);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    @Override
    public String toString() {
        return "Reservation:" + "name=" + name + "= field=" + field +
                "= mobile=" + mobile + "= date=" + date + "= day=" + day + "= hour=" + hour;
    }

    public static void main(String[] args) {
        Reservation r1 = new Reservation("me", "No1", "5446", "12-2023", 5, 3);
        System.out.println(r1);
        Reservation r2 = new Reservation(r1.toString() + "\n");
        System.out.println(r2);
    }
}

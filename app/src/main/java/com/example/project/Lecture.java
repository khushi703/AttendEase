package com.example.project;

public class Lecture {
    private String name;
    private String info;
    private String date;
    public Lecture(String name, String info, String date)
    {
        this.name = name;
        this.info = info;
        this.date=date;
    }

    public String getName() {
        return name;
    }

    public String getInfo() {
        return info;
    }
    // Getter for date
    public String getDate() {
        return date;
    }

}

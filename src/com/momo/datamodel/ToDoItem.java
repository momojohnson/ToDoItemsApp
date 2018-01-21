package com.momo.datamodel;

import java.time.LocalDate;

public class ToDoItem {

    private String description; // description text
    private String details; // details text
    private LocalDate date; // date variable

    public ToDoItem(String description, String details, LocalDate date) {
        this.description = description;
        this.details = details;
        this.date = date;
    }

    // Returns description
    public String getDescription() {
        return description;
    }

    // set description
    public void setDescription(String description) {
        this.description = description;
    }

    // Returns details
    public String getDetails() {
        return details;
    }

    // sets details
    public void setDetails(String details) {
        this.details = details;
    }

    // Returns date
    public LocalDate getDate() {
        return date;
    }

    // Sets the date
    public void setDate(LocalDate date) {
        this.date = date;
    }

    // Override object to string method. Returns only description
    @Override
    public String toString() {
        return getDescription();
    }
}

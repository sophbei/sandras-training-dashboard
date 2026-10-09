package de.hallberg;


import java.time.LocalDate;

public class OverdueTrainingRow {
    private String employeeName;
    private String trainingTitle;
    private LocalDate dueDate;
    private String location;
     private long overdueDays;
     private boolean mandatory;



    public OverdueTrainingRow (String employeeName, String trainingTitle, LocalDate dueDate, String location, long overdueDays, boolean mandatory){
        this.employeeName = employeeName;
        this.trainingTitle = trainingTitle;
        this.dueDate = dueDate;
        this.location = location;
        this.overdueDays = overdueDays;
        this.mandatory = mandatory;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public String getTrainingTitle() {
        return trainingTitle;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public String getLocation() {
        return location;
    }

    public long getOverdueDays() {
        return overdueDays;
    }

    public boolean isMandatory() {
        return mandatory;
    }
}

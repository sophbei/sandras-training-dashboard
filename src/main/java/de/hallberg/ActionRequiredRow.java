package de.hallberg;

public class ActionRequiredRow {

    private final String category;
    private final String employeeName;
    private final String trainingTitle;
    private final String description;
    private final String employeeId;
    private final String department;
    private final String location;

    public ActionRequiredRow(
            String category,
            String employeeName,
            String trainingTitle,
            String description) {

        this.category = category;
        this.employeeName = employeeName;
        this.trainingTitle = trainingTitle;
        this.description = description;
        this.employeeId = "";
        this.department = "";
        this.location = "";
    }
    public ActionRequiredRow(
            String category,
            String employeeName,
            String trainingTitle,
            String description,
            String employeeId,
            String department,
            String location) {

        this.category = category;
        this.employeeName = employeeName;
        this.trainingTitle = trainingTitle;
        this.description = description;
        this.employeeId = employeeId;
        this.department = department;
        this.location = location;
    }

    public String getCategory() {
        return category;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public String getTrainingTitle() {
        return trainingTitle;
    }

    public String getDescription() {
        return description;
    }
    public String getEmployeeId() {
        return employeeId;
    }

    public String getDepartment() {
        return department;
    }

    public String getLocation() {
        return location;
    }
}

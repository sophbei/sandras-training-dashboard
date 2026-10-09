package de.hallberg;

public class EmployeeOverdueRow {

    private String employeeName;
    private String email;
    private String department;
    private String location;
    private String managerName;
    private int overdueCount;

    public EmployeeOverdueRow(
            String employeeName,
            String email,
            String department,
            String location,
            String managerName,
            int overdueCount) {

        this.employeeName = employeeName;
        this.email = email;
        this.department = department;
        this.location = location;
        this.managerName = managerName;
        this.overdueCount = overdueCount;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public String getEmail() {
        return email;
    }

    public String getDepartment() {
        return department;
    }

    public String getLocation() {
        return location;
    }

    public String getManagerName() {
        return managerName;
    }

    public int getOverdueCount() {
        return overdueCount;
    }
}
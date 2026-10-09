package de.hallberg;

import java.time.LocalDate;

public class Employee {
    String id;
    String firstName;
    String lastName;
    String email;
    String department;
    String location;
    String jobTitle;
    String  managerId;
    LocalDate hireDate;

    public Employee(String id, String firstName, String lastName, String email, String department, String location, String jobTitle, String managerId, LocalDate hireDate){
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        String dept = department == null ? "" : department.strip();
        this.department = dept.isEmpty()
                ? dept
                : dept.substring(0, 1).toUpperCase() + dept.substring(1);
        this.location = location;
        this.jobTitle = jobTitle;
        this.managerId = managerId;
        this.hireDate = hireDate;

    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public String getManagerId() {
        return managerId;
    }

    public String getLocation() {
        return location;
    }
    public String getEmail(){ return email; }

    public String getDepartment() {
        return department;
    }
}

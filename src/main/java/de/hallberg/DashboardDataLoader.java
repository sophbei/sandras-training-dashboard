package de.hallberg;

import model.Assignment;
import java.io.IOException;

import java.util.List;


public class DashboardDataLoader {
    public DashboardData loadData()throws IOException {
        EmployeeCsvReader reader = new EmployeeCsvReader();
        TrainingCvsReader treader = new TrainingCvsReader();
        AssignmentCvsReader areader = new AssignmentCvsReader();


        List<Employee> employees = reader.readEmployees(
                "data/employees.csv"
        );
        List<Training> trainings = treader.readTrainings("data/trainings.csv");
        List<Assignment> assignments = areader.readAssignments("data/assignments.csv");
        return new DashboardData(employees, trainings, assignments);
    }
}

package de.hallberg;

import java.util.List;
import model.Assignment;
public class DashboardData {


        private List<Assignment> assignments;
         private List<Training> trainings;
         private List<Employee> employees;
public DashboardData(List <Employee> employees, List<Training> trainings, List<Assignment> assignments ){
    this.employees = employees;
    this.trainings = trainings;
    this.assignments = assignments;
}

    public List<Assignment> getAssignments() {
        return assignments;
    }

    public List<Training> getTrainings() {
        return trainings;
    }

    public List<Employee> getEmployees() {
        return employees;
    }
}


package de.hallberg;

import java.io.IOException;
import java.util.List;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

import model.Assignment;

public class Main {
    public static void main(String[] args) throws IOException {
        DashboardDataLoader loader = new DashboardDataLoader();
        DashboardData data = loader.loadData();

        SandrasService service = new SandrasService();
        EnumMap<ComplianceStatus, Integer> newCounts = service.calculateStatusCounts(
                data.getAssignments(),
                data.getTrainings(),
                LocalDate.of(2026, 11, 2)
        );

        EmployeeCsvReader reader = new EmployeeCsvReader();
       TrainingCvsReader treader = new TrainingCvsReader();
       AssignmentCvsReader areader = new AssignmentCvsReader();

        List<Employee> employees = reader.readEmployees("C:\\Users\\Sophie\\IdeaProjects\\TalessioSandra\\CSV\\employees.csv");
        List<Training> trainings = treader.readTrainings("C:\\Users\\Sophie\\IdeaProjects\\TalessioSandra\\CSV\\trainings.csv");
        List<Assignment> assignments = areader.readAssignments("C:\\Users\\Sophie\\IdeaProjects\\TalessioSandra\\CSV\\assignments.csv");
        System.out.print("Employees: " + employees.size());
        System.out.print("Trainings: " + trainings.size());
         System.out.print("Assignments: " + assignments.size());



        EnumMap<ComplianceStatus, Integer> testCounts =
                service.calculateStatusCounts(
                        assignments,
                        trainings,
                        LocalDate.of(2026, 11, 2)
                );



        EnumMap<ComplianceStatus, Integer> counts =
                new EnumMap<>(ComplianceStatus.class);
        HashMap<String, Integer> overdueByEmployee = new HashMap<>();
        for (Assignment assignment : assignments) {

            Training training = service.findTrainingById(
                    assignment.getTrainingId(),
                    trainings
            );
            ComplianceStatus status = service.calculateComplianceStatus(assignment, training, LocalDate.of(2026, 11, 2));

               counts.put(status, counts.getOrDefault(status , 0)+1);
            if (status == ComplianceStatus.OVERDUE) {
                overdueByEmployee.put(assignment.getEmployeeId(), overdueByEmployee.getOrDefault(assignment.getEmployeeId(), 0)+1);
            }

                }
        List<Map.Entry<String, Integer>> overdueList =
                new ArrayList<>(overdueByEmployee.entrySet());
        overdueList.sort(
                Map.Entry.<String, Integer>comparingByValue().reversed()
        );
        HashMap<String, Integer> overdueByManager = new HashMap<>();
        HashMap<String, Set<String>> affectedEmployeesByManager =
                new HashMap<>();
        for (Map.Entry<String, Integer> entry : overdueList) {
            Employee employee = service.findEmployeeById(entry.getKey(), employees);
            Employee manager = service.findEmployeeById(employee.getManagerId(), employees);
            overdueByManager.put(employee.getManagerId(), overdueByManager.getOrDefault(employee.getManagerId(), 0)+ entry.getValue());
            affectedEmployeesByManager
                    .computeIfAbsent(employee.getManagerId(), key -> new HashSet<>())
                    .add(employee.getId());
        }
        List<Map.Entry<String, Integer>> overdueManagerList =
                new ArrayList<>(overdueByManager.entrySet());
        overdueManagerList.sort(
                Map.Entry.<String, Integer>comparingByValue().reversed());
        for (Map.Entry<String, Integer> entry : overdueManagerList) {

            Employee manager = service.findEmployeeById(entry.getKey(), employees);
            if (manager == null) {
            } else {


            }

            //System.out.println(manager.firstName+ " " + manager.lastName + " | " + "Overdue im Team: " + entry.getValue());
        }
        HashMap<String, Integer> test = service.calculateOverdueByEmployee(
                assignments,
        trainings,
        LocalDate.of(2026,11,2)
);



    }}
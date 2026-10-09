package de.hallberg;

import model.Assignment;

import java.util.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public class SandrasService {
    private static final Map<String, String> GROUP_ALIASES = Map.of(
            "f&e", "forschung & entwicklung",
            "qm", "qualitätsmanagement"
    );
    public static final long LEGACY_THRESHOLD_DAYS = 365;

    public Employee findEmployeeById(String employeeId, List<Employee> employees){
        for(Employee employee : employees) {
            if ((employee.getId()).equals(employeeId)) {
                return employee;
            }

        }return null;
    }

 public Training findTrainingById (String trainingId, List <Training> trainings){
        for(Training training : trainings){
            if ((training.getTrainingId()).equals(trainingId)){
                return training;
            }
        }
        return null;
 }
public ComplianceStatus calculateComplianceStatus (Assignment assignment, Training training, LocalDate referenceDate){
       LocalDate dueSoonLimit = referenceDate.plusDays(30);
boolean completed = "Abgeschlossen".equals(assignment.getStatus());
if (!completed) {
    if (assignment.completionDate == null) {
        if (assignment.getDueDate() == null) {
            return ComplianceStatus.MISSING_DUE_DATE;
        }
        if ((assignment.getDueDate()).isBefore((referenceDate))) {
            return ComplianceStatus.OVERDUE;
        }
        if ((assignment.getDueDate().equals(referenceDate))) {
            return ComplianceStatus.DUE_TODAY;
        }
        if ((assignment.getDueDate()).isBefore(dueSoonLimit) || (assignment.getDueDate()).equals(dueSoonLimit)) {
            return ComplianceStatus.DUE_SOON;
        }
        return ComplianceStatus.OPEN;
    }
}
if (assignment.getCompletionDate() == null){
    return ComplianceStatus.COMPLETED_WITHOUT_DATE;
}

            if (training.getValidityMonths() == null){
            return ComplianceStatus.VALID;

        }
        LocalDate expiryDate = assignment.completionDate.plusMonths(training.getValidityMonths());
        if ((expiryDate).isBefore(referenceDate) || (expiryDate).equals(referenceDate)) {
            return ComplianceStatus.EXPIRED;
        }

            return ComplianceStatus.VALID;
        }
        public EnumMap<ComplianceStatus, Integer> calculateStatusCounts (List<Assignment> assignments, List<Training> trainings, LocalDate referenceDate){
            EnumMap<ComplianceStatus, Integer> counts =
                    new EnumMap<>(ComplianceStatus.class);
            for (Assignment assignment : assignments) {


                    Training training = findTrainingById(
                            assignment.getTrainingId(),
                            trainings
                    );

                    ComplianceStatus status =
                            calculateComplianceStatus(
                                    assignment,
                                    training,
                                    referenceDate
                            );

                    counts.put(
                            status,
                            counts.getOrDefault(status, 0) + 1
                    );
                }




            return counts;
        }
        public HashMap<String, Integer> calculateOverdueByEmployee(List <Assignment> assignments, List <Training> trainings, LocalDate referenceDate){
         HashMap <String, Integer> overdueByEmployee = new HashMap<>();
            for (Assignment assignment : assignments) {
                Training training = findTrainingById(assignment.getTrainingId(), trainings);
                ComplianceStatus status = calculateComplianceStatus(assignment, training, referenceDate);
                if (status == ComplianceStatus.OVERDUE) {
                    String employeeId = assignment.getEmployeeId();
                    overdueByEmployee.put(
                            employeeId,
                            overdueByEmployee.getOrDefault(employeeId, 0) + 1);
                }}
            return overdueByEmployee;


                }
                public List<OverdueTrainingRow> calculateOverdueTrainingRows (List <Assignment> assignments, List <Training> trainings, List <Employee> employees, LocalDate referenceDate){
        List<OverdueTrainingRow> overdueTrainingRows = new ArrayList<>();
        for(Assignment assignment : assignments){
            Training training = findTrainingById(assignment.getTrainingId(), trainings);
            ComplianceStatus status = calculateComplianceStatus( assignment,  training, referenceDate);
            if(status == ComplianceStatus.OVERDUE){
                Employee employee = findEmployeeById(assignment.getEmployeeId(), employees);
                long overdueDays = ChronoUnit.DAYS.between(assignment.getDueDate(), referenceDate);

                OverdueTrainingRow row = new OverdueTrainingRow((employee.getFirstName() +" " +employee.getLastName()), training.getTitle(), assignment.getDueDate(), employee.getLocation(), overdueDays, training.isMandatory());
                overdueTrainingRows.add(row);

            }
        }
        return overdueTrainingRows;
                }
    public List<ManagerOverdueRow> calculateManagerOverdueRows(
            List<Assignment> assignments,
            List<Training> trainings,
            List<Employee> employees,
            LocalDate referenceDate) {
        HashMap<String, Integer> overdueByManager = new HashMap<>();
        HashMap<String, Set<String>> affectedEmployeesByManager = new HashMap<>();
        for(Assignment assignment : assignments){
            Training training = findTrainingById(assignment.getTrainingId(), trainings);
           ComplianceStatus status = calculateComplianceStatus(assignment, training, referenceDate);
            if (status == ComplianceStatus.OVERDUE){
                Employee employee = findEmployeeById(
                        assignment.getEmployeeId(),
                               employees
                        );
                String managerId = employee.getManagerId();
                if (managerId != null && !managerId.isEmpty()) {
                    Employee manager = findEmployeeById(managerId, employees);
                    overdueByManager.put(managerId, overdueByManager.getOrDefault(managerId, 0)+1);
                    if (!affectedEmployeesByManager.containsKey(managerId)) {
                        affectedEmployeesByManager.put(managerId, new HashSet<>());
                    }

                    affectedEmployeesByManager.get(managerId).add(employee.getId());

                }

            }
        }
        List <ManagerOverdueRow> managerRows = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : overdueByManager.entrySet()) {
            Employee manager = findEmployeeById(entry.getKey(), employees);
            int affectedEmployees = affectedEmployeesByManager.get(entry.getKey()).size();
            ManagerOverdueRow row = new ManagerOverdueRow(manager.getFirstName() + " " + manager.getLastName(), manager.getEmail(), affectedEmployees, entry.getValue());
            managerRows.add(row);
        }
        managerRows.sort(Comparator.comparingInt(ManagerOverdueRow::getOverdueCount).reversed());
return managerRows;

    }
    public int calculateOldOverdueCount(
            List<Assignment> assignments,
            List<Training> trainings,
            LocalDate referenceDate) {

        int count = 0;
        int oldDueDates = 0;

        for (Assignment assignment : assignments) {
            Training training = findTrainingById(
                    assignment.getTrainingId(), trainings);

            if (training == null) {
                continue;
            }

            ComplianceStatus status = calculateComplianceStatus(
                    assignment, training, referenceDate);
            if (assignment.getDueDate() != null
                    && assignment.getDueDate()
                    .isBefore(referenceDate.minusDays(LEGACY_THRESHOLD_DAYS))) {
                oldDueDates++;
            }
            if (status == ComplianceStatus.OVERDUE
                    && assignment.getDueDate()
                    .isBefore(referenceDate.minusDays(LEGACY_THRESHOLD_DAYS))) {
                count++;
            }
        }

        return count;
    }
    public List<TrainingOverdueRow> calculateTrainingOverdueRows(
            List<Assignment> assignments,
            List<Training> trainings,
            LocalDate referenceDate) {

        Map<String, Integer> overdueByTraining = new HashMap<>();
        Map<String, Integer> notStartedByTraining = new HashMap<>();


        for (Assignment assignment : assignments) {

            Training training = findTrainingById(
                    assignment.getTrainingId(), trainings);

            if (training == null) {
                continue;
            }

            ComplianceStatus status = calculateComplianceStatus(
                    assignment, training, referenceDate);

            if (status == ComplianceStatus.OVERDUE) {
                String trainingId = training.getTrainingId();
                if ("Zugewiesen".equals(assignment.getStatus())) {
                    notStartedByTraining.merge(trainingId, 1, Integer::sum);
                }
                overdueByTraining.put(
                        trainingId,
                        overdueByTraining.getOrDefault(trainingId, 0) + 1
                );


            }
        }

        List<TrainingOverdueRow> rows = new ArrayList<>();

        for (Training training : trainings) {
            String trainingId = training.getTrainingId();
            int overdueCount = overdueByTraining.getOrDefault(trainingId, 0);

            if (overdueCount > 0) {


                rows.add(new TrainingOverdueRow(
                        training.getTitle(),
                        notStartedByTraining.getOrDefault(trainingId, 0),
                        overdueCount
                ));
            }
        }

        rows.sort(
                Comparator.comparingInt(TrainingOverdueRow::getOverdueCount)
                        .reversed()
        );

        return rows;
    }
    public int calculateExpiringSoonCount(
            List<Assignment> assignments,
            List<Training> trainings,
            LocalDate referenceDate) {

        int count = 0;
        LocalDate limitDate = referenceDate.plusDays(60);

        for (Assignment assignment : assignments) {

            LocalDate completionDate = assignment.getCompletionDate();

            // Nur abgeschlossene Trainings berücksichtigen
            if (completionDate == null) {
                continue;
            }

            Training training = trainings.stream()
                    .filter(t -> t.getTrainingId().equals(assignment.getTrainingId()))
                    .findFirst()
                    .orElse(null);

            if (training == null || training.getValidityMonths() == null) {
                continue;
            }

            LocalDate expirationDate =
                    completionDate.plusMonths(training.getValidityMonths());

            // Gültigkeit endet innerhalb der nächsten 30 Tage
            if (expirationDate.isAfter(referenceDate)
                    && !expirationDate.isAfter(limitDate)) {
                count++;
            }
        }

        return count;
    }
    public List<ActionRequiredRow> calculateMissingMandatoryAssignments(
            List<Employee> employees,
            List<Training> trainings,
            List<Assignment> assignments) {

        List<ActionRequiredRow> results = new ArrayList<>();

        for (Employee employee : employees) {

            // Prüfen, ob überhaupt ein Pflichttraining zugewiesen ist
            boolean hasMandatoryAssignment = assignments.stream()
                    .anyMatch(assignment -> {

                        if (!assignment.getEmployeeId().equals(employee.getId())) {
                            return false;
                        }

                        Training assignedTraining = findTrainingById(
                                assignment.getTrainingId(),
                                trainings
                        );

                        return assignedTraining != null
                                && assignedTraining.isMandatory();
                    });

            // Mitarbeiter ohne jegliche Pflichttraining-Zuordnung
            if (!hasMandatoryAssignment) {
                results.add(new ActionRequiredRow(
                        "Keine Pflichttrainings zugewiesen",
                        employee.getFirstName() + " " + employee.getLastName(),
                        "Alle Pflichttrainings",
                        "Mitarbeiter hat keine Pflichttraining-Zuordnung",
                        employee.getId(),
                        employee.getDepartment(),
                        employee.getLocation()
                ));
                continue;
            }

            // Bisherige Prüfung einzelner fehlender Trainings
            for (Training training : trainings) {

                // Nur Pflichttrainings berücksichtigen
                if (!training.isMandatory()) {
                    continue;
                }

                String targetGroup = training.getTargetGroup();
                String department = employee.getDepartment();

                if (targetGroup == null) {
                    continue;
                }

                // Prüfen, ob das Training für den Mitarbeiter gilt
                boolean applies = targetGroup.trim().equalsIgnoreCase("Alle");

                if (!applies && department != null) {
                    String[] groups = targetGroup.split(",");

                    for (String group : groups) {
                        String normalized = group.trim().toLowerCase();
                        normalized = GROUP_ALIASES.getOrDefault(normalized, normalized);

                        if (normalized.equals(department.trim().toLowerCase())) {
                            applies = true;
                            break;
                        }
                    }
                }

                if (!applies) {
                    continue;
                }

                // Prüfen, ob bereits eine Zuordnung existiert
                boolean assignmentExists = assignments.stream()
                        .anyMatch(a ->
                                a.getEmployeeId().equals(employee.getId())
                                        && a.getTrainingId().equals(training.getTrainingId())
                        );

                if (!assignmentExists) {
                    results.add(new ActionRequiredRow(
                            "Fehlende Zuordnung",
                            employee.getFirstName() + " " + employee.getLastName(),
                            training.getTitle(),
                            "Pflichttraining nicht zugewiesen",
                            employee.getId(),
                            employee.getDepartment(),
                            employee.getLocation()
                    ));
                }
            }
        }
        long missingAssignments = results.stream()
                .filter(row -> "Fehlende Zuordnung".equals(row.getCategory()))
                .count();

        long employeesWithoutAssignments = results.stream()
                .filter(row -> "Keine Pflichttrainings zugewiesen"
                        .equals(row.getCategory()))
                .count();

        return results;
    }

}









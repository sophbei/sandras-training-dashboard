package de.hallberg;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import java.util.Comparator;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import model.Assignment;


@Controller
public class DashboardController {
    private final SandrasService service = new SandrasService();
    private final DashboardData data;
    public DashboardController() throws IOException {
        this.data = new DashboardDataLoader().loadData();
    }
    @GetMapping("/")
    public String home(
            @RequestParam(required = false) String location,
            @RequestParam (required = false) String department,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam (defaultValue = "true") boolean mandatoryOnly,
            @RequestParam (defaultValue = "0") int employeePage,
            @RequestParam(defaultValue = "0") int managerPage,
            @RequestParam(defaultValue = "0") int assignmentPage,
            Model model)  {


        LocalDate referenceDate = LocalDate.of(2026,11,2);
        List<Assignment> filteredAssignments = new ArrayList<>();
        for (Assignment assignment : data.getAssignments()) {

            Training training = service.findTrainingById(
                    assignment.getTrainingId(),
                    data.getTrainings()
            );

            Employee employee = service.findEmployeeById(
                    assignment.getEmployeeId(),
                    data.getEmployees()
            );

            // Pflichttrainingsfilter
            if (mandatoryOnly && !training.isMandatory()) {
                continue;
            }



            // Abteilungsfilter
            if (department != null && !department.isEmpty()
                    && !department.equals(employee.getDepartment())) {
                continue;
            }
//Standortfilter
            if (location != null && !location.isEmpty()
                    && !location.equals(employee.getLocation())) {
                continue;
            }
            filteredAssignments.add(assignment);
        }
        List<Employee> filteredEmployees = data.getEmployees().stream()
                .filter(employee ->
                        location == null
                                || location.isBlank()
                                || location.equals(employee.getLocation())
                )
                .filter(employee ->
                        department == null
                                || department.isBlank()
                                || department.equals(employee.getDepartment())
                )
                .toList();

        List<ActionRequiredRow> actionRequiredRows =
                service.calculateMissingMandatoryAssignments(
                        filteredEmployees,
                        data.getTrainings(),
                        data.getAssignments()
                );

        actionRequiredRows.sort(
                Comparator.comparing(
                        (ActionRequiredRow row) ->
                                !"Fehlende Zuordnung".equals(row.getCategory())
                ).thenComparing(
                        ActionRequiredRow::getEmployeeName,
                        String.CASE_INSENSITIVE_ORDER
                )
        );
        List<ActionRequiredRow> filteredActionRows = actionRequiredRows;

        int assignmentPageSize = 20;
        int assignmentTotal = filteredActionRows.size();

        int assignmentTotalPages = Math.max(
                1,
                (assignmentTotal + assignmentPageSize - 1) / assignmentPageSize
        );

        assignmentPage = Math.max(
                0,
                Math.min(assignmentPage, assignmentTotalPages - 1)
        );

        int assignmentStartIndex = assignmentPage * assignmentPageSize;
        int assignmentEndIndex = Math.min(
                assignmentStartIndex + assignmentPageSize,
                assignmentTotal
        );

        List<ActionRequiredRow> assignmentPageRows =
                filteredActionRows.subList(
                        assignmentStartIndex,
                        assignmentEndIndex
                );

        model.addAttribute("assignmentPageRows", assignmentPageRows);
        model.addAttribute("assignmentCurrentPage", assignmentPage);
        model.addAttribute("assignmentDisplayStart",
                assignmentTotal == 0 ? 0 : assignmentStartIndex + 1);
        model.addAttribute("assignmentEndIndex", assignmentEndIndex);
        model.addAttribute("assignmentTotal", assignmentTotal);
        model.addAttribute("actionRequiredRows", actionRequiredRows);
        long missingAssignmentCount = actionRequiredRows.stream()
                .filter(row -> "Fehlende Zuordnung".equals(row.getCategory()))
                .count();

        model.addAttribute("missingAssignmentCount", missingAssignmentCount);
        long noMandatoryCount = actionRequiredRows.stream()
                .filter(row -> "Keine Pflichttrainings zugewiesen"
                        .equals(row.getCategory()))
                .count();

        model.addAttribute("noMandatoryCount", noMandatoryCount);
        int oldOverdue = service.calculateOldOverdueCount(
                filteredAssignments,
                data.getTrainings(),
                referenceDate
        );
        List<TrainingOverdueRow> trainingOverdueRows =
                service.calculateTrainingOverdueRows(
                        filteredAssignments,
                        data.getTrainings(),
                        referenceDate
                );

        EnumMap<ComplianceStatus, Integer> counts = service.calculateStatusCounts(filteredAssignments, data.getTrainings(), referenceDate);
        HashMap<String, Integer> overdueByEmployee =
                service.calculateOverdueByEmployee(
                        filteredAssignments,
        data.getTrainings(),
                referenceDate);

        List<EmployeeOverdueRow> overdueRows = new ArrayList<>();



        for(Map.Entry<String, Integer> entry : overdueByEmployee.entrySet()) {
            Employee employee = service.findEmployeeById(entry.getKey(), data.getEmployees());
            String managerName = "Nicht zugeordnet";

            if (employee.getManagerId() != null
                    && !employee.getManagerId().isEmpty()) {

                Employee manager = service.findEmployeeById(
                        employee.getManagerId(),
                        data.getEmployees()
                );

                if (manager != null) {
                    managerName = manager.getFirstName() + " " + manager.getLastName();
                }
            }

            EmployeeOverdueRow row = new EmployeeOverdueRow(
                    employee.getFirstName() + " " + employee.getLastName(),
                    employee.getEmail(),
                    employee.getDepartment(),
                    employee.getLocation(),
                    managerName,
                    entry.getValue()
            );

            overdueRows.add(row);
        }
            Set<String> locations = new HashSet<>();

        for (Employee employee : data.getEmployees()) {
            locations.add(employee.getLocation());
        }
            Set<String> departments = new TreeSet<>();

            for (Employee employee : data.getEmployees()) {
                if (employee.getDepartment() != null) {
                    departments.add(employee.getDepartment());
                }
            }

            model.addAttribute("departments", departments);
            model.addAttribute("selectedDepartment", department);

        model.addAttribute("locations", locations);
        model.addAttribute("selectedLocation", location);
        overdueRows.sort(
                Comparator.comparingInt(EmployeeOverdueRow::getOverdueCount).reversed()
        );
        int employeePageSize = 10;
        employeePage = clampPage(employeePage, overdueRows.size(), employeePageSize);
        int employeeStartIndex = employeePage* employeePageSize;
        int employeeEndIndex = Math.min(employeeStartIndex + employeePageSize, overdueRows.size());
        List <EmployeeOverdueRow> employeePageRows = overdueRows.subList(employeeStartIndex, employeeEndIndex);

        List<OverdueTrainingRow> overdueTrainingRows =
                service.calculateOverdueTrainingRows(filteredAssignments, data.getTrainings(), data.getEmployees(), referenceDate);
        overdueTrainingRows.sort(
                Comparator.comparing(OverdueTrainingRow::isMandatory).reversed()
                        .thenComparing(r -> r.getOverdueDays() > SandrasService.LEGACY_THRESHOLD_DAYS)
                        .thenComparing(Comparator.comparingLong(OverdueTrainingRow::getOverdueDays).reversed()));

        List<ManagerOverdueRow> managerOverdueRows = service.calculateManagerOverdueRows(filteredAssignments, data.getTrainings(), data.getEmployees(), referenceDate);
        int managerPageSize = 10;
        managerPage = clampPage(managerPage, managerOverdueRows.size(), managerPageSize);
        int managerStartIndex = managerPage * managerPageSize;

        int managerEndIndex = Math.min(
                managerStartIndex + managerPageSize,
                managerOverdueRows.size()
        );

        List<ManagerOverdueRow> managerPageRows =
                managerOverdueRows.subList(
                        managerStartIndex,
                        managerEndIndex
                );


        int pageSize = 10;
        page = clampPage(page, overdueTrainingRows.size(), pageSize);
        int startIndex = page * pageSize;
        int endIndex = Math.min(startIndex + pageSize, overdueTrainingRows.size()
        );
        List <OverdueTrainingRow> pageRows = overdueTrainingRows.subList(startIndex, endIndex);
        model.addAttribute("filteredCount", overdueTrainingRows.size());

        Integer overdue = counts.getOrDefault(ComplianceStatus.OVERDUE, 0);
        Integer dueToday = counts.getOrDefault(ComplianceStatus.DUE_TODAY, 0);
        Integer dueSoon = counts.getOrDefault(ComplianceStatus.DUE_SOON, 0);

        model.addAttribute("overdue", overdue);
        model.addAttribute("dueToday", dueToday);
        model.addAttribute("dueSoon", dueSoon);
        model.addAttribute("completedWithoutDate",
                counts.getOrDefault(ComplianceStatus.COMPLETED_WITHOUT_DATE, 0));
        model.addAttribute("missingDueDate",
                counts.getOrDefault(ComplianceStatus.MISSING_DUE_DATE, 0));
        model.addAttribute("overdueRows", employeePageRows);
        model.addAttribute("overdueTrainingRows", pageRows);
        model.addAttribute("currentPage", page);
        model.addAttribute("displayStart",
                overdueTrainingRows.isEmpty() ? 0 : startIndex + 1);
        model.addAttribute("endIndex", endIndex);
        model.addAttribute("total", overdueTrainingRows.size());
        model.addAttribute("mandatoryOnly", mandatoryOnly);
        model.addAttribute("managerOverdueRows", managerPageRows);
        model.addAttribute("employeeCurrentPage", employeePage);
        model.addAttribute("employeeEndIndex", employeeEndIndex);
        model.addAttribute("employeeTotal", overdueRows.size());
        model.addAttribute("employeeDisplayStart",
                overdueRows.isEmpty() ? 0 : employeeStartIndex + 1);
        model.addAttribute("managerCurrentPage", managerPage);
        model.addAttribute("managerDisplayStart",
                managerOverdueRows.isEmpty() ? 0 : managerStartIndex + 1);
        model.addAttribute("managerEndIndex", managerEndIndex);
        model.addAttribute("managerTotal", managerOverdueRows.size());
        model.addAttribute("oldOverdue", oldOverdue);
        int expiringSoon = service.calculateExpiringSoonCount(
                filteredAssignments,
                data.getTrainings(),
                referenceDate
        );

        model.addAttribute("expiringSoon", expiringSoon);
        model.addAttribute("trainingOverdueRows", trainingOverdueRows);


        return "dashboard" ;

    }
    private static int clampPage(int page, int total, int size) {
        int maxPage = Math.max(0, (total - 1) / size);
        return Math.min(Math.max(page, 0), maxPage);
    }
}

package de.hallberg;

import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
public class EmployeeCsvReader {

    public List<Employee> readEmployees(String resourcePath) throws IOException {

        List<String> lines;

        try (InputStream inputStream = getClass()
                .getClassLoader()
                .getResourceAsStream(resourcePath)) {

            if (inputStream == null) {
                throw new IOException("CSV-Datei nicht gefunden: " + resourcePath);
            }

            try (var reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                lines = reader.lines().toList();
            }
        }

        List<Employee> employees = new ArrayList<>();

        for (int i = 1; i < lines.size(); i++) {
            String currentLine = lines.get(i);
            String[] values = currentLine.split(",", -1);

            String id = values[0];
            String firstName = values[1];
            String lastName = values[2];
            String email = values[3];
            String department = values[4];
            String location = values[5];
            String jobTitle = values[6];
            String managerId = values[7];
            LocalDate hireDate = LocalDate.parse(values[8]);

            Employee newEmployee = new Employee(
                    id, firstName, lastName, email,
                    department, location, jobTitle, managerId, hireDate
            );

            employees.add(newEmployee);
        }

        return employees;
    }
}

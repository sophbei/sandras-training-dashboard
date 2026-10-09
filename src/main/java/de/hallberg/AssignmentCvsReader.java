package de.hallberg;

import model.Assignment;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AssignmentCvsReader {

    public List<Assignment> readAssignments(String resourcePath) throws IOException {

        InputStream inputStream = getClass()
                .getClassLoader()
                .getResourceAsStream(resourcePath);

        if (inputStream == null) {
            throw new IOException("CSV-Datei nicht gefunden: " + resourcePath);
        }

        List<Assignment> assignments = new ArrayList<>();

        try (Reader reader = new InputStreamReader(
                inputStream, StandardCharsets.UTF_8);

             CSVParser parser = CSVFormat.DEFAULT
                     .builder()
                     .setHeader()
                     .setSkipHeaderRecord(true)
                     .get()
                     .parse(reader)) {

            for (CSVRecord record : parser) {

                String assignmentId = record.get("assignment_id");
                String employeeId = record.get("employee_id");
                String trainingId = record.get("training_id");

                LocalDate assignedDate = LocalDate.parse(
                        record.get("assigned_date")
                );

                String dueValue = record.get("due_date");

                LocalDate dueDate;

                if (dueValue.isBlank()) {
                    dueDate = null;
                } else {
                    dueDate = LocalDate.parse(dueValue);
                }

                String status = record.get("status");

                String completionValue = record.get("completion_date");

                LocalDate completionDate;

                if (completionValue.isBlank()) {
                    completionDate = null;
                } else {
                    completionDate = LocalDate.parse(completionValue);
                }

                Assignment newAssignment = new Assignment(
                        assignmentId,
                        employeeId,
                        trainingId,
                        assignedDate,
                        dueDate,
                        status,
                        completionDate
                );

                assignments.add(newAssignment);
            }
        }

        return assignments;
    }
}
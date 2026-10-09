package de.hallberg;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class TrainingCvsReader {

    public List<Training> readTrainings(String resourcePath) throws IOException {

        InputStream inputStream = getClass()
                .getClassLoader()
                .getResourceAsStream(resourcePath);

        if (inputStream == null) {
            throw new IOException("CSV-Datei nicht gefunden: " + resourcePath);
        }

        List<Training> trainings = new ArrayList<>();

        try (Reader reader = new InputStreamReader(
                inputStream, StandardCharsets.UTF_8);

             CSVParser parser = CSVFormat.DEFAULT
                     .builder()
                     .setHeader()
                     .setSkipHeaderRecord(true)
                     .get()
                     .parse(reader)) {

            for (CSVRecord record : parser) {

                String trainingId = record.get("training_id");
                String title = record.get("title");
                String category = record.get("category");

                boolean mandatory = Boolean.parseBoolean(
                        record.get("mandatory")
                );

                String validityValue = record.get("validity_months");

                Integer validityMonths;

                if (validityValue.isBlank()) {
                    validityMonths = null;
                } else {
                    validityMonths = Integer.parseInt(validityValue);
                }

                String targetGroup = record.get("target_group");

                int durationMinutes = Integer.parseInt(
                        record.get("duration_minutes")
                );

                String format = record.get("format");

                Training training = new Training(
                        trainingId,
                        title,
                        category,
                        mandatory,
                        validityMonths,
                        targetGroup,
                        durationMinutes,
                        format
                );

                trainings.add(training);
            }
        }

        return trainings;
    }
}
package de.hallberg;

public class TrainingOverdueRow {

    private final String trainingTitle;
    private final int notStartedCount;
    private final int overdueCount;

    public TrainingOverdueRow(
            String trainingTitle,
            int affectedEmployees,
            int overdueCount) {

        this.trainingTitle = trainingTitle;
        this.notStartedCount = affectedEmployees;
        this.overdueCount = overdueCount;
    }

    public String getTrainingTitle() {
        return trainingTitle;
    }

    public int getNotStartedCount() {
        return notStartedCount;
    }

    public int getOverdueCount() {
        return overdueCount;
    }
}

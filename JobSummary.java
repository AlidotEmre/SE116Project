public class JobSummary {
    private String jobID;
    private double expectedDuration;
    private double actualDuration;
    private double difference;
    private int actualStartTime;
    private int actualEndTime;

    public JobSummary(String jobID, double expectedDuration, double actualDuration, double difference, int actualStartTime, int actualEndTime) {
        this.jobID = jobID;
        this.expectedDuration = expectedDuration;
        this.actualDuration = actualDuration;
        this.difference = difference;
        this.actualStartTime = actualStartTime;
        this.actualEndTime = actualEndTime;
    }

    public String getJobID() {
        return jobID;
    }

    public double getExpectedDuration() {
        return expectedDuration;
    }

    public double getActualDuration() {
        return actualDuration;
    }

    public double getDifference() {
        return difference;
    }

    public int getActualStartTime() {
        return actualStartTime;
    }

    public int getActualEndTime() {
        return actualEndTime;
    }
}

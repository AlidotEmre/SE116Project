public class JobSummary {
    private String jobID;
    private double expectedDuration;
    private double actualDuration;
    private double difference;
    private double actualStartTime;
    private double actualEndTime;

    public JobSummary(String jobID, double expectedDuration, double actualDuration, double difference, double actualStartTime, double actualEndTime) {
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

    public double getActualStartTime() {
        return actualStartTime;
    }

    public double getActualEndTime() {
        return actualEndTime;
    }
}

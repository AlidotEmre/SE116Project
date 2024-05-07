public abstract class Job {
    private String jobId;
    private String jobType;
    private int startTime;
    private int duration;

    public Job(String jobId, String jobType, int startTime, int duration) {
        this.jobId = jobId;
        this.jobType = jobType;
        this.startTime = startTime;
        this.duration = duration;
    }

    public String getJobId() {
        return jobId;
    }

    public String getJobType() {
        return jobType;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getDuration() {
        return duration;
    }
    public void Deneme(){
        System.out.println("Deneme...");
        System.out.println("Deneme 2..");
    }

    // Abstract methods that must be implemented by subclasses
    public abstract void startNextTask();
    public abstract void updateJobStatus();
}
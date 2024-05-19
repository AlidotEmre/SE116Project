import java.time.LocalTime;
import java.util.*;

public class Job {
    private String jobID;
    private JobType jobType;
    private int startTime; // in minutes
    private int duration;
    private LocalTime deadline;
    private int currentTaskIndex;
    private double actualStartTime;
    private double actualEndTime;
    private boolean completed = false;
    private List<Task> waitingTasks = new ArrayList<>();

    public Job(String jobID, JobType jobType, int startTime, int duration, LocalTime deadline) {
        this.jobID = jobID;
        this.jobType = jobType;
        this.startTime = startTime;
        this.duration = duration;
        this.deadline = deadline;
        this.currentTaskIndex = 0;
        this.actualStartTime = -1;
        this.actualEndTime = -1;
    }

    public String getJobID() {
        return jobID;
    }

    public JobType getJobType() {
        return jobType;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getDuration() {
        return duration;
    }

    public LocalTime getDeadline() {
        return deadline;
    }

    public int getCurrentTaskIndex() {
        return currentTaskIndex;
    }

    public void incrementTaskIndex() {
        currentTaskIndex++;
    }

    public TaskType getNextTask() {
        if (currentTaskIndex < jobType.getTasks().size()) {
            return jobType.getTasks().get(currentTaskIndex);
        }
        return null;
    }

    public double getActualStartTime() {
        return actualStartTime;
    }

    public void setActualStartTime(double actualStartTime) {
        this.actualStartTime = actualStartTime;
    }

    public double getActualEndTime() {
        return actualEndTime;
    }

    public void setActualEndTime(double actualEndTime) {
        this.actualEndTime = actualEndTime;
    }

    public List<Task> getWaitingTasks() {
        return waitingTasks;
    }

    public void addWaitingTask(Task task) {
        waitingTasks.add(task);
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    @Override
    public String toString() {
        return "Job{" +
                "jobID='" + jobID + '\'' +
                ", jobType=" + jobType +
                ", startTime=" + startTime +
                ", duration=" + duration +
                ", deadline=" + deadline +
                '}';
    }
}

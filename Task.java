import java.util.Random;

public class Task {
    private String taskID;
    private String taskTypeID;
    private double duration;
    private double deadline;
    private int startTime; // in minutes
    private double taskSpeed;

    public Task(String taskID, String taskTypeID, double duration, double deadline, int startTime, double taskSpeed) {
        this.taskID = taskID;
        this.taskTypeID = taskTypeID;
        this.duration = duration;
        this.deadline = deadline;
        this.startTime = startTime;
        this.taskSpeed = taskSpeed;
    }

    public String getTaskID() {
        return taskID;
    }

    public String getTaskTypeID() {
        return taskTypeID;
    }

    public double getDuration() {
        return duration;
    }

    public double getDeadline() {
        return deadline;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public double getActualDuration() {
        return duration / taskSpeed;
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskID='" + taskID + '\'' +
                ", taskTypeID='" + taskTypeID + '\'' +
                ", duration=" + duration +
                ", deadline=" + deadline +
                ", startTime=" + startTime +
                ", taskSpeed=" + taskSpeed +
                '}';
    }
}

import java.util.Random;

public class Task {
    private String taskID;
    private String taskTypeID;
    private double duration;
    private double deadline;

    public Task(String taskID, String taskTypeID, double duration, double deadline) {
        this.taskID = taskID;
        this.taskTypeID = taskTypeID;
        this.duration = duration;
        this.deadline = deadline;
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


    public double getActualDuration(double speed, double variation) {
        Random random = new Random();
        double minSpeed = speed * (1 - variation);
        double maxSpeed = speed * (1 + variation);
        double actualSpeed = minSpeed + (maxSpeed - minSpeed) * random.nextDouble();
        return duration / actualSpeed;
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskID='" + taskID + '\'' +
                ", taskTypeID='" + taskTypeID + '\'' +
                ", duration=" + duration +
                ", deadline=" + deadline +
                '}';
    }

    public void setDeadline(double deadline) {
        this.deadline = deadline;
    }
}

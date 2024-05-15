import java.time.LocalTime;

public class Task {
    private TaskType type;
    private boolean isCompleted;
    private double defaultSize;
    private double executionSpeed;
    private long startTimeMillis;
    private long expectedEndTimeMillis;
    private LocalTime startTime;
    private LocalTime endTime;

    public Task(TaskType type, double defaultSize) {
        this.type = type;
        this.defaultSize = defaultSize;
        this.isCompleted = false;
    }

    public void startExecution(double speed) {
        if (isCompleted) {
            System.out.println("Task " + type.getId() + " is already completed.");
            return;
        }

        this.executionSpeed = speed;
        this.startTimeMillis = System.currentTimeMillis();
        if (defaultSize != 0) {
            this.expectedEndTimeMillis = this.startTimeMillis + (long) (defaultSize / speed * 1000);
            System.out.println("Task " + type.getId() + " started. Expected to complete in " + (defaultSize / speed) + " seconds.");
        } else {
            System.out.println("Task " + type.getId() + " started with undefined default size.");
        }
    }

    public void execute(LocalTime start, int stationSpeed) {
        this.startTime = start;
        double taskDuration = defaultSize / stationSpeed; // Station speed ile işlem süresi hesaplanır, double tipinde
        this.endTime = start.plusMinutes((long) Math.ceil(taskDuration)); // Ondalıklı değeri yuvarlayarak LocalTime'a uygula
        this.isCompleted = true;
    }

    public void setCompleted(boolean completed) {
        if (isCompleted) {
            System.out.println("Task " + type.getId() + " is already marked as completed.");
            return;
        }

        this.isCompleted = completed;
        if (completed) {
            long actualEndTime = System.currentTimeMillis();
            System.out.println("Task " + type.getId() + " completed. Actual duration: " + ((actualEndTime - startTimeMillis) / 1000.0) + " seconds.");
        }
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public void setDefaultSize(double defaultSize) {
        this.defaultSize = defaultSize;
    }

    public void setType(TaskType type) {
        this.type = type;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public TaskType getType() {
        return type;
    }

    public double getDefaultSize() {
        return defaultSize;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    @Override
    public String toString() {
        return "Task{" +
                "type=" + type +
                ", isCompleted=" + isCompleted +
                ", startTimeMillis=" + startTimeMillis +
                ", expectedEndTimeMillis=" + expectedEndTimeMillis +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", defaultSize=" + defaultSize +
                ", executionSpeed=" + executionSpeed +
                '}';
    }
}

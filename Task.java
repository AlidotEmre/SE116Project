import java.time.LocalTime;

public class Task {
    private TaskType type;
    private boolean isCompleted;
    private LocalTime startTime;
    private LocalTime endTime;
    private int defaultSize; // unit/minute

    public Task(TaskType type, int defaultSize) {
        this.type = type;
        this.defaultSize = defaultSize;
        this.isCompleted = false;
    }

    public void execute(LocalTime start, int stationSpeed) {
        this.startTime = start;
        int taskDuration = defaultSize / stationSpeed; // Station speed ile işlem süresi hesaplanır
        this.endTime = start.plusMinutes(taskDuration);
        this.isCompleted = true;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public void setDefaultSize(int defaultSize) {
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
    public int getDefaultSize() {
        return defaultSize;
    }

}


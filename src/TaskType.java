public class TaskType {
    private String taskTypeID;
    private double defaultSize;

    public TaskType(String taskTypeID, double defaultSize) {
        this.taskTypeID = taskTypeID;
        this.defaultSize = defaultSize;
    }

    public String getTaskTypeID() {
        return taskTypeID;
    }

    public void setTaskTypeID(String taskTypeID) {
        this.taskTypeID = taskTypeID;
    }

    public double getDefaultSize() {
        return defaultSize;
    }

    public void setDefaultSize(double defaultSize) {
        this.defaultSize = defaultSize;
    }

    @Override
    public String toString() {
        return "TaskType{" +
                "taskTypeID='" + taskTypeID + '\'' +
                ", defaultSize=" + defaultSize +
                '}';
    }

}

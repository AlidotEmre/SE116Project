public class TaskType {
    private String taskTypeID;
    private int defaultSize;


    public TaskType(String taskTypeID, int defaultSize) {
        this.taskTypeID = taskTypeID;
        this.defaultSize = defaultSize;
    }


    public String gettaskTypeID() {
        return taskTypeID;
    }

    public void setTaskTypeID(String taskTypeID) {
        this.taskTypeID = taskTypeID;
    }

    public int getDefaultSize() {
        return defaultSize;
    }

    public void setDefaultSize(int defaultSize) {
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

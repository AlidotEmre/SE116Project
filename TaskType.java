public class TaskType {
    private String id;
    private double defaultSize;

    public TaskType(String id, double defaultSize) {
        this.id = id;
        this.defaultSize = defaultSize;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getDefaultSize() {
        return defaultSize;
    }

    public void setDefaultSize(double defaultSize) {
        this.defaultSize = defaultSize;
    }

    @Override
    public String toString() {
        return "TaskType{id='" + id + "', defaultSize=" + defaultSize + "}";
    }


}

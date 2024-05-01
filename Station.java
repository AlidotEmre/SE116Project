public interface Station {
    String getStationId();
    void addTask(Task task);
    void processTasks();
    void executeTask();
}

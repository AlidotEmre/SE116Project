public interface Task {
    String getTaskType();
    double getTaskSize();
    void setAssignedStation(Station station);
    void start();
    void complete();
}
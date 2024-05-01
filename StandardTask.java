public class StandardTask implements Task {
    private String taskType;
    private double taskSize;
    private Station assignedStation;

    public StandardTask(String taskType, double taskSize) {
        this.taskType = taskType;
        this.taskSize = taskSize;
    }

    @Override
    public String getTaskType() {
        return taskType;
    }

    @Override
    public double getTaskSize() {
        return taskSize;
    }

    @Override
    public void setAssignedStation(Station station) {
        this.assignedStation = station;
    }

    @Override
    public void start() {
        // Görev başlama mantığı burada uygulanacak
    }

    @Override
    public void complete() {
        // Görev tamamlama mantığı burada uygulanacak
    }
}

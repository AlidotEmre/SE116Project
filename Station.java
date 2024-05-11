import java.util.List;
import java.time.LocalTime;

public class Station {
    private String stationID;
    private List<TaskType> supportedTasks;
    private int speed; // Task işlem hızı (unit/minute)

    public Station(String stationID, List<TaskType> supportedTasks, int speed) {
        this.stationID = stationID;
        this.supportedTasks = supportedTasks;
        this.speed = speed;
    }

    public void processTasks(List<Task> tasks) {
        LocalTime currentTime = LocalTime.now();
        for (Task task : tasks) {
            if (supportedTasks.contains(task.getType())) {
                task.execute(currentTime, speed);
                currentTime = task.getEndTime(); // Sonraki görev için başlangıç zamanı güncellenir
            } else {
                System.out.println("Task type not supported by this station.");
            }
        }
    }

    public int getSpeed() {
        return speed;
    }

    public List<TaskType> getSupportedTasks() {
        return supportedTasks;
    }

    public String getStationID() {
        return stationID;
    }

    public void setStationID(String stationID) {
        this.stationID = stationID;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setSupportedTasks(List<TaskType> supportedTasks) {
        this.supportedTasks = supportedTasks;
    }


    @Override
    public String toString() {
        return "Station{" +
                "stationID='" + stationID + '\'' +
                ", supportedTasks=" + taskTypesToString() +  // Desteklenen görev tiplerini dizeye döker
                ", speed=" + speed +
                '}';
    }

    private String taskTypesToString() {
        StringBuilder sb = new StringBuilder("[");
        for (TaskType taskType : supportedTasks) {
            sb.append(taskType.gettaskTypeID()).append(", ");
        }
        if (!supportedTasks.isEmpty()) {
            sb.setLength(sb.length() - 2); // Son virgülü siler
        }
        sb.append("]");
        return sb.toString();
    }

}

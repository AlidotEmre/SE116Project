import java.util.ArrayList;
public abstract class AbstractStation implements Station {
    protected String stationId;
    protected int maxCapacity;
    protected ArrayList<Task> taskList;

    public AbstractStation(String stationId, int maxCapacity) {
        this.stationId = stationId;
        this.maxCapacity = maxCapacity;
        this.taskList = new ArrayList<>();
    }

    @Override
    public String getStationId() {
        return stationId;
    }

    public void addTask(Task task) {
        if (taskList.size() < maxCapacity) {
            taskList.add(task);
        } else {
            System.out.println("Station capacity reached, cannot add more tasks.");
        }
    }

    // Abstract methods that must be implemented by concrete subclasses
    @Override
    public abstract void processTasks();

    @Override
    public abstract void executeTask();
}
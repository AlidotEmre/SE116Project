import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class Station {
    private String id;
    private int maxCapacity;
    private boolean multiFlag;
    private boolean fifoFlag;
    private Map<TaskType, Double> taskSpeeds;
    private Map<TaskType, Double> taskSpeedVariations;
    private Queue<Task> tasks;
    private List<TaskType> supportedTasks;

    public Station(String id, int maxCapacity, boolean multiFlag, boolean fifoFlag, List<Task> supportedTasks) {
        this.id = id;
        this.maxCapacity = maxCapacity;
        this.multiFlag = multiFlag;
        this.fifoFlag = fifoFlag;
        this.taskSpeeds = new HashMap<>();
        this.taskSpeedVariations = new HashMap<>();
        this.tasks = new LinkedList<>();
        this.supportedTasks = this.supportedTasks;
    }

    public void assignTask(Task task) {
        if (task != null && canAcceptTask(task)) {
            if (fifoFlag) {
                tasks.offer(task);
            } else {
                startTask(task); // Directly start the task if FIFO is not required
            }
        }
    }

    public void startTask(Task task) {
        if (task == null) {
            System.out.println("Attempted to start a null task at Station " + id);
            return;
        }

        Double speed = taskSpeeds.get(task.getType());
        if (speed == null) {
            System.out.println("Speed not defined for task type " + task.getType().getId() + " at Station " + id);
            return;
        }

        Double variation = taskSpeedVariations.get(task.getType());
        if (variation == null) {
            variation = 0.0; // Default variation to 0 if not defined
        }

        double adjustedSpeed = speed * (1 + variation);
        task.startExecution(adjustedSpeed);
        System.out.println("Task " + task.getType().getId() + " started at Station " + id + " with adjusted speed: " + adjustedSpeed);
    }

    public boolean canHandleTask(Task task) {
        return taskSpeeds.containsKey(task.getType()); // Can handle task if speed is defined for its type
    }

    private boolean canAcceptTask(Task task) {
        return (multiFlag || tasks.isEmpty()) && tasks.size() < maxCapacity;
    }

    public void addTaskTypeSpeed(TaskType taskType, double speed, double plusMinus) {
        taskSpeeds.put(taskType, speed);
        taskSpeedVariations.put(taskType, plusMinus);
    }

    public Map<TaskType, Double> getTaskSpeeds() {
        return taskSpeeds;
    }

    @Override
    public String toString() {
        return "Station{id='" + id + "', maxCapacity=" + maxCapacity + ", multiFlag=" + multiFlag +
                ", fifoFlag=" + fifoFlag + ", taskSpeeds=" + taskSpeeds + ", taskSpeedVariations=" + taskSpeedVariations + "}";
    }
}

import java.util.*;

public class Station {
    private String stationID;
    private List<TaskType> supportedTasks;
    private int capacity;
    private boolean multiFlag;
    private boolean fifoFlag;
    private Queue<Task> taskQueue;
    private Set<Task> executingTasks = new HashSet<>();
    private Map<String, Double> taskSpeeds;
    private Map<String, Double> taskSpeedVariations;

    public Station(String stationID, int capacity, boolean multiFlag, boolean fifoFlag, List<TaskType> supportedTasks, Map<String, Double> taskSpeeds, Map<String, Double> taskSpeedVariations) {
        this.stationID = stationID;
        this.capacity = capacity;
        this.multiFlag = multiFlag;
        this.fifoFlag = fifoFlag;
        this.supportedTasks = supportedTasks;
        this.taskSpeeds = taskSpeeds;
        this.taskSpeedVariations = taskSpeedVariations;

        if (fifoFlag) {
            this.taskQueue = new LinkedList<>();
        } else {
            this.taskQueue = new PriorityQueue<>(Comparator.comparingDouble(Task::getDeadline));
        }
    }

    public String getStationID() {
        return stationID;
    }

    public List<TaskType> getSupportedTasks() {
        return supportedTasks;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isMultiFlag() {
        return multiFlag;
    }

    public boolean isFifoFlag() {
        return fifoFlag;
    }

    public Queue<Task> getWaitingTasks() {
        return taskQueue;
    }

    public Set<Task> getExecutingTasks() {
        return executingTasks;
    }

    public int getQueueSize() {
        return taskQueue.size();
    }

    public int getExecutingTasksSize() {
        return executingTasks.size();
    }

    public boolean supportsTaskType(String taskTypeID) {
        for (TaskType taskType : supportedTasks) {
            if (taskType.getTaskTypeID().equals(taskTypeID)) {
                return true;
            }
        }
        return false;
    }

    public void addTaskToQueue(Task task) {
        taskQueue.add(task);
        System.out.println("Task " + task.getTaskTypeID() + " added to station " + stationID + " queue.");
    }

    public void startNextTask() {
        if (!taskQueue.isEmpty() && executingTasks.size() < capacity) {
            Task task = taskQueue.poll();
            if (!executingTasks.contains(task)) {
                executingTasks.add(task);
                System.out.println("Task " + task.getTaskTypeID() + " started at station " + stationID);
            }
        }
    }

    public void completeTask(Task task) {
        if (executingTasks.contains(task)) {
            executingTasks.remove(task);
            System.out.println("Task " + task.getTaskTypeID() + " completed at station " + stationID);
        }
    }

    public void displayStatus() {
        System.out.println("Station " + stationID + " status:");
        if (executingTasks.isEmpty() && taskQueue.isEmpty()) {
            System.out.println("  Status: Idle");
        } else {
            System.out.println("  Executing tasks: " + executingTasks);
            System.out.println("  Waiting tasks: " + taskQueue);
        }
        System.out.println("  Capacity: " + capacity);
        System.out.println("  MultiFlag: " + multiFlag);
        System.out.println("  FIFOFlag: " + fifoFlag);
    }

    public double getTaskSpeed(String taskTypeID) {
        return taskSpeeds.getOrDefault(taskTypeID, 0.0);
    }

    public double getTaskSpeedVariation(String taskTypeID) {
        return taskSpeedVariations.getOrDefault(taskTypeID, 0.0);
    }

    @Override
    public String toString() {
        return "Station{" +
                "stationID='" + stationID + '\'' +
                ", supportedTasks=" + supportedTasks +
                ", capacity=" + capacity +
                ", multiFlag=" + multiFlag +
                ", fifoFlag=" + fifoFlag +
                ", taskQueue=" + taskQueue +
                ", executingTasks=" + executingTasks +
                ", taskSpeeds=" + taskSpeeds +
                ", taskSpeedVariations=" + taskSpeedVariations +
                '}';
    }
}

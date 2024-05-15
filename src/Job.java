import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class Job {
    public enum JobState {
        WAITING_TO_START,
        IN_EXECUTION,
        COMPLETED
    }

    private String jobID;
    private JobType jobType;
    private int startTime;
    private int duration;
    private LocalTime deadline;
    private JobState currentState;
    private Task currentTask;
    private Station currentStation;

    private static Map<String, Station> stations = new HashMap<>();

    public Job(String jobID, JobType jobType, int startTime, int duration, LocalTime deadline) {
        this.jobID = jobID;
        this.jobType = jobType;
        this.startTime = startTime;
        this.duration = duration;
        this.deadline = deadline;
        this.currentState = JobState.WAITING_TO_START;
    }

    public void updateState(int currentTime) {
        if (currentTime >= startTime && currentState == JobState.WAITING_TO_START) {
            currentState = JobState.IN_EXECUTION;
            selectNextTask();
        }
        if (currentTask != null && currentTask.isCompleted()) {
            if (!selectNextTask()) {
                currentState = JobState.COMPLETED;
                System.out.println("Job " + jobID + " completed: " + (LocalTime.now().isAfter(deadline) ? "late" : "on time"));
            }
        }
    }

    private boolean allTasksCompleted() {
        for (Task task : jobType.getTasks()) {
            if (!task.isCompleted()) {
                return false;
            }
        }
        return true;
    }

    private boolean selectNextTask() {
        if (jobType != null) {
            currentTask = jobType.getNextTask();
            if (currentTask != null) {
                currentStation = findAvailableStation(currentTask);
                if (currentStation != null) {
                    currentStation.assignTask(currentTask);
                    return true;
                }
            }
        }
        return false;
    }

    private Station findAvailableStation(Task task) {
        for (Station station : stations.values()) {
            if (station.canHandleTask(task)) {
                return station;
            }
        }
        return null;
    }

    public static void addStation(String id, Station station) {
        stations.put(id, station);
    }

    public String getJobID() {
        return jobID;
    }

    public JobType getJobType() {
        return jobType;
    }

    public JobState getCurrentState() {
        return currentState;
    }

    public LocalTime getDeadline() {
        return deadline;
    }

    public void setCurrentTask(Task task) {
        this.currentTask = task;
    }

    public void setCurrentStation(Station station) {
        this.currentStation = station;
    }

    @Override
    public String toString() {
        return "Job{" +
                "jobID='" + jobID + '\'' +
                ", jobType=" + jobType +
                ", startTime=" + startTime +
                ", duration=" + duration +
                ", deadline=" + deadline +
                ", currentState=" + currentState +
                '}';
    }
}

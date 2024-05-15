import java.time.LocalTime;
import java.util.Map;
import java.util.HashMap;

public class Job {
    public enum JobState {
        WAITING_TO_START,  // Job has not started yet, waiting for its start time
        IN_EXECUTION,      // Job is currently in execution
        COMPLETED          // Job has completed all tasks
    }

    private String jobID;
    private JobType jobType;
    private int startTime;  // Start time in minutes from a simulation start point
    private int duration;
    private LocalTime deadline;
    private JobState currentState;
    private Task currentTask; // Assuming there's a Task class that defines what a Task is
    private Station currentStation; // Assuming a Station class defines stations

    // Assuming a static map of all stations available globally
    private static Map<String, Station> stations = new HashMap<>();

    public Job(String jobID, JobType jobType, int startTime, int duration, LocalTime deadline) {
        this.jobID = jobID;
        this.jobType = jobType;
        this.startTime = startTime;
        this.duration = duration;
        this.deadline = deadline;
        this.currentState = JobState.WAITING_TO_START; // Default state
    }



    public void updateState(int currentTime) {
        if (currentTime >= startTime && currentState == JobState.WAITING_TO_START) {
            currentState = JobState.IN_EXECUTION;
            selectNextTask();
        }
        if (currentTask != null && currentTask.isCompleted()) {
            if (!selectNextTask()) { // If no more tasks, mark job as completed
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
            currentTask = jobType.getNextTask();  // Retrieve the next task from the job type
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

    // Assuming a static method to add stations globally
    public static void addStation(String id, Station station) {
        stations.put(id, station);
    }

    // Getters and Setters
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

import java.util.ArrayList;
import java.util.List;

public class JobType {
    private String jobTypeID;  // İş tipinin benzersiz kimliği
    private String name;       // İş tipinin ismi
    private List<Task> tasks;  // Bu iş tipi için gerekli olan görev tiplerinin listesi
    private int currentTaskIndex = 0;  // To track the current task for getNextTask method

    // Güncellenmiş Constructor
    public JobType(String jobTypeID, String name, List<Task> tasks) {
        this.jobTypeID = jobTypeID;
        this.name = name;
        this.tasks = tasks;
    }

    public JobType(String jobTypeID, List<Task> tasks) {
        this.jobTypeID = jobTypeID;
        this.tasks = tasks;
    }

    public JobType(String jobTypeID){
        this.jobTypeID = jobTypeID;
        this.tasks = new ArrayList<>();
    }

    // Görev tipi eklemek için metod
    public void addTask(Task task) {
        if (this.tasks == null) {
            this.tasks = new ArrayList<>();
        }
        this.tasks.add(task);
    }

    /**
     * Retrieves the next task that needs to be completed.
     * @return the next Task or null if all tasks have been completed.
     */
    public Task getNextTask() {
        if (currentTaskIndex < tasks.size()) {
            return tasks.get(currentTaskIndex++);
        }
        return null;  // All tasks are completed
    }

    // Getter ve Setter Metotları
    public String getJobTypeID() {
        return jobTypeID;
    }

    public void setJobTypeID(String jobTypeID) {
        this.jobTypeID = jobTypeID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public String toString() {
        return "JobType{" +
                "jobTypeID='" + jobTypeID + '\'' +
                ", name='" + name + '\'' +
                ", tasks=" + tasks +
                '}';
    }
}

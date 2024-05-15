import java.util.ArrayList;
import java.util.List;

public class JobType {
    private String jobTypeID;
    private String name;
    private List<Task> tasks;

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

    public void addTask(Task task) {
        if (this.tasks == null) {
            this.tasks = new ArrayList<>();
        }
        this.tasks.add(task);
    }

    public Task getNextTask() {
        for (Task task : tasks) {
            if (!task.isCompleted()) {
                return task;
            }
        }
        return null;
    }

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

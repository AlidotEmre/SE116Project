import java.util.ArrayList;

import java.util.List;

public class JobType {
    private String jobTypeID;
    private String name;
    private List<TaskType> tasks;


    public JobType(String jobTypeID, String name, List<TaskType> tasks) {
        this.jobTypeID = jobTypeID;
        this.name = name;
        this.tasks = tasks;
    }
    public JobType(String jobTypeID, List<TaskType> tasks) {
        this.jobTypeID = jobTypeID;
        this.tasks = tasks;
    }
    public JobType(String jobTypeID){
        this.jobTypeID=jobTypeID;
    }



    public void addTaskType(TaskType task) {
        if (this.tasks == null) {
            this.tasks = new ArrayList<>();
        }
        this.tasks.add(task);
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

    public List<TaskType> getTasks() {
        return tasks;
    }

    @Override
    public String toString() {
        return "JobType{" +
                "jobTypeID='" + jobTypeID + '\'' +
                ", name='" + name + '\'' +
                ", tasks=" + tasks +
                '}';
    }

    public void setTaskTypes(List<TaskType> tasks) {
        this.tasks = tasks;
    }



}


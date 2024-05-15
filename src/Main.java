import java.time.LocalTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        String workflowFilePath = args.length > 0 ? args[0] : "C:\\Users\\aliem\\IdeaProjects\\SE116Project\\src\\workflow.txt";
        String jobFilePath = args.length > 1 ? args[1] : "C:\\Users\\aliem\\IdeaProjects\\SE116Project\\src\\jobs.txt";

        try {
            WorkflowParser workflowParser = new WorkflowParser(workflowFilePath);
            workflowParser.parseFile();

            System.out.println("Parsing completed successfully for workflow data.");
            System.out.println("Loaded TaskTypes: " + workflowParser.getTaskTypes().size());
            System.out.println("Loaded JobTypes: " + workflowParser.getJobTypes().size());
            System.out.println("Loaded Stations: " + workflowParser.getStations().size());

            Map<String, JobType> jobTypes = workflowParser.getJobTypes();
            try {
                JobFileParser jobParser = new JobFileParser(jobFilePath, jobTypes);
                jobParser.parseFile();

                System.out.println("Parsing completed successfully for job data.");
                System.out.println("Parsed jobs: " + jobParser.getJobs().size());

                simulateJobProcessing(jobTypes, workflowParser.getStations());
            } catch (Exception e) {
                System.err.println("Error during parsing job data: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Error during parsing workflow data: " + e.getMessage());
        }
    }

    private static void simulateJobProcessing(Map<String, JobType> jobTypes, Map<String, Station> stations) {
        JobType jobType = jobTypes.get("J1");
        if (jobType != null) {
            TaskType taskType1 = new TaskType("TaskType1", 10.0);
            TaskType taskType2 = new TaskType("TaskType2", 15.0);
            Task task1 = new Task(taskType1, 10.0);
            Task task2 = new Task(taskType2, 15.0);
            jobType.addTask(task1);
            jobType.addTask(task2);

            Job job = new Job("ExampleJob", jobType, 0, 120, LocalTime.now().plusMinutes(120));

            Station station1 = stations.get("Station1");
            Station station2 = stations.get("Station2");

            if (station1 == null) {
                station1 = new Station("Station1", 2, true, false, new LinkedList<>(), new HashMap<>(), new HashMap<>());
                stations.put("Station1", station1);
            }
            if (station2 == null) {
                station2 = new Station("Station2", 1, true, false, new LinkedList<>(), new HashMap<>(), new HashMap<>());
                stations.put("Station2", station2);
            }

            Job.addStation("Station1", station1);
            Job.addStation("Station2", station2);

            station1.addTaskTypeSpeed(taskType1, 2.0, 0.0);
            station2.addTaskTypeSpeed(taskType2, 3.0, 0.0);

            job.updateState(LocalTime.now().toSecondOfDay());
            System.out.println("Job state after starting: " + job.getCurrentState());

            if (task1 != null) {
                station1.startTask(task1);
                task1.setCompleted(true);
                job.updateState(LocalTime.now().plusMinutes(5).toSecondOfDay());
                System.out.println("Job state after completing task1: " + job.getCurrentState());
            }

            if (task2 != null) {
                station2.startTask(task2);
                task2.setCompleted(true);
                job.updateState(LocalTime.now().plusMinutes(10).toSecondOfDay());
                System.out.println("Job state after completing task2: " + job.getCurrentState());
            }
        } else {
            System.out.println("JobType J1 not found or is null");
        }
    }
}

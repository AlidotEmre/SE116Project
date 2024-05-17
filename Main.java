import java.util.*;
import java.io.File;

public class Main {
    public static void main(String[] args) {

        String workflowFilePath = "C:\\Users\\PC\\Desktop\\SE116-Workflow-main\\workflow.txt";
        String jobFilePath = "C:\\Users\\PC\\Desktop\\SE116-Workflow-main\\job.txt";


        if (!checkFileAccessibility(workflowFilePath) || !checkFileAccessibility(jobFilePath)) {
            return;
        }


        WorkflowParser workflowParser = new WorkflowParser(workflowFilePath);
        workflowParser.parseFile();


        for (String errorMessage : workflowParser.errorMessageList) {
            System.out.println("Error: " + errorMessage);
        }


        for (String warningMessage : workflowParser.warningMessageList) {
            System.out.println("Warning: " + warningMessage);
        }


        workflowParser.printWorkflowInfo();


        Map<String, JobType> jobTypes = workflowParser.getJobTypes();


        JobFileParser jobFileParser = new JobFileParser(jobFilePath, jobTypes);
        jobFileParser.parseFile();


        for (String errorMessage : jobFileParser.getErrorMessageList()) {
            System.out.println("Error: " + errorMessage);
        }


        List<JobSummary> jobSummaries = new ArrayList<>();
        PriorityQueue<Event> eventQueue = new PriorityQueue<>(Comparator.comparingInt(Event::getTime));
        jobFileParser.getJobs().forEach(job -> eventQueue.add(new Event(job.getStartTime(), job)));

        int currentTime = 0;

        while (!eventQueue.isEmpty() || workflowParser.getStations().values().stream().anyMatch(station -> !station.getExecutingTasks().isEmpty() || !station.getWaitingTasks().isEmpty())) {
            while (!eventQueue.isEmpty() && eventQueue.peek().getTime() <= currentTime) {
                Event event = eventQueue.poll();
                Job job = event.getJob();

                if (job.getActualStartTime() == -1) {
                    job.setActualStartTime(currentTime);
                }

                TaskType nextTask = job.getNextTask();
                if (nextTask != null) {
                    assignTaskToStation(nextTask, workflowParser, currentTime, job);
                    job.incrementTaskIndex();
                }
            }

            for (Station station : workflowParser.getStations().values()) {
                List<Task> completedTasks = new ArrayList<>();
                for (Task task : station.getExecutingTasks()) {
                    if (currentTime >= task.getStartTime() + task.getActualDuration()) {
                        completedTasks.add(task);
                    }
                }

                for (Task task : completedTasks) {
                    station.completeTask(task);
                    Job job = findJobByTaskID(task.getTaskID(), jobFileParser.getJobs());
                    if (job != null) {
                        TaskType nextTask = job.getNextTask();
                        if (nextTask != null) {
                            assignTaskToStation(nextTask, workflowParser, currentTime, job);
                            job.incrementTaskIndex();
                        }


                        if (job.getCurrentTaskIndex() == job.getJobType().getTasks().size()) {
                            double actualDuration = currentTime - job.getActualStartTime();
                            job.setActualEndTime(currentTime, actualDuration);
                        }
                    }
                }

                while (!station.getWaitingTasks().isEmpty() && station.getExecutingTasks().size() < station.getCapacity()) {
                    station.startNextTask(currentTime);
                }
            }

            currentTime++;
        }

        for (Job job : jobFileParser.getJobs()) {
            double expectedDuration = job.getDuration();
            double actualDuration = job.getActualEndTime() != -1 ? job.getActualEndTime() - job.getActualStartTime() : 0.0;
            double difference = Math.abs(expectedDuration - actualDuration);
            jobSummaries.add(new JobSummary(job.getJobID(), expectedDuration, actualDuration, difference, job.getActualStartTime(), job.getActualEndTime()));
        }


        printJobSummaries(jobSummaries);
    }

    private static void assignTaskToStation(TaskType taskType, WorkflowParser workflowParser, int currentTime, Job job) {
        Station selectedStation = null;
        int minLoad = Integer.MAX_VALUE;

        for (Station station : workflowParser.getStations().values()) {
            if (station.supportsTaskType(taskType.getTaskTypeID())) {
                int currentLoad = station.getQueueSize() + station.getExecutingTasksSize();
                if (currentLoad < minLoad) {
                    selectedStation = station;
                    minLoad = currentLoad;
                }
            }
        }

        if (selectedStation != null) {
            double taskSpeed = selectedStation.getTaskSpeed(taskType.getTaskTypeID());
            Task task = new Task(job.getJobID(), taskType.getTaskTypeID(), taskType.getDefaultSize(), currentTime + job.getDuration(), currentTime, taskSpeed);
            selectedStation.addTaskToQueue(task);
        } else {
            System.out.println("Warning: No suitable station found for task " + taskType.getTaskTypeID());
        }
    }

    private static Job findJobByTaskID(String taskID, List<Job> jobs) {
        for (Job job : jobs) {
            for (TaskType taskType : job.getJobType().getTasks()) {
                if (taskType.getTaskTypeID().equals(taskID)) {
                    return job;
                }
            }
        }
        return null;
    }

    private static boolean checkFileAccessibility(String filePath) {
        File file = new File(filePath);
        if (!file.exists() || !file.canRead()) {
            System.out.println("File " + filePath + " does not exist or is not accessible.");
            return false;
        }
        return true;
    }

    private static void printJobSummaries(List<JobSummary> jobSummaries) {
        System.out.println("Job Summaries:");
        for (JobSummary summary : jobSummaries) {
            System.out.println("Job ID: " + summary.getJobID() + ", Expected Duration: " + summary.getExpectedDuration() +
                    ", Actual Duration: " + summary.getActualDuration() + ", Difference: " + summary.getDifference() +
                    ", Start Time: " + summary.getActualStartTime() + ", End Time: " + summary.getActualEndTime());
        }
    }
}

import java.io.File;
import java.util.*;

public class Main {
    static double totalTime;

    public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);
        System.out.println("Enter the workflow file path");
        String workflowFilePath = sc.next();
        System.out.println("Enter the job file path");
        String jobFilePath = sc.next();

        if (!checkFileAccessibility(workflowFilePath) || !checkFileAccessibility(jobFilePath)) {
            return;
        }

        WorkflowParser workflowParser = new WorkflowParser(workflowFilePath);
        workflowParser.parseFile();

        for (String errorMessage : workflowParser.getErrorMessageList()) {
            System.out.println("Error: " + errorMessage);
        }

        for (String warningMessage : workflowParser.getWarningMessageList()) {
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
        PriorityQueue<Event> eventQueue = new PriorityQueue<>(Comparator.comparingDouble(Event::getTime));
        jobFileParser.getJobs().forEach(job -> eventQueue.add(new Event(job.getStartTime(), job)));

        double currentTime = 0.0;
        Map<String, Double> stationIdleTimes = new HashMap<>();
        Map<String, Double> stationActiveTimes = new HashMap<>();

        workflowParser.getStations().values().forEach(station -> {
            stationIdleTimes.put(station.getStationID(), 0.0);
            stationActiveTimes.put(station.getStationID(), 0.0);
        });

        while (!eventQueue.isEmpty() || workflowParser.getStations().values().stream().anyMatch(station -> !station.getExecutingTasks().isEmpty() || !station.getWaitingTasks().isEmpty())) {
            while (!eventQueue.isEmpty() && eventQueue.peek().getTime() <= currentTime) {
                Event event = eventQueue.poll();
                Job job = event.getJob();

                if (job.getActualStartTime() == -1) {
                    job.setActualStartTime(currentTime);
                    System.out.println("Job " + job.getJobID() + " started at time " + currentTime);
                }

                TaskType nextTask = job.getNextTask();
                if (nextTask != null) {
                    assignTaskToStation(nextTask, job, workflowParser, currentTime, eventQueue, stationIdleTimes, stationActiveTimes);
                }
            }

            for (Station station : workflowParser.getStations().values()) {
                processTasksAtStation(station, jobFileParser.getJobs(), workflowParser, eventQueue, currentTime, stationIdleTimes, stationActiveTimes);
            }

            totalTime++;
            currentTime++;
            if (eventQueue.isEmpty()) {
                break;
            }
        }

        for (Job job : jobFileParser.getJobs()) {
            double expectedDuration = job.getDuration();
            double actualDuration = job.getActualEndTime() != -1 ? job.getActualEndTime() - job.getActualStartTime() : 0.0;
            double difference = expectedDuration - actualDuration;
            jobSummaries.add(new JobSummary(job.getJobID(), expectedDuration, actualDuration, difference, job.getActualStartTime(), job.getActualEndTime()));
        }

        printJobSummaries(jobSummaries);
        printJobDetails(jobSummaries);
        printStationStatuses(workflowParser.getStations());
        printAverageJobTardinessByJobType(jobSummaries);
        printStationUtilization(stationIdleTimes, stationActiveTimes, currentTime);
        printAverageTardiness(jobSummaries);
    }

    private static void assignTaskToStation(TaskType taskType, Job job, WorkflowParser workflowParser, double currentTime, PriorityQueue<Event> eventQueue, Map<String, Double> stationIdleTimes, Map<String, Double> stationActiveTimes) {
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
            Task task = new Task(taskType.getTaskTypeID(), taskType.getTaskTypeID(), taskType.getDefaultSize(), currentTime);
            selectedStation.addTaskToQueue(task);
            double taskDuration = task.getActualDuration(selectedStation.getTaskSpeed(taskType.getTaskTypeID()), selectedStation.getTaskSpeedVariation(taskType.getTaskTypeID()));
            System.out.println("Task " + task.getTaskID() + " assigned to station " + selectedStation.getStationID() + " at time " + currentTime + " and will complete in " + taskDuration + " units of time.");
            selectedStation.startNextTask();
            job.incrementTaskIndex();
            System.out.println("Job " + job.getJobID() + ": Task " + taskType.getTaskTypeID() + " assigned at time " + currentTime);
            if (job.getCurrentTaskIndex() < job.getJobType().getTasks().size()) {
                eventQueue.add(new Event(currentTime + taskDuration, job));
            } else if (!job.isCompleted()) {
                job.setActualEndTime(currentTime + taskDuration);
                job.setCompleted(true);
                System.out.println("Job " + job.getJobID() + " completed at time " + (currentTime + taskDuration));
            }
            stationActiveTimes.put(selectedStation.getStationID(), stationActiveTimes.get(selectedStation.getStationID()) + taskDuration);
        } else {
            System.out.println("Warning: No suitable station found for task " + taskType.getTaskTypeID());
            job.addWaitingTask(new Task(taskType.getTaskTypeID(), taskType.getTaskTypeID(), taskType.getDefaultSize(), currentTime));
        }
    }

    private static void processTasksAtStation(Station station, List<Job> jobs, WorkflowParser workflowParser, PriorityQueue<Event> eventQueue, double currentTime, Map<String, Double> stationIdleTimes, Map<String, Double> stationActiveTimes) {
        Iterator<Task> taskIterator = station.getExecutingTasks().iterator();
        while (taskIterator.hasNext()) {
            Task task = taskIterator.next();
            double taskDuration = task.getActualDuration(station.getTaskSpeed(task.getTaskTypeID()), station.getTaskSpeedVariation(task.getTaskTypeID()));
            task.setDeadline(task.getDeadline() + taskDuration);

            if (task.getDeadline() <= currentTime) {
                taskIterator.remove();
                station.completeTask(task);

                Job job = findJobByTaskID(task.getTaskID(), jobs);
                if (job != null) {
                    if (job.getCurrentTaskIndex() < job.getJobType().getTasks().size()) {
                        TaskType nextTask = job.getNextTask();
                        assignTaskToStation(nextTask, job, workflowParser, currentTime, eventQueue, stationIdleTimes, stationActiveTimes);
                    } else if (!job.isCompleted()) {
                        double actualDuration = currentTime - job.getActualStartTime();
                        job.setActualEndTime(job.getActualStartTime() + actualDuration);
                        job.setCompleted(true);
                        System.out.println("Job " + job.getJobID() + " completed at time " + currentTime);

                        eventQueue.removeIf(event -> event.getJob().getJobID().equals(job.getJobID()));
                    }
                }
            }
        }

        while (!station.getWaitingTasks().isEmpty() && station.getExecutingTasks().size() < station.getCapacity()) {
            station.startNextTask();
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
            System.out.println("Job ID: " + summary.getJobID() +
                    ", Start Time: " + summary.getActualStartTime() +
                    ", End Time: " + summary.getActualEndTime() +
                    ", Expected Duration: " + summary.getExpectedDuration() +
                    ", Actual Duration: " + summary.getActualDuration() +
                    ", Difference: " + summary.getDifference());
        }
    }

    private static void printJobDetails(List<JobSummary> jobSummaries) {
        System.out.println("Job Details:");
        for (JobSummary summary : jobSummaries) {
            System.out.println("Job ID: " + summary.getJobID());
            System.out.println("  Start Time: " + summary.getActualStartTime());
            System.out.println("  Expected Duration: " + summary.getExpectedDuration());
            System.out.println("  Actual Duration: " + summary.getActualDuration());
            System.out.println("  Difference: " + summary.getDifference());
            System.out.println("  End Time: " + summary.getActualEndTime());
            System.out.println();
        }
    }

    private static void printStationStatuses(Map<String, Station> stations) {
        System.out.println("Station Statuses:");
        for (Station station : stations.values()) {
            station.displayStatus();
        }
    }

    private static void printAverageJobTardinessByJobType(List<JobSummary> jobSummaries) {
        Map<String, List<Double>> tardinessByJobType = new HashMap<>();
        for (JobSummary summary : jobSummaries) {
            if (summary.getActualEndTime() > summary.getExpectedDuration()) {
                tardinessByJobType.computeIfAbsent(summary.getJobID(), k -> new ArrayList<>())
                        .add(summary.getActualDuration() - summary.getExpectedDuration());
            }
        }

        System.out.println("Average Job Tardiness by Job Type:");
        for (Map.Entry<String, List<Double>> entry : tardinessByJobType.entrySet()) {
            double Tardiness = entry.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            System.out.println("Job Type: " + entry.getKey() + ",  Tardiness: " + Tardiness);
        }
    }

    private static void printStationUtilization(Map<String, Double> stationIdleTimes, Map<String, Double> stationActiveTimes, double totalTime) {
        System.out.println("Station Utilization:");
        for (Map.Entry<String, Double> entry : stationActiveTimes.entrySet()) {
            String stationID = entry.getKey();
            double activeTime = entry.getValue();
            double utilization = (activeTime / totalTime) * 100.0;
            System.out.println("Station ID: " + stationID + ", Utilization: " +"%"+ utilization );
        }
    }

    private static void printAverageTardiness(List<JobSummary> jobSummaries) {
        double totalTardiness = 0.0;
        int tardyJobsCount = 0;
        for (JobSummary summary : jobSummaries) {
            if (summary.getActualDuration() > summary.getExpectedDuration()) {
                totalTardiness += (summary.getActualDuration() - summary.getExpectedDuration());
                tardyJobsCount++;
            }
        }
        double averageTardiness = tardyJobsCount > 0 ? totalTardiness / tardyJobsCount : 0.0;
        System.out.println("Average Tardiness: " + averageTardiness);
    }
}

class Event {
    private double time;
    private Job job;

    public Event(double time, Job job) {
        this.time = time;
        this.job = job;
    }

    public double getTime() {
        return time;
    }

    public Job getJob() {
        return job;
    }
}

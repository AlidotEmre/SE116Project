import java.io.File;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        // Dosya yollarını direkt olarak burada belirtiyoruz
        String workflowFilePath = "C:\\Users\\pc\\IdeaProjects\\Projee\\workflow.txt";
        String jobFilePath = "C:\\Users\\pc\\IdeaProjects\\Projee\\job.txt";

        if (!checkFileAccessibility(workflowFilePath) || !checkFileAccessibility(jobFilePath)) {
            return;
        }

        // WorkflowParser ile workflow dosyasını parse ediyoruz
        WorkflowParser workflowParser = new WorkflowParser(workflowFilePath);
        workflowParser.parseFile();

        // Hata mesajlarını yazdırıyoruz
        for (String errorMessage : workflowParser.getErrorMessageList()) {
            System.out.println("Error: " + errorMessage);
        }

        // Uyarı mesajlarını yazdırıyoruz
        for (String warningMessage : workflowParser.getWarningMessageList()) {
            System.out.println("Warning: " + warningMessage);
        }

        // Workflow bilgilerini konsola yazdır
        workflowParser.printWorkflowInfo();

        // JobType'ları alıyoruz
        Map<String, JobType> jobTypes = workflowParser.getJobTypes();

        // JobFileParser ile job dosyasını parse ediyoruz
        JobFileParser jobFileParser = new JobFileParser(jobFilePath, jobTypes);
        jobFileParser.parseFile();

        // Hata mesajlarını yazdırıyoruz
        for (String errorMessage : jobFileParser.getErrorMessageList()) {
            System.out.println("Error: " + errorMessage);
        }

        // Job'ları işliyoruz ve her işin tamamlanma süresini hesaplıyoruz
        List<JobSummary> jobSummaries = new ArrayList<>();
        PriorityQueue<Event> eventQueue = new PriorityQueue<>(Comparator.comparingDouble(Event::getTime));
        jobFileParser.getJobs().forEach(job -> eventQueue.add(new Event(job.getStartTime(), job)));

        double currentTime = 0.0;

        while (!eventQueue.isEmpty() || workflowParser.getStations().values().stream().anyMatch(station -> !station.getExecutingTasks().isEmpty() || !station.getWaitingTasks().isEmpty())) {
            // Event Queue'dan job'ları zamanlarına göre alıyoruz
            while (!eventQueue.isEmpty() && eventQueue.peek().getTime() <= currentTime) {
                Event event = eventQueue.poll();
                Job job = event.getJob();

                // Job'ı başlatıyoruz
                if (job.getActualStartTime() == -1) {
                    job.setActualStartTime(currentTime);
                    System.out.println("Job " + job.getJobID() + " started at time " + currentTime);
                }

                // Sıradaki görevi alıyoruz
                TaskType nextTask = job.getNextTask();
                if (nextTask != null) {
                    assignTaskToStation(nextTask, job, workflowParser, currentTime, eventQueue);
                }
            }

            // İstasyonlardaki görevleri işliyoruz
            for (Station station : workflowParser.getStations().values()) {
                processTasksAtStation(station, jobFileParser.getJobs(), workflowParser, eventQueue, currentTime);
            }

            currentTime++;
            if(eventQueue.isEmpty()){
                break;
            }
        }

        for (Job job : jobFileParser.getJobs()) {
            double expectedDuration = job.getDuration();
            double actualDuration = job.getActualEndTime() != -1 ? job.getActualEndTime() - job.getActualStartTime() : 0.0;
            double difference = expectedDuration - actualDuration;
            jobSummaries.add(new JobSummary(job.getJobID(), expectedDuration, actualDuration, difference, job.getActualStartTime(), job.getActualEndTime()));
        }

        // İşlerin özetini yazdır
        printJobSummaries(jobSummaries);

        // İşlerin detaylarını yazdır
        printJobDetails(jobSummaries);

        // İstasyonların durumunu yazdır
        printStationStatuses(workflowParser.getStations());
    }

    private static void assignTaskToStation(TaskType taskType, Job job, WorkflowParser workflowParser, double currentTime, PriorityQueue<Event> eventQueue) {
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
            selectedStation.startNextTask(); // İstasyondaki uygunluğu kontrol edip görevi başlatıyoruz
            job.incrementTaskIndex();
            System.out.println("Job " + job.getJobID() + ": Task " + taskType.getTaskTypeID() + " assigned at time " + currentTime);
            if (job.getCurrentTaskIndex() < job.getJobType().getTasks().size()) {
                eventQueue.add(new Event(currentTime + taskDuration, job));
            } else if (!job.isCompleted()) {
                job.setActualEndTime(currentTime + taskDuration); // Güncellenmiş tamamlama süresi
                job.setCompleted(true);
                System.out.println("Job " + job.getJobID() + " completed at time " + (currentTime + taskDuration));
            }
        } else {
            System.out.println("Warning: No suitable station found for task " + taskType.getTaskTypeID());
            job.addWaitingTask(new Task(taskType.getTaskTypeID(), taskType.getTaskTypeID(), taskType.getDefaultSize(), currentTime));
        }
    }

    private static void processTasksAtStation(Station station, List<Job> jobs, WorkflowParser workflowParser, PriorityQueue<Event> eventQueue, double currentTime) {
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
                        assignTaskToStation(nextTask, job, workflowParser, currentTime, eventQueue);
                    } else if (!job.isCompleted()) {
                        double actualDuration = currentTime - job.getActualStartTime();
                        job.setActualEndTime(job.getActualStartTime() + actualDuration);
                        job.setCompleted(true);
                        System.out.println("Job " + job.getJobID() + " completed at time " + currentTime);
                        // Job tamamlandığında eventQueue'dan çıkartalım
                        eventQueue.removeIf(event -> event.getJob().getJobID().equals(job.getJobID()));
                    }
                }
            }
        }

        // Bekleyen görevleri işlemeye başlıyoruz
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
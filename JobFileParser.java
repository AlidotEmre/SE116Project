import java.io.*;
import java.nio.file.*;
import java.time.LocalTime;
import java.util.*;

public class JobFileParser {
    private String filePath;
    private Map<String, Job> jobs = new HashMap<>();
    private Map<String, JobType> jobTypes = new HashMap<>();

    public JobFileParser(String filePath) {
        this.filePath = filePath;
    }

    public void loadJobTypes(Map<String, JobType> preloadedJobTypes) {
        jobTypes.putAll(preloadedJobTypes);
    }

    public void parseFile() {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                parseLine(line, lineNumber);
                lineNumber++;
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    private void parseLine(String line, int lineNumber) {
        String[] parts = line.split("\\s+");
        if (parts.length != 4) {
            reportSyntaxError("Invalid number of elements on line", lineNumber);
            return;
        }

        String jobID = parts[0];
        String jobTypeID = parts[1];
        String startTimeStr = parts[2];
        String durationStr = parts[3];

        if (!validateJobIDUnique(jobID)) {
            reportSemanticError("Job ID is not unique", lineNumber);
            return;
        }

        try {
            int startTime = Integer.parseInt(startTimeStr);
            int duration = Integer.parseInt(durationStr);
            LocalTime deadline = LocalTime.ofSecondOfDay(startTime * 60).plusMinutes(duration);
            JobType jobType = getJobTypeFromID(jobTypeID);
            Job job = new Job(jobID, jobType, startTime, duration, deadline);
            jobs.put(jobID, job);
        } catch (NumberFormatException e) {
            reportSyntaxError("Start time or duration is not a valid integer", lineNumber);
        }
    }

    private boolean validateJobIDUnique(String jobID) {
        return !jobs.containsKey(jobID);
    }

    private void reportSyntaxError(String message, int lineNumber) {
        System.out.println("Syntax error on line " + lineNumber + ": " + message);
    }

    private void reportSemanticError(String message, int lineNumber) {
        System.out.println("Semantic error on line " + lineNumber + ": " + message);
    }

    private JobType getJobTypeFromID(String jobTypeID) {
        if (!jobTypes.containsKey(jobTypeID)) {
            throw new IllegalArgumentException("JobType ID not found: " + jobTypeID);
        }
        return jobTypes.get(jobTypeID);
    }

    public Map<String, Job> getJobs() {
        return jobs;
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java JobFileParser <path_to_job_file>");
            return;
        }
        JobFileParser parser = new JobFileParser(args[0]);
        parser.parseFile();
        System.out.println("Parsed jobs: " + parser.getJobs().size());
    }
}

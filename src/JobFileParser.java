import java.io.*;
import java.nio.file.*;
import java.time.LocalTime;
import java.util.*;

public class JobFileParser {
    private String filePath;
    private Map<String, JobType> jobTypes;
    private List<Job> jobs = new ArrayList<>();
    private List<String> errorMessageList = new ArrayList<>();

    public JobFileParser(String filePath, Map<String, JobType> jobTypes) {
        this.filePath = filePath;
        this.jobTypes = jobTypes;
    }

    public void parseFile() {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
            String line;
            int lineNumber = 1;
            Set<String> jobIDs = new HashSet<>();
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    lineNumber++;
                    continue;
                }
                parseLine(line, lineNumber, jobIDs);
                lineNumber++;
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: '" + filePath + "'. Please check the file path.");
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }

        for (String errorMessage : errorMessageList) {
            System.out.println(errorMessage);
        }
    }

    private void parseLine(String line, int lineNumber, Set<String> jobIDs) {
        String[] parts = line.split(" ");
        if (parts.length != 4) {
            errorMessageList.add("Syntax Error on line " + lineNumber + ": Invalid number of fields");
            return;
        }

        String jobID = parts[0];
        String jobTypeID = parts[1];
        try {
            int startTime = Integer.parseInt(parts[2]);
            int duration = Integer.parseInt(parts[3]);
            if (startTime < 0 || duration < 0) {
                errorMessageList.add("Semantic Error on line " + lineNumber + ": Start time and duration must be non-negative");
            } else {
                if (jobIDs.contains(jobID)) {
                    errorMessageList.add("Semantic Error on line " + lineNumber + ": Duplicate job ID");
                } else {
                    jobIDs.add(jobID);
                    JobType jobType = jobTypes.get(jobTypeID);
                    if (jobType == null) {
                        errorMessageList.add("Semantic Error on line " + lineNumber + ": Undefined job type ID");
                    } else {
                        LocalTime deadline = LocalTime.of(startTime / 60, startTime % 60).plusMinutes(duration);
                        Job job = new Job(jobID, jobType, startTime, duration, deadline);
                        jobs.add(job);
                    }
                }
            }
        } catch (NumberFormatException e) {
            errorMessageList.add("Semantic Error on line " + lineNumber + ": Start time and duration must be integers");
        }
    }

    public List<Job> getJobs() {
        return jobs;
    }

    public List<String> getErrorMessageList() {
        return errorMessageList;
    }

    public static void main(String[] args) {
        Map<String, JobType> jobTypes = new HashMap<>();
        jobTypes.put("J1", new JobType("J1", new ArrayList<>()));
        jobTypes.put("J2", new JobType("J2", new ArrayList<>()));
        jobTypes.put("J3", new JobType("J3", new ArrayList<>()));

        JobFileParser parser = new JobFileParser("path/to/jobs.txt", jobTypes);
        parser.parseFile();
        System.out.println("Parsed jobs: " + parser.getJobs().size());
        for (String errorMessage : parser.getErrorMessageList()) {
            System.out.println(errorMessage);
        }
    }
}

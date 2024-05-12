import java.io.*;
import java.nio.file.*;
import java.util.*;

public class WorkflowParser {
    private String filePath;
    private Map<String, TaskType> taskTypes = new HashMap<>();
    private Map<String, JobType> jobTypes = new HashMap<>();
    private Map<String, Station> stations = new HashMap<>();

    public WorkflowParser(String filePath) {
        this.filePath = filePath;
    }

    public void parseFile() {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("(TASKTYPES")) {
                    parseTaskTypes(reader);
                } else if (line.startsWith("(JOBTYPES")) {
                    parseJobTypes(reader);
                } else if (line.startsWith("(STATIONS")) {
                    parseStations(reader);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    private void parseTaskTypes(BufferedReader reader) throws IOException {
        String line;
        while (!(line = reader.readLine()).equals(")")) {
            String[] parts = line.trim().split("\\s+");
            for (int i = 0; i < parts.length; i++) {
                String taskTypeId = parts[i];
                validateTaskTypeID(taskTypeId);

                if (!isNumeric(parts[i + 1])) {
                    throw new IllegalArgumentException("Task size must be numeric. Found in TaskType: " + taskTypeId);
                }

                int defaultSize = Integer.parseInt(parts[i + 1]);
                validateDefaultSize(defaultSize, taskTypeId);

                if (taskTypes.containsKey(taskTypeId)) {
                    throw new IllegalArgumentException("Duplicate TaskType ID found: " + taskTypeId);
                }

                taskTypes.put(taskTypeId, new TaskType(taskTypeId, defaultSize));
                i++; // Skip next part since it's the size
            }
        }
    }

    private void parseJobTypes(BufferedReader reader) throws IOException {
        String line;
        while (!(line = reader.readLine()).equals(")")) {
            String[] parts = line.trim().split("\\s+");
            String jobTypeId = parts[0];
            List<TaskType> tasks = new ArrayList<>();

            for (int i = 1; i < parts.length; i += 2) {
                String taskTypeId = parts[i];
                int size = Integer.parseInt(parts[i + 1]);
                if (taskTypes.containsKey(taskTypeId)) {
                    tasks.add(new TaskType(taskTypeId, size));
                } else {
                    throw new IllegalArgumentException("TaskType ID not found for JobType: " + jobTypeId);
                }
            }

            if (jobTypes.containsKey(jobTypeId)) {
                throw new IllegalArgumentException("Duplicate JobType ID found: " + jobTypeId);
            }

            jobTypes.put(jobTypeId, new JobType(jobTypeId, "Name Placeholder", tasks));
        }
    }

    private void parseStations(BufferedReader reader) throws IOException {
        String line;
        while (!(line = reader.readLine()).equals(")")) {
            String[] parts = line.trim().split("\\s+");
            String stationId = parts[0];
            int capacity = Integer.parseInt(parts[1]);
            boolean multiFlag = parts[2].equals("Y");
            boolean fifoFlag = parts[3].equals("Y");
            List<TaskType> supportedTasks = new ArrayList<>();

            for (int i = 4; i < parts.length; i += 2) {
                String taskTypeId = parts[i];
                int speed = Integer.parseInt(parts[i + 1]);
                if (taskTypes.containsKey(taskTypeId)) {
                    supportedTasks.add(new TaskType(taskTypeId, speed));
                } else {
                    throw new IllegalArgumentException("TaskType ID not found for Station: " + stationId);
                }
            }

            if (stations.containsKey(stationId)) {
                throw new IllegalArgumentException("Duplicate Station ID found: " + stationId);
            }

            stations.put(stationId, new Station(stationId, capacity, multiFlag, fifoFlag, supportedTasks));
        }
    }

    private boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void validateTaskTypeID(String id) {
        if (!Character.isLetter(id.charAt(0))) {
            throw new IllegalArgumentException("TaskType ID must start with a letter. Invalid ID: " + id);
        }
    }

    private void validateDefaultSize(int size, String id) {
        if (size < 0) {
            throw new IllegalArgumentException("Negative task size found for TaskType ID: " + id);
        }
    }
}

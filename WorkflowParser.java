import java.io.*;
import java.nio.file.*;
import java.util.*;

public class WorkflowParser {
    private String filePath;
    private Map<String, TaskType> taskTypes = new HashMap<>();
    boolean isTaskTypeContinue;
    private TaskType currentTaskType;
    private String taskID;
    private double taskValue;
    private boolean isTaskTypeProgressContinue;
    private Map<String, JobType> jobTypes = new HashMap<>();
    private Map<String, Station> stations = new HashMap<>();
    List<String> errorMessageList = new ArrayList<>();

    public WorkflowParser(String filePath) {
        this.filePath = filePath;
    }

    public void parseFile() {
        isTaskTypeProgressContinue = false;
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    lineNumber++;
                    continue;
                }
                try {
                    if (line.startsWith("(TASKTYPES") || isTaskTypeProgressContinue) {
                        isTaskTypeProgressContinue = true;
                        parseTaskTypes(line, lineNumber);
                        if (line.contains(")"))
                            isTaskTypeProgressContinue = false;

                    } else if (line.startsWith("(JOBTYPES")) {
                        parseJobTypes(reader, lineNumber);
                    } else if (line.startsWith("(STATIONS")) {
                        parseStations(reader, lineNumber);
                    }
                } catch (IllegalArgumentException | IllegalStateException e) {
                    System.err.println("Error on line " + lineNumber + ": " + e.getMessage());
                }
                lineNumber++;
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: '" + filePath + "'. Please check the file path.");
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    private void parseTaskTypes(String line, int lineNumber) throws IOException {
        if (!line.isEmpty()) {
            List<String> splittedTaskType = new ArrayList<>();
            splittedTaskType = Arrays.stream(line.split(" ")).toList();
            if (!splittedTaskType.isEmpty()) {
                for (String currentSplittedTaskType : splittedTaskType) {
                    if (currentSplittedTaskType.startsWith("T") && !isTaskTypeContinue) {
                        isTaskTypeContinue = true;
                        taskID = currentSplittedTaskType;
                    } else if ((!currentSplittedTaskType.startsWith("(") || !currentSplittedTaskType.contains("TASKTYPES")) && currentSplittedTaskType.contains("T") && !Character.isLetter(currentSplittedTaskType.charAt(0))) {
                        errorMessageList.add(currentSplittedTaskType + "" + "is an invalid taskTypeID on Line: " + lineNumber);
                        isTaskTypeContinue = false;
                    } else if (currentSplittedTaskType.startsWith("T") && isTaskTypeContinue) {
                        currentTaskType = new TaskType(taskID, 0);
                        taskTypes.put(taskID, currentTaskType);
                        isTaskTypeContinue = true;
                        taskID = currentSplittedTaskType;
                    } else if (!currentSplittedTaskType.startsWith("T") && isTaskTypeContinue) {
                        if (taskTypes.containsKey(taskID)) {
                            errorMessageList.add(taskID + ":" + "is listed twice on Line:" + "" + lineNumber);
                            isTaskTypeContinue = false;
                        }
                        if (Double.parseDouble(currentSplittedTaskType) > 0) {
                            currentTaskType = new TaskType(taskID, Double.parseDouble(currentSplittedTaskType));
                            taskTypes.put(taskID, currentTaskType);
                            isTaskTypeContinue = false;
                        } else {
                            errorMessageList.add(taskID + ":" + "Task size can not be negative Line number:" + lineNumber);
                            isTaskTypeContinue = false;
                            taskID = "";
                        }
                    }
                }
            }
        }
        for (String errorMessage : errorMessageList) {
            System.out.println(errorMessage);
        }
    }

    private void parseJobTypes(BufferedReader reader, int lineNumber) throws IOException {
        Set<String> jobTypeSet = new HashSet<>();
        String line;

        while ((line = reader.readLine()) != null && !line.trim().equals(")")) {
            lineNumber++;
            line = line.trim();

            if (line.startsWith("(") && line.endsWith(")")) {
                line = line.substring(1, line.length() - 1).trim();
            }

            List<String> tokens = new ArrayList<>(Arrays.asList(line.split("\\s+")));
            if (tokens.isEmpty()) continue;

            String jobTypeId = tokens.get(0);
            if (jobTypeSet.contains(jobTypeId)) {
                errorMessageList.add("Error on line " + lineNumber + ": Job type " + jobTypeId + " is already declared.");
                continue;
            }
            jobTypeSet.add(jobTypeId);

            List<Task> jobTasks = new ArrayList<>();
            for (int i = 1; i < tokens.size(); i++) {
                String taskTypeId = tokens.get(i);

                if (!taskTypeId.matches("[A-Za-z0-9]+")) {
                    errorMessageList.add("Error on line " + lineNumber + ": Invalid task type ID " + taskTypeId);
                    continue;
                }

                int counter = 0;
                if (tokens.get(i).startsWith("T")) {

                    if (taskTypes.containsKey(tokens.get(i))) {
                        counter++;
                    }

                    if (counter == 0) {
                        System.out.println(tokens.get(i) + " is not declared of TaskTypes");
                    }
                    counter = 0;
                }

                double size = -1;
                if (i + 1 < tokens.size() && tokens.get(i + 1).matches("-?\\d+(\\.\\d+)?")) {
                    size = Double.parseDouble(tokens.get(++i));
                    if (size <= 0) {
                        errorMessageList.add("Error on line " + lineNumber + ": Task type " + taskTypeId + " has a negative or zero size.");
                        continue;
                    }
                } else {
                    TaskType taskType = taskTypes.get(taskTypeId);
                    if (taskType != null) {
                        size = taskType.getDefaultSize();
                        if (size <= 0) {
                            errorMessageList.add("Error on line " + lineNumber + ": Task type " + taskTypeId + " has no default size and no size specified.");
                            continue;
                        }
                    }
                }

                if (size > 0) {
                    jobTasks.add(new Task(taskTypes.get(taskTypeId), size));
                }
            }

            if (!jobTasks.isEmpty()) {
                jobTypes.put(jobTypeId, new JobType(jobTypeId, jobTasks));
            }
        }

        for (String errorMessage : errorMessageList) {
            System.out.println(errorMessage);
        }
    }

    private void parseStations(BufferedReader reader, int lineNumber) throws IOException {
        Set<String> taskTypesInStations = new HashSet<>();
        String line;

        while ((line = reader.readLine()) != null && !line.trim().equals(")")) {
            lineNumber++;
            line = line.trim();

            if (line.startsWith("(") && line.endsWith(")")) {
                line = line.substring(1, line.length() - 1).trim();
            }

            List<String> tokens = new ArrayList<>(Arrays.asList(line.split("\\s+")));
            if (tokens.isEmpty()) continue;

            if (tokens.size() < 5) {
                errorMessageList.add("Error on line " + lineNumber + ": Invalid STATION format.");
                continue;
            }

            String stationId = tokens.get(0);
            int maxCapacity = Integer.parseInt(tokens.get(1));
            boolean multiFlag = tokens.get(2).equalsIgnoreCase("Y");
            boolean fifoFlag = tokens.get(3).equalsIgnoreCase("Y");

            Queue<Task> supportedTasks = new LinkedList<>();
            for (int i = 4; i < tokens.size(); i += 2) {
                String taskTypeId = tokens.get(i);

                // TaskType ID kontrolü
                if (!taskTypeId.matches("[A-Za-z0-9]+")) {
                    errorMessageList.add("Error on line " + lineNumber + ": Invalid task type ID " + taskTypeId);
                    continue;
                }

                if (!taskTypes.containsKey(taskTypeId)) {
                    errorMessageList.add("Error on line " + lineNumber + ": TaskType ID '" + taskTypeId + "' not declared in TASKTYPES.");
                    continue;
                }
                taskTypesInStations.add(taskTypeId);

                try {
                    double speed = Double.parseDouble(tokens.get(i + 1));
                    if (speed <= 0) {
                        errorMessageList.add("Error on line " + lineNumber + ": Speed must be a positive number.");
                        continue;
                    }
                    supportedTasks.add(new Task(taskTypes.get(taskTypeId), speed));
                } catch (NumberFormatException e) {
                    errorMessageList.add("Error on line " + lineNumber + ": Invalid speed value for task type " + taskTypeId + ".");
                    continue;
                }
            }

            stations.put(stationId, new Station(stationId, maxCapacity, multiFlag, fifoFlag, (List<Task>) supportedTasks));
        }

        // Check for task types in JOBTYPES that are not executed in any STATIONS
        for (JobType jobType : jobTypes.values()) {
            for (Task task : jobType.getTasks()) {
                if (!taskTypesInStations.contains(task.getType().getId())) {
                    errorMessageList.add("Warning: Task type " + task.getType().getId() + " is not executed in any STATIONS.");
                }
            }
        }

        // Check for task types in TASKTYPES that are not executed in any STATIONS
        for (String taskTypeId : taskTypes.keySet()) {
            if (!taskTypesInStations.contains(taskTypeId)) {
                errorMessageList.add("Warning: Task type " + taskTypeId + " is not executed in any STATIONS.");
            }
        }

        if (line == null || !line.trim().equals(")")) {
            errorMessageList.add("Error on line " + lineNumber + ": Missing closing parenthesis for STATIONS section.");
        }

        for (String errorMessage : errorMessageList) {
            System.out.println(errorMessage);
        }
    }
    public Map<String, TaskType> getTaskTypes() {
        return taskTypes;
    }

    public Map<String, Station> getStations() {
        return stations;
    }

    public Map<String, JobType> getJobTypes() {
        return jobTypes;
    }

    public String getFilePath() {
        return filePath;
    }
}

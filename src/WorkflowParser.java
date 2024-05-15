import java.io.*;
import java.nio.file.*;
import java.util.*;

public class WorkflowParser {
    private String filePath;
    private Map<String, TaskType> taskTypes = new HashMap<>();
    private boolean isTaskTypeContinue;
    private TaskType currentTaskType;
    private String taskID;
    private double taskValue;
    private boolean isTaskTypeProgressContinue;
    private Map<String, JobType> jobTypes = new HashMap<>();
    private Map<String, Station> stations = new HashMap<>();
    List<String> errorMessageList = new ArrayList<>();
    List<String> warningMessageList = new ArrayList<>();

    public WorkflowParser(String filePath) {
        this.filePath = filePath;
    }

    public void parseFile() {
        isTaskTypeProgressContinue = false;
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
            String line;
            int lineNumber = 1;
            boolean isJobTypesSection = false;
            boolean isStationsSection = false;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    lineNumber++;
                    continue;
                }

                if (line.startsWith("(TASKTYPES") || isTaskTypeProgressContinue) {
                    isTaskTypeProgressContinue = true;
                    parseTaskTypes(line, lineNumber);
                    if (line.contains(")")) {
                        isTaskTypeProgressContinue = false;
                    }
                } else if (line.startsWith("(JOBTYPES") || isJobTypesSection) {
                    isJobTypesSection = true;
                    parseJobTypes(reader, lineNumber);
                    isJobTypesSection = false;
                } else if (line.startsWith("(STATIONS") || isStationsSection) {
                    isStationsSection = true;
                    parseStations(reader, lineNumber);
                    isStationsSection = false;
                }
                lineNumber++;
            }

            checkMissingClosingParenthesis(lineNumber);
            checkTaskTypesInStations();
            checkJobTypesInStations();

        } catch (FileNotFoundException e) {
            System.err.println("File not found: '" + filePath + "'. Please check the file path.");
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }

        for (String errorMessage : errorMessageList) {
            System.out.println(errorMessage);
        }
        for (String warningMessage : warningMessageList) {
            System.out.println(warningMessage);
        }
    }

    private void parseTaskTypes(String line, int lineNumber) {
        if (!line.isEmpty()) {
            List<String> splittedTaskType = new ArrayList<>();
            splittedTaskType = Arrays.stream(line.split(" ")).toList();
            if (!splittedTaskType.isEmpty()) {
                for (String currentSplittedTaskType : splittedTaskType) {
                    if (currentSplittedTaskType.startsWith("T") && !isTaskTypeContinue) {
                        isTaskTypeContinue = true;
                        taskID = currentSplittedTaskType;
                    } else if (currentSplittedTaskType.startsWith("T") && isTaskTypeContinue) {
                        currentTaskType = new TaskType(taskID, 0);
                        taskTypes.put(taskID, currentTaskType);
                        isTaskTypeContinue = true;
                        taskID = currentSplittedTaskType;
                    } else if (!currentSplittedTaskType.startsWith("T") && isTaskTypeContinue) {
                        if (taskTypes.containsKey(taskID)) {
                            errorMessageList.add(taskID + ": is listed twice on Line: " + lineNumber);
                            isTaskTypeContinue = false;
                        }
                        if (Double.parseDouble(currentSplittedTaskType) > 0) {
                            currentTaskType = new TaskType(taskID, Double.parseDouble(currentSplittedTaskType));
                            taskTypes.put(taskID, currentTaskType);
                            isTaskTypeContinue = false;
                        } else {
                            errorMessageList.add(taskID + ": Task size cannot be negative on Line: " + lineNumber);
                            isTaskTypeContinue = false;
                            taskID = "";
                        }
                    }
                }
            }
        }
    }

    private void parseJobTypes(BufferedReader reader, int lineNumber) throws IOException {
        Set<String> jobTypeSet = new HashSet<>();
        String line;
        boolean endOfSection = false;

        while ((line = reader.readLine()) != null && !endOfSection) {
            line = line.trim();
            lineNumber++;

            if (line.startsWith("(STATIONS")) {
                endOfSection = true;
                parseStations(reader, lineNumber);
                break;
            }

            if (line.equals(")")) {
                endOfSection = true;
                continue;
            }

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

                if (!taskTypes.containsKey(taskTypeId)) {
                    errorMessageList.add("Error on line " + lineNumber + ": Task type " + taskTypeId + " is not declared.");
                    continue;
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
    }

    private void parseStations(BufferedReader reader, int lineNumber) throws IOException {
        String line;
        while ((line = reader.readLine()) != null && !line.trim().equals(")")) {
            line = line.trim();
            lineNumber++;

            if (line.startsWith("(") && line.endsWith(")")) {
                line = line.substring(1, line.length() - 1).trim();
            }

            List<String> tokens = new ArrayList<>(Arrays.asList(line.split("\\s+")));
            if (tokens.isEmpty()) continue;

            String stationId = tokens.get(0);
            int capacity = Integer.parseInt(tokens.get(1));
            boolean multiFlag = tokens.get(2).equals("Y");
            boolean fifoFlag = tokens.get(3).equals("Y");

            List<TaskType> supportedTasks = new ArrayList<>();
            Map<String, Double> taskSpeeds = new HashMap<>();
            Map<String, Double> taskSpeedVariations = new HashMap<>();

            for (int i = 4; i < tokens.size(); i++) {
                String taskTypeId = tokens.get(i);
                if (!taskTypes.containsKey(taskTypeId)) {
                    errorMessageList.add("Error on line " + lineNumber + ": Task type " + taskTypeId + " is not declared.");
                    continue;
                }

                double speed;
                try {
                    speed = Double.parseDouble(tokens.get(++i));
                } catch (NumberFormatException e) {
                    errorMessageList.add("Error on line " + lineNumber + ": Invalid speed value " + tokens.get(i) + " for task type " + taskTypeId);
                    continue;
                }

                supportedTasks.add(taskTypes.get(taskTypeId));
                taskSpeeds.put(taskTypeId, speed);

                if (i + 1 < tokens.size() && tokens.get(i + 1).matches("-?\\d+(\\.\\d+)?")) {
                    double variation = Double.parseDouble(tokens.get(i + 1));
                    if (variation > 0 && variation < 1) {
                        taskSpeedVariations.put(taskTypeId, variation);
                        i++;
                    }
                }
            }

            stations.put(stationId, new Station(stationId, capacity, multiFlag, fifoFlag, supportedTasks, taskSpeeds, taskSpeedVariations));
        }
    }

    private void checkMissingClosingParenthesis(int lineNumber) {
        if (isTaskTypeProgressContinue) {
            errorMessageList.add("Line " + lineNumber + ": ')' missing");
        }
    }

    private void checkTaskTypesInStations() {
        Set<String> taskTypesInStations = new HashSet<>();
        for (Station station : stations.values()) {
            for (TaskType taskType : station.getSupportedTasks()) {
                taskTypesInStations.add(taskType.getTaskTypeID());
            }
        }

        for (String taskTypeId : taskTypes.keySet()) {
            if (!taskTypesInStations.contains(taskTypeId)) {
                warningMessageList.add(taskTypeId + " is not executed in any STATIONS even though it is listed as possible task types.");
            }
        }
    }

    private void checkJobTypesInStations() {
        Set<String> taskTypesInStations = new HashSet<>();
        for (Station station : stations.values()) {
            for (TaskType taskType : station.getSupportedTasks()) {
                taskTypesInStations.add(taskType.getTaskTypeID());
            }
        }

        for (JobType jobType : jobTypes.values()) {
            for (Task task : jobType.getTasks()) {
                if (!taskTypesInStations.contains(task.getType().getTaskTypeID())) {
                    warningMessageList.add("There are no STATIONs which execute " + task.getType().getTaskTypeID() + ", however, " + task.getType().getTaskTypeID() + " is a part of some job type.");
                }
            }
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

    public static void main(String[] args) throws IOException {
        WorkflowParser workflowParser = new WorkflowParser("C:\\Users\\aliem\\IdeaProjects\\SE116Project\\src\\workflow.txt");
        workflowParser.parseFile();

        for (String errorMessage : workflowParser.errorMessageList) {
            System.out.println(errorMessage);
        }
        for (String warningMessage : workflowParser.warningMessageList) {
            System.out.println(warningMessage);
        }

        Map<String, JobType> jobTypes = workflowParser.getJobTypes();
        JobFileParser jobFileParser = new JobFileParser("C:\\Users\\aliem\\IdeaProjects\\SE116Project\\src\\jobs.txt", jobTypes);
        jobFileParser.parseFile();

        for (String errorMessage : jobFileParser.getErrorMessageList()) {
            System.out.println(errorMessage);
        }

        for (Job job : jobFileParser.getJobs()) {
            for (Task task : job.getJobType().getTasks()) {
                for (Station station : workflowParser.getStations().values()) {
                    if (station.supportsTaskType(task.getType().getTaskTypeID())) {
                        station.addTaskToQueue(task);
                    }
                }
            }
        }

        for (Station station : workflowParser.getStations().values()) {
            System.out.println("Starting tasks for station: " + station.getStationID());

            while (!station.getWaitingTasks().isEmpty() && station.getExecutingTasks().size() < station.getCapacity()) {
                station.startNextTask();
            }

            while (!station.getExecutingTasks().isEmpty()) {
                station.completeTask(station.getExecutingTasks().get(0));
            }

            station.displayStatus();
        }
    }
}

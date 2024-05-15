import java.util.Map;

public class Main {
    public static void main(String[] args) {
        WorkflowParser workflowParser = new WorkflowParser("C:\\Users\\PC\\Desktop\\SE116-Workflow-main\\workflow.txt");
        workflowParser.parseFile();

        Map<String, JobType> jobTypes = workflowParser.getJobTypes();
        JobFileParser jobFileParser = new JobFileParser("C:\\Users\\PC\\Desktop\\SE116-Workflow-main\\job.txt", jobTypes);
        jobFileParser.parseFile();


        for (Job job : jobFileParser.getJobs()) {
            System.out.println(job);
        }
        for (String errorMessage : jobFileParser.getErrorMessageList()) {
            System.out.println(errorMessage);
        }
    }
}
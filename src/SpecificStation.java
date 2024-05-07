public class SpecificStation extends AbstractStation {
    private boolean isMultitask;
    private boolean useFIFO;

    public SpecificStation(String stationId, int maxCapacity, boolean isMultitask, boolean useFIFO) {
        super(stationId, maxCapacity);
        this.isMultitask = isMultitask;
        this.useFIFO = useFIFO;
    }

    @Override
    public void processTasks() {
        // Example logic to process tasks based on certain criteria
        if (!taskList.isEmpty()) {
            if (useFIFO) {
                // FIFO: First In, First Out, simply leave the list as is
            } else {
                // Non-FIFO: Could reorder based on other criteria here
            }
        }
    }

    @Override
    public void executeTask() {
        // Execute the first task in the list (if FIFO) or another logic for non-FIFO
        if (!taskList.isEmpty()) {
            Task task = taskList.remove(0); // Always execute the first task in the list
            task.complete();
        }
    }
}
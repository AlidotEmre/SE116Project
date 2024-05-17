public class Event {
    private int time;
    private Job job;

    public Event(int time, Job job) {
        this.time = time;
        this.job = job;
    }

    public int getTime() {
        return time;
    }

    public Job getJob() {
        return job;
    }
}

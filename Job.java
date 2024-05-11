import java.time.LocalTime;

public class Job {
    private String id;
    private JobType type;
    private int startTime;  // Başlangıç zamanı dakika olarak ifade edilmiştir
    private int duration;   // Süre dakika olarak ifade edilmiştir
    private LocalTime deadline;  // Teslim tarihi saat ve dakika olarak ifade edilmiştir

    public Job(String id, JobType type, int startTime, int duration, LocalTime deadline) {
        this.id = id;
        this.type = type;
        this.startTime = startTime;
        this.duration = duration;
        this.deadline = deadline;
    }

    // Getter ve setter metodları
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public JobType getType() {
        return type;
    }

    public void setType(JobType type) {
        this.type = type;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public LocalTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalTime deadline) {
        this.deadline = deadline;
    }

    @Override
    public String toString() {
        return "Job{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", startTime=" + startTime +
                ", duration=" + duration +
                ", deadline=" + deadline +
                '}';
    }
}

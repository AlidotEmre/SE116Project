public class SpecificJob extends Job {
    public SpecificJob(String jobId, String jobType, int startTime, int duration) {
        super(jobId, jobType, startTime, duration);
    }

    @Override
    public void startNextTask() {
        // Özelleştirilmiş görev başlatma mantığı burada yer alacak
    }

    @Override
    public void updateJobStatus() {
        // İş durumunu güncelleme mantığı burada uygulanacak
    }
}
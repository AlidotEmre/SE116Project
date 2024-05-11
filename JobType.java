public class JobType {
    private String id;    // İş türünün benzersiz tanımlayıcısı
    private String name;  // İş türünün adı

    public JobType(String id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getter ve setter metodları
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "JobType{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}

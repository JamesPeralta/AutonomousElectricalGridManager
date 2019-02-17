public class UsageModel {
    public String date;
    public Double usage;
    public String hour;

    public String fullDate;

    public UsageModel(String d, Double u, String h, String fd) {
        this.date = d;
        this.usage = u;
        this.hour = h;
        this.fullDate = fd;
    }
}

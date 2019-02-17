import java.math.BigDecimal;
import java.math.RoundingMode;

public class ResEnergyModel {
    public int loc;
    public int interval;
    public Double usage;
    public String date;
    public String hour;
    public String min;

    public String key;

    public ResEnergyModel(int l, int i, Double u, String d, String hr, String min) {
        this.loc = l;
        this.date = d;
        this.interval = i;
        this.usage = u;
        this.hour = hr;
        this.min = min;

        this.key = this.date + this.hour;
    }

    public String toString() {
        return this.date + " " + this.hour + ":" + this.min + "," + this.usage;
    }

    public String dateString() {
        return this.date + " " + this.hour + ":" + this.min;
    }

    public static ResEnergyModel createResEnergyModel(String line) {
        String[] splits = line.split(",");
        if (splits.length != 4) {
            return null;
        }

        try {
            int l = Integer.parseInt(splits[0]);
            int i = Integer.parseInt(splits[2]);

            String d = splits[1];
            String date = d.substring(0, 4) + "-" + d.substring(4, 6) + "-" + d.substring(6, 8);
            String hr = d.substring(8, 10);
            String min = d.substring(10, 12);

            if (date.isEmpty() || hr.isEmpty() || min.isEmpty() || date.length() != 10 || hr.length() != 2 || min.length() != 2) {
                return null;
            }

            Double toBeTruncated = new Double(splits[3]);
            Double truncatedDouble = BigDecimal.valueOf(toBeTruncated)
                .setScale(6, RoundingMode.HALF_UP)
                .doubleValue();

            return new ResEnergyModel(l, i, truncatedDouble, date, hr, min);
        } catch (Exception err) {
            System.out.println(err.getCause().toString());
            return null;
        }
    }
}

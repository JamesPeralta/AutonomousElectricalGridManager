import java.math.BigDecimal;
import java.math.RoundingMode;

public class TempModel {
    public String date;
    public Double temp;
    public String month;

    public TempModel(String d, Double t, String m) {
        this.date = d;
        this.temp = t;
        this.month = m;
    }

    public static TempModel createTempModel(String line) {
        String[] splits = line.split(",");
        if (splits.length < 6) {
            return null;
        }

        try {
            String date = splits[0].substring(1, splits[0].length() - 1);
            String mon = splits[2].substring(1, splits[2].length() - 1);

            Double toBeTruncated = new Double(splits[5].substring(1, splits[5].length() - 1));
            Double truncatedDouble = BigDecimal.valueOf(toBeTruncated)
                .setScale(6, RoundingMode.HALF_UP)
                .doubleValue();

            if (date.isEmpty() || mon.isEmpty()) {
                return null;
            }

            return new TempModel(date, truncatedDouble, mon);
        } catch(Exception err) {
            System.out.println(err.toString());
            return null;
        }
    }
}

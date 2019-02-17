public class TempModel2 {
    public String date;
    public Double temp;
    public Integer month;
    public Integer hour;

    public TempModel2(String date, Double temp, Integer month, Integer hour) {
        this.date = date;
        this.temp = temp;
        this.month = month;
        this.hour = hour;
    }

    public String toString() {
        return this.date + "," + this.month + "," + this.hour  + "," + this.temp;
    }

    public static TempModel2 createTempModel2(String line, String year) {
        String[] splits = line.split(",");
        if (splits.length < 6) {
            return null;
        }

        try {
            String date;
            Double temp;
            Integer mon;
            Integer hr;

            if (year.equals("2018") || year.equals("2019")) {
                date = splits[0].split(" ")[0];
                temp = new Double(splits[5]);
                mon = Integer.parseInt(splits[2]);
                hr = Integer.parseInt(splits[4].split(":")[0]);
            } else {
                date = splits[0].substring(1, splits[0].length() - 1).split(" ")[0];
                temp = new Double(splits[5].substring(1, splits[5].length() - 1));
                mon = Integer.parseInt(splits[2].substring(1, splits[2].length() - 1));
                hr = Integer.parseInt(splits[4].substring(1, splits[4].length() - 1).split(":")[0]);
            }

            if (date.isEmpty() || mon < 1 || hr < 0) {
                return null;
            }

            return new TempModel2(date, temp, mon, hr);
        } catch(Exception err) {
            System.out.println(err.toString());
            return null;
        }
    }
}

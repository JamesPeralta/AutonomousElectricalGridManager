import java.io.*;
import java.util.ArrayList;

public class ProcessTemps {
    public ArrayList<TempModel2> temps;

    public ProcessTemps() {
        this.temps = new ArrayList<>();
    }

    public void processAllYears() {
        String[] years = { "2010", "2011", "2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019"};

        for (String year: years) {
            this.processYear(year);
        }
        this.writeToFile();
    }

   private void processYear(String year) {
        String filename = year + "-temp.csv";

        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader("/home/satyaki/Projects/Hackathon2019/hackathon-data-processor/temp/" + filename));
            String line = reader.readLine();
            while (line != null && line.length() > 0) {
                TempModel2 model = TempModel2.createTempModel2(line, year);
                if (model != null) {
                    this.temps.add(model);
                }
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException err) {
            System.out.println(err.getCause().toString());
        }
        System.out.println("Done temp file: " + filename + ", Size: " + this.temps.size());
    }

    private void writeToFile() {
        try {
            File file = new File("/home/satyaki/Projects/Hackathon2019/hackathon-data-processor/out/all-temps.csv");
            PrintWriter printWriter = new PrintWriter(file);

            int wroteLines = 0;
            for (int i = 0; i < this.temps.size(); i++) {
                TempModel2 model = this.temps.get(i);
                if (model != null) {
                    printWriter.println(model.toString());
                    wroteLines++;
                }
            }

            printWriter.close();
            System.out.println("Wrote " + wroteLines + " lines");
        } catch (Exception err) {
            System.out.println(err.getCause().toString());
        }
    }

    public static void main(String[] args) {
        ProcessTemps temps = new ProcessTemps();
        temps.processAllYears();
    }
}

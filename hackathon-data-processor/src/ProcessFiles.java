import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;


public class ProcessFiles {
    private String year = "2016";

    public HashMap<String, ArrayList<ResEnergyModel>> energyMap;
    public HashMap<String, TempModel> tempMap;
    public HashMap<String, UsageModel> usageMap;

    public ProcessFiles() {
        this.energyMap = new HashMap<String, ArrayList<ResEnergyModel>>();
        this.tempMap = new HashMap<String, TempModel>();
        this.usageMap = new HashMap<String, UsageModel>();
    }

    public void processEnergy() {
        String filename = year + "-energy.csv";
        System.out.println("Starting energy file: " + filename);

        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader("/home/satyaki/Projects/hackathon-data-processor/energy/" + filename));
            String line = reader.readLine();
            line = reader.readLine();
            while (line != null && line.length() > 0) {
                ResEnergyModel model = ResEnergyModel.createResEnergyModel(line);
                if (model != null) {
                    ArrayList<ResEnergyModel> list = this.energyMap.get(model.key);
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    list.add(model);
                    this.energyMap.put(model.key, list);
                }
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException err) {
            System.out.println(err.getCause().toString());
        }
        System.out.println("Done reading energy files. " + this.energyMap.size());
        this.cleanUpEnergy();
    }

    public void processTemp() {
        String filename = year + "-temp.csv";
        System.out.println("Starting temp file: " + filename);

        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader("/home/satyaki/Projects/hackathon-data-processor/temp/" + filename));
            String line = reader.readLine();
            while (line != null && line.length() > 0) {
                TempModel model = TempModel.createTempModel(line);
                if (model != null) {
                    this.tempMap.put(model.date, model);
                }
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException err) {
            System.out.println(err.getCause().toString());
        }
        System.out.println("Done temp file: " + filename + ", Size: " + this.tempMap.size());
    }

    public void generateCombined() {
        try {
            File file = new File("/home/satyaki/Projects/hackathon-data-processor/out/" + this.year + "-combined.csv");
            PrintWriter printWriter = new PrintWriter(file);

            int wroteLines = 0;
            for (String key: this.usageMap.keySet()) {
                UsageModel usage = this.usageMap.get(key);
                TempModel temp = this.tempMap.get(key);

                if (temp != null) {
                    printWriter.println(usage.date + "," + usage.hour + "," + temp.temp + "," + usage.usage);
                    wroteLines++;
                }
            }

            printWriter.close();
            System.out.println("Wrote " + wroteLines + " lines");
        } catch (Exception err) {
            System.out.println(err.getCause().toString());
        }
    }

    private void cleanUpEnergy() {
        System.out.println("Usage data before: " + this.usageMap.size());
        for (String key: this.energyMap.keySet()) {
            ArrayList<ResEnergyModel> modelList = this.energyMap.get(key);
            HashMap<Integer, ArrayList<ResEnergyModel>> sortedModelMap = new HashMap<>();

            for (int i = 0; i < modelList.size(); i++) {
                ResEnergyModel model = modelList.get(i);

                ArrayList<ResEnergyModel> sortedModelList = sortedModelMap.get(model.loc);
                if (sortedModelList == null) {
                    sortedModelList = new ArrayList<>();
                }
                sortedModelList.add(model);
                sortedModelMap.put(model.loc, sortedModelList);
            }

            Double usage = 0.0;
            for (Integer loc: sortedModelMap.keySet()) {
                ArrayList<ResEnergyModel> locList = sortedModelMap.get(loc);
                Double total = 0.0;
                for (int x = 0; x < locList.size(); x++) {
                    total += locList.get(x).usage;
                }

                Double truncatedDouble = BigDecimal.valueOf(total/locList.size())
                .setScale(6, RoundingMode.HALF_UP)
                .doubleValue();
                usage += truncatedDouble;
            }

            ResEnergyModel firstModel = modelList.get(0);
            if (firstModel != null) {
                UsageModel usageModel = new UsageModel(firstModel.date, usage, firstModel.hour, firstModel.dateString());
                usageMap.put(usageModel.fullDate, usageModel);
            } else {
                System.out.println("WTF happened." + key);
            }
        }
        System.out.println("Usage data after: " + this.usageMap.size());
    }
}

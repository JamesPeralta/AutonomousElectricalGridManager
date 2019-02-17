import java.io.*;
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
            reader = new BufferedReader(new FileReader("/home/satyaki/Projects/Hackathon2019/hackathon-data-processor/energy/" + filename));
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
            reader = new BufferedReader(new FileReader("/home/satyaki/Projects/Hackathon2019/hackathon-data-processor/temp/" + filename));
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
            File file = new File("/home/satyaki/Projects/Hackathon2019/hackathon-data-processor/out/" + this.year + "-combined.csv");
            PrintWriter printWriter = new PrintWriter(file);

            int wroteLines = 0;
            for (String key: this.usageMap.keySet()) {
                UsageModel usage = this.usageMap.get(key);
                TempModel temp = this.tempMap.get(key);

                if (temp != null) {
                    printWriter.println(usage.date + "," + temp.month + "," + usage.hour + "," + temp.temp + "," + usage.usage);
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

            Double usage = 0.0;
            for (int i = 0; i < modelList.size(); i++) {
                ResEnergyModel model = modelList.get(i);
                if (model != null) {
                    usage += model.usage;
                }
            }

            ResEnergyModel firstModel = modelList.get(0);
            usage = usage/modelList.size();

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

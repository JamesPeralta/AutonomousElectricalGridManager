public class Main {
    public static void main(String[] args) {
        ProcessFiles eng = new ProcessFiles();
        eng.processEnergy();
        eng.processTemp();
        eng.generateCombined();
    }
}

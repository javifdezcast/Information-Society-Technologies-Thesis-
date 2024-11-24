import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;

/**
 * Main class to run multiple instances of a simulated attack, manage results, and save them to a file.
 */
public class Main {

    private static final int DURATION = 3 * 60 * 1000; // 3 minutes in milliseconds
    private static final int[] INSTANCE_COUNTS = {30, 30, 30, 30, 30, 15, 15, 15, 15, 10, 10};
    private static final int NUM_REQUESTS = 310;
    private static final String[] VARIABLE_NAMES = {"ResponseCode", "AttackerSendingTime", "VictimReceptionTime",
            "VictimSendingTime", "AttackerReceptionTime", "ProcessingTime"};
    private static final int SIZE_VARIANTS = 6;

    public static void main(String[] args) {
        String filePath = generateFilePath();
        Map<String, Map<String, Map<String, List<Double>>>> dataStructure = initializeDataStructure();

        try (FileWriter writer = new FileWriter(filePath)) {
            performIterations(writer, dataStructure, filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Performs multiple iterations of attacks with varying configurations, saving results to a file.
     */
    private static void performIterations(FileWriter writer, Map<String, Map<String, Map<String, List<Double>>>> dataStructure, String filePath)
            throws IOException, InterruptedException, ExecutionException {
        for (int iteration = 0; iteration < 5; iteration++) {
            int offset = new Random(System.currentTimeMillis()).nextInt(6);

            System.out.println("Iteration " + iteration);
            for (int sizeVariant = 0; sizeVariant < SIZE_VARIANTS; sizeVariant++) {
                int size = (sizeVariant + offset) % SIZE_VARIANTS;
                performAttackForFormats(writer, filePath, iteration, size);
            }
        }
    }

    /**
     * Calculates the start time rounded up to the next minute mark.
     */
    private static long calculateRoundedStartTime() {
        LocalDateTime now = LocalDateTime.now().plusMinutes(1).withSecond(0).withNano(0);
        return now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * Performs an attack for different formats and sizes, and writes the start time to the file.
     */
    private static void performAttackForFormats(FileWriter writer, String filePath, int iteration, int size)
            throws IOException, InterruptedException, ExecutionException {
        DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("HH:mm:ss");

        for (int formatIndex = 0; formatIndex < 2; formatIndex++) {
            long startTime = calculateRoundedStartTime();
            long endTime = startTime + DURATION;

            LocalDateTime actualStart = LocalDateTime.ofInstant(Instant.ofEpochMilli(startTime), TimeZone
                    .getDefault().toZoneId());
            writer.append("Size ").append(String.valueOf(size)).append(" format ")
                    .append(Constantes.IMAGE_SUFFIX[formatIndex]).append(" ").append(actualStart.format(formatterTime)).append("\n").flush();


            executeAttack(size, Constantes.IMAGE_SUFFIX[formatIndex], String.valueOf(iteration), filePath, INSTANCE_COUNTS[size], startTime, endTime);
            Thread.sleep(30 * 1000); // Pause between attacks
        }
    }

    /**
     * Executes an attack using a thread pool and collects results.
     */
    private static void executeAttack(int size, String format, String iteration, String filePath, int instances, long startTime, long endTime)
            throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(instances);
        List<Future<List<Double[]>>> futures = submitAttackTasks(executor, size, format, iteration, instances, startTime, endTime);

        Map<Integer, List<Double[]>> results = collectResults(futures);
        executor.shutdown();
        //saveResultsToFile(filePath, results);
    }

    /**
     * Submits attack tasks to the executor and returns a list of futures.
     */
    private static List<Future<List<Double[]>>> submitAttackTasks(ExecutorService executor, int size, String format, String iteration, int instances, long startTime, long endTime) {
        List<Future<List<Double[]>>> futures = new ArrayList<>();
        for (int i = 0; i < instances; i++) {
            futures.add(executor.submit(() -> new AtacanteMasivo().ataqueTemporal(startTime, endTime, String.valueOf(size), format, iteration)));
        }
        return futures;
    }

    /**
     * Collects results from each future and stores them in a map.
     */
    private static Map<Integer, List<Double[]>> collectResults(List<Future<List<Double[]>>> futures)
            throws ExecutionException, InterruptedException {
        Map<Integer, List<Double[]>> results = new HashMap<>();
        for (int i = 0; i < futures.size(); i++) {
            results.put(i, futures.get(i).get());
        }
        return results;
    }

    /**
     * Saves the results from the attack to the specified file.
     */
    private static void saveResultsToFile(String filePath, Map<Integer, List<Double[]>> results) {
        try (FileWriter writer = new FileWriter(filePath, true)) {
            for (Map.Entry<Integer, List<Double[]>> entry : results.entrySet()) {
                writer.append("Thread ").append(String.valueOf(entry.getKey())).append("\n");
                for (Double[] row : entry.getValue()) {
                    writer.append(String.join(",", Arrays.toString(row))).append("\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the data structure to store results for different configurations.
     */
    private static Map<String, Map<String, Map<String, List<Double>>>> initializeDataStructure() {
        Map<String, Map<String, Map<String, List<Double>>>> dataStructure = new HashMap<>();
        for (String suffix : Constantes.IMAGE_SUFFIX) {
            Map<String, Map<String, List<Double>>> sizes = new HashMap<>();
            for (int j = 0; j < 10; j++) {
                Map<String, List<Double>> variables = initializeVariables();
                sizes.put(String.valueOf(j), variables);
            }
            dataStructure.put(suffix, sizes);
        }
        return dataStructure;
    }

    /**
     * Initializes the variable structure with an empty list for each variable name.
     */
    private static Map<String, List<Double>> initializeVariables() {
        Map<String, List<Double>> variables = new HashMap<>();
        for (String variable : VARIABLE_NAMES) {
            variables.put(variable, new ArrayList<>());
        }
        return variables;
    }

    /**
     * Inserts data values into the final results structure.
     */
    private static void storeData(Map<String, Map<String, Map<String, List<Double>>>> finalResults,
                                  int size, int format, List<Double[]> variables) {
        for (int i = 0; i < variables.size(); i++) {
            for (int j = 0; j < variables.get(i).length; j++) {
                finalResults.get(Constantes.IMAGE_SUFFIX[format]).get(String.valueOf(size)).get(VARIABLE_NAMES[j]).add(variables.get(i)[j]);
            }
        }
    }

    /**
     * Generates a unique file path for result output.
     */
    private static String generateFilePath() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm");
        return Constantes.RES_FILEPATH + "Resultados" + now.format(formatter) + ".csv";
    }
}

import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ExperimentoPreliminar implements Experimento{
    
    private final int time;
    private final int sizeVariants;
    private final String[] variableNames;
    private final int[] instanceCounts;
    
    public ExperimentoPreliminar(int time, int sizeVariants, int[] instanceCounts, String[] variableNames) {
        this.time = time;
        this.sizeVariants = sizeVariants;
        this.instanceCounts = instanceCounts;
        this.variableNames = variableNames;
    }

    @Override
    public void ejecutar(FileWriter fw) throws IOException, ExecutionException, InterruptedException {
        this.performIterations(fw);

    }

    /**
     * Performs multiple iterations of attacks with varying configurations, saving results to a file.
     */
    private void performIterations(FileWriter writer)
            throws IOException, InterruptedException, ExecutionException {
        for (int iteration = 0; iteration < 5; iteration++) {
            int offset = new Random(System.currentTimeMillis()).nextInt(6);

            System.out.println("Iteration " + iteration);
            for (int sizeVariant = 0; sizeVariant < this.sizeVariants; sizeVariant++) {
                int size = (sizeVariant + offset) % this.sizeVariants;
                performAttackForFormats(writer, iteration, size);
            }
        }
    }

    /**
     * Calculates the start time rounded up to the next minute mark.
     */
    private long calculateRoundedStartTime() {
        LocalDateTime now = LocalDateTime.now().plusMinutes(1).withSecond(0).withNano(0);
        return now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * Performs an attack for different formats and sizes, and writes the start time to the file.
     */
    private void performAttackForFormats(FileWriter writer, int iteration, int size)
            throws IOException, InterruptedException, ExecutionException {
        DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("HH:mm:ss");

        for (int formatIndex = 0; formatIndex < 2; formatIndex++) {
            long startTime = calculateRoundedStartTime();
            long endTime = startTime + this.time;

            LocalDateTime actualStart = LocalDateTime.ofInstant(Instant.ofEpochMilli(startTime), TimeZone
                    .getDefault().toZoneId());
            writer.append("Size ").append(String.valueOf(size)).append(" format ")
                    .append(Constantes.IMAGE_SUFFIX[formatIndex]).append(" ").append(actualStart.format(formatterTime)).append("\n").flush();


            executeAttack(size, Constantes.IMAGE_SUFFIX[formatIndex], String.valueOf(iteration), this.instanceCounts[size], startTime, endTime);
            Thread.sleep(30 * 1000); // Pause between attacks
        }
    }

    /**
     * Executes an attack using a thread pool and collects results.
     */
    private void executeAttack(int size, String format, String iteration, int instances, long startTime, long endTime)
            throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(instances);
        List<Future<List<Double[]>>> futures = submitAttackTasks(executor, size, format, iteration, instances, startTime, endTime);
        executor.shutdown();
        //saveResultsToFile(filePath, results);
    }

    /**
     * Submits attack tasks to the executor and returns a list of futures.
     */
    private List<Future<List<Double[]>>> submitAttackTasks(ExecutorService executor, int size, String format, String iteration, int instances, long startTime, long endTime) {
        List<Future<List<Double[]>>> futures = new ArrayList<>();
        for (int i = 0; i < instances; i++) {
            futures.add(executor.submit(() -> new AtacanteMasivo().ataqueTemporal(startTime, endTime, String.valueOf(size), format, iteration)));
        }
        return futures;
    }

    /**
     * Saves the results from the attack to the specified file.
     */
    private void saveResultsToFile(String filePath, Map<Integer, List<Double[]>> results) {
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
    private Map<String, Map<String, Map<String, List<Double>>>> initializeDataStructure() {
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
    private Map<String, List<Double>> initializeVariables() {
        Map<String, List<Double>> variables = new HashMap<>();
        for (String variable : this.variableNames) {
            variables.put(variable, new ArrayList<>());
        }
        return variables;
    }

    /**
     * Inserts data values into the final results structure.
     */
    private void storeData(Map<String, Map<String, Map<String, List<Double>>>> finalResults,
                                  int size, int format, List<Double[]> variables) {
        for(int i = 0; i < variables.size(); i++) {
            for (int j = 0; j < variables.get(i).length; j++) {
                finalResults.get(Constantes.IMAGE_SUFFIX[format]).get(String.valueOf(size)).get(this.variableNames[j]).add(variables.get(i)[j]);
            }
        }
    }

}

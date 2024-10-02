import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;

public class Main {

    private static int duracion = 10;
    private static int numInstances = 3;
    private static int numSolicitudes = 310;
    private static String[] variableNames = {"ResponseCode","AttackerSendingTime","VictimReceptionTime","VictimSendingTime","AttackerReceptionTime","ProcessingTime"};
    private static int tamanios = 10;

    public static void main(String[] args){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm");
        Map<String, Map<String, Map<String, List<Double>>>> formatos = getResultDataStruct();
        try{
            for(int i = 0 ; i<2 ; i++){
                for(int j = 0 ; j < 2 ; j++){
                    String fullPath = Atacante.RES_FILEPATH + "Resultados" + now.format(formatter) +
                            Atacante.IMAGE_PREFIX + i + Atacante.IMAGE_SUFFIX[j] + ".csv";
                    System.out.println("Tamanio " + i + " formato " + Atacante.IMAGE_SUFFIX[j]);
                    ataqueMasivo(Atacante.IMAGE_PREFIX + i + Atacante.IMAGE_SUFFIX[j], fullPath);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void ataqueMasivo(String imagen, String fullPath) throws ExecutionException, InterruptedException {
        // Create a thread pool with a fixed number of threads
        ExecutorService executor = Executors.newFixedThreadPool(numInstances);

        // List to store the Future results
        List<Future<List<Double[]>>> futures = new ArrayList<>();

        // Submit tasks to the executor
        for (int i = 0; i < numInstances; i++) {
            Future<List<Double[]>> future = executor.submit(new Callable<List<Double[]>>() {
                @Override
                public List<Double[]> call() {
                    AtacanteMasivo atacante = new AtacanteMasivo();
                    return atacante.ataqueTemporal(duracion, imagen);
                }
            });
            futures.add(future);
        }

        // Map to store results from all threads
        Map<Integer, List<Double[]>> results = new HashMap<>();

        // Collect the results from all futures
        for (int i = 0; i < futures.size(); i++) {
            List<Double[]> result = futures.get(i).get();
            results.put(i, result); // Store each result by thread id
        }

        // Shutdown the executor after the attack duration
        executor.shutdown();

        // Save the results to file after execution
        saveTemporalResultsToFile(fullPath, results);
    }

    private static void saveTemporalResultsToFile(String fullPath, Map<Integer, List<Double[]>> results) {
        try (FileWriter writer = new FileWriter(fullPath, true)) {
            for (Map.Entry<Integer, List<Double[]>> entry : results.entrySet()) {
                int threadId = entry.getKey();
                List<Double[]> data = entry.getValue();

                writer.append("Thread ").append(String.valueOf(threadId)).append("\n");

                for (Double[] row : data) {
                    for (int i = 0; i < row.length; i++) {
                        writer.append(String.valueOf(row[i]));
                        if (i != row.length - 1) {
                            writer.append(",");
                        }
                    }
                    writer.append("\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveResultsToFile(String fullPath, Map<String, Map<String, Map<String, List<Double>>>> formatos) {
        try(FileWriter writer = new FileWriter(fullPath)){
            for(int i = 0 ; i < variableNames.length ; i++){
                writer.append(variableNames[i]).append("\n");
                for(int j = 0 ; j < Atacante.IMAGE_SUFFIX.length ; j++){
                    writer.append(Atacante.IMAGE_SUFFIX[j]).append("\n");
                    for(int k = 0 ; k < tamanios ; k++){
                        writer.append(String.valueOf(k)).append(',');
                        for(int l = 0 ; l < formatos.get(Atacante.IMAGE_SUFFIX[j]).get(""+k).get(variableNames[i]).size(); l++){
                            insertDataValue(formatos, l, j, k, i, writer);
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void insertDataValue(Map<String, Map<String, Map<String, List<Double>>>> formatos, int l, int j, int k, int i, FileWriter writer) throws IOException {
        if(l != formatos.get(Atacante.IMAGE_SUFFIX[j]).get(""+ k).get(variableNames[i]).size() - 1) {
            writer.append(
                    String.valueOf(
                            formatos.get(Atacante.IMAGE_SUFFIX[j]).
                                    get("" + k).get(variableNames[i]).get(l)
                    )).append(',');
        }else{
            writer.append(
                    String.valueOf(
                            formatos.get(Atacante.IMAGE_SUFFIX[j]).
                                    get("" + k).get(variableNames[i]).get(l)
                    )).append('\n');
        }
    }

    private static Map<String, Map<String, Map<String, List<Double>>>>  getResultDataStruct() {
        Map<String, Map<String, Map<String, List<Double>>>> formatos = new HashMap<>();
        for(int i = 0; i < Atacante.IMAGE_SUFFIX.length; i++){
            Map<String, Map<String, List<Double>>> tamanios = new HashMap<>();
            for(int j = 0 ; j < 10 ; j++){
                Map<String, List<Double>> variables = new HashMap<>();
                for(int k = 0 ; k < variableNames.length ; k++){
                    variables.put(variableNames[k], new ArrayList<>());
                }
                tamanios.put("" + j, variables);
            }
            formatos.put(Atacante.IMAGE_SUFFIX[i], tamanios);
        }
      return formatos;
    }

    private static void storeData(Map<String, Map<String, Map<String, List<Double>>>> finalResults,
                                  int size, int format, List<Double[]> variables){
        for(int i = 0 ; i < variables.size() ; i++){
            for(int j = 0 ; j < variables.get(i).length ; j++){
                finalResults.get(Atacante.IMAGE_SUFFIX[format]).get(""+size).get(variableNames[j]).add(variables.get(i)[j]);
            }
        }
    }
}

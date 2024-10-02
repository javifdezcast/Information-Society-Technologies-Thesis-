import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {

    private static int duracion = 10;
    private static int numInstances = 50;
    private static int numSolicitudes = 310;
    private static String[] variableNames = {"ResponseCode","AttackerSendingTime","VictimReceptionTime","VictimSendingTime","AttackerReceptionTime","ProcessingTime"};
    private static int tamanios = 10;

    public static void main(String[] args){
        Atacante atacante = new AtacanteMasivo();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm");
        String fullPath = Atacante.RES_FILEPATH + "Resultados" + now.format(formatter) + ".csv";
        Map<String, Map<String, Map<String, List<Double>>>> formatos = getResultDataStruct();
        try{
            for(int i = 0 ; i<tamanios ; i++){
                for(int j = 0 ; j < 2 ; j++){
                    storeData(formatos, i, j, atacante.ataqueCuantitativo(0, numSolicitudes,
                            Atacante.IMAGE_PREFIX + i + Atacante.IMAGE_SUFFIX[j]));
                    System.out.println("Tamanio " + i + " formato " + Atacante.IMAGE_SUFFIX[j]);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        saveResultsToFile(fullPath, formatos);
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

/*
// Create a thread pool with a fixed number of threads
ExecutorService executor = Executors.newFixedThreadPool(numInstances);
    // Submit tasks to the executor
        for (int i = 0; i < numInstances; i++) {
        int id = i;
        Callable callable = () -> {
            Atacante atacante = new AtacanteMasivo();
            atacante.atacar(id, duracion, fullPath, Atacante.PERRO3);
            return null;
        };
        executor.submit(callable);
    }
    // Shutdown the executor after the attack duration
        try {
        Thread.sleep(duracion *60 * 1000); // Convert seconds to milliseconds
    } catch (InterruptedException e) {
        e.printStackTrace();
    } finally {
        executor.shutdown();
    }
 */
}

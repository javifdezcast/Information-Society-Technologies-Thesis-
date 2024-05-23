import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    private static int duracion = 120;

    public static void main(String[] args){
        int numInstances = 20; // Number of instances of AtacanteMasivo
        int attackDuration = 60; // Duration of the attack in seconds

        // Create a thread pool with a fixed number of threads
        ExecutorService executor = Executors.newFixedThreadPool(numInstances);

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm");
        String filename = "Resultados" + now.format(formatter) + ".csv";
        String fullPath = Paths.get(Atacante.RES_FILEPATH, filename).toString();
        File resultado = new File(fullPath);
        try (FileWriter writer = new FileWriter(resultado)) {
            writer.append("ResponseCode,SendingTime,ReceptionTime,ProcessingTime\n");

        }catch (IOException e){

        }
            // Submit tasks to the executor
        for (int i = 0; i < 2; i++) {
            Callable callable = () -> {
                Atacante atacante = new AtacanteMasivo();
                atacante.atacar(duracion, filename);
                return null;
            };
            executor.submit(callable);
        }

        // Shutdown the executor after the attack duration
        try {
            Thread.sleep(duracion * 1000); // Convert seconds to milliseconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }
}

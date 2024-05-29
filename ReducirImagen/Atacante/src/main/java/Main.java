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

    private static int duracion = 10;
    private static int numInstances = 50;

    public static void main(String[] args){

        // Create a thread pool with a fixed number of threads
        ExecutorService executor = Executors.newFixedThreadPool(numInstances);

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm");
        String fullPath = Atacante.RES_FILEPATH + "Resultados" + now.format(formatter);
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
    }
}

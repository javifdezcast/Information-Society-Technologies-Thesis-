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

        // Submit tasks to the executor
        for (int i = 0; i < 2; i++) {
            Callable callable = () -> {
                Atacante atacante = new AtacanteMasivo();
                atacante.atacar(duracion);
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

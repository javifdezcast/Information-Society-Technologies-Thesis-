import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public interface Experimento {
    void ejecutar(FileWriter fw) throws IOException, ExecutionException, InterruptedException;
}

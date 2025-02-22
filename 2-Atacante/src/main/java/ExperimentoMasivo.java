import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ExperimentoMasivo implements Experimento {
    private final int time;
    private final String format;
    private final int size;
    private final int iteraciones;

    public ExperimentoMasivo(int time, String format, int size, int iteraciones) {
        this.time = time;
        this.format = format;
        this.size = size;
        this.iteraciones = iteraciones;
    }


    @Override
    public void ejecutar(FileWriter writer)
            throws IOException, InterruptedException, ExecutionException {
        for (int i = 0; i < iteraciones; i++) {
            DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("HH:mm:ss");

            long startTime = calculateRoundedStartTime();
            long endTime = startTime + this.time;

            LocalDateTime actualStart = LocalDateTime.ofInstant(Instant.ofEpochMilli(startTime), TimeZone
                    .getDefault().toZoneId());
            writer.append("Size ").append(String.valueOf(size)).append(" format ")
                    .append(format).append(" ").append(actualStart.format(formatterTime)).append("\n").flush();
            executeAttack(startTime, endTime, i);
        }
        Thread.sleep(time); // Pause between attacks
    }

    /**
     * Calculates the start time rounded up to the next minute mark.
     */
    private long calculateRoundedStartTime() {
        LocalDateTime now = LocalDateTime.now().plusMinutes(1).withSecond(0).withNano(0);
        return now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }


   /**
    *  Executes an attack using a thread pool and collects results.
    */
    private void executeAttack(long startTime, long endTime, int iteracion)
            throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(iteraciones);
        submitAttackTasks(executor, startTime, endTime, iteracion);
        executor.shutdown();

    }

    private void submitAttackTasks(ExecutorService executor, long startTime, long endTime, int iteracion) {
        for (int i = 0; i < Constantes.INSTANCE_COUNTS[size]; i++) {
            executor.submit(() -> new AtacanteMasivoTemporal(String.valueOf(size), format, startTime, endTime,
                    String.valueOf(iteracion)).atacar());
        }
    }
}

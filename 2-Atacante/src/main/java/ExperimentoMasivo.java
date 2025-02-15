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
    private final int replicas;

    public ExperimentoMasivo(int time, String format, int size, int replicas) {
        this.time = time;
        this.format = format;
        this.size = size;
        this.replicas = replicas;
    }


    @Override
    public void ejecutar(FileWriter writer)
            throws IOException, InterruptedException, ExecutionException {
        DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("HH:mm:ss");

        long startTime = calculateRoundedStartTime();
        long endTime = startTime + this.time;

        LocalDateTime actualStart = LocalDateTime.ofInstant(Instant.ofEpochMilli(startTime), TimeZone
                .getDefault().toZoneId());
        writer.append("Size ").append(String.valueOf(size)).append(" format ")
                .append(format).append(" ").append(actualStart.format(formatterTime)).append("\n").flush();

        executeAttack(startTime, endTime);
        Thread.sleep((long) time); // Pause between attacks
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
    private void executeAttack(long startTime, long endTime)
            throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(replicas);
        submitAttackTasks(executor, startTime, endTime);
        executor.shutdown();

    }

    private void submitAttackTasks(ExecutorService executor, long startTime, long endTime) {
        for (int i = 0; i < replicas; i++) {
            executor.submit(() -> new AtacanteMasivo().ataqueTemporal(startTime, endTime, String.valueOf(size), format,
                    "0"));
        }
    }
}

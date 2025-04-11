import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;
import java.util.concurrent.*;


public class ExperimentoMasivo implements Experimento {
    private final int time;
    private final String format;
    private final int size;
    private final int iteraciones;
    private ClienteFirewall cliente;

    public ExperimentoMasivo(int time, String format, int size, int iteraciones) {
        this.time = time;
        this.format = format;
        this.size = size;
        this.iteraciones = iteraciones;
        try {
            this.cliente = new ClienteFirewall();
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    @Override
    public void ejecutar(FileWriter writer)
            throws IOException, InterruptedException, ExecutionException {
        for (int i = 0; i < iteraciones; i++) {
            int j = i+1;
            DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

            long startTime = calculateRoundedStartTime();
            long endTime = startTime + this.time;

            LocalDateTime actualStart = LocalDateTime.ofInstant(Instant.ofEpochMilli(startTime), TimeZone
                    .getDefault().toZoneId());

            System.out.println("Iteracion " + j + " comientza a las " + formatterTime.format(actualStart));
            writer.append(String.valueOf(size)).append(";")
                    .append(format).append(";").append(actualStart.format(formatterTime)).append("\n").flush();
            cliente.setUnblockTime(endTime);
            String[] respuesta = cliente.solicitudRespuesta();
            System.out.println("Iteracion " + j + " firewall responde " + respuesta[0]);
            executeAttack(startTime, endTime, i);
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
    *  Executes an attack using a thread pool and collects results.
    */
    private void executeAttack(long startTime, long endTime, int iteracion)
            throws ExecutionException, InterruptedException {

        ExecutorService executor = Executors.newFixedThreadPool(Constantes.INSTANCE_COUNTS[size]);
        submitAttackTasks(executor, startTime, endTime, iteracion);
        executor.shutdown();
        executor.awaitTermination(endTime-startTime+60000, TimeUnit.MILLISECONDS);
        Thread.sleep(60*1000);

    }

    private void submitAttackTasks(ExecutorService executor, long startTime, long endTime, int iteracion) {
        for (int i = 0; i < Constantes.INSTANCE_COUNTS[size]; i++) {
            executor.submit(() -> new AtacanteMasivoTemporal(String.valueOf(size), format, startTime, endTime,
                    String.valueOf(iteracion)).atacar());
        }
    }
}

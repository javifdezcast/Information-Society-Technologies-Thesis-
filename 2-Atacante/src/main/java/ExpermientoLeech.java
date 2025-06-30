import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

public class ExpermientoLeech implements Experimento {

    private final int time;
    private final String format;
    private final int size;
    private final int iteraciones;
    private ClienteFirewall clienteFW;
    private ClienteHTTP clienteHTTP;

    public ExpermientoLeech(int time, String format, int size, int iteraciones) {
        this.time = time;
        this.format = format;
        this.size = size;
        this.iteraciones = iteraciones;
        try {
            this.clienteFW = new ClienteFirewall();
            this.clienteHTTP  = new ClienteImagen(Constantes.IMAGE_PREFIX + size + format);
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
            clienteFW.setUnblockTime(endTime);
            String[] respuesta = clienteFW.solicitudRespuesta();
            System.out.println("Iteracion " + j + " firewall responde " + respuesta[0]);
            executeAttack(endTime);
        }
    }

    private void executeAttack(long endTime) {
        Random random = new Random();
        while(System.currentTimeMillis() < endTime) {
            clienteHTTP.solicitudRespuesta();
            float sleep = random.nextFloat() * 30 + 30;
            try {
                Thread.sleep((long) sleep*1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }

    private long calculateRoundedStartTime() {
        LocalDateTime now = LocalDateTime.now().plusMinutes(1).withSecond(0).withNano(0);
        return now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

}

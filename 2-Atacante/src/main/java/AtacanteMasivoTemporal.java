import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;


public class AtacanteMasivoTemporal implements IAtacante {

    private final Cliente cliente;
    private final String tamanio;
    private final String formato;
    private final int id;
    private final long inicio;
    private final long fin;
    private final String iteracion;

    public AtacanteMasivoTemporal(String tamanio, String formato, long inicio, long fin, String iteracion) {
        this.cliente = new ClienteHTTP(Constantes.IMAGE_PREFIX + tamanio + formato);
        this.id = new Random().nextInt(999999);
        this.tamanio = tamanio;
        this.formato = formato;
        this.inicio = inicio;
        this.fin = fin;
        this.iteracion = iteracion;
    }

    @Override
    public List<String[]> atacar() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss.SSS");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime inicioAtaque = LocalDateTime.ofInstant(Instant.ofEpochMilli(inicio), TimeZone
                        .getDefault().toZoneId());
        LocalDateTime finAtaque = LocalDateTime.ofInstant(Instant.ofEpochMilli(fin), TimeZone
                .getDefault().toZoneId());
        System.out.println("\tAtacante " + id + " comienza ataque a las " + formatter.format(now));
        System.out.println("\t\t" + id + " inicio: " + formatter.format(inicioAtaque) +", fin: " +
                formatter.format(finAtaque));

        List<String[]> resultados = new ArrayList<>();
        while (System.currentTimeMillis() < inicio) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        while (System.currentTimeMillis() < fin) {
            try {
                resultados.add(cliente.solicitudRespuesta());
            } catch (Exception ignored) {

            }
        }
        now = LocalDateTime.now();
        System.out.println("\tAtacante " + id + " finaliza ataque a las " + formatter.format(now));
        int aceptadas = 0;
        for (String[] resultado : resultados) {
            if (resultado[0].equals("200")) {
                aceptadas++;
            }
        }
        System.out.println("\t\t" + iteracion + ", " + tamanio + "," + formato + ", " + this.id + ", " + resultados.size() +
                ", " + aceptadas);
        return resultados;
    }
}
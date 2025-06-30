import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AtacanteMasivoCuantitativo implements IAtacante {
    private final Cliente cliente;
    private final String tamanio;
    private final String formato;
    private final int id;
    private final int solicitudes;

    public AtacanteMasivoCuantitativo(String tamanio, String formato, int solicitudes) throws IOException {
        this.cliente = new ClienteImagen(Constantes.IMAGE_PREFIX + tamanio + formato);
        this.id = new Random().nextInt(999999);
        this.tamanio = tamanio;
        this.formato = formato;
        this.solicitudes = solicitudes;
    }

    @Override
    public List<String[]> atacar(){
        int i = 0;
        List<String[]> resultados = new ArrayList<>();
        System.out.println("Iniciando ataque cuantitativo. Thread: " + this.id + ", numero de solicitudes: " + solicitudes + " minutos.");
        while (i < solicitudes) {
            try {
                i++;
                resultados.add(cliente.solicitudRespuesta());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        System.out.println("Fin ataque cuantitativo. Thread: " + this.id);
        return resultados;
    }
}

import java.util.List;

public interface Atacante {


    List<Double[]>  ataqueTemporal(long inicio, long fin, String tamanio, String formato, String iteracion) ;

    List<Double[]> ataqueCuantitativo(int solicitudes, String imagen);

}

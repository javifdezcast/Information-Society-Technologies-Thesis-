import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EstructuraDatosRecopilados {
    private List<LocalDateTime> tiempos;
    private List<String> cabeceras;
    private Long[][] datos;

    public EstructuraDatosRecopilados(List<List<String>> datos) {
        cabeceras = datos.get(0);
        tiempos = new ArrayList<>();
        for(int i=1;i<datos.size();i++) {
            tiempos.add(LocalDateTime.parse(datos.get(i).get(0)));
        }
        this.datos = new Long[tiempos.size()][cabeceras.size()-1];
        for(int i=1;i<datos.size();i++) {
            for(int j=1;j<datos.get(0).size();j++) {
                if(!datos.get(i).get(j).isEmpty()) {
                    this.datos[i - 1][j - 1] = Long.valueOf(datos.get(i).get(j));
                }else{
                    this.datos[i - 1][j] = 0L;
                }
            }
        }
    }
}

import java.io.FileWriter;
import java.util.List;
import java.util.Map;

public interface Atacante {

    String URL = "http://192.168.1.49:8080/function/reducirimagen";
    String IMAGE_FORMAT = "IMAGE_FORMAT";
    String IMAGE_SIZE = "IMAGE_SIZE";
    String IMAGE_HEIGHT = "IMAGE_HEIGHT";
    String IMAGE_WIDTH = "IMAGE_WIDTH";
    String CONTENT_TYPE = "CONTENT_TYPE";
    String TIMESTAMP = "TIMESTAMP";
    String IMAGE_PREFIX = "perro_";
    String[] IMAGE_SUFFIX = {".jpg", ".png"};
    String RES_FILEPATH = "C:\\Users\\jfdez.DESKTOP-2BOORBB.000\\Information-Society-Technologies-Thesis-\\ReducirImagen\\Atacante\\src\\main\\java\\resultados\\";

    String PROCESSING_TIME = "processingTime";
    String VICTIM_RECEPTION_TIME = "receptionTime";
    String VICTIM_SENDING_TIME = "sendingTime";
    String TIME = "time";

    void ataqueTemporal(int id, int time, String resultFileName, String imagen);

    List<Double[]> ataqueCuantitativo(int id, int solicitudes, String imagen);

}

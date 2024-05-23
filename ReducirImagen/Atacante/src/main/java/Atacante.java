public interface Atacante {

    String URL = "http://127.0.0.1:8080/function/reducirimagen";
    String IMAGE_FORMAT = "IMAGE_FORMAT";
    String IMAGE_SIZE = "IMAGE_SIZE";
    String IMAGE_HEIGHT = "IMAGE_HEIGHT";
    String IMAGE_WIDTH = "IMAGE_WIDTH";
    String CONTENT_TYPE = "CONTENT_TYPE";
    String TIMESTAMP = "TIMESTAMP";
    String FILEPATH = "C:\\Users\\jfdez\\OpenFaas\\ReducirImagen\\reducirimagen\\perro23.jpg";
    String RES_FILEPATH = "C:\\Users\\jfdez\\OpenFaas\\ReducirImagen\\Atacante\\";

    String PROCESSING_TIME = "processingTime";
    String TIME = "time";

    void atacar(int time, String resultFileName);
}

public interface Atacante {

    String URL = "http://192.168.1.49:8080/function/reducirimagen";
    String IMAGE_FORMAT = "IMAGE_FORMAT";
    String IMAGE_SIZE = "IMAGE_SIZE";
    String IMAGE_HEIGHT = "IMAGE_HEIGHT";
    String IMAGE_WIDTH = "IMAGE_WIDTH";
    String CONTENT_TYPE = "CONTENT_TYPE";
    String TIMESTAMP = "TIMESTAMP";
    String PERRO1 = "perro1.jpg";
    String PERRO2 = "perro2.png";
    String PERRO3 = "perro3.jpg";
    String RES_FILEPATH = "C:\\Users\\jfdez.DESKTOP-2BOORBB.000\\Information-Society-Technologies-Thesis-\\ReducirImagen\\Atacante\\src\\main\\java\\resultados\\";

    String PROCESSING_TIME = "processingTime";
    String TIME = "time";

    void atacar(int id, int time, String resultFileName, String imagen);
}

public class Constantes {
    
    /**
     * Constantes de conexión
     */
    public static String VICTIM_IP ="192.168.1.36";
    public static String URL_FUNCION = "http://" + VICTIM_IP + ":8080/function/reducirimagen";
    public static String URL_FIREWALL = "http://" + VICTIM_IP + ":5000/unblock";

    /**
     * Constantes de solicitudes http
     */
    public static String IMAGE_FORMAT = "IMAGE_FORMAT";
    public static String IMAGE_SIZE = "IMAGE_SIZE";
    public static String IMAGE_HEIGHT = "IMAGE_HEIGHT";
    public static String IMAGE_WIDTH = "IMAGE_WIDTH";
    public static String CONTENT_TYPE = "CONTENT_TYPE";
    public static String TIME = "time";
    public static String POST = "POST";

    /**
     * Constantes de parametrización de ataques
     */
    public static String IMAGE_PREFIX = "perro_";
    public static String[] IMAGE_SUFFIX = {".jpg", ".png"};
    public static String RES_FILEPATH = "C:\\Users\\jfdez.DESKTOP-2BOORBB.000\\Information-Society-Technologies-Thesis-" +
            "\\ReducirImagen\\Atacante\\src\\main\\java\\resultados\\";
    public static int[] INSTANCE_COUNTS = {30, 30, 10, 30, 30, 15, 15, 15, 15, 10, 10};

    /**
     * Constantes experimento preliminar 1
     */
    public static int NUM_REQUESTS = 310;
    public static String[] VARIABLE_NAMES = {"ResponseCode", "AttackerSendingTime", "VictimReceptionTime",
            "VictimSendingTime", "AttackerReceptionTime", "ProcessingTime"};

    /**
     * Constantes experimento preliminar 2
     */
    public static int SIZE_VARIANTS = 6;
    public static int DURATION_1 = 3 * 60 * 1000; // 3 minutes in milliseconds

    /**
     * Constantes experimento 1: ataque masivo
     */
    public static int DURATION_2 = 15 * 60 * 1000; // 3 minutes in milliseconds
    public static String RES_MASSIVO_FILEPATH = "C:\\Users\\jfdez\\Information-Society-Technologies-Thesis-\\3-Documentos\\Experimentos\\Experimento 3 Ataque Masivo\\";

}

import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Main class to run multiple instances of a simulated attack, manage results, and save them to a file.
 */
public class Main {


    public static void main(String[] args) {
        String filePath = generateFilePath();
        ExpermientoLeech expermientoLeech = new ExpermientoLeech(Constantes.DURATION_3, Constantes.IMAGE_SUFFIX[0], 2,
                10);
        try (FileWriter writer = new FileWriter(filePath)) {
            expermientoLeech.ejecutar(writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates a unique file path for result output.
     */
    private static String generateFilePath() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm");
        return Constantes.RES_LEECH_FILEPATH + "Resultados" + now.format(formatter) + ".csv";
    }

}

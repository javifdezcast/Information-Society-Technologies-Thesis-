import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.openfaas.model.IRequest;
import com.openfaas.model.IResponse;
import com.openfaas.model.Request;
import org.junit.Test;
import static org.junit.Assert.*;

import com.openfaas.function.Handler;
import com.openfaas.model.IHandler;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.html.HTML;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class HandlerTest {

    public static final String URL = "http://127.0.0.1:8080/function/reducirimagen";
    public static final String user = "admin";
    public static final String password = "3HZK8NPKEeaC";
    public static String IMAGE_FORMAT = "IMAGE_FORMAT";
    public static String IMAGE_SIZE = "IMAGE_SIZE";
    public static String IMAGE_HEIGHT = "IMAGE_HEIGHT";
    public static String IMAGE_WIDTH = "IMAGE_WIDTH";
    public static String CONTENT_TYPE = "CONTENT_TYPE";
    public static String DATE = "DATE";

    @Test public void handlerIsNotNull() {
        IHandler handler = new Handler();
        assertTrue("Expected handler not to be null", handler != null);
    }

    @Test
    public void reduceImagenGrande(){
        try {
            //Lectura y transformacvión de imagen a byte[]
            byte[] byteImage = Files.readAllBytes(Paths.get("C:\\Users\\jfdez\\OpenFaas\\ReducirImagen\\reducirimagen\\perro23.jpg"));
            //Creación de los campos de la cabecera
            HashMap<String, String> headers = new HashMap<>();
            headers.put(IMAGE_FORMAT, "jpg");
            headers.put(IMAGE_SIZE, String.valueOf(byteImage.length));
            headers.put(IMAGE_WIDTH, String.valueOf(867));
            headers.put(IMAGE_HEIGHT, String.valueOf(1300));
            headers.put(CONTENT_TYPE, "application/json");
            //Serializacion y construcción de solicitud
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode nodoSolicitud = mapper.createObjectNode();
            nodoSolicitud.put("image", byteImage);
            String cuerpo = nodoSolicitud.toString();
            IRequest solicitud = new Request(cuerpo, new HashMap<>());
            //Obtención de respuesta
            Handler handler = new Handler();
            IResponse respuesta  = handler.Handle(solicitud);
            String cuerpoRespuesta = respuesta.getBody();
            // Get the response

            JsonNode nodoConImagen = mapper.readTree(cuerpoRespuesta);
            String imagenEnBase64 = nodoConImagen.get("image").asText();
            byte[] img = java.util.Base64.getDecoder().decode(imagenEnBase64);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(img);

            //Procesamiento
            BufferedImage imagenProcesada = ImageIO.read(inputStream);

            JLabel label = new JLabel(new ImageIcon(imagenProcesada));
            JFrame frame = new JFrame("Imagen Procesada");
            frame.add(label);
            frame.setSize(200, 300);
            frame.setVisible(true);
            Thread.sleep(100000);

            String originalImagePath = "C:\\Users\\jfdez\\OpenFaas\\ReducirImagen\\reducirimagen\\perro23.jpg";
            String processedImagePath = originalImagePath.replace("23.jpg", "Procesada.jpg");

            try {
                File outputImageFile = new File(processedImagePath);
                ImageIO.write(imagenProcesada, "jpg", outputImageFile);
                System.out.println("Processed image saved successfully to: " + processedImagePath);
            } catch (IOException e) {
                System.out.println("Error occurred while saving the processed image: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (ProtocolException ex) {
            ex.printStackTrace();
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (JsonMappingException ex) {
            ex.printStackTrace();
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

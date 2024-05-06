import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.openfaas.model.IRequest;
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
import java.util.HashMap;

public class HandlerTest {

    public static final String URL = "http://127.0.0.1:8080/function/reducirimagen";
    public static final String user = "admin";
    public static final String password = "3HZK8NPKEeaC";

    @Test public void handlerIsNotNull() {
        IHandler handler = new Handler();
        assertTrue("Expected handler not to be null", handler != null);
    }

    @Test
    public void reduceImagenGrande(){
        try {

            Image perro = ImageIO.read(new File("C:\\Users\\jfdez\\OpenFaas\\ReducirImagen\\reducirimagen\\perro23.jpg"));
            BufferedImage buffProcesada = new BufferedImage(867, 1300, BufferedImage.TYPE_INT_RGB);
            Graphics2D bGr = buffProcesada.createGraphics();
            bGr.drawImage(perro, 0, 0, null);
            bGr.dispose();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(buffProcesada, "jpg", outputStream);
            byte[] byteProcesada = outputStream.toByteArray();

            //Serializacion y construcción de respuesta
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode nodoRespuesta = mapper.createObjectNode();
            nodoRespuesta.put("image", byteProcesada);
            String cuerpo = nodoRespuesta.toString();
            IRequest solicitud = new Request(cuerpo, new HashMap<>());





            String  respuesta = "";
            // Write the request body
            try{
                URL url = new URL(this.URL);
                URLConnection conexion = url.openConnection();
                HttpURLConnection conexionHTTP = (HttpURLConnection) conexion;
                conexionHTTP.setRequestMethod("POST");
                conexionHTTP.setDoOutput(true);
                byte[] input = cuerpo.getBytes("utf-8");
                OutputStream os = conexionHTTP.getOutputStream();
                os.write(input, 0, input.length);
                os.close();
                assertEquals(HttpURLConnection.HTTP_OK, conexionHTTP.getResponseCode());
                respuesta = conexionHTTP.getResponseMessage();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            // Get the response

            JsonNode nodoConImagen = mapper.readTree(respuesta);
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

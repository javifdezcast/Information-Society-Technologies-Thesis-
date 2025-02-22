import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ClienteHTTP implements Cliente {
    private HttpURLConnection conexion;
    private BufferedImage image;
    private byte[] byteImage;
    private ObjectMapper mapper;

    public ClienteHTTP(String imagen) {
        try {
            byteImage = Files.readAllBytes(Paths.get(imagen));
            image = ImageIO.read(new File(imagen));
            conexion = setUpConettion(imagen, byteImage, image);
            mapper = new ObjectMapper();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public String[] solicitudRespuesta() throws IOException {
        Long inicio = enviarSolicitud();
        String[] resultado = procesarRespuesta();
        String[] result = {resultado[0], String.valueOf(inicio),
                resultado[1],  resultado[2]};
        return result;
    }

    private Long enviarSolicitud() throws IOException {
        ObjectNode node = mapper.createObjectNode();
        node.put("image", byteImage);
        OutputStream stream = conexion.getOutputStream();
        Long inicio = System.currentTimeMillis();
        stream.write(node.toString().getBytes(StandardCharsets.UTF_8));
        stream.flush();
        return inicio;
    }

    private String[] procesarRespuesta() throws IOException {
        int responseCode = 0;
        try {
            responseCode = conexion.getResponseCode();
        }catch (Exception ignored){

        }
        Long fin = System.currentTimeMillis();
        String processingTime = "";
        if(responseCode==200){
            InputStream input = conexion.getInputStream();
            String responseBody = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            input.close();
            JsonNode jsonResponse = mapper.readTree(responseBody);
            processingTime = jsonResponse.get(Constantes.TIME).asText();
        }
        return new String[]{String.valueOf(responseCode), String.valueOf(fin), processingTime};
    }

    private HttpURLConnection setUpConettion(String imagen, byte[] byteImage, BufferedImage image) throws IOException {
        java.net.URL url = new URL(Constantes.URL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty(Constantes.IMAGE_FORMAT, Files.probeContentType(Paths.get(imagen)));
        con.setRequestProperty(Constantes.IMAGE_SIZE, String.valueOf(byteImage.length));
        con.setRequestProperty(Constantes.IMAGE_WIDTH, String.valueOf(image.getWidth()));
        con.setRequestProperty(Constantes.IMAGE_HEIGHT, String.valueOf(image.getHeight()));
        con.setRequestProperty(Constantes.CONTENT_TYPE, "application/json");
        con.setConnectTimeout(180*1000);
        con.setReadTimeout(180*1000);
        return con;
    }
}
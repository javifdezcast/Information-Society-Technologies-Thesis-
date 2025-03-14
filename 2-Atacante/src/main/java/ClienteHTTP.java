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
    private String imagen;

    public ClienteHTTP(String imagen) {
        try {
            this.byteImage = Files.readAllBytes(Paths.get(imagen));
            this.image = ImageIO.read(new File(imagen));
            this.mapper = new ObjectMapper();
            this.imagen = imagen;
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public String[] solicitudRespuesta() throws IOException {
        String[] result = {"","","",""};
        try{
            this.setUpConection(Constantes.URL_FUNCION, "POST");
            Long inicio = enviarSolicitud();
            String[] resultado = procesarRespuesta();
            result[0] = resultado[0];
            result[1] = String.valueOf(inicio);
            result[2] = resultado[1];
            result[3] = resultado[2];
        }catch(Exception e){
            e.printStackTrace();
        }
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

    private void setUpConection(String url, String method) throws IOException {
        HttpURLConnection con = createConnection(url, method);
        con.setRequestProperty(Constantes.IMAGE_FORMAT, Files.probeContentType(Paths.get(imagen)));
        con.setRequestProperty(Constantes.IMAGE_SIZE, String.valueOf(byteImage.length));
        con.setRequestProperty(Constantes.IMAGE_WIDTH, String.valueOf(image.getWidth()));
        con.setRequestProperty(Constantes.IMAGE_HEIGHT, String.valueOf(image.getHeight()));
        con.setRequestProperty(Constantes.CONTENT_TYPE, "application/json");
        con.setConnectTimeout(180*1000);
        con.setReadTimeout(180*1000);
        this.conexion = con;
    }

    private static HttpURLConnection createConnection(String url, String method) throws IOException {
        URL urlObject = new URL(url);
        HttpURLConnection con = (HttpURLConnection) urlObject.openConnection();
        con.setRequestMethod(method);
        con.setDoOutput(true);
        return con;
    }

    @Override
    public void unblockFirewall() throws IOException {
        HttpURLConnection con = createConnection(Constantes.URL_FIREWALL, "POST");

        try (OutputStream stream = con.getOutputStream()) {
            stream.write(new byte[0]);
            stream.flush();
        }

        int responseCode = con.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("Failed to unblock firewall: " + responseCode);
        }
    }
}
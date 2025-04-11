import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ClienteImagen extends ClienteHTTP {

    private BufferedImage image;
    private byte[] byteImage;
    private String imagen;

    public ClienteImagen(String imagen) throws IOException {
        super(Constantes.URL_FUNCION, Constantes.POST);
        try {
            this.byteImage = Files.readAllBytes(Paths.get(imagen));
            this.image = ImageIO.read(new File(imagen));
            this.imagen = imagen;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String[] solicitudRespuesta() {
        ObjectNode node = super.getMapper().createObjectNode();
        node.put("imagen", this.imagen);

        String[] result = {"","","",""};
        try{
            Long inicio = enviarSolicitud(node);
            String[] resultado = procesarRespuesta();
            result[0] = resultado[0];
            result[1] = String.valueOf(inicio);
            result[2] = resultado[1];
            result[3] = resultado[2];
        }catch(IOException e){

        }
        return result;
    }

    private String[] procesarRespuesta() throws IOException {
        int responseCode = 0;
        Long fin = System.currentTimeMillis();
        String processingTime = "";
        try {
            responseCode = super.getConexion().getResponseCode();
            fin = System.currentTimeMillis();
            if(responseCode==200){
                InputStream input = super.getConexion().getInputStream();
                String responseBody = new String(input.readAllBytes(), StandardCharsets.UTF_8);
                input.close();
                JsonNode jsonResponse = super.getMapper().readTree(responseBody);
                processingTime = jsonResponse.get(Constantes.TIME).asText();
            }
        }catch (Exception ignored){

        }

        return new String[]{String.valueOf(responseCode), String.valueOf(fin), processingTime};
    }

    private void setUpConection(String url, String method) throws IOException {
        HttpURLConnection con = super.getConexion();
        con.setRequestProperty(Constantes.IMAGE_FORMAT, Files.probeContentType(Paths.get(imagen)));
        con.setRequestProperty(Constantes.IMAGE_SIZE, String.valueOf(byteImage.length));
        con.setRequestProperty(Constantes.IMAGE_WIDTH, String.valueOf(image.getWidth()));
        con.setRequestProperty(Constantes.IMAGE_HEIGHT, String.valueOf(image.getHeight()));
        con.setConnectTimeout(10*1000);
        con.setReadTimeout(10*1000);
    }
}

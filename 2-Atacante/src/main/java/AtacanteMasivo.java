import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.imageio.ImageIO;


public class AtacanteMasivo implements Atacante{

    private int id;

    public AtacanteMasivo(){
        Random r = new Random();
        this.id = r.nextInt();
    }

    @Override
    public List<Double[]> ataqueTemporal(long inicio, long fin, String tamanio, String formato, String iteracion) {
        String imagen = Constantes.IMAGE_PREFIX + tamanio + formato;
        List<Double[]> resultados = new ArrayList<>();
        while (System.currentTimeMillis() < inicio){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        while (System.currentTimeMillis() < fin) {
            try {
                resultados.add(enviarSolicitud(imagen));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        int aceptadas = 0;
        for(Double[] resultado : resultados){
            if(resultado[0]==200.0){
                aceptadas++;
            }
        }
        System.out.println(iteracion + ", " + tamanio + "," + formato + ", " + this.id + ", " + resultados.size() +
                ", " + aceptadas );
        return resultados;
    }


    @Override
    public List<Double[]> ataqueCuantitativo(int solicitudes, String imagen){
        int i = 0;
        List<Double[]> resultados = new ArrayList<>();
        System.out.println("Iniciando ataque cuantitativo. Thread: " + this.id + ", numero de solicitudes: " + solicitudes + " minutos.");
        while (i < solicitudes) {
            try {
                i++;
                resultados.add(enviarSolicitud(imagen));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        System.out.println("Fin ataque cuantitativo. Thread: " + this.id);
        return resultados;
    }

    private static Double[] enviarSolicitud(String imagen) throws IOException {
        byte[] byteImage = Files.readAllBytes(Paths.get(imagen));
        BufferedImage image = ImageIO.read(new File(imagen));
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("image", byteImage);

        HttpURLConnection con = setUpConettion(imagen, byteImage, image);
        OutputStream stream = con.getOutputStream();

        Long inicio = System.currentTimeMillis();

        stream.write(node.toString().getBytes(StandardCharsets.UTF_8));
        stream.flush();
        int responseCode = 0;
        try {
           responseCode = con.getResponseCode();
        }catch (Exception ignored){

        }

        Long fin = System.currentTimeMillis();
        String responseBody = "";
        String processingTime = "0";
        String victimSendingTime = "0";
        String victimReceptionTime = "0";
        if(responseCode==200){
            InputStream input = con.getInputStream();
            responseBody = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            input.close();

            // Parse the JSON response
            JsonNode jsonResponse = mapper.readTree(responseBody);
            processingTime = jsonResponse.get(Constantes.TIME).asText();
        }

        Double[] result = {(double) responseCode, Double.valueOf(inicio),
                Double.valueOf(victimReceptionTime), Double.valueOf(victimSendingTime),
                Double.valueOf(fin),  Double.valueOf(processingTime)};
        return result;
    }

    private static HttpURLConnection setUpConettion(String imagen, byte[] byteImage, BufferedImage image) throws IOException {
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

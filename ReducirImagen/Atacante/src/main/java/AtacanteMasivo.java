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
    public List<Double[]> ataqueTemporal(int time, String imagen) {
        long futureTimeMillis = System.currentTimeMillis() + (time * 1000);
        List<Double[]> resultados = new ArrayList<>();
        System.out.println("Iniciando ataque masivo. Thread: " + this.id + ", duración: " + time + " minutos.");
        while (System.currentTimeMillis() < futureTimeMillis) {
            try {
                resultados.add(enviarSolicitud(imagen));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        System.out.println("Fin ataque masivo. Thread: " + this.id + ", solicitudes enviadas: " + time + ".");
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
        System.out.println("Fin ataque masivo. Thread: " + this.id);
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
        String processingTime = "";
        String victimSendingTime = "";
        String victimReceptionTime = "";
        if(responseCode!=500){
            InputStream input = con.getInputStream();
            responseBody = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            input.close();

            // Parse the JSON response
            JsonNode jsonResponse = mapper.readTree(responseBody);
            processingTime = jsonResponse.get(PROCESSING_TIME).asText();
            victimSendingTime = jsonResponse.get(VICTIM_SENDING_TIME).asText();
            victimReceptionTime = jsonResponse.get(VICTIM_RECEPTION_TIME).asText();
        }

        Double[] result = {(double) responseCode, Double.valueOf(inicio),
                Double.valueOf(victimReceptionTime), Double.valueOf(victimSendingTime),
                Double.valueOf(fin),  Double.valueOf(processingTime)};
        return result;
    }

    private static HttpURLConnection setUpConettion(String imagen, byte[] byteImage, BufferedImage image) throws IOException {
        java.net.URL url = new URL(Atacante.URL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty(Atacante.IMAGE_FORMAT, Files.probeContentType(Paths.get(imagen)));
        con.setRequestProperty(Atacante.IMAGE_SIZE, String.valueOf(byteImage.length));
        con.setRequestProperty(Atacante.IMAGE_WIDTH, String.valueOf(image.getWidth()));
        con.setRequestProperty(Atacante.IMAGE_HEIGHT, String.valueOf(image.getHeight()));
        con.setRequestProperty(Atacante.CONTENT_TYPE, "application/json");
        return con;
    }
}

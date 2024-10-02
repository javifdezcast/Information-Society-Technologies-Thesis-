import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.imageio.ImageIO;

public class AtacanteMasivo implements Atacante{

    public AtacanteMasivo(){
        
    }

    @Override
    public void ataqueTemporal(int id, int time, String resultFileName, String imagen) {

        long futureTimeMillis = System.currentTimeMillis() + (time * 60 * 1000);
        Date end = new Date(futureTimeMillis);

        Integer hitcount = 0;
        File resultado = new File(resultFileName + "-" + id + ".csv");

        try (FileWriter writer = new FileWriter(resultado)) {
            writer.append("ResponseCode,SendingTime,ReceptionTime,ProcessingTime\n");
            while (new java.util.Date().before(end)) {
                Double[] res = enviarSolicitud(imagen);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println(hitcount + " requests sent");
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

    @Override
    public List<Double[]> ataqueCuantitativo(int id, int solicitudes, String imagen){
        int i = 0;
        List<Double[]> results = new ArrayList<>();
        try {
            while(i<solicitudes){
                Double[] resultados = enviarSolicitud(imagen);
                System.out.println("\t");
                for(int j = 0; j < resultados.length ; j++){
                    System.out.print(resultados[j] + ", ");
                }
                results.add(resultados);
                i++;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return results;
    }
}

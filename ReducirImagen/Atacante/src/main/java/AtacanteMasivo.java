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
import java.util.Calendar;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.imageio.ImageIO;

public class AtacanteMasivo implements Atacante{

    public AtacanteMasivo(){
        
    }

    @Override
    public void atacar(int id, int time, String resultFileName, String imagen) {

        long futureTimeMillis = System.currentTimeMillis() + (time * 60 * 1000);
        Date end = new Date(futureTimeMillis);

        Integer hitcount = 0;
        File resultado = new File(resultFileName + "-" + id + ".csv");
        try (FileWriter writer = new FileWriter(resultado)) {
            writer.append("ResponseCode,SendingTime,ReceptionTime,ProcessingTime\n");
            while(new java.util.Date().before(end)) {
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm:ss.SSSS");
                String envio = now.format(formatter);

                byte[] byteImage = Files.readAllBytes(Paths.get(imagen));
                BufferedImage image = ImageIO.read(new File(imagen));
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode node = mapper.createObjectNode();
                node.put("image", byteImage);

                java.net.URL url = new URL(Atacante.URL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoOutput(true);

                con.setRequestProperty(Atacante.IMAGE_FORMAT, Files.probeContentType(Paths.get(imagen)));
                con.setRequestProperty(Atacante.IMAGE_SIZE, String.valueOf(byteImage.length));
                con.setRequestProperty(Atacante.IMAGE_WIDTH, String.valueOf(image.getWidth()));
                con.setRequestProperty(Atacante.IMAGE_HEIGHT, String.valueOf(image.getHeight()));
                con.setRequestProperty(Atacante.CONTENT_TYPE, "application/json");

                OutputStream stream = con.getOutputStream();
                stream.write(node.toString().getBytes(StandardCharsets.UTF_8));
                stream.flush();
                hitcount++;
                int responseCode = con.getResponseCode();
                InputStream input = con.getInputStream();
                String responseBody = new String(input.readAllBytes(), StandardCharsets.UTF_8);
                input.close();

                // Parse the JSON response
                JsonNode jsonResponse = mapper.readTree(responseBody);
                String processingTime = jsonResponse.get(PROCESSING_TIME).asText();
                String responseTime = jsonResponse.get(TIME).asText();
                writer.append(String.valueOf(responseCode)).append(",").append(envio).append(",")
                        .append(responseTime).append(",").append(processingTime).append("\n");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println(hitcount + " requests sent");
    }
}

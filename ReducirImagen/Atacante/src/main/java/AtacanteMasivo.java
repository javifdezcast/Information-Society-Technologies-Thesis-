import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.Calendar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.imageio.ImageIO;

public class AtacanteMasivo implements Atacante{

    public AtacanteMasivo(){
        
    }

    @Override
    public void atacar(int time) {
        long futureTimeMillis = System.currentTimeMillis() + (120 * 1000);
        Date end = new Date(futureTimeMillis);

        Integer hitcount = 0;

        try {
            while(new java.util.Date().before(end)) {
                byte[] byteImage = Files.readAllBytes(Paths.get(FILEPATH));
                BufferedImage image = ImageIO.read(new File(FILEPATH));
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode node = mapper.createObjectNode();
                node.put("image", byteImage);

                java.net.URL url = new URL(URL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoOutput(true);

                con.setRequestProperty(IMAGE_FORMAT, Files.probeContentType(Paths.get(FILEPATH)));
                con.setRequestProperty(IMAGE_SIZE, String.valueOf(byteImage.length));
                con.setRequestProperty(IMAGE_WIDTH, String.valueOf(image.getWidth()));
                con.setRequestProperty(IMAGE_HEIGHT, String.valueOf(image.getHeight()));
                con.setRequestProperty(CONTENT_TYPE, "application/json");

                OutputStream stream = con.getOutputStream();
                stream.write(node.toString().getBytes(StandardCharsets.UTF_8));
                stream.flush();
                hitcount++;
                int responseCode = con.getResponseCode();
                InputStream input = con.getInputStream();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println(hitcount + " requests sent");
    }
}

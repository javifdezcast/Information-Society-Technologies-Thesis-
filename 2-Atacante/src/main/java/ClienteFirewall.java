import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ClienteFirewall extends ClienteHTTP{
    public long unblockTime;

    public ClienteFirewall() throws IOException {
        super(Constantes.URL_FIREWALL);
    }

    @Override
    public String[] solicitudRespuesta() {
        ObjectNode node = super.getMapper().createObjectNode();
        node.put("time", unblockTime);

        String[] result = {"","",""};
        try{
            Long inicio = System.currentTimeMillis();
            enviarSolicitud(node);
            String[] respuesta = procesarRespuesta();
            result[0] = respuesta[0];
            result[1] = String.valueOf(inicio);
            result[2] = respuesta[1];
        }catch(IOException e){
            e.printStackTrace();
        }
        return result;
    }

    private String[] procesarRespuesta() throws IOException {
        int responseCode = 0;
        Long fin = System.currentTimeMillis();
        try {
            responseCode = super.getConexion().getResponseCode();
            fin = System.currentTimeMillis();
        }catch (Exception ignored){

        }
        return new String[]{String.valueOf(responseCode), String.valueOf(fin),};
    }

    public void setUnblockTime(long unblockTime) {
        this.unblockTime = unblockTime;
    }
}

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public abstract class ClienteHTTP implements Cliente {
    private HttpURLConnection conexion;
    private ObjectMapper mapper;
    private String url;

    public ClienteHTTP(String URL) throws IOException {
        this.mapper = new ObjectMapper();
        this.url = URL;
    }


    public abstract String[] solicitudRespuesta();

    protected Long enviarSolicitud(ObjectNode node) throws IOException {
        this.conexion = createConnection(this.url, Constantes.POST);
        OutputStream stream = conexion.getOutputStream();
        Long inicio = System.currentTimeMillis();
        stream.write(node.toString().getBytes(StandardCharsets.UTF_8));
        stream.flush();
        stream.close();
        return inicio;
    }

    public HttpURLConnection createConnection(String url, String method) throws IOException {
        URL urlObject = new URL(url);
        HttpURLConnection con = (HttpURLConnection) urlObject.openConnection();
        con.setRequestMethod(method);
        con.setDoOutput(true);
        con.setRequestProperty(Constantes.CONTENT_TYPE, "application/json");
        return con;
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public HttpURLConnection getConexion() {
        return conexion;
    }

    public void setConexion(HttpURLConnection conexion) {
        this.conexion = conexion;
    }
}
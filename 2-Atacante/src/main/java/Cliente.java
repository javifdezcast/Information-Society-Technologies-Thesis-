import java.io.IOException;

public interface Cliente {
    String[] solicitudRespuesta() throws IOException;

    void unblockFirewall() throws IOException;
}
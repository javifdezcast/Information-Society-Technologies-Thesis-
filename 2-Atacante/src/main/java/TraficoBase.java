public class TraficoBase {
    Cliente cliente;

    public TraficoBase(Cliente cliente) {
        Cliente cliente1 = null;
        try {
            cliente1 = new ClienteImagen(Constantes.IMAGE_PREFIX + String.valueOf(2)
                    + Constantes.IMAGE_SUFFIX[0]);
        }catch (Exception ignored){

        }
        this.cliente = cliente1;
    }

    public void generaTrafico(long tiempo) {
        while(System.currentTimeMillis() < tiempo){
            try {
                cliente.solicitudRespuesta();
                Thread.sleep(10 * 1000);
            }catch (Exception ignored){
                
            }
        }
    }
}

import java.io.*;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private static final String COMMA_DELIMITER = ";";
    private static final String FICHERO_DATOS = "fichero1";
    private static final String FICHERO_TIEMPOS = "fichero2";
    private static final String FICHERO_SALIDA = "fichero3";



    public static void main(String[] args) {
        // Parsear datos
        List<List<String>> datos = parseFile();
        // Reordenar Columnas
        List<List<String>> ordenados = ordenaDatos(datos);
        // Agrupar por valores
        Map<Integer, Map<String, Map<Integer, Map<Integer, Map<Integer, Double>>>>> agrupados = agrupaDatos(ordenados);
        // Escribir datos
        exportarDatos(agrupados);
    }

    private static void exportarDatos(Map<Integer, Map<String, Map<Integer, Map<Integer, Map<Integer, Double>>>>> agrupados) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FICHERO_SALIDA))) {
            // Writing CSV Header
            writer.write("Tamaño,Formato,Tiempo,Iteración,Replica,Valor\n");

            for (int tamanio : agrupados.keySet()) {
                for (String formato : agrupados.get(tamanio).keySet()) {
                    for (int tiempo : agrupados.get(tamanio).get(formato).keySet()) {
                        for (int iteracion : agrupados.get(tamanio).get(formato).get(tiempo).keySet()) {
                            for (int replica : agrupados.get(tamanio).get(formato).get(tiempo).get(iteracion).keySet()) {
                                double valor = agrupados.get(tamanio).get(formato).get(tiempo).get(iteracion).get(replica);
                                writer.write(tamanio + "," + formato + "," + tiempo + "," + iteracion + "," + replica + "," + valor + "\n");
                            }
                        }
                    }
                }
            }

            System.out.println("Datos exportados exitosamente a " + FICHERO_SALIDA);
        } catch (IOException e) {
            System.err.println("Error al escribir el archivo CSV: " + e.getMessage());
        }
    }

    private static Map agrupaDatos(List<List<String>> ordenados){
        EstructuraDatosRecopilados datos = new EstructuraDatosRecopilados(ordenados);
        List<List<Object>> triosTFS = obtenerTrios();
        int[] iteraciones = {0,0,0,0,0,0};
        Map<Integer, Map<String, Map<Integer, Map<Integer, Map<Integer, Double>>>>> agregados = new HashMap<>();
        for(int i = 0 ; i < triosTFS.size() ; i++) {
            List<Object> trio = triosTFS.get(i);
            Integer size = Integer.parseInt(String.valueOf(trio.get(1)));
            String format = String.valueOf(trio.get(2));
            if(!agregados.containsKey(size)){
                Map<String, Map<Integer, Map<Integer, Map<Integer, Double>>>> formatMap = new HashMap<>();
                agregados.put(size, formatMap);
            }else{
                iteraciones[size]++;
            }
            if(!agregados.get(size).containsKey(format)){
                Map<Integer, Map<Integer, Map<Integer, Double>>> timeMap = new HashMap<>();
                agregados.get(size).put(format, timeMap);
            }
            for(int j = 0 ; j< 16 ; j++){
                if(!agregados.get(size).get(format).containsKey(j)){
                    Map<Integer, Map<Integer, Double>> iterationMap = new HashMap<>();
                    agregados.get(size).get(format).put(j, iterationMap);
                }
                if(!agregados.get(size).get(format).get(j).containsKey(iteraciones[size])){
                    Map<Integer, Double> replicaMap = new HashMap<>();
                    agregados.get(size).get(format).get(j).put(iteraciones[size], replicaMap);
                }
                for(int k = 0 ; k < 5 ;k++){
                    String valorString = ordenados.get(i*16+j).get(k);
                    if(!valorString.isBlank()){
                        Double valor = Double.parseDouble(valorString);
                        if(!agregados.get(size).get(format).get(iteraciones[0]).containsKey(j)){
                            Map<Integer, Double> timeMap = new HashMap<>();
                            agregados.get(size).get(format).get(iteraciones[0]).put(j, timeMap);
                        }
                        agregados.get(size).get(format).get(iteraciones[0]).get(j).put(k, valor);
                    }

                }
            }
        }

        return agregados;
    }

    private static List<List<Object>> obtenerTrios() {
        List<List<Object>> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FICHERO_TIEMPOS))) {
            String line;
            while ((line = br.readLine()) != null) {
                extraeDatosDeLinea(line, records);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return records;
    }

    private static void extraeDatosDeLinea(String line, List<List<Object>> records) {
        String timeStr = "";
        String size = "";
        String format = "";

        Pattern sizePattern = Pattern.compile("[0-5] ");
        Pattern formatPattern = Pattern.compile("\\....");
        Pattern timePattern = Pattern.compile("[0-9][0-9]:[0-9][0-9]:[0-9][0-9]");

        Matcher sizeMatcher = sizePattern.matcher(line);
        Matcher formatMatcher = formatPattern.matcher(line);
        Matcher timeMatcher = timePattern.matcher(line);

        if (sizeMatcher.find()) {
            size = sizeMatcher.group();
        }
        if (formatMatcher.find()) {
            format = formatMatcher.group();
        }
        if (timeMatcher.find()) {
            timeStr = timeMatcher.group();
        }

        LocalTime time = LocalTime.parse(timeStr);
        List<Object> record = new ArrayList<>();
        record.add(time);
        record.add(Integer.parseInt(size));
        record.add(format);
        records.add(record);
    }

    private static List<List<String>> ordenaDatos(List<List<String>> datos) {
        List<List<String>> ordenados = new ArrayList<>();
        int columna = 0;
        for(int i = 0; i < datos.size(); i++) {
            ordenados.add(new ArrayList<>());
            ordenados.get(i).add(datos.get(i).get(0));
            columna++;
        }
        for (int i = 1; i < datos.size(); i++) {
            for (int j = 1; j < datos.get(0).size(); j++) {
                if(!datos.get(i).get(j).isEmpty()) {
                    for(int k = 0; k < datos.size(); k++) {
                        ordenados.get(k).add(datos.get(k).get(j));
                        datos.get(k).remove(j);
                    }
                }
            }
        }
        return ordenados;
    }

    private static List<List<String>> parseFile() {
        List<List<String>> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FICHERO_DATOS))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(COMMA_DELIMITER);
                records.add(Arrays.asList(values));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return records;
    }

}
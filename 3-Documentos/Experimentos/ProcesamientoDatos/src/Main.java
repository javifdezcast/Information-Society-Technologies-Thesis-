import java.io.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private static final String COMMA_DELIMITER = ",";

    private static final String FICHERO_DATOS = "Consumo Memoria-data-as-joinbyfield-2025-01-31 18_18_29.csv";
    private static final String FICHERO_TIEMPOS = "Resultados2025-01-29-20-04.csv";
    private static final String FICHERO_SALIDA = "Consumo Memoria procesado.csv";
    private static final String CARPETA = "C:\\Users\\jfdez\\Information-Society-Technologies-Thesis-\\" +
            "3-Documentos\\Experimentos\\Experimento2.2\\";



    public static void main(String[] args) {
        // Parsear datos
        List<List<String>> datos = parseFile();

        List<List<String>> igualados = igualaDatos(datos);
        // Reordenar Columnas
        List<List<String>> ordenados = ordenaDatos(igualados);

        List<List<String>> completados = completaDatos(ordenados);
        imprimirDatos(completados, "Consumo Memoria ordenado.csv");
    }

    private static List<List<String>> completaDatos(List<List<String>> ordenados) {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LinkedList<List<String>> completados = new LinkedList<>();
        TemporalUnit segundos = ChronoUnit.SECONDS;
        for(int i = 1; i < ordenados.size() - 1 ; i++) {
            completados.addLast(ordenados.get(i));
            LocalDateTime actual = LocalDateTime.parse(ordenados.get(i).get(0), formato);
            LocalDateTime siguiente = LocalDateTime.parse(ordenados.get(i+1).get(0), formato);
            if(!siguiente.isEqual(actual.plus(30, segundos))){
                List<String> nuevo = new ArrayList<>();
                nuevo.add(formato.format(actual.plus(30, segundos)));
                for(int j = 1; j < ordenados.get(i).size(); j++) {
                    nuevo.add("");
                }
                completados.addLast(nuevo);
            }

        }
        completados.addLast(ordenados.get(ordenados.size()-1));
        return completados;
    }

    private static List<List<String>> igualaDatos(List<List<String>> datos) {
        int longitudMaxima = 0;
        for (List<String> dato : datos) {
            if (dato.size() > longitudMaxima) {
                longitudMaxima = dato.size();
            }
        }
        for(List<String> dato : datos) {
            if (dato.size() < longitudMaxima) {
                int longitud = dato.size();
                for(int i = longitud; i < longitudMaxima; i++) {
                    dato.add("");
                }
            }
        }
        return datos;
    }

    private static void exportarDatos(Map<Integer, Map<String, Map<Integer, Map<Integer, Map<Integer, String>>>>> agrupados) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CARPETA + FICHERO_SALIDA))) {
            // Writing CSV Header
            for (int tamanio : agrupados.keySet()) {
                for (String formato : agrupados.get(tamanio).keySet()) {
                    for (int tiempo : agrupados.get(tamanio).get(formato).keySet()) {
                        writer.write(tamanio + "," + formato + "," + tiempo + ",");
                        for (int iteracion : agrupados.get(tamanio).get(formato).get(tiempo).keySet()) {
                            for (int replica : agrupados.get(tamanio).get(formato).get(tiempo).get(iteracion).keySet()) {
                                String redondeado = agrupados.get(tamanio).get(formato).get(tiempo).get(iteracion).get(replica);
                                writer.write("I" + iteracion + "R" + replica + "," + redondeado+ ",");
                            }
                        }
                        writer.write("\n");
                    }
                }
            }

            System.out.println("Datos exportados exitosamente a " + FICHERO_SALIDA);
        } catch (IOException e) {
            System.err.println("Error al escribir el archivo CSV: " + e.getMessage());
        }
    }


    private static List<List<Object>> obtenerTrios() {
        List<List<Object>> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(CARPETA + FICHERO_TIEMPOS))) {
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
        record.add(Integer.parseInt(size.stripTrailing()));
        record.add(format);
        records.add(record);
    }

    private static List<List<String>> ordenaDatos(List<List<String>> datos) {
        List<List<String>> ordenados = new ArrayList<>();
        int columna = 0;
        for(int i = 0; i < datos.size(); i++) {
            ordenados.add(new LinkedList<>());
            ordenados.get(i).add(datos.get(i).get(0));
            columna++;
        }
        for (int i = 1; i < datos.size(); i++) {
            for (int j = 1; j < datos.get(0).size(); j++) {
                if(!datos.get(i).get(j).isEmpty()) {
                    for(int k = 0; k < datos.size(); k++) {
                        String dato;
                        try{
                            dato = datos.get(k).get(j);
                        } catch (Exception e) {
                            dato = "";
                        }
                        ordenados.get(k).add(dato);
                        if(datos.get(k).size() >= j){
                            datos.get(k).remove(j);
                        }
                    }
                    columna++;
                }
            }
        }
        return ordenados;
    }

    private static List<List<String>> parseFile() {
        List<List<String>> records = new LinkedList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(CARPETA + FICHERO_DATOS))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(COMMA_DELIMITER);
                LinkedList<String> datos = new LinkedList<>(Arrays.asList(values));
                records.add(datos);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return records;
    }

    public static void imprimirDatos(List<List<String>> datos, String fichero){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CARPETA + fichero))) {
            for (int i = 0; i < datos.get(0).size(); i++) {
                writer.write(datos.get(0).get(i) + ",");
            }
            writer.write("\n");
            for (int i = 1; i < datos.size(); i++) {
                writer.write(datos.get(i).get(0) + ",");
                for (int j = 1; j < datos.get(i).size(); j++) {
                    if(!datos.get(i).get(j).isBlank()){
                        writer.write(datos.get(i).get(j) + ",");
                    }else {
                        writer.write(datos.get(i).get(j) + ",");
                    }
                }
                writer.write("\n");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
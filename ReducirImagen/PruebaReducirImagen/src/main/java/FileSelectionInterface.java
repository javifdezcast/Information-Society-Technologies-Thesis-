import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.openfaas.model.IRequest;
import com.openfaas.model.IResponse;
import com.openfaas.model.Request;
import com.openfaas.model.Response;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLOutput;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileSelectionInterface {
    private final static String URL="http://127.0.0.1:8080/function/reducirimagen";

    private JFrame frame;
    private JTextField selectedFileField;
    private BufferedImage image;
    private String fileFormat;
    private String filePath;


    public static String IMAGE_FORMAT = "IMAGE_FORMAT";
    public static String IMAGE_SIZE = "IMAGE_SIZE";
    public static String IMAGE_HEIGHT = "IMAGE_HEIGHT";
    public static String IMAGE_WIDTH = "IMAGE_WIDTH";
    public static String CONTENT_TYPE = "CONTENT_TYPE";
    public static String DATE = "DATE";


    public FileSelectionInterface() {
        crearVentanaInicial();
    }

    private void crearVentanaInicial() {
        frame = new JFrame("File Selection Interface");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 200);

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        selectedFileField = new JTextField(20);
        selectedFileField.setEditable(false);
        panel.add(selectedFileField);

        JButton selectFileButton = new JButton("Select File");
        selectFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    selectedFileField.setText(selectedFile.getAbsolutePath());
                }
            }
        });
        panel.add(selectFileButton);

        JButton confirmButton = new JButton("Confirm");
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedFilePath = selectedFileField.getText();
                if (selectedFilePath.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please select a file first.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    File selectedFile = new File(selectedFilePath);
                    if (selectedFile.isFile() && selectedFile.exists()) {
                        try {
                            BufferedImage image = ImageIO.read(selectedFile);
                            if (image != null) {
                                filePath=selectedFilePath;
                                String[] nombre = selectedFilePath.split("\\.");
                                fileFormat = nombre[nombre.length-1];
                                displayImage(image);
                            } else {
                                JOptionPane.showMessageDialog(frame, "The selected file is not an image.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(frame, "An error occurred while reading the file.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(frame, "Invalid file selected.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        panel.add(confirmButton);

        frame.add(panel);
        frame.setVisible(true);
    }

    private void displayImage(BufferedImage image) {
        frame = new JFrame("Image Display");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1080, 720);

        JLabel label = new JLabel(new ImageIcon(image));
        frame.add(label);

        JButton processButton = new JButton("Process Image");
        processButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Perform processing on the image here
                processImage(image);
            }
        });
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(processButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void processImage(BufferedImage image){
        frame = new JFrame("Image Processor");
        BufferedImage imagenProcesada = extracted(image);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JPanel panel = new JPanel(new BorderLayout());

        // Panel for images
        JPanel imagesPanel = new JPanel(new GridLayout(1, 2));
        JScrollPane originalScrollPane = new JScrollPane();
        originalScrollPane.setViewportView(new JLabel(new ImageIcon(image)));
        imagesPanel.add(originalScrollPane);



        if (imagenProcesada != null) {
            JScrollPane processedScrollPane = new JScrollPane();
            processedScrollPane.setViewportView(new JLabel(new ImageIcon(imagenProcesada)));
            imagesPanel.add(processedScrollPane);
        }
        panel.add(imagesPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton startAgainButton = new JButton("Start Again");
        startAgainButton.addActionListener(e -> {
            crearVentanaInicial();
        });
        buttonPanel.add(startAgainButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        String processedImagePath = filePath.replace(".jpg", "Procesada.jpg");

        try {
            File outputImageFile = new File(processedImagePath);
            ImageIO.write(imagenProcesada, "jpg", outputImageFile);
            System.out.println("Processed image saved successfully to: " + processedImagePath);
        } catch (IOException e) {
            System.out.println("Error occurred while saving the processed image: " + e.getMessage());
            e.printStackTrace();
        }

        frame.add(panel);
        frame.setVisible(true);

    }

    private BufferedImage extracted(BufferedImage image) {
        try {

            byte[] byteImage = Files.readAllBytes(Paths.get(filePath));

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode node = mapper.createObjectNode();
            node.put("image", byteImage);

            java.net.URL url = new URL(URL);
            HttpURLConnection con =  (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);

            con.setRequestProperty(IMAGE_FORMAT, fileFormat);
            con.setRequestProperty(IMAGE_SIZE, String.valueOf(byteImage.length));
            con.setRequestProperty(IMAGE_WIDTH, String.valueOf(image.getWidth()));
            con.setRequestProperty(IMAGE_HEIGHT, String.valueOf(image.getHeight()));
            con.setRequestProperty(CONTENT_TYPE, "application/json");

            OutputStream stream = con.getOutputStream();
            stream.write(node.toString().getBytes(StandardCharsets.UTF_8));
            stream.flush();

            int responseCode = con.getResponseCode();
            InputStream input = con.getInputStream();

            Map<String, List<String>> headers = con.getHeaderFields();

            // Read body
            StringBuilder body = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    body.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            String[] cuerpo = body.toString().split(",");
            String imagen = cuerpo[0];
            cuerpo = imagen.split(":");
            imagen = cuerpo[1];
            imagen = imagen.substring(1, imagen.length()-2);




            // Decode Base64 encoded image data into byte array
            byte[] imageBytes = Base64.getDecoder().decode(imagen);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
            BufferedImage processedImage = ImageIO.read(bis);
            return processedImage;

        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }
}

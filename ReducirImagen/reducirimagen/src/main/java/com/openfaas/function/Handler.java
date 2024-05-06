package com.openfaas.function;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.openfaas.model.IResponse;
import com.openfaas.model.IRequest;
import com.openfaas.model.Response;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Handler extends com.openfaas.model.AbstractHandler {

    public Handler(){

    }

    public IResponse Handle(IRequest req) {
        long inicio = System.currentTimeMillis();
        IResponse res = new Response();

        ObjectMapper mapper = new ObjectMapper();
        try {
            //Des-serializacion
            JsonNode node = mapper.readTree(req.getBody());
            String imagenEnBase64 = node.get("image").asText();
            byte[] img = java.util.Base64.getDecoder().decode(imagenEnBase64);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(img);

            //Procesamiento
            BufferedImage imagenBruta = ImageIO.read(inputStream);
            Image procesada = imagenBruta.getScaledInstance(200, 300, BufferedImage.SCALE_SMOOTH);

            //Transformacion a byte[]
            BufferedImage buffProcesada = new BufferedImage(200, 300, BufferedImage.TYPE_INT_RGB);
            Graphics2D bGr = buffProcesada.createGraphics();
            bGr.drawImage(procesada, 0, 0, null);
            bGr.dispose();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(buffProcesada, "jpg", outputStream);
            byte[] byteProcesada = outputStream.toByteArray();

            //Serializacion y construcción de respuesta
            ObjectNode nodoRespuesta = mapper.createObjectNode();
            nodoRespuesta.put("image", byteProcesada);
            nodoRespuesta.put("time", System.currentTimeMillis()-inicio);
            res.setContentType("application/json");
            res.setStatusCode(200);
            res.setBody(nodoRespuesta.toString());

        } catch (JsonProcessingException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }
}

package com.example.demo.security;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/*
 * Энэ класс нь SOAP auth service рүү request явуулж
 * token хүчинтэй эсэхийг шалгана.
 */
@Component
public class SoapAuthClient {

    private final RestTemplate restTemplate = new RestTemplate();

    public boolean validateToken(String token) {
        try {
            String soapRequest =
                    "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" "
                    + "xmlns:auth=\"http://example.com/usersoapservice/auth\">"
                    + "<soapenv:Header/>"
                    + "<soapenv:Body>"
                    + "<auth:ValidateTokenRequest>"
                    + "<auth:token>" + token + "</auth:token>"
                    + "</auth:ValidateTokenRequest>"
                    + "</soapenv:Body>"
                    + "</soapenv:Envelope>";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_XML);
            headers.add("SOAPAction", "");

            HttpEntity<String> requestEntity = new HttpEntity<>(soapRequest, headers);

            String response = restTemplate.postForObject(
                    "http://localhost:8080/ws",
                    requestEntity,
                    String.class
            );

            if (response == null) {
                return false;
            }

            System.out.println("SOAP response from auth service:");
            System.out.println(response);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);

            Document document = factory.newDocumentBuilder()
                    .parse(new ByteArrayInputStream(response.getBytes(StandardCharsets.UTF_8)));

            NodeList validNodes = document.getElementsByTagNameNS("*", "valid");

            if (validNodes.getLength() == 0) {
                return false;
            }

            String value = validNodes.item(0).getTextContent();
            return "true".equalsIgnoreCase(value);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
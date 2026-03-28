package com.example.demo.security;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

@Component
public class SoapAuthClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${soap.service.url}")
    private String soapServiceUrl;

    public Integer getUserIdFromToken(String token) {
        try {
            System.out.println("Sending token to SOAP validate: " + token);
            System.out.println("SOAP URL: " + soapServiceUrl);

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
                    soapServiceUrl,
                    requestEntity,
                    String.class
            );

            if (response == null) {
                return null;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);

            Document document = factory.newDocumentBuilder()
                    .parse(new ByteArrayInputStream(response.getBytes(StandardCharsets.UTF_8)));

            NodeList validNodes = document.getElementsByTagNameNS("*", "valid");
            if (validNodes.getLength() == 0) {
                return null;
            }

            String validValue = validNodes.item(0).getTextContent();
            if (!"true".equalsIgnoreCase(validValue)) {
                return null;
            }

            NodeList userIdNodes = document.getElementsByTagNameNS("*", "userId");
            if (userIdNodes.getLength() == 0) {
                return null;
            }

            String userIdText = userIdNodes.item(0).getTextContent();
            if (userIdText == null || userIdText.isBlank()) {
                return null;
            }

            return Integer.parseInt(userIdText);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isTokenValid(String token) {
        try {
            System.out.println("SOAP URL: " + soapServiceUrl);

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
                    soapServiceUrl,
                    requestEntity,
                    String.class
            );

            if (response == null) {
                return false;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);

            Document document = factory.newDocumentBuilder()
                    .parse(new ByteArrayInputStream(response.getBytes(StandardCharsets.UTF_8)));

            NodeList validNodes = document.getElementsByTagNameNS("*", "valid");
            if (validNodes.getLength() == 0) {
                return false;
            }

            String validValue = validNodes.item(0).getTextContent();
            return "true".equalsIgnoreCase(validValue);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
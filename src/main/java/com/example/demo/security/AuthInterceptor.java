package com.example.demo.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final SoapAuthClient soapAuthClient;

    public AuthInterceptor(SoapAuthClient soapAuthClient) {
        this.soapAuthClient = soapAuthClient;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }

        String uri = request.getRequestURI();
        if (uri.startsWith("/auth")) {
            return true;
        }

        // Internal service calls (mail-service зэрэг) token-гүй GET /users хандах
        String internalHeader = request.getHeader("X-Internal-Service");
        if ("mail-service".equals(internalHeader)) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        System.out.println("Authorization header: " + authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing or invalid Authorization header");
            return false;
        }

        String token = authHeader.substring(7);
        System.out.println("Extracted token: " + token);

        // POST /users дээр profile үүсгэх үед userId хараахан байхгүй байж болно
        if ("/users".equals(request.getRequestURI()) && "POST".equalsIgnoreCase(request.getMethod())) {
            boolean valid = soapAuthClient.isTokenValid(token);

            if (!valid) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid token");
                return false;
            }

            return true;
        }

        Integer userId = soapAuthClient.getUserIdFromToken(token);

        if (userId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid token");
            return false;
        }

        request.setAttribute("userId", userId);
        return true;
    }
}
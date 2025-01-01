package com.example.security_app.exceptionhandling;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setStatus(HttpStatus.FORBIDDEN.value());
        String message = accessDeniedException.getMessage();
        String jsonFormat = String.format("""
                "error":"Forbidden",
                "message": "%s",
                "status":"403",
                "path":"%s"
                """, message,request.getRequestURI());
        response.getWriter().write(jsonFormat);
    }
}

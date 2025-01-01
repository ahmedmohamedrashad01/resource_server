package com.example.security_app.exceptionhandling;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.print.DocFlavor;
import java.io.IOException;

public class CustomBasicAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {


        response.setContentType("application/json");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        String errorMessage = authException.getMessage();
        String jsonFormat = String.format("""
                "error":"Unauthorized",
                "message":"%s",
                "status":"401"
                """,errorMessage);
        response.getWriter().write(jsonFormat);
    }

}

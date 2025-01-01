package com.example.security_app.event_listener;

import com.example.security_app.service.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.InetAddress;
// -------------------------- Ahmed Rashad ------------------------
@Component
@Slf4j
@RequiredArgsConstructor
public class SuccessLoginAndFailue {

    private final EmailService emailService;

    @EventListener
    public void onSuccess (AuthenticationSuccessEvent event){
        log.info(event.getAuthentication().getName());
        String email = event.getAuthentication().getName();
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            String hostname = inetAddress.getHostName();
            String ipAddress = inetAddress.getHostAddress();

            String message = "Login alert: Someone logged in to your account.\n" +
                    "Details:\n" +
                    "Hostname: " + hostname + "\n" +
                    "IP Address: " + ipAddress;

            emailService.sendEmail(email, "Login Alert", message);
        } catch (Exception e) {
            log.error("Unable to fetch client details", e);
        }


    }

    @EventListener
    public void onFailure (AbstractAuthenticationFailureEvent failureEvent){
        log.info("Failure event"+failureEvent.getAuthentication().getName());
    }


}

package com.example.security_app.controller;

import com.example.security_app.constants.ApplicationConstants;
import com.example.security_app.model.LoginRequestDTO;
import com.example.security_app.model.LoginResponseDTO;
import com.example.security_app.model.UserEntity;
import com.example.security_app.repository.UserRepo;
import com.example.security_app.service.EmailService;
import com.example.security_app.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
@Slf4j

public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private Environment env;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @PostMapping("/register")
    public ResponseEntity<String> addUser(@RequestBody UserEntity userEntity) {
       return this.userService.addUser(userEntity);
    }

    @GetMapping("/test")
    public String getName() {
        return "Ahmed Rashad";
    }

    @GetMapping("/testUser")
    public String getDataForUser() {
        return "Welcome from user";
    }

    @GetMapping("/allUsers")
    public List<UserEntity> getAllUsers() {
        return this.userService.getAllUsers();
    }

    @PostMapping("/apiLogin")
    public ResponseEntity<LoginResponseDTO> apiLogin(@RequestBody LoginRequestDTO loginRequest) {
        String jwt = "";
        Authentication authentication = UsernamePasswordAuthenticationToken.unauthenticated(
                loginRequest.username(), loginRequest.password()
        );
        Authentication authenticationResponse = authenticationManager.authenticate(authentication);
        if(authenticationResponse != null && authenticationResponse.isAuthenticated()){

            eventPublisher.publishEvent(new AuthenticationSuccessEvent(authenticationResponse));


            if(env != null){
                String secret = env.getProperty(ApplicationConstants.JWT_SECRET_KEY, ApplicationConstants.JWT_SECRET_DEFAULT_VALUE);
                SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
                jwt = Jwts.builder().issuer("Ahmed").subject("JWT Token")
                        .claim("username", authenticationResponse.getName())
                        .claim("authorities", authenticationResponse.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority).collect(Collectors.joining(",")))
                        .issuedAt(new Date())
                        .expiration(new Date((new Date()).getTime() + 800000))
                        .signWith(secretKey).compact();
            }
        }
        return ResponseEntity.status(HttpStatus.OK).header(ApplicationConstants.JWT_HEADER, jwt)
                .body(new LoginResponseDTO(HttpStatus.OK.getReasonPhrase(),jwt));
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activateAccount(@RequestParam String code) {
        // البحث عن المستخدم باستخدام كود التفعيل
        UserEntity user = userRepo.findByActivationCode(code).orElse(null);

        // التحقق إذا لم يتم العثور على المستخدم باستخدام كود التفعيل
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid activation code");
        }
        // تفعيل الحساب
        user.setEnabled(true);
        user.setActivationCode(null); // مسح كود التفعيل
        userRepo.save(user);

        // إرسال بريد إلكتروني بتأكيد التفعيل
        emailService.sendEmail(user.getEmail(), "Account Activated", "Your account has been successfully activated.");

        // الرد على المستخدم بتأكيد التفعيل
        return ResponseEntity.ok("Account activated successfully");
    }

//    Resend activation code
    @PostMapping("/resend-activation-code")
    public ResponseEntity<String> resendActivationCode(@RequestBody Map<String, String> request) {
        // استلام البريد الإلكتروني من جسم الطلب
        String email = request.get("email");

        // التحقق إذا كان البريد الإلكتروني غير موجود في الطلب
        if (email == null || email.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email is required");
        }

        // البحث عن المستخدم باستخدام البريد الإلكتروني
        UserEntity user = userRepo.findByEmail(email).orElse(null);

        // إذا لم يتم العثور على المستخدم
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        // إذا كان الحساب مفعل مسبقًا
        if (user.isEnabled()) {
            return ResponseEntity.badRequest().body("Account is already activated");
        }

        // إنشاء رمز تفعيل جديد
        String newActivationCode = UUID.randomUUID().toString();
        user.setActivationCode(newActivationCode);

        // حفظ المستخدم مع رمز التفعيل الجديد
        userRepo.save(user);

        // إرسال بريد إلكتروني يحتوي على رمز التفعيل الجديد
        emailService.sendEmail(user.getEmail(),
                "Resend Activation Code",
                "http://localhost:8083/user/activate?code=" + newActivationCode);

        // الرد على المستخدم
        return ResponseEntity.ok("A new activation code has been sent to your email.");
    }

//    ___________________________________________________



}

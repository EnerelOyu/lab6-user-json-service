package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.User;
import com.example.demo.security.SoapAuthClient;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {

    private final SoapAuthClient soapAuthClient;
    private final UserService userService;

    public AuthController(SoapAuthClient soapAuthClient, UserService userService) {
        this.soapAuthClient = soapAuthClient;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        boolean registerOk = soapAuthClient.registerUser(request.getUsername(), request.getPassword());
        if (!registerOk) {
            return ResponseEntity.badRequest().body("SOAP register failed");
        }

        String token = soapAuthClient.loginUser(request.getUsername(), request.getPassword());
        if (token == null) {
            return ResponseEntity.badRequest().body("SOAP login failed after register");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setBio(request.getBio());
        user.setPhone(request.getPhone());

        User savedUser = userService.createUser(user);

        boolean linkOk = soapAuthClient.linkUserProfile(request.getUsername(), savedUser.getId());
        if (!linkOk) {
            return ResponseEntity.badRequest().body("Profile link failed");
        }

        return ResponseEntity.ok(new AuthResponse(token, savedUser.getId(), savedUser));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        String token = soapAuthClient.loginUser(request.getUsername(), request.getPassword());
        if (token == null) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }

        Integer userId = soapAuthClient.getUserIdFromToken(token);
        return ResponseEntity.ok(new LoginResponse(token, userId));
    }

    public static class RegisterRequest {
        private String username;
        private String password;
        private String name;
        private String email;
        private String bio;
        private String phone;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getBio() { return bio; }
        public void setBio(String bio) { this.bio = bio; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
    }

    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public record AuthResponse(String token, Integer userId, User user) {}
    public record LoginResponse(String token, Integer userId) {}
}

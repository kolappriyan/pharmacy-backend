package com.pharmacy.pharmacy_backend.controller;

import com.pharmacy.pharmacy_backend.model.User;
import com.pharmacy.pharmacy_backend.repository.UserRepository;
import com.pharmacy.pharmacy_backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public Map<String, String> register(@RequestBody User user) {
        User existing = userRepository.findByEmail(user.getEmail());
        if (existing != null) {
            return Map.of("message", "Email already registered!");
        }
        user.setRole("customer");
        userRepository.save(user);
        return Map.of("message", "Registration successful!");
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String phone = credentials.get("phone");
        String password = credentials.get("password");

        User user = null;
        if (email != null && !email.isEmpty()) {
            user = userRepository.findByEmail(email);
        } else if (phone != null && !phone.isEmpty()) {
            user = userRepository.findByPhone(phone);
        }

        if (user == null || !user.getPassword().equals(password)) {
            return Map.of("message", "Invalid credentials!");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

        return Map.of(
            "message", "Login successful!",
            "token", token,
            "role", user.getRole(),
            "name", user.getFullName()
        );
    }
}
package org.example.chatgoup.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody ChatUsers user) {
        System.out.println("Received request to register user: " + user.getUsername());
        if (!userService.isUsernameAvailable(user.getUsername())) {
            System.out.println("Username already exists: " + user.getUsername());
            Map<String, String> response = new HashMap<>();
            response.put("message", "Username already exists");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        userService.registerUser(user);
        Map<String, String> response = new HashMap<>();
        response.put("message", "User registered successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody ChatUsers user) {
        System.out.println("Received login request for user: " + user.getUsername());
        try {
            if (userService.authenticateUser(user.getUsername(), user.getPassword())) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Login successful");
                return ResponseEntity.ok(response);
            } else {
                System.out.println("Invalid username or password for user: " + user.getUsername());
                Map<String, String> response = new HashMap<>();
                response.put("message", "Invalid username or password");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            System.out.println("Error logging in user: " + user.getUsername());
            e.printStackTrace();
            Map<String, String> response = new HashMap<>();
            response.put("message", "Error logging in user");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

package org.example.controller;

import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/users/setIsActive")
    public ResponseEntity<?> setUserActive(@RequestBody Map<String, Object> request) {
        try {
            String userId = (String) request.get("user_id");
            Boolean isActive = (Boolean) request.get("is_active");

            var user = userService.setUserActive(userId, isActive);
            return ResponseEntity.ok(Map.of("user", user));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(createErrorResponse("NOT_FOUND", e.getMessage()));
        }
    }

    @GetMapping("/users/getReview")
    public ResponseEntity<?> getUserReviews(@RequestParam("user_id") String userId) {
        try {
            var result = userService.getUserReviews(userId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(createErrorResponse("NOT_FOUND", e.getMessage()));
        }
    }

    private Map<String, Object> createErrorResponse(String code, String message) {
        return Map.of(
                "error", Map.of(
                        "code", code,
                        "message", message
                )
        );
    }
}

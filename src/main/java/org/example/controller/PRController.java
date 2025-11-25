package org.example.controller;

import org.example.service.PRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class PRController {

    @Autowired
    private PRService prService;

    @PostMapping("/pullRequest/create")
    public ResponseEntity<?> createPR(@RequestBody Map<String, Object> request) {
        try {
            String prId = (String) request.get("pull_request_id");
            String prName = (String) request.get("pull_request_name");
            String authorId = (String) request.get("author_id");

            var pr = prService.createPR(prId, prName, authorId);
            return ResponseEntity.status(201).body(Map.of("pr", pr));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("already exists")) {
                return ResponseEntity.status(409).body(createErrorResponse("PR_EXISTS", e.getMessage()));
            }
            return ResponseEntity.status(404).body(createErrorResponse("NOT_FOUND", e.getMessage()));
        }
    }

    @PostMapping("/pullRequest/merge")
    public ResponseEntity<?> mergePR(@RequestBody Map<String, Object> request) {
        try {
            String prId = (String) request.get("pull_request_id");
            var pr = prService.mergePR(prId);
            return ResponseEntity.ok(Map.of("pr", pr));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(createErrorResponse("NOT_FOUND", e.getMessage()));
        }
    }

    @PostMapping("/pullRequest/reassign")
    public ResponseEntity<?> reassignReviewer(@RequestBody Map<String, Object> request) {
        try {
            String prId = (String) request.get("pull_request_id");
            String oldUserId = (String) request.get("old_user_id");

            var result = prService.reassignReviewer(prId, oldUserId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            String errorCode = getErrorCode(e);
            return ResponseEntity.status(409).body(createErrorResponse(errorCode, e.getMessage()));
        }
    }

    private String getErrorCode(RuntimeException e) {
        String message = e.getMessage();
        if (message.contains("merged")) return "PR_MERGED";
        if (message.contains("not assigned")) return "NOT_ASSIGNED";
        if (message.contains("no active replacement")) return "NO_CANDIDATE";
        return "NOT_FOUND";
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
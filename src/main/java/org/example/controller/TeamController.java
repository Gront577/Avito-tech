package org.example.controller;

import org.example.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class TeamController {

    @Autowired
    private TeamService teamService;

    @PostMapping("/team/add")
    public ResponseEntity<?> createTeam(@RequestBody Map<String, Object> request) {
        try {
            String teamName = (String) request.get("team_name");
            var members = (java.util.List<Map<String, Object>>) request.get("members");

            var team = teamService.createTeam(teamName, members);
            return ResponseEntity.status(201).body(Map.of("team", team));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(createErrorResponse("TEAM_EXISTS", e.getMessage()));
        }
    }

    @GetMapping("/team/get")
    public ResponseEntity<?> getTeam(@RequestParam("team_name") String teamName) {
        try {
            var team = teamService.getTeam(teamName);
            return ResponseEntity.ok(team);
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
package org.example.service;

import org.example.model.Team;
import org.example.model.User;
import org.example.repository.TeamRepository;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;

    public Map<String, Object> createTeam(String teamName, List<Map<String, Object>> members) {
        if (teamRepository.existsById(teamName)) {
            throw new IllegalArgumentException("team_name already exists");
        }

        Team team = new Team();
        team.setTeamName(teamName);

        for (Map<String, Object> memberData : members) {
            String userId = (String) memberData.get("user_id");
            String username = (String) memberData.get("username");
            Boolean isActive = (Boolean) memberData.get("is_active");

            User user = userRepository.findById(userId).orElse(new User());
            user.setUserId(userId);
            user.setUsername(username);
            user.setActive(isActive != null ? isActive : true);
            user.setTeam(team);

            team.getMembers().add(user);
        }

        Team savedTeam = teamRepository.save(team);

        return convertToTeamResponse(savedTeam);
    }

    public Map<String, Object> getTeam(String teamName) {
        Team team = teamRepository.findById(teamName)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        return convertToTeamResponse(team);
    }

    private Map<String, Object> convertToTeamResponse(Team team) {
        List<Map<String, Object>> members = team.getMembers().stream()
                .map(user -> {
                    Map<String, Object> memberMap = new HashMap<>();
                    memberMap.put("user_id", user.getUserId());
                    memberMap.put("username", user.getUsername());
                    memberMap.put("is_active", user.isActive());
                    return memberMap;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("team_name", team.getTeamName());
        response.put("members", members);

        return response;
    }
}
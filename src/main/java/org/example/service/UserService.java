package org.example.service;

import org.example.model.PullRequest;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Map<String, Object> setUserActive(String userId, Boolean isActive) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setActive(isActive);
        User savedUser = userRepository.save(user);

        return convertToUserResponse(savedUser);
    }

    public Map<String, Object> getUserReviews(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        var pullRequests = user.getAssignedPullRequests().stream()
                .map(this::convertToPRShort)
                .collect(Collectors.toList());

        return Map.of(
                "user_id", userId,
                "pull_requests", pullRequests
        );
    }

    private Map<String, Object> convertToUserResponse(User user) {
        return Map.of(
                "user_id", user.getUserId(),
                "username", user.getUsername(),
                "team_name", user.getTeam().getTeamName(),
                "is_active", user.isActive()
        );
    }

    private Map<String, Object> convertToPRShort(PullRequest pr) {
        return Map.of(
                "pull_request_id", pr.getPullRequestId(),
                "pull_request_name", pr.getPullRequestName(),
                "author_id", pr.getAuthor().getUserId(),
                "status", pr.getStatus().name()
        );
    }
}
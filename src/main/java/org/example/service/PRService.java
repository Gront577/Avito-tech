package org.example.service;

import org.example.model.PRStatus;
import org.example.model.PullRequest;
import org.example.model.Team;
import org.example.model.User;
import org.example.repository.PullRequestRepository;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class PRService {

    @Autowired
    private PullRequestRepository pullRequestRepository;

    @Autowired
    private UserRepository userRepository;

    public Map<String, Object> createPR(String prId, String prName, String authorId) {
        if (pullRequestRepository.existsById(prId)) {
            throw new IllegalArgumentException("PR id already exists");
        }

        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Author not found"));

        if (!author.isActive()) {
            throw new RuntimeException("Author is not active");
        }

        PullRequest pr = new PullRequest();
        pr.setPullRequestId(prId);
        pr.setPullRequestName(prName);
        pr.setAuthor(author);
        pr.setStatus(PRStatus.OPEN);
        pr.setCreatedAt(LocalDateTime.now());

        Set<User> reviewers = assignReviewers(author);
        pr.setAssignedReviewers(reviewers);

        PullRequest savedPR = pullRequestRepository.save(pr);
        return convertToPRResponse(savedPR);
    }

    public Map<String, Object> mergePR(String prId) {
        PullRequest pr = pullRequestRepository.findById(prId)
                .orElseThrow(() -> new RuntimeException("PR not found"));

        if (pr.getStatus() != PRStatus.MERGED) {
            pr.setStatus(PRStatus.MERGED);
            pr.setMergedAt(LocalDateTime.now());
            pr = pullRequestRepository.save(pr);
        }

        return convertToPRResponse(pr);
    }

    public Map<String, Object> reassignReviewer(String prId, String oldUserId) {
        PullRequest pr = pullRequestRepository.findById(prId)
                .orElseThrow(() -> new RuntimeException("PR not found"));

        if (pr.getStatus() == PRStatus.MERGED) {
            throw new RuntimeException("cannot reassign on merged PR");
        }

        User oldReviewer = pr.getAssignedReviewers().stream()
                .filter(r -> r.getUserId().equals(oldUserId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("reviewer is not assigned to this PR"));

        Team team = oldReviewer.getTeam();
        List<User> availableReviewers = userRepository
                .findByTeamAndIsActiveTrueAndUserIdNot(team, pr.getAuthor().getUserId());

        availableReviewers.removeAll(pr.getAssignedReviewers());
        availableReviewers.remove(oldReviewer);

        if (availableReviewers.isEmpty()) {
            throw new RuntimeException("no active replacement candidate in team");
        }

        Collections.shuffle(availableReviewers);
        User newReviewer = availableReviewers.get(0);

        pr.getAssignedReviewers().remove(oldReviewer);
        pr.getAssignedReviewers().add(newReviewer);

        PullRequest updatedPR = pullRequestRepository.save(pr);

        Map<String, Object> response = new HashMap<>();
        response.put("pr", convertToPRResponse(updatedPR));
        response.put("replaced_by", newReviewer.getUserId());
        return response;
    }

    private Set<User> assignReviewers(User author) {
        Team team = author.getTeam();
        List<User> availableReviewers = userRepository
                .findByTeamAndIsActiveTrueAndUserIdNot(team, author.getUserId());

        Collections.shuffle(availableReviewers);

        return availableReviewers.stream()
                .limit(2)
                .collect(Collectors.toSet());
    }

    private Map<String, Object> convertToPRResponse(PullRequest pr) {
        List<String> reviewerIds = pr.getAssignedReviewers().stream()
                .map(User::getUserId)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("pull_request_id", pr.getPullRequestId());
        response.put("pull_request_name", pr.getPullRequestName());
        response.put("author_id", pr.getAuthor().getUserId());
        response.put("status", pr.getStatus().name());
        response.put("assigned_reviewers", reviewerIds);
        response.put("createdAt", pr.getCreatedAt());
        response.put("mergedAt", pr.getMergedAt());

        return response;
    }
}
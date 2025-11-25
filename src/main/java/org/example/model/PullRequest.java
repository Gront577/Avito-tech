package org.example.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "pull_requests")
public class PullRequest {
    @Id
    @Column(name = "pull_request_id")
    private String pullRequestId;

    @Column(name = "pull_request_name", nullable = false)
    private String pullRequestName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PRStatus status = PRStatus.OPEN;

    @ManyToMany
    @JoinTable(
            name = "pr_reviewers",
            joinColumns = @JoinColumn(name = "pr_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> assignedReviewers = new HashSet<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "merged_at")
    private LocalDateTime mergedAt;

    public PullRequest() {}

    public PullRequest(String pullRequestId, String pullRequestName, User author) {
        this.pullRequestId = pullRequestId;
        this.pullRequestName = pullRequestName;
        this.author = author;
        this.createdAt = LocalDateTime.now();
    }

    public String getPullRequestId() {
        return pullRequestId;
    }

    public void setPullRequestId(String pullRequestId) {
        this.pullRequestId = pullRequestId;
    }

    public String getPullRequestName() {
        return pullRequestName;
    }

    public void setPullRequestName(String pullRequestName) {
        this.pullRequestName = pullRequestName;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public PRStatus getStatus() {
        return status;
    }

    public void setStatus(PRStatus status) {
        this.status = status;
    }

    public Set<User> getAssignedReviewers() {
        return assignedReviewers;
    }

    public void setAssignedReviewers(Set<User> assignedReviewers) {
        this.assignedReviewers = assignedReviewers;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getMergedAt() {
        return mergedAt;
    }

    public void setMergedAt(LocalDateTime mergedAt) {
        this.mergedAt = mergedAt;
    }
}
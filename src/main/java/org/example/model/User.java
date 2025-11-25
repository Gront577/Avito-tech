package org.example.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(nullable = false)
    private String username;

    @Column(name = "is_active")
    private boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_name")
    private Team team;

    @ManyToMany(mappedBy = "assignedReviewers")
    private Set<PullRequest> assignedPullRequests = new HashSet<>();

    public User() {}

    public User(String userId, String username, boolean isActive) {
        this.userId = userId;
        this.username = username;
        this.isActive = isActive;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Set<PullRequest> getAssignedPullRequests() {
        return assignedPullRequests;
    }

    public void setAssignedPullRequests(Set<PullRequest> assignedPullRequests) {
        this.assignedPullRequests = assignedPullRequests;
    }
}
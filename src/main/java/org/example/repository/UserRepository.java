package org.example.repository;

import org.example.model.Team;
import org.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    List<User> findByTeamAndIsActiveTrue(Team team);
    List<User> findByTeamAndIsActiveTrueAndUserIdNot(Team team, String excludedUserId);
    List<User> findByIsActiveTrue();
}
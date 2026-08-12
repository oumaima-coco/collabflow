package com.collabflow.userservice;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "team_memberships")
public class TeamMembership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long teamId;

    @Column(nullable = false)
    private Long userId; // references a User in auth-service, by ID only

    @Column(nullable = false)
    private String role; // "OWNER" or "MEMBER"

    @Column(nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    public TeamMembership() {
    }

    public TeamMembership(Long teamId, Long userId, String role) {
        this.teamId = teamId;
        this.userId = userId;
        this.role = role;
        this.joinedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getTeamId() {
        return teamId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getRole() {
        return role;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }
}

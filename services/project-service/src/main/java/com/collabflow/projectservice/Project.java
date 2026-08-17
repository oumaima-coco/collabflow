package com.collabflow.projectservice;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "projects")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private Long teamId; // references a Team in user-service, by ID only

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Project() {
    }

    public Project(String name, String description, Long teamId) {
        this.name = name;
        this.description = description;
        this.teamId = teamId;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getTeamId() {
        return teamId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

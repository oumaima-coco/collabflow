package com.collabflow.projectservice;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
public class CreateProjectRequest {
    @NotBlank(message = "Project name is required")
    private String name;

    private String description;

    @NotNull(message = "Team ID is required")
    private Long teamId;

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
    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }
}

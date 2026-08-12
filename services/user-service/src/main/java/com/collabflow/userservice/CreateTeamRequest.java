package com.collabflow.userservice;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
public class CreateTeamRequest {
    @NotBlank(message = "Team name is required")
    private String name;

    private String description;

    @NotNull(message = "Owner ID is required")
    private Long ownerId;

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

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
}

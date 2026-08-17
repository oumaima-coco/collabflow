package com.collabflow.projectservice;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserServiceClient userServiceClient;

    public ProjectService(ProjectRepository projectRepository, UserServiceClient userServiceClient) {
        this.projectRepository = projectRepository;
        this.userServiceClient = userServiceClient;
    }

    public Project createProject(String name, String description, Long teamId) {
        if (!userServiceClient.teamExists(teamId)) {
            throw new IllegalArgumentException("Team does not exist");
        }

        Project project = new Project(name, description, teamId);
        return projectRepository.save(project);
    }
    public List<Project> getProjectsForTeam(Long teamId) {
        return projectRepository.findByTeamId(teamId);
    }
}

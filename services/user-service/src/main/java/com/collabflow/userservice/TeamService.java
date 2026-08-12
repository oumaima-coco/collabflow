package com.collabflow.userservice;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TeamService {
    private final TeamRepository teamRepository;
    private final TeamMembershipRepository membershipRepository;
    private final AuthServiceClient authServiceClient;

    public TeamService(TeamRepository teamRepository,
                       TeamMembershipRepository membershipRepository,
                       AuthServiceClient authServiceClient) {
        this.teamRepository = teamRepository;
        this.membershipRepository = membershipRepository;
        this.authServiceClient = authServiceClient;
    }

    public Team createTeam(String name, String description, Long ownerId) {
        if (!authServiceClient.userExists(ownerId)) {
            throw new IllegalArgumentException("Owner user does not exist");
        }

        Team team = new Team(name, description, ownerId);
        team = teamRepository.save(team);

        membershipRepository.save(new TeamMembership(team.getId(), ownerId, "OWNER"));

        return team;
    }

    public TeamMembership addMember(Long teamId, Long userId) {
        if (!authServiceClient.userExists(userId)) {
            throw new IllegalArgumentException("User does not exist");
        }

        if (!teamRepository.existsById(teamId)) {
            throw new IllegalArgumentException("Team does not exist");
        }

        if (membershipRepository.findByTeamIdAndUserId(teamId, userId).isPresent()) {
            throw new IllegalArgumentException("User is already a member of this team");
        }

        return membershipRepository.save(new TeamMembership(teamId, userId, "MEMBER"));
    }

    public List<Team> getTeamsForUser(Long userId) {
        List<TeamMembership> memberships = membershipRepository.findByUserId(userId);
        return memberships.stream()
                .map(m -> teamRepository.findById(m.getTeamId()).orElse(null))
                .filter(t -> t != null)
                .toList();
    }
}

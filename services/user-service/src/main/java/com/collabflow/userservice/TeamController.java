package com.collabflow.userservice;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teams")
public class TeamController {
    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @PostMapping
    public ResponseEntity<?> createTeam(@Valid @RequestBody CreateTeamRequest request) {
        try {
            Team team = teamService.createTeam(request.getName(), request.getDescription(), request.getOwnerId());
            return ResponseEntity.status(HttpStatus.CREATED).body(team);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{teamId}/members")
    public ResponseEntity<?> addMember(@PathVariable Long teamId, @Valid @RequestBody AddMemberRequest request) {
        try {
            TeamMembership membership = teamService.addMember(teamId, request.getUserId());
            return ResponseEntity.status(HttpStatus.CREATED).body(membership);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Team>> getTeamsForUser(@PathVariable Long userId) {
        return ResponseEntity.ok(teamService.getTeamsForUser(userId));
    }
}

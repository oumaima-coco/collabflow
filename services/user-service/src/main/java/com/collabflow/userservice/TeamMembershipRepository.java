package com.collabflow.userservice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TeamMembershipRepository  extends JpaRepository<TeamMembership, Long> {
    List<TeamMembership> findByUserId(Long userId);
    List<TeamMembership> findByTeamId(Long teamId);
    Optional<TeamMembership> findByTeamIdAndUserId(Long teamId, Long userId);
}

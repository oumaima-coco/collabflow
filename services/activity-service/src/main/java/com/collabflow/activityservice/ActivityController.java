package com.collabflow.activityservice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/activity")
public class ActivityController {

    private final ActivityLogRepository activityLogRepository;

    public ActivityController(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<ActivityLog>> getActivityForTask(@PathVariable Long taskId) {
        return ResponseEntity.ok(activityLogRepository.findByTaskIdOrderByCreatedAtDesc(taskId));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<ActivityLog>> getActivityForProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(activityLogRepository.findByProjectIdOrderByCreatedAtDesc(projectId));
    }
}
package com.collabflow.activityservice;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/activity")
public class ActivityController {
    private final ActivityLogRepository activityLogRepository;
    private final CommentRepository commentRepository;

    public ActivityController(ActivityLogRepository activityLogRepository, CommentRepository commentRepository) {
        this.activityLogRepository = activityLogRepository;
        this.commentRepository = commentRepository;
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<ActivityLog>> getActivityForTask(@PathVariable Long taskId) {
        return ResponseEntity.ok(activityLogRepository.findByTaskIdOrderByCreatedAtDesc(taskId));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<ActivityLog>> getActivityForProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(activityLogRepository.findByProjectIdOrderByCreatedAtDesc(projectId));
    }

    @PostMapping("/task/{taskId}/comments")
    public ResponseEntity<Comment> createComment(@PathVariable Long taskId, @Valid @RequestBody CreateCommentRequest request) {
        Comment comment = new Comment(taskId, request.getAuthorId(), request.getContent());
        Comment saved = commentRepository.save(comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/task/{taskId}/comments")
    public ResponseEntity<List<Comment>> getCommentsForTask(@PathVariable Long taskId) {
        return ResponseEntity.ok(commentRepository.findByTaskIdOrderByCreatedAtAsc(taskId));
    }
}
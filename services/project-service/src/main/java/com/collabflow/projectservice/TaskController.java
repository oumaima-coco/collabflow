package com.collabflow.projectservice;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/projects")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/{projectId}/tasks")
    public ResponseEntity<?> createTask(@PathVariable Long projectId, @Valid @RequestBody CreateTaskRequest request) {
        try {
            Task task = taskService.createTask(projectId, request.getTitle(), request.getDescription(),
                    request.getAssigneeId(), request.getDueDate());
            return ResponseEntity.status(HttpStatus.CREATED).body(task);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (ServiceUnavailableException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage());
        }
    }

    @GetMapping("/{projectId}/tasks")
    public ResponseEntity<List<Task>> getTasksForProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(taskService.getTasksForProject(projectId));
    }

    @PatchMapping("/tasks/{taskId}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long taskId, @Valid @RequestBody UpdateStatusRequest request) {
        try {
            Task task = taskService.updateStatus(taskId, request.getStatus());
            return ResponseEntity.ok(task);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/tasks/{taskId}/assign")
    public ResponseEntity<?> assignTask(@PathVariable Long taskId, @Valid @RequestBody AssignTaskRequest request) {
        try {
            Task task = taskService.assignTask(taskId, request.getAssigneeId());
            return ResponseEntity.ok(task);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (ServiceUnavailableException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage());
        }
    }
}
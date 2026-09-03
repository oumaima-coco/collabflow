package com.collabflow.projectservice;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final AuthServiceClient authServiceClient;
    private final TaskEventProducer taskEventProducer;

    public TaskService(TaskRepository taskRepository, ProjectRepository projectRepository,
                       AuthServiceClient authServiceClient, TaskEventProducer taskEventProducer) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.authServiceClient = authServiceClient;
        this.taskEventProducer = taskEventProducer;
    }

    public Task createTask(Long projectId, String title, String description, Long assigneeId, LocalDate dueDate) {
        if (!projectRepository.existsById(projectId)) {
            throw new IllegalArgumentException("Project does not exist");
        }
        if (assigneeId != null && !authServiceClient.userExists(assigneeId)) {
            throw new IllegalArgumentException("Assignee does not exist");
        }
        Task task = new Task(projectId, title, description, assigneeId, dueDate);
        Task savedTask = taskRepository.save(task);

        taskEventProducer.publishTaskCreated(
                new TaskCreatedEvent(savedTask.getId(), savedTask.getTitle(), savedTask.getProjectId())
        );

        return savedTask;
    }

    public List<Task> getTasksForProject(Long projectId) {
        return taskRepository.findByProjectId(projectId);
    }

    public Task updateStatus(Long taskId, TaskStatus newStatus) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
        String oldStatus = task.getStatus().toString();
        task.setStatus(newStatus);
        Task savedTask = taskRepository.save(task);

        taskEventProducer.publishTaskStatusChanged(
                new TaskStatusChangedEvent(savedTask.getId(), savedTask.getTitle(), savedTask.getProjectId(),
                        oldStatus, newStatus.toString())
        );

        return savedTask;
    }

    public Task assignTask(Long taskId, Long assigneeId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
        if (!authServiceClient.userExists(assigneeId)) {
            throw new IllegalArgumentException("Assignee does not exist");
        }
        task.setAssigneeId(assigneeId);
        Task savedTask = taskRepository.save(task);
        taskEventProducer.publishTaskAssigned(
                new TaskAssignedEvent(savedTask.getId(), savedTask.getTitle(), assigneeId, savedTask.getProjectId())
        );
        return savedTask;
    }

}

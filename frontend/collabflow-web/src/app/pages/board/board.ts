import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { CdkDragDrop, DragDropModule } from '@angular/cdk/drag-drop';
import { Task, TaskModel, TaskStatus } from '../../services/task';
import { Project, ProjectModel } from '../../services/project';
import { Team, TeamMembershipModel } from '../../services/team';
import { Activity, ActivityLogModel, CommentModel } from '../../services/activity';
import { Auth } from '../../services/auth';

@Component({
  selector: 'app-board',
  imports: [CommonModule, RouterLink, ReactiveFormsModule, DragDropModule],
  templateUrl: './board.html',
  styleUrl: './board.scss',
})
export class Board implements OnInit {
  projectId!: number;
  currentUserId = signal<number | null>(null);
  project = signal<ProjectModel | null>(null);
  members = signal<TeamMembershipModel[]>([]);
  tasks = signal<TaskModel[]>([]);
  loading = signal<boolean>(true);
  error = signal<string | null>(null);
  showForm = signal<boolean>(false);

  expandedTaskId = signal<number | null>(null);
  activityLog = signal<ActivityLogModel[]>([]);
  comments = signal<CommentModel[]>([]);
  commentForm = new FormGroup({
    content: new FormControl('', [Validators.required]),
  });

  todoTasks = computed(() => this.tasks().filter(t => t.status === 'TODO'));
  inProgressTasks = computed(() => this.tasks().filter(t => t.status === 'IN_PROGRESS'));
  doneTasks = computed(() => this.tasks().filter(t => t.status === 'DONE'));

  taskForm = new FormGroup({
    title: new FormControl('', [Validators.required]),
    description: new FormControl(''),
    dueDate: new FormControl(''),
  });

  constructor(
    private route: ActivatedRoute,
    private taskService: Task,
    private projectService: Project,
    private teamService: Team,
    private activityService: Activity,
    private auth: Auth
  ) {}

  ngOnInit(): void {
    this.projectId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadProjectAndMembers();
    this.loadTasks();
    this.auth.getCurrentUser().subscribe({
      next: (user) => this.currentUserId.set(user.id),
      error: (err) => console.error('Failed to load current user', err)
    });
  }

  loadProjectAndMembers(): void {
    this.projectService.getProjectById(this.projectId).subscribe({
      next: (project) => {
        this.project.set(project);
        this.teamService.getMembersForTeam(project.teamId).subscribe({
          next: (members) => this.members.set(members),
          error: (err) => console.error('Failed to load team members', err)
        });
      },
      error: (err) => console.error('Failed to load project', err)
    });
  }

  loadTasks(): void {
    this.loading.set(true);
    this.taskService.getTasksForProject(this.projectId).subscribe({
      next: (tasks) => {
        this.tasks.set(tasks);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set('Failed to load tasks');
        this.loading.set(false);
        console.error(err);
      }
    });
  }

  toggleForm(): void {
    this.showForm.set(!this.showForm());
  }

  onSubmit(): void {
    if (this.taskForm.invalid) return;

    const formValue = this.taskForm.value;
    const request: any = {
      title: formValue.title,
    };
    if (formValue.description) request.description = formValue.description;
    if (formValue.dueDate) request.dueDate = formValue.dueDate;

    this.taskService.createTask(this.projectId, request).subscribe({
      next: (newTask) => {
        this.tasks.update(tasks => [...tasks, newTask]);
        this.taskForm.reset();
        this.showForm.set(false);
      },
      error: (err) => {
        console.error('Failed to create task', err);
      }
    });
  }

  drop(event: CdkDragDrop<TaskModel[]>, newStatus: TaskStatus): void {
    if (event.previousContainer === event.container) {
      return;
    }

    const task = event.item.data as TaskModel;

    this.taskService.updateStatus(task.id, newStatus).subscribe({
      next: (updatedTask) => {
        this.tasks.update(tasks =>
          tasks.map(t => t.id === updatedTask.id ? updatedTask : t)
        );
      },
      error: (err) => {
        console.error('Failed to update task status', err);
      }
    });
  }

  assignTask(task: TaskModel, event: Event): void {
    const select = event.target as HTMLSelectElement;
    const assigneeId = Number(select.value);
    if (!assigneeId) return;

    this.taskService.assignTask(task.id, assigneeId).subscribe({
      next: (updatedTask) => {
        this.tasks.update(tasks =>
          tasks.map(t => t.id === updatedTask.id ? updatedTask : t)
        );
      },
      error: (err) => {
        console.error('Failed to assign task', err);
      }
    });
  }

  toggleDetails(task: TaskModel): void {
    if (this.expandedTaskId() === task.id) {
      this.expandedTaskId.set(null);
      return;
    }

    this.expandedTaskId.set(task.id);
    this.loadActivityAndComments(task.id);
  }

  loadActivityAndComments(taskId: number): void {
    this.activityService.getActivityForTask(taskId).subscribe({
      next: (log) => this.activityLog.set(log),
      error: (err) => console.error('Failed to load activity log', err)
    });

    this.activityService.getCommentsForTask(taskId).subscribe({
      next: (comments) => this.comments.set(comments),
      error: (err) => console.error('Failed to load comments', err)
    });
  }

  submitComment(task: TaskModel): void {
    if (this.commentForm.invalid) return;
    const userId = this.currentUserId();
    if (!userId) return;

    const content = this.commentForm.value.content!;

    this.activityService.createComment(task.id, { content, authorId: userId }).subscribe({
      next: (newComment) => {
        this.comments.update(comments => [...comments, newComment]);
        this.commentForm.reset();
      },
      error: (err) => console.error('Failed to post comment', err)
    });
  }
}
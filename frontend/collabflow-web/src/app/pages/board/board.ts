import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { Task, TaskModel, TaskStatus } from '../../services/task';

@Component({
  selector: 'app-board',
  imports: [CommonModule, RouterLink, ReactiveFormsModule],
  templateUrl: './board.html',
  styleUrl: './board.scss',
})
export class Board implements OnInit {
  projectId!: number;
  tasks = signal<TaskModel[]>([]);
  loading = signal<boolean>(true);
  error = signal<string | null>(null);
  showForm = signal<boolean>(false);

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
    private taskService: Task
  ) {}

  ngOnInit(): void {
    this.projectId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadTasks();
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

  nextStatus(current: TaskStatus): TaskStatus | null {
    if (current === 'TODO') return 'IN_PROGRESS';
    if (current === 'IN_PROGRESS') return 'DONE';
    return null;
  }

  moveTask(task: TaskModel): void {
    const next = this.nextStatus(task.status);
    if (!next) return;

    this.taskService.updateStatus(task.id, next).subscribe({
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
}
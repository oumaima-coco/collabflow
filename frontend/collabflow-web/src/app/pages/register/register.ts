import { Component } from '@angular/core';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Auth } from '../../services/auth';

@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule],
  templateUrl: './register.html',
  styleUrl: './register.scss',
})
export class Register {
  registerForm = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', [Validators.required, Validators.minLength(8)]),
  });

  errorMessage = '';
  successMessage = '';

  constructor(private auth: Auth, private router: Router) {}

  onSubmit() {
    if (this.registerForm.invalid) return;

    this.errorMessage = '';
    this.successMessage = '';

    this.auth.register(this.registerForm.value as { email: string; password: string }).subscribe({
      next: () => {
        this.successMessage = 'Account created! Redirecting to login...';
        setTimeout(() => this.router.navigate(['/login']), 1500);
      },
      error: (err) => {
        this.errorMessage = err.error ?? 'Registration failed. Please try again.';
      }
    });
  }
}
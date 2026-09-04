import { Component } from '@angular/core';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Auth } from '../../services/auth';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class Login {
  loginForm = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', [Validators.required]),
  });
  errorMessage = '';
  constructor(private auth: Auth, private router: Router) {}
  onSubmit() {
    if (this.loginForm.invalid) return;
    this.errorMessage = '';
    this.auth.login(this.loginForm.value as { email: string; password: string }).subscribe({
      next: (response) => {
        this.auth.saveToken(response.token);
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.errorMessage = err.error ?? 'Login failed. Please try again.';
      }
    });
  }
}
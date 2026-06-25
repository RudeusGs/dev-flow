import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { RegisterRequest } from '../../../core/models/auth.model';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <div class="login-wrapper">
      <!-- Glow Orbs in Background -->
      <div class="glow-orb orb-1"></div>
      <div class="glow-orb orb-2"></div>
      <div class="glow-orb orb-3"></div>

      <div class="login-card">
        <div class="brand">
          <div class="logo-container">
            <i class="bi bi-git"></i>
          </div>
          <h2>DevFlow</h2>
        </div>
        
        <div class="welcome-text">
          <h3>Create an account</h3>
          <p>Join the best developer workflow platform.</p>
        </div>

        <form [formGroup]="registerForm" (ngSubmit)="onSubmit()" class="login-form">
          <div class="form-group">
            <label for="email">Email</label>
            <div class="input-wrapper">
              <i class="bi bi-envelope input-icon"></i>
              <input 
                id="email" 
                type="email" 
                class="input-field-custom" 
                formControlName="email"
                placeholder="name@company.com">
            </div>
            <p *ngIf="registerForm.get('email')?.touched && registerForm.get('email')?.errors?.['email']" class="field-error-text">
              Please enter a valid email address.
            </p>
          </div>

          <div class="form-group">
            <label for="username">Username</label>
            <div class="input-wrapper">
              <i class="bi bi-person input-icon"></i>
              <input 
                id="username" 
                type="text" 
                class="input-field-custom" 
                formControlName="username"
                placeholder="Choose a username">
            </div>
            <p *ngIf="registerForm.get('username')?.touched && registerForm.get('username')?.errors?.['minlength']" class="field-error-text">
              Username must be at least 3 characters long.
            </p>
          </div>

          <div class="form-group">
            <label for="password">Password</label>
            <div class="input-wrapper">
              <i class="bi bi-lock input-icon"></i>
              <input 
                id="password" 
                [type]="showPassword ? 'text' : 'password'" 
                class="input-field-custom" 
                formControlName="password"
                placeholder="••••••••">
              <button type="button" class="password-toggle" (click)="togglePasswordVisibility()">
                <i class="bi" [ngClass]="showPassword ? 'bi-eye-slash' : 'bi-eye'"></i>
              </button>
            </div>
            <p class="field-helper-text">
              Must be at least 8 characters, containing uppercase, lowercase, digit, and special char (@#$%^&+=!)
            </p>
            <p *ngIf="registerForm.get('password')?.touched && registerForm.get('password')?.invalid && !registerForm.get('password')?.errors?.['required']" class="field-error-text">
              Password doesn't meet the security requirements above.
            </p>
          </div>

          <!-- Error Alert Box -->
          <div *ngIf="errorMsg" class="error-box animate-shake">
            <i class="bi bi-exclamation-triangle-fill"></i>
            <span class="error-text-container">{{ errorMsg }}</span>
          </div>

          <button type="submit" class="submit-btn-custom" [disabled]="registerForm.invalid || isLoading">
            <span *ngIf="isLoading" class="spinner"></span>
            <span>{{ isLoading ? 'Creating account...' : 'Sign up' }}</span>
            <i *ngIf="!isLoading" class="bi bi-arrow-right-short"></i>
          </button>
          
          <p class="footer-text">
            Already have an account? 
            <a routerLink="/login" class="footer-link">Sign in</a>
          </p>
        </form>
      </div>
    </div>
  `,
  styles: [`
    .login-wrapper {
      min-height: 100vh;
      display: flex;
      align-items: center;
      justify-content: center;
      background-color: #090d16;
      background-image: 
        radial-gradient(at 0% 0%, rgba(37, 99, 235, 0.15) 0px, transparent 50%),
        radial-gradient(at 100% 100%, rgba(16, 185, 129, 0.12) 0px, transparent 50%),
        radial-gradient(at 50% 50%, rgba(168, 85, 247, 0.08) 0px, transparent 50%);
      position: relative;
      overflow: hidden;
      padding: 1.5rem;
      font-family: 'Inter', sans-serif;
    }

    /* Background Glowing Orbs */
    .glow-orb {
      position: absolute;
      border-radius: 50%;
      filter: blur(100px);
      opacity: 0.25;
      z-index: 0;
      pointer-events: none;
    }
    .orb-1 {
      width: 350px;
      height: 350px;
      background: #3b82f6;
      top: 10%;
      left: 5%;
      animation: float-1 20s infinite alternate ease-in-out;
    }
    .orb-2 {
      width: 450px;
      height: 450px;
      background: #10b981;
      bottom: 10%;
      right: 5%;
      animation: float-2 25s infinite alternate ease-in-out;
    }
    .orb-3 {
      width: 250px;
      height: 250px;
      background: #ec4899;
      top: 60%;
      left: 50%;
      transform: translate(-50%, -50%);
      opacity: 0.06;
    }

    @keyframes float-1 {
      0% { transform: translate(0, 0) scale(1); }
      50% { transform: translate(30px, -50px) scale(1.05); }
      100% { transform: translate(-15px, 15px) scale(0.95); }
    }
    @keyframes float-2 {
      0% { transform: translate(0, 0) scale(1); }
      50% { transform: translate(-40px, 40px) scale(1.03); }
      100% { transform: translate(20px, -20px) scale(0.95); }
    }

    /* Glass Card */
    .login-card {
      width: 100%;
      max-width: 440px;
      padding: 3.5rem 2.5rem;
      background: rgba(17, 24, 39, 0.7);
      border: 1px solid rgba(255, 255, 255, 0.08);
      backdrop-filter: blur(20px) saturate(180%);
      -webkit-backdrop-filter: blur(20px) saturate(180%);
      border-radius: 1.25rem;
      box-shadow: 
        0 10px 40px rgba(0, 0, 0, 0.5),
        0 1px 3px rgba(255, 255, 255, 0.05) inset;
      z-index: 10;
      display: flex;
      flex-direction: column;
      gap: 2rem;
      animation: fadeIn 0.5s cubic-bezier(0.16, 1, 0.3, 1) forwards;
    }

    @keyframes fadeIn {
      from { opacity: 0; transform: translateY(15px); }
      to { opacity: 1; transform: translateY(0); }
    }

    /* Brand Section */
    .brand {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 0.75rem;
    }
    .logo-container {
      width: 42px;
      height: 42px;
      background: linear-gradient(135deg, #3b82f6 0%, #10b981 100%);
      border-radius: 0.625rem;
      display: flex;
      align-items: center;
      justify-content: center;
      box-shadow: 0 0 15px rgba(59, 130, 246, 0.3);
      transition: transform 0.4s ease;
    }
    .logo-container i {
      color: #ffffff;
      font-size: 1.35rem;
    }
    .brand h2 {
      font-size: 1.65rem;
      font-weight: 700;
      background: linear-gradient(135deg, #ffffff 0%, #cbd5e1 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      letter-spacing: -0.025em;
    }
    .login-card:hover .logo-container {
      transform: rotate(10deg) scale(1.05);
    }

    /* Welcome Text */
    .welcome-text {
      text-align: center;
      display: flex;
      flex-direction: column;
      gap: 0.25rem;
    }
    .welcome-text h3 {
      font-size: 1.35rem;
      font-weight: 600;
      color: #f8fafc;
    }
    .welcome-text p {
      color: #94a3b8;
      font-size: 0.85rem;
    }

    /* Form Styles */
    .login-form {
      display: flex;
      flex-direction: column;
      gap: 1.25rem;
    }
    .form-group {
      display: flex;
      flex-direction: column;
      gap: 0.5rem;
    }
    .form-group label {
      font-size: 0.8rem;
      font-weight: 500;
      color: #cbd5e1;
      letter-spacing: 0.025em;
    }

    .field-helper-text {
      font-size: 0.725rem;
      color: #64748b;
      margin-top: 0.15rem;
      line-height: 1.3;
    }
    
    .field-error-text {
      font-size: 0.725rem;
      color: #f87171;
      margin-top: 0.15rem;
      line-height: 1.3;
    }

    /* Input Wrapper & Styled Input */
    .input-wrapper {
      position: relative;
      display: flex;
      align-items: center;
    }
    .input-icon {
      position: absolute;
      left: 1rem;
      color: #64748b;
      font-size: 1rem;
      transition: color 0.25s;
      pointer-events: none;
    }
    .input-field-custom {
      width: 100%;
      padding: 0.8rem 1rem 0.8rem 2.5rem;
      font-size: 0.875rem;
      background: rgba(15, 23, 42, 0.5);
      border: 1px solid rgba(255, 255, 255, 0.08);
      border-radius: 0.625rem;
      color: #f8fafc;
      transition: all 0.2s ease;
      outline: none;
      font-family: inherit;
    }
    .input-field-custom::placeholder {
      color: #475569;
    }
    .input-field-custom:focus {
      border-color: #3b82f6;
      background: rgba(15, 23, 42, 0.75);
      box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.15);
    }
    .input-field-custom:focus + .input-icon,
    .input-wrapper:focus-within .input-icon {
      color: #3b82f6;
    }

    /* Password Toggle */
    .password-toggle {
      position: absolute;
      right: 1rem;
      background: none;
      border: none;
      color: #64748b;
      cursor: pointer;
      padding: 0.25rem;
      display: flex;
      align-items: center;
      justify-content: center;
      transition: color 0.2s;
    }
    .password-toggle:hover {
      color: #94a3b8;
    }

    /* Buttons */
    .submit-btn-custom {
      margin-top: 0.5rem;
      width: 100%;
      padding: 0.8rem;
      font-size: 0.9rem;
      font-weight: 600;
      color: #ffffff;
      background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
      border: none;
      border-radius: 0.625rem;
      cursor: pointer;
      transition: all 0.2s ease;
      box-shadow: 0 4px 12px rgba(37, 99, 235, 0.25);
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 0.375rem;
    }
    .submit-btn-custom:hover:not(:disabled) {
      background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
      transform: translateY(-1px);
      box-shadow: 0 6px 15px rgba(37, 99, 235, 0.3);
    }
    .submit-btn-custom:active:not(:disabled) {
      transform: translateY(0);
    }
    .submit-btn-custom:disabled {
      opacity: 0.5;
      cursor: not-allowed;
      transform: none;
      box-shadow: none;
    }

    /* Spinner */
    .spinner {
      width: 1.1rem;
      height: 1.1rem;
      border: 2px solid rgba(255, 255, 255, 0.3);
      border-radius: 50%;
      border-top-color: #ffffff;
      animation: spin 0.8s linear infinite;
    }
    @keyframes spin {
      to { transform: rotate(360deg); }
    }

    /* Error Box */
    .error-box {
      background: rgba(239, 68, 68, 0.08);
      border: 1px solid rgba(239, 68, 68, 0.15);
      border-radius: 0.5rem;
      padding: 0.75rem;
      display: flex;
      align-items: flex-start;
      gap: 0.5rem;
      color: #fca5a5;
      font-size: 0.8rem;
      margin-top: 0.25rem;
    }
    .error-box i {
      margin-top: 0.1rem;
      flex-shrink: 0;
    }
    .error-text-container {
      line-height: 1.4;
      text-align: left;
    }
    .animate-shake {
      animation: shake 0.4s ease;
    }
    @keyframes shake {
      0%, 100% { transform: translateX(0); }
      25% { transform: translateX(-4px); }
      75% { transform: translateX(4px); }
    }

    /* Footer Text */
    .footer-text {
      text-align: center;
      color: #94a3b8;
      font-size: 0.85rem;
      margin-top: 0.25rem;
    }
    .footer-link {
      color: #3b82f6;
      font-weight: 500;
      text-decoration: none;
      transition: color 0.2s;
    }
    .footer-link:hover {
      color: #60a5fa;
      text-decoration: underline;
    }
  `]
})
export class RegisterComponent {
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private authService = inject(AuthService);

  // Regex pattern enforcing: min 8 char, 1 digit, 1 lowercase, 1 uppercase, 1 special character
  private passwordPattern = /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$/;

  registerForm = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    username: ['', [Validators.required, Validators.minLength(3)]],
    password: ['', [Validators.required, Validators.minLength(8), Validators.pattern(this.passwordPattern)]]
  });

  errorMsg = '';
  isLoading = false;
  showPassword = false;

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  onSubmit(): void {
    if (this.registerForm.valid) {
      this.isLoading = true;
      this.errorMsg = '';

      const registerReq: RegisterRequest = {
        email: this.registerForm.value.email!,
        username: this.registerForm.value.username!,
        password: this.registerForm.value.password!
      };

      this.authService.register(registerReq).subscribe({
        next: () => {
          this.isLoading = false;
          this.router.navigate(['/']);
        },
        error: (err) => {
          this.isLoading = false;
          
          // Formats field-level validation errors returned from Spring Boot
          if (err?.error?.errors) {
            const validationErrors = Object.entries(err.error.errors)
              .map(([field, msg]) => `${field}: ${msg}`)
              .join(' | ');
            this.errorMsg = `Validation failed - ${validationErrors}`;
          } else {
            this.errorMsg = err?.error?.message || 'Registration failed';
          }
        }
      });
    }
  }
}

import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, AbstractControl } from '@angular/forms';
import { Router } from '@angular/router';
import { UserService } from '../service/user.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css'],
  standalone: false
})
export class RegisterComponent implements OnInit {
  registerForm: FormGroup;
  isLoading = false;
  hidePassword = true;
  hideConfirmPassword = true;
  avatarFile: File | null = null;
  coverImageFile: File | null = null;
  avatarPreview: string | null = null;
  coverImagePreview: string | null = null;

  constructor(
    private fb: FormBuilder,
    private UserService: UserService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {
    this.registerForm = this.fb.group({
      fullName: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      username: ['', [Validators.required, Validators.minLength(3), Validators.pattern(/^[a-zA-Z0-9_]+$/)]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]],
      avatar: ['', [Validators.required]],
      coverImage: ['']
    }, { validators: this.passwordMatchValidator });
  }

  ngOnInit(): void {}

  // Custom validator for password confirmation
  passwordMatchValidator(control: AbstractControl): { [key: string]: boolean } | null {
    const password = control.get('password');
    const confirmPassword = control.get('confirmPassword');

    if (!password || !confirmPassword) {
      return null;
    }

    return password.value === confirmPassword.value ? null : { passwordMismatch: true };
  }

  onFileSelected(event: any, fileType: 'avatar' | 'coverImage'): void {
    const file = event.target.files[0];

    if (file) {
      // Validate file type
      const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif'];
      if (!allowedTypes.includes(file.type)) {
        this.snackBar.open('Please select a valid image file (JPEG, PNG, GIF)', 'Close', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
        return;
      }

      // Validate file size (5MB limit)
      const maxSize = 5 * 1024 * 1024; // 5MB
      if (file.size > maxSize) {
        this.snackBar.open('File size should not exceed 5MB', 'Close', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });
        return;
      }

      if (fileType === 'avatar') {
        this.avatarFile = file;
        this.createImagePreview(file, 'avatar');
        this.registerForm.patchValue({ avatar: file.name });
        this.registerForm.get('avatar')?.updateValueAndValidity();
      } else {
        this.coverImageFile = file;
        this.createImagePreview(file, 'coverImage');
        this.registerForm.patchValue({ coverImage: file.name });
      }
    }
  }

  private createImagePreview(file: File, type: 'avatar' | 'coverImage'): void {
    const reader = new FileReader();
    reader.onload = () => {
      if (type === 'avatar') {
        this.avatarPreview = reader.result as string;
      } else {
        this.coverImagePreview = reader.result as string;
      }
    };
    reader.readAsDataURL(file);
  }

  onSubmit(): void {
    if (this.registerForm.valid && this.avatarFile) {
      this.isLoading = true;

      const formData = new FormData();
      formData.append('fullName', this.registerForm.value.fullName);
      formData.append('email', this.registerForm.value.email);
      formData.append('userName', this.registerForm.value.username);
      formData.append('password', this.registerForm.value.password);
      formData.append('avatar', this.avatarFile);

      if (this.coverImageFile) {
        formData.append('coverImage', this.coverImageFile);
      }

      this.UserService.register(formData).subscribe({
        next: (user) => {
          this.snackBar.open('Registration successful! Please log in.', 'Close', {
            duration: 5000,
            panelClass: ['success-snackbar']
          });
          this.router.navigate(['/user/login']);
        },
        error: (error) => {
          this.isLoading = false;
          this.snackBar.open(error.message || 'Registration failed', 'Close', {
            duration: 5000,
            panelClass: ['error-snackbar']
          });
        }
      });
    } else {
      this.markFormGroupTouched();
    }
  }

  private markFormGroupTouched(): void {
    Object.keys(this.registerForm.controls).forEach(key => {
      const control = this.registerForm.get(key);
      control?.markAsTouched();
    });
  }

  getErrorMessage(fieldName: string): string {
    const control = this.registerForm.get(fieldName);

    if (control?.hasError('required')) {
      return `${this.getFieldDisplayName(fieldName)} is required`;
    }

    if (control?.hasError('email')) {
      return 'Please enter a valid email address';
    }

    if (control?.hasError('minlength')) {
      const minLength = control.errors?.['minlength'].requiredLength;
      return `${this.getFieldDisplayName(fieldName)} must be at least ${minLength} characters`;
    }

    if (control?.hasError('pattern')) {
      return 'Username can only contain letters, numbers, and underscores';
    }

    if (fieldName === 'confirmPassword' && this.registerForm.hasError('passwordMismatch')) {
      return 'Passwords do not match';
    }

    return '';
  }

  private getFieldDisplayName(fieldName: string): string {
    const fieldNames: { [key: string]: string } = {
      fullName: 'Full Name',
      email: 'Email',
      username: 'Username',
      password: 'Password',
      confirmPassword: 'Confirm Password',
      avatar: 'Avatar Image'
    };

    return fieldNames[fieldName] || fieldName;
  }
}

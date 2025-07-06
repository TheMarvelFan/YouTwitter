import { Component, OnInit } from '@angular/core';
import { UserService } from './service/user.service';
import { User } from '../models/User';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-user',
  standalone: false,
  templateUrl: './user.component.html',
  styleUrl: './user.component.css'
})
export class UserComponent implements OnInit {
  currentUser: User | null = null;
  isLoggedIn = false;
  isLoggingOut = false;

  constructor(
    private UserService: UserService,
    private router: Router,
    private snackBar: MatSnackBar,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    // Subscribe to authentication status
    this.UserService.isLoggedIn$.subscribe(isLoggedIn => {
      this.isLoggedIn = isLoggedIn;
    });

    // Subscribe to current user
    this.UserService.currentUser$.subscribe(user => {
      this.currentUser = user;
    });
  }

  onLoginClick(): void {
    this.router.navigate(['/user/login']);
  }

  onLogout(): void {
    this.isLoggingOut = true;

    this.UserService.logout().subscribe({
      next: () => {
        this.snackBar.open('Logged out successfully', 'Close', {
          duration: 3000,
          panelClass: ['success-snackbar']
        });
        this.isLoggingOut = false;
      },
      error: (error) => {
        console.log("Error during logout: ", error);
        this.isLoggingOut = false;
        this.snackBar.open('Error logging out', 'Close', {
          duration: 3000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }

  onNavigateToVideos(): void {
    this.router.navigate(['/videos']);
  }
}

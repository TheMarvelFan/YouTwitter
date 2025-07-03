import { Inject, Injectable } from '@angular/core';
import { APP_SERVICE_CONFIG } from '../../config/app.config.service';
import { AppConfig } from '../../models/app.config';
import { HttpClient } from '@angular/common/http';
import { User } from '../../models/User';
import { ResponseType } from '../../models/ResponseType';
import { BehaviorSubject, catchError, map, Observable, tap, throwError } from 'rxjs';
import { Router } from '@angular/router';

export interface LoginRequest {
  username: string;
  password: string;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();
  private isLoggedInSubject = new BehaviorSubject<boolean>(false);
  public isLoggedIn$ = this.isLoggedInSubject.asObservable();

  constructor(
    @Inject(APP_SERVICE_CONFIG)
    private config: AppConfig,
    private http: HttpClient,
    private router: Router
  ) {
    this.checkAuthStatus();
  }

  // Check if user is authenticated on app initialization
  private checkAuthStatus(): void {
    // Check if access token exists in cookies
    if (this.hasAccessToken()) {
      this.isLoggedInSubject.next(true);
      // Optionally fetch user profile here
    } else if (this.hasRefreshToken()) {
      // Try to refresh token
      this.refreshToken().subscribe({
        next: () => {
          this.isLoggedInSubject.next(true);
        },
        error: () => {
          this.logout();
        }
      });
    }
  }

  // Login user
  login(loginRequest: LoginRequest): Observable<User> {
    return this.http.post<ResponseType<User>>(`${this.config.apiUrl}/login`, loginRequest, {
      withCredentials: true
    }).pipe(
      map(response => response.data),
      tap(user => {
        this.currentUserSubject.next(user);
        this.isLoggedInSubject.next(true);
      }),
      catchError(this.handleError)
    );
  }

  // Register user
  register(formData: FormData): Observable<User> {
    return this.http.post<ResponseType<User>>(`${this.config.apiUrl}/register`, formData, {
      withCredentials: true
    }).pipe(
      map(response => response.data),
      catchError(this.handleError)
    );
  }

  // Refresh token
  refreshToken(): Observable<any> {
    return this.http.post(`${this.config.apiUrl}/refresh-token`, {}, {
      withCredentials: true
    }).pipe(
      tap(() => {
        this.isLoggedInSubject.next(true);
      }),
      catchError(this.handleError)
    );
  }

  // Check if user is logged in
  isAuthenticated(): boolean {
    return this.isLoggedInSubject.value;
  }

  // Get current user
  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  // Check if access token exists in cookies
  private hasAccessToken(): boolean {
    return document.cookie.includes('accessToken');
  }

  // Check if refresh token exists in cookies
  private hasRefreshToken(): boolean {
    return document.cookie.includes('refreshToken');
  }

  // Handle HTTP errors
  private handleError(error: any): Observable<never> {
    let errorMessage = 'An error occurred';

    if (error.error && error.error.message) {
      errorMessage = error.error.message;
    } else if (error.message) {
      errorMessage = error.message;
    }

    return throwError(() => new Error(errorMessage));
  }

  logout(): Observable<any> {
    return this.http.post(`${this.config.apiUrl}/logout`, {}, {
      withCredentials: true
    }).pipe(
      tap(() => {
        this.currentUserSubject.next(null);
        this.isLoggedInSubject.next(false);
        this.router.navigate(['/login']);
      }),
      catchError((error) => {
        // Even if logout fails on backend, clear frontend state
        this.currentUserSubject.next(null);
        this.isLoggedInSubject.next(false);
        this.router.navigate(['/login']);
        return throwError(() => error);
      })
    );
  }

  logoutClientSide(): void {
    this.currentUserSubject.next(null);
    this.isLoggedInSubject.next(false);
    this.router.navigate(['/login']);
  }
}

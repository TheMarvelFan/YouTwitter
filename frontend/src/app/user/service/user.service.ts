import { Inject, Injectable } from '@angular/core';
import { APP_SERVICE_CONFIG } from '../../config/app.config.service';
import { AppConfig } from '../../models/app.config';
import { HttpClient } from '@angular/common/http';
import { User } from '../../models/User';
import { ResponseType } from '../../models/ResponseType';
import { BehaviorSubject, catchError, map, Observable, tap, throwError, of, ReplaySubject } from 'rxjs';
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

  // Use ReplaySubject for isLoggedIn to ensure late subscribers get the correct value
  private isLoggedInSubject = new ReplaySubject<boolean>(1);
  public isLoggedIn$ = this.isLoggedInSubject.asObservable();

  private authInitialized = false;

  constructor(
    @Inject(APP_SERVICE_CONFIG)
    private config: AppConfig,
    private http: HttpClient,
    private router: Router
  ) {
    this.initializeAuthState();
  }

  // Initialize authentication state on app startup
  private initializeAuthState(): void {
    this.checkAuthStatus().subscribe({
      next: (isAuthenticated) => {
        console.log('Auth initialization completed:', isAuthenticated);
        this.authInitialized = true;
      },
      error: (error) => {
        console.error('Auth initialization failed:', error);
        this.authInitialized = true;
      }
    });
  }

  // Check if auth has been initialized
  public isAuthInitialized(): boolean {
    return this.authInitialized;
  }

  // Check authentication status and fetch user data if authenticated
  checkAuthStatus(): Observable<boolean> {
    // First check if we have any tokens
    if (!this.hasAccessToken() && !this.hasRefreshToken()) {
      this.clearAuthState();
      return of(false);
    }

    // Try to get current user profile
    return this.getCurrentUserProfile().pipe(
      map(user => {
        console.log("Fetched user profile: ", user);
        if (user) {
          this.currentUserSubject.next(user);
          this.isLoggedInSubject.next(true);
          return true;
        }
        return false;
      }),
      catchError(() => {
        // If getting profile fails, try refresh token
        if (this.hasRefreshToken()) {
          return this.refreshToken().pipe(
            map(() => {
              this.isLoggedInSubject.next(true);
              return true;
            }),
            catchError(() => {
              this.clearAuthState();
              return of(false);
            })
          );
        }
        this.clearAuthState();
        return of(false);
      })
    );
  }

  // Get current user profile from backend
  getCurrentUserProfile(): Observable<User | null> {
    return this.http.get<ResponseType<User>>(`${this.config.apiUrl}/users/current-user`, {
      withCredentials: true
    }).pipe(
      map(response => response.data),
      catchError(() => {
        return of(null);
      })
    );
  }

  // Login user
  login(loginRequest: LoginRequest): Observable<User> {
    return this.http.post<ResponseType<User>>(`${this.config.apiUrl}/users/login`, loginRequest, {
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
    return this.http.post<ResponseType<User>>(`${this.config.apiUrl}/users/register`, formData, {
      withCredentials: true
    }).pipe(
      map(response => response.data),
      catchError(this.handleError)
    );
  }

  // Refresh token
  refreshToken(): Observable<any> {
    return this.http.post(`${this.config.apiUrl}/users/refresh-token`, {}, {
      withCredentials: true
    }).pipe(
      tap(() => {
        this.isLoggedInSubject.next(true);
        // Optionally fetch user profile after refresh
        this.getCurrentUserProfile().subscribe(user => {
          if (user) {
            this.currentUserSubject.next(user);
          }
        });
      }),
      catchError(this.handleError)
    );
  }

  // Check if user is logged in
  isAuthenticated(): boolean {
    return this.currentUserSubject.value !== null;
  }

  // Get current user
  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  // Improved cookie checking methods
  private hasAccessToken(): boolean {
    return this.getCookieValue('accessToken') !== null;
  }

  private hasRefreshToken(): boolean {
    return this.getCookieValue('refreshToken') !== null;
  }

  // Helper method to get cookie value
  private getCookieValue(cookieName: string): string | null {
    const cookies = document.cookie.split(';');
    for (let cookie of cookies) {
      const trimmedCookie = cookie.trim();
      // Use indexOf to find the first equals sign, then split properly
      const equalsIndex = trimmedCookie.indexOf('=');
      if (equalsIndex > 0) {
        const name = trimmedCookie.substring(0, equalsIndex);
        const value = trimmedCookie.substring(equalsIndex + 1);
        if (name === cookieName) {
          return decodeURIComponent(value);
        }
      }
    }
    return null;
  }

  // Clear authentication state
  private clearAuthState(): void {
    this.currentUserSubject.next(null);
    this.isLoggedInSubject.next(false);
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

  // Logout user
  logout(): Observable<any> {
    return this.http.post(`${this.config.apiUrl}/users/logout`, {}, {
      withCredentials: true
    }).pipe(
      tap(() => {
        this.clearAuthState();
        this.router.navigate(['/user/login']);
      }),
      catchError((error) => {
        // Even if logout fails on backend, clear frontend state
        this.clearAuthState();
        this.router.navigate(['/user/login']);
        return throwError(() => error);
      })
    );
  }

  // Client-side logout
  logoutClientSide(): void {
    this.clearAuthState();
    this.router.navigate(['/user/login']);
  }
}

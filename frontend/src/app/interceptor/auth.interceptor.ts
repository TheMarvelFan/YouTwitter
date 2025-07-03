import {
  HttpErrorResponse,
  HttpEvent,
  HttpHandlerFn,
  HttpInterceptorFn,
  HttpRequest
} from '@angular/common/http';
import {BehaviorSubject, catchError, filter, Observable, switchMap, take, throwError} from 'rxjs';
import { inject } from '@angular/core';
import { UserService } from '../user/service/user.service';
import { Router } from '@angular/router';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authReq = req.clone({
    setHeaders: {
      'Content-Type': req.headers.get('Content-Type') || 'application/json'
    },
    withCredentials: true
  });

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      // Handle 401 Unauthorized errors
      if (error.status === 401 && !req.url.includes('/login') && !req.url.includes('/register')) {
        return handle401Error(req, next);
      }

      return throwError(() => error);
    })
  );
};

function handle401Error (request: HttpRequest<any>, next: HttpHandlerFn): Observable<HttpEvent<any>> {
  let isRefreshing = false;
  const refreshTokenSubject: BehaviorSubject<any> = new BehaviorSubject<any>(null);

  const authService: UserService = inject(UserService);
  const router = inject(Router);

  if (!isRefreshing) {
    isRefreshing = true;
    refreshTokenSubject.next(null);

    return authService.refreshToken().pipe(
      switchMap(() => {
        isRefreshing = false;
        refreshTokenSubject.next(true);

        // Retry the original request
        return next(request.clone({ withCredentials: true }));
      }),
      catchError((error) => {
        isRefreshing = false;

        // Refresh token failed, redirect to log in
        authService.logout().subscribe();
        return throwError(() => error);
      })
    );
  } else {
    // Wait for refresh to complete
    return refreshTokenSubject.pipe(
      filter(token => token != null),
      take(1),
      switchMap(() => next(request.clone({ withCredentials: true })))
    );
  }
}

import { CanActivateFn, Router } from '@angular/router';
import { map, take } from 'rxjs';
import { inject } from '@angular/core';
import { UserService } from '../../user/service/user.service';

export const guestGuard: CanActivateFn = (route, state) => {
  const authService = inject(UserService);
  const router = inject(Router);

  return authService.isLoggedIn$.pipe(
    take(1),
    map(isLoggedIn => {
      if (!isLoggedIn) {
        return true;
      } else {
        return router.createUrlTree(['/videos']);
      }
    })
  );
};

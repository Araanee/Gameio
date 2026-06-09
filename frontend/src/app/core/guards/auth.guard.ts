import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService, Role } from '../services/auth.service';

export function authGuard(requiredRole: Role): CanActivateFn {
  return () => {
    const auth = inject(AuthService);
    const router = inject(Router);

    if (!auth.isLoggedIn()) {
      router.navigate(['/login']);
      return false;
    }

    if (!auth.hasRoleOrHigher(requiredRole)) {
      router.navigate(['/']);
      return false;
    }

    return true;
  };
}

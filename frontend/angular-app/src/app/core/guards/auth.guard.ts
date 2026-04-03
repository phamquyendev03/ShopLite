import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

/**
 * authGuard — Functional Route Guard (Angular 15+).
 *
 * Bảo vệ các route yêu cầu đăng nhập.
 * Nếu người dùng chưa login → chuyển hướng về /login.
 *
 * @example
 * // app.routes.ts
 * { path: 'products', canActivate: [authGuard], ... }
 */
export const authGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isLoggedIn()) {
    return true;
  }

  // Chưa đăng nhập → redirect an toàn qua createUrlTree
  return router.createUrlTree(['/login']);
};

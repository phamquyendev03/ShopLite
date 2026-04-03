import { HttpInterceptorFn, HttpRequest, HttpHandlerFn, HttpEvent } from '@angular/common/http';
import { inject } from '@angular/core';
import { Observable } from 'rxjs';
import { StorageService } from '../services/storage.service';

/** Phải đồng bộ với key trong AuthService */
const ACCESS_TOKEN_KEY = 'access_token';

/**
 * jwtInterceptor — Functional HTTP Interceptor (Angular 15+).
 *
 * Tự động gắn header `Authorization: Bearer <token>` vào mỗi outgoing request
 * nếu người dùng đã có access token trong storage.
 *
 * Đăng ký trong main.ts:
 *   provideHttpClient(withInterceptors([jwtInterceptor]))
 */
export const jwtInterceptor: HttpInterceptorFn = (
  req: HttpRequest<unknown>,
  next: HttpHandlerFn
): Observable<HttpEvent<unknown>> => {
  const storage = inject(StorageService);
  const token = storage.getItem(ACCESS_TOKEN_KEY);

  // Không có token → tiếp tục request gốc
  if (!token) {
    return next(req);
  }

  // HttpRequest là immutable → phải clone() trước khi thêm header
  const authReq = req.clone({
    setHeaders: {
      Authorization: `Bearer ${token}`
    }
  });

  return next(authReq);
};

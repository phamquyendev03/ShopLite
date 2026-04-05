import { HttpInterceptorFn, HttpEvent, HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';

/**
 * apiResponseInterceptor — Tự động unwrap response wrapper từ backend & xử lý lỗi bảo mật.
 *
 * Backend (FormatRestResponse) luôn bọc response thành:
 *   { "statusCode": 200, "message": "...", "data": <actual data> }
 *
 * Interceptor này:
 * 1. Trích xuất phần `data` để service & component nhận được dữ liệu thực tế.
 * 2. Bắt lỗi HTTP (401, 403) như hết hạn JWT, thu hồi vé và bắt người dùng đăng nhập lại.
 */
export const apiResponseInterceptor: HttpInterceptorFn = (
  req,
  next
): Observable<HttpEvent<unknown>> => {
  const router = inject(Router);
  const authService = inject(AuthService);

  return next(req).pipe(
    map((event) => {
      if (event instanceof HttpResponse) {
        const body = event.body as any;
        // Chỉ unwrap nếu body có dạng { statusCode, data }
        if (body && typeof body === 'object' && 'statusCode' in body && 'data' in body) {
          return event.clone({ body: body.data });
        }
      }
      return event;
    }),
    catchError((error: HttpErrorResponse) => {
      // Nếu Auth hết hạn hoặc từ chối truy cập (JWT Expired, v.v)
      if (error.status === 401 || error.status === 403) {
        console.warn('Authentication token expired or unauthorized. Logging out...');
        // Xoá cookie/token cũ
        authService.logout();
        // Redirect người dùng tới trang Đăng nhập
        router.navigate(['/login']);
      }
      // Vẫn throwError để service/component nếu có bắt thêm thì bắt được
      return throwError(() => error);
    })
  );
};

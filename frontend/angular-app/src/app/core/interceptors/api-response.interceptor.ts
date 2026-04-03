import { HttpInterceptorFn, HttpEvent, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

/**
 * apiResponseInterceptor — Tự động unwrap response wrapper từ backend.
 *
 * Backend (FormatRestResponse) luôn bọc response thành:
 *   { "statusCode": 200, "message": "...", "data": <actual data> }
 *
 * Interceptor này trích xuất phần `data` để service & component
 * nhận được dữ liệu thực tế, khớp với kiểu đã khai báo.
 */
export const apiResponseInterceptor: HttpInterceptorFn = (
  req,
  next
): Observable<HttpEvent<unknown>> => {
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
    })
  );
};

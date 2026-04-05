import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { StorageService } from './storage.service';
import { environment } from '../../../environments/environment';
import { LoginResponse, UserInfo } from '../../models/auth.model';

/** Keys lưu trong storage */
const ACCESS_TOKEN_KEY = 'access_token';
const REFRESH_TOKEN_KEY = 'refresh_token';
const USER_INFO_KEY = 'user_info';

/**
 * AuthService — Xử lý toàn bộ luồng xác thực.
 *
 * Lưu ý: apiResponseInterceptor đã unwrap { statusCode, message, data }
 * → res trong .pipe(tap(res => ...)) là LoginResponse trực tiếp.
 */
@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly apiUrl = environment.apiUrl;

  constructor(
    private http: HttpClient,
    private storage: StorageService
  ) { }

  /**
   * Đăng nhập — gửi username + password.
   * Response (sau interceptor unwrap): LoginResponse { accessToken, refreshToken, user }
   */
  login(username: string, password: string): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(`${this.apiUrl}/auth/login`, { username, password })
      .pipe(
        tap((res: any) => {
          // Interceptor đã unwrap → res là LoginResponse trực tiếp
          this.saveToken(res.accessToken, res.refreshToken);
          if (res.user) {
            this.storage.setItem(USER_INFO_KEY, JSON.stringify(res.user));
          }
        })
      );
  }

  /**
   * Đăng ký — gửi username + password.
   * Response (sau interceptor unwrap): LoginResponse { accessToken, refreshToken, user }
   */
  register(username: string, password: string): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(`${this.apiUrl}/auth/register`, { username, password })
      .pipe(
        tap((res: any) => {
          // Interceptor đã unwrap → res là LoginResponse trực tiếp
          this.saveToken(res.accessToken, res.refreshToken);
          if (res.user) {
            this.storage.setItem(USER_INFO_KEY, JSON.stringify(res.user));
          }
        })
      );
  }

  /**
   * Lấy thông tin user hiện tại từ storage.
   */
  getCurrentUser(): UserInfo | null {
    const raw = this.storage.getItem(USER_INFO_KEY);
    if (!raw) return null;
    try {
      return JSON.parse(raw) as UserInfo;
    } catch {
      return null;
    }
  }

  /**
   * Gọi GET /auth/me để lấy thông tin user đang đăng nhập.
   */
  getMe(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/auth/me`);
  }

  /**
   * Đăng xuất — xoá tất cả token & user info khỏi storage.
   */
  logout(): void {
    this.storage.removeItem(ACCESS_TOKEN_KEY);
    this.storage.removeItem(REFRESH_TOKEN_KEY);
    this.storage.removeItem(USER_INFO_KEY);
  }

  /** Lưu access token (và optional refresh token) vào storage. */
  saveToken(accessToken: string, refreshToken?: string): void {
    this.storage.setItem(ACCESS_TOKEN_KEY, accessToken);
    if (refreshToken) {
      this.storage.setItem(REFRESH_TOKEN_KEY, refreshToken);
    }
  }

  /** Lấy access token hiện tại */
  getToken(): string | null {
    return this.storage.getItem(ACCESS_TOKEN_KEY);
  }

  /** Lấy refresh token hiện tại */
  getRefreshToken(): string | null {
    return this.storage.getItem(REFRESH_TOKEN_KEY);
  }

  /** Kiểm tra trạng thái đăng nhập */
  isLoggedIn(): boolean {
    return !!this.getToken();
  }
}

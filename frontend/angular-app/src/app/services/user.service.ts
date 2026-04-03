import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface User {
  id: number;
  username: string;
  email: string;
  roleName: string;
  createdAt?: string;
}

export interface CreateUserRequest {
  username: string;
  email: string;
  password: string;
  roleId?: number;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private readonly apiUrl = `${environment.apiUrl}/users`;

  constructor(private http: HttpClient) { }

  /** GET /api/v1/users — Lấy danh sách tất cả người dùng */
  findAll(): Observable<User[]> {
    return this.http.get<User[]>(this.apiUrl);
  }

  /** GET /api/v1/users/:id — Lấy thông tin một user */
  findById(id: number): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/${id}`);
  }

  /** POST /api/v1/users — Tạo user mới */
  create(req: CreateUserRequest): Observable<User> {
    return this.http.post<User>(this.apiUrl, req);
  }

  /** PUT /api/v1/users/:id — Cập nhật user */
  update(id: number, req: Partial<CreateUserRequest>): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/${id}`, req);
  }

  /** DELETE /api/v1/users/:id — Xoá user */
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}

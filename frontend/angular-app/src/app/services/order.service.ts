import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Order, CreateOrderRequest } from '../models/order.model';
import { StatusEnum } from '../models/enums.model';

@Injectable({
  providedIn: 'root'
})
export class OrderService {

  private readonly apiUrl = `${environment.apiUrl}/orders`;

  constructor(private http: HttpClient) {}

  create(req: CreateOrderRequest): Observable<Order> {
    return this.http.post<Order>(this.apiUrl, req);
  }

  findById(id: number): Observable<Order> {
    return this.http.get<Order>(`${this.apiUrl}/${id}`);
  }

  findAll(): Observable<Order[]> {
    return this.http.get<Order[]>(this.apiUrl);
  }

  updateStatus(id: number, status: StatusEnum): Observable<Order> {
    let params = new HttpParams().set('status', status);
    return this.http.patch<Order>(`${this.apiUrl}/${id}/status`, null, { params });
  }

  getUserOrders(): Observable<Order[]> {
    // Backend chưa có /my-orders → dùng /orders (danh sách chung)
    return this.http.get<Order[]>(this.apiUrl);
  }
}

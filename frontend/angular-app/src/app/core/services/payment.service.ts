import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PaymentService {
  private readonly apiUrl = `${environment.apiUrl}/payment`;

  constructor(private http: HttpClient) {}

  createPaymentSession(orderId: number): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/create`, { orderId });
  }
}

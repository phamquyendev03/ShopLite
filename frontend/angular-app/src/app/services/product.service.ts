import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Product, ProductPage, CreateProductRequest, UpdateProductRequest } from '../models/product.model';

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  private readonly apiUrl = `${environment.apiUrl}/products`;

  constructor(private http: HttpClient) {}

  create(req: CreateProductRequest): Observable<Product> {
    return this.http.post<Product>(this.apiUrl, req);
  }

  findById(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.apiUrl}/${id}`);
  }

  getProducts(params?: {
    keyword?: string;
    categoryId?: number;
    minPrice?: number;
    maxPrice?: number;
    page?: number;     // default 0
    size?: number;     // default 10
    sortBy?: string;   // default createdAt
    sortDir?: string;  // default desc
  }): Observable<ProductPage> {
    let httpParams = new HttpParams();
    if (params) {
      if (params.keyword != null) httpParams = httpParams.set('keyword', params.keyword);
      if (params.categoryId != null) httpParams = httpParams.set('categoryId', params.categoryId.toString());
      if (params.minPrice != null) httpParams = httpParams.set('minPrice', params.minPrice.toString());
      if (params.maxPrice != null) httpParams = httpParams.set('maxPrice', params.maxPrice.toString());
      if (params.page != null) httpParams = httpParams.set('page', params.page.toString());
      if (params.size != null) httpParams = httpParams.set('size', params.size.toString());
      if (params.sortBy) httpParams = httpParams.set('sortBy', params.sortBy);
      if (params.sortDir) httpParams = httpParams.set('sortDir', params.sortDir);
    }
    
    return this.http.get<ProductPage>(this.apiUrl, { params: httpParams });
  }

  update(id: number, req: UpdateProductRequest): Observable<Product> {
    return this.http.put<Product>(`${this.apiUrl}/${id}`, req);
  }

  softDelete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}

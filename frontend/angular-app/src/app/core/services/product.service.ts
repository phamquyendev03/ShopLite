import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Product, ProductPage } from '../../models/product.model';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private readonly apiUrl = `${environment.apiUrl}/products`;

  constructor(private http: HttpClient) {}

  /**
   * Lấy danh sách sản phẩm có phân trang và filter
   */
  getProducts(
    page: number = 0,
    size: number = 10,
    keyword?: string,
    categoryId?: number,
    minPrice?: number,
    maxPrice?: number,
    sortBy: string = 'createdAt',
    sortDir: string = 'desc'
  ): Observable<ProductPage> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy)
      .set('sortDir', sortDir);

    if (keyword) params = params.set('keyword', keyword);
    if (categoryId) params = params.set('categoryId', categoryId.toString());
    if (minPrice) params = params.set('minPrice', minPrice.toString());
    if (maxPrice) params = params.set('maxPrice', maxPrice.toString());

    return this.http.get<ProductPage>(this.apiUrl, { params });
  }

  /**
   * Lấy chi tiết 1 sản phẩm theo ID
   */
  findById(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.apiUrl}/${id}`);
  }

  /**
   * Tạo mới sản phẩm
   */
  create(product: any): Observable<Product> {
    return this.http.post<Product>(this.apiUrl, product);
  }

  /**
   * Cập nhật sản phẩm
   */
  update(id: number, product: any): Observable<Product> {
    return this.http.put<Product>(`${this.apiUrl}/${id}`, product);
  }

  /**
   * Xóa sản phẩm
   */
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}

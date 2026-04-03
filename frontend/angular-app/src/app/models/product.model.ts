/**
 * product.model.ts — Mapping ResProductDTO, ResProductPageDTO, ReqProductDTO
 */

export interface Product {
  id: number;
  categoryId: number;
  categoryName: string;
  name: string;
  sku: string;
  stock: number;
  price: number;
  isDeleted: boolean;
  createdAt: string; // LocalDate is serialized as YYYY-MM-DD
  description?: string; // Optional property for UI
}

export interface ProductPage {
  totalElements: number;
  totalPages: number;
  page: number;
  size: number;
  data: Product[];
}

export interface CreateProductRequest {
  name: string;
  categoryId: number;
  price: number;
}

export interface UpdateProductRequest {
  name: string;
  categoryId: number;
  price: number;
}

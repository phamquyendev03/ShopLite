/**
 * category.model.ts — Mapping ResCategoryDTO và ReqCategoryDTO
 */

export interface Category {
  id: number;
  name: string;
}

export interface CategoryRequest {
  name: string;
}

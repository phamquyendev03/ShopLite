import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import {
  IonContent, IonHeader, IonTitle, IonToolbar,
  IonSpinner, IonButtons, IonButton,
  IonSearchbar, IonChip, IonGrid, IonRow, IonCol,
  IonCard, IonCardContent, IonIcon, IonBadge, IonLabel, IonText
} from '@ionic/angular/standalone';
import { ProductService } from '../../services/product.service';
import { CategoryService } from '../../services/category.service';
import { Product, ProductPage } from '../../models/product.model';
import { Category } from '../../models/category.model';

@Component({
  selector: 'app-product-list',
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.scss'],
  standalone: true,
  imports: [
    CommonModule, RouterModule, FormsModule,
    IonContent, IonHeader, IonTitle, IonToolbar,
    IonSpinner, IonButtons, IonButton,
    IonSearchbar, IonChip, IonGrid, IonRow, IonCol,
    IonCard, IonCardContent, IonIcon, IonBadge, IonLabel, IonText
  ]
})
export class ProductListComponent implements OnInit {
  products: Product[] = [];
  categories: Category[] = [];
  selectedCategoryId: number | null = null;
  keyword = '';
  loading = false;
  errorMsg = '';

  // Pagination
  currentPage = 0;
  totalPages = 0;
  totalElements = 0;

  constructor(
    private productService: ProductService,
    private categoryService: CategoryService
  ) { }

  ngOnInit() {
    this.loadCategories();
    this.loadProducts();
  }

  loadCategories() {
    this.categoryService.findAll().subscribe({
      next: (res: any) => {
        // Interceptor đã unwrap → res là Category[] trực tiếp
        this.categories = Array.isArray(res) ? res : [];
      },
      error: () => { this.categories = []; }
    });
  }

  loadProducts(page = 0) {
    this.loading = true;
    this.errorMsg = '';
    this.productService.getProducts({
      page,
      size: 20,
      keyword: this.keyword || undefined,
      categoryId: this.selectedCategoryId ?? undefined
    }).subscribe({
      next: (res: any) => {
        // Interceptor đã unwrap → res là ProductPage: { totalElements, totalPages, page, size, data }
        const page: ProductPage = res;
        this.products = page.data ?? [];
        this.totalPages = page.totalPages ?? 0;
        this.totalElements = page.totalElements ?? 0;
        this.currentPage = page.page ?? 0;
        this.loading = false;
      },
      error: (err) => {
        this.errorMsg = err?.error?.message || 'Không thể tải sản phẩm. Vui lòng thử lại.';
        this.loading = false;
      }
    });
  }

  selectCategory(id: number | null) {
    this.selectedCategoryId = id;
    this.loadProducts(0);
  }

  onSearch(event: any) {
    this.keyword = event.detail.value ?? '';
    this.loadProducts(0);
  }

  prevPage() {
    if (this.currentPage > 0) this.loadProducts(this.currentPage - 1);
  }

  nextPage() {
    if (this.currentPage < this.totalPages - 1) this.loadProducts(this.currentPage + 1);
  }
}

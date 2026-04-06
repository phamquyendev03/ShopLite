import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import {
  IonContent, IonHeader, IonTitle, IonToolbar,
  IonSpinner, IonButtons, IonButton, IonBackButton,
  IonSearchbar, IonRow, IonCol,
  IonIcon, IonLabel,
  IonModal, IonItem, IonInput, IonSelect, IonSelectOption,
  IonFab, IonFabButton, IonRefresher, IonRefresherContent,
  AlertController, ToastController
} from '@ionic/angular/standalone';
import { ProductService } from '../../core/services/product.service';
import { CategoryService } from '../../core/services/category.service';
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
    IonSpinner, IonButtons, IonButton, IonBackButton,
    IonSearchbar, IonRow, IonCol,
    IonIcon, IonLabel,
    IonModal, IonItem, IonInput, IonSelect, IonSelectOption,
    IonFab, IonFabButton, IonRefresher, IonRefresherContent
  ]
})
export class ProductListComponent implements OnInit {
  // List State
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

  // Product Form Modal State
  isModalOpen = false;
  isEditing = false;
  productForm: any = {
    id: null,
    name: '',
    sku: '',
    price: 0,
    stock: 0,
    categoryId: null
  };

  constructor(
    private productService: ProductService,
    private categoryService: CategoryService,
    private alertController: AlertController,
    private toastController: ToastController
  ) { }

  ngOnInit() {
    this.loadCategories();
    this.loadProducts();
  }

  // --- Modal Actions ---
  openAddModal() {
    this.isEditing = false;
    this.productForm = {
      id: null,
      name: '',
      sku: '',
      price: 0,
      stock: 0,
      categoryId: this.selectedCategoryId || (this.categories.length > 0 ? this.categories[0].id : null)
    };
    this.isModalOpen = true;
  }

  openEditModal(product: Product, event: Event) {
    event.stopPropagation();
    this.isEditing = true;
    this.productForm = { ...product };
    this.isModalOpen = true;
  }

  closeModal() {
    this.isModalOpen = false;
  }

  // --- CRUD Operations ---
  async saveProduct() {
    if (!this.productForm.name || !this.productForm.sku || !this.productForm.categoryId) {
      this.showToast('Vui lòng điền đủ thông tin bắt buộc', 'warning');
      return;
    }

    const action = this.isEditing ?
      this.productService.update(this.productForm.id, this.productForm) :
      this.productService.create(this.productForm);

    action.subscribe({
      next: () => {
        this.showToast(this.isEditing ? 'Cập nhật thành công' : 'Thêm mới thành công', 'success');
        this.closeModal();
        this.loadProducts(this.currentPage);
      },
      error: (err) => {
        this.showToast('Lỗi: ' + (err.error?.message || 'Không thể lưu sản phẩm'), 'danger');
      }
    });
  }

  async deleteProduct(product: Product, event: Event) {
    event.stopPropagation();
    const alert = await this.alertController.create({
      header: 'Cảnh báo',
      message: `Bạn có chắc chắn muốn xóa sản phẩm "${product.name}"?`,
      buttons: [
        { text: 'Hủy', role: 'cancel' },
        {
          text: 'Xóa',
          cssClass: 'alert-button-delete',
          handler: () => {
            this.productService.delete(product.id).subscribe({
              next: () => {
                this.showToast('Xóa sản phẩm thành công', 'success');
                this.loadProducts(this.currentPage);
              },
              error: () => this.showToast('Lỗi khi xóa sản phẩm', 'danger')
            });
          }
        }
      ]
    });
    await alert.present();
  }

  async showToast(message: string, color: string = 'primary') {
    const toast = await this.toastController.create({
      message,
      duration: 2000,
      color,
      position: 'top'
    });
    await toast.present();
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

  loadProducts(page = 0, event?: any) {
    this.loading = true;
    this.errorMsg = '';

    const kw = this.keyword ? this.keyword : undefined;
    const catId = this.selectedCategoryId ? this.selectedCategoryId : undefined;

    this.productService.getProducts(page, 20, kw, catId).subscribe({
      next: (res: any) => {
        // Interceptor đã unwrap → res là ProductPage: { totalElements, totalPages, page, size, data }
        const pageResponse: ProductPage = res;
        this.products = pageResponse.data ?? [];
        this.totalPages = pageResponse.totalPages ?? 0;
        this.totalElements = pageResponse.totalElements ?? 0;
        this.currentPage = pageResponse.page ?? 0;
        this.loading = false;
        if (event) event.target.complete();
      },
      error: (err) => {
        this.errorMsg = err?.error?.message || 'Không thể tải sản phẩm. Vui lòng thử lại.';
        this.loading = false;
        if (event) event.target.complete();
      }
    });
  }

  handleRefresh(event: any) {
    this.loadCategories();
    this.loadProducts(0, event);
  }

  selectCategory(id: number | null) {
    this.selectedCategoryId = id;
    this.loadProducts(0);
  }

  onSearch(event: any) {
    this.keyword = event.detail.value ?? '';
    this.loadProducts(0);
  }

  get totalStock(): number {
    return this.products.reduce((sum, product) => sum + (product.stock ?? 0), 0);
  }

  get totalInventoryValue(): number {
    return this.products.reduce((sum, product) => sum + ((product.price ?? 0) * (product.stock ?? 0)), 0);
  }

  prevPage() {
    if (this.currentPage > 0) this.loadProducts(this.currentPage - 1);
  }

  nextPage() {
    if (this.currentPage < this.totalPages - 1) this.loadProducts(this.currentPage + 1);
  }
}

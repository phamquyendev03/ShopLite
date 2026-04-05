import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { 
  IonContent, IonHeader, IonToolbar, IonTitle, IonButtons, IonButton, 
  IonSearchbar, IonSpinner, ToastController 
} from '@ionic/angular/standalone';
import { addIcons } from 'ionicons';
import { 
  searchOutline, addOutline, removeOutline, trashOutline, 
  chevronUpOutline, chevronDownOutline, checkmarkCircleOutline 
} from 'ionicons/icons';
import { Subscription, Subject, debounceTime } from 'rxjs';
import { ProductService } from '../../core/services/product.service';
import { CartService, CartItem } from '../../core/services/cart.service';
import { OrderService } from '../../core/services/order.service';
import { AuthService } from '../../core/services/auth.service';
import { Product } from '../../models/product.model';
import { CreateOrderRequest } from '../../models/order.model';
import { PaymentMethodEnum } from '../../models/enums.model';

type PaymentType = 'CASH' | 'QR';

@Component({
  selector: 'app-pos',
  templateUrl: './pos.component.html',
  styleUrls: ['./pos.component.scss'],
  standalone: true,
  imports: [
    CommonModule, FormsModule, 
    IonContent, IonHeader, IonToolbar, IonTitle, IonButtons, IonButton, 
    IonSearchbar, IonSpinner
  ]
})
export class PosComponent implements OnInit, OnDestroy {
  products: Product[] = [];
  cartItems: CartItem[] = [];
  keyword = '';
  loading = false;
  cartExpanded = false;
  checkingOut = false;

  // Checkout Modal State
  showCheckoutModal = false;
  selectedPayment: PaymentType = 'CASH';
  qrCodeUrl = '';

  private cartSub!: Subscription;
  private searchSubject = new Subject<string>();
  private searchSub!: Subscription;

  constructor(
    private productService: ProductService,
    private cartService: CartService,
    private orderService: OrderService,
    private authService: AuthService,
    private router: Router,
    private toastCtrl: ToastController
  ) {
    addIcons({ 
      searchOutline, addOutline, removeOutline, trashOutline, 
      chevronUpOutline, chevronDownOutline, checkmarkCircleOutline 
    });
  }

  ngOnInit() {
    this.cartSub = this.cartService.cartItems$.subscribe(items => {
      this.cartItems = items;
    });

    this.searchSub = this.searchSubject
      .pipe(debounceTime(300))
      .subscribe(kw => this.loadProducts(kw));

    this.loadProducts();
  }

  ngOnDestroy() {
    this.cartSub?.unsubscribe();
    this.searchSub?.unsubscribe();
  }

  loadProducts(keyword?: string) {
    this.loading = true;
    this.productService.getProducts(0, 30, keyword || undefined).subscribe({
      next: (res: any) => {
        this.products = (res.data ?? res) as Product[];
        this.loading = false;
      },
      error: () => { this.loading = false; }
    });
  }

  onSearch() {
    this.searchSubject.next(this.keyword);
  }

  addToCart(product: Product) {
    this.cartService.addToCart(product, 1);
  }

  isInCart(productId: number): boolean {
    return this.cartItems.some(i => i.product.id === productId);
  }

  getCartQty(productId: number): number {
    const item = this.cartItems.find(i => i.product.id === productId);
    return item ? item.quantity : 0;
  }

  get totalCartCount(): number {
    return this.cartItems.reduce((acc, i) => acc + i.quantity, 0);
  }

  get total(): number {
    return this.cartService.getTotalPrice();
  }

  toggleCart() {
    this.cartExpanded = !this.cartExpanded;
  }

  increaseQty(item: CartItem) {
    this.cartService.updateQuantity(item.product.id, item.quantity + 1);
  }

  decreaseQty(item: CartItem) {
    if (item.quantity > 1) {
      this.cartService.updateQuantity(item.product.id, item.quantity - 1);
    } else {
      this.cartService.removeFromCart(item.product.id);
    }
  }

  removeItem(item: CartItem) {
    this.cartService.removeFromCart(item.product.id);
  }

  // ─── Checkout Modal ───
  openCheckoutModal() {
    this.selectedPayment = 'CASH';
    this.qrCodeUrl = '';
    this.showCheckoutModal = true;
  }

  closeCheckoutModal() {
    if (this.checkingOut) return;
    this.showCheckoutModal = false;
  }

  selectPayment(method: PaymentType) {
    this.selectedPayment = method;

    if (method === 'QR') {
      // Build QR data: bank transfer info + amount
      const qrData = `ShopLite|VCB|1234567890|${this.total}|Thanh toan don hang`;
      this.qrCodeUrl = `https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=${encodeURIComponent(qrData)}`;
    }
  }

  async confirmCheckout() {
    const user = this.authService.getCurrentUser();
    if (!user) {
      this.showToast('Vui lòng đăng nhập lại', 'warning');
      this.router.navigate(['/login']);
      return;
    }
    if (this.cartItems.length === 0) {
      this.showToast('Giỏ hàng đang trống', 'warning');
      return;
    }

    const paymentMethod = this.selectedPayment === 'QR'
      ? PaymentMethodEnum.BANK
      : PaymentMethodEnum.CASH;

    const orderReq: CreateOrderRequest = {
      userId: user.id,
      customerName: user.username,
      discount: 0,
      paymentMethod,
      items: this.cartItems.map(i => ({
        productId: i.product.id,
        quantity: i.quantity,
        price: i.product.price
      }))
    };

    this.checkingOut = true;
    this.orderService.create(orderReq).subscribe({
      next: () => {
        this.cartService.clearCart();
        this.showCheckoutModal = false;
        this.cartExpanded = false;
        this.checkingOut = false;
        this.showToast('Đặt hàng thành công! 🎉', 'success');
      },
      error: (err) => {
        this.checkingOut = false;
        this.showToast('Đặt hàng thất bại. Vui lòng thử lại.', 'danger');
        console.error(err);
      }
    });
  }

  private async showToast(message: string, color: string) {
    const toast = await this.toastCtrl.create({
      message,
      duration: 2500,
      color,
      position: 'top'
    });
    await toast.present();
  }
}

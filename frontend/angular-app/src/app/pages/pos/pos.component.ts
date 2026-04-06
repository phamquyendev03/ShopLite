import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { 
  IonContent, IonHeader, IonToolbar, IonTitle, IonButtons, IonButton, 
  IonSearchbar, IonSpinner, ToastController, IonRefresher, IonRefresherContent
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
import { Order, CreateOrderRequest } from '../../models/order.model';
import { PaymentMethodEnum } from '../../models/enums.model';
import { PaymentService } from '../../core/services/payment.service';

type PaymentType = 'CASH' | 'QR';

@Component({
  selector: 'app-pos',
  templateUrl: './pos.component.html',
  styleUrls: ['./pos.component.scss'],
  standalone: true,
  imports: [
    CommonModule, FormsModule, 
    IonContent, IonHeader, IonToolbar, IonTitle, IonButtons, IonButton, 
    IonSearchbar, IonSpinner, IonRefresher, IonRefresherContent
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
  qrStepActive = false; // Add state to show QR instead of generic form
  pollingInterval: any; // Add polling reference
  currentOrderId: number | null = null; // Track order being polled

  private cartSub!: Subscription;
  private searchSubject = new Subject<string>();
  private searchSub!: Subscription;

  constructor(
    private productService: ProductService,
    private cartService: CartService,
    private orderService: OrderService,
    private authService: AuthService,
    private paymentService: PaymentService,
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
    if (this.pollingInterval) {
      clearInterval(this.pollingInterval);
    }
  }

  loadProducts(keyword?: string, event?: any) {
    this.loading = true;
    this.productService.getProducts(0, 30, keyword || undefined).subscribe({
      next: (res: any) => {
        this.products = (res.data ?? res) as Product[];
        this.loading = false;
        if (event) event.target.complete();
      },
      error: () => { 
        this.loading = false; 
        if (event) event.target.complete();
      }
    });
  }

  handleRefresh(event: any) {
    this.loadProducts(this.keyword, event);
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
    this.qrStepActive = false;
    this.checkingOut = false;
    if (this.pollingInterval) clearInterval(this.pollingInterval);
    this.showCheckoutModal = true;
  }

  closeCheckoutModal() {
    if (this.checkingOut && !this.qrStepActive) return; // Prevent closing while API call is running
    this.showCheckoutModal = false;
    this.qrStepActive = false;
    if (this.pollingInterval) clearInterval(this.pollingInterval);
  }

  selectPayment(method: PaymentType) {
    this.selectedPayment = method;
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
      next: (res: any) => {
        const orderData = res.data ?? res;
        const orderId = orderData.id;
        
        if (paymentMethod === PaymentMethodEnum.BANK) {
          // Tạo payment session
          this.paymentService.createPaymentSession(orderId).subscribe({
            next: (sessionData: any) => {
              const session = sessionData.data ?? sessionData;
              // Dùng VietQR để show mã trực tiếp cho SePay, vì link của SePay là hosted link HTML
              // Có thể dùng qr.sepay.vn hoặc vietqr.io
              this.qrCodeUrl = `https://img.vietqr.io/image/VCB-1234567890-compact.jpg?amount=${this.total}&addInfo=${session.order_code}&accountName=ShopLite`;
              
              this.qrStepActive = true;
              this.checkingOut = false;
              this.currentOrderId = orderId;
              this.startPollingStatus();
            },
            error: (err) => {
              this.checkingOut = false;
              this.showToast('Lỗi khi tạo mã QR qua SePay', 'danger');
              console.error(err);
            }
          });
        } else {
          // Tiền mặt -> Thành công luôn
          this.cartService.clearCart();
          this.showCheckoutModal = false;
          this.cartExpanded = false;
          this.checkingOut = false;
          this.showToast('Đặt hàng thành công! 🎉', 'success');
        }
      },
      error: (err) => {
        this.checkingOut = false;
        this.showToast('Đặt hàng thất bại. Vui lòng thử lại.', 'danger');
        console.error(err);
      }
    });
  }

  startPollingStatus() {
    if (!this.currentOrderId) return;
    this.pollingInterval = setInterval(() => {
      this.orderService.findById(this.currentOrderId!).subscribe((res: any) => {
        const order = res.data ?? res;
        if (order.status === 'PAID') {
          // Thanh toán thành công (Webhook đã cập nhật trạng thái!)
          clearInterval(this.pollingInterval);
          this.showToast('Thanh toán thành công! Đã gửi thông báo. 🎉', 'success');
          // Notification logic would go here
          this.cartService.clearCart();
          this.showCheckoutModal = false;
          this.qrStepActive = false;
          this.cartExpanded = false;
        }
      });
    }, 3000); // Check mỗi 3 giây
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

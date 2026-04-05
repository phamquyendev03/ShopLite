import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import {
  IonContent, IonHeader, IonTitle, IonToolbar, IonButtons, IonBackButton,
  IonList, IonItem, IonThumbnail, IonLabel, IonIcon, IonButton, IonFooter,
  ToastController
} from '@ionic/angular/standalone';
import { CartService, CartItem } from '../../core/services/cart.service';
import { OrderService } from '../../core/services/order.service';
import { AuthService } from '../../core/services/auth.service';
import { CreateOrderRequest } from '../../models/order.model';
import { PaymentMethodEnum } from '../../models/enums.model';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.scss'],
  standalone: true,
  imports: [
    CommonModule, RouterModule,
    IonContent, IonHeader, IonTitle, IonToolbar, IonButtons, IonBackButton,
    IonList, IonItem, IonThumbnail, IonLabel, IonIcon, IonButton, IonFooter
  ]
})
export class CartComponent implements OnInit, OnDestroy {
  cartItems: CartItem[] = [];
  private cartSub!: Subscription;

  constructor(
    private cartService: CartService,
    private orderService: OrderService,
    private authService: AuthService,
    private router: Router,
    private toastCtrl: ToastController
  ) { }

  ngOnInit() {
    this.cartSub = this.cartService.cartItems$.subscribe(items => {
      this.cartItems = items;
    });
  }

  ngOnDestroy() {
    if (this.cartSub) this.cartSub.unsubscribe();
  }

  get total(): number {
    return this.cartService.getTotalPrice();
  }

  increaseQuantity(item: CartItem) {
    this.cartService.updateQuantity(item.product.id, item.quantity + 1);
  }

  decreaseQuantity(item: CartItem) {
    if (item.quantity > 1) {
      this.cartService.updateQuantity(item.product.id, item.quantity - 1);
    }
  }

  removeItem(item: CartItem) {
    this.cartService.removeFromCart(item.product.id);
  }

  async checkout() {
    const user = this.authService.getCurrentUser();
    if (!user) {
      this.showToast('Please login to checkout', 'warning');
      this.router.navigate(['/login']);
      return;
    }

    if (this.cartItems.length === 0) {
      this.showToast('Your cart is empty', 'warning');
      return;
    }

    const orderReq: CreateOrderRequest = {
      userId: user.id,
      customerName: user.username,
      discount: 0,
      paymentMethod: PaymentMethodEnum.CASH,
      items: this.cartItems.map(i => ({
        productId: i.product.id,
        quantity: i.quantity,
        price: i.product.price
      }))
    };

    this.orderService.create(orderReq).subscribe({
      next: (res) => {
        this.cartService.clearCart();
        this.showToast('Order placed successfully!', 'success');
        this.router.navigate(['/products']);
      },
      error: (err) => {
        this.showToast('Failed to place order. Please try again.', 'danger');
        console.error(err);
      }
    });
  }

  private async showToast(message: string, color: string) {
    const toast = await this.toastCtrl.create({
      message,
      duration: 2000,
      color,
      position: 'top'
    });
    await toast.present();
  }
}

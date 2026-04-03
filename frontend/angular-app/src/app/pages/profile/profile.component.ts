import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { 
  IonContent, IonHeader, IonToolbar, IonTitle,
  IonList, IonItem, IonLabel, IonIcon, IonAvatar,
  IonCard, IonCardContent, IonBadge, IonButtons, IonBackButton
} from '@ionic/angular/standalone';
import { OrderService } from '../../services/order.service';
import { AuthService } from '../../core';
import { Order } from '../../models/order.model';
import { StatusEnum } from '../../models/enums.model';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss'],
  standalone: true,
  imports: [
    CommonModule, 
    IonContent, IonHeader, IonToolbar, IonTitle,
    IonList, IonItem, IonLabel, IonIcon, IonAvatar,
    IonCard, IonCardContent, IonBadge, IonButtons, IonBackButton
  ]
})
export class ProfileComponent implements OnInit {
  orders: Order[] = [];
  user: any = null;

  constructor(
    private orderService: OrderService,
    private authService: AuthService,
    private router: Router
  ) { }

  ngOnInit() {
    this.user = { name: 'Người dùng ShopLite' }; // Mock username for now
    this.loadOrders();
  }

  loadOrders() {
    this.orderService.getUserOrders().subscribe({
      next: (res: any) => {
        // Interceptor đã unwrap → res là Order[] trực tiếp
        this.orders = Array.isArray(res) ? res : [];
      },
      error: (err: any) => {
        console.error('Lỗi tải đơn hàng:', err);
        this.orders = [];
      }
    });
  }

  getStatusColor(status: StatusEnum): string {
    switch (status) {
      case StatusEnum.PENDING: return 'medium';
      case StatusEnum.CONFIRMED: return 'secondary';
      case StatusEnum.SHIPPING: return 'primary';
      case StatusEnum.COMPLETED: return 'success';
      case StatusEnum.CANCELLED: return 'danger';
      default: return 'medium';
    }
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login'], { replaceUrl: true });
  }
}

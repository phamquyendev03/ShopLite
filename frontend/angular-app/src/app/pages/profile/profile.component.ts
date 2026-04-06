import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import {
  IonContent, IonList, IonItem, IonLabel, IonIcon,
  IonCard, IonCardContent, IonBadge, IonHeader, IonToolbar, IonTitle,
  IonRefresher, IonRefresherContent
} from '@ionic/angular/standalone';
import { addIcons } from 'ionicons';
import { personOutline, storefrontOutline, logOutOutline } from 'ionicons/icons';
import { OrderService } from '../../core/services/order.service';
import { AuthService } from '../../core/services/auth.service';
import { Order } from '../../models/order.model';
import { StatusEnum } from '../../models/enums.model';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss'],
  standalone: true,
  imports: [
    CommonModule, RouterModule,
    IonContent, IonList, IonItem, IonLabel, IonIcon,
    IonCard, IonCardContent, IonBadge, IonHeader, IonToolbar, IonTitle,
    IonRefresher, IonRefresherContent
  ]
})
export class ProfileComponent implements OnInit {
  orders: Order[] = [];
  user: any = null;

  constructor(
    private orderService: OrderService,
    private authService: AuthService,
    private router: Router
  ) {
    addIcons({ personOutline, storefrontOutline, logOutOutline });
  }

  ngOnInit() {
    this.user = this.authService.getCurrentUser() || { name: 'Người dùng ShopLite' };
    this.loadOrders();
  }

  loadOrders(event?: any) {
    this.orderService.findAll().subscribe({
      next: (res: any) => {
        // Interceptor đã unwrap → res là Order[] trực tiếp
        this.orders = Array.isArray(res) ? res : [];
        if (event) event.target.complete();
      },
      error: (err: any) => {
        console.error('Lỗi tải đơn hàng:', err);
        this.orders = [];
        if (event) event.target.complete();
      }
    });
  }

  handleRefresh(event: any) {
    this.loadOrders(event);
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

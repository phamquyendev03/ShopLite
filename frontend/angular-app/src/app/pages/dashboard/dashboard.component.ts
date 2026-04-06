import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import {
  IonContent, IonHeader, IonToolbar,
  IonSearchbar, IonSpinner, IonRefresher, IonRefresherContent
} from '@ionic/angular/standalone';
import { addIcons } from 'ionicons';
import { notificationsOutline, searchOutline, arrowForwardOutline, barChartOutline, statsChartOutline, cartOutline, cubeOutline, peopleOutline, appsOutline } from 'ionicons/icons';
import { OrderService } from '../../core/services/order.service';
import { AuthService } from '../../core/services/auth.service';
import { Order } from '../../models/order.model';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
  standalone: true,
  imports: [
    CommonModule, RouterModule,
    IonContent, IonHeader, IonToolbar, IonSearchbar, IonSpinner,
    IonRefresher, IonRefresherContent
  ]
})
export class DashboardComponent implements OnInit {
  userName = 'Khách';
  totalRevenue = 0;
  totalOrders = 0;
  totalProfit = 0;
  loading = false;

  constructor(
    private orderService: OrderService,
    private authService: AuthService
  ) {
    addIcons({
      notificationsOutline, searchOutline, arrowForwardOutline,
      barChartOutline, statsChartOutline, cartOutline,
      cubeOutline, peopleOutline, appsOutline
    });
  }

  ngOnInit() {
    const user = this.authService.getCurrentUser();
    if (user) {
      this.userName = user.username || 'Khách';
    }
    this.loadStats();
  }

  loadStats(event?: any) {
    this.loading = true;
    this.orderService.findAll().subscribe({
      next: (res: any) => {
        const orders: Order[] = (res.data ?? res) as Order[];

        // Tính tổng từ tất cả đơn hàng (không lọc hôm nay nữa để luôn có số liệu)
        this.totalOrders = orders.length;
        this.totalRevenue = orders.reduce((sum, o) => sum + (o.totalAmount || 0), 0);
        // Giả sử lợi nhuận 20%
        this.totalProfit = this.totalRevenue * 0.2;

        this.loading = false;
        if (event) {
          event.target.complete();
        }
      },
      error: () => {
        this.loading = false;
        if (event) {
          event.target.complete();
        }
      }
    });
  }

  handleRefresh(event: any) {
    this.loadStats(event);
  }
}

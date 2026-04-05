import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { 
  IonContent, IonHeader, IonToolbar, IonTitle, IonSpinner 
} from '@ionic/angular/standalone';
import { OrderService } from '../../core/services/order.service';
import { Order } from '../../models/order.model';
import { StatusEnum } from '../../models/enums.model';

type OrderTab = 'ALL' | 'PROCESSING' | 'COMPLETED' | 'CANCELLED';

@Component({
  selector: 'app-orders',
  templateUrl: './orders.component.html',
  styleUrls: ['./orders.component.scss'],
  standalone: true,
  imports: [
    CommonModule, 
    IonContent, IonHeader, IonToolbar, IonTitle, IonSpinner
  ]
})
export class OrdersComponent implements OnInit {
  activeTab: OrderTab = 'ALL';
  orders: Order[] = [];
  loading = false;

  constructor(private orderService: OrderService) {}

  ngOnInit() {
    this.loadOrders();
  }

  loadOrders() {
    this.loading = true;
    this.orderService.findAll().subscribe({
      next: (res: any) => {
        // Handle if response is wrapped
        this.orders = (res.data ?? res) as Order[];
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  get filteredOrders(): Order[] {
    if (this.activeTab === 'ALL') return this.orders;
    
    return this.orders.filter(order => {
      switch (this.activeTab) {
        case 'PROCESSING':
          return [StatusEnum.PENDING, StatusEnum.CONFIRMED, StatusEnum.SHIPPING].includes(order.status);
        case 'COMPLETED':
          return order.status === StatusEnum.COMPLETED;
        case 'CANCELLED':
          return order.status === StatusEnum.CANCELLED;
        default:
          return true;
      }
    });
  }

  setTab(tab: OrderTab) {
    this.activeTab = tab;
  }
}

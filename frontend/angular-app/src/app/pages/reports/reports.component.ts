import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration, ChartOptions } from 'chart.js';
import { 
  IonContent, IonHeader, IonToolbar, IonTitle,
  IonRefresher, IonRefresherContent
} from '@ionic/angular/standalone';
import { OrderService } from '../../core/services/order.service';
import { Order } from '../../models/order.model';

@Component({
  selector: 'app-reports',
  templateUrl: './reports.component.html',
  styleUrls: ['./reports.component.scss'],
  standalone: true,
  imports: [CommonModule, IonContent, IonHeader, IonToolbar, IonTitle, BaseChartDirective, IonRefresher, IonRefresherContent]
})
export class ReportsComponent implements OnInit {
  // KPI Data
  totalRevenue = 0;
  totalOrders = 0;
  avgOrderValue = 0;

  // Chart Properties
  public lineChartData: ChartConfiguration<'line'>['data'] = {
    labels: [],
    datasets: []
  };
  public lineChartOptions: ChartOptions<'line'> = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { display: false }
    },
    scales: {
      y: { beginAtZero: true, ticks: { maxTicksLimit: 5 } },
      x: { grid: { display: false } }
    }
  };

  public doughnutChartData: ChartConfiguration<'doughnut'>['data'] = {
    labels: [],
    datasets: []
  };
  public doughnutChartOptions: ChartOptions<'doughnut'> = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { position: 'bottom' }
    }
  };

  loading = true;

  constructor(private orderService: OrderService) {}

  ngOnInit() {
    this.loadData();
  }

  loadData(event?: any) {
    this.loading = true;
    this.orderService.findAll().subscribe({
      next: (ordersData: any) => {
        const orders: Order[] = Array.isArray(ordersData) ? ordersData : [];
        this.processKpis(orders);
        this.processRevenueChart(orders);
        this.processTopProductsChart(orders);
        this.loading = false;
        if (event) event.target.complete();
      },
      error: (err) => {
        console.error('Error loading orders for reports', err);
        this.loading = false;
        if (event) event.target.complete();
      }
    });
  }

  handleRefresh(event: any) {
    this.loadData(event);
  }

  private processKpis(orders: Order[]) {
    this.totalOrders = orders.length;
    this.totalRevenue = orders.reduce((sum, o) => sum + (o.totalAmount || 0), 0);
    this.avgOrderValue = this.totalOrders > 0 ? this.totalRevenue / this.totalOrders : 0;
  }

  private processRevenueChart(orders: Order[]) {
    // Group revenue by date (YYYY-MM-DD or roughly short date string)
    const revenueByDate: { [key: string]: number } = {};
    
    // Sort orders by date
    const sortedOrders = [...orders].sort((a, b) => {
      return new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime();
    });

    sortedOrders.forEach(o => {
      const dateString = new Date(o.createdAt).toLocaleDateString();
      revenueByDate[dateString] = (revenueByDate[dateString] || 0) + o.totalAmount;
    });

    const labels = Object.keys(revenueByDate);
    const data = Object.values(revenueByDate);

    // If there's only 1 day of data, add a padding 0 day so line chart looks better
    if (labels.length === 1) {
      labels.unshift('');
      data.unshift(0);
    }

    this.lineChartData = {
      labels: labels,
      datasets: [
        {
          data: data,
          label: 'Doanh thu (VNĐ)',
          fill: true,
          tension: 0.4,
          borderColor: '#0284c7', // Sky Blue 600
          backgroundColor: 'rgba(2, 132, 199, 0.1)',
          pointBackgroundColor: '#0284c7'
        }
      ]
    };
  }

  private processTopProductsChart(orders: Order[]) {
    const qtyByProductId: { [key: number]: number } = {};
    
    orders.forEach(o => {
      o.items.forEach(item => {
        qtyByProductId[item.productId] = (qtyByProductId[item.productId] || 0) + item.quantity;
      });
    });

    // In a real app we'd map productId to Product Name. Since we don't have all products fetched here, 
    // we just use Product ID as a label or fetch names. For simplicity we will prefix "Sản phẩm "
    const sortedEntries = Object.entries(qtyByProductId)
      .sort((a, b) => b[1] - a[1]) // Sort descending
      .slice(0, 5); // Take top 5

    const labels = sortedEntries.map(e => `Mã SP: ${e[0]}`);
    const data = sortedEntries.map(e => e[1]);

    this.doughnutChartData = {
      labels: labels,
      datasets: [
        {
          data: data,
          backgroundColor: [
            '#0284c7', // Sky blue
            '#3b82f6', // Blue
            '#0ea5e9', // Light blue
            '#38bdf8', // Lighter blue
            '#bae6fd'  // Very light blue
          ],
          borderWidth: 0
        }
      ]
    };
  }
}

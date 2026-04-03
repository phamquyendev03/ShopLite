import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import {
  IonContent, IonHeader, IonTitle, IonToolbar, IonButtons, IonBackButton,
  IonCard, IonCardContent, IonCardHeader, IonCardTitle, IonCardSubtitle, IonSpinner,
  IonBadge, IonFooter, IonButton, IonIcon
} from '@ionic/angular/standalone';
import { ProductService } from '../../services/product.service';
import { Product } from '../../models/product.model';

@Component({
  selector: 'app-product-detail',
  templateUrl: './product-detail.component.html',
  styleUrls: ['./product-detail.component.scss'],
  standalone: true,
  imports: [
    CommonModule, RouterModule,
    IonContent, IonHeader, IonTitle, IonToolbar, IonButtons, IonBackButton,
    IonCard, IonCardContent, IonCardHeader, IonCardTitle, IonCardSubtitle, IonSpinner,
    IonBadge, IonFooter, IonButton, IonIcon
  ]
})
export class ProductDetailComponent implements OnInit {
  product: Product | null = null;
  loading = false;

  constructor(
    private route: ActivatedRoute,
    private productService: ProductService
  ) { }

  ngOnInit() {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.loadProduct(+idParam);
    }
  }

  loadProduct(id: number) {
    this.loading = true;
    this.productService.findById(id).subscribe({
      next: (res: any) => {
        // Interceptor đã unwrap → res là ResProductDTO trực tiếp
        this.product = res;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }
}

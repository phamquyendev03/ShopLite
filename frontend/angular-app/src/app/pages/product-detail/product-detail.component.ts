import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import {
  IonContent, IonHeader, IonTitle, IonToolbar, IonButtons, IonBackButton,
  IonSpinner, IonBadge, IonFooter, IonButton, IonIcon, ToastController
} from '@ionic/angular/standalone';
import { ProductService } from '../../core/services/product.service';
import { CartService } from '../../core/services/cart.service';
import { Product } from '../../models/product.model';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-product-detail',
  templateUrl: './product-detail.component.html',
  styleUrls: ['./product-detail.component.scss'],
  standalone: true,
  imports: [
    CommonModule, RouterModule,
    IonContent, IonHeader, IonTitle, IonToolbar, IonButtons, IonBackButton,
    IonSpinner, IonBadge, IonFooter, IonButton, IonIcon
  ]
})
export class ProductDetailComponent implements OnInit, OnDestroy {
  product: Product | null = null;
  loading = false;
  cartBadge = 0;
  private cartSub!: Subscription;

  constructor(
    private route: ActivatedRoute,
    private productService: ProductService,
    private cartService: CartService,
    private toastCtrl: ToastController
  ) { }

  ngOnInit() {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.loadProduct(+idParam);
    }
    
    this.cartSub = this.cartService.cartItems$.subscribe(items => {
      this.cartBadge = items.reduce((acc, item) => acc + item.quantity, 0);
    });
  }

  ngOnDestroy() {
    if (this.cartSub) this.cartSub.unsubscribe();
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

  async addToCart() {
    if (this.product) {
      this.cartService.addToCart(this.product, 1);
      const toast = await this.toastCtrl.create({
        message: 'Thêm vào giỏ hàng thành công',
        duration: 1500,
        color: 'success',
        position: 'top'
      });
      await toast.present();
    }
  }
}

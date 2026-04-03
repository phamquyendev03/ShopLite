import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import {
  IonContent, IonHeader, IonTitle, IonToolbar, IonButtons, IonBackButton,
  IonList, IonItem, IonThumbnail, IonLabel, IonIcon, IonButton, IonFooter,
  IonText
} from '@ionic/angular/standalone';

@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    IonContent, IonHeader, IonTitle, IonToolbar, IonButtons, IonBackButton,
    IonList, IonItem, IonThumbnail, IonLabel, IonIcon, IonButton, IonFooter,
    IonText
  ]
})
export class CartComponent implements OnInit {
  // Mock cart items for UI purposes
  cartItems: any[] = [
    {
      id: 1,
      name: 'Macbook Pro M2 2023',
      price: 32000000,
      quantity: 1,
      image: 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?auto=format&fit=crop&w=200&q=80'
    },
    {
      id: 2,
      name: 'Sony WH-1000XM5 Headphones',
      price: 8500000,
      quantity: 2,
      image: 'https://images.unsplash.com/photo-1618366712010-f4ae9c647dcb?auto=format&fit=crop&w=200&q=80'
    }
  ];

  constructor() { }
  ngOnInit() { }

  get total(): number {
    return this.cartItems.reduce((acc, item) => acc + (item.price * item.quantity), 0);
  }

  increaseQuantity(item: any) {
    item.quantity++;
  }

  decreaseQuantity(item: any) {
    if (item.quantity > 1) {
      item.quantity--;
    }
  }

  removeItem(item: any) {
    this.cartItems = this.cartItems.filter(i => i.id !== item.id);
  }

  checkout() {
    console.log('Proceed to checkout', this.cartItems);
  }
}

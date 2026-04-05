import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Product } from '../../models/product.model';

export interface CartItem {
  product: Product;
  quantity: number;
}

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private cartItemsSubject = new BehaviorSubject<CartItem[]>(this.loadCartFromStorage());
  public cartItems$ = this.cartItemsSubject.asObservable();

  constructor() {}

  private loadCartFromStorage(): CartItem[] {
    const data = localStorage.getItem('shoplite_cart');
    return data ? JSON.parse(data) : [];
  }

  private saveCartToStorage(items: CartItem[]): void {
    localStorage.setItem('shoplite_cart', JSON.stringify(items));
    this.cartItemsSubject.next(items);
  }

  getCartItems(): CartItem[] {
    return this.cartItemsSubject.value;
  }

  addToCart(product: Product, quantity: number = 1): void {
    const items = this.getCartItems();
    const existing = items.find(item => item.product.id === product.id);
    
    if (existing) {
      existing.quantity += quantity;
    } else {
      items.push({ product, quantity });
    }
    this.saveCartToStorage(items);
  }

  updateQuantity(productId: number, quantity: number): void {
    const items = this.getCartItems();
    const existing = items.find(item => item.product.id === productId);
    if (existing) {
      existing.quantity = quantity;
      if (existing.quantity <= 0) {
        this.removeFromCart(productId);
        return;
      }
      this.saveCartToStorage(items);
    }
  }

  removeFromCart(productId: number): void {
    const items = this.getCartItems().filter(item => item.product.id !== productId);
    this.saveCartToStorage(items);
  }

  clearCart(): void {
    this.saveCartToStorage([]);
  }

  getTotalPrice(): number {
    return this.getCartItems().reduce((acc, item) => acc + (item.product.price * item.quantity), 0);
  }
}

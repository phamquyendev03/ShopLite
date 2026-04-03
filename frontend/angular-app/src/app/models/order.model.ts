import { PaymentMethodEnum, StatusEnum } from './enums.model';

/**
 * order.model.ts — Mapping ResOrderDTO, ReqOrderDTO, ResOrderItemDTO
 */

export interface OrderItem {
  id?: number; // Chỉ có ở response
  productId: number;
  productName?: string; // Chỉ có ở response
  quantity: number;
  price: number;
  totalPrice?: number; // Chỉ có ở response
}

export interface Order {
  id: number;
  code: string;
  customerName: string;
  totalAmount: number;
  discount: number;
  status: StatusEnum;
  paymentMethod: PaymentMethodEnum;
  createdAt: string;
  paidAt: string | null;
  userId: number;
  username: string;
  items: OrderItem[];
}

export interface CreateOrderRequest {
  userId: number;
  customerName: string;
  discount: number;
  paymentMethod: PaymentMethodEnum;
  items: OrderItem[];
}

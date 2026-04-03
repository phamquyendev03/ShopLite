/**
 * enums.model.ts — Mapping các Enum constants từ backend
 */

export enum StatusEnum {
  PENDING = 'PENDING',
  CONFIRMED = 'CONFIRMED',
  SHIPPING = 'SHIPPING',
  COMPLETED = 'COMPLETED',
  CANCELLED = 'CANCELLED'
}

export enum PaymentMethodEnum {
  CASH = 'CASH',
  BANK = 'BANK'
}

export enum RoleEnum {
  ADMIN = 'ADMIN',
  STAFF = 'STAFF'
}

export enum TypeInventoryEnum {
  IMPORT = 'IMPORT',
  EXPORT = 'EXPORT'
}

export enum TypeTransactionEnum {
  INCOME = 'INCOME',
  EXPENSE = 'EXPENSE'
}

import { Injectable } from '@angular/core';

/**
 * StorageService — Abstraction layer cho storage.
 *
 * Hiện dùng localStorage (Web/PWA).
 * Mở rộng Capacitor Native: thay body bằng @capacitor/preferences:
 *   import { Preferences } from '@capacitor/preferences';
 *   await Preferences.set({ key, value: serialized });
 */
@Injectable({
  providedIn: 'root'
})
export class StorageService {

  /** Lưu giá trị; object sẽ được JSON.stringify tự động */
  setItem(key: string, value: unknown): void {
    const v = typeof value === 'string' ? value : JSON.stringify(value);
    localStorage.setItem(key, v);
  }

  /** Lấy giá trị dạng string, hoặc null nếu không tồn tại */
  getItem(key: string): string | null {
    return localStorage.getItem(key);
  }

  /** Xoá một key khỏi storage */
  removeItem(key: string): void {
    localStorage.removeItem(key);
  }

  /** Xoá toàn bộ storage — dùng khi logout */
  clear(): void {
    localStorage.clear();
  }
}

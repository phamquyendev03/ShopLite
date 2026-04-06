import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { PushNotifications, Token, ActionPerformed, PushNotification } from '@capacitor/push-notifications';
import { environment } from '../../../environments/environment';
import { Capacitor } from '@capacitor/core';

@Injectable({
  providedIn: 'root'
})
export class PushNotificationService {
  private readonly apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) { }

  /**
   * Khởi tạo quá trình đăng ký thông báo đẩy.
   * Nên gọi sau khi người dùng đăng nhập thành công.
   */
  async register(userId: number) {
    // Chỉ thực hiện trên thiết bị thật (Android/iOS)
    if (Capacitor.getPlatform() === 'web') {
      console.warn('Push Notifications không hỗ trợ trên nền tảng Web thông thường.');
      return;
    }

    // 1. Xin quyền từ người dùng
    let permStatus = await PushNotifications.checkPermissions();
    if (permStatus.receive === 'prompt') {
      permStatus = await PushNotifications.requestPermissions();
    }

    if (permStatus.receive !== 'granted') {
      console.error('Người dùng không cấp quyền thông báo.');
      return;
    }

    // 2. Thêm các listen events
    this.addListeners(userId);

    // 3. Đăng ký nhận token
    await PushNotifications.register();
  }

  private addListeners(userId: number) {
    // Lấy thành công token từ Firebase
    PushNotifications.addListener('registration', (token: Token) => {
      console.log('FCM Token: ' + token.value);
      this.sendTokenToBackend(userId, token.value);
    });

    // Lỗi khi đăng ký
    PushNotifications.addListener('registrationError', (error: any) => {
      console.error('Lỗi khi đăng ký Push: ' + JSON.stringify(error));
    });

    // Nhận thông báo khi app đang mở (Foreground)
    PushNotifications.addListener('pushNotificationReceived', (notification: PushNotification) => {
      console.log('Nhận thông báo: ', notification);
    });

    // Người dùng nhấn vào thông báo
    PushNotifications.addListener('pushNotificationActionPerformed', (notification: ActionPerformed) => {
      console.log('Người dùng nhấn vào thông báo: ', notification.notification);
    });
  }

  /**
   * Gửi token lên server để lưu vào database.
   */
  private sendTokenToBackend(userId: number, token: string) {
    const payload = {
      userId: userId,
      token: token,
      deviceType: Capacitor.getPlatform().toUpperCase()
    };

    this.http.post(`${this.apiUrl}/device-tokens/register`, payload).subscribe({
      next: () => console.log('Đã đăng ký FCM Token thành công với server.'),
      error: (err) => console.error('Lỗi khi gửi Token lên server: ', err)
    });
  }
}

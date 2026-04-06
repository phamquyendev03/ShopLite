import { Component } from '@angular/core';
import { IonApp, IonRouterOutlet } from '@ionic/angular/standalone';
import { AuthService } from './core/services/auth.service';
import { PushNotificationService } from './core/services/push-notification.service';

@Component({
  selector: 'app-root',
  templateUrl: 'app.component.html',
  imports: [IonApp, IonRouterOutlet],
})
export class AppComponent {
  constructor(
    private authService: AuthService,
    private pushService: PushNotificationService
  ) {
    this.initializeApp();
  }

  private initializeApp() {
    const user = this.authService.getCurrentUser();
    if (user) {
      // Nếu đã đăng nhập, tự động đăng ký lại Push Token (đề phòng token mới hoặc cài lại app)
      this.pushService.register(user.id);
    }
  }
} 

import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { IonContent, ToastController } from '@ionic/angular/standalone';
import { AuthService } from '../../core';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    IonContent
  ]
})
export class LoginComponent {
  username = '';
  password = '';
  showPasswordLogin = false;

  regPhone = '';
  regPassword = '';
  showPasswordReg = false;

  currentView: 'login' | 'register' = 'login';
  loading = false;

  constructor(
    private authService: AuthService,
    private router: Router,
    private toastCtrl: ToastController
  ) { }

  toggleView(view: 'login' | 'register') {
    this.currentView = view;
  }

  async onLogin() {
    if (!this.username || !this.password) return;

    this.loading = true;
    this.authService.login(this.username, this.password).subscribe({
      next: async (res) => {
        this.loading = false;
        const toast = await this.toastCtrl.create({
          message: 'Đăng nhập thành công!',
          duration: 2000,
          color: 'success'
        });
        await toast.present();
        this.router.navigate(['/tabs']);
      },
      error: async (err) => {
        this.loading = false;
        const errorMsg = err.error?.message || err.message || 'Đăng nhập thất bại';
        const toast = await this.toastCtrl.create({
          message: errorMsg,
          duration: 3000,
          color: 'danger'
        });
        await toast.present();
      }
    });
  }

  async onRegister() {
    if (!this.regPhone || !this.regPassword) return;

    this.loading = true;

    setTimeout(async () => {
      this.loading = false;
      const toast = await this.toastCtrl.create({
        message: 'Đăng ký thành công! Hãy đăng nhập.',
        duration: 3000,
        color: 'success'
      });
      await toast.present();

      this.username = this.regPhone;
      this.password = this.regPassword;
      this.toggleView('login');

      this.regPhone = '';
      this.regPassword = '';
    }, 1000);
  }
}

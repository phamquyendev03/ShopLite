import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import {
  IonContent, IonHeader, IonTitle, IonToolbar,
  IonList, IonItem, IonInput, IonButton,
  ToastController, IonIcon
} from '@ionic/angular/standalone';
import { AuthService } from '../../core';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
  standalone: true,
  imports: [
    CommonModule, FormsModule,
    IonContent, IonHeader, IonTitle, IonToolbar,
    IonList, IonItem, IonInput, IonButton, IonIcon
  ]
})
export class LoginComponent {
  username = '';
  password = '';
  loading = false;

  constructor(
    private authService: AuthService,
    private router: Router,
    private toastCtrl: ToastController
  ) { }

  async onLogin() {
    if (!this.username || !this.password) return;

    this.loading = true;
    this.authService.login(this.username, this.password).subscribe({
      next: async (res) => {
        this.loading = false;
        const toast = await this.toastCtrl.create({
          message: 'Login successful!',
          duration: 2000,
          color: 'success'
        });
        await toast.present();
        this.router.navigate(['/products']);
      },
      error: async (err) => {
        this.loading = false;
        const errorMsg = err.error?.message || err.message || 'Login failed';
        const toast = await this.toastCtrl.create({
          message: errorMsg,
          duration: 3000,
          color: 'danger'
        });
        await toast.present();
      }
    });
  }
}

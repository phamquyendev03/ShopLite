import { Component } from '@angular/core';
import { IonContent } from '@ionic/angular/standalone';

@Component({
  selector: 'app-reports',
  template: `<ion-content class="ion-padding"><h2 style="font-family:'Outfit',sans-serif;color:#0F172A;margin-top:40px;text-align:center;">📊 Báo cáo<br><small style="font-size:14px;color:#94A3B8;">Coming soon...</small></h2></ion-content>`,
  standalone: true,
  imports: [IonContent]
})
export class ReportsComponent {}

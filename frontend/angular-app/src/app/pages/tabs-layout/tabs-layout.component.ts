import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { IonContent, IonRouterOutlet } from '@ionic/angular/standalone';
import { Router } from '@angular/router';

@Component({
  selector: 'app-tabs-layout',
  templateUrl: './tabs-layout.component.html',
  styleUrls: ['./tabs-layout.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    IonContent,
    IonRouterOutlet,
  ]
})
export class TabsLayoutComponent {
  constructor(private router: Router) {}

  onFabClick() {
    // FAB: Quick action to open Cart/POS
    this.router.navigate(['/tabs/orders']);
  }
}

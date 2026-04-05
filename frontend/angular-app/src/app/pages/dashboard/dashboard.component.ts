import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { 
  IonContent, IonHeader, IonToolbar, IonButtons, IonButton, 
  IonIcon, IonSearchbar 
} from '@ionic/angular/standalone';
import { addIcons } from 'ionicons';
import { notificationsOutline, searchOutline, arrowForwardOutline, barChartOutline, statsChartOutline, cartOutline, cubeOutline, peopleOutline, appsOutline } from 'ionicons/icons';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
  standalone: true,
  imports: [
    CommonModule, RouterModule, 
    IonContent, IonHeader, IonToolbar, IonButtons, IonButton, IonIcon, IonSearchbar
  ]
})
export class DashboardComponent implements OnInit {
  constructor() {
    addIcons({ 
      notificationsOutline, searchOutline, arrowForwardOutline, 
      barChartOutline, statsChartOutline, cartOutline, 
      cubeOutline, peopleOutline, appsOutline 
    });
  }

  ngOnInit() {}
}

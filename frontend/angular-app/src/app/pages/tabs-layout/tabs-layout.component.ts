import { Component } from '@angular/core';
import {
  IonTabs, IonTabBar, IonTabButton, IonIcon, IonLabel
} from '@ionic/angular/standalone';
import { addIcons } from 'ionicons';
import {
  gridOutline, cartOutline, receiptOutline,
  cubeOutline, menuOutline, personOutline,
  statsChartOutline
} from 'ionicons/icons';

@Component({
  selector: 'app-tabs-layout',
  templateUrl: './tabs-layout.component.html',
  styleUrls: ['./tabs-layout.component.scss'],
  standalone: true,
  imports: [IonTabs, IonTabBar, IonTabButton, IonIcon, IonLabel]
})
export class TabsLayoutComponent {
  constructor() {
    addIcons({
      gridOutline, cartOutline, receiptOutline,
      cubeOutline, menuOutline, personOutline,
      statsChartOutline
    });
  }
}

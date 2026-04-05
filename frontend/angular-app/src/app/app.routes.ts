import { Routes } from '@angular/router';
import { authGuard } from './core';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'welcome',
    pathMatch: 'full',
  },
  {
    path: 'home',
    redirectTo: 'welcome',
    pathMatch: 'full',
  },
  {
    path: 'welcome',
    loadComponent: () =>
      import('./pages/welcome/welcome.component').then(m => m.WelcomeComponent)
  },
  {
    path: 'login',
    loadComponent: () =>
      import('./pages/login/login.component').then(m => m.LoginComponent)
  },

  {
    path: 'tabs',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./pages/tabs-layout/tabs-layout.component').then(m => m.TabsLayoutComponent),
    children: [
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full',
      },
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./pages/dashboard/dashboard.component').then(m => m.DashboardComponent)
      },
      {
        path: 'pos',
        loadComponent: () =>
          import('./pages/pos/pos.component').then(m => m.PosComponent)
      },
      {
        path: 'reports',
        loadComponent: () =>
          import('./pages/reports/reports.component').then(m => m.ReportsComponent)
      },
      {
        path: 'orders',
        loadComponent: () =>
          import('./pages/orders/orders.component').then(m => m.OrdersComponent)
      },
      {
        path: 'inventory',
        loadComponent: () =>
          import('./pages/product-list/product-list.component').then(m => m.ProductListComponent)
      },
      {
        path: 'inventory/:id',
        loadComponent: () =>
          import('./pages/product-detail/product-detail.component').then(m => m.ProductDetailComponent)
      },
      {
        path: 'more',
        loadComponent: () =>
          import('./pages/profile/profile.component').then(m => m.ProfileComponent)
      },
    ]
  },

  {
    path: 'cart',
    redirectTo: 'tabs/pos',
    pathMatch: 'full'
  },
  {
    path: 'products',
    redirectTo: 'tabs/pos',
    pathMatch: 'full'
  },
  {
    path: 'profile',
    redirectTo: 'tabs/more',
    pathMatch: 'full'
  },
  {
    path: 'products/:id',
    redirectTo: 'tabs/inventory/:id',
  },
];

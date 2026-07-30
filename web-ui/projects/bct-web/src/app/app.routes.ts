import { Routes } from '@angular/router';

import { MainLayoutComponent } from './layout/main-layout/main-layout.component';
import { HoSoDetailComponent } from './features/ho-so-detail/ho-so-detail.component';
import { HoSoListComponent } from './features/ho-so-list/ho-so-list.component';

export const routes: Routes = [
  {
    path: '',
    component: MainLayoutComponent,
    children: [
      { path: '', component: HoSoListComponent },
      { path: 'ho-so/:maSoHoSo', component: HoSoDetailComponent },
    ],
  },
  { path: '**', redirectTo: '' },
];

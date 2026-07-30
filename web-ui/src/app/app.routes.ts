import { Routes } from '@angular/router';

import { MainLayoutComponent } from './layout/main-layout/main-layout.component';
import { GuiHoSoComponent } from './features/nsw/gui-ho-so/gui-ho-so.component';
import { TraCuuHoSoComponent } from './features/nsw/tra-cuu/tra-cuu-ho-so.component';

export const routes: Routes = [
  {
    path: '',
    component: MainLayoutComponent,
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'nop-ho-so' },
      { path: 'nop-ho-so', component: GuiHoSoComponent },
      { path: 'tra-cuu', component: TraCuuHoSoComponent },
    ],
  },
  { path: '**', redirectTo: 'nop-ho-so' },
];

import { Routes } from '@angular/router';
import {LoginComponent} from './login/component/login.component';
import {RegisterComponent} from './register/component/register.component';
import {DashboardComponent} from './dashboard/dashboard.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'dashboard', component: DashboardComponent },
  { path: '', redirectTo: '/login', pathMatch: 'full' },
];

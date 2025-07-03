import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UserComponent } from './user.component';
import { guestGuard } from '../guard/guestGuard/guest.guard';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';

const routes: Routes = [
  {
    path: '',
    component: UserComponent
  },
  {
    path: 'login',
    canActivate: [guestGuard],
    component: LoginComponent
  },
  {
    path: 'register',
    canActivate: [guestGuard],
    component: RegisterComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class UserRoutingModule { }

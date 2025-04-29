import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { UserRoutingModule } from './user-routing.module';
import { UserComponent } from './user.component';
import {provideHttpClient} from '@angular/common/http';


@NgModule({
  declarations: [
    UserComponent
  ],
  imports: [
    CommonModule,
    UserRoutingModule
  ],
  providers: [
    provideHttpClient()
  ],
})
export class UserModule { }

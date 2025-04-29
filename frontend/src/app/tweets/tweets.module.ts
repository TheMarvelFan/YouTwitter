import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { TweetsRoutingModule } from './tweets-routing.module';
import { TweetsComponent } from './tweets.component';
import { provideHttpClient } from '@angular/common/http';
import { TweetCardComponent } from '../tweet-card/tweet-card.component';
import { NgOptimizedImage } from '@angular/common';

@NgModule({
  declarations: [
    TweetsComponent,
    TweetCardComponent
  ],
  imports: [
    CommonModule,
    TweetsRoutingModule,
    NgOptimizedImage
  ],
  providers: [
    provideHttpClient()
  ]
})
export class TweetsModule { }

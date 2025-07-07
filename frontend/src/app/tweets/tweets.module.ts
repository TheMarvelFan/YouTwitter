import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { TweetsRoutingModule } from './tweets-routing.module';
import { TweetsComponent } from './tweets.component';
import { provideHttpClient } from '@angular/common/http';
import { TweetCardComponent } from './tweet-card/tweet-card.component';
import { NgOptimizedImage } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { AddTweetComponent } from './add-tweet/add-tweet.component';
import {MatDialogModule} from '@angular/material/dialog';
import {MatFormFieldModule} from '@angular/material/form-field';
import {ReactiveFormsModule} from '@angular/forms';
import {MatInputModule} from '@angular/material/input';
import {MatButtonModule} from '@angular/material/button';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';

@NgModule({
  declarations: [
    TweetsComponent,
    TweetCardComponent,
    AddTweetComponent
  ],
  imports: [
    CommonModule,
    TweetsRoutingModule,
    NgOptimizedImage,
    MatIconModule,
    MatDialogModule,
    MatFormFieldModule,
    ReactiveFormsModule,
    MatInputModule,
    MatButtonModule,
    MatProgressSpinnerModule,
  ],
  providers: [
    provideHttpClient()
  ]
})
export class TweetsModule { }

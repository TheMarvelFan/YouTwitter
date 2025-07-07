import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { VideosRoutingModule } from './videos-routing.module';
import { VideosComponent } from './videos.component';
import { NgOptimizedImage, provideCloudinaryLoader } from '@angular/common';
import { provideHttpClient } from '@angular/common/http';
import { VideoCardComponent } from './video-card/video-card.component';
import { APP_CONFIG, APP_SERVICE_CONFIG } from '../config/app.config.service';
import { AddVideoComponent } from './add-video/add-video.component';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { ReactiveFormsModule } from '@angular/forms';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatButtonModule } from '@angular/material/button';


@NgModule({
  declarations: [
    VideosComponent,
    VideoCardComponent,
    AddVideoComponent
  ],
  imports: [
    CommonModule,
    VideosRoutingModule,
    NgOptimizedImage,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    ReactiveFormsModule,
    MatProgressSpinnerModule,
    MatButtonModule
  ],
  exports: [
    VideosComponent
  ],
  providers: [
    provideHttpClient(),
    provideCloudinaryLoader("https://res.cloudinary.com/dnxncx5sv"),
    {
      provide: APP_SERVICE_CONFIG,
      useValue: APP_CONFIG
    }
  ]
})
export class VideosModule { }

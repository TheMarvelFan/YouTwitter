import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { VideosRoutingModule } from './videos-routing.module';
import { VideosComponent } from './videos.component';
import { NgOptimizedImage, provideCloudinaryLoader } from '@angular/common';
import { provideHttpClient } from '@angular/common/http';
import { VideoCardComponent } from './video-card/video-card.component';
import {APP_CONFIG, APP_SERVICE_CONFIG} from '../config/app.config.service';


@NgModule({
  declarations: [
    VideosComponent,
    VideoCardComponent
  ],
  imports: [
    CommonModule,
    VideosRoutingModule,
    NgOptimizedImage
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

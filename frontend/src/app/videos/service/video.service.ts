import { Inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable, shareReplay } from 'rxjs';
import { APP_SERVICE_CONFIG } from '../../config/app.config.service';
import { AppConfig } from '../../models/app.config';
import { Video } from '../../models/Video';
import { HttpParams } from "@angular/common/http";
import { VideoResponse } from '../../models/VideoResponse';

@Injectable({
  providedIn: 'root'
})
export class VideoService {
  videos$: Observable<Video[]>;

  constructor(
    @Inject(APP_SERVICE_CONFIG)
    private config: AppConfig,
    private http: HttpClient
  ) {
    const options = {
      params: new HttpParams()
        .set('page', '0')
        .set('size', '10')
    };

    console.log("Before error occurs");
    this.videos$ = this.http.get<VideoResponse>(`${this.config.apiUrl}/videos/`, options).pipe(
      map(response => response?.data?.content),
      shareReplay(1)
    );
  }
}

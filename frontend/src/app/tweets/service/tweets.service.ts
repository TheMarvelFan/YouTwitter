import { Inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable, shareReplay } from 'rxjs';
import { APP_SERVICE_CONFIG } from '../../config/app.config.service';
import { AppConfig } from '../../models/app.config';
import { HttpParams } from "@angular/common/http";
import { Tweet } from '../../models/Tweet';
import { TweetResponse } from '../../models/TweetResponse';

@Injectable({
  providedIn: 'root'
})
export class TweetsService {
  tweets$: Observable<Tweet[]>;

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

    this.tweets$ = this.http.get<TweetResponse>(`${this.config.apiUrl}/tweets/`, options).pipe(
      map(response => response?.data?.content),
      shareReplay(1)
    );
  }
}

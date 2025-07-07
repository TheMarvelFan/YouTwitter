import { Inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable, shareReplay } from 'rxjs';
import { APP_SERVICE_CONFIG } from '../../config/app.config.service';
import { AppConfig } from '../../models/app.config';
import { HttpParams } from "@angular/common/http";
import { Tweet } from '../../models/Tweet';
import { TweetResponse } from '../../models/TweetResponse';
import { ResponseType } from '../../models/ResponseType';

interface StringRequest {
  field: string;
}

@Injectable({
  providedIn: 'root'
})
export class TweetsService {
  tweets$ !: Observable<Tweet[]>;

  constructor(
    @Inject(APP_SERVICE_CONFIG)
    private config: AppConfig,
    private http: HttpClient
  ) {
    this.loadTweets();
  }

  private loadTweets(): void {
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

  // Method to create a new tweet
  createTweet(content: string): Observable<Tweet> {
    const requestBody: StringRequest = {
      field: content
    };

    return this.http.post<ResponseType<Tweet>>(`${this.config.apiUrl}/tweets/`, requestBody, {
      withCredentials: true,
      headers: {
        'Content-Type': 'application/json'
      }
    }).pipe(
      map(response => response.data)
    );
  }

  // Method to refresh the tweets list
  refreshTweets(): void {
    this.loadTweets();
  }
}

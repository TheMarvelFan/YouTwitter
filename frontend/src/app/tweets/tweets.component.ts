import { Component, OnInit, SkipSelf } from '@angular/core';
import { Tweet } from '../models/Tweet';
import {catchError, Observable, of, Subject} from 'rxjs';
import { TweetsService } from './service/tweets.service';

@Component({
  selector: 'app-tweets',
  standalone: false,
  templateUrl: './tweets.component.html',
  styleUrl: './tweets.component.css'
})
export class TweetsComponent implements OnInit {
  tweets$: Observable<Tweet[]> | undefined;

  error$: Subject<string> = new Subject<string>();

  getError$ = this.error$.asObservable();

  constructor(
    @SkipSelf() private tweetService: TweetsService
  ) {

  }

  ngOnInit(): void {
      this.tweets$ = this.tweetService.tweets$.pipe(
        catchError(error => {
          console.log(error);
          this.error$.next(error);
          return of([]);
        })
      );
  }
}

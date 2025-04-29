import { Component, OnInit, SkipSelf } from '@angular/core';
import { VideoService } from './service/video.service';
import { Video } from '../models/Video';
import { catchError, Observable, of, Subject } from 'rxjs';

@Component({
  selector: 'app-videos',
  standalone: false,
  templateUrl: './videos.component.html',
  styleUrl: './videos.component.css'
})
export class VideosComponent implements OnInit {
  videos$: Observable<Video[]> | undefined;

  error$: Subject<string> = new Subject<string>();

  getError$ = this.error$.asObservable();

  constructor(@SkipSelf() private videoService: VideoService) {

  }

  ngOnInit() {
    this.videos$ = this.videoService.videos$.pipe(
      catchError(error => {
        console.log(error);
        this.error$.next(error);
        return of([]);
      })
    );
  }

  transformCloudinaryUrl(url: string): string {
    if (!url) return '';
    // Remove the base part of the Cloudinary URL
    return url.replace('http://res.cloudinary.com/dnxncx5sv/image/upload/', '');
  }

  findType(parameter: any) {
    console.log(parameter);
    return parameter;
  }
}

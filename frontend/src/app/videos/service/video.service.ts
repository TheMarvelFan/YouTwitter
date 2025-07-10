import { Inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable, shareReplay } from 'rxjs';
import { APP_SERVICE_CONFIG } from '../../config/app.config.service';
import { AppConfig } from '../../models/app.config';
import { Video } from '../../models/Video';
import { HttpParams } from "@angular/common/http";
import { VideoResponse } from '../../models/VideoResponse';
import { ResponseType } from '../../models/ResponseType';

@Injectable({
  providedIn: 'root'
})
export class VideoService {
  videos$ !: Observable<Video[]>;

  constructor(
    @Inject(APP_SERVICE_CONFIG)
    private config: AppConfig,
    private http: HttpClient
  ) {
    this.loadVideos();
  }

  private loadVideos(): void {
    const options = {
      params: new HttpParams()
        .set('page', '0')
        .set('size', '10')
    };

    this.videos$ = this.http.get<VideoResponse>(`${this.config.apiUrl}/videos/`, options).pipe(
      map(response => response?.data?.content),
      shareReplay(1)
    );
  }

  // Method to upload a new video
  uploadVideo(title: string, description: string, videoFile: File, thumbnail: File): Observable<Video> {
    const formData = new FormData();
    formData.append('title', title);
    formData.append('description', description);
    formData.append('videoFile', videoFile);
    formData.append('thumbnail', thumbnail);

    return this.http.post<ResponseType<Video>>(`${this.config.apiUrl}/videos/`, formData, {
      withCredentials: true
    }).pipe(
      map(response => response.data)
    );
  }

  // Method to refresh the videos list
  refreshVideos(): void {
    this.loadVideos();
  }

  // Get a single video by ID
  getVideoById(videoId: number): Observable<ResponseType<Video>> {
    return this.http.get<ResponseType<Video>>(`${this.config.apiUrl}/videos/${videoId}`);
  }

  // Method to update a video
  updateVideo(videoId: number, title?: string, description?: string, thumbnail?: File): Observable<Video> {
    const formData = new FormData();

    if (title) formData.append('title', title);
    if (description) formData.append('description', description);
    if (thumbnail) formData.append('thumbnail', thumbnail);

    return this.http.patch<ResponseType<Video>>(`${this.config.apiUrl}/videos/${videoId}`, formData, {
      withCredentials: true
    }).pipe(
      map(response => response.data)
    );
  }

  // Method to delete a video
  deleteVideo(videoId: number): Observable<ResponseType<string>> {
    return this.http.delete<ResponseType<string>>(`${this.config.apiUrl}/videos/${videoId}`, {
      withCredentials: true
    });
  }

  // Method to toggle publish status
  togglePublish(videoId: number): Observable<ResponseType<Video>> {
    return this.http.patch<ResponseType<Video>>(`${this.config.apiUrl}/videos/toggle/publish/${videoId}`, {}, {
      withCredentials: true
    });
  }

  // Method to get current user's videos
  getCurrentUserVideos(page: number = 0, size: number = 10): Observable<VideoResponse> {
    const options = {
      params: new HttpParams()
        .set('page', page.toString())
        .set('size', size.toString())
    };

    return this.http.get<VideoResponse>(`${this.config.apiUrl}/videos/current`, {
      ...options,
      withCredentials: true
    });
  }
}

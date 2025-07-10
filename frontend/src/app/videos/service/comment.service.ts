import { Injectable, Inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { APP_SERVICE_CONFIG } from '../../config/app.config.service';
import { AppConfig } from '../../models/app.config';
import { Comment } from '../../models/Comment';
import { ResponseType } from '../../models/ResponseType';

export interface CommentResponse {
  content: Comment[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class CommentService {

  constructor(
    @Inject(APP_SERVICE_CONFIG) private config: AppConfig,
    private http: HttpClient
  ) { }

  // Get comments for a video with pagination
  getComments(videoId: number, page: number = 0, size: number = 10): Observable<ResponseType<CommentResponse>> {
    const params = { page: page.toString(), size: size.toString() };

    return this.http.get<ResponseType<CommentResponse>>(
      `${this.config.apiUrl}/comments/${videoId}`,
      { params }
    );
  }

  // Get a single comment by ID
  getComment(commentId: number): Observable<ResponseType<Comment>> {
    return this.http.get<ResponseType<Comment>>(
      `${this.config.apiUrl}/comments/c/${commentId}`
    );
  }

  // Add a new comment to a video
  addComment(videoId: number, content: string): Observable<ResponseType<Comment>> {
    const body = { field: content };

    return this.http.post<ResponseType<Comment>>(
      `${this.config.apiUrl}/comments/${videoId}`,
      body,
      { withCredentials: true }
    );
  }

  // Update an existing comment
  updateComment(commentId: number, content: string): Observable<ResponseType<Comment>> {
    const body = { field: content };

    return this.http.patch<ResponseType<Comment>>(
      `${this.config.apiUrl}/comments/c/${commentId}`,
      body,
      { withCredentials: true }
    );
  }

  // Delete a comment
  deleteComment(commentId: number): Observable<ResponseType<null>> {
    return this.http.delete<ResponseType<null>>(
      `${this.config.apiUrl}/comments/c/${commentId}`,
      { withCredentials: true }
    );
  }
}

import { Component, ElementRef, HostListener, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Video } from '../../models/Video';
import { ActivatedRoute, Router } from '@angular/router';
import { VideoService } from '../service/video.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserService } from '../../user/service/user.service';
import { catchError, of, Subject, takeUntil } from 'rxjs';
import { CommentService } from '../service/comment.service';
import { Comment } from '../../models/Comment';

@Component({
  selector: 'app-watch-video',
  standalone: false,
  templateUrl: './watch-video.component.html',
  styleUrl: './watch-video.component.css'
})
export class WatchVideoComponent implements OnInit, OnDestroy {
  @ViewChild('videoPlayer', { static: false }) videoPlayer!: ElementRef<HTMLVideoElement>;
  @ViewChild('progressBar', { static: false }) progressBar!: ElementRef<HTMLDivElement>;
  @ViewChild('volumeSlider', { static: false }) volumeSlider!: ElementRef<HTMLInputElement>;
  @ViewChild('speedMenu', { static: false }) speedMenu!: ElementRef;

  video: Video | null = null;
  comments: Comment[] = [];
  currentPage = 0;
  pageSize = 10;
  totalComments = 0;
  loading = true;
  commentsLoading = false;
  videoError = false;
  videoErrorMessage = '';

  // Authentication
  isLoggedIn = false;
  currentUser: any = null;

  // Comment form
  commentForm: FormGroup;
  submittingComment = false;
  editingCommentId: number | null = null;

  // Video player controls
  isPlaying = false;
  currentTime = 0;
  duration = 0;
  volume = 1;
  isMuted = false;
  isFullscreen = false;
  showControls = true;
  buffering = false;
  playbackRate = 1;
  speedMenuOpen = false;
  videoLoaded = false;

  // Available playback speeds
  playbackSpeeds = [0.25, 0.5, 0.75, 1, 1.25, 1.5, 1.75, 2];

  private destroy$ = new Subject<void>();
  private controlsTimeout: any;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private videoService: VideoService,
    private commentService: CommentService,
    private authService: UserService,
    private snackBar: MatSnackBar,
    private fb: FormBuilder
  ) {
    this.commentForm = this.fb.group({
      content: ['', [Validators.required, Validators.minLength(1), Validators.maxLength(1000)]]
    });
  }

  ngOnInit(): void {
    this.checkAuthStatus();
    this.loadVideo();
    this.setupControlsHiding();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    if (this.controlsTimeout) {
      clearTimeout(this.controlsTimeout);
    }
  }

  private checkAuthStatus(): void {
    this.authService.isLoggedIn$.pipe(
      takeUntil(this.destroy$)
    ).subscribe(isLoggedIn => {
      this.isLoggedIn = isLoggedIn;
      if (isLoggedIn) {
        this.authService.currentUser$.pipe(
          takeUntil(this.destroy$)
        ).subscribe(user => {
          this.currentUser = user;
        });
      }
    });
  }

  private loadVideo(): void {
    const videoId = this.route.snapshot.paramMap.get('id');
    if (!videoId) {
      this.router.navigate(['/videos']);
      return;
    }

    this.videoService.getVideoById(+videoId).pipe(
      catchError(error => {
        console.error('Error loading video:', error);
        this.snackBar.open('Error loading video', 'Close', { duration: 3000 });
        this.router.navigate(['/videos']);
        return of(null);
      }),
      takeUntil(this.destroy$)
    ).subscribe(response => {
      if (response?.data) {
        this.video = response.data;
        this.loading = false;
        this.loadComments();

        // Debug: Log video URL
        console.log('Video URL:', this.video.videoFile);
        console.log('Video object:', this.video);
      }
    });
  }

  private loadComments(): void {
    if (!this.video?.id) return;

    this.commentsLoading = true;
    this.commentService.getComments(this.video.id, this.currentPage, this.pageSize).pipe(
      catchError(error => {
        console.error('Error loading comments:', error);
        this.snackBar.open('Error loading comments', 'Close', { duration: 3000 });
        return of(null);
      }),
      takeUntil(this.destroy$)
    ).subscribe(response => {
      if (response?.data) {
        this.comments = response.data.content;
        this.totalComments = response.data.totalElements;
      }
      this.commentsLoading = false;
    });
  }

  // Video Player Controls
  onVideoLoaded(): void {
    const video = this.videoPlayer.nativeElement;
    this.duration = video.duration;
    this.volume = video.volume;
    this.videoLoaded = true;
    this.videoError = false;
    console.log('Video loaded successfully', { duration: this.duration, volume: this.volume });
  }

  onVideoError(event: Event): void {
    const video = this.videoPlayer.nativeElement;
    this.videoError = true;
    this.videoLoaded = false;

    // Get specific error information
    const error = video.error;
    if (error) {
      switch (error.code) {
        case error.MEDIA_ERR_ABORTED:
          this.videoErrorMessage = 'Video playback was aborted';
          break;
        case error.MEDIA_ERR_NETWORK:
          this.videoErrorMessage = 'Network error occurred while loading video';
          break;
        case error.MEDIA_ERR_DECODE:
          this.videoErrorMessage = 'Video format is not supported or file is corrupted';
          break;
        case error.MEDIA_ERR_SRC_NOT_SUPPORTED:
          this.videoErrorMessage = 'Video format is not supported by your browser';
          break;
        default:
          this.videoErrorMessage = 'An unknown error occurred';
      }
    } else {
      this.videoErrorMessage = 'Failed to load video';
    }

    console.error('Video error:', {
      error: error,
      message: this.videoErrorMessage,
      videoUrl: this.video?.videoFile
    });

    this.snackBar.open(this.videoErrorMessage, 'Close', { duration: 5000 });
  }

  onTimeUpdate(): void {
    const video = this.videoPlayer.nativeElement;
    this.currentTime = video.currentTime;
    this.updateProgressBar();
  }

  onWaiting(): void {
    this.buffering = true;
  }

  onCanPlay(): void {
    this.buffering = false;
  }

  onCanPlayThrough(): void {
    this.buffering = false;
    console.log('Video can play through without interruption');
  }

  togglePlayPause(): void {
    if (!this.videoLoaded || this.videoError) {
      console.warn('Cannot play video: not loaded or error occurred');
      return;
    }

    const video = this.videoPlayer.nativeElement;

    try {
      if (video.paused) {
        const playPromise = video.play();
        if (playPromise !== undefined) {
          playPromise
            .then(() => {
              this.isPlaying = true;
              console.log('Video started playing');
            })
            .catch(error => {
              console.error('Error playing video:', error);
              this.snackBar.open('Error playing video: ' + error.message, 'Close', { duration: 3000 });
            });
        }
      } else {
        video.pause();
        this.isPlaying = false;
        console.log('Video paused');
      }
    } catch (error) {
      console.error('Error in togglePlayPause:', error);
      this.snackBar.open('Error controlling video playback', 'Close', { duration: 3000 });
    }
  }

  onProgressClick(event: MouseEvent): void {
    if (!this.videoLoaded || this.videoError) return;

    const progressBar = this.progressBar.nativeElement;
    const rect = progressBar.getBoundingClientRect();
    const percent = (event.clientX - rect.left) / rect.width;
    const video = this.videoPlayer.nativeElement;
    video.currentTime = percent * video.duration;
  }

  private updateProgressBar(): void {
    if (this.duration > 0) {
      const progressBar = this.progressBar.nativeElement;
      const percent = (this.currentTime / this.duration) * 100;
      progressBar.style.setProperty('--progress', `${percent}%`);
    }
  }

  changeVolume(event: Event): void {
    const target = event.target as HTMLInputElement;
    const volume = parseFloat(target.value);
    const video = this.videoPlayer.nativeElement;
    video.volume = volume;
    this.volume = volume;
    this.isMuted = volume === 0;
  }

  toggleMute(): void {
    const video = this.videoPlayer.nativeElement;
    if (this.isMuted) {
      video.volume = this.volume > 0 ? this.volume : 0.5;
      this.isMuted = false;
    } else {
      video.volume = 0;
      this.isMuted = true;
    }
  }

  changePlaybackSpeed(speed: number): void {
    const video = this.videoPlayer.nativeElement;
    video.playbackRate = speed;
    this.playbackRate = speed;
  }

  toggleFullscreen(): void {
    const video = this.videoPlayer.nativeElement;
    if (!this.isFullscreen) {
      if (video.requestFullscreen) {
        video.requestFullscreen();
      }
    } else {
      if (document.exitFullscreen) {
        document.exitFullscreen();
      }
    }
    this.isFullscreen = !this.isFullscreen;
  }

  private setupControlsHiding(): void {
    this.showControls = true;
    this.resetControlsTimeout();
  }

  onMouseMove(): void {
    this.showControls = true;
    this.resetControlsTimeout();
  }

  private resetControlsTimeout(): void {
    if (this.controlsTimeout) {
      clearTimeout(this.controlsTimeout);
    }
    this.controlsTimeout = setTimeout(() => {
      if (this.isPlaying) {
        this.showControls = false;
      }
    }, 3000);
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    const target = event.target as HTMLElement;

    // If speed menu is open and click is outside the menu, close it
    if (this.speedMenuOpen && this.speedMenu && !this.speedMenu.nativeElement.contains(target)) {
      this.speedMenuOpen = false;
    }
  }

  @HostListener('document:keydown', ['$event'])
  onKeyDown(event: KeyboardEvent): void {
    if (event.target instanceof HTMLInputElement || event.target instanceof HTMLTextAreaElement) {
      return;
    }

    switch (event.key) {
      case ' ':
        event.preventDefault();
        this.togglePlayPause();
        break;
      case 'f':
        event.preventDefault();
        this.toggleFullscreen();
        break;
      case 'm':
        event.preventDefault();
        this.toggleMute();
        break;
      case 'ArrowLeft':
        event.preventDefault();
        this.skipTime(-10);
        break;
      case 'ArrowRight':
        event.preventDefault();
        this.skipTime(10);
        break;
    }
  }

  private skipTime(seconds: number): void {
    const video = this.videoPlayer.nativeElement;
    video.currentTime = Math.max(0, Math.min(video.duration, video.currentTime + seconds));
  }

  // Helper method to check if video URL is valid
  isValidVideoUrl(url: string): boolean {
    if (!url) return false;

    // Check if it's a valid URL
    try {
      new URL(url);
      return true;
    } catch {
      return false;
    }
  }

  // Method to reload video
  reloadVideo(): void {
    if (this.video?.videoFile) {
      const video = this.videoPlayer.nativeElement;
      video.load();
      this.videoError = false;
      this.videoLoaded = false;
    }
  }

  // Comment Functions
  onSubmitComment(): void {
    if (!this.commentForm.valid || !this.video?.id) return;

    this.submittingComment = true;
    const content = this.commentForm.value.content;

    if (this.editingCommentId) {
      this.commentService.updateComment(this.editingCommentId, content).pipe(
        catchError(error => {
          console.error('Error updating comment:', error);
          this.snackBar.open('Error updating comment', 'Close', { duration: 3000 });
          return of(null);
        }),
        takeUntil(this.destroy$)
      ).subscribe(response => {
        if (response?.data) {
          const index = this.comments.findIndex(c => c.id === this.editingCommentId);
          if (index !== -1) {
            this.comments[index] = response.data;
          }
          this.snackBar.open('Comment updated successfully', 'Close', { duration: 3000 });
          this.cancelEdit();
        }
        this.submittingComment = false;
      });
    } else {
      this.commentService.addComment(this.video.id, content).pipe(
        catchError(error => {
          console.error('Error adding comment:', error);
          this.snackBar.open('Error adding comment', 'Close', { duration: 3000 });
          return of(null);
        }),
        takeUntil(this.destroy$)
      ).subscribe(response => {
        if (response?.data) {
          this.comments.unshift(response.data);
          this.totalComments++;
          this.commentForm.reset();
          this.snackBar.open('Comment added successfully', 'Close', { duration: 3000 });
        }
        this.submittingComment = false;
      });
    }
  }

  editComment(comment: Comment): void {
    this.editingCommentId = comment.id;
    this.commentForm.patchValue({ content: comment.content });
  }

  cancelEdit(): void {
    this.editingCommentId = null;
    this.commentForm.reset();
  }

  deleteComment(commentId: number): void {
    if (!confirm('Are you sure you want to delete this comment?')) return;

    this.commentService.deleteComment(commentId).pipe(
      catchError(error => {
        console.error('Error deleting comment:', error);
        this.snackBar.open('Error deleting comment', 'Close', { duration: 3000 });
        return of(null);
      }),
      takeUntil(this.destroy$)
    ).subscribe(response => {
      if (response) {
        this.comments = this.comments.filter(c => c.id !== commentId);
        this.totalComments--;
        this.snackBar.open('Comment deleted successfully', 'Close', { duration: 3000 });
      }
    });
  }

  canEditComment(comment: Comment): boolean {
    return this.isLoggedIn && this.currentUser?.id === comment.owner.id;
  }

  formatTime(seconds: number): string {
    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = Math.floor(seconds % 60);
    return `${minutes}:${remainingSeconds.toString().padStart(2, '0')}`;
  }

  formatDate(date: Date): string {
    return new Date(date).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  }

  goToSignIn(): void {
    this.router.navigate(['/user/login']);
  }
}

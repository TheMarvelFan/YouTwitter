import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { VideoService } from '../service/video.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-add-video',
  standalone: false,
  templateUrl: './add-video.component.html',
  styleUrl: './add-video.component.css'
})
export class AddVideoComponent implements OnInit {
  videoForm: FormGroup;
  thumbnailPreview: string | null = null;
  isUploading = false;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<AddVideoComponent>,
    private videoService: VideoService,
    private snackBar: MatSnackBar
  ) {
    this.videoForm = this.fb.group({
      title: ['', [Validators.required]],
      description: ['', [Validators.required]],
      videoFile: [null, [Validators.required]],
      thumbnail: [null, [Validators.required]]
    });
  }

  ngOnInit(): void {}

  onVideoFileSelected(event: Event): void {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (file) {
      this.videoForm.patchValue({ videoFile: file });
    }
  }

  onThumbnailSelected(event: Event): void {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (file) {
      this.videoForm.patchValue({ thumbnail: file });

      // Create preview
      const reader = new FileReader();
      reader.onload = (e) => {
        this.thumbnailPreview = e.target?.result as string;
      };
      reader.readAsDataURL(file);
    }
  }

  onSubmit(): void {
    if (this.videoForm.valid) {
      this.isUploading = true;

      const formData = this.videoForm.value;

      this.videoService.uploadVideo(
        formData.title,
        formData.description,
        formData.videoFile,
        formData.thumbnail
      ).subscribe({
        next: (response) => {
          this.isUploading = false;
          this.snackBar.open('Video uploaded successfully!', 'Close', {
            duration: 3000,
            panelClass: ['success-snackbar']
          });
          this.videoService.refreshVideos();
          this.dialogRef.close(true);
        },
        error: (error) => {
          this.isUploading = false;
          this.snackBar.open(error.message || 'Error uploading video', 'Close', {
            duration: 3000,
            panelClass: ['error-snackbar']
          });
        }
      });
    }
  }

  onCancel(): void {
    this.dialogRef.close(false);
  }
}

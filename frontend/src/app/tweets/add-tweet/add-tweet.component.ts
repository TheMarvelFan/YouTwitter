import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { TweetsService } from '../service/tweets.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-add-tweet',
  standalone: false,
  templateUrl: './add-tweet.component.html',
  styleUrl: './add-tweet.component.css'
})
export class AddTweetComponent implements OnInit {
  tweetForm: FormGroup;
  characterCount = 0;
  isPosting = false;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<AddTweetComponent>,
    private tweetsService: TweetsService,
    private snackBar: MatSnackBar
  ) {
    this.tweetForm = this.fb.group({
      content: ['', [Validators.required, Validators.maxLength(280)]]
    });
  }

  ngOnInit(): void {}

  updateCharacterCount(): void {
    const content = this.tweetForm.get('content')?.value || '';
    this.characterCount = content.length;
  }

  onSubmit(): void {
    if (this.tweetForm.valid) {
      this.isPosting = true;

      const content = this.tweetForm.get('content')?.value;

      this.tweetsService.createTweet(content).subscribe({
        next: (response) => {
          this.isPosting = false;
          this.snackBar.open('Tweet posted successfully!', 'Close', {
            duration: 3000,
            panelClass: ['success-snackbar']
          });
          this.tweetsService.refreshTweets();
          this.dialogRef.close(true);
        },
        error: (error) => {
          this.isPosting = false;
          this.snackBar.open(error.message || 'Error posting tweet', 'Close', {
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

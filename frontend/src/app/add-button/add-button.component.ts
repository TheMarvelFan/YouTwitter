import {Component, OnInit} from '@angular/core';
import { UserService } from '../user/service/user.service';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { AddVideoComponent } from '../videos/add-video/add-video.component';
import { AddTweetComponent } from '../tweets/add-tweet/add-tweet.component';

@Component({
  selector: 'app-add-button',
  standalone: false,
  templateUrl: './add-button.component.html',
  styleUrl: './add-button.component.css'
})
export class AddButtonComponent implements OnInit {
  isLoggedIn = false;

  constructor(
    private userService: UserService,
    private router: Router,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.userService.isLoggedIn$.subscribe(loggedIn => {
      this.isLoggedIn = loggedIn;
    });
  }

  onAddClick(): void {
    if (!this.isLoggedIn) {
      this.router.navigate(['/user']);
    }
    // If logged in, the menu will open automatically due to matMenuTriggerFor
  }

  openVideoDialog(): void {
    const dialogRef = this.dialog.open(AddVideoComponent, {
      width: '600px',
      maxWidth: '90vw',
      panelClass: 'youtube-dark-mode'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.router.navigate(['/videos']);
      }
    });
  }

  openTweetDialog(): void {
    const dialogRef = this.dialog.open(AddTweetComponent, {
      width: '500px',
      maxWidth: '90vw',
      panelClass: 'youtube-dark-mode'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.router.navigate(['/tweets']);
      }
    });
  }
}

import { Component, Input } from '@angular/core';
import { User } from '../../models/User';
import { Router } from '@angular/router';

@Component({
  selector: 'app-video-card',
  standalone: false,
  templateUrl: './video-card.component.html',
  styleUrl: './video-card.component.css'
})
export class VideoCardComponent {
  @Input()
  thumbnail: string = "";

  @Input()
  title: string = "";

  @Input()
  duration: number = 0;

  @Input()
  owner: User = {
    id: 0,
    username: "",
    email: "",
    fullName: "",
    avatar: "",
    coverImage: "",
    password: "",
    refreshToken: "",
    createdAt: new Date(),
    updatedAt: new Date()
  };

  @Input()
  views: number | null = 0;

  @Input()
  createdAt: Date = new Date();

  @Input() videoId!: number;

  constructor(private router: Router) {}

  onCardClick(): void {
    this.router.navigate(['/videos', this.videoId]);
  }

  extractUsername(owner: User): string {
    if (owner && owner.username) {
      return owner.username;
    }
    return '';
  }

  timeAgo(input: Date | string): string {
    const inputDate = new Date(input);

    if (isNaN(inputDate.getTime())) {
      return "Invalid date";
    }

    const now = new Date();

    const diffMs = now.getTime() - inputDate.getTime();
    const diffSeconds = Math.floor(diffMs / 1000);
    const diffMinutes = Math.floor(diffSeconds / 60);
    const diffHours = Math.floor(diffMinutes / 60);
    const diffDays = Math.floor(diffHours / 24);
    const diffWeeks = Math.floor(diffDays / 7);
    const diffMonths = now.getMonth() - inputDate.getMonth() + 12 * (now.getFullYear() - inputDate.getFullYear());
    const diffYears = now.getFullYear() - inputDate.getFullYear();

    const isSameDay = inputDate.toDateString() === now.toDateString();
    const isSameWeek =
      now.getFullYear() === inputDate.getFullYear() &&
      getWeekNumber(now) === getWeekNumber(inputDate);
    const isSameMonth =
      now.getFullYear() === inputDate.getFullYear() &&
      now.getMonth() === inputDate.getMonth();
    const isSameYear = now.getFullYear() === inputDate.getFullYear();

    if (diffSeconds < 60) {
      return `Just now`;
    } else if (diffMinutes < 60) {
      return `${diffMinutes} minute${diffMinutes !== 1 ? "s" : ""} ago`;
    } else if (isSameDay) {
      return `${diffHours} hour${diffHours !== 1 ? "s" : ""} ago`;
    } else if (isSameWeek) {
      return `${diffDays !== 1 ? `${diffDays} days ago` : "Yesterday"}`;
    } else if (isSameMonth) {
      return `${diffWeeks !== 1 ? `${diffWeeks} weeks ago` : "Last week"}`;
    } else if (isSameYear) {
      return `${diffMonths !== 1 ? `${diffMonths} months ago` : "Last month"}`;
    } else {
      return `${diffYears !== 1 ? `${diffYears} years ago` : "Last year"}`;
    }

    function getWeekNumber(date: Date): number {
      const tempDate = new Date(Date.UTC(date.getFullYear(), date.getMonth(), date.getDate()));
      const dayNum = tempDate.getUTCDay() || 7;
      tempDate.setUTCDate(tempDate.getUTCDate() + 4 - dayNum);
      const yearStart = new Date(Date.UTC(tempDate.getUTCFullYear(), 0, 1));
      return Math.ceil((((tempDate.getTime() - yearStart.getTime()) / 86400000) + 1) / 7);
    }
  }

  formatViews(views: number | null): string {
    if (!views) {
      return "0";
    }

    if (views < 1000) return views.toString();

    const units = ["K", "M", "B"];
    let unitIndex = -1;
    let shortViews = views;

    while (shortViews >= 1000 && unitIndex < units.length - 1) {
      shortViews /= 1000;
      unitIndex++;
    }

    const rounded =
      shortViews < 10 ? shortViews.toFixed(1) : Math.floor(shortViews).toString();

    return `${rounded}${units[unitIndex]}`;
  }

  formatYouTubeDuration(seconds: number): string {
    const hrs = Math.floor(seconds / 3600);
    const mins = Math.floor((seconds % 3600) / 60);
    const secs = seconds % 60;

    const pad = (num: number) => String(num).padStart(2, '0');

    if (hrs > 0) {
      // Format: HH:MM:SS
      return `${pad(hrs)}:${pad(mins)}:${pad(secs)}`;
    } else {
      // Format: MM:SS (but MM is still padded to 2 digits)
      return `${pad(mins)}:${pad(secs)}`;
    }
  }

  extractAvatar(owner: User): string {
    if (owner && owner.avatar) {
      if (!owner.avatar) return '';
      // Remove the base part of the Cloudinary URL
      return owner.avatar.replace('http://res.cloudinary.com/dnxncx5sv/image/upload/', '');
    }
    return '';
  }
}

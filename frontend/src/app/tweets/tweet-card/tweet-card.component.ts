import {Component, Input} from '@angular/core';
import {User} from '../../models/User';

@Component({
  selector: 'app-tweet-card',
  standalone: false,
  templateUrl: './tweet-card.component.html',
  styleUrl: './tweet-card.component.css'
})
export class TweetCardComponent {
  @Input()
  content: string = "";

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
  }

  @Input()
  createdAt: Date = new Date();

  extractAvatar(owner: User): string {
    if (owner && owner.avatar) {
      if (!owner.avatar) return '';
      // Remove the base part of the Cloudinary URL
      return owner.avatar.replace('http://res.cloudinary.com/dnxncx5sv/image/upload/', '');
    }
    return '';
  }

  extractFullName(owner: User): string {
    if (owner && owner.fullName) {
      return owner.fullName;
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
      return `${diffDays > 1 ? `${diffDays} days ago` : "Yesterday"}`;
    } else if (isSameMonth) {
      return `${diffWeeks > 1 ? `${diffWeeks} weeks ago` : "Last week"}`;
    } else if (isSameYear) {
      return `${diffMonths > 1 ? `${diffMonths} months ago` : "Last month"}`;
    } else {
      return `${diffYears > 1 ? `${diffYears} years ago` : "Last year"}`;
    }

    function getWeekNumber(date: Date): number {
      const tempDate = new Date(Date.UTC(date.getFullYear(), date.getMonth(), date.getDate()));
      const dayNum = tempDate.getUTCDay() || 7;
      tempDate.setUTCDate(tempDate.getUTCDate() + 4 - dayNum);
      const yearStart = new Date(Date.UTC(tempDate.getUTCFullYear(), 0, 1));
      return Math.ceil((((tempDate.getTime() - yearStart.getTime()) / 86400000) + 1) / 7);
    }
  }

  isLiked = false;
  likeCount = 0;

  toggleLike(): void {
    this.isLiked = !this.isLiked;
    this.likeCount += this.isLiked ? 1 : -1;
  }

  extractUsername(owner: User): string {
    if (owner && owner.username) {
      return `@${owner.username}`;
    }
    return '';
  }
}

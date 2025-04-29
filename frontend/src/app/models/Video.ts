import { User } from './User';

export interface Video {
  id: number;
  title: string;
  description: string;
  video_file: string;
  thumbnail: string;
  published: boolean;
  duration: number; // seconds
  views: number | null;
  createdAt: Date;
  updatedAt: Date;
  owner: User;
}

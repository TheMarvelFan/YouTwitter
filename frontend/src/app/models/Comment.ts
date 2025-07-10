import { User } from './User';
import { Video } from './Video';

export interface Comment {
  id: number;
  content: string;
  video: Video;
  owner: User;
  createdAt: Date;
  updatedAt: Date;
}

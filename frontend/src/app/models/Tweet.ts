import { User } from './User';

export interface Tweet {
  id: number;
  content: string;
  owner: User;
  createdAt: Date;
  updatedAt: Date;
}

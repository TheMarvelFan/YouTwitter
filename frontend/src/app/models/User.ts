export interface User {
  id: number;
  username: string;
  email: string;
  fullName: string;
  avatar: string;
  coverImage: string;
  password: string;
  refreshToken: string;
  createdAt: Date; // ISO date string
  updatedAt: Date; // ISO date string
}

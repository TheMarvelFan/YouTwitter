import { Video } from './Video';

export interface VideoResponse {
  message: string;
  data: {
    content: Video[];
    pageable: any;
    totalElements: number;
    totalPages: number;
    size: number;
    number: number;
  };
  statusCode: number;
  success: boolean;
}

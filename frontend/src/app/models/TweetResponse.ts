import { Tweet } from './Tweet';

export interface TweetResponse {
  message: string;
  data: {
    content: Tweet[];
    pageable: any;
    totalElements: number;
    totalPages: number;
    size: number;
    number: number;
  };
  statusCode: number;
  success: boolean;
}

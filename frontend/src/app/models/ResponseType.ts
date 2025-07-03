export interface ResponseType<T> {
  message: string;
  data: T;
  statusCode: number;
}

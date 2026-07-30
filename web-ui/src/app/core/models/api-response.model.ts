export interface ApiResponse<T> {
  status?: number | string;
  message: string;
  data: T;
}

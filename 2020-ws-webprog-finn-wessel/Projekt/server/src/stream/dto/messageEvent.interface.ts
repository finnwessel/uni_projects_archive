export interface MessageEvent {
  data: string;
  id?: string;
  event?: string;
  retry?: number;
}

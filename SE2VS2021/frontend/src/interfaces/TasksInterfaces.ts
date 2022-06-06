export interface NewTask {
  title: string;
  index: number;
  untilDate: string;
  description: string;
}

export interface Task extends NewTask {
  id: string;
}

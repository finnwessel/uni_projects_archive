import { Task } from "@/interfaces/TasksInterfaces";

export interface NewRegister {
  title: string;
  index: number;
}

export interface Register extends NewRegister {
  id: string;
  tasks: Task[];
}

export interface SwitchedIndex {
  id: string;
  index: number;
}

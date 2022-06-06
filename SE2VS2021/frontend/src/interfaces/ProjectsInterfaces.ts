export interface NewProject {
  title: string;
  index: number;
}
export interface Project extends NewProject {
  id: string;
}

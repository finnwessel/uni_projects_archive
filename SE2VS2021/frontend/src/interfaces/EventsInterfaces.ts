export interface NewEvent {
  title: string;
  start: string;
  end: string;
  allDay: boolean;
  description: string;
  ownerId: string;
}

export interface Event extends NewEvent {
  id: string;
}

import { defineStore } from "pinia";
import EventService from "@/services/EventService";
import { Event, NewEvent } from "@/interfaces/EventsInterfaces";
import { useAuthStore } from "@/store/AuthStore";

export type EventsState = {
  events: Event[];
  invitations: Event[];
};

export const useEventsStore = defineStore({
  id: "events",
  state: () =>
    ({
      events: [],
      invitations: [],
    } as EventsState),

  actions: {
    addEvent(newEvent: NewEvent) {
      return new Promise((resolve, reject) => {
        EventService.add(newEvent)
          .then((id) => {
            const event: Event = {
              id,
              ownerId: useAuthStore().user.id,
              start: newEvent.start,
              end: newEvent.end,
              title: newEvent.title,
              allDay: newEvent.allDay,
              description: newEvent.description,
            };
            this.events = [...this.events].concat(event);
            resolve(event);
          })
          .catch((err) => reject(err));
      });
    },
    updateEvent(event: Event) {
      event.ownerId = useAuthStore().user.id;
      return new Promise((resolve, reject) => {
        EventService.update(event)
          .then((res) => {
            const newArray = this.events.filter((e) => e.id !== res.id);
            this.events = newArray.concat(event);
            resolve(true);
          })
          .catch((err) => reject(err));
      });
    },
    removeEvent(event: Event) {
      return new Promise((resolve, reject) => {
        EventService.delete(event.id)
          .then((r) => {
            this.events = this.events.filter((e) => e.id !== r.id);
            resolve(true);
          })
          .catch((err) => reject(err));
      });
    },
    acceptInvitation(eventId) {
      return new Promise((resolve, reject) => {
        EventService.acceptInvitation(eventId)
          .then((r) => {
            this.invitations = this.invitations.filter((e) => e.id !== r);
            resolve(true);
          })
          .catch((err) => reject(err));
      });
    },
    declineInvitation(eventId) {
      return new Promise((resolve, reject) => {
        EventService.deleteInvitation(eventId)
          .then((r) => {
            this.invitations = this.invitations.filter((e) => e.id !== r);
            resolve(true);
          })
          .catch((err) => reject(err));
      });
    },
    loadEvents() {
      EventService.get().then((res) => {
        this.events = res;
      });
    },
    loadInvitations() {
      EventService.getInvitations().then((res) => {
        this.invitations = res;
      });
    },
  },
  getters: {
    getEvents(state) {
      return state.events;
    },
    getInvitations(state) {
      return state.invitations;
    },
  },
});

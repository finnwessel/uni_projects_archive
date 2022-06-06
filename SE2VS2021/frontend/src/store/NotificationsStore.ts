import { defineStore } from "pinia";
import { useAuthStore } from "@/store/AuthStore";
import { NotificationEvent } from "@/interfaces/NotificationsInterfaces";

export type NotificationState = {
  eventSource: EventSource;
  notificationEvents: NotificationEvent[];
  notification;
};

export const useNotificationsStore = defineStore({
  id: "notification",
  persistedState: { persist: false },
  state: () =>
    ({
      eventSource: null,
      notification: {},
      notificationEvents: [
        {
          type: "EVENT:INVITE",
          title: "Event invitation",
        },
      ],
    } as NotificationState),

  actions: {
    initEventSource() {
      const token = useAuthStore().getUser.token;
      if (token == null) {
        this.evenSource = null;
      } else if (this.eventSource == null) {
        this.eventSource = new EventSource(
          `${process.env.VUE_APP_NOTIFICATIONS_SERVICE_URL}/messages/subscribe?token=${token}`
        );
        this.addListeners();
      }
    },
    addListeners() {
      this.notificationEvents.forEach((e: NotificationEvent) => {
        this.eventSource.addEventListener(e.type, (event) => {
          const data = JSON.parse(event.data);
          this.notification = { title: e.title, message: data.message };
        });
      });

      this.eventSource.onerror = (err) => {
        console.log("EventSource error: ", err);
      };
    },
  },

  getters: {
    getNotification(state) {
      return state.notification;
    },
  },
});

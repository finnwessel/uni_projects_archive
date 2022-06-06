import axios from "axios";
import { Event, NewEvent } from "@/interfaces/EventsInterfaces";

const api = process.env.VUE_APP_EVENT_SERVICE_URL;

export default {
  add(event: NewEvent): Promise<string> {
    return new Promise((resolve, reject) => {
      axios
        .put(api, event)
        .then((res) => {
          if (res.status == 200) {
            resolve(res.data);
          } else {
            reject();
          }
        })
        .catch((err) => reject(err));
    });
  },
  delete(eventId: string): Promise<Event> {
    return new Promise((resolve, reject) => {
      axios
        .delete(api, {
          params: {
            eventId,
          },
        })
        .then((res) => {
          if (res.status == 200) {
            resolve(res.data);
          } else {
            reject();
          }
        })
        .catch((err) => reject(err));
    });
  },
  update(event: Event): Promise<Event> {
    return new Promise((resolve, reject) => {
      axios
        .post(api, event)
        .then((res) => {
          if (res.status == 200) {
            resolve(res.data);
          } else {
            reject();
          }
        })
        .catch((err) => reject(err));
    });
  },
  get(from: string = null, to: string = null): Promise<Event[]> {
    let params;
    switch (true) {
      case from == null && to == null:
        params = {};
        break;
      case from != null && to == null:
        params = { from };
        break;
      default:
        params = {
          from,
          to,
        };
    }
    return new Promise((resolve, reject) => {
      axios
        .get(api, {
          params,
        })
        .then((res) => {
          if (res.status == 200) {
            resolve(res.data);
          } else {
            reject();
          }
        })
        .catch((err) => reject(err));
    });
  },
  sendInvitation(contactId: string, eventId: string): Promise<string> {
    return new Promise((resolve, reject) => {
      axios
        .put(api.concat("invites"), null, {
          params: {
            contactId,
            eventId,
          },
        })
        .then((res) => {
          if (res.status == 200) {
            resolve(res.data);
          } else {
            reject();
          }
        })
        .catch((err) => reject(err));
    });
  },
  getInvitations(): Promise<Event[]> {
    return new Promise((resolve, reject) => {
      axios
        .get(api.concat("invites"))
        .then((res) => {
          if (res.status == 200) {
            resolve(res.data);
          } else {
            reject();
          }
        })
        .catch((err) => reject(err));
    });
  },
  deleteInvitation(eventId: string): Promise<string> {
    return new Promise((resolve, reject) => {
      axios
        .delete(api.concat("invites"), {
          params: {
            eventId: eventId,
          },
        })
        .then((res) => {
          if (res.status == 200) {
            resolve(res.data);
          } else {
            reject();
          }
        })
        .catch((err) => reject(err));
    });
  },
  acceptInvitation(eventId: string): Promise<string> {
    return new Promise((resolve, reject) => {
      axios
        .post(api.concat("invites"), null, {
          params: {
            eventId: eventId,
          },
        })
        .then((res) => {
          if (res.status == 200) {
            resolve(res.data);
          } else {
            reject();
          }
        })
        .catch((err) => resolve(err));
    });
  },
};

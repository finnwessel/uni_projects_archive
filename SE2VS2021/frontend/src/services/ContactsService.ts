import axios from "axios";
import { Contact, NewContact } from "@/interfaces/ContactsInterfaces";

const api = process.env.VUE_APP_CONTACTS_SERVICE_URL;

export default {
  add(contact: NewContact): Promise<string> {
    return new Promise((resolve, reject) => {
      axios
        .post(api, contact)
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
  update(contact: Contact): Promise<Contact> {
    return new Promise((resolve, reject) => {
      axios
        .put(api, contact)
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
  delete(contactId: string): Promise<Contact> {
    return new Promise((resolve, reject) => {
      axios
        .delete(api, {
          params: {
            contactId,
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
  get(): Promise<Contact[]> {
    return new Promise((resolve, reject) => {
      axios
        .get(api)
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
  getSearch(query: string): Promise<Contact[]> {
    return new Promise((resolve, reject) => {
      axios
        .get(api + "/search", {
          params: {
            query,
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
  addUser(contactId: string): Promise<Contact> {
    return new Promise((resolve, reject) => {
      axios
        .post(api + `/${contactId}`)
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
};

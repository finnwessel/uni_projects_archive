import { Contact, NewContact } from "@/interfaces/ContactsInterfaces";
import { defineStore } from "pinia";
import ContactsService from "@/services/ContactsService";

export type ContactsState = {
  contacts: Contact[];
  searchedContacts: Contact[];
};

export const useContactsStore = defineStore({
  id: "contacts",
  state: () =>
    ({
      contacts: [],
      searchedContacts: [],
    } as ContactsState),

  actions: {
    addContact(newContact: NewContact): Promise<Contact> {
      return new Promise((resolve, reject) => {
        ContactsService.add(newContact)
          .then((id) => {
            const contact: Contact = {
              id,
              contactId: newContact.contactId,
              firstname: newContact.firstname,
              lastname: newContact.lastname,
              birthday: newContact.birthday,
              email: newContact.email,
              phoneNumber: newContact.phoneNumber,
              avatarId: "",
              addresses: [],
            };
            this.contacts = [...this.contacts].concat(contact);
            resolve(contact);
          })
          .catch((err) => reject(err));
      });
    },
    addUserContact(contactId: string): Promise<Contact> {
      return new Promise((resolve, reject) => {
        ContactsService.addUser(contactId)
          .then((c) => {
            const contact: Contact = {
              id: c.id,
              contactId: c.contactId,
              firstname: c.firstname,
              lastname: c.lastname,
              birthday: c.birthday,
              email: c.email,
              phoneNumber: c.phoneNumber,
              avatarId: "",
              addresses: [],
            };
            this.contacts = [...this.contacts].concat(contact);
            this.searchedContacts = this.searchedContacts.filter(
              (i) => i.id !== c.contactId
            );
            resolve(contact);
          })
          .catch((err) => reject(err));
      });
    },
    search(query: string): Promise<Contact[]> {
      return new Promise((resolve, reject) => {
        ContactsService.getSearch(query)
          .then((contacts) => {
            this.searchedContacts = contacts;
            resolve(contacts);
          })
          .catch((err) => reject(err));
      });
    },
    async loadContacts() {
      this.contacts = await ContactsService.get();
    },
    clearSearched() {
      this.searchedContacts = [];
    },
  },
  getters: {
    getContacts(state): Contact[] {
      return state.contacts;
    },
    getSearchedContacts(state): Contact[] {
      return state.searchedContacts;
    },
  },
});

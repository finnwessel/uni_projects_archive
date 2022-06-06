<template>
  <div class="grid">
    <div class="col-fixed pl-0">
      <Listbox
        v-model="selectedContact"
        :options="contactsStore.getContacts"
        :filter="true"
        filterMatchMode="contains"
        :filter-fields="searchFields"
        optionLabel="id"
        listStyle="max-height: 50vh"
        style="width: 20rem"
      >
        <template #option="contact">
          <div class="flex align-items-center">
            <Avatar
              v-if="!contact.option.avatarId"
              :label="contact.option.firstname[0]"
              class="p-mr-2"
              size="large"
              shape="circle"
            />
            <Avatar
              v-if="contact.option.avatarId"
              :image="contact.option.avatarId"
              class="p-mr-2"
              size="large"
              shape="circle"
            />
            <div class="flex flex-column align-items-start ml-2">
              <div class="font-bold">
                {{ contact.option.firstname }} {{ contact.option.lastname }}
              </div>
              <div class="email font-light">
                {{ contact.option.email }}
              </div>
            </div>
          </div>
        </template>
        <template #footer>
          <div class="flex justify-content-center">
            <Button class="m-2" @click="openModal"
              >Add Contact
              <i class="pi pi-plus pl-2" />
            </Button>
          </div>
        </template>
      </Listbox>
    </div>
    <div class="col justify-content-center align-content-start m-0 px-0">
      <div class="flex flex-column">
        <ContactInfo
          v-if="selectedContact != null"
          v-bind:contact="selectedContact"
        />
      </div>
    </div>
    <div>
      <AddContactModal
        v-bind:display="modalIsVisible"
        v-bind:contacStore="contactsStore"
        v-bind:isSearching="isSearching"
        @close="modalIsVisible = $event"
        v-on:search="search($event)"
      />
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import ContactInfo from "@/components/contacts/ContactInfo.vue";
import { useContactsStore } from "@/store/ContactsStore";
import { NewContact } from "@/interfaces/ContactsInterfaces";
import AddContactModal from "@/components/contacts/AddContactModal.vue";

export default defineComponent({
  name: "Contacts",
  components: { AddContactModal, ContactInfo },
  setup() {
    const contactsStore = useContactsStore();
    contactsStore.loadContacts();

    let selectedContactWithAvatar = null;

    /*watch(
      () => selectedContact.avatarid,
      (Id, prevId) => {
        selectedContact.avatar = FileService.getFilePath(Id);
      }
    );*/

    return {
      contactsStore,
      selectedContactWithAvatar,
    };
  },
  data() {
    return {
      searchFields: ["firstname", "lastname"],
      selectedContact: null,
      modalIsVisible: false,
      isSearching: false,
    };
  },
  beforeMount() {
    this.$emitter.on("contacts:add", (contactId) => {
      this.contactsStore.addUserContact(contactId);
    });
  },
  unmounted() {
    this.$emitter.off("contacts:add");
  },
  methods: {
    search(query) {
      this.isSearching = true;
      this.contactsStore
        .search(query)
        .then(() => (this.isSearching = false))
        .catch(() => (this.isSearching = false));
    },
    openModal() {
      this.contactsStore.clearSearched();
      this.modalIsVisible = true;
    },
    add() {
      const contact = {
        contactId: null,
        firstname: "Harald",
        lastname: "Lesch",
        birthday: new Date(2099, 6, 21).toISOString(),
        email: "x@x.xxx",
        phoneNumber: "1",
        avatarId: "",
        addresses: [],
      } as NewContact;
      this.contactsStore.addContact(contact);
    },
  },
  /*computed: {
    avatar(): string {
      return FileService.getFilePath(this.selectedContact.avatarid);
    },
  },*/
});
</script>

<style scoped></style>

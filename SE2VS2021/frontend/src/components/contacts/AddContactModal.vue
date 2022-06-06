<template>
  <Dialog
    modal
    @close="$emit('close')"
    :visible="display"
    @update:visible="$emit('close', $event)"
  >
    <template #header>
      <span><b>Add new contacts</b></span>
    </template>

    <span class="p-input-icon-right py-2">
      <i class="pi pi-spin pi-spinner" v-if="isSearching" />
      <InputText
        type="text"
        class="p-inputtext-lg"
        placeholder="Firstname, Lastname"
        v-model="query"
      />
    </span>

    <ContactItem
      v-for="contact in contactsStore.getSearchedContacts"
      :key="contact.id"
      v-bind:contact="contact"
    />

    <h2 v-if="contactsStore.getSearchedContacts.length === 0">
      No results found.
    </h2>

    <template #footer>
      <Button
        @click="emitCancel"
        label="Cancel"
        icon="pi pi-times"
        class="p-button-text"
      />
    </template>
  </Dialog>
</template>

<script>
import { defineComponent } from "vue";
import { useContactsStore } from "@/store/ContactsStore";
import ContactItem from "@/components/contacts/ContactItem";

export default defineComponent({
  name: "AddContactModal",
  components: { ContactItem },
  setup() {
    const contactsStore = useContactsStore();
    return {
      contactsStore,
    };
  },
  data() {
    return {
      query: "",
    };
  },
  props: {
    display: {
      type: Boolean,
      default: false,
    },
    isSearching: {
      type: Boolean,
      default: false,
    },
  },
  watch: {
    display() {
      this.query = "";
    },
    query() {
      let value = this.query;
      setTimeout(() => {
        if (this.query === value) {
          if (this.query !== "") {
            this.$emit("search", this.query);
          }
        }
      }, 1000);
    },
  },
  methods: {
    emitCancel() {
      this.contactsStore.clearSearched();
      this.$emit("close");
    },
  },
});
</script>

<style scoped></style>

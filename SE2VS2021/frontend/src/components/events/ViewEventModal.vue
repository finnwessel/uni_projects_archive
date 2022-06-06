<template>
  <div>
    <Dialog
      @close="$emit('close')"
      :visible="display"
      @update:visible="$emit('close', $event)"
    >
      <template #header>
        <span><b>View Event</b></span>
      </template>

      <div class="grid p-fluid align-items-center mt-1">
        <div class="col-4">
          <div class="p-inputgroup">
            <span class="p-inputgroup-addon"> Title </span>
            <InputText id="title" v-model="event.title" autoFocus />
          </div>
        </div>

        <div class="col-6">
          <MultiSelect
            v-model="selectedContacts"
            :options="contactsStore.getContacts"
            optionLabel="firstname"
            placeholder="Select contacts"
            :filter="true"
            class="multiselect-custom"
          >
            <template #value="slotProps">
              <div class="flex">
                <div v-for="option of slotProps.value" :key="option.id">
                  <div class="flex ml-2">
                    <div class="bg-primary px-2 py-1 border-round font-bold">
                      <Avatar
                        v-if="!option.avatarId"
                        :label="option.firstname[0]"
                        size="small"
                        shape="circle"
                      />
                      <Avatar
                        v-if="option.avatarId"
                        :image="option.avatarId"
                        size="small"
                        shape="circle"
                      />
                      {{ option.firstname }}
                      {{ option.lastname }}
                    </div>
                  </div>
                </div>
              </div>
              <template v-if="!slotProps.value || slotProps.value.length === 0">
                Select Contacts to invite
              </template>
            </template>
            <template #option="slotProps">
              <div class="flex align-items-center">
                <Avatar
                  v-if="!slotProps.option.avatarId"
                  :label="slotProps.option.firstname[0]"
                  class="p-mr-2"
                  size="large"
                  shape="circle"
                />
                <Avatar
                  v-if="slotProps.option.avatarId"
                  :image="slotProps.option.avatarId"
                  class="p-mr-2"
                  size="large"
                  shape="circle"
                />
                <div class="flex align-items-start ml-2">
                  <div class="font-bold">
                    {{ slotProps.option.firstname }}
                    {{ slotProps.option.lastname }}
                  </div>
                </div>
              </div>
            </template>
          </MultiSelect>
        </div>
        <div class="col-2">
          <Button
            @click="inviteContacts"
            class="p-button-info"
            label="Invite"
            icon="pi pi-check"
          />
        </div>

        <div class="col-12 md:col-6">
          <div class="p-inputgroup">
            <span class="p-inputgroup-addon"> Start </span>
            <Calendar
              id="startDate"
              v-model="event.start"
              :showTime="true"
              hourFormat="24"
            />
          </div>
        </div>

        <div class="col-12 md:col-6">
          <div class="p-inputgroup">
            <span class="p-inputgroup-addon"> End </span>
            <Calendar
              id="endDate"
              v-model="event.end"
              :showTime="true"
              hourFormat="24"
            />
          </div>
        </div>

        <div class="col-12">
          <Textarea
            id="description"
            v-model="event.description"
            :autoResize="true"
            rows="5"
            cols="30"
            placeholder="Description"
          />
        </div>
      </div>

      <template #footer>
        <div class="grid justify-content-between">
          <div>
            <Button
              @click="confirmDelete($event)"
              label="Delete"
              icon="pi pi-trash"
              class="p-button-danger"
            />
            <Button
              @click="openLogModal"
              label="Logs"
              icon="pi pi-book"
              class="p-button-text"
            />
          </div>

          <div>
            <Button
              @click="emitCancel"
              label="Cancel"
              icon="pi pi-times"
              class="p-button-text"
            />
            <Button
              @click="emitSave"
              label="Save"
              icon="pi pi-check"
              autofocus
            />
          </div>
        </div>
      </template>
    </Dialog>
    <ConfirmPopup></ConfirmPopup>
  </div>
</template>

<script lang="ts">
import { defineComponent, PropType } from "vue";
import Calendar from "primevue/calendar";
import Textarea from "primevue/textarea";
import Avatar from "primevue/avatar";
import ConfirmPopup from "primevue/confirmpopup";
import { useContactsStore } from "@/store/ContactsStore";
import { Contact } from "@/interfaces/ContactsInterfaces";
import { Event } from "@/interfaces/EventsInterfaces";

export default defineComponent({
  name: "ViewEventModal",
  components: {
    Calendar,
    Textarea,
    Avatar,
    ConfirmPopup,
  },
  setup() {
    const contactsStore = useContactsStore();
    contactsStore.loadContacts();
    return {
      contactsStore,
    };
  },
  data() {
    return {
      selectedContacts: null,
      event: {
        ownerId: null,
        start: null,
        end: null,
        title: null,
        description: null,
        allDay: false,
      } as Event,
    };
  },
  props: {
    display: {
      type: Boolean,
      default: false,
    },
    selectedEvent: {
      type: Object as PropType<Event>,
      default() {
        return {
          id: null,
          ownerId: null,
          start: null,
          end: null,
          title: null,
          description: null,
        };
      },
    },
  },
  watch: {
    selectedEvent(e) {
      this.event.id = e.id;
      this.event.ownerId = e.ownerId;
      this.event.start = e.start;
      this.event.end = e.end;
      this.event.title = e.title;
      this.event.description = e.extendedProps.description;
      this.event.allDay = e.allDay;
    },
  },
  methods: {
    emitSave() {
      this.$emit("save", this.event);
      this.$emit("close");
    },
    emitCancel() {
      this.event = {
        id: null,
        ownerId: null,
        start: null,
        end: null,
        title: null,
        description: null,
        allDay: false,
      } as Event;
      this.$emit("close");
    },
    inviteContacts() {
      this.selectedContacts.forEach((c: Contact) => {
        this.$emitter.emit(
          "event:send-invite",
          c.contactId,
          this.selectedEvent.id
        );
      });
      this.selectedContacts = null;
    },
    confirmDelete(event) {
      this.$confirm.require({
        target: event.currentTarget,
        message: "Do you want to delete this record?",
        header: "Delete Confirmation",
        icon: "pi pi-info-circle",
        acceptClass: "p-button-danger",
        accept: () => {
          this.$toast.add({
            severity: "success",
            summary: "Confirmed",
            detail: "Record deleted",
            life: 3000,
          });
          this.$emit("delete", this.selectedEvent);
        },
        reject: () => {
          this.$toast.add({
            severity: "error",
            summary: "Rejected",
            detail: "You have rejected",
            life: 3000,
          });
        },
      });
    },
    openLogModal() {
      this.$emitter.emit("log:open", this.event.id);
    },
  },
});
</script>

<style scoped></style>

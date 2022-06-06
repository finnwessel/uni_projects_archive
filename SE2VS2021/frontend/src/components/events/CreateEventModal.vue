<template>
  <Dialog
    @close="$emit('close')"
    :visible="display"
    @update:visible="$emit('close', $event)"
  >
    <template #header>
      <span><b>Add new Event</b></span>
    </template>

    <div class="grid p-fluid">
      <div class="col-12">
        <div class="p-inputgroup">
          <span class="p-inputgroup-addon"> Title </span>
          <InputText id="title" v-model="event.title" autoFocus />
        </div>
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
      <Button
        @click="emitCancel"
        label="Cancel"
        icon="pi pi-times"
        class="p-button-text"
      />
      <Button @click="emitSave" label="Save" icon="pi pi-check" autofocus />
    </template>
  </Dialog>
</template>

<script>
import { defineComponent } from "vue";
import Calendar from "primevue/calendar";
import Textarea from "primevue/textarea";

export default defineComponent({
  name: "CreateEventModal",
  components: {
    Calendar,
    Textarea,
  },
  data() {
    return {
      event: {
        ownerId: null,
        start: null,
        end: null,
        title: null,
        description: null,
        allDay: false,
      },
    };
  },
  props: {
    display: {
      type: Boolean,
      default: false,
    },
    selectInfo: {
      type: Object,
    },
  },
  watch: {
    selectInfo(i) {
      this.event.start = i.start;
      this.event.end = i.end;
      this.event.allDay = i.allDay;
    },
  },
  methods: {
    emitSave() {
      this.$emit("save", this.event);
      this.$emit("close");
    },
    emitCancel() {
      this.event = {
        ownerId: null,
        start: null,
        end: null,
        title: null,
        description: null,
        allDay: false,
      };
      this.$emit("close");
    },
  },
});
</script>

<style scoped></style>

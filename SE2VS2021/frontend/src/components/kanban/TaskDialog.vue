<template>
  <div>
    <Dialog :visible="display" @update:visible="$emit('close', $event)">
      <template #header>
        <span><b>View Task</b></span>
      </template>

      <div class="grid p-fluid">
        <div class="col-12 md:col-12">
          <div class="p-inputgroup">
            <span class="p-inputgroup-addon"> Title </span>
            <InputText id="title" v-model="task.title" />
          </div>
        </div>

        <div class="col-12 md:col-12">
          <div class="p-inputgroup">
            <span class="p-inputgroup-addon"> Until </span>
            <Calendar
              id="endDate"
              v-model="task.untilDate"
              :showTime="true"
              hourFormat="24"
            />
          </div>
        </div>

        <div class="col-12">
          <Textarea
            id="description"
            v-model="task.description"
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
              @click="emitClose"
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
import { defineComponent } from "vue";
import Textarea from "primevue/textarea";
import Calendar from "primevue/calendar";
import ConfirmPopup from "primevue/confirmpopup";
import { Task } from "@/interfaces/TasksInterfaces";

export default defineComponent({
  name: "TaskDialog",
  components: {
    Textarea,
    Calendar,
    ConfirmPopup,
  },
  data() {
    return {
      task: {
        id: null,
        untilDate: null,
        title: null,
      } as Task,
    };
  },
  props: {
    selectedTask: {
      type: Object,
      default: () => ({
        registerId: null,
        task: {
          id: null,
          untilDate: null,
          title: null,
          content: null,
        },
      }),
    },
    display: {
      type: Boolean,
      default: false,
    },
  },
  watch: {
    selectedTask(t) {
      this.task.id = t.task.id;
      this.task.untilDate = t.task.untilDate;
      this.task.title = t.task.title;
      this.task.description = t.task.description;
    },
  },
  methods: {
    emitClose() {
      this.$emit("close");
    },
    emitSave() {
      this.$emit("save", this.selectedTask.registerId, this.task);
    },
    confirmDelete(event) {
      this.$confirm.require({
        target: event.currentTarget,
        message: "Do you want to delete this task?",
        header: "Delete Confirmation",
        icon: "pi pi-info-circle",
        acceptClass: "p-button-danger",
        accept: () => {
          this.$toast.add({
            severity: "info",
            summary: "Confirmed",
            detail: "Record deleted",
            life: 3000,
          });
          this.$emit("delete", this.selectedTask);
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
      this.$emitter.emit("log:open", this.selectedTask.task.id);
    },
  },
});
</script>

<style scoped></style>

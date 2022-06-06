<template>
  <div
    class="
      mb-1
      p-2
      hover:surface-400
      bg-white-alpha-20
      border-round
      cursor-pointer
    "
    @click="clickedTask"
  >
    <h3 class="m-0">{{ formattedTitle(task.title) }}</h3>
    <p>{{ formattedTitle(task.description) }}</p>
  </div>
</template>

<script lang="ts">
import { defineComponent, PropType } from "vue";
import { Task } from "@/interfaces/TasksInterfaces";

export default defineComponent({
  name: "TaskItem",
  props: {
    registerId: {
      default() {
        return null;
      },
    },
    task: {
      type: Object as PropType<Task>,
    },
  },
  methods: {
    clickedTask(): void {
      this.$emitter.emit("kanban:clicked-task", {
        registerId: this.registerId,
        task: this.task,
      });
    },
    formattedTitle(title: string): string {
      let allowedLength = 30;
      if (title.length < allowedLength) {
        return title;
      } else {
        return title.substring(0, allowedLength).concat("...");
      }
    },
  },
});
</script>

<style scoped></style>

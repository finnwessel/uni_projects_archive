<template>
  <div class="flex">
    <draggable
      :list="registers"
      :animation="200"
      item-key="id"
      class="flex"
      tag="transition-group"
      :component-data="{
        tag: 'ul',
        type: 'transition-group',
        name: !dragging ? 'flip-list' : null,
      }"
      handle=".move-handle"
      @change="changedList"
      @end="dragging = false"
      @start="dragging = true"
    >
      <template #item="{ element }">
        <Register :register="element"></Register>
      </template>
    </draggable>
    <div class="flex-none">
      <InputText
        class="my-3 ml-2"
        placeholder="Add new register"
        v-model="newListName"
        v-on:keyup.enter="addNewList"
      ></InputText>
    </div>
  </div>
</template>

<script lang="ts">
import draggable from "vuedraggable";
import { defineComponent } from "vue";
import Register from "@/components/kanban/Register.vue";

export default defineComponent({
  name: "RegisterContainer",
  props: ["registers"],
  components: {
    Register,
    draggable,
  },
  data() {
    return {
      dragging: false,
      newListName: "",
    };
  },
  methods: {
    changedList(evt: any): void {
      if (evt.moved != undefined) {
        this.$emitter.emit("kanban:moved-list");
      } else if (evt.added != undefined) {
        this.$emitter.emit("kanban:added-list", evt);
      } else if (evt.removed != undefined) {
        this.$emitter.emit("kanban:removed-list", evt);
      } else {
        console.log("Unhandled Event");
      }
    },
    addNewList(): void {
      this.$emitter.emit("kanban:created-list", this.newListName);
      this.newListName = "";
    },
  },
});
</script>

<style scoped></style>

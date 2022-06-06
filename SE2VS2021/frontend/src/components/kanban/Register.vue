<template>
  <Card class="mx-2 move-handle bg-white-alpha-20">
    <template #title>
      <div class="flex justify-content-between">
        <div class="flex">
          <Inplace :closable="true" :key="register.id">
            <template #display>
              <span class="text-2xl font-bold">{{
                register.title || "No name"
              }}</span>
            </template>
            <template #content>
              <InputText
                @keyup.enter="renameListName"
                style="width: 100px"
                v-model="registerTitle"
                autoFocus
              />
            </template>
          </Inplace>
        </div>
        <div class="flex">
          <Button
            icon="pi pi-ellipsis-h"
            class="
              text-blue-50
              p-button-sm p-button-rounded
              bg-transparent
              border-white
            "
            @click="showMenu"
          ></Button>
        </div>
      </div>
      <ContextMenu ref="menu" :model="menuItems"> </ContextMenu>
    </template>
    <template #content>
      <draggable
        :list="register.tasks"
        group="tasks"
        item-key="id"
        :animation="200"
        tag="transition-group"
        :component-data="{
          tag: 'div',
          type: 'transition-group',
          name: !dragging ? 'flip-list' : null,
        }"
        @end="dragging = false"
        @start="dragging = true"
        @change="changedTask"
      >
        <template #item="{ element }">
          <TaskItem :registerId="register.id" :task="element"></TaskItem>
        </template>
      </draggable>
    </template>
    <template #footer>
      <div class="p-fluid">
        <InputText
          placeholder="New task"
          v-model="taskTitle"
          v-on:keyup.enter="addNewTask"
        ></InputText>
      </div>
    </template>
  </Card>
</template>

<script lang="ts">
import Inplace from "primevue/inplace";
import draggable from "vuedraggable";
import TaskItem from "@/components/kanban/TaskItem.vue";
import { defineComponent, PropType } from "vue";
import { useTasksStore } from "@/store/TasksStore";
import { Register } from "@/interfaces/RegistersInterfaces";

export default defineComponent({
  name: "Register",
  components: {
    Inplace,
    TaskItem,
    draggable,
  },
  setup(props) {
    const tasksStore = useTasksStore();
    tasksStore.loadTasks(props.register.id);
  },
  props: {
    register: {
      type: Object as PropType<Register>,
    },
  },
  data() {
    return {
      registerTitle: this.register.title,
      taskTitle: "",
      dragging: false,
      menuItems: [
        {
          label: "Logs",
          icon: "pi pi-book",
          command: () => this.openLogModal(),
        },
        {
          label: "Delete",
          icon: "pi pi-trash",
          command: () => this.deleteList(),
        },
      ],
    };
  },
  methods: {
    changedTask(evt: any): void {
      if (evt.moved != undefined) {
        this.$emitter.emit("kanban:moved-task", this.register.id);
      } else if (evt.added != undefined) {
        this.$emitter.emit("kanban:added-task", this.register.id, evt);
      } else if (evt.removed != undefined) {
        this.$emitter.emit("kanban:removed-task", this.register.id, evt);
      } else {
        console.log("Unhandled Event");
      }
    },
    addNewTask(): void {
      const task = {
        title: this.taskTitle,
        index: this.register.tasks.length,
        untilDate: "",
        description: "",
      };
      this.$emitter.emit("kanban:add-new-task", this.register.id, task);
      this.taskTitle = "";
    },
    deleteList(): void {
      this.$emitter.emit("kanban:deleted-list", this.register.id);
    },
    showMenu(ev: any): void {
      this.$refs.menu.show(ev);
    },
    renameListName(): void {
      this.$emitter.emit(
        "kanban:renamed-list-name",
        this.register,
        this.registerTitle
      );
    },
    openLogModal() {
      this.$emitter.emit("log:open", this.register.id);
    },
  },
});
</script>

<style scoped></style>

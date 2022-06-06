<template>
  <div class="overflow-x-scroll">
    <RegisterContainer
      :registers="registersStore.getRegisters"
    ></RegisterContainer>
  </div>
  <TaskDialog
    :selectedTask="selectedTask"
    :display="dialogVisible"
    @delete="deleteTask"
    @save="saveTask"
    @close="closeDialog"
  ></TaskDialog>
</template>

<script lang="ts">
import TaskDialog from "@/components/kanban/TaskDialog.vue";
import { computed, defineComponent } from "vue";
import { useRoute } from "vue-router";
import { useRegistersStore } from "@/store/RegistersStore";
import { useTasksStore } from "@/store/TasksStore";
import { NewRegister, Register } from "@/interfaces/RegistersInterfaces";
import { NewTask, Task } from "@/interfaces/TasksInterfaces";
import RegisterContainer from "@/components/kanban/RegisterContainer.vue";

export default defineComponent({
  name: "Kanban",
  components: {
    TaskDialog,
    RegisterContainer,
  },
  setup() {
    const registersStore = useRegistersStore();
    const tasksStore = useTasksStore();
    const route = useRoute();
    const projectId = computed(() => route.params.id);
    return {
      registersStore,
      tasksStore,
      route,
      projectId,
    };
  },
  data() {
    return {
      dialogVisible: false,
      selectedTask: {
        registerId: null,
        task: {
          id: "",
          index: 0,
          untilDate: null,
          title: "",
          description: "",
        } as Task,
      },
    };
  },
  beforeMount() {
    this.registersStore.clearRegisters();
    this.registersStore.loadRegisters(this.projectId);

    this.$emitter.on("kanban:moved-list", () => {
      this.registersStore.switchIndex();
    });

    this.$emitter.on("kanban:created-list", (registerTitle) => {
      const newRegister: NewRegister = {
        title: registerTitle,
        index: this.registersStore.getRegisters.length,
      };
      this.registersStore.addRegister(this.projectId, newRegister);
    });

    this.$emitter.on(
      "kanban:renamed-list-name",
      (register: Register, registerTitle) => {
        register.title = registerTitle;
        this.registersStore.updateRegister(register);
      }
    );

    this.$emitter.on("kanban:deleted-list", (registerId) => {
      this.registersStore.deleteRegister(registerId);
    });

    this.$emitter.on("kanban:clicked-task", (task) => {
      this.selectedTask = task;
      this.dialogVisible = true;
    });

    this.$emitter.on("kanban:moved-task", (registerId: string) => {
      this.tasksStore.switchIndex(registerId);
    });

    this.$emitter.on(
      "kanban:add-new-task",
      (registerId: string, task: NewTask) => {
        this.tasksStore.addTask(registerId, task, false);
      }
    );

    this.$emitter.on("kanban:added-task", (registerId, evt) => {
      console.log(evt);
      this.tasksStore.addTask(registerId, evt.added.element, true);
    });
    this.$emitter.on("kanban:removed-task", (registerId, evt) => {
      console.log(evt);
      this.tasksStore.deleteTask(registerId, evt.removed.element.id);
    });
  },
  unmounted() {
    this.$emitter.off("kanban:add-new-task");
    this.$emitter.off("kanban:moved-task");
    this.$emitter.off("kanban:clicked-task");
    this.$emitter.off("kanban:deleted-list");
    this.$emitter.off("kanban:renamed-list-name");
    this.$emitter.off("kanban:created-list");
    this.$emitter.off("kanban:moved-list");
    this.$emitter.off("kanban:added-task");
    this.$emitter.off("kanban:removed-task");
  },
  methods: {
    saveTask(registerId: string, task: Task): void {
      this.tasksStore.updateTask(registerId, task);
      this.dialogVisible = false;
    },
    deleteTask(task) {
      this.tasksStore.deleteTask(task.registerId, task.task.id);
      this.closeDialog();
    },
    closeDialog(): void {
      this.selectedTask = {
        registerId: null,
        task: {
          id: null,
          untilDate: null,
          title: "",
        } as Task,
      };
      this.dialogVisible = false;
    },
  },
});
</script>

<style scoped>
/* width */
::-webkit-scrollbar {
  width: 10px;
}

/* Track */
::-webkit-scrollbar-track {
  background: #f1f1f1;
}

/* Handle */
::-webkit-scrollbar-thumb {
  background: #888;
}

/* Handle on hover */
::-webkit-scrollbar-thumb:hover {
  background: #555;
}
</style>

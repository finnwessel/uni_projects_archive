<template>
  <div
    class="
      flex
      justify-content-between
      p-card p-2
      border-white-alpha-10 border-1
      cursor-pointer
      hover:surface-200
      mb-1
      bg-white-alpha-10
    "
    @click="openKanban(project.id)"
  >
    <span class="flex text-xl font-bold">{{
      formattedTitle(project.title)
    }}</span>
    <div class="pl-2">
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
    <ContextMenu ref="menu" :model="menuItems"> </ContextMenu>
  </div>
</template>

<script lang="ts">
import { defineComponent, PropType } from "vue";
import { useProjectsStore } from "@/store/ProjectsStore";
import { Project } from "@/interfaces/ProjectsInterfaces";

export default defineComponent({
  name: "ProjectItem",
  props: {
    project: {
      type: Object as PropType<Project>,
    },
  },
  setup() {
    const projectsStore = useProjectsStore();
    return {
      projectsStore,
    };
  },
  data() {
    return {
      menuItems: [
        {
          label: "Rename",
          icon: "pi pi-pencil",
          command: () => this.openEditModal(),
        },
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
    openKanban(id) {
      this.$router.push({
        name: "Kanban",
        params: {
          id,
        },
      });
    },
    deleteList(): void {
      this.$emitter.emit("project:deleted-project", this.project.id);
    },
    openEditModal() {
      this.$emit("edit", this.project);
    },
    showMenu(ev: any): void {
      this.$refs.menu.show(ev);
    },
    openLogModal() {
      this.$emitter.emit("log:open", this.project.id);
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

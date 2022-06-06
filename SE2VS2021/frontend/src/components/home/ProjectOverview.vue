<template>
  <div>
    <Card class="mx-2">
      <template #title> Projects </template>
      <template #content>
        <draggable
          :list="projectsStore.getProjects"
          :animation="200"
          item-key="id"
          tag="transition-group"
          :component-data="{
            tag: 'div',
            type: 'transition-group',
            name: !dragging ? 'flip-list' : null,
          }"
          handle=".move-handle"
          @change="changedList"
          @end="dragging = false"
          @start="dragging = true"
        >
          <template #item="{ element }">
            <ProjectItem
              class="move-handle"
              :project="element"
              @edit="setSelectedProject($event)"
            />
          </template>
        </draggable>
      </template>
      <template #footer>
        <div class="p-fluid">
          <InputText
            type="text"
            placeholder="New project"
            v-model="newProject.title"
            v-on:keyup.enter="addProject"
          ></InputText>
        </div>
      </template>
    </Card>
    <Dialog
      modal
      :visible="displayDialog"
      @update:visible="displayDialog = false"
    >
      <template #header>
        <span><b>Edit Project</b></span>
      </template>

      <div class="p-fluid">
        <div class="p-inputgroup">
          <span class="p-inputgroup-addon"> Title </span>
          <InputText id="title" v-model="selectedProject.title" autoFocus />
        </div>
      </div>

      <template #footer>
        <Button
          @click="displayDialog = false"
          label="Cancel"
          icon="pi pi-times"
          class="p-button-text"
        />
        <Button
          @click="editProject"
          label="Save"
          icon="pi pi-check"
          class="p-button-success"
        />
      </template>
    </Dialog>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import { useProjectsStore } from "@/store/ProjectsStore";
import { NewProject, Project } from "@/interfaces/ProjectsInterfaces";
import ProjectItem from "@/components/home/ProjectItem.vue";
import draggable from "vuedraggable";

export default defineComponent({
  name: "ProjectOverview",
  components: { ProjectItem, draggable },
  setup() {
    const projectsStore = useProjectsStore();
    projectsStore.loadProjects();
    const newProject: NewProject = {
      title: "",
      index: 0,
    };
    let selectedProject: Project;
    return {
      projectsStore,
      selectedProject,
      newProject,
    };
  },
  data() {
    return {
      dragging: false,
      displayDialog: false,
    };
  },
  methods: {
    changedList(evt: any): void {
      if (evt.moved != undefined) {
        this.$emitter.emit("project:moved-project");
      } else if (evt.added != undefined) {
        //this.$emitter.emit("kanban:added-list", evt);
      } else if (evt.removed != undefined) {
        //this.$emitter.emit("kanban:removed-list", evt);
      } else {
        console.log("Unhandled Event");
      }
    },
    addProject() {
      this.newProject.index = this.projectsStore.getProjects.length;
      this.projectsStore.addProject(this.newProject);
      this.newProject.title = "";
    },
    setSelectedProject(project: Project) {
      this.selectedProject = {
        id: project.id,
        index: project.index,
        title: project.title,
      };
      this.displayDialog = true;
    },
    editProject() {
      this.displayDialog = false;
      this.$emitter.emit("project:edit-project", this.selectedProject);
    },
  },
});
</script>

<style scoped></style>

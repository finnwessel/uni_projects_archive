<template>
  <div class="flex flex-grow-1 align-items-center justify-content-center">
    <div class="flex align-items-start">
      <div class="flex">
        <EventInvites
          v-if="eventStore.getInvitations.length > 0"
          v-bind:events="eventStore.getInvitations"
        />
      </div>
      <div class="flex">
        <ProjectOverview />
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import { useEventsStore } from "@/store/EventsStore";
import { useProjectsStore } from "@/store/ProjectsStore";
import { Project } from "@/interfaces/ProjectsInterfaces";
import ProjectOverview from "@/components/home/ProjectOverview.vue";
import EventInvites from "@/components/home/EventInvites.vue";
import { useNotificationsStore } from "@/store/NotificationsStore";

export default defineComponent({
  name: "Home",
  components: { ProjectOverview, EventInvites },
  setup() {
    const eventStore = useEventsStore();
    const projectsStore = useProjectsStore();
    eventStore.loadInvitations();
    const notificationsStore = useNotificationsStore();
    notificationsStore.initEventSource();
    return {
      eventStore,
      projectsStore,
    };
  },
  beforeMount() {
    this.$emitter.on("event-invitation:accepted", (eventId) => {
      this.eventStore.acceptInvitation(eventId);
    });
    this.$emitter.on("event-invitation:declined", (eventId) => {
      this.eventStore.declineInvitation(eventId);
    });
    this.$emitter.on("project:deleted-project", (projectId) => {
      this.projectsStore.deleteProject(projectId);
    });
    this.$emitter.on("project:edit-project", (project: Project) => {
      this.projectsStore.updateProject(project);
    });
    this.$emitter.on("project:moved-project", () => {
      this.projectsStore.switchIndex();
    });
  },
  unmounted() {
    this.$emitter.off("event-invitation:accepted");
    this.$emitter.off("event-invitation:declined");
    this.$emitter.off("project:deleted-project");
    this.$emitter.off("project:edit-project");
    this.$emitter.off("project:moved-project");
  },
});
</script>

<style scoped></style>

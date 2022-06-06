<template>
  <div id="app" class="flex flex-grow-0 flex-column p-2">
    <Toast />
    <LogModal
      :display="displayLogModal"
      @close="closeDialog"
      :referenceId="logReferenceId"
    />
    <Menubar :model="items" v-if="authStore.isLoggedIn">
      <template #start>
        <MenuAccountButton />
      </template>
    </Menubar>
    <router-view class="p-2" />
  </div>
</template>

<script lang="ts">
import MenuAccountButton from "@/components/MenuAccountButton.vue";
import { useAuthStore } from "@/store/AuthStore";
import { computed, defineComponent } from "vue";
import axios from "axios";
import LogModal from "@/components/LogModal.vue";
import { useNotificationsStore } from "@/store/NotificationsStore";
import { Task } from "@/interfaces/TasksInterfaces";
import { useLogsStore } from "@/store/LogsStore";

export default defineComponent({
  components: {
    LogModal,
    MenuAccountButton,
  },
  setup() {
    const authStore = useAuthStore();
    const notificationsStore = useNotificationsStore();
    const logsStore = useLogsStore();
    notificationsStore.initEventSource();
    let notification = computed(() => notificationsStore.getNotification);
    return {
      authStore,
      logsStore,
      notification,
    };
  },
  data() {
    return {
      displayLogModal: false,
      logReferenceId: "",
      items: [
        {
          label: "Home",
          icon: "pi pi-fw pi-home",
          to: "/",
        },
        {
          label: "Events",
          icon: "pi pi-fw pi-calendar",
          to: "/events",
        },
        {
          label: "Contacts",
          icon: "pi pi-fw pi-users",
          to: "/contacts",
        },
      ],
    };
  },
  watch: {
    notification: {
      handler(n) {
        this.$toast.add({
          severity: "info",
          summary: n.title,
          detail: n.message,
          life: 10000,
        });
      },
    },
  },
  methods: {
    closeDialog(): void {
      this.displayLogModal = false;
    },
    loadLogs() {
      this.logsStore.clearLogs();
      this.logsStore.loadLogs(this.logReferenceId);
    },
  },
  mounted() {
    axios.defaults.headers.common[
      "Authorization"
    ] = `Bearer ${this.authStore.user.token}`;

    this.$emitter.on("log:open", (referenceId) => {
      this.logReferenceId = referenceId;
      this.loadLogs();
      this.displayLogModal = true;
    });
  },
});
</script>

<style>
body,
html {
  background-color: var(--surface-b);
  margin: 0;
  height: 100vh;
}

#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
  height: 100vh;
}

#nav a {
  font-weight: bold;
  color: #2c3e50;
}

a {
  color: white;
}
</style>

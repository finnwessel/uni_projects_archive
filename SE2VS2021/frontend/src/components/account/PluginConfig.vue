<template>
  <div class="account mt-5">
    <Fieldset legend="Notification Settings">
      <Dropdown
        v-model="activePlugin"
        :options="dropdown"
        placeholder="Select secondary notification channel"
      />
      <form class="p-fluid">
        <PluginConfigItem
          :key="index"
          :plugin="plugin"
          v-for="(plugin, index) in plugins"
        />
      </form>
    </Fieldset>
  </div>
</template>

<script>
import Dropdown from "primevue/dropdown";
import { defineComponent } from "vue";
import PluginConfigItem from "@/components/account/PluginConfigItem.vue";
import NotificationsService from "@/services/NotificationsService";
export default defineComponent({
  name: "PluginConfig",
  components: {
    Dropdown,
    PluginConfigItem,
  },
  data() {
    return {
      activePlugin: null,
      dropdown: [],
      plugins: [],
    };
  },
  mounted() {
    NotificationsService.getPlugins().then((p) => {
      this.plugins = p;
      this.dropdown = [...p, "None"];
    });
    NotificationsService.getActivePlugin().then((p) => {
      this.activePlugin = p;
    });
  },
  watch: {
    activePlugin(pluginName) {
      if (pluginName === "None") {
        NotificationsService.disableNotifications().then(() => {
          this.$toast.add({
            severity: "info",
            summary: "Disabled",
            detail: `Notifications are deactivated.`,
            life: 3000,
          });
        });
      } else {
        NotificationsService.setActivePlugin(pluginName).then(() => {
          this.$toast.add({
            severity: "info",
            summary: "Updated active plugin",
            detail: `${pluginName} is now active.`,
            life: 3000,
          });
        });
      }
    },
  },
});
</script>

<style scoped></style>

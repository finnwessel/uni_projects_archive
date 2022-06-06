<template>
  <Card>
    <template #title>
      <span>{{ plugin }}</span>
    </template>
    <template #content>
      <InputText
        :key="index"
        v-for="(key, index) in Object.keys(config)"
        v-model="config[key]"
        :placeholder="key"
      ></InputText>
      <Button
        type="submit"
        label="Save config"
        class="p-button-sm"
        @click.prevent="saveConfig()"
      />
    </template>
  </Card>
</template>

<script>
import { defineComponent } from "vue";
import NotificationsService from "@/services/NotificationsService";

export default defineComponent({
  name: "PluginConfigItem",
  props: {
    plugin: {
      type: String,
    },
  },
  data() {
    return {
      config: {
        email: "",
      },
    };
  },
  mounted() {
    NotificationsService.getPluginConfig(this.plugin).then((c) => {
      this.config = c;
    });
  },
  methods: {
    saveConfig() {
      NotificationsService.setConfig(this.plugin, this.config)
        .then(() => {
          this.$toast.add({
            severity: "info",
            summary: "Updated config",
            detail: `${this.plugin} configuration updated.`,
            life: 3000,
          });
        })
        .catch(() => {
          this.$toast.add({
            severity: "error",
            summary: "Error",
            detail: `${this.plugin} configuration failed.`,
            life: 3000,
          });
        });
    },
  },
});
</script>

<style scoped></style>

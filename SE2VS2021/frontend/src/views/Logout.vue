<template>
  <div>You logged out</div>
</template>

<script lang="ts">
import router from "@/router";
import { useAuthStore } from "@/store/AuthStore";
import { defineComponent } from "vue";

export default defineComponent({
  name: "Logout",
  setup() {
    const authStore = useAuthStore();
    return {
      authStore,
    };
  },
  beforeMount() {
    this.authStore.$reset();
    router.push({ name: "Login" });
  },
});
</script>

<style scoped></style>

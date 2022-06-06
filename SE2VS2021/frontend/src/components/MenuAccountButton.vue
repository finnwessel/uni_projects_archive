<template>
  <SplitButton
    :model="items"
    class="p-button-success mr-2"
    style="outline: none; border: none; box-shadow: none"
  >
    <Button @click="route" class="border-noround">
      <div class="flex align-items-center">
        <Avatar
          v-if="!account.avatar"
          :label="account.username[0]"
          class="p-mr-2"
          size="large"
          shape="circle"
        />
        <Avatar
          v-if="account.avatar"
          :image="account.avatar"
          class="p-mr-2"
          size="large"
          shape="circle"
        />
        <div class="flex flex-column align-items-start ml-2">
          <div class="font-bold">
            {{ account.username }}
          </div>
          <div class="email font-light">
            {{ account.email }}
          </div>
        </div>
      </div>
    </Button>
  </SplitButton>
</template>

<script lang="ts">
import router from "@/router";
import { useAuthStore } from "@/store/AuthStore";
import { defineComponent } from "vue";

export default defineComponent({
  name: "MenuAccountButton",
  setup() {
    const authStore = useAuthStore();
    return { account: authStore.user };
  },
  data() {
    return {
      avatar: "",
      items: [
        {
          label: "Logout",
          icon: "pi pi-fw pi-power-off",
          to: "/logout",
        },
      ],
    };
  },
  methods: {
    route(): void {
      router.push({ name: "Account" });
    },
  },
});
</script>

<style scoped>
.email {
  font-size: 13px;
}
</style>

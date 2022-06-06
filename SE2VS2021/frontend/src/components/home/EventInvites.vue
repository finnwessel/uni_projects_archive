<template>
  <Card class="flex m-auto justify-content-center w-max">
    <template #title> You've been added to events! </template>
    <template #content>
      <div class="flex flex-column">
        <InviteItem
          v-for="event in events"
          :key="event.id"
          v-bind:event="event"
        />
      </div>
    </template>
  </Card>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import InviteItem from "@/components/home/InviteItem.vue";

export default defineComponent({
  name: "EventInvites",
  components: { InviteItem },
  props: ["events"],
});
</script>

<style scoped></style>

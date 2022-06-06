<template>
  <div
    class="
      flex
      mb-1
      p-2
      surface-100
      hover:surface-200
      cursor-pointer
      align-items-center
      justify-content-between
      border-round
    "
  >
    <div class="flex flex-column flex-grow-1 justify-content-between">
      <h3 class="flex m-0">{{ formattedTitle }}</h3>

      <div class="flex flex-column mt-2">
        <div
          class="
            flex
            justify-content-between
            align-items-center
            surface-50
            border-round
            mb-1
            pr-2
          "
        >
          <div
            class="
              flex
              surface-300
              border-round
              w-3rem
              justify-content-center
              py-1
            "
          >
            Start
          </div>
          <h5 class="flex m-0">{{ dateFormatted(event.start) }}</h5>
          <h5 class="flex m-0">{{ timeFormatted(event.start) }}</h5>
        </div>
        <div
          class="
            flex
            justify-content-between
            align-items-center
            surface-50
            border-round
            pr-2
          "
        >
          <div
            class="
              flex
              surface-300
              border-round
              w-3rem
              justify-content-center
              py-1
            "
          >
            End
          </div>
          <h5 class="flex m-0">{{ dateFormatted(event.end) }}</h5>
          <h5 class="flex m-0">{{ timeFormatted(event.end) }}</h5>
        </div>
      </div>
    </div>

    <div class="flex flex-none ml-2">
      <Button
        class="h-2rem w-2rem m-1 p-button-success"
        icon="pi pi-check"
        @click="acceptInvitation"
      />
      <Button
        class="h-2rem w-2rem m-1 p-button-danger"
        icon="pi pi-times"
        @click="declineInvitation"
      />
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import { dateFormatted, timeFormatted } from "@/helpers/date";

export default defineComponent({
  name: "InviteItem",
  props: ["event"],
  setup() {
    return {
      dateFormatted,
      timeFormatted,
    };
  },
  methods: {
    acceptInvitation() {
      this.$emitter.emit("event-invitation:accepted", this.event.id);
    },
    declineInvitation() {
      this.$emitter.emit("event-invitation:declined", this.event.id);
    },
  },
  computed: {
    formattedTitle(): string {
      let allowedLength = 30;
      if (this.event.title.length < allowedLength) {
        return this.event.title;
      } else {
        return this.event.title.substring(0, allowedLength).concat("...");
      }
    },
  },
});
</script>

<style scoped></style>

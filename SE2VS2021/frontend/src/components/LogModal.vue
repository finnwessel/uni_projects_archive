<template>
  <Dialog
    modal
    @close="$emit('close')"
    :visible="display"
    @update:visible="$emit('close', $event)"
  >
    <template #header>
      <span><b>Log</b></span>
    </template>

    <div
      class="flex flex-column mt-2"
      v-for="(log, index) in logs"
      :key="index"
    >
      <div class="flex align-items-center surface-50 border-round">
        <div
          class="
            flex
            surface-300
            border-round
            justify-content-center
            p-1
            font-bold
          "
        >
          {{ dateFormatted(log.timeStamp) }}
          {{ timeFormatted(log.timeStamp) }}
        </div>
        <p class="flex m-0 ml-2 px-2">{{ log.message }}</p>
      </div>
    </div>

    <template #footer>
      <Button
        @click="emitCancel"
        label="Ok"
        icon="pi pi-check"
        class="p-button-success"
      />
    </template>
  </Dialog>
</template>

<script lang="ts">
import { computed, defineComponent } from "vue";
import { useLogsStore } from "@/store/LogsStore";
import { dateFormatted, timeFormatted } from "@/helpers/date";

export default defineComponent({
  name: "LogModal",
  setup() {
    const logsStore = useLogsStore();
    let logs = computed(() => logsStore.getLogs);
    return {
      logsStore,
      logs,
      timeFormatted,
      dateFormatted,
    };
  },
  props: {
    display: {
      type: Boolean,
    },
    referenceId: {
      type: String,
    },
  },
  methods: {
    emitCancel() {
      this.$emit("close");
    },
  },
});
</script>

<style scoped></style>

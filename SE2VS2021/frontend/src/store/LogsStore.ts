import { defineStore } from "pinia";
import { Log } from "@/interfaces/LogsInterfaces";
import LogService from "@/services/LogService";

export type LogsState = {
  logs: Log[];
};

export const useLogsStore = defineStore({
  id: "logs",
  state: () =>
    ({
      logs: [],
    } as LogsState),

  actions: {
    loadLogs(referenceId: string) {
      LogService.getFromReference(referenceId).then((logs) => {
        this.logs = logs;
      });
    },
    clearLogs() {
      this.logs = [];
    },
  },
  getters: {
    getLogs(state): Log[] {
      return state.logs;
    },
  },
});

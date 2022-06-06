import { defineStore } from "pinia";
import { NewTask, Task } from "@/interfaces/TasksInterfaces";
import TaskService from "@/services/TaskService";
import { useRegistersStore } from "@/store/RegistersStore";
import { SwitchedIndex } from "@/interfaces/RegistersInterfaces";

export const useTasksStore = defineStore({
  id: "tasks",

  actions: {
    addTask(registerId: string, task: NewTask, moved: boolean): Promise<Task> {
      return new Promise((resolve, reject) => {
        TaskService.add(registerId, task)
          .then((task) => {
            const register = this.getRegister(registerId);
            if (!moved) {
              register.tasks = [...register.tasks].concat(task);
            }
            resolve(task);
          })
          .catch((err) => reject(err));
      });
    },
    updateTask(registerId: string, task: Task): Promise<boolean> {
      return new Promise((resolve, reject) => {
        TaskService.update(task)
          .then((b) => {
            const register = this.getRegister(registerId);
            const newArray = register.tasks.filter((t) => t.id !== task.id);
            newArray.splice(task.index, 0, task);
            register.tasks = [...newArray];
            resolve(b);
          })
          .catch((err) => reject(err));
      });
    },
    switchIndex(registerId: string) {
      const switchedIndexes: SwitchedIndex[] = [];
      const register = this.getRegister(registerId);
      for (const i in register.tasks) {
        switchedIndexes.push({ id: register.tasks[i].id, index: parseInt(i) });
      }
      TaskService.updateIndex(switchedIndexes).then();
    },
    deleteTask(registerId: string, taskId: string): Promise<boolean> {
      return new Promise((resolve, reject) => {
        TaskService.delete(taskId)
          .then((b) => {
            const register = this.getRegister(registerId);
            register.tasks = register.tasks.filter((task) => task.id != taskId);
            resolve(b);
          })
          .catch((err) => reject(err));
      });
    },
    loadTasks(registerId: string) {
      TaskService.get(registerId).then((tasks) => {
        const register = this.getRegister(registerId);
        register.tasks = tasks.sort((a, b) => (a.index > b.index ? 1 : -1));
      });
    },
    getRegister(registerId: string) {
      return useRegistersStore().getRegisters.find(
        (register) => register.id == registerId
      );
    },
  },
});

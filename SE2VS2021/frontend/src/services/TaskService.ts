import axios from "axios";
import { NewTask, Task } from "@/interfaces/TasksInterfaces";
import { SwitchedIndex } from "@/interfaces/RegistersInterfaces";

const api = process.env.VUE_APP_TASKS_SERVICE_URL + "Task/";

export default {
  add(registerId: string, task: NewTask): Promise<Task> {
    return new Promise((resolve, reject) => {
      axios
        .post(api.concat(registerId), task)
        .then((res) => {
          if (res.status == 201) {
            resolve(res.data);
          } else {
            reject();
          }
        })
        .catch((err) => reject(err));
    });
  },
  update(task: Task): Promise<boolean> {
    return new Promise((resolve, reject) => {
      axios
        .put(api, task)
        .then((res) => {
          if (res.status == 200) {
            resolve(true);
          } else {
            reject();
          }
        })
        .catch((err) => reject(err));
    });
  },
  updateIndex(indexes: SwitchedIndex[]): Promise<boolean> {
    return new Promise((resolve, reject) => {
      axios
        .put(api.concat("index"), indexes)
        .then((res) => {
          if (res.status == 200) {
            resolve(true);
          } else {
            reject();
          }
        })
        .catch((err) => reject(err));
    });
  },
  delete(taskId: string): Promise<boolean> {
    return new Promise((resolve, reject) => {
      axios
        .delete(api.concat(taskId))
        .then((res) => {
          if (res.status == 200) {
            resolve(true);
          } else {
            reject();
          }
        })
        .catch((err) => reject(err));
    });
  },
  get(registerId: string): Promise<Task[]> {
    return new Promise((resolve, reject) => {
      axios
        .get(api.concat(registerId))
        .then((res) => {
          if (res.status == 200) {
            resolve(res.data);
          } else {
            reject();
          }
        })
        .catch((err) => reject(err));
    });
  },
};

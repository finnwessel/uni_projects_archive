import axios from "axios";
import {
  NewRegister,
  Register,
  SwitchedIndex,
} from "@/interfaces/RegistersInterfaces";

const api = process.env.VUE_APP_TASKS_SERVICE_URL + "Register/";

export default {
  add(projectId: string, project: NewRegister): Promise<Register> {
    return new Promise((resolve, reject) => {
      axios
        .post(api.concat(projectId), project)
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
  update(register: Register): Promise<boolean> {
    return new Promise((resolve, reject) => {
      axios
        .put(api, register)
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
  delete(registerId: string): Promise<boolean> {
    return new Promise((resolve, reject) => {
      axios
        .delete(api.concat(registerId))
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
  get(projectId: string): Promise<Register[]> {
    return new Promise((resolve, reject) => {
      axios
        .get(api.concat(projectId))
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

import axios from "axios";
import { NewProject, Project } from "@/interfaces/ProjectsInterfaces";
import { SwitchedIndex } from "@/interfaces/RegistersInterfaces";

const api = process.env.VUE_APP_TASKS_SERVICE_URL + "Project/";

export default {
  add(project: NewProject): Promise<Project> {
    return new Promise((resolve, reject) => {
      axios
        .post(api, project)
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
  update(project: Project): Promise<boolean> {
    return new Promise((resolve, reject) => {
      axios
        .put(api, project)
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
  delete(projectId: string): Promise<boolean> {
    return new Promise((resolve, reject) => {
      axios
        .delete(api.concat(projectId))
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
  get(): Promise<Project[]> {
    return new Promise((resolve, reject) => {
      axios
        .get(api)
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

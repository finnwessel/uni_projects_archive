import { LoginCredentials, LoginData } from "@/interfaces/AuthInterfaces";
import axios from "axios";

const api = process.env.VUE_APP_NOTIFICATIONS_SERVICE_URL;

export default {
  setConfig(type: string, conf: { [key: string]: string }): Promise<void> {
    return new Promise((resolve, reject) => {
      axios
        .post(api + `/configure/${type}`, conf)
        .then((res) => {
          if (res.status == 200) {
            resolve();
          } else {
            reject();
          }
        })
        .catch((err) => reject(err));
    });
  },
  getPluginConfig(type: string): Promise<{ [key: string]: string }> {
    return new Promise((resolve, reject) => {
      axios
        .get(api + `/configuration/${type}`)
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
  getPlugins(): Promise<string[]> {
    return new Promise((resolve, reject) => {
      axios
        .get(api + `/types`)
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
  getActivePlugin(): Promise<string> {
    return new Promise((resolve, reject) => {
      axios
        .get(api + `/active`)
        .then((res) => {
          if (res.status == 200) {
            resolve(res.data);
          } else if (res.status == 204) {
            resolve(null);
          } else {
            reject();
          }
        })
        .catch((err) => reject(err));
    });
  },
  setActivePlugin(type: string): Promise<void> {
    return new Promise((resolve, reject) => {
      axios
        .post(api + `/activate/${type}`)
        .then((res) => {
          if (res.status == 200) {
            resolve();
          } else {
            reject();
          }
        })
        .catch((err) => reject(err));
    });
  },
  disableNotifications(): Promise<void> {
    return new Promise((resolve, reject) => {
      axios
        .post(api + `/disable`)
        .then((res) => {
          if (res.status == 200) {
            resolve();
          } else {
            reject();
          }
        })
        .catch((err) => reject(err));
    });
  },
};

import axios from "axios";
import {
  Account,
  LoginCredentials,
  LoginData,
  RegisterCredentials,
  RegisterData,
} from "@/interfaces/AuthInterfaces";

const api = process.env.VUE_APP_ACCOUNT_SERVICE_URL;

export default {
  login(credentials: LoginCredentials): Promise<LoginData> {
    return new Promise((resolve, reject) => {
      axios
        .post(api + "/login", credentials)
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
  register(credentials: RegisterCredentials): Promise<RegisterData> {
    return new Promise((resolve, reject) => {
      axios
        .post(api + "/register", credentials)
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
  updateAcc(account_data, id) {
    return axios.put(api + `/user/${id}`, account_data);
  },
  getAcc(): Promise<Account> {
    return axios.get(api + "/profile");
  },
};

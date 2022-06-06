import AccountService from "@/services/AccountService";
import { defineStore } from "pinia";
import axios from "axios";
import { Account } from "@/interfaces/AuthInterfaces";

export type AuthState = {
  user: Account;
};

export const useAuthStore = defineStore({
  id: "auth",
  state: () =>
    ({
      user: {
        id: null,
        username: "",
        firstname: "",
        lastname: "",
        email: "",
        token: null,
        avatar: "",
      },
    } as AuthState),

  actions: {
    loginUser(p) {
      return new Promise((resolve, reject) => {
        AccountService.login(p)
          .then((res) => {
            this.user = res;
            axios.defaults.headers.common[
              "Authorization"
            ] = `Bearer ${res.token}`;
            resolve(true);
          })
          .catch((error) => reject(error));
      });
    },
    registerUser(p) {
      return new Promise((resolve, reject) => {
        AccountService.register(p)
          .then(() => {
            resolve(true);
          })
          .catch((error) => reject(error));
      });
    },
  },
  getters: {
    isLoggedIn(state): boolean {
      return state.user.token != null;
    },
    getUser(state) {
      return state.user;
    },
  },
});

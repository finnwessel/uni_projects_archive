import {
  loginUser, logoutUser,
  refreshAccessToken,
  registerUser
} from "@/helper/authCalls";
import { parseJwt } from "@/helper/auth";

const state = {
  user: {
    id: null,
    username: null
  },
  auth: {
    expiresIn: null,
    refreshTask: null,
    access_token: null,
    refresh_token: null
  }
};

const mutations = {
  M_LOGIN_USER: (state, p) => {
    state.user = p.data.user;
    state.auth.access_token = p.data.payload.access_token;
    state.auth.expiresIn = parseJwt(p.data.payload.access_token).exp;
    state.auth.refresh_token = p.data.payload.refresh_token;
  },
  M_SET_USER_LOGGED_OUT: state => {
    state.user = { id: null, username: null };
    state.auth.access_token = null;
    state.auth.refresh_token = null;
  },
  M_SET_ACCESS_TOKEN_EXPIRES_IN: (state, p) => {
    state.auth.expiresIn = p;
  },
  M_SET_ACCESS_TOKEN: (state, p) => {
    state.auth.access_token = p;
  },
  M_SET_REFRESH_TASK: (state, p) => {
    state.auth.refreshTask = p;
  }
};

const getters = {
  GET_USER: state => {
    return state.user;
  },
  GET_ACCESS_TOKEN_EXPIRES_IN: state => {
    return state.auth.expiresIn;
  },
  GET_ACCESS_TOKEN: state => {
    return state.auth.access_token;
  },
  GET_REFRESH_TOKEN: state => {
    return state.auth.refresh_token;
  },
  // Return boolean if user is logged in
  GET_USER_LOGGED_IN: state => {
    return (
      state.auth.access_token !== null &&
      state.auth.refresh_token !== null &&
      new Date() / 1000 < state.auth.expiresIn
    );
  }
};

const actions = {
  LOGIN_USER(context, p) {
    return new Promise((resolve, reject) => {
      loginUser(p, context.rootGetters.GET_ACCESS_TOKEN)
        .then(res => {
          context.commit("M_LOGIN_USER", res);
          this.dispatch("AUTO_REFRESH");
          resolve(true);
        })
        .catch(error => reject(error));
    });
  },
  REGISTER_USER(context, p) {
    return new Promise((resolve, reject) => {
      registerUser(p)
        .then(res => {
          context.commit("M_LOGIN_USER", res);
          this.dispatch("AUTO_REFRESH");
          resolve(true);
        })
        .catch(error => {
          reject(error);
        });
    });
  },
  REFRESH_ACCESS_TOKEN(context) {
    return new Promise((resolve, reject) => {
      if (context.rootGetters.GET_REFRESH_TOKEN == null) {
        resolve(false);
        return;
      }
      refreshAccessToken(
        {
          refresh_token: context.rootGetters.GET_REFRESH_TOKEN
        },
        context.rootGetters.GET_ACCESS_TOKEN
      )
        .then(res => {
          context.commit("M_SET_ACCESS_TOKEN", res.data.payload.access_token);
          context.commit(
            "M_SET_ACCESS_TOKEN_EXPIRES_IN",
            parseJwt(res.data.payload.access_token).exp
          );
          this.dispatch("AUTO_REFRESH");
          resolve(true);
        })
        .catch(error => {
          reject(error);
        });
    });
  },
  SET_USER_LOGGED_OUT(context) {
    logoutUser(context.rootGetters.GET_REFRESH_TOKEN).catch(() => {
      console.log("Failed to revoke refresh token");
    });
    context.commit("M_SET_USER_LOGGED_OUT");
    context.commit("M_SET_REFRESH_TASK", null);
  },
  AUTO_REFRESH(context) {
    console.log("Called auto refresh");
    const { state, commit, dispatch } = context;
    const { exp } = parseJwt(state.auth.access_token);
    const now = Date.now() / 1000;
    let timeUntilRefresh = exp - now;
    timeUntilRefresh -= 60;
    const refreshTask = setTimeout(
      () => dispatch("REFRESH_ACCESS_TOKEN"),
      timeUntilRefresh * 1000
    );
    commit("M_SET_REFRESH_TASK", refreshTask);
  }
};

export default {
  state,
  mutations,
  getters,
  actions
};

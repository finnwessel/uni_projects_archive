import Vue from "vue";
import VueRouter from "vue-router";
import Base from "@/views/Base";
import Auth from "@/views/auth/Auth";
import store from "../store";
import Lists from "@/views/Lists";
import Profile from "@/views/Profile";

import ListTweets from "@/views/ListTweets";

Vue.use(VueRouter);

const routes = [
  {
    path: "/",
    name: "Base",
    component: Base,
    meta: {
      auth: true
    }
  },
  {
    path: "/lists",
    name: "Lists",
    component: Lists,
    meta: {
      auth: true
    }
  },
  {
    path: "/lists/:id",
    name: "ListTweets",
    component: ListTweets,
    meta: {
      auth: true
    }
  },
  {
    path: "/profile",
    name: "Profile",
    component: Profile,
    meta: {
      auth: true
    }
  },
  {
    path: "/auth",
    name: "Auth",
    component: Auth,
    meta: {
      auth: false
    }
  },
  {
    path: "/logout",
    beforeEnter(to, from, next) {
      store.dispatch("SET_USER_LOGGED_OUT");
      next({ path: "/" });
    }
  }
];
// Todo: 404 not found

const router = new VueRouter({
  mode: "history", // history hash
  base: process.env.BASE_URL,
  routes
});

const auth = {
  loggedIn() {
    return store.getters.GET_USER_LOGGED_IN;
  }
};

router.beforeEach((to, from, next) => {
  // Check if auth is required
  if (to && to.meta && to.meta.auth) {
    if (auth.loggedIn()) {
      if (to.name === "Auth") {
        router.push({ name: "Base" }).then();
        return false;
      }
      next();
    } else {
      if (to.name !== "Auth") {
        router.push({ name: "Auth" }).then();
        return false;
      }
      next();
    }
  } else {
    if (auth.loggedIn() && to.name === "Auth") {
      router.push({ name: "Base" }).then();
      return false;
    }
    next();
  }
});

export default router;

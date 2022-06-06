import { createRouter, createWebHashHistory, RouteRecordRaw } from "vue-router";
import { useAuthStore } from "@/store/AuthStore";
import Home from "../views/Home.vue";

const routes: Array<RouteRecordRaw> = [
  {
    path: "/",
    name: "Home",
    component: Home,
    meta: {
      auth: true,
    },
  },
  {
    path: "/register",
    name: "Register",
    component: () => import("@/views/Register.vue"),
    meta: {
      auth: false,
    },
  },
  {
    path: "/login",
    name: "Login",
    component: () => import("@/views/Login.vue"),
    meta: {
      auth: false,
    },
  },
  {
    path: "/account",
    name: "Account",
    component: () => import("@/views/Account.vue"),
    meta: {
      auth: true,
    },
  },
  {
    path: "/logout",
    name: "Logout",
    component: () => import("@/views/Logout.vue"),
    meta: {
      auth: true,
    },
  },
  {
    path: "/contacts",
    name: "Contacts",
    component: () => import("@/views/Contacts.vue"),
    meta: {
      auth: true,
    },
  },
  {
    path: "/events",
    name: "Events",
    component: () => import("@/views/Events.vue"),
    meta: {
      auth: true,
    },
  },
  {
    path: "/kanban/:id",
    name: "Kanban",
    component: () => import("@/views/Kanban.vue"),
    meta: {
      auth: true,
    },
  },
];

const router = createRouter({
  history: createWebHashHistory(),
  routes,
});

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();
  // Check if auth is required
  if (to && to.meta && to.meta.auth) {
    if (authStore.isLoggedIn) {
      if (to.name === "Login") {
        next({ name: "Home" });
        return false;
      }
      next();
    } else {
      if (to.name !== "Login" && to.name !== "Register") {
        next({ name: "Login" });
        return false;
      }
      next();
    }
  } else {
    if (
      authStore.isLoggedIn &&
      (to.name === "Login" || to.name === "Register")
    ) {
      next({ name: "Home" });
      return false;
    }
    next();
  }
});

export default router;

import Vue from "vue";
import Vuex from "vuex";
import createPersistedState from "vuex-persistedstate";

import auth from "@/store/modules/auth";
import search from "@/store/modules/search";
import stream from "@/store/modules/stream";
import tweets from "@/store/modules/tweets";
import layout from "@/store/modules/layout";
import lists from "@/store/modules/lists";

Vue.use(Vuex);

export default new Vuex.Store({
  state: {},
  mutations: {},
  actions: {},
  modules: {
    auth,
    search,
    stream,
    lists,
    tweets,
    layout
  },
  plugins: [createPersistedState({ paths: ["auth"] })]
});

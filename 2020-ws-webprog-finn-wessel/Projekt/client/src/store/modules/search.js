import {
  getParamsFromSearch,
  getTweetsFromRecentSearch
} from "@/helper/apiCalls";

const state = {
  search: {
    id: null,
    keywords: null,
    settings: {
      take: 10,
      images: 0,
      videos: 0,
      retweets: 0
    }
  }
};

const mutations = {
  M_SET_SEARCH_ID: (state, p) => {
    state.search.id = p.id;
  },
  M_SET_SEARCH_KEYWORDS: (state, p) => {
    state.search.keywords = p;
  },
  M_SET_SEARCH_SETTINGS: (state, p) => {
    state.search.settings = p;
  }
};

const getters = {
  GET_SEARCH_KEYWORDS: state => {
    return state.search.keywords;
  },
  GET_SEARCH_SETTINGS: state => {
    return state.search.settings;
  }
};

const actions = {
  EXECUTE_SEARCH(context) {
    return new Promise((resolve, reject) => {
      getTweetsFromRecentSearch(
        context.state.search.keywords,
        context.state.search.settings,
        context.rootGetters.GET_ACCESS_TOKEN
      )
        .then(res => {
          context.commit("M_SET_SEARCH_ID", res);
          context.commit("M_SET_TWEETS_SOURCE", {
            type: "search",
            uuid: res.id
          });
          context.commit("M_SET_TWEETS", res.tweets);
          resolve(true);
        })
        .catch(error => {
          reject(error);
        });
    });
  },
  SET_SEARCH_KEYWORDS(context, p) {
    context.commit("M_SET_SEARCH_KEYWORDS", p);
  },
  SET_SEARCH_SETTINGS(context, p) {
    context.commit("M_SET_SEARCH_SETTINGS", p);
  },
  RERUN_SEARCH(context, id) {
    return new Promise((resolve, reject) => {
      getParamsFromSearch(id, context.rootGetters.GET_ACCESS_TOKEN)
        .then(res => {
          context.commit("M_SET_SEARCH_KEYWORDS", res.keywords);
          context.commit("M_SET_SEARCH_SETTINGS", {
            take: 10,
            images: res.images,
            videos: res.videos,
            retweets: res.retweets
          });
          context
            .dispatch("EXECUTE_SEARCH")
            .then(() => {
              resolve(true);
            })
            .catch(error => reject(error));
        })
        .catch(error => reject(error));
    });
  }
};

export default {
  state,
  mutations,
  getters,
  actions
};

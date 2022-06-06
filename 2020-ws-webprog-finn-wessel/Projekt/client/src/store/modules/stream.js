import { getParamsFromStream, getTweetsFromStream } from "@/helper/apiCalls";

const state = {
  stream: {
    openStream: null,
    keywords: null,
    settings: {
      take: 20
    }
  }
};

const mutations = {
  M_STOP_STREAM(state) {
    if (state.stream.openStream !== null) {
      state.stream.openStream.close();
      state.stream.openStream = null;
    }
  },
  M_SET_STREAM: (state, p) => {
    state.stream.openStream = p;
  },
  M_SET_STREAM_KEYWORDS: (state, p) => {
    state.stream.keywords = p;
  },
  M_SET_STREAM_SETTINGS: (state, p) => {
    state.stream.settings = p;
  }
};

const getters = {
  GET_STREAM_KEYWORDS: state => {
    return state.stream.keywords;
  },
  GET_STREAM_SETTINGS: state => {
    return state.stream.settings;
  },
  GET_STREAM_OPEN: state => {
    return state.stream.openStream !== null;
  }
};

const actions = {
  START_STREAM(context) {
    return new Promise((resolve, reject) => {
      if (context.state.stream.keywords !== null) {
        context.commit("M_SET_TWEETS", []);
        let counter = 0;
        const openStream = getTweetsFromStream(state.stream.keywords);
        openStream.addEventListener("info", event => {
          context.commit("M_SET_TWEETS_SOURCE", {
            type: "stream",
            uuid: JSON.parse(event.data).stream_id
          });
        });
        openStream.addEventListener("tweet", event => {
          console.log(event);
          if (event.data) {
            context.commit("M_ADD_TWEET_AT_START", JSON.parse(event.data));
            counter++;
          }
          if (counter >= state.stream.settings.take) {
            context.commit("M_STOP_STREAM");
          }
        });
        openStream.onerror = function(err) {
          console.error("EventSource failed:", err);
          context.commit("M_STOP_STREAM");
        };
        context.commit("M_SET_STREAM", openStream);
        resolve(true);
      }
      reject(false);
    });
  },
  RERUN_STREAM(context, id) {
    return new Promise((resolve, reject) => {
      getParamsFromStream(id, context.rootGetters.GET_ACCESS_TOKEN)
        .then(res => {
          context.commit("M_SET_STREAM_KEYWORDS", res.keywords);
          context
            .dispatch("START_STREAM")
            .then(() => {
              resolve(true);
            })
            .catch(error => {
              reject(error);
            });
        })
        .catch(error => {
          reject(error);
        });
    });
  },
  STOP_STREAM(context) {
    return new Promise(resolve => {
      context.commit("M_STOP_STREAM");
      resolve(true);
    });
  },
  SET_STREAM_KEYWORDS(context, p) {
    context.commit("M_SET_STREAM_KEYWORDS", p);
  },
  SET_STREAM_SETTINGS(context, p) {
    context.commit("M_SET_STREAM_SETTINGS", p);
  }
};

export default {
  state,
  mutations,
  getters,
  actions
};

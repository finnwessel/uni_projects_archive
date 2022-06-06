import { getTweetMediaFromTweetWithId } from "@/helper/apiCalls";

const state = {
  tweets: {
    source: {
      type: null,
      uuid: null
    },
    array: []
  }
};

const mutations = {
  M_UPDATE_TWEET_MEDIA: (state, p) => {
    if (state.tweets.array.length > 0) {
      state.tweets.array.find(tweet => tweet.id === p.id).media = p.media;
    }
  },
  M_SET_TWEETS: (state, p) => {
    state.tweets.array = p;
  },
  M_SET_TWEETS_SOURCE: (state, p) => {
    state.tweets.source = p;
  },
  M_ADD_TWEET_AT_START: (state, p) => {
    state.tweets.array.unshift(p);
  }
};

const getters = {
  GET_TWEETS: state => {
    return state.tweets.array;
  },
  GET_TWEETS_SOURCE: state => {
    return state.tweets.source;
  }
};

const actions = {
  LOAD_TWEET_MEDIA(context, id) {
    return new Promise((resolve, reject) => {
      getTweetMediaFromTweetWithId(id, context.rootGetters.GET_ACCESS_TOKEN)
        .then(res => {
          context.commit("M_UPDATE_TWEET_MEDIA", res);
          resolve(res);
        })
        .catch(error => {
          reject(error);
        });
    });
  },
  SET_TWEETS(context, p) {
    context.commit("M_SET_TWEETS", p);
  },
  SET_TWEETS_SOURCE(context, p) {
    context.commit("M_SET_TWEETS_SOURCE", p);
  },
  ADD_TWEET_AT_START(context, p) {
    context.commit("M_ADD_TWEET_AT_START", p);
  }
};

export default {
  state,
  mutations,
  getters,
  actions
};

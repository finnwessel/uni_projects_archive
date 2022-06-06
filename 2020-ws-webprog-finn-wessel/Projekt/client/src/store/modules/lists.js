import {
  createList,
  deleteList,
  getListsFromUser,
  getListWithId,
  updateList
} from "@/helper/listCalls";

const state = {
  lists: []
};

const mutations = {
  M_SET_LISTS: (state, p) => {
    state.lists = p;
  },
  M_CREATE_LIST: (state, p) => {
    console.log(p);
  },
  M_UPDATE_LIST: (state, data) => {
    const index = state.lists.findIndex(list => list.id === data.id);
    const currentState = Array.from(state.lists);
    currentState[index] = {
      ...currentState[index],
      ...data
    };
    state.lists = currentState;
  },
  M_DELETE_LIST: (state, id) => {
    state.lists = state.lists.filter(list => list.id !== id);
  }
};

const getters = {
  GET_LISTS: state => {
    return state.lists;
  }
};

const actions = {
  LOAD_LISTS(context) {
    return new Promise((resolve, reject) => {
      getListsFromUser(context.rootGetters.GET_ACCESS_TOKEN)
        .then(res => {
          context.commit("M_SET_LISTS", res);
          resolve(true);
        })
        .catch(error => {
          reject(error);
        });
    });
  },
  LOAD_LIST_WITH_ID(context, id) {
    return new Promise((resolve, reject) => {
      getListWithId(id, context.rootGetters.GET_ACCESS_TOKEN)
        .then(res => {
          resolve(res);
        })
        .catch(error => reject(error));
    });
  },
  CREATE_LIST(context, data) {
    return new Promise((resolve, reject) => {
      const tweetsSource = context.rootGetters.GET_TWEETS_SOURCE;
      createList(
        {
          name: data.name,
          sourceType: tweetsSource.type,
          uuid: tweetsSource.uuid
        },
        context.rootGetters.GET_ACCESS_TOKEN
      )
        .then(() => {
          context.commit("M_CREATE_LIST", data);
          resolve(true);
        })
        .catch(error => reject(error));
    });
  },
  UPDATE_LIST(context, data) {
    return new Promise((resolve, reject) => {
      updateList(data, context.rootGetters.GET_ACCESS_TOKEN)
        .then(() => {
          context.commit("M_UPDATE_LIST", data);
          resolve(true);
        })
        .catch(error => {
          reject(error);
        });
    });
  },
  DELETE_LIST(context, id) {
    return new Promise((resolve, reject) => {
      deleteList(id, context.rootGetters.GET_ACCESS_TOKEN)
        .then(() => {
          context.commit("M_DELETE_LIST", id);
          resolve(true);
        })
        .catch(error => {
          reject(error);
        });
    });
  }
};

export default {
  state,
  mutations,
  getters,
  actions
};

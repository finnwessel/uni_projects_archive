const state = {
  layout: {
    masonry: true
  }
};

const mutations = {
  M_SET_LAYOUT: (state, p) => {
    state.layout = p;
  },
  TOGGLE_MASONRY_LAYOUT: state => {
    state.layout.masonry = !state.layout.masonry;
  }
};

const getters = {
  GET_LAYOUT: state => {
    return state.layout;
  }
};

const actions = {
  SET_LAYOUT(context, p) {
    context.commit("M_SET_LAYOUT", p);
  },
  TOGGLE_MASONRY_LAYOUT(context) {
    context.commit("M_TOGGLE_LAYOUT");
  }
};

export default {
  state,
  mutations,
  getters,
  actions
};

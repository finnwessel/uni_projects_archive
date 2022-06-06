<template>
  <div class="container">
    <div class="info">
      <p class="list-name">
        <input :readonly="!editMode" :disabled="!editMode" v-model="list.name"/>
        <span> | {{ list.tweets.length }} Tweets</span>
      </p>
      <button v-if="editMode" class="list-tweet-count" @click="updateList">
        Save list
      </button>
      <button v-else class="list-tweet-count" @click="setEditMode(true)">
        Edit list
      </button>
    </div>
  </div>
</template>

<script>
export default {
  name: "ListInfo",
  props: {
    list: Object,
    editMode: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      listName: this.$props.list.name
    };
  },
  beforeMount() {
    this.listName = this.$props.list.name;
  },
  methods: {
    setEditMode(bool) {
      this.$emit("set-edit-mode", bool);
    },
    updateList() {
      this.$emit("update-list-name", this.list.name);
      this.$emit("trigger-update-list");
      this.setEditMode(false);
    }
  }
};
</script>

<style lang="scss" scoped>
.container {
  align-items: center;
  text-align: center;
  padding-top: 8px;
}
.info {
  display: flex;
  margin: auto;
  width: 75%;
  color: #778585;
  background-color: #2e3f4f;
  border-radius: 5px;
  justify-content: space-between;

  .list-name {
    text-align: start;
    padding-left: 10px;
  }

  .list-tweet-count {
    padding-right: 10px;
  }
}
</style>

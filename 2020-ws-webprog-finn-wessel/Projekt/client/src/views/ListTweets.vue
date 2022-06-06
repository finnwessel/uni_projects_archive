<template>
  <div class="base-container">
    <ListInfo
      v-if="list"
      :list="list"
      :edit-mode="editMode"
      @update-list-name="editListName"
      @set-edit-mode="setEditMode"
      @trigger-update-list="saveList"
    ></ListInfo>
    <Masonry :tweets="list.tweets"></Masonry>
  </div>
</template>

<script>
import ListInfo from "@/components/ListInfo";
import Masonry from "@/views/Masonry";
import { errorNotify, infoNotify } from "@/helper/notify";

export default {
  name: "ListTweets",
  components: {
    ListInfo,
    Masonry
  },
  data() {
    return {
      list: {
        id: this.$route.params.id,
        name: null,
        tweets: []
      },
      initialListName: null,
      editedListName: null,
      editMode: false
    };
  },
  mounted() {
    this.loadListWithId();
    setTimeout(() => {
      let tweets = window.document.querySelectorAll(".tweet.card");
      [].map.call(tweets, el => {
        el.addEventListener("click", () => callBack(el), false);
      });
    }, 500);
    const callBack = elem => {
      if (this.editMode) {
        elem.classList.toggle("delete");
      }
    };
  },
  methods: {
    saveList() {
      let tweetElements = window.document.querySelectorAll(
        ".tweet.card.delete"
      );
      let tweets = [].slice.call(tweetElements).map(function(el) {
        return el.getAttribute("id");
      });

      let changedList = false;
      let data = {
        id: this.list.id
      };

      if (tweets.length > 0) {
        tweets.forEach(tweetId => {
          const index = this.list.tweets.findIndex(i => {
            return i.id === tweetId;
          });
          if (index >= 0) {
            this.list.tweets.splice(index, 1);
          }
        });
        data.tweets = this.list.tweets.map(function(a) {
          return { id: a.id };
        });
        changedList = true;
      }

      if (this.initialListName && this.initialListName !== this.list.name) {
        data.name = this.editedListName;
        changedList = true;
      }

      if (changedList) {
        this.$store
          .dispatch("UPDATE_LIST", data)
          .then(() => {
            infoNotify("Updated list.");
          })
          .catch(() => {
            errorNotify("Failed to update list.");
          });
      }
    },
    loadListWithId() {
      this.$store
        .dispatch("LOAD_LIST_WITH_ID", this.$route.params.id)
        .then(res => {
          this.list = res;
          this.initialListName = res.name;
        })
        .catch(error => {
          switch (error.status) {
            case 403:
              errorNotify("You are not allowed to view the list.");
              break;
            case 404:
              errorNotify("Requested list does not exist.");
              break;
            default:
              errorNotify("Failed loding list tweets");
          }
          this.$router.push({ name: "Lists" });
        });
    },
    setEditMode(value) {
      this.editMode = value;
    },
    editListName(value) {
      this.editedListName = value;
    }
  }
};
</script>

<style scoped>
.base-container {
  padding-top: 7vh;
}
</style>

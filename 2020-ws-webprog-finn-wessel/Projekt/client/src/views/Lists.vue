<template>
  <div>
    <div class="lists">
      <div class="header">
        <h3>List overview</h3>
      </div>
      <div class="content">
        <div
          v-for="(list, index) in lists"
          :key="list.id"
          class="row"
          :class="{ darken: index % 2 === 0 }"
        >
          <div class="column">
            <a :href="'/lists/' + list.id">
              <span style="padding-left: 10px">{{ list.name }}</span>
            </a>
          </div>
          <div class="column">
            <p>{{ formatDate(list.created_at) }}</p>
          </div>
          <div class="column">
            <button style="float: right" @click="deleteList(list.id)">
              <!-- Quelle: vue-material-design-icons -->
              <DeleteOutline
                :size="14"
                fill-color="darkred"
                title="Delete List"
              ></DeleteOutline>
              <!-- Quelle Ende: vue-material-design-icons -->
            </button>
            <button style="float: right" @click="toggleStatus(list)">
              <!-- Quelle: vue-material-design-icons -->
              <LockOffOutline
                v-if="list.isPublic"
                :size="14"
                fill-color="darkred"
                decorative
              ></LockOffOutline>
              <LockCheckOutline
                v-else
                :size="14"
                fill-color="darkgreen"
                title="Change visibility"
                decorative
              ></LockCheckOutline>
              <!-- Quelle Ende: vue-material-design-icons -->
            </button>
            <button style="float: right">
              <!-- Quelle: vue-material-design-icons -->
              <PlayCircleOutline
                @click="rerunSearchOrStream(list)"
                :size="14"
              ></PlayCircleOutline>
              <!-- Quelle Ende: vue-material-design-icons -->
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import dayjs from "dayjs";
import relativeTime from "dayjs/plugin/relativeTime";

import LockCheckOutline from "vue-material-design-icons/LockCheckOutline";
import LockOffOutline from "vue-material-design-icons/LockOffOutline";
import DeleteOutline from "vue-material-design-icons/DeleteOutline";
import PlayCircleOutline from "vue-material-design-icons/PlayCircleOutline";
import { errorNotify, infoNotify } from "@/helper/notify";
export default {
  name: "Lists",
  computed: {
    lists() {
      return this.$store.getters.GET_LISTS;
    }
  },
  beforeMount() {
    dayjs.extend(relativeTime);
    this.$store.dispatch("LOAD_LISTS");
  },
  components: {
    LockCheckOutline,
    LockOffOutline,
    DeleteOutline,
    PlayCircleOutline
  },
  methods: {
    sortedList(lists) {
      return lists.sort((a, b) => {
        return a.created_at < b.created_at ? 1 : -1;
      });
    },
    // Quelle: DayJs
    formatDate(date) {
      return dayjs(date).fromNow();
    },
    toggleStatus(list) {
      this.updateList({
        id: list.id,
        isPublic: !list.isPublic
      }).then(() => {
        infoNotify("Updated list visibility.");
      });
    },
    changeName(name, list) {
      this.updateList({
        id: list.id,
        name: name
      }).then(() => {
        infoNotify("Updated list name.");
      });
    },
    updateList(list) {
      return this.$store.dispatch("UPDATE_LIST", list);
    },
    deleteList(id) {
      this.$store.dispatch("DELETE_LIST", id);
    },
    rerunSearchOrStream(list) {
      if (list.sourceType === "search") {
        this.$store
          .dispatch("RERUN_SEARCH", list.sourceId)
          .then(() => {
            this.$router.push({ name: "Base" });
            infoNotify("Rerun search");
          })
          .catch(() => {
            errorNotify("Failed to rerun search");
          });
      } else if (list.sourceType === "stream") {
        this.$store
          .dispatch("RERUN_STREAM", list.sourceId)
          .then(() => {
            this.$router.push({ name: "Base" });
            infoNotify("Rerun stream");
          })
          .catch(() => {
            errorNotify("Failed to rerun stream");
          });
      }
    }
  }
};
</script>

<style lang="scss" scoped>
.lists {
  margin: auto;
  width: 50%;
  background-color: #778585;
  color: black;
}
.lists .header {
  background-color: #2e3f4f;
  h3 {
    padding: 10px;
  }
}
.row {
  width: 100%;
}
.column {
  width: 33%;
  display: inline-block;
}

.darken {
  box-shadow: inset 0 0 400px 110px rgba(0, 0, 0, 0.4);
}

@media only screen and (max-width: 1024px) {
  .lists {
    width: 75%;
  }
}
@media only screen and (max-width: 768px) {
  .lists {
    width: 85%;
  }
}
@media only screen and (max-width: 425px) {
  .lists {
    width: 95%;
  }
}
</style>

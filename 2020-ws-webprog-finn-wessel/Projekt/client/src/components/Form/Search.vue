<template>
  <div class="container">
    <div class="form">
      <input
        type="text"
        id="search"
        autocomplete="off"
        v-model="searchInput"
        v-on:keyup.enter.prevent="triggerSearch"
        :readonly="isStreamOpen"
        required
      />
      <label for="search" class="label-name">
        <span class="content-name">{{ apiOption }} keywords</span>
      </label>
    </div>
    <Button v-if="isStreamOpen" class="actionBtn" @click="closeStream">
      <!-- Quelle: vue-material-design-icons -->
      <CloseOctagonOutline
        fill-color="#af0505"
        title="Close Stream"
      ></CloseOctagonOutline>
      <!-- Quelle Ende: vue-material-design-icons -->
    </Button>
    <Button v-else id="modalBtn" class="actionBtn" @click="modalOpen = true">
      <!-- Quelle: vue-material-design-icons -->
      <Cog fill-color="#778585" title="Change Settings"></Cog>
      <!-- Quelle Ende: vue-material-design-icons -->
    </Button>
    <div id="myModal" class="modal" v-if="modalOpen">
      <!-- Modal content -->
      <div class="modal-content">
        <div class="modal-header">
          <span class="close" @click="modalOpen = false">&times;</span>
          <h3>Search Settings</h3>
        </div>
        <div class="modal-body">
          <div>
            <fieldset style="margin-top: 2px">
              <legend>Select Mode:</legend>
              <input
                v-model="apiOption"
                type="radio"
                id="search-option"
                name="api-option"
                value="search"
                :checked="apiOption === 'search'"
              />
              <label for="search-option">Search</label><br />
              <input
                v-model="apiOption"
                type="radio"
                id="stream-option"
                name="api-option"
                value="stream"
                :checked="apiOption === 'stream'"
              />
              <label for="stream-option">Stream</label><br />
            </fieldset>
            <fieldset v-if="apiOption === 'search'" style="margin-bottom: 10px">
              <legend>Select Number of results:</legend>
              <label for="search-quantity">Quantity (10 - 100):</label>
              <input
                type="number"
                id="search-quantity"
                name="search-quantity"
                min="10"
                max="100"
                v-model="searchSettings.maxResults"
              />
            </fieldset>
            <fieldset v-else style="margin-bottom: 10px">
              <legend>Select Number of tweets returned from stream:</legend>
              <label for="stream-quantity">Quantity (25 - 200):</label>
              <input
                type="number"
                id="stream-quantity"
                name="stream-quantity"
                min="25"
                max="200"
                v-model="streamSettings.maxResults"
              />
            </fieldset>
            <fieldset>
              <legend>Select Media types:</legend>
              <input
                id="checkboxPhoto"
                name="checkboxPhoto"
                type="button"
                class="triStateBtn"
                @click="changeCheckBoxStatus(checkboxes.images)"
                :value="tristate(checkboxes.images.value)"
                :disabled="apiOption === 'stream'"
              />
              <label for="checkboxPhoto"> Photos </label><br />
              <input
                id="checkboxVideo"
                name="checkboxVideo"
                type="button"
                class="triStateBtn"
                @click="changeCheckBoxStatus(checkboxes.videos)"
                :value="tristate(checkboxes.videos.value)"
                :disabled="apiOption === 'stream'"
              />
              <label for="checkboxVideo"> Videos </label><br />
            </fieldset>
            <fieldset>
              <legend>Extra Settings:</legend>
              <input
                id="checkboxRetweet"
                name="checkboxRetweet"
                type="button"
                class="triStateBtn"
                @click="changeCheckBoxStatus(checkboxes.retweets)"
                :value="tristate(checkboxes.retweets.value)"
                :disabled="apiOption === 'stream'"
              />
              <label for="checkboxVideo"> Retweets </label><br />
              <input
                id="checkboxMention"
                name="checkboxMention"
                type="button"
                class="triStateBtn"
                @click="changeCheckBoxStatus(checkboxes.mentions)"
                :value="tristate(checkboxes.mentions.value)"
                :disabled="apiOption === 'stream'"
              />
              <label for="checkboxVideo"> Mentions </label><br />
            </fieldset>
          </div>
        </div>
        <div class="modal-footer">
          <div style="float: right">
            <button style="color: #ae0101" @click="modalOpen = false">
              Cancel
            </button>
            <button @click.prevent="saveSettings">Save</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Cog from "vue-material-design-icons/Cog";
import CloseOctagonOutline from "vue-material-design-icons/CloseOctagonOutline";
import { errorNotify, infoNotify } from "@/helper/notify";
export default {
  name: "Search",
  components: {
    Cog,
    CloseOctagonOutline
  },
  data() {
    return {
      searchSettings: {
        maxResults: 10
      },
      streamSettings: {
        maxResults: 25
      },
      modalOpen: false,
      apiOption: "search",
      checkboxes: {
        images: {
          value: 0,
          min: 0,
          max: 2
        },
        videos: {
          value: 0,
          min: 0,
          max: 2
        },
        retweets: {
          value: 2,
          min: 0,
          max: 2
        },
        mentions: {
          value: 0,
          min: 0,
          max: 2
        }
      }
    };
  },
  methods: {
    triggerSearch() {
      if (this.apiOption === "search") {
        console.log(this.apiOption);
        this.$store
          .dispatch("EXECUTE_SEARCH")
          .then(() => {
            infoNotify("Started search");
          })
          .catch(() => {
            errorNotify("Failed to start the stream");
          });
      } else {
        this.$store
          .dispatch("START_STREAM")
          .then(() => {
            infoNotify("Stream search");
          })
          .catch(() => {
            errorNotify("Failed to execute the search");
          });
      }
    },
    closeStream() {
      this.$store.dispatch("STOP_STREAM").then(() => {
        infoNotify("Stream stopped");
      });
    },
    saveSettings() {
      this.$store.dispatch("SET_SEARCH_SETTINGS", {
        take: this.searchSettings.maxResults,
        images: this.checkboxes.images.value,
        videos: this.checkboxes.videos.value,
        retweets: 0
      });
      this.$store.dispatch("SET_STREAM_SETTINGS", {
        take: this.streamSettings.maxResults
      });
      this.modalOpen = false;
    },
    changeCheckBoxStatus(checkbox) {
      if (checkbox.value >= checkbox.max) {
        checkbox.value = checkbox.min;
      } else {
        checkbox.value++;
      }
    },
    tristate(value) {
      switch (value) {
        case 0:
          return "\u2753";
        case 1:
          return "\u2705";
        case 2:
          return "\u274C";
        default:
          return "\u2753";
      }
    }
  },
  computed: {
    searchInput: {
      get() {
        if (this.apiOption === "search") {
          return this.$store.getters.GET_SEARCH_KEYWORDS;
        } else {
          return this.$store.getters.GET_STREAM_KEYWORDS;
        }
      },
      set(value) {
        if (this.apiOption === "search") {
          this.$store.dispatch("SET_SEARCH_KEYWORDS", value);
        } else {
          this.$store.dispatch("SET_STREAM_KEYWORDS", value);
        }
      }
    },
    isStreamOpen() {
      return this.$store.getters.GET_STREAM_OPEN;
    }
  }
};
</script>

<style lang="scss" scoped>
.container {
  display: flex;
}

.actionBtn {
  background-color: transparent;
  font-size: 16px;
  border: none #778585;
  border-bottom: 1px solid;
  color: #778585;
  border-radius: 2px;
  outline: none;
  cursor: pointer;
  padding-top: 5px;
}

.modal {
  position: fixed;
  z-index: 1;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  overflow: auto;
  background-color: rgb(0, 0, 0);
  background-color: rgba(0, 0, 0, 0.4);
}

.modal-header {
  padding: 2px 16px;
  background-color: #2c3e50;
  color: #727265;
}

.modal-body {
  padding: 2px 16px;
}

.modal-footer {
  padding: 2px 16px;
  height: 42px;
  background-color: #2c3e50;
  color: white;
  button {
    margin-left: 5px;
    margin-top: 3px;
    margin-bottom: 3px;
    font-size: 16px;
    border: 1px solid #778585;
    color: #778585;
    background-color: #2c3e50;
    border-radius: 2px;
    padding: 0.5rem 1rem;
    outline: none;
    cursor: pointer;
  }
}

.modal-content {
  position: relative;
  background-color: #bdc3c7;
  top: 25%;
  margin: auto;
  padding: 0;
  border: 1px solid #888;
  width: 50%;
  box-shadow: 0 4px 8px 0 rgba(0, 0, 0, 0.2), 0 6px 20px 0 rgba(0, 0, 0, 0.19);
  animation-name: fadeInFromTop;
  animation-duration: 0.5s;
}

@keyframes fadeInFromTop {
  from {
    top: -300px;
    opacity: 0;
  }
  to {
    top: 25%;
    opacity: 1;
  }
}

.close {
  color: #aaa;
  float: right;
  font-size: 28px;
  font-weight: bold;
}

.close:hover,
.close:focus {
  color: #ae0101;
  text-decoration: none;
  cursor: pointer;
}

.form {
  position: relative;
  height: 80%;
  width: 100%;
  overflow: hidden;
  background-color: inherit;
}

.form input {
  width: 100%;
  height: 80%;
  color: white;
  padding-top: 20px;
  border: none;
  outline: none;
  background-color: inherit;
  font-size: 15px;
}

.form label {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 100%;
  color: #778585;
  pointer-events: none;
  border-bottom: 1px solid #778585;
  text-transform: capitalize;
}

.form label::after {
  content: "";
  position: absolute;
  left: -1px;
  bottom: -1px;
  height: 100%;
  width: 100%;
  border-bottom: 2px solid white;
  transform: translateX(-100%);
  transition: transform 0.3s ease;
}

.content-name {
  position: absolute;
  bottom: 5px;
  transition: all 0.3s ease;
}

.form input:focus + .label-name .content-name,
.form input:valid + .label-name .content-name,
.form input[readonly] + .label-name .content-name {
  transform: translateY(-100%);
  font-size: 14px;
  color: white;
}

.form input:focus .label-name::after,
.form input:valid + .label-name::after {
  transform: translateX(0%);
}

/* Media Queries */
@media screen and (max-width: 800px) {
  .modal-content {
    width: 75%;
  }
}

.triStateBtn {
  width: 40px;
}
</style>

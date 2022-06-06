<template>
  <div>
    <div class="container">
      <div class="message" @click="modalOpen = true">
        Hier klicken um die {{ tweet_count }} Tweets in einer Liste zu
        speichern.
      </div>
    </div>
    <Modal :is-open="modalOpen" @close="modalOpen = false">
      <h3 slot="header">Suche speichern</h3>
      <div slot="content">
        <label for="listName">Name für die Liste:</label>
        <input v-model="list.name" type="text" id="listName" name="listName" />
      </div>
      <div slot="footer" class="footer">
        <button class="saveBtn" @click="createList">
          Save
        </button>
      </div>
    </Modal>
  </div>
</template>

<script>
import Modal from "@/components/common/Modal";
import { errorNotify, successNotify } from "@/helper/notify";
export default {
  name: "ResultOptions",
  props: {
    tweet_count: {
      default: null
    },
    sourceType: {
      default: null
    },
    uuid: {
      default: null
    }
  },
  components: {
    Modal
  },
  data() {
    return {
      modalOpen: false,
      list: {
        name: null
      }
    };
  },
  methods: {
    createList() {
      if (this.list.name !== null) {
        this.$store
          .dispatch("CREATE_LIST", {
            name: this.list.name
          })
          .then(() => {
            successNotify(`Liste ${this.list.name} wurde erstellt.`);
            this.list.name = null;
          })
          .catch(() => {
            errorNotify("Liste konnte nicht erstell werden.");
          });
        this.modalOpen = false;
      }
    }
  }
};
</script>

<style scoped>
.container {
  align-items: center;
  text-align: center;
  padding-top: 8px;
}
.message {
  margin: auto;
  width: 50%;
  color: #778585;
  background-color: #2e3f4f;
  border-radius: 5px;
  cursor: pointer;
}

input[type="text"] {
  width: 100%;
  padding: 12px 20px;
  margin: 8px 0;
  box-sizing: border-box;
}

.modal-content {
  margin-top: 10px;
}

.footer {
  height: 40px;
}

.saveBtn {
  float: right;
  background-color: #7f8c8d;
  border: none;
  color: white;
  padding: 8px 16px;
  text-decoration: none;
  margin: 4px 4px;
  cursor: pointer;
}

@media only screen and (max-width: 630px) {
  .message {
    width: 90%;
  }
}
</style>

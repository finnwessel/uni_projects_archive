<template>
  <div>
    <div class="container">
      <img
        v-if="image != null"
        class="blurIfSensitive"
        :src="image.url"
        alt=""
        @loadeddata="$emit('loadeddata')"
        @click="modalOpen = true"
      />
    </div>
    <TweetCardMediaModal
      :open="modalOpen"
      @close="modalOpen = false"
    >
      <img slot="modal-content" :src="image.url" class="modal-content" alt="" />
    </TweetCardMediaModal>
  </div>
</template>

<script>
import TweetCardMediaModal from "@/components/TweetCard/Media/TweetCardMediaModal";
export default {
  name: "TweetCardImage",
  props: {
    image: {
      type: Object,
      required: false,
      default: null
    }
  },
  components: {
    TweetCardMediaModal
  },
  data() {
    return {
      modalOpen: false
    };
  }
};
</script>

<style scoped>
.container {
  position: relative;
  width: 100%;
}
.container:after {
  content: "";
  display: block;
  padding-bottom: 100%;
}

.container img {
  border-radius: 5px;
  position: absolute;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
  width: 100%;
  height: 100%;
  object-fit: cover; /* contain */
  object-position: center;
  cursor: pointer;
}
</style>

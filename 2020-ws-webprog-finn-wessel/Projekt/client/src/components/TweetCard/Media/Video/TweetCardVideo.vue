<template>
  <div>
    <div class="container">
      <img
        v-if="video != null"
        class="blurIfSensitive"
        :src="video.preview_image_url"
        alt=""
        @loadeddata="$emit('loadeddata')"
        @click="videoClicked(video)"
      />
      <img
        @click="videoClicked(video)"
        style="width: 33px; height:33px"
        :src="`${getBaseUrl()}/img/play_button.png`"
        class="thumb"
      />
    </div>
    <TweetCardMediaModal :open="modalOpen" @close="modalOpen = false">
      <div slot="modal-content" class="modal-video-container">
        <video style="height: 100%; width: auto" controls>
          <source :src="video.url" />
          Your browser does not support the videos.
        </video>
      </div>
    </TweetCardMediaModal>
  </div>
</template>

<script>
import TweetCardMediaModal from "@/components/TweetCard/Media/TweetCardMediaModal";
import { errorNotify, infoNotify } from "@/helper/notify";
export default {
  name: "TweetCardVideo",
  props: {
    tweetId: {
      type: String,
      required: false,
      default: null
    },
    video: {
      type: Object,
      required: false,
      default: null
    }
  },
  data() {
    return {
      modalOpen: false
    };
  },
  components: {
    TweetCardMediaModal
  },
  methods: {
    getBaseUrl() {
      return window.location.origin;
    },
    videoClicked(video) {
      if (video.url === null && video.url !== "none") {
        this.$store
          .dispatch("LOAD_TWEET_MEDIA", this.tweetId)
          .then(res => {
            this.video.url = res.media.find(
              media => media.media_key === video.media_key
            ).url;
            if (this.video.url === "none" || this.video.url === null) {
              errorNotify("Failed to load video");
            } else {
              infoNotify("Loaded Video Url");
              this.modalOpen = true;
            }
          })
          .catch(error => {
            console.log(error);
            errorNotify("Could not retrieve the video url");
          });
      } else {
        this.modalOpen = true;
      }
    }
  }
};
</script>

<style scoped>
img {
  max-width: 100%;
  border-radius: 5px;
}

.container {
  position: relative;
  width: 100%;
}
.container:after {
  content: "";
  display: block;
  padding-bottom: 100%;
}

.thumb {
  position: relative;
  margin: auto;
  width: 40px;
  height: 40px;
  left: 50%;
  top: 50%;
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

.modal-video-container {
  height: 70%;
  margin: auto;
  width: 70%;
  position: absolute;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
}
.modal-video-container video {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  margin: auto;
  padding-top: 60px;
}
</style>

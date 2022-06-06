<template>
  <!-- ToDo:  || media.type === 'animated_gif' -->
  <TweetCardImage
    v-if="media.type === 'photo' || handleGifs(media)"
    class="media"
    :image="media"
    @loadeddata="$emit('loadeddata')"
  ></TweetCardImage>
  <TweetCardVideo
    v-else-if="media.type === 'video'"
    class="media"
    :tweet-id="tweetId"
    :video="media"
    @loadeddata="$emit('loadeddata')"
  ></TweetCardVideo>
</template>

<script>
import TweetCardImage from "@/components/TweetCard/Media/Image/TweetCardImage";
import TweetCardVideo from "@/components/TweetCard/Media/Video/TweetCardVideo";
export default {
  name: "TweetCardMedia",
  components: {
    TweetCardImage,
    TweetCardVideo
  },
  props: {
    tweetId: {
      type: String,
      required: false,
      default: null
    },
    media: {
      type: Object,
      required: false,
      default: function() {
        return {
          type: null
        };
      }
    }
  },
  methods: {
    handleGifs(media){
      if (media.type === "animated_gif") {
        media.url = media.preview_image_url;
        return true;
      } else {
        return false;
      }
    }
  }
};
</script>

<style scoped></style>

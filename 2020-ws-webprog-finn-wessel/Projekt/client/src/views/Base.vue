<template>
  <div v-if="tweetsCount > 0">
    <ResultOptions :tweet_count="tweetsCount"></ResultOptions>
    <Masonry v-if="isMasonryLayout" :tweets="tweets"></Masonry>
    <Scroll v-else></Scroll>
  </div>
  <div v-else class="full-height">
    <div class="center">
      <div class="info">
        <p>
          No Tweets to display.<br />
          Start a new Stream or execute a new search.
        </p>
      </div>
    </div>
  </div>
</template>

<script>
import Masonry from "@/views/Masonry";
import Scroll from "@/views/Scroll";
import ResultOptions from "@/components/ResultOptions";

export default {
  name: "Base",
  components: {
    ResultOptions,
    Masonry,
    Scroll
  },
  computed: {
    isMasonryLayout() {
      return this.$store.getters.GET_LAYOUT.masonry;
    },
    tweetsCount() {
      return this.$store.getters.GET_TWEETS.length;
    },
    tweets() {
      return this.$store.getters.GET_TWEETS;
    }
  }
};
</script>

<style scoped>
.center {
  height: 100%;
  min-width: 100vw;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.full-height {
  padding-top: unset;
  height: 100%;
}

.info {
  background-color: #2c3e50;
  color: #7f8c8d;
  width: 30%;
  text-align: center;
  border-radius: 5px;
}

@media only screen and (max-width: 900px) {
  .base-container {
    padding-top: 18vh;
  }
}
</style>

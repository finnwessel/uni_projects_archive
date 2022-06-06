<template>
  <div class="masonry">
    <div class="masonry-item" v-for="(tweet, index) in tweets" :key="tweet.id">
      <TweetCard
        :tweet="tweet"
        @loadeddata="loadedMedia(index)"
      ></TweetCard>
    </div>
  </div>
</template>

<script>
import TweetCard from "@/components/TweetCard/TweetCard";
export default {
  name: "Masonry",
  components: { TweetCard },
  data() {
    return {};
  },
  props: {
    tweets: {
      type: Array,
    }
  },
  watch: {
    tweets: function() {
      if (this.tweets.length > 0) {
        this.$nextTick(() => {
          this.resizeAllMasonryItems();
        });
      }
    }
  },
  created() {
    window.addEventListener("load", this.resizeAllMasonryItems);
    window.addEventListener("resize", this.resizeAllMasonryItems);
  },
  mounted() {
    this.$nextTick(() => {
      this.resizeAllMasonryItems();
    });

    this.resizeAllMasonryItems();
  },
  beforeDestroy() {
    window.removeEventListener("resize", this.resizeAllMasonryItems);
    window.removeEventListener("load", this.resizeAllMasonryItems);
  },
  methods: {
    resizeMasonryItem(item) {
      const masonry = document.getElementsByClassName("masonry")[0];
      const masonryRowGap = parseInt(
        window.getComputedStyle(masonry).getPropertyValue("grid-row-gap")
      );
      const masonryRowHeight = parseInt(
        window.getComputedStyle(masonry).getPropertyValue("grid-auto-rows")
      );

      let rowSpan = Math.ceil(
        (item.querySelector(".card").getBoundingClientRect().height +
          masonryRowGap) /
          (masonryRowHeight + masonryRowGap)
      );
      item.style.gridRowEnd = "span " + rowSpan;
    },
    resizeAllMasonryItems() {
      const masonryItems = document.getElementsByClassName("masonry-item");
      for (let i = 0; i < masonryItems.length; i++) {
        this.resizeMasonryItem(masonryItems[i]);
      }
    },
    loadedMedia(index) {
      this.resizeMasonryItem(
        document.getElementsByClassName("masonry-item")[index]
      );
    }
  }
};
</script>

<style scoped>
.masonry {
  display: grid;
  grid-gap: 0.5em;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  grid-auto-rows: 0;
  padding: 0.5%;
}

.masonry-item {
}
</style>

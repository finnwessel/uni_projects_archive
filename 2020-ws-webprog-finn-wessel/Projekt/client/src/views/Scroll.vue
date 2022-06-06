<template>
  <div id="scroll-area">
    <div v-for="tweet in tweets" :key="tweet.id" class="side-space">
      <TweetCard
        class="fall-off-scroll"
        :tweet="tweet"
        :resize-callback="() => {}"
      ></TweetCard>
    </div>
  </div>
</template>

<script>
import TweetCard from "@/components/TweetCard/TweetCard";

// Constants
const OFFSET_TOP = 210;
const OFFSET_BOTTOM = 150;

export default {
  name: "Scroll",
  components: {
    TweetCard
  },
  data() {
    return {};
  },
  computed: {
    tweets() {
      return this.$store.getters.GET_TWEETS;
    }
  },
  mounted() {
    this.animationType("fall-off-scroll");
  },
  created() {
    window.addEventListener("scroll", () => {
      this.animationType("fall-off-scroll");
    });
  },
  methods: {
    animationType(name) {
      document.querySelectorAll(`.${name}`).forEach(element => {
        switch (name) {
          case "fall-off-scroll":
            this.toggleClass(element, "falloff");
            break;
        }
      });
    },

    toggleClass(object, name) {
      if (this.isVisible(object.parentElement)) {
        object.classList.add(name);
      } else {
        object.classList.remove(name);
      }
    },
    isVisible(object) {
      let viewport = Math.max(
        document.documentElement.clientHeight || 0,
        window.innerHeight || 0
      );
      let rand = object.getBoundingClientRect();
      return !(
        viewport < rand.top + OFFSET_BOTTOM ||
        document.body.scrollTop > rand.top + object.offsetHeight - OFFSET_TOP
      );
    }
  }
};
</script>

<style scoped>
.fall-off-scroll {
  margin-top: 1%;
  transition: all 500ms;
  opacity: 0;
  transform: scale(0.7) rotatex(180deg);
}

.falloff {
  opacity: 1;
  transform: scale(1);
}

.side-space {
  margin-left: 25%;
  margin-right: 25%;
}

@media only screen and (min-width: 1200px) {
  .side-space {
    margin-left: 30%;
    margin-right: 30%;
  }
}

@media only screen and (max-width: 800px) {
  .side-space {
    margin-left: 5%;
    margin-right: 5%;
  }
}
</style>

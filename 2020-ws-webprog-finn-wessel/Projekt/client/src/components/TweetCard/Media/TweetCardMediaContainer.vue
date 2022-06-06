<template>
  <div>
    <div v-if="tweetMedia.length > 1" class="media">
      <!-- Slideshow container -->
      <div class="slideshow-container">
        <!-- Full-width images with number and caption text -->
        <div
          class="media-slides fade"
          v-for="(oneMedia, index) in tweetMedia"
          :key="index"
          :style="{ display: [index === currentSlideNum ? 'block' : 'none'] }"
        >
          <div class="counter-text">
            {{ index + 1 }} / {{ tweetMedia.length }}
          </div>
          <TweetCardMedia
            :tweet-id="tweetId"
            :media="oneMedia"
            @loadeddata="$emit('loadeddata')"
          ></TweetCardMedia>
        </div>

        <!-- Next and previous buttons -->
        <a class="prev" @click="plusSlides(-1)">&#10094;</a>
        <a class="next" @click="plusSlides(1)">&#10095;</a>
      </div>
    </div>
    <div v-else-if="tweetMedia.length === 1" class="slideshow-container media">
      <TweetCardMedia
        :tweet-id="tweetId"
        :media="tweetMedia[0]"
        @loadeddata="$emit('loadeddata')"
      ></TweetCardMedia>
    </div>
  </div>
</template>

<script>
import TweetCardMedia from "@/components/TweetCard/Media/TweetCardMedia";
export default {
  name: "TweetCardMediaContainer",
  props: {
    tweetId: {
      type: String,
      required: false,
      default: null
    },
    tweetMedia: {
      type: Array,
      required: false,
      default: function() {
        return {
          type: []
        };
      }
    }
  },
  components: {
    TweetCardMedia
  },
  data() {
    return {
      currentSlideNum: 0
    };
  },
  methods: {
    plusSlides(num) {
      const nextSlideNum = (this.currentSlideNum += num);
      if (nextSlideNum < 0) {
        this.currentSlideNum = this.tweetMedia.length - 1;
      } else if (nextSlideNum > this.tweetMedia.length - 1) {
        this.currentSlideNum = 0;
      } else {
        this.currentSlideNum = nextSlideNum;
      }
    }
  }
};
</script>

<style scoped>
* {
  box-sizing: border-box;
}

/* Slideshow container */
.slideshow-container {
  max-width: 90%;
  position: relative;
  margin: auto;
  background-color: #3a454f;
  border-radius: 5px;
}

/* Hide the images by default */
.media-slides {
  display: none;
}

/* Next & previous buttons */
.prev,
.next {
  cursor: pointer;
  position: absolute;
  top: 50%;
  width: auto;
  margin-top: -22px;
  padding: 16px;
  color: #778585;
  font-weight: bold;
  font-size: 18px;
  transition: 0.6s ease;
  border-radius: 0 3px 3px 0;
  user-select: none;
}

/* Position the "next button" to the right */
.next {
  right: 0;
  border-radius: 3px 0 0 3px;
}

/* On hover, add a black background color with a little bit see-through */
.prev:hover,
.next:hover {
  background-color: rgba(0, 0, 0, 0.8);
}

/* Number text (1/3 etc) */
.counter-text {
  color: #778585;
  font-size: 12px;
  padding: 8px 12px;
  width: 100%;
  position: absolute;
  z-index: 2;
}

/* Fading animation  ToDo: fix animation
.fade {
  -webkit-animation-name: fade;
  -webkit-animation-duration: 1.5s;
  animation-name: fade;
  animation-duration: 1.5s;
}
 */

@-webkit-keyframes fade {
  from {
    opacity: 0.4;
  }
  to {
    opacity: 1;
  }
}

@keyframes fade {
  from {
    opacity: 0.4;
  }
  to {
    opacity: 1;
  }
}
</style>

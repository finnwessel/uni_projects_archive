<template>
  <div
    :id="tweet.id"
    class="tweet card"
    :class="{ 'possibly-sensitive': tweet.possibly_sensitive }"
  >
    <VTooltip> <!-- Quelle: v-tooltip  Betrifft eigentlich nur das Tag VTooltip und das Template Tag, der Rest ist eigene Leistung -->
      <div class="header">
        <img
          class="author-image"
          :src="
            tweet.author.profile_image_url ||
              'https://abs.twimg.com/sticky/default_profile_images/default_profile_400x400.png'
          "
          :alt="tweet.author.username"
        />
        <span class="author">
          <a
            :href="`https://twitter.com/${tweet.author.username}`"
            target="_blank"
            >{{ tweet.author.name.substring(0, 20) }}</a
          >
          <!-- Quelle: vue-material-design-icons -->
          <CheckDecagram
            v-if="tweet.author.verified"
            :size="16"
            fill-color="white"
          ></CheckDecagram>
          <!-- Quelle Ende: vue-material-design-icons -->
        </span>
      </div>
      <template #popper>
        <div class="tooltip">
          <p>@{{ tweet.author.username }}</p>
          <p v-if="tweet.author.location">
            <!-- Quelle: vue-material-design-icons -->
            <MapMarkerRadiusOutline
              class="icon"
              fill-color="#2E3F4F"
              :size="20"
              title="Tweet Count"
            ></MapMarkerRadiusOutline>
            <!-- Quelle Ende: vue-material-design-icons -->
            {{ tweet.author.location }}
          </p>
          <p v-if="tweet.author.description">{{ tweet.author.description }}</p>
          <div v-if="tweet.author.public_metrics" class="metrics">
            <div>
              <!-- Quelle: vue-material-design-icons -->
              <Typewriter
                class="icon"
                fill-color="#2E3F4F"
                :size="20"
                title="Tweet Count"
              ></Typewriter>
              <!-- Quelle Ende: vue-material-design-icons -->
              <!-- Quelle: kFormatter -->
              {{ kFormatter(tweet.author.public_metrics.tweet_count) }}
              <!-- Quelle Ende: kFormatter -->
            </div>
            <div>
              <!-- Quelle: vue-material-design-icons -->
              <AccountHeartOutline
                class="icon"
                fill-color="#2E3F4F"
                :size="20"
                title="Following Count"
              ></AccountHeartOutline>
              <!-- Quelle Ende: vue-material-design-icons -->
              <!-- Quelle: kFormatter -->
              {{ kFormatter(tweet.author.public_metrics.following_count) }}
              <!-- Quelle Ende: kFormatter -->
            </div>
            <div>
              <!-- Quelle: vue-material-design-icons -->
              <AccountGroupOutline
                class="icon"
                :size="20"
                fill-color="#2E3F4F"
                title="Followers Count"
              ></AccountGroupOutline>
              <!-- Quelle Ende: vue-material-design-icons -->
              <!-- Quelle: kFormatter -->
              {{ kFormatter(tweet.author.public_metrics.followers_count) }}
              <!-- Quelle Ende: kFormatter -->
            </div>
          </div>
        </div>
      </template>
    </VTooltip> <!-- Quelle Ende: v-tooltip -->
    <div class="content">
      <p v-html="insertMentions(insertLinks(tweet.text))"></p>
    </div>
    <TweetCardMediaContainer
      :tweet-id="tweet.id"
      :tweet-media="tweet.media"
      @loadeddata="$emit('loadeddata')"
    >
    </TweetCardMediaContainer>
    <div class="bottom">
      <div class="metrics">
        <!-- Quelle: vue-material-design-icons -->
        <Heart class="icon" fill-color="#778585" :size="20"></Heart>
        <!-- Quelle Ende: vue-material-design-icons -->
        <!-- Quelle: kFormatter -->
        <span> {{ kFormatter(tweet.public_metrics.like_count) }} </span>
        <!-- Quelle Ende: kFormatter -->
        <!-- Quelle: vue-material-design-icons -->
        <TwitterRetweet class="icon" fill-color="#778585"></TwitterRetweet>
        <!-- Quelle Ende: vue-material-design-icons -->
        <!-- Quelle: kFormatter -->
        <span> {{ kFormatter(tweet.public_metrics.retweet_count) }} </span>
        <!-- Quelle Ende: kFormatter -->
        <!-- Quelle: vue-material-design-icons -->
        <Reply class="icon" fill-color="#778585"></Reply>
        <!-- Quelle Ende: vue-material-design-icons -->
        <!-- Quelle: kFormatter -->
        <span> {{ kFormatter(tweet.public_metrics.reply_count) }} </span>
        <!-- Quelle Ende: kFormatter -->
      </div>
      <p class="date">{{ formatDate(tweet.created_at) }}</p>
    </div>
  </div>
</template>

<script>
import dayjs from "dayjs";
import relativeTime from "dayjs/plugin/relativeTime";
import TweetCardMediaContainer from "@/components/TweetCard/Media/TweetCardMediaContainer";
import Heart from "vue-material-design-icons/Heart";
import TwitterRetweet from "vue-material-design-icons/TwitterRetweet";
import Reply from "vue-material-design-icons/Reply";
import CheckDecagram from "vue-material-design-icons/CheckDecagram";
import AccountGroupOutline from "vue-material-design-icons/AccountGroupOutline";
import AccountHeartOutline from "vue-material-design-icons/AccountHeartOutline";
import Typewriter from "vue-material-design-icons/Typewriter";
import MapMarkerRadiusOutline from "vue-material-design-icons/MapMarkerRadiusOutline";

export default {
  name: "TweetCard",
  props: {
    tweet: {
      type: Object,
      required: true
    }
  },
  components: {
    TweetCardMediaContainer,
    Heart,
    TwitterRetweet,
    CheckDecagram,
    Reply,
    AccountGroupOutline,
    AccountHeartOutline,
    Typewriter,
    MapMarkerRadiusOutline
  },
  methods: {
    formatDate(date) {
      // Quelle: DayJs
      return dayjs(date).fromNow();
    },
    insertLinks(text) {
      if (this.tweet.urls.length > 0) {
        let checkPos = [];
        this.tweet.urls.forEach(u => {
          if (!checkPos.includes(u.start + u.end)) {
            text = text.replace(
              u.url,
              `<a href="${u.url}" target="_blank">${u.display_url}</a>`
            );
            checkPos.push(u.start + u.end);
          }
        });
      }
      return text;
    },
    insertMentions(text) {
      const pattern = /\B@[a-z0-9_-]+/gi;
      let mentions = text.match(pattern);
      if (mentions) {
        mentions.forEach(mention => {
          text = text.replace(
            mention,
            `<a href="https://twitter.com/${mention.substring(
              1,
              mention.length
            )}" target="_blank">${mention}</a>`
          );
        });
      }
      return text;
    },
    // Methode kFormatter https://stackoverflow.com/a/9461657/11356966
    kFormatter(num) {
      return Math.abs(num) > 999
        ? Math.sign(num) * (Math.abs(num) / 1000).toFixed(1) + "k"
        : Math.sign(num) * Math.abs(num);
    }
  },
  beforeMount() {
    dayjs.extend(relativeTime);
  }
};
</script>
<style lang="scss">
.delete {
  background-color: #f13030 !important;
  border-color: #4f0000;
  border-radius: 3px;
}
a {
  color: black;
  white-space: pre-wrap; /* CSS3 */
  white-space: -moz-pre-wrap; /* Mozilla, since 1999 */
  white-space: -pre-wrap; /* Opera 4-6 */
  white-space: -o-pre-wrap; /* Opera 7 */
  word-wrap: break-word; /* Internet Explorer 5.5+ */
}
.possibly-sensitive {
  box-shadow: 0 1px 3px 0 #c4003d, 0 0 0 1px #4f0000;
  .content p {
    display: inline;
    background-repeat: no-repeat;
    transition: all 500ms ease-in-out;
    color: transparent;
    background-position: right;
    background-size: 100% 100%;
    background-image: linear-gradient(#778585, #778585);
    a {
      color: transparent;
    }
    &:hover {
      color: black;
      background-size: 0 100%;
      a {
        color: black;
      }
    }
  }

  .media {
    overflow: hidden;
  }

  .media .blurIfSensitive {
    filter: blur(15px);
    -moz-filter: blur(15px);
    -o-filter: blur(15px);
    -ms-filter: blur(15px);
    &:hover {
      filter: blur(0px);
      -moz-filter: blur(0px);
      -o-filter: blur(0px);
      -ms-filter: blur(0px);
    }
  }
}
</style>
<style lang="scss" scoped>
@import url("https://fonts.googleapis.com/css?family=Markazi Text");
.card {
  box-shadow: 0 1px 3px 0 #989ca2, 0 0 0 1px #979a9f;
  background-color: #bdc3c7;
  max-width: 100%;
  width: 100%;
  min-width: 100%;
  border-radius: 5px;
  font-size: 1.2em;
  line-height: 1.4285em;
  font-family: "Markazi Text", sans-serif;
}

.card .author {
  text-align: left;
  margin: 1.2em 0 0 0.2em;
}

.card .header {
  display: flex;
  align-items: center;
  height: 30px;
}

.author-image {
  border-radius: 50%;
  margin: 1em 0 0 1em;
  width: 30px;
  height: 30px;
}

.card .content {
  text-align: left;
  margin-bottom: 0;
  padding: 4%;
}

.card .bottom {
  outline: 0;
  border-radius: 5px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  margin: 0;
}

.card .bottom .date {
  margin-right: 10px;
  font-size: 0.75em;
}

.card .media {
  display: block;
  margin-left: auto;
  margin-right: auto;
}

.card .metrics {
  font-size: 0.75em;
  display: flex;
  align-items: center;
}

.card .icon {
  margin-left: 1em;
  margin-top: 0.5em;
}

.tooltip {
  background-color: #a0a9ae;
  min-width: 300px;
  max-width: 300px;
  height: 100%;
  border-radius: 5px;
  border-style: solid;
  border-width: 2px;
  border-color: #2e3f4f;
  font-family: "Markazi Text", sans-serif;

  .metrics {
    padding: 5px;
    font-size: 1em;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  p {
    padding: 5px;
    line-break: auto;
    margin-bottom: 5px;
    margin-top: 5px;
  }
}
</style>

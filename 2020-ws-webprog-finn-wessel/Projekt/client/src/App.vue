<template>
  <div id="app">
    <Navigation v-if="loggedIn" id="nav"></Navigation>
    <SideNavigation id="sidenav"></SideNavigation>
    <router-view class="router-content" />
  </div>
</template>
<script>
import Navigation from "@/components/navigation/Navigation";
import SideNavigation from "@/components/navigation/SideNavigation";
export default {
  name: "App",
  components: {
    SideNavigation,
    Navigation
  },
  mounted() {
    this.$store.dispatch("AUTO_REFRESH");
  },
  computed: {
    loggedIn() {
      return this.$store.getters.GET_USER_LOGGED_IN;
    }
  }
};
</script>
<style lang="scss">
@import url("https://fonts.googleapis.com/css?family=Amaranth");

body {
  background: #7f8c8d;
  margin: 0;
  padding: 0;
}

#app {
  height: 100vh;
}
#nav {
  height: 10vh;
}
#sidenav {
  margin-top: 10vh;
}

@media screen and (min-width: 850px) {
  #nav {
    height: 7vh;
  }
  .router-content {
    padding-top: 7vh;
  }
  #sidenav {
    margin-top: 7vh;
  }
}

@media only screen and (max-width: 900px) {
  #nav {
    height: 18vh;
  }
  .router-content {
    padding-top: 18vh;
  }
  #sidenav {
    margin-top: 18vh;
  }
}
</style>

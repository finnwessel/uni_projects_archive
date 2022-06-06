<template>
  <div class="header">
    <div class="left">
      <span class="logo" @click="toggleNav()">Twitter App</span>
    </div>
    <div class="middle">
      <Search v-if="isSearchPage"></Search>
    </div>
    <div class="right">
      <div class="desktop-menu">
        <UserMenu></UserMenu>
      </div>
    </div>
  </div>
</template>

<script>
import Search from "@/components/Form/Search";
import UserMenu from "@/components/UserMenu";
export default {
  name: "Navigation",
  data() {
    return {};
  },
  methods: {
    toggleNav() {
      document.getElementById("sidenav").classList.toggle("open");
    }
  },
  computed: {
    isSearchPage() {
      return this.$route.name === "Base";
    }
  },
  components: {
    Search,
    UserMenu
  }
};
</script>

<style lang="scss" scoped>
.header {
  height: 100%;
  width: 100%;
  display: flex;
  flex-flow: row wrap;
  justify-content: space-between;
  position: fixed;
  top: 0;
  left: 0;
  z-index: 99;
  align-items: center;
  background-color: #2c3e50;
  font-family: "Amaranth", sans-serif;
}

.header .logo {
  margin-left: 2%;
  border: 1px solid #778585;
  color: #778585;
  padding: 0.5rem 1rem;
  cursor: pointer;
}

.header .left {
  width: 33.33%;
}
.header .middle {
  width: 33.33%;
}
.header .right {
  width: 33.33%;
}

.header .desktop-menu {
  margin-right: 2%;
  display: flex;
  float: right;
}
@media only screen and (max-width: 1200px) {
  .header .left {
    width: 25%;
  }
  .header .middle {
    width: 50%;
  }
  .header .right {
    width: 25%;
  }
}
@media only screen and (max-width: 900px) {
  .header {
    justify-content: space-between;
  }

  .header .left {
    width: 50%;
    order: 1;
  }
  .header .middle {
    width: 100%;
    order: 3;
  }
  .header .right {
    width: 50%;
    order: 2;
  }
}

.v-center {
  display: flex;
  align-items: center;
}
</style>

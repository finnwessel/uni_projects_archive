<template>
  <div class="sidenav" id="sidenav">
    <span class="divider">Home</span>
    <a href="/">Tweets</a>
    <a href="/lists">Lists</a>
    <span class="divider">Settings</span>
    <button
      v-if="isMasonryLayout"
      @click="setMasonryLayout(false)"
      class="v-center"
    >
      <TableColumn class="icon" fill-color="#2c3e50"></TableColumn>
      <span>Simple Layout</span>
    </button>
    <button v-else @click="setMasonryLayout(true)" class="v-center">
      <Grid class="icon" fill-color="#2c3e50"></Grid>
      <span>Masonry Layout</span>
    </button>
  </div>
</template>

<script>
import Grid from "vue-material-design-icons/Grid";
import TableColumn from "vue-material-design-icons/TableColumn";

export default {
  name: "SideNavigation",
  data() {
    return {};
  },
  computed: {
    isMasonryLayout() {
      return this.$store.getters.GET_LAYOUT.masonry;
    }
  },
  methods: {
    setMasonryLayout(boolean) {
      this.$store.dispatch("SET_LAYOUT", {
        masonry: boolean
      });
    }
  },
  mounted() {
    /*
    const sidenav = document.getElementById("sidenav");
    window.onclick = function(event) {
      if (event.target !== sidenav) {
        sidenav.classList.remove("open");
      }
    }
     */
  },
  components: {
    Grid,
    TableColumn
  }
};
</script>

<style lang="scss" scoped>
.sidenav {
  height: 100%;
  width: 0;
  position: fixed;
  z-index: 20;
  top: 0;
  left: 0;
  background-color: #2e3f4f;
  overflow-x: hidden;
  padding-top: 15px;
  transition: 0.2s;
}

.sidenav a,
.sidenav button{
  padding: 8px 8px 8px 32px;
  text-decoration: none;
  font-size: 25px;
  color: #818181;
  display: block;
  transition: 0.1s;
}

.sidenav button {
  margin: auto;
}

.sidenav a:hover {
  color: #f1f1f1;
}

#main {
  transition: ease 0.2s;
  padding: 20px;
}

.open {
  width: 250px;
}

.divider {
  color: #fff;
  font-size: 3vh;
  text-shadow: 1px 1px 1px rgba(0, 0, 0, 0.45);

  display: flex;
  justify-content: center;
  align-items: center;

  margin-top: 1vh;
  margin-bottom: 1vh;
  &::before,
  &::after {
    content: "";
    display: block;
    height: 0.09em;
    min-width: 200px;
  }

  &::before {
    background: linear-gradient(to right, rgba(240, 240, 240, 0), #95a5a6);
    margin-right: 2vh;
  }

  &::after {
    background: linear-gradient(to left, rgba(240, 240, 240, 0), #95a5a6);
    margin-left: 2vh;
  }
}

@media screen and (max-height: 450px) {
  .sidenav {
    padding-top: 15px;
  }
  .sidenav a {
    font-size: 18px;
  }
}
</style>

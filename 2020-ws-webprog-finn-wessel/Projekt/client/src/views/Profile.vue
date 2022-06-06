<template>
  <div class="center-screen">
    <div class="card">
      <div class="tab">
        <button class="tab-link" @click="openTab('info')">Info</button>
        <button class="tab-link" @click="openTab('statistic')">
          Statistic
        </button>
        <button class="tab-link" @click="openTab('security')">Security</button>
        <button class="tab-link" @click="openTab('settings')">Settings</button>
      </div>
      <div v-if="openTabName === 'info'" class="tab-content">
        <h3>Info</h3>
        <p>{{ user.username }} {{ user.email }}</p>
      </div>

      <div v-else-if="openTabName === 'statistic'" class="tab-content">
        <h3>Statistic</h3>
        <p>Statistic content.</p>
      </div>

      <div v-else-if="openTabName === 'security'" class="tab-content">
        <h3>Security</h3>
        <p>Security content.</p>
      </div>

      <div v-else class="tab-content">
        <h3>Settings</h3>
        <p>Settings content.</p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Profile.vue",
  data() {
    return {
      openTabName: "info"
    };
  },
  computed: {
    user() {
      return this.$store.getters.GET_USER;
    }
  },
  methods: {
    openTab(tabName) {
      this.openTabName = tabName;
    }
  }
};
</script>

<style scoped>
.center-screen {
  padding-top: unset;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
  min-height: 100vh;
}

.card {
  box-shadow: 0 1px 3px 0 #989ca2, 0 0 0 1px #979a9f;
  background-color: #bdc3c7;
  max-width: 75vw;
  min-width: 75vw;
  min-height: 75vh;
  border-radius: 5px;
  font-size: 1.2em;
  line-height: 1.4285em;
}

.tab {
  overflow: hidden;
  width: 100%;
  border: 1px solid #ccc;
  background-color: #f1f1f1;
}

.tab button {
  width: 25%;
  background-color: inherit;
  float: left;
  border: none;
  outline: none;
  cursor: pointer;
  padding: 14px 16px;
  transition: 0.3s;
}

.tab button:hover {
  background-color: #ddd;
}

.tab button.active {
  background-color: #373e51;
}

@media screen and (max-width: 1500px) {
  .card {
    min-width: 40%;
  }
}

@media screen and (max-width: 1000px) {
  .card {
    min-width: 55%;
  }
}

@media screen and (max-width: 750px) {
  .card {
    min-width: 95%;
  }
}
</style>

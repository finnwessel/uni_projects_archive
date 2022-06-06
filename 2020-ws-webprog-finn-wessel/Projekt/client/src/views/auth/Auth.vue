<template>
  <div class="center-screen">
    <div class="card">
      <div class="tab">
        <button @click="loginActive = true" :class="{ active: loginActive }">
          <b>Login</b>
        </button>
        <button @click="loginActive = false" :class="{ active: !loginActive }">
          <b>Register</b>
        </button>
      </div>

      <div v-if="loginActive">
        <Login></Login>
      </div>
      <div v-else>
        <Register></Register>
      </div>
    </div>
  </div>
</template>

<script>
import Login from "@/views/auth/Login";
import Register from "@/views/auth/Register";

export default {
  name: "Auth",
  components: {
    Login,
    Register
  },
  data() {
    return {
      loginActive: true,
      prevRoute: null
    };
  },
  beforeRouteEnter(to, from, next) {
    next(vm => {
      vm.prevRoute = from;
    });
  },
  beforeMount() {
    this.$store
      .dispatch("REFRESH_ACCESS_TOKEN")
      .then(refreshedToken => {
        if (refreshedToken) {
          if (this.prevRoute) {
            this.$router.push(this.prevRoute);
          } else {
            this.$router.push({ name: "Base" });
          }
        }
      })
      .catch(error => {
        console.log(error);
      });
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
  min-height: 100%;
  min-width: 100vw;
}

.card {
  box-shadow: 0 1px 3px 0 #989ca2, 0 0 0 1px #979a9f;
  background-color: #bdc3c7;
  max-width: 30%;
  min-width: 30%;
  min-height: 200px;
  border-radius: 5px;
  font-size: 1.2em;
  line-height: 1.5em;
}

.tab {
  overflow: hidden;
  width: 100%;
  border: 1px solid #ccc;
  background-color: #f1f1f1;
}

.tab button {
  width: 50%;
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

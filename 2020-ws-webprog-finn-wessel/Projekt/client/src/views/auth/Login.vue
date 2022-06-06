<template>
  <form @keypress.enter.prevent="login">
    <div class="container">
      <label for="usernameOrEmail">Username or Email</label>
      <input
        type="text"
        placeholder="Enter Username or Email"
        id="usernameOrEmail"
        name="usernameOrEmail"
        required
        v-model="identifier"
      />

      <label for="password">Password</label>
      <input
        type="password"
        placeholder="Enter Password"
        id="password"
        name="password"
        required
        v-model="password"
      />
      <button type="submit" @click.prevent="login">Login</button>
    </div>
  </form>
</template>

<script>
import { errorNotify } from "@/helper/notify";
export default {
  name: "Login",
  data() {
    return {
      identifier: "",
      password: ""
    };
  },
  methods: {
    login() {
      this.$store
        .dispatch("LOGIN_USER", {
          identifier: this.identifier,
          password: this.password
        })
        .then(() => {
          this.$router.push("/");
        })
        .catch(error => {
          console.log(error);
          if (error.status === 401) {
            errorNotify("Your login credentials seem incorrect.");
          } else {
            errorNotify("Failed to verify your credentials.");
          }
        });
    }
  }
};
</script>

<style scoped>
input[type="text"],
input[type="password"] {
  width: 100%;
  padding: 12px 20px;
  margin: 8px 0;
  display: inline-block;
  border: 1px solid #2c3e50;
  box-sizing: border-box;
}

label {
  float: left;
}

button {
  background-color: #2c3e50;
  color: white;
  padding: 14px 20px;
  margin: 8px 0;
  border: none;
  cursor: pointer;
  width: 100%;
}

button:hover {
  opacity: 0.8;
}

.container {
  padding: 16px;
}
</style>

<template>
  <form @keypress.enter.prevent="register">
    <div class="container">
      <label for="username">Username</label>
      <input
        type="text"
        placeholder="Enter Username"
        id="username"
        name="username"
        v-model="username"
        required
      />

      <label for="email">Email</label>
      <input
        type="email"
        placeholder="Enter Email"
        id="email"
        name="email"
        v-model="email"
        required
      />

      <label for="password">Password</label>
      <input
        type="password"
        placeholder="Enter Password"
        id="password"
        name="password"
        v-model="password"
        required
      />

      <label for="repeatedPassword">Repeat Password</label>
      <input
        type="password"
        placeholder="Enter Password"
        id="repeatedPassword"
        name="repeatedPassword"
        v-model="repeatedPassword"
        required
      />
      <button type="submit" @click.prevent="register">Login</button>
    </div>
  </form>
</template>

<script>
import { errorNotify } from "@/helper/notify";

export default {
  name: "Register",
  data() {
    return {
      username: "",
      email: "",
      password: "",
      repeatedPassword: ""
    };
  },
  methods: {
    register() {
      if (this.password === this.repeatedPassword) {
        this.$store
          .dispatch("REGISTER_USER", {
            email: this.email,
            username: this.username,
            password: this.password
          })
          .then(() => {
            this.$router.push("/");
          })
          .catch(error => {
            if (error.status === 409) {
              errorNotify("Username or email already taken");
            } else {
              errorNotify("Failed to register");
            }
          });
      } else {
        errorNotify("Your passwords do not match");
      }
    }
  }
};
</script>

<style scoped>
input[type="text"],
input[type="password"],
input[type="email"] {
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

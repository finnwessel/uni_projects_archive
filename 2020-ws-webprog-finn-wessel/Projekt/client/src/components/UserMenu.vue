<template>
  <div class="dropdown">
    <button>{{ user.username }}</button>
    <div class="dropdown-links">
      <a href="/profile">Profile</a>
      <a id="logout" href="/logout">Logout</a>
    </div>
  </div>
</template>

<script>
export default {
  name: "UserMenu",
  computed: {
    user() {
      return this.$store.getters.GET_USER;
    }
  }
};
</script>

<style scoped>
button {
  font-size: 16px;
  border: 1px solid #778585;
  color: #778585;
  background-color: #2c3e50;
  border-radius: 2px;
  padding: 0.5rem 1rem;
  outline: none;
  cursor: pointer;
}

.dropdown {
  position: relative;
  display: inline-block;
}

.dropdown-links {
  display: none;
  position: absolute;
  right: 0;
  background-color: #bdc3c7;
  min-width: 60px;
  box-shadow: 0 8px 16px 0 rgba(0, 0, 0, 0.2);
  z-index: 1;
}
#logout {
  color: #8f0101;
}

.dropdown-links a {
  color: black;
  padding: 12px 16px;
  text-decoration: none;
  display: block;
}

.dropdown-links a:hover {
  background-color: #c8ced2;
}

.dropdown:hover .dropdown-links {
  display: block;
}

.dropdown:hover button {
  background-color: #344353;
}
</style>

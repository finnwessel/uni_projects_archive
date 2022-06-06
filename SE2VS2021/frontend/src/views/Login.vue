<template>
  <div class="flex justify-content-center">
    <Card class="card my-5">
      <template #content>
        <div class="form p-d-inline-flex p-shadow-2 p-p-5 p-mt-lg-6">
          <h4 class="p-text-center">Login</h4>
          <form @submit.prevent="handleSubmit()" class="p-fluid">
            <div class="p-field">
              <div class="p-float-label">
                <InputText
                  id="name"
                  v-model="v$.username.$model"
                  :class="{ 'p-invalid': v$.username.$invalid && submitted }"
                />
                <label :class="{ 'p-error': v$.username.$invalid && submitted }"
                  >Username</label
                >
              </div>
              <small
                v-if="
                  (v$.username.$invalid && submitted) ||
                  v$.username.$pending.$response
                "
                class="p-error"
                >{{
                  v$.username.required.$message.replace("Value", "Name")
                }}</small
              >
            </div>
            <div class="p-field">
              <div class="p-float-label">
                <Password
                  id="password"
                  v-model="v$.password.$model"
                  :class="{ 'p-invalid': v$.password.$invalid && submitted }"
                  :feedback="false"
                >
                </Password>
                <label :class="{ 'p-error': v$.password.$invalid && submitted }"
                  >Password</label
                >
              </div>
              <small
                v-if="
                  (v$.password.$invalid && submitted) ||
                  v$.password.$pending.$response
                "
                class="p-error"
                >{{
                  v$.password.required.$message.replace("Value", "Password")
                }}</small
              >
            </div>
            <p>
              No account yet?<router-link to="/register"
                ><a href="" class="mx-2">Register here</a></router-link
              >
            </p>
            <Button type="submit" label="Login" class="p-mt-2" />
          </form>
        </div>
      </template>
    </Card>
  </div>
</template>

<script lang="ts">
import { required } from "@vuelidate/validators";
import { useVuelidate } from "@vuelidate/core";
import router from "@/router";
import { useAuthStore } from "@/store/AuthStore";
import { defineComponent } from "vue";

export default defineComponent({
  name: "Login",
  data() {
    return {
      username: "",
      password: "",
      submitted: false,
    };
  },
  setup() {
    const authStore = useAuthStore();
    return {
      v$: useVuelidate(),
      authStore,
    };
  },
  validations() {
    return {
      username: {
        required,
      },
      password: {
        required,
      },
    };
  },
  methods: {
    handleSubmit(): void {
      this.submitted = true;
      const result = !this.v$.$invalid;

      if (result) {
        const credentials = {
          username: this.username,
          password: this.password,
        };
        this.authStore
          .loginUser(credentials)
          .then((res) => {
            if (res) {
              router.push("/");
              this.resetForm();
            }
          })
          .catch((err) => console.log(err));
      }
    },
    resetForm(): void {
      this.username = "";
      this.password = "";
      this.submitted = false;
    },
  },
});
</script>

<style scoped>
.card {
  max-width: 450px;
}

form {
  margin-top: 2rem;
}

.p-field {
  margin-bottom: 1.5rem;
}

@media screen and (max-width: 960px) {
  .card {
    width: 100%;
  }
}
</style>

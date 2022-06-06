<template>
  <div class="flex justify-content-center">
    <Card class="card my-5">
      <template #content>
        <div class="form">
          <Dialog
            v-model:visible="showMessage"
            :breakpoints="{ '960px': '80vw' }"
            :style="{ width: '30vw' }"
            position="top"
          >
            <div class="flex align-items-center">
              <i
                class="pi pi-check-circle"
                :style="{ fontSize: '5rem', color: 'var(--green-500)' }"
              ></i>
              <h5>Registration Successful!</h5>
              <p :style="{ lineHeight: 1.5, textIndent: '1rem' }">
                Your account is registered under name <b>{{ username }}</b>
              </p>
            </div>
            <template #footer>
              <div class="p-d-flex p-jc-center">
                <Button
                  label="OK"
                  @click="toggleDialog"
                  class="p-button-text"
                />
              </div>
            </template>
          </Dialog>

          <h4>Register</h4>
          <form @submit.prevent="handleSubmit()" class="p-fluid">
            <div class="p-field">
              <div class="p-float-label">
                <InputText
                  id="email"
                  v-model="v$.email.$model"
                  :class="{ 'p-invalid': v$.email.$invalid && submitted }"
                />
                <label :class="{ 'p-error': v$.email.$invalid && submitted }"
                  >eMail</label
                >
              </div>
              <small
                v-if="
                  (v$.email.$invalid && submitted) ||
                  v$.email.$pending.$response
                "
                class="p-error"
                >{{
                  v$.email.required.$message.replace("Value", "eMail")
                }}</small
              >
            </div>

            <div class="p-field">
              <div class="p-float-label">
                <InputText
                  id="firstname"
                  v-model="v$.firstname.$model"
                  :class="{ 'p-invalid': v$.firstname.$invalid && submitted }"
                />
                <label
                  :class="{ 'p-error': v$.firstname.$invalid && submitted }"
                  >First name</label
                >
              </div>
              <small
                v-if="
                  (v$.firstname.$invalid && submitted) ||
                  v$.firstname.$pending.$response
                "
                class="p-error"
                >{{
                  v$.firstname.required.$message.replace("Value", "Firstname")
                }}</small
              >
            </div>

            <div class="p-field">
              <div class="p-float-label">
                <InputText
                  id="lastname"
                  v-model="v$.lastname.$model"
                  :class="{ 'p-invalid': v$.lastname.$invalid && submitted }"
                />
                <label :class="{ 'p-error': v$.lastname.$invalid && submitted }"
                  >Last name</label
                >
              </div>
              <small
                v-if="
                  (v$.lastname.$invalid && submitted) ||
                  v$.lastname.$pending.$response
                "
                class="p-error"
                >{{
                  v$.lastname.required.$message.replace("Value", "Last name")
                }}</small
              >
            </div>
            <div class="p-field">
              <div class="p-float-label">
                <InputText
                  id="username"
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
                  v$.username.required.$message.replace("Value", "Username")
                }}</small
              >
            </div>
            <div class="p-field">
              <div class="p-float-label">
                <Password
                  id="password"
                  v-model="v$.password.$model"
                  :class="{ 'p-invalid': v$.password.$invalid && submitted }"
                  toggleMask
                >
                  <template #header>
                    <h6>Pick a password</h6>
                  </template>
                  <template #footer="sp">
                    {{ sp.level }}
                    <Divider />
                    <p class="p-mt-2">Suggestions</p>
                    <ul class="p-pl-2 p-ml-2 p-mt-0" style="line-height: 1.5">
                      <li>At least one lowercase</li>
                      <li>At least one uppercase</li>
                      <li>At least one numeric</li>
                      <li>Minimum 8 characters</li>
                    </ul>
                  </template>
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
            <Button type="submit" label="Register" class="p-mt-2" />
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
import { defineComponent } from "vue";
import { useAuthStore } from "@/store/AuthStore";

export default defineComponent({
  name: "Register",
  data() {
    return {
      firstname: "",
      lastname: "",
      username: "",
      password: "",
      email: "",
      submitted: false as boolean,
      showMessage: false as boolean,
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
      firstname: {
        required,
      },
      lastname: {
        required,
      },
      username: {
        required,
      },
      password: {
        required,
      },
      email: {
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
          firstname: this.firstname,
          lastname: this.lastname,
          username: this.username,
          password: this.password,
          email: this.email,
        };
        this.authStore
          .registerUser(credentials)
          .then((res) => {
            if (res) {
              this.toggleDialog();
            }
            // ToDo: Show error
          })
          .catch((err) => {
            console.log(err);
          });
      }
    },
    toggleDialog(): void {
      this.showMessage = !this.showMessage;

      if (!this.showMessage) {
        this.forward();
      }
    },
    forward(): void {
      router.push({ name: "Login" });
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
    width: 80%;
  }
}
</style>

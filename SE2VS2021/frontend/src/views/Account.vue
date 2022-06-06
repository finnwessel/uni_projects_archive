<template>
  <div class="flex justify-content-center">
    <Dialog
      v-model:visible="showMessage"
      :breakpoints="{ '960px': '80vw' }"
      :style="{ width: '30vw' }"
      position="top"
    >
      <i
        class="pi pi-check-circle"
        :style="{ fontSize: '5rem', color: 'var(--green-500)' }"
      ></i>
      <h5>Registration Successful!</h5>
      <p :style="{ lineHeight: 1.5, textIndent: '1rem' }">
        Your account was successfully updated!
      </p>
      <template #footer>
        <div class="p-d-flex p-jc-center">
          <Button label="OK" @click="toggleDialog" class="p-button-text" />
        </div>
      </template>
    </Dialog>

    <div class="account mt-5">
      <Fieldset legend="Account">
        <div class="flex flex-column">
          <Avatar
            v-if="!account.avatar"
            :label="account.username[0]"
            class="flex align-self-center"
            size="xlarge"
            shape="circle"
          />
          <Avatar
            v-if="account.avatar"
            :image="account.avatar"
            class="flex align-self-center"
            size="xlarge"
            shape="circle"
          />
          <div class="flex my-3 align-self-center">
            <FileUpload
              mode="basic"
              name="demo[]"
              url="./upload"
              accept="image/*"
              :customUpload="true"
              @uploader="uploadAvatar"
            >
            </FileUpload>
          </div>
        </div>
        <form @submit.prevent="handleSubmit()" class="p-fluid">
          <div>Username:</div>
          <InputText
            id="username"
            v-model="v$.account.username.$model"
            :class="{
              'p-invalid': v$.account.username.$invalid && submitted,
            }"
          />
          <div>First name:</div>
          <InputText
            id="firstname"
            v-model="v$.account.firstname.$model"
            :class="{
              'p-invalid': v$.account.firstname.$invalid && submitted,
            }"
          />
          <div>Last name:</div>
          <InputText
            id="lastname"
            v-model="v$.account.lastname.$model"
            :class="{
              'p-invalid': v$.account.lastname.$invalid && submitted,
            }"
          />
          <div>eMail:</div>
          <InputText
            id="email"
            v-model="v$.account.email.$model"
            :class="{
              'p-invalid': v$.account.email.$invalid && submitted,
            }"
          />
          <Button
            type="submit"
            label="Save changes"
            class="mt-5 p-button-danger"
          />
        </form>
      </Fieldset>
    </div>
    <PluginConfig></PluginConfig>
  </div>
</template>

<script lang="ts">
import AccountService from "@/services/AccountService";
import { required } from "@vuelidate/validators";
import { useVuelidate } from "@vuelidate/core";
import { useAuthStore } from "@/store/AuthStore";
import { defineComponent } from "vue";
import FileService from "@/services/FileService";
import PluginConfig from "@/components/account/PluginConfig.vue";

export default defineComponent({
  name: "Account",
  components: {
    PluginConfig,
  },

  data() {
    return {
      submitted: false as boolean,
      showMessage: false,
    };
  },
  setup() {
    const authStore = useAuthStore();
    return {
      v$: useVuelidate(),
      account: authStore.user,
    };
  },
  validations() {
    return {
      account: {
        firstname: {
          required,
        },
        lastname: {
          required,
        },
        username: {
          required,
        },
        email: {
          required,
        },
      },
    };
  },
  methods: {
    uploadAvatar(event) {
      FileService.add(event.files).then(async (res) => {
        this.account.avatar = FileService.getFilePath(res[0]);
      });
    },
    handleSubmit(): void {
      this.submitted = true;
      const result = !this.v$.$invalid;

      if (result) {
        const account_data = {
          id: this.account.id,
          firstname: this.account.firstname,
          lastname: this.account.lastname,
          username: this.account.username,
          email: this.account.email,
        };
        AccountService.updateAcc(account_data, this.account.id).then(
          (res: any) => {
            if (res.status == 200) {
              this.toggleDialog();
            }
          }
        );
      }
    },
    toggleDialog(): void {
      this.showMessage = !this.showMessage;
      /*if (!this.showMessage) {
        this.forward();
      }*/
    },
  },
});
</script>

<style scoped>
@media screen and (max-width: 550px) {
  .account {
    width: 100%;
  }
}
</style>

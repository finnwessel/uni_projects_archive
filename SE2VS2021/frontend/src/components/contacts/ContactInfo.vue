<template>
  <Card class="border-white-alpha-10 border-1">
    <template #title>Contact</template>
    <template #content>
      <div class="grid align-content-center">
        <div class="col align-self-center">
          <Avatar
            v-if="!contact.avatarId"
            :label="contact.firstname[0]"
            size="xlarge"
            shape="circle"
          />
          <Avatar
            v-if="contact.avatarId"
            :image="contact.avatarId"
            size="xlarge"
            shape="circle"
          />
        </div>
        <div class="col align-self-center">
          <h3>{{ contact.firstname }} {{ contact.lastname }}</h3>
        </div>
        <div class="col align-self-center">
          <div class="flex">
            <i class="pi pi-envelope align-self-center px-3"></i>
            <h4 class="m-1">{{ contact.email }}</h4>
          </div>
          <div class="flex" v-if="contact.phoneNumber">
            <i class="pi pi-phone align-self-center px-3"></i>
            <h4 class="m-1">{{ contact.phoneNumber }}</h4>
          </div>
          <div class="flex" v-if="contact.birthday">
            <i class="pi pi-id-card align-self-center px-3"></i>
            <h4 class="m-1">{{ dateFormatted(contact.birthday) }}</h4>
          </div>
        </div>
        <div class="col align-self-center" v-if="contact.addresses.length >= 0">
          <h3>Addresses</h3>
        </div>
      </div>
    </template>
  </Card>
</template>

<script lang="ts">
import { defineComponent, PropType } from "vue";
import { Contact } from "@/interfaces/ContactsInterfaces";
import { dateFormatted } from "@/helpers/date";

export default defineComponent({
  name: "ContactInfo",
  props: {
    contact: Object as PropType<Contact>,
  },
  setup() {
    return {
      dateFormatted,
    };
  },
});
</script>

<style scoped></style>

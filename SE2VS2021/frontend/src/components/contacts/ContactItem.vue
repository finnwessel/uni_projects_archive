<template>
  <div
    class="
      flex
      align-items-center
      border-round
      cursor-pointer
      hover:surface-200
      justify-content-between
      p-2
      my-1
    "
  >
    <Avatar
      v-if="!contact.avatarId"
      :label="contact.firstname[0]"
      class="p-mr-2"
      size="large"
      shape="circle"
    />
    <Avatar
      v-if="contact.avatarId"
      :image="contact.avatarId"
      class="p-mr-2"
      size="large"
      shape="circle"
    />
    <div class="flex flex-column align-items-start ml-2">
      <div class="font-bold">
        {{ contact.firstname }} {{ contact.lastname }}
      </div>
    </div>
    <div class="flex">
      <Button class="p-button-success" @click="addUser">
        <p class="m-0" v-if="!isAdding">Add</p>
        <i class="pi pi-spin pi-spinner" v-if="isAdding" />
      </Button>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
  name: "ContactItem",
  props: ["contact"],
  data() {
    return {
      isAdding: false,
    };
  },
  methods: {
    addUser() {
      this.isAdding = true;
      this.$emitter.emit("contacts:add", this.contact.id);
    },
  },
});
</script>

<style scoped></style>

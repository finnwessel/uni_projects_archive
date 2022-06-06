<template>
  <!-- v-click-outside="close" -->
  <div v-if="open" class="modal" @click="close">
    <slot name="modal-content" class="modal-content"></slot>
  </div>
</template>

<script>
export default {
  name: "TweetCardMediaModal",
  props: {
    open: {
      type: Boolean,
      default: false
    }
  },
  methods: {
    close() {
      this.$emit("close");
    }
  }
};
</script>

<style scoped>
.modal {
  position: fixed;
  z-index: 20;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  overflow: auto;
  background-color: rgb(0, 0, 0);
  background-color: rgba(0, 0, 0, 0.9);
}

.modal-content {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  margin: auto;
  padding-top: 80px;
  padding-bottom: 20px;
  display: block;
  width: 80%;
  max-height: 70%;
  object-fit: contain;
}

.modal-content {
  animation-name: zoom;
  animation-duration: 0.2s;
}

@keyframes zoom {
  from {
    transform: scale(0);
  }
  to {
    transform: scale(1);
  }
}

@media only screen and (max-width: 700px) {
  .modal-content {
    width: 100%;
  }
}
</style>
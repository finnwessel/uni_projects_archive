<template>
  <div id="myModal" class="modal" v-if="isOpen">
    <div class="modal-content">
      <div class="modal-header">
        <span class="close" @click="close">&times;</span>
        <slot name="header"></slot>
      </div>
      <div class="modal-body">
        <slot name="content"></slot>
      </div>
      <slot name="footer" class="modal-footer"></slot>
    </div>
  </div>
</template>

<script>
export default {
  name: "Modal",
  props: {
    isOpen: {
      type: Boolean,
      required: true,
      default: false
    }
  },
  methods: {
    close() {
      this.$emit("close");
    }
  }
}
</script>

<style lang="scss" scoped>
.modal {
  position: fixed;
  z-index: 10;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  overflow: auto;
  background-color: rgb(0, 0, 0);
  background-color: rgba(0, 0, 0, 0.4);
}

.modal-header {
  padding: 2px 16px;
  background-color: #2c3e50;
  color: #727265;
}

.modal-body {
  padding: 2px 16px;
}

.modal-footer {
  padding: 2px 16px;
  height: 42px;
  background-color: #2c3e50;
  color: white;
  button {
    margin-left: 5px;
    margin-top: 3px;
    margin-bottom: 3px;
    font-size: 16px;
    border: 1px solid #778585;
    color: #778585;
    background-color: #2c3e50;
    border-radius: 2px;
    padding: 0.5rem 1rem;
    outline: none;
    cursor: pointer;
  }
}

.modal-content {
  position: relative;
  background-color: #bdc3c7;
  top: 25%;
  margin: auto;
  padding: 0;
  border: 1px solid #888;
  width: 50%;
  box-shadow: 0 4px 8px 0 rgba(0, 0, 0, 0.2), 0 6px 20px 0 rgba(0, 0, 0, 0.19);
  animation-name: fadeInFromTop;
  animation-duration: 0.5s;
}

.close {
  color: #aaa;
  float: right;
  font-size: 28px;
  font-weight: bold;
}

.close:hover,
.close:focus {
  color: #ae0101;
  text-decoration: none;
  cursor: pointer;
}

@keyframes fadeInFromTop {
  from {
    top: -300px;
    opacity: 0;
  }
  to {
    top: 25%;
    opacity: 1;
  }
}
</style>
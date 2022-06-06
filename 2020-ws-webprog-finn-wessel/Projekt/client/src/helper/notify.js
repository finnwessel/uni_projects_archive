import Vue from "vue";
// Quelle vue-toast-notification
export function infoNotify(message) {
  Vue.$toast.open({
    message: message,
    type: "info",
    position: "top-right",
    duration: 5000,
    dismissible: true,
    pauseOnHover: true
  });
}

export function errorNotify(message) {
  Vue.$toast.open({
    message: message,
    type: "error",
    position: "top-right",
    duration: 5000,
    dismissible: true,
    pauseOnHover: true
  });
}

export function successNotify(message) {
  Vue.$toast.open({
    message: message,
    type: "success",
    position: "top-right",
    duration: 5000,
    dismissible: true,
    pauseOnHover: true
  });
}
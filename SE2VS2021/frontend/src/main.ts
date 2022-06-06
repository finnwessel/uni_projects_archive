import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import PrimeVue from "primevue/config";
import ConfirmationService from "primevue/confirmationservice";
import ToastService from "primevue/toastservice";
import { createPinia } from "pinia";
import { createPersistedStatePlugin } from "pinia-plugin-persistedstate-2";

/*import "primevue/resources/themes/saga-blue/theme.css";*/
import "@/assets/themes/arya-green/theme.css";
import "primevue/resources/primevue.min.css";
import "primeicons/primeicons.css";
import "primeflex/primeflex.css";
import Button from "primevue/button";
import Avatar from "primevue/avatar";
import Fieldset from "primevue/fieldset";
import InputText from "primevue/inputtext";
import Dialog from "primevue/dialog";
import SplitButton from "primevue/splitbutton";
import Menubar from "primevue/menubar";
import Password from "primevue/password";
import Card from "primevue/card";
import Divider from "primevue/dataview";

import { TinyEmitter } from "tiny-emitter";
import ContextMenu from "primevue/contextmenu";
import FullCalendar from "primevue/fullcalendar";
import Listbox from "primevue/listbox";
import DataTable from "primevue/datatable";
import FileUpload from "primevue/fileupload";
import Toast from "primevue/toast";
import MultiSelect from "primevue/multiselect";

const app = createApp(App);
const store = createPinia();
store.use(createPersistedStatePlugin());
app.config.globalProperties.$emitter = new TinyEmitter();
app.component("Menubar", Menubar);
app.component("Button", Button);
app.component("Card", Card);
app.component("Avatar", Avatar);
app.component("Fieldset", Fieldset);
app.component("InputText", InputText);
app.component("Dialog", Dialog);
app.component("SplitButton", SplitButton);
app.component("Password", Password);
app.component("Divider", Divider);
app.component("ContextMenu", ContextMenu);
app.component("FullCalendar", FullCalendar);
app.component("Listbox", Listbox);
app.component("DataTable", DataTable);
app.component("FileUpload", FileUpload);
app.component("Toast", Toast);
app.component("MultiSelect", MultiSelect);

app
  .use(store)
  .use(router)
  .use(PrimeVue)
  .use(ConfirmationService)
  .use(ToastService)
  .mount("#app");

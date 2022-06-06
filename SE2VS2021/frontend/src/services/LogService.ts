import axios from "axios";
import { Log } from "@/interfaces/LogsInterfaces";

const api = process.env.VUE_APP_LOGGING_SERVICE_URL;

export default {
  getFromReference(referenceId: string): Promise<Log[]> {
    return new Promise((resolve, reject) => {
      axios
        .get(api.concat(`reference/${referenceId}`))
        .then((res) => {
          if (res.status == 200) {
            resolve(res.data);
          } else {
            reject();
          }
        })
        .catch((err) => reject(err));
    });
  },
};

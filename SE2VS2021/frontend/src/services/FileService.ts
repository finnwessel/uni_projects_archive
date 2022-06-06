import axios from "axios";

const api = process.env.VUE_APP_FILE_SERVICE_URL;

export default {
  add(files: File[]): Promise<string> {
    const formData = new FormData();
    for (const file of files) {
      formData.append("files", file, file.name);
    }
    return new Promise((resolve, reject) => {
      axios
        .post(api + "upload", formData, {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        })
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
  getFile(fileId: string): Promise<File> {
    return new Promise((resolve, reject) => {
      axios
        .get(api + fileId)
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
  getFilePath(fileId: string): string {
    return api + fileId;
  },
};

import { defineStore } from "pinia";
import {
  NewRegister,
  Register,
  SwitchedIndex,
} from "@/interfaces/RegistersInterfaces";
import RegisterService from "@/services/RegisterService";

export type RegisterState = {
  registers: Register[];
};

export const useRegistersStore = defineStore({
  id: "registers",
  state: () =>
    ({
      registers: [],
    } as RegisterState),

  actions: {
    addRegister(
      projectId: string,
      newRegister: NewRegister
    ): Promise<Register> {
      return new Promise((resolve, reject) => {
        RegisterService.add(projectId, newRegister)
          .then((r) => {
            const register: Register = {
              id: r.id,
              title: r.title,
              index: r.index,
              tasks: [],
            };
            this.registers = [...this.registers].concat(register);
            resolve(r);
          })
          .catch((err) => reject(err));
      });
    },
    updateRegister(register: Register): Promise<boolean> {
      return new Promise((resolve, reject) => {
        RegisterService.update(register)
          .then((b) => {
            const newArray = this.registers.filter((p) => p.id !== register.id);
            newArray.splice(register.index, 0, register);
            this.registers = [...newArray];
            resolve(b);
          })
          .catch((err) => reject(err));
      });
    },
    switchIndex() {
      const switchedIndexes: SwitchedIndex[] = [];
      for (const i in this.registers) {
        switchedIndexes.push({ id: this.registers[i].id, index: parseInt(i) });
      }
      RegisterService.updateIndex(switchedIndexes).then();
    },
    deleteRegister(registerId: string): Promise<boolean> {
      return new Promise((resolve, reject) => {
        RegisterService.delete(registerId)
          .then((b) => {
            this.registers = this.registers.filter((p) => p.id !== registerId);
            resolve(b);
          })
          .catch((err) => reject(err));
      });
    },
    async loadRegisters(registerId: string) {
      this.registers = await RegisterService.get(registerId);
      this.registers.sort((a, b) => (a.index > b.index ? 1 : -1));
    },
    clearRegisters() {
      this.registers = [];
    },
  },
  getters: {
    getRegisters(state): Register[] {
      return state.registers;
    },
  },
});

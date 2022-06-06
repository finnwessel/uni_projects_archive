import { defineStore } from "pinia";
import { NewProject, Project } from "@/interfaces/ProjectsInterfaces";
import ProjectService from "@/services/ProjectService";
import { SwitchedIndex } from "@/interfaces/RegistersInterfaces";
import RegisterService from "@/services/RegisterService";

export type ProjectState = {
  projects: Project[];
};

export const useProjectsStore = defineStore({
  id: "projects",
  state: () =>
    ({
      projects: [],
    } as ProjectState),

  actions: {
    addProject(newProject: NewProject): Promise<Project> {
      return new Promise((resolve, reject) => {
        ProjectService.add(newProject)
          .then((p) => {
            const project: Project = {
              id: p.id,
              title: p.title,
              index: p.index,
            };
            this.projects = [...this.projects].concat(project);
            resolve(p);
          })
          .catch((err) => reject(err));
      });
    },
    updateProject(project: Project): Promise<boolean> {
      return new Promise((resolve, reject) => {
        ProjectService.update(project)
          .then((b) => {
            const newArray = this.projects.filter((p) => p.id !== project.id);
            newArray.splice(project.index, 0, project);
            this.projects = [...newArray].sort((a, b) =>
              a.index > b.index ? 1 : -1
            );
            resolve(b);
          })
          .catch((err) => reject(err));
      });
    },
    switchIndex() {
      const switchedIndexes: SwitchedIndex[] = [];
      for (const i in this.projects) {
        switchedIndexes.push({ id: this.projects[i].id, index: parseInt(i) });
      }
      ProjectService.updateIndex(switchedIndexes).then();
    },
    deleteProject(projectId: string): Promise<boolean> {
      return new Promise((resolve, reject) => {
        ProjectService.delete(projectId)
          .then((b) => {
            this.projects = this.projects.filter((p) => p.id !== projectId);
            resolve(b);
          })
          .catch((err) => reject(err));
      });
    },
    async loadProjects() {
      this.projects = await ProjectService.get();
      this.projects.sort((a, b) => (a.index > b.index ? 1 : -1));
    },
  },
  getters: {
    getProjects(state): Project[] {
      return state.projects;
    },
  },
});

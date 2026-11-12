import { useMemo } from "react";
import type { ChecklistItem, Project } from "../types";
import { nextChecklistItem } from "../utils";

export interface DashboardStats {
  active: number;
  done: number;
  total: number;
  paid: number;
  percent: number;
}

export interface DashboardWarning {
  text: string;
  projectId: string;
  tone: "amber" | "red";
}

const REVIEW_STAGES: Project["stage"][] = [
  "REVIEW_SUBMITTED",
  "REVIEW_RESPONSE",
  "RELEASE_PREPARATION",
];

export function useDashboardData(
  projects: Project[],
  checklists: Record<string, ChecklistItem[]>,
) {
  const activeProjects = useMemo(
    () => projects.filter((project) => !project.archived),
    [projects],
  );

  const continueProject = useMemo(() => {
    return [...activeProjects].sort(
      (a, b) => new Date(b.updatedAt).getTime() - new Date(a.updatedAt).getTime(),
    )[0];
  }, [activeProjects]);

  const continueNext = continueProject
    ? nextChecklistItem(checklists[continueProject.id] ?? [])
    : undefined;

  const stats = useMemo<DashboardStats>(() => {
    const done = activeProjects.reduce((sum, project) => sum + project.checklistCompleted, 0);
    const total = activeProjects.reduce((sum, project) => sum + project.checklistTotal, 0);
    const paid = activeProjects.filter((project) => project.releaseType === "PAID").length;
    return {
      active: activeProjects.length,
      done,
      total,
      paid,
      percent: total > 0 ? Math.round((done / total) * 100) : 0,
    };
  }, [activeProjects]);

  const warnings = useMemo(() => {
    const list: DashboardWarning[] = [];
    const today = new Date();
    activeProjects.forEach((project) => {
      const items = checklists[project.id] ?? [];
      if (project.releaseType === "PAID") {
        const paidTest = items.find(
          (item) => item.name.includes("テストプレイ3回") && item.status !== "DONE",
        );
        if (paidTest) {
          list.push({
            text: "有料作品のテストプレイが、UZU 1回を含む3回以上に届いていません。",
            projectId: project.id,
            tone: "amber",
          });
        }
      }
      const overdue = items.find(
        (item) =>
          item.dueDate &&
          item.status !== "DONE" &&
          new Date(`${item.dueDate}T23:59:59+09:00`).getTime() < today.getTime(),
      );
      if (overdue) {
        list.push({
          text: `期限を過ぎた項目があります: ${overdue.name}`,
          projectId: project.id,
          tone: "red",
        });
      }
      const reviewPending = items.find(
        (item) => item.name.includes("審査") && item.status !== "DONE",
      );
      if (reviewPending && REVIEW_STAGES.includes(project.stage)) {
        list.push({
          text: `審査・公開準備の未完了項目があります: ${reviewPending.name}`,
          projectId: project.id,
          tone: "amber",
        });
      }
    });
    return list.slice(0, 3);
  }, [activeProjects, checklists]);

  const todayLabel = useMemo(
    () =>
      new Intl.DateTimeFormat("ja-JP", {
        month: "long",
        day: "numeric",
        weekday: "short",
      }).format(new Date()),
    [],
  );

  return { activeProjects, continueProject, continueNext, stats, warnings, todayLabel };
}

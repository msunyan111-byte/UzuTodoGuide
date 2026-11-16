import type { ChecklistItem, Project, SectionDefinition } from "./types";

/** UZU StudioのトップURL */
export const UZU_STUDIO_URL = "https://studio.uzu-app.com/";

/** UZU公式ガイドのトップURL */
export const OFFICIAL_DOCS_URL = "https://docs.studio.uzu-app.com/";

export function formatDate(value: string | null | undefined): string {
  if (!value) return "未設定";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;
  return new Intl.DateTimeFormat("ja-JP", {
    month: "short",
    day: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  }).format(date);
}

export function formatDay(value: string | null | undefined): string {
  if (!value) return "期限なし";
  const date = new Date(`${value}T00:00:00+09:00`);
  if (Number.isNaN(date.getTime())) return value;
  return new Intl.DateTimeFormat("ja-JP", {
    month: "short",
    day: "numeric",
  }).format(date);
}

export function progressPercent(project: Project): number {
  if (project.checklistTotal <= 0) return 0;
  return Math.round((project.checklistCompleted / project.checklistTotal) * 100);
}

export function nextChecklistItem(items: ChecklistItem[]): ChecklistItem | undefined {
  return [...items]
    .filter((item) => item.status !== "DONE")
    .sort((a, b) => a.displayOrder - b.displayOrder)[0];
}

export function sectionKeyForItem(item: ChecklistItem): string {
  return item.sectionKey || "OTHER";
}

export function groupItemsBySection(items: ChecklistItem[]): Record<string, ChecklistItem[]> {
  return items.reduce<Record<string, ChecklistItem[]>>((groups, item) => {
    const key = sectionKeyForItem(item);
    groups[key] = [...(groups[key] ?? []), item];
    return groups;
  }, {});
}

export function sectionProgress(items: ChecklistItem[]): { done: number; total: number } {
  return {
    done: items.filter((item) => item.status === "DONE").length,
    total: items.length,
  };
}

export function findSection(
  sections: SectionDefinition[],
  key: string,
): SectionDefinition | undefined {
  return sections.find((section) => section.key === key);
}

export function officialUrl(path: string): string {
  return `${OFFICIAL_DOCS_URL}${path.replace(/^\/+/, "")}`;
}

export function studioUrl(value: string | null | undefined): string {
  return value || UZU_STUDIO_URL;
}

export function errorMessage(error: unknown, fallback: string): string {
  return error instanceof Error && error.message ? error.message : fallback;
}

export function copyText(text: string): Promise<void> {
  if (navigator.clipboard?.writeText) {
    return navigator.clipboard.writeText(text);
  }
  return Promise.reject(new Error("クリップボードを利用できません"));
}

export function readStorage<T>(key: string, fallback: T): T {
  try {
    const value = window.localStorage.getItem(key);
    return value ? (JSON.parse(value) as T) : fallback;
  } catch {
    return fallback;
  }
}

export function writeStorage(key: string, value: unknown): void {
  try {
    window.localStorage.setItem(key, JSON.stringify(value));
  } catch {
    return;
  }
}

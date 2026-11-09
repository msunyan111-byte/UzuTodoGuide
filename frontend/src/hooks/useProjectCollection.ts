import { useCallback, useEffect, useState } from "react";
import { api, isConnectionError } from "../api";
import { DEMO_CHECKLIST, DEMO_PROJECT, DEMO_PROJECT_ID } from "../data/demo";
import type { ChecklistItem, Project } from "../types";

interface CollectionState {
  projects: Project[];
  checklists: Record<string, ChecklistItem[]>;
  loading: boolean;
  demo: boolean;
  error: string | null;
}

async function loadCollection(): Promise<CollectionState> {
  try {
    const projects = await api.listProjects();
    const entries = await Promise.all(
      projects.map(async (project) => {
        const checklist = await api.listChecklist(project.id);
        return [project.id, checklist] as const;
      }),
    );
    return {
      projects,
      checklists: Object.fromEntries(entries),
      loading: false,
      demo: false,
      error: null,
    };
  } catch (error) {
    return {
      projects: [DEMO_PROJECT],
      checklists: { [DEMO_PROJECT_ID]: DEMO_CHECKLIST },
      loading: false,
      demo: true,
      error: isConnectionError(error)
        ? "APIに接続できないため、デモデータを表示しています。"
        : "作品APIを利用できないため、デモデータを表示しています。",
    };
  }
}

export function useProjectCollection() {
  const [state, setState] = useState<CollectionState>({
    projects: [],
    checklists: {},
    loading: true,
    demo: false,
    error: null,
  });

  const reload = useCallback(async () => {
    const next = await loadCollection();
    setState(next);
  }, []);

  useEffect(() => {
    let cancelled = false;
    const load = async () => {
      const next = await loadCollection();
      if (!cancelled) setState(next);
    };
    void load();
    return () => {
      cancelled = true;
    };
  }, []);

  return { ...state, reload };
}

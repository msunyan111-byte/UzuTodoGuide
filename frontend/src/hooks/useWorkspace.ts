import { useEffect, useMemo, useState } from "react";
import { api, isConnectionError } from "../api";
import { useToast } from "../components/Toast";
import { readinessFor } from "../components/workspace/readiness";
import { DEMO_CHECKLIST, DEMO_PROJECT, DEMO_PROJECT_ID } from "../data/demo";
import { SECTIONS } from "../data/uzu";
import type { ChecklistItem, ChecklistStatus, Project, SectionDefinition } from "../types";
import {
  errorMessage,
  findSection,
  groupItemsBySection,
  nextChecklistItem,
  sectionProgress,
} from "../utils";

const OTHER_SECTION: SectionDefinition = {
  key: "OTHER",
  label: "その他のチェック",
  group: "共通",
  order: 99,
  uzuPath: "overview/makingflow.md",
  purpose: "公式画面に直接対応しない、作品固有の確認項目です。",
  basic: "yes",
  advanced: "yes",
  fields: [],
  constraints: [],
  tips: ["作品固有の項目はUZU上の画面名と対応づけておくと見返しやすくなります。"],
  confidence: "estimated",
};

export function useWorkspace(projectId: string, activeKey: string) {
  const toast = useToast();
  const [project, setProject] = useState<Project | null>(null);
  const [items, setItems] = useState<ChecklistItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [demo, setDemo] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [savingIds, setSavingIds] = useState<Set<string>>(new Set());

  useEffect(() => {
    let cancelled = false;
    const load = async () => {
      setLoading(true);
      setError(null);
      try {
        const [loadedProject, loadedItems] = await Promise.all([
          api.getProject(projectId),
          api.listChecklist(projectId),
        ]);
        if (!cancelled) {
          setProject(loadedProject);
          setItems(loadedItems);
          setDemo(false);
        }
      } catch (loadError) {
        if (!cancelled && projectId === DEMO_PROJECT_ID) {
          setProject(DEMO_PROJECT);
          setItems(DEMO_CHECKLIST);
          setDemo(true);
        } else if (!cancelled) {
          setError(
            isConnectionError(loadError)
              ? "APIに接続できません。Spring Bootが起動しているか確認してください。"
              : errorMessage(loadError, "作品を読み込めませんでした。"),
          );
        }
      } finally {
        if (!cancelled) setLoading(false);
      }
    };
    void load();
    return () => {
      cancelled = true;
    };
  }, [projectId]);

  const activeSection = useMemo(() => {
    if (activeKey === "OTHER") return OTHER_SECTION;
    return findSection(SECTIONS, activeKey) ?? SECTIONS[0];
  }, [activeKey]);

  const itemsBySection = useMemo(() => groupItemsBySection(items), [items]);
  const activeItems = itemsBySection[activeKey] ?? [];
  const nextItem = nextChecklistItem(items);
  const sectionNext = nextChecklistItem(activeItems);
  const activeProgress = sectionProgress(activeItems);
  const doneCount = items.filter((item) => item.status === "DONE").length;
  const readiness = project ? readinessFor(items, project) : [];

  const markSaving = (id: string, saving: boolean) => {
    setSavingIds((current) => {
      const next = new Set(current);
      if (saving) next.add(id);
      else next.delete(id);
      return next;
    });
  };

  const changeStatus = async (item: ChecklistItem, status: ChecklistStatus) => {
    const previous = items;
    setItems((current) =>
      current.map((entry) => (entry.id === item.id ? { ...entry, status } : entry)),
    );
    if (demo) return;
    markSaving(item.id, true);
    try {
      const updated = await api.updateChecklistItem(projectId, item.id, { status });
      setItems((current) => current.map((entry) => (entry.id === item.id ? updated : entry)));
      if (status === "DONE") toast.show(`完了: ${item.name}`, "success");
    } catch (updateError) {
      setItems(previous);
      toast.show(errorMessage(updateError, "状態を更新できませんでした。"), "error");
    } finally {
      markSaving(item.id, false);
    }
  };

  const changeMemo = async (item: ChecklistItem, memo: string) => {
    const previous = items;
    setItems((current) =>
      current.map((entry) => (entry.id === item.id ? { ...entry, memo } : entry)),
    );
    if (demo) return;
    markSaving(item.id, true);
    try {
      const updated = await api.updateChecklistItem(projectId, item.id, { memo });
      setItems((current) => current.map((entry) => (entry.id === item.id ? updated : entry)));
      toast.show("メモを保存しました。", "success");
    } catch (updateError) {
      setItems(previous);
      toast.show(errorMessage(updateError, "メモを保存できませんでした。"), "error");
    } finally {
      markSaving(item.id, false);
    }
  };

  const addItem = async (name: string, description: string): Promise<boolean> => {
    if (!name) return false;
    const input = {
      name,
      description: description || undefined,
      displayOrder: items.length + 1,
    };
    if (demo) {
      const now = new Date().toISOString();
      setItems((current) => [
        ...current,
        {
          id: crypto.randomUUID(),
          projectId,
          name: input.name,
          description: input.description ?? null,
          status: "NOT_STARTED",
          memo: null,
          dueDate: null,
          displayOrder: input.displayOrder,
          createdAt: now,
          updatedAt: now,
        },
      ]);
      toast.show("チェック項目を追加しました。", "success");
      return true;
    }
    try {
      const created = await api.addChecklistItem(projectId, input);
      setItems((current) => [...current, created]);
      toast.show("チェック項目を追加しました。", "success");
      return true;
    } catch (addError) {
      toast.show(errorMessage(addError, "チェック項目を追加できませんでした。"), "error");
      return false;
    }
  };

  return {
    project,
    items,
    loading,
    demo,
    error,
    savingIds,
    activeSection,
    itemsBySection,
    activeItems,
    nextItem,
    sectionNext,
    activeProgress,
    doneCount,
    readiness,
    changeStatus,
    changeMemo,
    addItem,
    updateProject: setProject,
  };
}

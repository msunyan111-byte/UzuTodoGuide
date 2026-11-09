import { useEffect, useMemo, useState } from "react";
import { Link, useParams, useSearchParams } from "react-router-dom";
import { api, isConnectionError } from "../api";
import { Icon } from "../components/Icon";
import { Loading } from "../components/Loading";
import { SectionNav } from "../components/SectionNav";
import { useToast } from "../components/Toast";
import { ChecklistPanel } from "../components/workspace/ChecklistPanel";
import { GuidancePanel } from "../components/workspace/GuidancePanel";
import { ProjectEditModal } from "../components/workspace/ProjectEditModal";
import { readinessFor } from "../components/workspace/readiness";
import { SectionDetail } from "../components/workspace/SectionDetail";
import { SectionMemoPanel } from "../components/workspace/SectionMemoPanel";
import { WorkspaceAside } from "../components/workspace/WorkspaceAside";
import { WorkspaceTopbar } from "../components/workspace/WorkspaceTopbar";
import { DEMO_CHECKLIST, DEMO_PROJECT, DEMO_PROJECT_ID } from "../data/demo";
import { SECTIONS } from "../data/uzu";
import type { ChecklistItem, ChecklistStatus, Project, SectionDefinition } from "../types";
import { findSection, groupItemsBySection, nextChecklistItem, sectionProgress } from "../utils";

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

export function Workspace() {
  const { projectId = "" } = useParams();
  const [searchParams, setSearchParams] = useSearchParams();
  const toast = useToast();
  const activeKey = searchParams.get("section") ?? "SUMMARY";

  const [project, setProject] = useState<Project | null>(null);
  const [items, setItems] = useState<ChecklistItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [demo, setDemo] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [savingIds, setSavingIds] = useState<Set<string>>(new Set());
  const [editOpen, setEditOpen] = useState(false);

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
              : loadError instanceof Error
                ? loadError.message
                : "作品を読み込めませんでした。",
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
  const section = sectionProgress(activeItems);
  const doneCount = items.filter((item) => item.status === "DONE").length;
  const readiness = project ? readinessFor(items, project) : [];

  const selectSection = (key: string) => {
    setSearchParams({ section: key }, { replace: true });
  };

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
      toast.show(
        updateError instanceof Error ? updateError.message : "状態を更新できませんでした。",
        "error",
      );
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
      toast.show(
        updateError instanceof Error ? updateError.message : "メモを保存できませんでした。",
        "error",
      );
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
          createdAt: new Date().toISOString(),
          updatedAt: new Date().toISOString(),
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
      toast.show(
        addError instanceof Error ? addError.message : "チェック項目を追加できませんでした。",
        "error",
      );
      return false;
    }
  };

  if (loading) {
    return <Loading label="作品を読み込んでいます" />;
  }

  if (error || !project) {
    return (
      <div className="page-stack narrow-page">
        <div className="error-panel">
          <Icon name="alert" size={22} />
          <div>
            <h1>作品を開けませんでした</h1>
            <p>{error ?? "作品が見つかりません。"}</p>
            <Link to="/projects" className="button button-primary">
              作品一覧へ戻る
            </Link>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="workspace">
      <WorkspaceTopbar
        project={project}
        section={activeSection}
        doneCount={doneCount}
        totalCount={items.length}
        nextItem={nextItem}
        demo={demo}
        onEdit={() => setEditOpen(true)}
      />

      <div className="workspace-grid">
        <aside className="workspace-nav panel">
          <div className="workspace-nav-heading">
            <span className="eyebrow">UZU画面</span>
            <span className="workspace-nav-count">39セクション</span>
          </div>
          <SectionNav
            sections={SECTIONS}
            activeKey={activeKey}
            itemsBySection={itemsBySection}
            onSelect={selectSection}
          />
        </aside>

        <div className="workspace-content">
          <SectionDetail section={activeSection} nextItem={sectionNext} />

          <ChecklistPanel
            items={activeItems}
            progress={section}
            savingIds={savingIds}
            onStatusChange={(item, status) => void changeStatus(item, status)}
            onMemoChange={(item, memo) => void changeMemo(item, memo)}
            onAdd={addItem}
          />

          <SectionMemoPanel
            key={`${projectId}:${activeKey}`}
            projectId={projectId}
            sectionKey={activeKey}
          />

          <GuidancePanel section={activeSection} />
        </div>

        <WorkspaceAside
          project={project}
          nextItem={nextItem}
          readiness={readiness}
          onCompleteNext={(item) => void changeStatus(item, "DONE")}
        />
      </div>

      {editOpen && (
        <ProjectEditModal
          project={project}
          demo={demo}
          onClose={() => setEditOpen(false)}
          onSaved={(updated) => setProject(updated)}
        />
      )}
    </div>
  );
}

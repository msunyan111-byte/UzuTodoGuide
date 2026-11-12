import { useState } from "react";
import { Link, useParams, useSearchParams } from "react-router-dom";
import { Icon } from "../components/Icon";
import { Loading } from "../components/Loading";
import { SectionNav } from "../components/SectionNav";
import { ChecklistPanel } from "../components/workspace/ChecklistPanel";
import { GuidancePanel } from "../components/workspace/GuidancePanel";
import { ProjectEditModal } from "../components/workspace/ProjectEditModal";
import { SectionDetail } from "../components/workspace/SectionDetail";
import { SectionMemoPanel } from "../components/workspace/SectionMemoPanel";
import { WorkspaceAside } from "../components/workspace/WorkspaceAside";
import { WorkspaceTopbar } from "../components/workspace/WorkspaceTopbar";
import { SECTION_COUNT, SECTIONS } from "../data/uzu";
import { useWorkspace } from "../hooks/useWorkspace";

export function Workspace() {
  const { projectId = "" } = useParams();
  const [searchParams, setSearchParams] = useSearchParams();
  const [editOpen, setEditOpen] = useState(false);
  const activeKey = searchParams.get("section") ?? "SUMMARY";
  const {
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
    updateProject,
  } = useWorkspace(projectId, activeKey);

  const selectSection = (key: string) => {
    setSearchParams({ section: key }, { replace: true });
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
            <span className="workspace-nav-count">{SECTION_COUNT}セクション</span>
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
            progress={activeProgress}
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
          onSaved={(updated) => updateProject(updated)}
        />
      )}
    </div>
  );
}

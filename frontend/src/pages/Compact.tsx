import { useMemo, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { api } from "../api";
import { Icon } from "../components/Icon";
import { Loading } from "../components/Loading";
import { useToast } from "../components/Toast";
import { CompactHeader } from "../components/compact/CompactHeader";
import { CompactNextTab } from "../components/compact/CompactNextTab";
import { CompactScreenTab } from "../components/compact/CompactScreenTab";
import { CompactTabs, type CompactTab } from "../components/compact/CompactTabs";
import { SectionMemo } from "../components/compact/SectionMemo";
import { useProjectCollection } from "../hooks/useProjectCollection";
import { SECTIONS } from "../data/uzu";
import type { ChecklistItem, Project } from "../types";
import {
  errorMessage,
  findSection,
  groupItemsBySection,
  nextChecklistItem,
  sectionKeyForItem,
  sectionProgress,
} from "../utils";

export function Compact() {
  const { projectId } = useParams();
  const { projects, checklists, loading, demo, error } = useProjectCollection();

  const project = projectId
    ? projects.find((entry) => entry.id === projectId)
    : projects.find((entry) => !entry.archived) ?? projects[0];

  if (loading) {
    return (
      <div className="compact-shell">
        <Loading label="読み込んでいます" />
      </div>
    );
  }

  if (!project) {
    return (
      <div className="compact-shell">
        <div className="compact-empty">
          <Icon name="compact" size={30} />
          <h1>作品がありません</h1>
          <p>通常画面から作品を追加してください。</p>
          <Link to="/projects/new" className="button button-primary">
            作品を追加
          </Link>
        </div>
      </div>
    );
  }

  return (
    <CompactProject
      key={project.id}
      project={project}
      checklists={checklists}
      demo={demo}
      error={error}
    />
  );
}

function CompactProject({
  project,
  checklists,
  demo,
  error,
}: {
  project: Project;
  checklists: Record<string, ChecklistItem[]>;
  demo: boolean;
  error: string | null;
}) {
  const toast = useToast();
  const [localItems, setLocalItems] = useState<ChecklistItem[] | null>(null);
  const [tab, setTab] = useState<CompactTab>("next");
  const [selectedSection, setSelectedSection] = useState<string | null>(null);

  const items = useMemo(
    () => localItems ?? checklists[project.id] ?? [],
    [checklists, localItems, project.id],
  );

  const nextItem = nextChecklistItem(items);
  const nextSectionKey = nextItem ? sectionKeyForItem(nextItem) : "SUMMARY";
  const currentKey = selectedSection ?? nextSectionKey;
  const currentSection = findSection(SECTIONS, currentKey) ?? SECTIONS[0];
  const itemsBySection = useMemo(() => groupItemsBySection(items), [items]);
  const currentItems = itemsBySection[currentKey] ?? [];
  const currentProgress = sectionProgress(currentItems);
  const allProgress = sectionProgress(items);

  const completeNext = async (item: ChecklistItem) => {
    const previous = items;
    const optimistic = items.map((entry) =>
      entry.id === item.id ? { ...entry, status: "DONE" as const } : entry,
    );
    setLocalItems(optimistic);
    if (demo) {
      toast.show(`完了: ${item.name}`, "success");
      return;
    }
    try {
      await api.updateChecklistItem(project.id, item.id, { status: "DONE" });
      toast.show(`完了: ${item.name}`, "success");
    } catch (updateError) {
      setLocalItems(previous);
      toast.show(errorMessage(updateError, "更新できませんでした。"), "error");
    }
  };

  return (
    <div className="compact-shell">
      <CompactHeader project={project} progress={allProgress} demo={demo} error={error} />

      <main className="compact-main">
        {tab === "next" && (
          <CompactNextTab
            currentKey={currentKey}
            currentProgress={currentProgress}
            section={currentSection}
            nextItem={nextItem}
            items={currentItems}
            onSelectSection={setSelectedSection}
            onComplete={(item) => void completeNext(item)}
          />
        )}

        {tab === "screen" && <CompactScreenTab section={currentSection} />}

        {tab === "memo" && (
          <SectionMemo
            key={`${project.id}:${currentKey}`}
            storageKey={`uzu:memo:${project.id}:${currentKey}`}
            label={`${currentSection.label} のメモ`}
          />
        )}
      </main>

      <CompactTabs tab={tab} projectId={project.id} onChange={setTab} />
    </div>
  );
}

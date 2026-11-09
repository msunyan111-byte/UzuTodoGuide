import { useMemo, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { api } from "../api";
import { Badge, StageBadge } from "../components/Badge";
import { Icon } from "../components/Icon";
import { Loading } from "../components/Loading";
import { ProgressBar } from "../components/ProgressBar";
import { useToast } from "../components/Toast";
import { useProjectCollection } from "../hooks/useProjectCollection";
import { SECTIONS } from "../data/uzu";
import type { ChecklistItem, Project } from "../types";
import { RELEASE_LABELS } from "../types";
import {
  findSection,
  groupItemsBySection,
  nextChecklistItem,
  officialUrl,
  readStorage,
  sectionKeyForItem,
  sectionProgress,
  writeStorage,
} from "../utils";

type Tab = "next" | "screen" | "memo";

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
  const [tab, setTab] = useState<Tab>("next");
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
      toast.show(
        updateError instanceof Error ? updateError.message : "更新できませんでした。",
        "error",
      );
    }
  };

  return (
    <div className="compact-shell">
      <header className="compact-header">
        <div className="compact-title-row">
          <Link to={`/projects/${project.id}`} className="compact-back" aria-label="ワークスペースに戻る">
            <Icon name="arrowRight" size={17} className="back-arrow" />
          </Link>
          <div className="compact-title">
            <strong>{project.title}</strong>
            <div>
              <StageBadge stage={project.stage} />
              <Badge tone={project.releaseType === "PAID" ? "amber" : "green"} outline>
                {RELEASE_LABELS[project.releaseType]}
              </Badge>
            </div>
          </div>
          <a
            className="compact-uzu"
            href={project.studioUrl ?? "https://studio.uzu-app.com/"}
            target="_blank"
            rel="noreferrer"
          >
            <Icon name="external" size={18} />
          </a>
        </div>
        <div className="compact-progress">
          <ProgressBar value={allProgress.total ? (allProgress.done / allProgress.total) * 100 : 0} size="sm" />
          <span>
            {allProgress.done}/{allProgress.total}
          </span>
        </div>
        {demo && <p className="compact-demo">{error ?? "デモデータを表示中"}</p>}
      </header>

      <main className="compact-main">
        {tab === "next" && (
          <div className="compact-next">
            <div className="compact-section-row">
              <select
                value={currentKey}
                onChange={(event) => setSelectedSection(event.target.value)}
                aria-label="セクション"
              >
                {SECTIONS.map((section) => (
                  <option key={section.key} value={section.key}>
                    {section.group} / {section.label}
                  </option>
                ))}
              </select>
              <span>
                {currentProgress.done}/{currentProgress.total}
              </span>
            </div>
            <div className="compact-section-title">
              <span>{currentSection.group}</span>
              <h2>{currentSection.label}</h2>
            </div>
            <div className="compact-action-card">
              <span className="compact-label">次にやること</span>
              <strong>{nextItem?.name ?? "すべて完了しています"}</strong>
              {nextItem?.description && <p>{nextItem.description}</p>}
              {currentSection.constraints[0] && (
                <span className="compact-constraint">
                  <Icon name="alert" size={13} />
                  {currentSection.constraints[0]}
                </span>
              )}
              {nextItem && (
                <button
                  type="button"
                  className="button button-primary compact-complete"
                  onClick={() => void completeNext(nextItem)}
                >
                  <Icon name="check" size={18} />
                  完了にする
                </button>
              )}
            </div>
            {currentItems.filter((item) => item.id !== nextItem?.id).length > 0 && (
              <div className="compact-items">
                <span className="compact-label">この画面の未完了</span>
                {currentItems
                  .filter((item) => item.id !== nextItem?.id && item.status !== "DONE")
                  .slice(0, 4)
                  .map((item) => (
                    <button
                      key={item.id}
                      type="button"
                      className="compact-item"
                      onClick={() => void completeNext(item)}
                    >
                      <span className="compact-item-dot" />
                      <span>{item.name}</span>
                      <Icon name="check" size={15} />
                    </button>
                  ))}
              </div>
            )}
          </div>
        )}

        {tab === "screen" && (
          <div className="compact-screen">
            <span className="compact-label">UZU画面</span>
            <h2>{currentSection.label}</h2>
            <p>{currentSection.purpose}</p>
            {currentSection.fields.length > 0 && (
              <div className="chip-cloud">
                {currentSection.fields.map((field) => (
                  <span className="info-chip" key={field}>
                    {field}
                  </span>
                ))}
              </div>
            )}
            {currentSection.constraints.length > 0 && (
              <div className="compact-constraints">
                <span className="compact-label">制約</span>
                {currentSection.constraints.map((constraint) => (
                  <span className="compact-constraint" key={constraint}>
                    <Icon name="alert" size={13} />
                    {constraint}
                  </span>
                ))}
              </div>
            )}
            <a
              className="button button-primary compact-complete"
              href={officialUrl(currentSection.uzuPath)}
              target="_blank"
              rel="noreferrer"
            >
              <Icon name="external" size={17} />
              公式ガイドを開く
            </a>
          </div>
        )}

        {tab === "memo" && (
          <SectionMemo
            key={`${project.id}:${currentKey}`}
            storageKey={`uzu:memo:${project.id}:${currentKey}`}
            label={`${currentSection.label} のメモ`}
          />
        )}
      </main>

      <nav className="compact-tabs" aria-label="コンパクトモード">
        <button type="button" className={tab === "next" ? "active" : ""} onClick={() => setTab("next")}>
          <Icon name="arrowRight" size={19} />
          次にやる
        </button>
        <button type="button" className={tab === "screen" ? "active" : ""} onClick={() => setTab("screen")}>
          <Icon name="guide" size={19} />
          UZU画面
        </button>
        <button type="button" className={tab === "memo" ? "active" : ""} onClick={() => setTab("memo")}>
          <Icon name="copy" size={19} />
          メモ
        </button>
        <Link to={`/projects/${project.id}`}>
          <Icon name="more" size={19} />
          その他
        </Link>
      </nav>
    </div>
  );
}

function SectionMemo({ storageKey, label }: { storageKey: string; label: string }) {
  const toast = useToast();
  const [memo, setMemo] = useState(() => readStorage(storageKey, ""));

  const saveMemo = () => {
    writeStorage(storageKey, memo);
    toast.show("メモを保存しました。", "success");
  };

  return (
    <div className="compact-memo">
      <span className="compact-label">{label}</span>
      <textarea
        className="textarea"
        value={memo}
        onChange={(event) => setMemo(event.target.value.slice(0, 2000))}
        onBlur={saveMemo}
        placeholder="気づきを短く残す"
        rows={8}
      />
      <div className="compact-memo-footer">
        <span>{memo.length}/2000</span>
        <button type="button" className="button button-ghost button-small" onClick={saveMemo}>
          保存
        </button>
      </div>
    </div>
  );
}

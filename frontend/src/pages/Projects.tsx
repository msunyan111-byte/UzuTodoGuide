import { useMemo, useState } from "react";
import { Link } from "react-router-dom";
import { useProjectCollection } from "../hooks/useProjectCollection";
import type { ReleaseType, Stage } from "../types";
import { STAGE_LABELS } from "../types";
import { nextChecklistItem } from "../utils";
import { EmptyState, Loading } from "../components/Loading";
import { ProjectCard } from "../components/ProjectCard";
import { Icon } from "../components/Icon";

const stageOptions = Object.keys(STAGE_LABELS) as Stage[];

export function Projects() {
  const { projects, checklists, loading, demo, error, reload } = useProjectCollection();
  const [query, setQuery] = useState("");
  const [release, setRelease] = useState<"ALL" | ReleaseType>("ALL");
  const [stage, setStage] = useState<"ALL" | Stage>("ALL");
  const [showArchived, setShowArchived] = useState(false);

  const filtered = useMemo(() => {
    const normalized = query.trim().toLowerCase();
    return projects
      .filter((project) => (showArchived ? true : !project.archived))
      .filter((project) => (release === "ALL" ? true : project.releaseType === release))
      .filter((project) => (stage === "ALL" ? true : project.stage === stage))
      .filter((project) => {
        if (!normalized) return true;
        return `${project.title} ${project.furigana ?? ""} ${project.genre ?? ""}`
          .toLowerCase()
          .includes(normalized);
      })
      .sort((a, b) => new Date(b.updatedAt).getTime() - new Date(a.updatedAt).getTime());
  }, [projects, query, release, stage, showArchived]);

  if (loading) {
    return <Loading label="作品一覧を読み込んでいます" />;
  }

  return (
    <div className="page-stack">
      <div className="page-heading">
        <div>
          <p className="eyebrow">作品</p>
          <h1>作品一覧</h1>
          <p className="page-subtitle">公開形態・制作段階・アーカイブ有無で絞り込めます。</p>
        </div>
        <div className="heading-actions">
          <button className="button button-ghost" type="button" onClick={() => void reload()}>
            <Icon name="refresh" size={16} />
            更新
          </button>
          <Link to="/projects/new" className="button button-primary">
            <Icon name="plus" size={17} />
            作品を追加
          </Link>
        </div>
      </div>

      {demo && (
        <div className="inline-banner">
          <Icon name="info" size={17} />
          <span>{error ?? "デモデータを表示しています。"}</span>
          <button type="button" className="text-button" onClick={() => void reload()}>
            再接続
          </button>
        </div>
      )}

      <div className="filter-bar">
        <label className="search-field">
          <Icon name="search" size={17} />
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="タイトル・ふりがな・ジャンルで検索"
          />
        </label>
        <label className="select-field">
          <span>公開形態</span>
          <select value={release} onChange={(event) => setRelease(event.target.value as "ALL" | ReleaseType)}>
            <option value="ALL">すべて</option>
            <option value="FREE">無料</option>
            <option value="PAID">有料</option>
          </select>
        </label>
        <label className="select-field">
          <span>制作段階</span>
          <select value={stage} onChange={(event) => setStage(event.target.value as "ALL" | Stage)}>
            <option value="ALL">すべて</option>
            {stageOptions.map((option) => (
              <option key={option} value={option}>
                {STAGE_LABELS[option]}
              </option>
            ))}
          </select>
        </label>
        <label className="switch-field">
          <input
            type="checkbox"
            checked={showArchived}
            onChange={(event) => setShowArchived(event.target.checked)}
          />
          <span className="switch-track" />
          アーカイブを表示
        </label>
      </div>

      <div className="list-summary">
        <span>{filtered.length}件の作品</span>
        <span className="list-summary-note">既定では未アーカイブを更新日時の降順で表示</span>
      </div>

      {filtered.length === 0 ? (
        <EmptyState
          title="条件に合う作品がありません"
          description="検索条件を変えるか、新しい作品を追加してください。"
          action={
            <Link to="/projects/new" className="button button-primary">
              <Icon name="plus" size={17} />
              作品を追加
            </Link>
          }
        />
      ) : (
        <div className="project-grid project-grid-wide">
          {filtered.map((project) => (
            <ProjectCard
              key={project.id}
              project={project}
              nextItem={nextChecklistItem(checklists[project.id] ?? [])}
            />
          ))}
        </div>
      )}
    </div>
  );
}

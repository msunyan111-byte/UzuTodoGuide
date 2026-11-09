import { Link } from "react-router-dom";
import type { ChecklistItem, Project, SectionDefinition } from "../../types";
import { RELEASE_LABELS, SCORING_LABELS } from "../../types";
import { formatDate } from "../../utils";
import { Badge, StageBadge } from "../Badge";
import { Icon } from "../Icon";
import { ProgressBar } from "../ProgressBar";

export function WorkspaceTopbar({
  project,
  section,
  doneCount,
  totalCount,
  nextItem,
  demo,
  onEdit,
}: {
  project: Project;
  section: SectionDefinition;
  doneCount: number;
  totalCount: number;
  nextItem: ChecklistItem | undefined;
  demo: boolean;
  onEdit: () => void;
}) {
  return (
    <>
      <div className="workspace-topbar">
        <div className="breadcrumb">
          <Link to="/projects">作品</Link>
          <span>/</span>
          <span>{project.title}</span>
          <span>/</span>
          <span>{section.label}</span>
        </div>
        <div className="workspace-topbar-actions">
          <Link to={`/projects/${project.id}/compact`} className="button button-ghost button-small">
            <Icon name="compact" size={16} />
            コンパクト
          </Link>
          <button type="button" className="button button-ghost button-small" onClick={onEdit}>
            作品情報を編集
          </button>
          <a
            className="button button-primary button-small"
            href={project.studioUrl ?? "https://studio.uzu-app.com/"}
            target="_blank"
            rel="noreferrer"
          >
            <Icon name="external" size={16} />
            UZUを開く
          </a>
        </div>
      </div>

      <header className="workspace-header">
        <div>
          <div className="hero-labels">
            <StageBadge stage={project.stage} />
            <Badge tone={project.releaseType === "PAID" ? "amber" : "green"} outline>
              {RELEASE_LABELS[project.releaseType]}
            </Badge>
            <Badge tone="neutral" outline>
              {SCORING_LABELS[project.scoringRule]}
            </Badge>
          </div>
          <h1>{project.title}</h1>
          <p className="workspace-subtitle">
            {project.genre ?? "ジャンル未設定"} ・ {project.playerCount ?? "–"}人 ・{" "}
            {project.estimatedMinutes ?? "–"}分 ・ 更新 {formatDate(project.updatedAt)}
          </p>
        </div>
        <div className="workspace-progress">
          <div className="workspace-progress-top">
            <span>全体の進捗</span>
            <strong>
              {doneCount}/{totalCount}
            </strong>
          </div>
          <ProgressBar value={totalCount ? (doneCount / totalCount) * 100 : 0} />
          <span className="workspace-progress-note">
            次: {nextItem?.name ?? "すべて完了しています"}
          </span>
        </div>
      </header>

      {demo && (
        <div className="inline-banner">
          <Icon name="info" size={17} />
          <span>APIに接続できないため、デモデータを表示しています。編集はこの画面内だけに反映されます。</span>
        </div>
      )}
    </>
  );
}

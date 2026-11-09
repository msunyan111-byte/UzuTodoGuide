import { Link } from "react-router-dom";
import type { ChecklistItem, Project } from "../types";
import { RELEASE_LABELS } from "../types";
import { formatDate, progressPercent } from "../utils";
import { Badge, StageBadge } from "./Badge";
import { Icon } from "./Icon";
import { ProgressBar } from "./ProgressBar";

export function ProjectCard({
  project,
  nextItem,
}: {
  project: Project;
  nextItem?: ChecklistItem;
}) {
  const percent = progressPercent(project);
  return (
    <article className="project-card">
      <div className="project-card-top">
        <div className="project-card-badges">
          <StageBadge stage={project.stage} />
          <Badge tone={project.releaseType === "PAID" ? "amber" : "green"} outline>
            {RELEASE_LABELS[project.releaseType]}
          </Badge>
        </div>
        {project.archived && <Badge tone="neutral">アーカイブ</Badge>}
      </div>
      <h3 className="project-card-title">{project.title}</h3>
      <div className="project-card-meta">
        <span>{project.playerCount ? `${project.playerCount}人` : "人数未設定"}</span>
        <span>{project.estimatedMinutes ? `${project.estimatedMinutes}分` : "時間未設定"}</span>
        <span className="project-card-updated">
          <Icon name="clock" size={14} />
          {formatDate(project.updatedAt)}
        </span>
      </div>
      <div className="project-progress">
        <div className="project-progress-labels">
          <span>チェック進捗</span>
          <strong>
            {project.checklistCompleted}/{project.checklistTotal}
          </strong>
        </div>
        <ProgressBar value={percent} tone={percent === 100 ? "green" : "purple"} size="sm" />
      </div>
      <div className="next-action">
        <span className="next-action-label">次にやること</span>
        <span className="next-action-text">{nextItem?.name ?? "すべて完了しています"}</span>
      </div>
      <Link to={`/projects/${project.id}`} className="project-card-link">
        ワークスペースを開く
        <Icon name="arrowRight" size={16} />
      </Link>
    </article>
  );
}

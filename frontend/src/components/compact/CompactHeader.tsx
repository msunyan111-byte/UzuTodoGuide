import { Link } from "react-router-dom";
import { ProjectBadges } from "../Badge";
import { Icon } from "../Icon";
import { ProgressBar } from "../ProgressBar";
import type { Project } from "../../types";
import { studioUrl } from "../../utils";

interface CompactHeaderProps {
  project: Project;
  progress: { done: number; total: number };
  demo: boolean;
  error: string | null;
}

export function CompactHeader({ project, progress, demo, error }: CompactHeaderProps) {
  return (
    <header className="compact-header">
      <div className="compact-title-row">
        <Link to={`/projects/${project.id}`} className="compact-back" aria-label="ワークスペースに戻る">
          <Icon name="arrowRight" size={17} className="back-arrow" />
        </Link>
        <div className="compact-title">
          <strong>{project.title}</strong>
          <div>
            <ProjectBadges project={project} />
          </div>
        </div>
        <a
          className="compact-uzu"
          href={studioUrl(project.studioUrl)}
          target="_blank"
          rel="noreferrer"
        >
          <Icon name="external" size={18} />
        </a>
      </div>
      <div className="compact-progress">
        <ProgressBar value={progress.total ? (progress.done / progress.total) * 100 : 0} size="sm" />
        <span>
          {progress.done}/{progress.total}
        </span>
      </div>
      {demo && <p className="compact-demo">{error ?? "デモデータを表示中"}</p>}
    </header>
  );
}

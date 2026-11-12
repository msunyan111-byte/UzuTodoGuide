import { Link } from "react-router-dom";
import { ProjectBadges } from "../Badge";
import { Icon } from "../Icon";
import { ProgressBar } from "../ProgressBar";
import type { ChecklistItem, Project } from "../../types";
import { progressPercent, studioUrl } from "../../utils";

interface DashboardHeroProps {
  project: Project;
  nextItem: ChecklistItem | undefined;
}

export function DashboardHero({ project, nextItem }: DashboardHeroProps) {
  return (
    <section className="hero-card">
      <div className="hero-card-content">
        <div className="hero-labels">
          <span>続きから</span>
          <ProjectBadges project={project} />
        </div>
        <h2>{project.title}</h2>
        <div className="hero-progress">
          <div>
            <span>全体の進捗</span>
            <strong>
              {project.checklistCompleted}/{project.checklistTotal}
            </strong>
          </div>
          <ProgressBar value={progressPercent(project)} size="md" />
        </div>
        <div className="hero-next">
          <span className="hero-next-label">次にやること</span>
          <span>{nextItem?.name ?? "すべてのチェックが完了しています"}</span>
        </div>
        <div className="hero-actions">
          <Link to={`/projects/${project.id}`} className="button button-light">
            ワークスペースを開く
            <Icon name="arrowRight" size={16} />
          </Link>
          <a
            className="button button-ghost-light"
            href={studioUrl(project.studioUrl)}
            target="_blank"
            rel="noreferrer"
          >
            <Icon name="external" size={16} />
            UZUを開く
          </a>
        </div>
      </div>
      <div className="hero-orbit hero-orbit-one" />
      <div className="hero-orbit hero-orbit-two" />
      <div className="hero-spark">
        <Icon name="spark" size={44} />
      </div>
    </section>
  );
}

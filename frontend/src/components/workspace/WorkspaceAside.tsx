import { Link } from "react-router-dom";
import type { ChecklistItem, Project } from "../../types";
import { RELEASE_LABELS, SCORING_LABELS, STAGE_LABELS } from "../../types";
import { Icon } from "../Icon";
import type { Readiness } from "./readiness";

export function WorkspaceAside({
  project,
  nextItem,
  readiness,
  onCompleteNext,
}: {
  project: Project;
  nextItem: ChecklistItem | undefined;
  readiness: Readiness[];
  onCompleteNext: (item: ChecklistItem) => void;
}) {
  return (
    <aside className="workspace-aside">
      <section className="panel aside-panel">
        <div className="aside-project">
          <span className="aside-project-icon">
            <Icon name="projects" size={20} />
          </span>
          <div>
            <strong>{project.title}</strong>
            <span>{STAGE_LABELS[project.stage]}</span>
          </div>
        </div>
        <dl className="info-list">
          <div>
            <dt>公開形態</dt>
            <dd>{RELEASE_LABELS[project.releaseType]}</dd>
          </div>
          <div>
            <dt>得点ルール</dt>
            <dd>{SCORING_LABELS[project.scoringRule]}</dd>
          </div>
          <div>
            <dt>プレイ人数</dt>
            <dd>{project.playerCount ?? "未設定"}人</dd>
          </div>
          <div>
            <dt>想定時間</dt>
            <dd>{project.estimatedMinutes ?? "未設定"}分</dd>
          </div>
        </dl>
      </section>

      <section className="panel aside-panel">
        <div className="panel-heading panel-heading-compact">
          <h2>次にやること</h2>
        </div>
        <div className="aside-next">
          <span className="aside-next-label">全体</span>
          <strong>{nextItem?.name ?? "すべて完了しています"}</strong>
          {nextItem && (
            <button
              type="button"
              className="button button-soft button-small"
              onClick={() => onCompleteNext(nextItem)}
            >
              <Icon name="check" size={15} />
              完了にする
            </button>
          )}
        </div>
      </section>

      <section className="panel aside-panel">
        <div className="panel-heading panel-heading-compact">
          <h2>公開準備チェック</h2>
        </div>
        <ul className="readiness-list">
          {readiness.map((item) => (
            <li key={item.label}>
              <span className={`readiness-icon ${item.state}`}>
                {item.state === "done" ? (
                  <Icon name="check" size={13} />
                ) : item.state === "na" ? (
                  <span>–</span>
                ) : (
                  <Icon name="clock" size={13} />
                )}
              </span>
              <div>
                <strong>{item.label}</strong>
                <span>{item.state === "na" ? "対象外" : item.detail}</span>
              </div>
            </li>
          ))}
        </ul>
        <Link to="/guide" className="text-link aside-link">
          機能ガイドで条件を確認
          <Icon name="arrowRight" size={14} />
        </Link>
      </section>
    </aside>
  );
}

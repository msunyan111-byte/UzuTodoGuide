import { Link } from "react-router-dom";
import { Icon } from "../Icon";
import type { DashboardWarning } from "../../hooks/useDashboardData";

interface DashboardWarningsProps {
  warnings: DashboardWarning[];
}

export function DashboardWarnings({ warnings }: DashboardWarningsProps) {
  return (
    <aside className="panel warning-panel">
      <div className="panel-heading">
        <div>
          <h2>公開準備の注意</h2>
          <p>見落としやすい条件をまとめています。</p>
        </div>
        <Icon name="alert" size={20} />
      </div>
      {warnings.length === 0 ? (
        <div className="all-clear">
          <span>
            <Icon name="check" size={18} />
          </span>
          <div>
            <strong>大きな警告はありません</strong>
            <p>公開前にバックアップと公式ガイドの最終確認を行ってください。</p>
          </div>
        </div>
      ) : (
        <ul className="warning-list">
          {warnings.map((warning, index) => (
            <li key={`${warning.projectId}-${index}`}>
              <span className={`warning-dot ${warning.tone}`} />
              <div>
                <p>{warning.text}</p>
                <Link to={`/projects/${warning.projectId}`} className="text-link">
                  該当作品を開く
                  <Icon name="arrowRight" size={14} />
                </Link>
              </div>
            </li>
          ))}
        </ul>
      )}
      <Link to="/guide" className="guide-cta">
        <span className="guide-cta-icon">
          <Icon name="guide" size={18} />
        </span>
        <div>
          <strong>UZU機能ガイド</strong>
          <p>制約と設定項目を検索できます。</p>
        </div>
        <Icon name="arrowRight" size={16} />
      </Link>
    </aside>
  );
}

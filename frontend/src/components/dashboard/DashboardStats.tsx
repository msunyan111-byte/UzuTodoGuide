import { Icon } from "../Icon";
import type { DashboardStats } from "../../hooks/useDashboardData";

interface DashboardStatsProps {
  stats: DashboardStats;
}

export function DashboardStats({ stats }: DashboardStatsProps) {
  return (
    <section className="stat-grid">
      <div className="stat-card">
        <span className="stat-icon purple">
          <Icon name="projects" size={19} />
        </span>
        <div>
          <strong>{stats.active}</strong>
          <span>進行中の作品</span>
        </div>
      </div>
      <div className="stat-card">
        <span className="stat-icon green">
          <Icon name="check" size={19} />
        </span>
        <div>
          <strong>
            {stats.done}
            <small>/{stats.total}</small>
          </strong>
          <span>完了チェック</span>
        </div>
      </div>
      <div className="stat-card">
        <span className="stat-icon amber">
          <Icon name="spark" size={19} />
        </span>
        <div>
          <strong>{stats.percent}%</strong>
          <span>全体の達成率</span>
        </div>
      </div>
      <div className="stat-card">
        <span className="stat-icon blue">
          <Icon name="guide" size={19} />
        </span>
        <div>
          <strong>{stats.paid}</strong>
          <span>有料作品</span>
        </div>
      </div>
    </section>
  );
}

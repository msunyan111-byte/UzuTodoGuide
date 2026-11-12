import { Link } from "react-router-dom";
import { useDashboardData } from "../hooks/useDashboardData";
import { useProjectCollection } from "../hooks/useProjectCollection";
import { nextChecklistItem } from "../utils";
import { Icon } from "../components/Icon";
import { EmptyState, Loading } from "../components/Loading";
import { ProjectCard } from "../components/ProjectCard";
import { DashboardHero } from "../components/dashboard/DashboardHero";
import { DashboardStats } from "../components/dashboard/DashboardStats";
import { DashboardWarnings } from "../components/dashboard/DashboardWarnings";

export function Dashboard() {
  const { projects, checklists, loading, demo, error, reload } = useProjectCollection();
  const { activeProjects, continueProject, continueNext, stats, warnings, todayLabel } =
    useDashboardData(projects, checklists);

  if (loading) {
    return <Loading label="作品を読み込んでいます" />;
  }

  return (
    <div className="page-stack">
      <div className="page-heading">
        <div>
          <p className="eyebrow">ダッシュボード</p>
          <h1>制作の続きを、ここから。</h1>
          <p className="page-subtitle">{todayLabel} ・ UZUの編集画面と並べて使えます。</p>
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

      {activeProjects.length === 0 ? (
        <EmptyState
          title="作品がまだありません"
          description="UZUの作品を登録すると、画面ごとの確認項目と次アクションをまとめて追えます。"
          action={
            <Link to="/projects/new" className="button button-primary">
              <Icon name="plus" size={17} />
              最初の作品を追加
            </Link>
          }
        />
      ) : (
        <>
          <DashboardHero project={continueProject} nextItem={continueNext} />
          <DashboardStats stats={stats} />

          <div className="content-grid">
            <section className="panel">
              <div className="panel-heading">
                <div>
                  <h2>作品</h2>
                  <p>更新の新しい順に表示しています。</p>
                </div>
                <Link to="/projects" className="text-link">
                  すべて見る
                  <Icon name="arrowRight" size={15} />
                </Link>
              </div>
              <div className="project-grid">
                {activeProjects.slice(0, 3).map((project) => (
                  <ProjectCard
                    key={project.id}
                    project={project}
                    nextItem={nextChecklistItem(checklists[project.id] ?? [])}
                  />
                ))}
              </div>
            </section>

            <DashboardWarnings warnings={warnings} />
          </div>
        </>
      )}
    </div>
  );
}

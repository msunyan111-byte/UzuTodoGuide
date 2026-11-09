import { Link } from "react-router-dom";
import { useMemo } from "react";
import { useProjectCollection } from "../hooks/useProjectCollection";
import { nextChecklistItem, progressPercent } from "../utils";
import { RELEASE_LABELS } from "../types";
import { Badge, StageBadge } from "../components/Badge";
import { Icon } from "../components/Icon";
import { Loading, EmptyState } from "../components/Loading";
import { ProjectCard } from "../components/ProjectCard";
import { ProgressBar } from "../components/ProgressBar";

export function Dashboard() {
  const { projects, checklists, loading, demo, error, reload } = useProjectCollection();

  const activeProjects = useMemo(
    () => projects.filter((project) => !project.archived),
    [projects],
  );

  const continueProject = useMemo(() => {
    return [...activeProjects].sort(
      (a, b) => new Date(b.updatedAt).getTime() - new Date(a.updatedAt).getTime(),
    )[0];
  }, [activeProjects]);

  const continueNext = continueProject
    ? nextChecklistItem(checklists[continueProject.id] ?? [])
    : undefined;

  const stats = useMemo(() => {
    const done = activeProjects.reduce((sum, project) => sum + project.checklistCompleted, 0);
    const total = activeProjects.reduce((sum, project) => sum + project.checklistTotal, 0);
    const paid = activeProjects.filter((project) => project.releaseType === "PAID").length;
    return {
      active: activeProjects.length,
      done,
      total,
      paid,
      percent: total > 0 ? Math.round((done / total) * 100) : 0,
    };
  }, [activeProjects]);

  const warnings = useMemo(() => {
    const list: Array<{ text: string; projectId: string; tone: "amber" | "red" }> = [];
    const today = new Date();
    activeProjects.forEach((project) => {
      const items = checklists[project.id] ?? [];
      if (project.releaseType === "PAID") {
        const paidTest = items.find(
          (item) => item.name.includes("テストプレイ3回") && item.status !== "DONE",
        );
        if (paidTest) {
          list.push({
            text: "有料作品のテストプレイが、UZU 1回を含む3回以上に届いていません。",
            projectId: project.id,
            tone: "amber",
          });
        }
      }
      const overdue = items.find(
        (item) =>
          item.dueDate &&
          item.status !== "DONE" &&
          new Date(`${item.dueDate}T23:59:59+09:00`).getTime() < today.getTime(),
      );
      if (overdue) {
        list.push({
          text: `期限を過ぎた項目があります: ${overdue.name}`,
          projectId: project.id,
          tone: "red",
        });
      }
      const reviewPending = items.find(
        (item) => item.name.includes("審査") && item.status !== "DONE",
      );
      if (
        reviewPending &&
        ["REVIEW_SUBMITTED", "REVIEW_RESPONSE", "RELEASE_PREPARATION"].includes(project.stage)
      ) {
        list.push({
          text: `審査・公開準備の未完了項目があります: ${reviewPending.name}`,
          projectId: project.id,
          tone: "amber",
        });
      }
    });
    return list.slice(0, 3);
  }, [activeProjects, checklists]);

  const todayLabel = new Intl.DateTimeFormat("ja-JP", {
    month: "long",
    day: "numeric",
    weekday: "short",
  }).format(new Date());

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
          <section className="hero-card">
            <div className="hero-card-content">
              <div className="hero-labels">
                <span>続きから</span>
                <StageBadge stage={continueProject.stage} />
                <Badge tone={continueProject.releaseType === "PAID" ? "amber" : "green"} outline>
                  {RELEASE_LABELS[continueProject.releaseType]}
                </Badge>
              </div>
              <h2>{continueProject.title}</h2>
              <div className="hero-progress">
                <div>
                  <span>全体の進捗</span>
                  <strong>
                    {continueProject.checklistCompleted}/{continueProject.checklistTotal}
                  </strong>
                </div>
                <ProgressBar value={progressPercent(continueProject)} size="md" />
              </div>
              <div className="hero-next">
                <span className="hero-next-label">次にやること</span>
                <span>{continueNext?.name ?? "すべてのチェックが完了しています"}</span>
              </div>
              <div className="hero-actions">
                <Link to={`/projects/${continueProject.id}`} className="button button-light">
                  ワークスペースを開く
                  <Icon name="arrowRight" size={16} />
                </Link>
                {continueProject.studioUrl ? (
                  <a
                    className="button button-ghost-light"
                    href={continueProject.studioUrl}
                    target="_blank"
                    rel="noreferrer"
                  >
                    <Icon name="external" size={16} />
                    UZUを開く
                  </a>
                ) : (
                  <a
                    className="button button-ghost-light"
                    href="https://studio.uzu-app.com/"
                    target="_blank"
                    rel="noreferrer"
                  >
                    <Icon name="external" size={16} />
                    UZUを開く
                  </a>
                )}
              </div>
            </div>
            <div className="hero-orbit hero-orbit-one" />
            <div className="hero-orbit hero-orbit-two" />
            <div className="hero-spark">
              <Icon name="spark" size={44} />
            </div>
          </section>

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
          </div>
        </>
      )}
    </div>
  );
}

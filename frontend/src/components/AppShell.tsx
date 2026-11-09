import { useEffect, useState, type ReactNode } from "react";
import { NavLink, useLocation } from "react-router-dom";
import { Icon, type IconName } from "./Icon";

const navItems: Array<{ to: string; label: string; icon: IconName; end?: boolean }> = [
  { to: "/", label: "ダッシュボード", icon: "dashboard", end: true },
  { to: "/projects", label: "作品", icon: "projects" },
  { to: "/guide", label: "UZU機能ガイド", icon: "guide" },
  { to: "/settings", label: "設定・バックアップ", icon: "settings" },
];

export function AppShell({ children }: { children: ReactNode }) {
  const [mobileOpen, setMobileOpen] = useState(false);
  const location = useLocation();

  useEffect(() => {
    window.scrollTo({ top: 0, behavior: "auto" });
  }, [location.pathname]);

  return (
    <div className="app-shell">
      <aside className={`sidebar${mobileOpen ? " sidebar-open" : ""}`}>
        <div className="brand">
          <div className="brand-mark">
            <span />
            <span />
            <span />
          </div>
          <div>
            <div className="brand-name">uzu-todo-guide</div>
            <div className="brand-sub">STUDIO COMPANION</div>
          </div>
          <button
            className="icon-button sidebar-close"
            type="button"
            onClick={() => setMobileOpen(false)}
            aria-label="メニューを閉じる"
          >
            <Icon name="close" />
          </button>
        </div>

        <nav className="side-nav" aria-label="メインナビゲーション">
          {navItems.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              end={item.end}
              className={({ isActive }) => `side-link${isActive ? " active" : ""}`}
              onClick={() => setMobileOpen(false)}
            >
              <Icon name={item.icon} />
              <span>{item.label}</span>
            </NavLink>
          ))}
          <NavLink
            to="/compact"
            className={({ isActive }) => `side-link${isActive ? " active" : ""}`}
            onClick={() => setMobileOpen(false)}
          >
            <Icon name="compact" />
            <span>コンパクトモード</span>
          </NavLink>
        </nav>

        <div className="sidebar-note">
          <div className="sidebar-note-icon">
            <Icon name="spark" size={17} />
          </div>
          <div>
            <strong>UZUは別タブで開く</strong>
            <p>このアプリはUZUの再実装ではありません。編集はUZU上で行い、ここで確認と記録を進めます。</p>
          </div>
        </div>

        <a
          className="sidebar-external"
          href="https://docs.studio.uzu-app.com/"
          target="_blank"
          rel="noreferrer"
          onClick={() => setMobileOpen(false)}
        >
          <Icon name="external" size={16} />
          公式ガイドを開く
        </a>
      </aside>

      {mobileOpen && (
        <div className="sidebar-scrim" onClick={() => setMobileOpen(false)} role="presentation" />
      )}

      <div className="app-main">
        <header className="mobile-topbar">
          <button
            className="icon-button"
            type="button"
            onClick={() => setMobileOpen(true)}
            aria-label="メニューを開く"
          >
            <Icon name="menu" />
          </button>
          <span className="mobile-brand">uzu-todo-guide</span>
          <a
            className="icon-button"
            href="https://docs.studio.uzu-app.com/"
            target="_blank"
            rel="noreferrer"
            aria-label="公式ガイド"
          >
            <Icon name="external" />
          </a>
        </header>
        <main className="page">{children}</main>
      </div>
    </div>
  );
}

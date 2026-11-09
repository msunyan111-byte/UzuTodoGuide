import { useMemo, useState } from "react";
import { Link } from "react-router-dom";
import { Icon } from "../components/Icon";
import { Modal } from "../components/Modal";
import { Badge } from "../components/Badge";
import { SECTIONS } from "../data/uzu";
import type { SectionDefinition, SectionGroup } from "../types";
import { GROUP_ORDER } from "../data/uzu";
import { officialUrl } from "../utils";

type TemplateFilter = "all" | "basic" | "advanced";

export function Guide() {
  const [query, setQuery] = useState("");
  const [group, setGroup] = useState<"ALL" | SectionGroup>("ALL");
  const [template, setTemplate] = useState<TemplateFilter>("all");
  const [constraintsOnly, setConstraintsOnly] = useState(false);
  const [selected, setSelected] = useState<SectionDefinition | null>(null);

  const filtered = useMemo(() => {
    const normalized = query.trim().toLowerCase();
    return SECTIONS.filter((section) => {
      if (group !== "ALL" && section.group !== group) return false;
      if (template === "basic" && section.basic === "no") return false;
      if (template === "advanced" && section.advanced === "no") return false;
      if (constraintsOnly && section.constraints.length === 0) return false;
      if (!normalized) return true;
      const text = [
        section.label,
        section.purpose,
        ...section.fields,
        ...section.constraints,
        ...section.tips,
      ]
        .join(" ")
        .toLowerCase();
      return text.includes(normalized);
    });
  }, [query, group, template, constraintsOnly]);

  return (
    <div className="page-stack">
      <div className="page-heading">
        <div>
          <p className="eyebrow">UZU機能ガイド</p>
          <h1>設定項目と制約を、制作中に引く。</h1>
          <p className="page-subtitle">
            公式ガイドの内容を、画面ごとの確認項目として要約しています。
          </p>
        </div>
        <a
          className="button button-primary"
          href="https://docs.studio.uzu-app.com/"
          target="_blank"
          rel="noreferrer"
        >
          <Icon name="external" size={16} />
          公式ガイドを開く
        </a>
      </div>

      <div className="guide-filter">
        <label className="search-field search-field-large">
          <Icon name="search" size={18} />
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="機能名・設定項目・制約・注意文で検索"
          />
        </label>
        <label className="select-field">
          <span>テンプレート</span>
          <select value={template} onChange={(event) => setTemplate(event.target.value as TemplateFilter)}>
            <option value="all">すべて</option>
            <option value="basic">基本で使う</option>
            <option value="advanced">発展で使う</option>
          </select>
        </label>
        <label className="switch-field">
          <input
            type="checkbox"
            checked={constraintsOnly}
            onChange={(event) => setConstraintsOnly(event.target.checked)}
          />
          <span className="switch-track" />
          制約あり
        </label>
      </div>

      <div className="group-chips">
        <button
          type="button"
          className={`group-chip${group === "ALL" ? " active" : ""}`}
          onClick={() => setGroup("ALL")}
        >
          すべて
          <span>{SECTIONS.length}</span>
        </button>
        {GROUP_ORDER.map((item) => {
          const count = SECTIONS.filter((section) => section.group === item).length;
          return (
            <button
              key={item}
              type="button"
              className={`group-chip${group === item ? " active" : ""}`}
              onClick={() => setGroup(item)}
            >
              {item}
              <span>{count}</span>
            </button>
          );
        })}
      </div>

      <div className="list-summary">
        <span>{filtered.length}件の機能</span>
        <span className="list-summary-note">
          推定バッジの機能は、現行のUZU画面で確認するまで確定情報として扱わないでください。
        </span>
      </div>

      {filtered.length === 0 ? (
        <div className="empty-state">
          <div className="empty-orb" />
          <h3>条件に合う機能がありません</h3>
          <p>検索語を短くするか、フィルターを解除してください。</p>
        </div>
      ) : (
        <div className="guide-grid">
          {filtered.map((section) => (
            <article className="guide-card" key={section.key}>
              <div className="guide-card-top">
                <span className="guide-group">{section.group}</span>
                <Badge tone={section.confidence === "confirmed" ? "green" : "amber"} outline>
                  {section.confidence === "confirmed" ? "公式確認済み" : "推定"}
                </Badge>
              </div>
              <h2>{section.label}</h2>
              <p className="guide-purpose">{section.purpose}</p>
              <div className="guide-path">{section.uzuPath}</div>
              {section.fields.length > 0 && (
                <div className="chip-cloud">
                  {section.fields.slice(0, 4).map((field) => (
                    <span className="info-chip" key={field}>
                      {field}
                    </span>
                  ))}
                  {section.fields.length > 4 && (
                    <span className="info-chip info-chip-more">+{section.fields.length - 4}</span>
                  )}
                </div>
              )}
              {section.constraints.length > 0 && (
                <div className="guide-constraints">
                  <Icon name="alert" size={14} />
                  <span>{section.constraints[0]}</span>
                  {section.constraints.length > 1 && <em>+{section.constraints.length - 1}</em>}
                </div>
              )}
              <div className="guide-card-actions">
                <button type="button" className="text-link" onClick={() => setSelected(section)}>
                  詳細を見る
                  <Icon name="arrowRight" size={14} />
                </button>
                <a
                  className="text-link muted-link"
                  href={officialUrl(section.uzuPath)}
                  target="_blank"
                  rel="noreferrer"
                >
                  公式
                  <Icon name="external" size={13} />
                </a>
              </div>
            </article>
          ))}
        </div>
      )}

      {selected && (
        <Modal open onClose={() => setSelected(null)} title={selected.label} size="lg">
          <div className="guide-detail">
            <div className="guide-detail-meta">
              <Badge tone="purple">{selected.group}</Badge>
              {selected.basic === "yes" && <Badge tone="purple" outline>基本</Badge>}
              {selected.advanced === "yes" && <Badge tone="blue" outline>発展</Badge>}
              <Badge tone={selected.confidence === "confirmed" ? "green" : "amber"} outline>
                {selected.confidence === "confirmed" ? "公式確認済み" : "推定"}
              </Badge>
            </div>
            <p className="guide-detail-purpose">{selected.purpose}</p>
            <div className="guide-detail-path">
              <span>公式パス</span>
              <code>{selected.uzuPath}</code>
            </div>
            <div className="guide-detail-grid">
              <div>
                <h3>主な設定項目</h3>
                {selected.fields.length > 0 ? (
                  <div className="chip-cloud">
                    {selected.fields.map((field) => (
                      <span className="info-chip" key={field}>
                        {field}
                      </span>
                    ))}
                  </div>
                ) : (
                  <p className="muted">項目情報はありません。</p>
                )}
              </div>
              <div>
                <h3>制約・注意</h3>
                {selected.constraints.length > 0 ? (
                  <ul className="tip-list">
                    {selected.constraints.map((constraint) => (
                      <li key={constraint}>{constraint}</li>
                    ))}
                  </ul>
                ) : (
                  <p className="muted">大きな制約はありません。</p>
                )}
              </div>
            </div>
            <div>
              <h3>確認のヒント</h3>
              <ul className="tip-list">
                {selected.tips.map((tip) => (
                  <li key={tip}>{tip}</li>
                ))}
              </ul>
            </div>
            <div className="modal-actions">
              <a
                className="button button-primary"
                href={officialUrl(selected.uzuPath)}
                target="_blank"
                rel="noreferrer"
              >
                <Icon name="external" size={16} />
                公式ページを開く
              </a>
              <Link to="/projects" className="button button-ghost" onClick={() => setSelected(null)}>
                作品ワークスペースへ
                <Icon name="arrowRight" size={15} />
              </Link>
            </div>
          </div>
        </Modal>
      )}
    </div>
  );
}

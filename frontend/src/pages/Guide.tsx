import { useMemo, useState } from "react";
import { Icon } from "../components/Icon";
import { GuideCard } from "../components/guide/GuideCard";
import { GuideDetailModal } from "../components/guide/GuideDetailModal";
import { GuideFaqSection } from "../components/guide/GuideFaqSection";
import { GROUP_ORDER, SECTIONS } from "../data/uzu";
import type { SectionDefinition, SectionGroup } from "../types";
import { OFFICIAL_DOCS_URL } from "../utils";

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
          href={OFFICIAL_DOCS_URL}
          target="_blank"
          rel="noreferrer"
        >
          <Icon name="external" size={16} />
          公式ガイドを開く
        </a>
      </div>

      <GuideFaqSection onSelectSection={setSelected} />

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
            <GuideCard key={section.key} section={section} onSelect={setSelected} />
          ))}
        </div>
      )}

      {selected && <GuideDetailModal section={selected} onClose={() => setSelected(null)} />}
    </div>
  );
}

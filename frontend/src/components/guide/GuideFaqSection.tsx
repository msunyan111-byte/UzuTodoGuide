import { useEffect, useMemo, useState } from "react";
import { api } from "../../api";
import { Icon } from "../Icon";
import { SECTIONS } from "../../data/uzu";
import type { GuideFaq, SectionDefinition } from "../../types";

interface GuideFaqSectionProps {
  onSelectSection: (section: SectionDefinition) => void;
}

export function GuideFaqSection({ onSelectSection }: GuideFaqSectionProps) {
  const [faqs, setFaqs] = useState<GuideFaq[]>([]);
  const [status, setStatus] = useState<"loading" | "ready" | "error">("loading");
  const [reloadKey, setReloadKey] = useState(0);
  const [query, setQuery] = useState("");
  const [category, setCategory] = useState("ALL");

  useEffect(() => {
    let cancelled = false;
    const load = async () => {
      setStatus("loading");
      try {
        const items = await api.listGuideFaqs();
        if (!cancelled) {
          setFaqs(items);
          setStatus("ready");
        }
      } catch {
        if (!cancelled) {
          setFaqs([]);
          setStatus("error");
        }
      }
    };
    void load();
    return () => {
      cancelled = true;
    };
  }, [reloadKey]);

  const categories = useMemo(() => {
    const counts = new Map<string, { code: string; label: string; count: number }>();
    for (const faq of faqs) {
      const current = counts.get(faq.category);
      if (current) {
        current.count += 1;
      } else {
        counts.set(faq.category, {
          code: faq.category,
          label: faq.categoryLabel,
          count: 1,
        });
      }
    }
    return Array.from(counts.values());
  }, [faqs]);

  const filtered = useMemo(() => {
    const normalized = query.trim().toLowerCase();
    return faqs.filter((faq) => {
      if (category !== "ALL" && faq.category !== category) return false;
      if (!normalized) return true;
      return [faq.question, faq.answer, faq.categoryLabel]
        .join(" ")
        .toLowerCase()
        .includes(normalized);
    });
  }, [faqs, query, category]);

  return (
    <section className="guide-faq-section" aria-labelledby="guide-faq-title">
      <div className="guide-faq-heading">
        <div>
          <p className="eyebrow">困ったときのQ&A</p>
          <h2 id="guide-faq-title">つまずいたときの原因と、次の操作。</h2>
          <p className="page-subtitle">
            公式ガイドで案内されているトラブルと確認手順です。内容はデータベースで管理しています。
          </p>
        </div>
        {status === "ready" && <span className="guide-faq-count">{filtered.length}件</span>}
      </div>

      {status === "loading" && (
        <div className="empty-state guide-faq-state">
          <div className="empty-orb" />
          <h3>Q&Aを読み込んでいます</h3>
          <p>少し待ってから、もう一度確認してください。</p>
        </div>
      )}

      {status === "error" && (
        <div className="empty-state guide-faq-state">
          <div className="empty-orb" />
          <h3>Q&Aを読み込めませんでした</h3>
          <p>APIに接続できません。Spring Bootが起動しているか確認してください。</p>
          <button
            type="button"
            className="button button-ghost"
            onClick={() => setReloadKey((key) => key + 1)}
          >
            再読み込み
          </button>
        </div>
      )}

      {status === "ready" && (
        <>
          <div className="guide-faq-filter">
            <label className="search-field search-field-large">
              <Icon name="search" size={18} />
              <input
                type="search"
                value={query}
                onChange={(event) => setQuery(event.target.value)}
                placeholder="質問・回答・カテゴリで検索"
              />
            </label>
          </div>

          <div className="group-chips">
            <button
              type="button"
              className={`group-chip${category === "ALL" ? " active" : ""}`}
              onClick={() => setCategory("ALL")}
            >
              すべて
              <span>{faqs.length}</span>
            </button>
            {categories.map((item) => (
              <button
                key={item.code}
                type="button"
                className={`group-chip${category === item.code ? " active" : ""}`}
                onClick={() => setCategory(item.code)}
              >
                {item.label}
                <span>{item.count}</span>
              </button>
            ))}
          </div>

          <div className="list-summary">
            <span>{filtered.length}件のQ&A</span>
            <span className="list-summary-note">
              公式Q&Aの内容を、原因と次の操作が分かる形に整理しています。
            </span>
          </div>

          {filtered.length === 0 ? (
            <div className="empty-state">
              <div className="empty-orb" />
              <h3>条件に合うQ&Aがありません</h3>
              <p>検索語を短くするか、カテゴリを変更してください。</p>
            </div>
          ) : (
            <div className="faq-list">
              {filtered.map((faq) => {
                const relatedSection = faq.sectionKey
                  ? SECTIONS.find((section) => section.key === faq.sectionKey)
                  : undefined;
                return (
                  <details className="faq-item" key={faq.id}>
                    <summary className="faq-summary">
                      <span className="faq-question">{faq.question}</span>
                      <span className="faq-summary-meta">
                        <span className="faq-category">{faq.categoryLabel}</span>
                        <Icon name="chevron" size={16} className="faq-chevron" />
                      </span>
                    </summary>
                    <div className="faq-answer">
                      <p>{faq.answer}</p>
                      <div className="faq-actions">
                        <span className="faq-checked">確認日 {faq.checkedAt}</span>
                        {relatedSection && (
                          <button
                            type="button"
                            className="text-link"
                            onClick={() => onSelectSection(relatedSection)}
                          >
                            <Icon name="arrowRight" size={14} />
                            関連する画面を見る
                          </button>
                        )}
                        <a
                          className="text-link"
                          href={faq.sourceUrl}
                          target="_blank"
                          rel="noreferrer"
                        >
                          <Icon name="external" size={14} />
                          公式Q&A
                        </a>
                      </div>
                    </div>
                  </details>
                );
              })}
            </div>
          )}
        </>
      )}
    </section>
  );
}

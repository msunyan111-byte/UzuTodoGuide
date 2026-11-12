import type { SectionDefinition } from "../../types";
import { supportForSection } from "../../data/creatorSupport";
import { FieldChips } from "../FieldChips";
import { Icon } from "../Icon";

export function GuidancePanel({ section }: { section: SectionDefinition }) {
  const support = supportForSection(section);

  return (
    <section className="panel guidance-panel">
      <div className="panel-heading">
        <div>
          <h2>制作サポート</h2>
          <p>{section.label}の作業前に読む、短いヒントと参考リンクです。</p>
        </div>
        <Icon name="spark" size={20} />
      </div>

      <div className="support-hero">
        <div className="support-hero-copy">
          <span className="support-kicker">ミニレクチャー</span>
          <h3>{support.headline}</h3>
          <p>{support.summary}</p>
        </div>
        <span className="support-section-chip">{section.label}</span>
      </div>

      <div className="support-grid">
        <div className="support-block">
          <h3>3ステップで進める</h3>
          <ol className="support-step-list">
            {support.steps.map((step) => (
              <li key={step}>{step}</li>
            ))}
          </ol>
        </div>
        <div className="support-block">
          <h3>考えるヒント</h3>
          <ul className="support-prompt-list">
            {support.prompts.map((prompt) => (
              <li key={prompt}>{prompt}</li>
            ))}
          </ul>
        </div>
      </div>

      <div className="support-resources">
        <div className="support-block-head">
          <h3>参考リンク・レクチャー</h3>
          <span>{support.resources.length}件</span>
        </div>
        <div className="support-resource-grid">
          {support.resources.map((resource) => (
            <a
              className="support-resource"
              data-kind={resource.kind}
              href={resource.url}
              key={resource.url}
              rel="noreferrer"
              target="_blank"
            >
              <span className="support-resource-kind">{resource.kind}</span>
              <strong>{resource.title}</strong>
              <span className="support-resource-source">
                {resource.source}
                <Icon name="external" size={11} />
              </span>
              <span className="support-resource-description">{resource.description}</span>
            </a>
          ))}
        </div>
      </div>

      <div className="support-details">
        <div className="guidance-grid">
          <div>
            <h3>主な設定項目</h3>
            {section.fields.length > 0 ? (
              <FieldChips fields={section.fields} />
            ) : (
              <p className="muted">項目情報はありません。</p>
            )}
          </div>
          <div>
            <h3>気をつけること</h3>
            <ul className="tip-list">
              {section.tips.map((tip) => (
                <li key={tip}>{tip}</li>
              ))}
            </ul>
          </div>
        </div>
      </div>
    </section>
  );
}

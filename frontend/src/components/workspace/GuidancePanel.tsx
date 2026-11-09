import type { SectionDefinition } from "../../types";
import { Icon } from "../Icon";

export function GuidancePanel({ section }: { section: SectionDefinition }) {
  return (
    <section className="panel guidance-panel">
      <div className="panel-heading">
        <div>
          <h2>確認のヒント</h2>
          <p>公式ガイドの内容をもとにした短いチェックポイントです。</p>
        </div>
        <Icon name="spark" size={20} />
      </div>
      <div className="guidance-grid">
        <div>
          <h3>主な設定項目</h3>
          {section.fields.length > 0 ? (
            <div className="chip-cloud">
              {section.fields.map((field) => (
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
          <h3>気をつけること</h3>
          <ul className="tip-list">
            {section.tips.map((tip) => (
              <li key={tip}>{tip}</li>
            ))}
          </ul>
        </div>
      </div>
    </section>
  );
}

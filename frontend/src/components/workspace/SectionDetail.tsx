import type { ChecklistItem, SectionDefinition } from "../../types";
import { copyText, officialUrl } from "../../utils";
import { Badge } from "../Badge";
import { Icon } from "../Icon";
import { useToast } from "../Toast";

export function SectionDetail({
  section,
  nextItem,
}: {
  section: SectionDefinition;
  nextItem: ChecklistItem | undefined;
}) {
  const toast = useToast();

  const copyName = async () => {
    try {
      await copyText(section.label);
      toast.show("画面名をコピーしました。", "success");
    } catch {
      toast.show("コピーできませんでした。", "error");
    }
  };

  return (
    <section className="section-detail panel">
      <div className="section-detail-head">
        <div>
          <p className="eyebrow">{section.group} / UZU画面</p>
          <h2>{section.label}</h2>
        </div>
        <div className="section-detail-badges">
          {section.basic === "yes" && <Badge tone="purple">基本</Badge>}
          {section.advanced === "yes" && section.basic !== "yes" && <Badge tone="blue">発展</Badge>}
          {section.advanced === "conditional" && <Badge tone="neutral">条件付き</Badge>}
          <Badge tone={section.confidence === "confirmed" ? "green" : "amber"} outline>
            {section.confidence === "confirmed" ? "公式確認済み" : "推定"}
          </Badge>
        </div>
      </div>

      <p className="section-purpose">{section.purpose}</p>

      <div className="section-links">
        <a
          className="button button-ghost button-small"
          href={officialUrl(section.uzuPath)}
          target="_blank"
          rel="noreferrer"
        >
          <Icon name="external" size={15} />
          公式ガイドで確認
        </a>
        <button
          type="button"
          className="button button-ghost button-small"
          onClick={() => void copyName()}
        >
          <Icon name="copy" size={15} />
          画面名をコピー
        </button>
        <span className="section-path">{section.uzuPath}</span>
      </div>

      {nextItem && (
        <div className="section-next">
          <span className="section-next-icon">
            <Icon name="arrowRight" size={17} />
          </span>
          <div>
            <span>この画面の次にやること</span>
            <strong>{nextItem.name}</strong>
          </div>
        </div>
      )}

      {section.constraints.length > 0 && (
        <div className="constraint-row">
          <span className="constraint-label">
            <Icon name="alert" size={15} />
            制約
          </span>
          {section.constraints.map((constraint) => (
            <span className="constraint-chip" key={constraint}>
              {constraint}
            </span>
          ))}
        </div>
      )}
    </section>
  );
}

import { Badge } from "../Badge";
import { FieldChips } from "../FieldChips";
import { Icon } from "../Icon";
import type { SectionDefinition } from "../../types";
import { officialUrl } from "../../utils";

interface GuideCardProps {
  section: SectionDefinition;
  onSelect: (section: SectionDefinition) => void;
}

export function GuideCard({ section, onSelect }: GuideCardProps) {
  return (
    <article className="guide-card">
      <div className="guide-card-top">
        <span className="guide-group">{section.group}</span>
        <Badge tone={section.confidence === "confirmed" ? "green" : "amber"} outline>
          {section.confidence === "confirmed" ? "公式確認済み" : "推定"}
        </Badge>
      </div>
      <h2>{section.label}</h2>
      <p className="guide-purpose">{section.purpose}</p>
      <div className="guide-path">{section.uzuPath}</div>
      <FieldChips fields={section.fields} limit={4} />
      {section.constraints.length > 0 && (
        <div className="guide-constraints">
          <Icon name="alert" size={14} />
          <span>{section.constraints[0]}</span>
          {section.constraints.length > 1 && <em>+{section.constraints.length - 1}</em>}
        </div>
      )}
      <div className="guide-card-actions">
        <button type="button" className="text-link" onClick={() => onSelect(section)}>
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
  );
}

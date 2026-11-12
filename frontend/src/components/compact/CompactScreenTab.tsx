import { FieldChips } from "../FieldChips";
import { Icon } from "../Icon";
import type { SectionDefinition } from "../../types";
import { officialUrl } from "../../utils";

interface CompactScreenTabProps {
  section: SectionDefinition;
}

export function CompactScreenTab({ section }: CompactScreenTabProps) {
  return (
    <div className="compact-screen">
      <span className="compact-label">UZU画面</span>
      <h2>{section.label}</h2>
      <p>{section.purpose}</p>
      {section.fields.length > 0 && <FieldChips fields={section.fields} />}
      {section.constraints.length > 0 && (
        <div className="compact-constraints">
          <span className="compact-label">制約</span>
          {section.constraints.map((constraint) => (
            <span className="compact-constraint" key={constraint}>
              <Icon name="alert" size={13} />
              {constraint}
            </span>
          ))}
        </div>
      )}
      <a
        className="button button-primary compact-complete"
        href={officialUrl(section.uzuPath)}
        target="_blank"
        rel="noreferrer"
      >
        <Icon name="external" size={17} />
        公式ガイドを開く
      </a>
    </div>
  );
}

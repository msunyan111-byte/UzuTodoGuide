import { Icon } from "../Icon";
import { SECTIONS } from "../../data/uzu";
import type { ChecklistItem, SectionDefinition } from "../../types";

interface CompactNextTabProps {
  currentKey: string;
  currentProgress: { done: number; total: number };
  section: SectionDefinition;
  nextItem: ChecklistItem | undefined;
  items: ChecklistItem[];
  onSelectSection: (key: string) => void;
  onComplete: (item: ChecklistItem) => void;
}

export function CompactNextTab({
  currentKey,
  currentProgress,
  section,
  nextItem,
  items,
  onSelectSection,
  onComplete,
}: CompactNextTabProps) {
  return (
    <div className="compact-next">
      <div className="compact-section-row">
        <select
          value={currentKey}
          onChange={(event) => onSelectSection(event.target.value)}
          aria-label="セクション"
        >
          {SECTIONS.map((entry) => (
            <option key={entry.key} value={entry.key}>
              {entry.group} / {entry.label}
            </option>
          ))}
        </select>
        <span>
          {currentProgress.done}/{currentProgress.total}
        </span>
      </div>
      <div className="compact-section-title">
        <span>{section.group}</span>
        <h2>{section.label}</h2>
      </div>
      <div className="compact-action-card">
        <span className="compact-label">次にやること</span>
        <strong>{nextItem?.name ?? "すべて完了しています"}</strong>
        {nextItem?.description && <p>{nextItem.description}</p>}
        {section.constraints[0] && (
          <span className="compact-constraint">
            <Icon name="alert" size={13} />
            {section.constraints[0]}
          </span>
        )}
        {nextItem && (
          <button
            type="button"
            className="button button-primary compact-complete"
            onClick={() => onComplete(nextItem)}
          >
            <Icon name="check" size={18} />
            完了にする
          </button>
        )}
      </div>
      {items.filter((item) => item.id !== nextItem?.id && item.status !== "DONE").length > 0 && (
        <div className="compact-items">
          <span className="compact-label">この画面の未完了</span>
          {items
            .filter((item) => item.id !== nextItem?.id && item.status !== "DONE")
            .slice(0, 4)
            .map((item) => (
              <button
                key={item.id}
                type="button"
                className="compact-item"
                onClick={() => onComplete(item)}
              >
                <span className="compact-item-dot" />
                <span>{item.name}</span>
                <Icon name="check" size={15} />
              </button>
            ))}
        </div>
      )}
    </div>
  );
}

import { useState } from "react";
import type { ChecklistItem, SectionDefinition, SectionGroup } from "../types";
import { GROUP_ORDER, PINNED_SECTIONS } from "../data/uzu";
import { sectionProgress } from "../utils";
import { Icon } from "./Icon";

export function SectionNav({
  sections,
  activeKey,
  itemsBySection,
  onSelect,
}: {
  sections: SectionDefinition[];
  activeKey: string;
  itemsBySection: Record<string, ChecklistItem[]>;
  onSelect: (key: string) => void;
}) {
  const [openGroups, setOpenGroups] = useState<SectionGroup[]>([
    "アカウント",
    "作品",
    "エディタ",
    "品質",
  ]);

  const pinned = PINNED_SECTIONS.map((key) => sections.find((section) => section.key === key)).filter(
    (section): section is SectionDefinition => Boolean(section),
  );

  const toggleGroup = (group: SectionGroup) => {
    setOpenGroups((current) =>
      current.includes(group) ? current.filter((item) => item !== group) : [...current, group],
    );
  };

  const renderSection = (section: SectionDefinition) => {
    const items = itemsBySection[section.key] ?? [];
    const { done, total } = sectionProgress(items);
    const isActive = section.key === activeKey;
    return (
      <button
        key={section.key}
        type="button"
        className={`section-link${isActive ? " active" : ""}`}
        onClick={() => onSelect(section.key)}
      >
        <span className="section-link-main">
          <span className="section-link-label">{section.label}</span>
          {section.advanced === "yes" && section.basic === "no" && (
            <span className="mini-tag">発展</span>
          )}
        </span>
        <span className={`section-count${done === total && total > 0 ? " complete" : ""}`}>
          {total > 0 ? `${done}/${total}` : "–"}
        </span>
      </button>
    );
  };

  return (
    <nav className="section-nav" aria-label="セクション">
      <div className="section-nav-label">よく使う</div>
      <div className="section-pinned">{pinned.map(renderSection)}</div>
      {GROUP_ORDER.map((group) => {
        const groupSections = sections.filter((section) => section.group === group);
        if (groupSections.length === 0) return null;
        const isOpen = openGroups.includes(group);
        return (
          <div className="section-group" key={group}>
            <button type="button" className="section-group-toggle" onClick={() => toggleGroup(group)}>
              <span>{group}</span>
              <Icon name="chevron" size={15} className={isOpen ? "chevron-open" : ""} />
            </button>
            {isOpen && <div className="section-group-items">{groupSections.map(renderSection)}</div>}
          </div>
        );
      })}
      {(itemsBySection.OTHER?.length ?? 0) > 0 && (
        <div className="section-group">
          <div className="section-group-toggle section-group-static">
            <span>その他</span>
            <span className="section-count">{itemsBySection.OTHER.length}</span>
          </div>
          <div className="section-group-items">
            <button
              type="button"
              className={`section-link${activeKey === "OTHER" ? " active" : ""}`}
              onClick={() => onSelect("OTHER")}
            >
              <span className="section-link-label">未分類のチェック</span>
              <span className="section-count">
                {itemsBySection.OTHER.filter((item) => item.status === "DONE").length}/
                {itemsBySection.OTHER.length}
              </span>
            </button>
          </div>
        </div>
      )}
    </nav>
  );
}

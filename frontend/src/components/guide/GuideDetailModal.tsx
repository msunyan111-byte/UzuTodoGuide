import { Link } from "react-router-dom";
import { Badge } from "../Badge";
import { FieldChips } from "../FieldChips";
import { Icon } from "../Icon";
import { Modal } from "../Modal";
import type { SectionDefinition } from "../../types";
import { officialUrl } from "../../utils";

interface GuideDetailModalProps {
  section: SectionDefinition;
  onClose: () => void;
}

export function GuideDetailModal({ section, onClose }: GuideDetailModalProps) {
  return (
    <Modal open onClose={onClose} title={section.label} size="lg">
      <div className="guide-detail">
        <div className="guide-detail-meta">
          <Badge tone="purple">{section.group}</Badge>
          {section.basic === "yes" && <Badge tone="purple" outline>基本</Badge>}
          {section.advanced === "yes" && <Badge tone="blue" outline>発展</Badge>}
          <Badge tone={section.confidence === "confirmed" ? "green" : "amber"} outline>
            {section.confidence === "confirmed" ? "公式確認済み" : "推定"}
          </Badge>
        </div>
        <p className="guide-detail-purpose">{section.purpose}</p>
        <div className="guide-detail-path">
          <span>公式パス</span>
          <code>{section.uzuPath}</code>
        </div>
        <div className="guide-detail-grid">
          <div>
            <h3>主な設定項目</h3>
            {section.fields.length > 0 ? (
              <FieldChips fields={section.fields} />
            ) : (
              <p className="muted">項目情報はありません。</p>
            )}
          </div>
          <div>
            <h3>制約・注意</h3>
            {section.constraints.length > 0 ? (
              <ul className="tip-list">
                {section.constraints.map((constraint) => (
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
            {section.tips.map((tip) => (
              <li key={tip}>{tip}</li>
            ))}
          </ul>
        </div>
        <div className="modal-actions">
          <a
            className="button button-primary"
            href={officialUrl(section.uzuPath)}
            target="_blank"
            rel="noreferrer"
          >
            <Icon name="external" size={16} />
            公式ページを開く
          </a>
          <Link to="/projects" className="button button-ghost" onClick={onClose}>
            作品ワークスペースへ
            <Icon name="arrowRight" size={15} />
          </Link>
        </div>
      </div>
    </Modal>
  );
}
